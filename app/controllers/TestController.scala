package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date
import play.api.libs.json.Writes.dateWrites

import scala.concurrent.ExecutionContext


class TestController @Inject()(personRepo: PersonRepository, quoteRepo: QuoteRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc)  {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val CompanyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]

  implicit val pageFormat = Json.format[Page]
  implicit val quoteFormat = Json.format[Quote]
  implicit val productFormat = Json.format[Product]
  implicit val productPersonPageFormat = Json.format[QuoteAndPersonAndProduct]

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
}


