package controllers

import javax.inject._

import models._
import play.api.http.HttpEntity
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.json.Writes.dateWrites

import scala.concurrent.{ExecutionContext, Future}

case class TestProduct(Id: Long, Name: String, Description: String)


class TestController @Inject()(personRepo: PersonRepository, quoteRepo: QuoteRepository, xsellsDao: XsellsDAO, ws: WSClient, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val testProductFormat = Json.format[TestProduct]

  implicit val personFormat = Json.format[Person]
  implicit val CompanyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]

  implicit val pageFormat = Json.format[Page]
  implicit val quoteFormat = Json.format[Quote]
  implicit val productFormat = Json.format[Product]
  implicit val productPersonPageFormat = Json.format[QuoteWithProducts]

  implicit val xsellFormat = Json.format[Xsell]

  implicit val quotePageFormat = Json.format[QuotePage]


  def get(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    personRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getQuotes(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    quoteRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getXsells() = Action.async { implicit request =>
    xsellsDao.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertXsell() = Action.async(parse.json) { implicit request =>
    request.body.validate[Xsell].fold(
      errors => Future(BadRequest(errors.mkString)),
      xsell => {
        xsellsDao.insert(xsell).map { xsellWithId =>
          Ok(Json.toJson(xsellWithId))
        }
      }
    )
  }

  def updateXsell(id: Long) = Action(parse.json) { implicit request =>
    request.body.validate[Xsell].fold(
      errors => BadRequest(errors.mkString),
      xsell => {
        xsellsDao.update(id, xsell)
        Ok("Updated XSell")
      }
    )
  }

  def deleteXsell(id: Long) = Action.async { implicit request =>
    xsellsDao.delete(id).map { a =>
      Ok("Deleted Xsell")
    }
  }

  def getASIProduct() = Action.async { implicit request =>
    ws.url("https://api.asicentral.com/v1/products/5399926.json").
      addHttpHeaders("Authorization" -> "AsiMemberAuth client_id=500057384&client_secret=fde3381a96af18c43d4ce2d73667585c").
      get() map { response =>
      val productJsValue = Json.parse(response.body)
      val id = (productJsValue \ "Id").get.as[Long]
      val name = (productJsValue \ "Name").get.as[String]
      val description = (productJsValue \ "Description").get.as[String]

      val testProduct = TestProduct(id, name, description)
      Ok(Json.toJson(testProduct))
    }
  }
}


