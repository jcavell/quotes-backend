package xsell

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class XsellAPIController @Inject()(xsellRepo: XsellSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val xsellFormat = Json.format[Xsell]


  def getXsells() = Action.async { implicit request =>
    xsellRepo.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertXsell() = Action.async(parse.json) { implicit request =>
    request.body.validate[Xsell].fold(
      errors => Future(BadRequest(errors.mkString)),
      xsell => {
        xsellRepo.insert(xsell).map { xsellWithId =>
          Ok(Json.toJson(xsellWithId))
        }
      }
    )
  }

  def updateXsell(id:Long) = Action(parse.json) { implicit request =>
    request.body.validate[Xsell].fold(
      errors => BadRequest(errors.mkString),
      xsell => {
        xsellRepo.update(id, xsell)
        Ok("Updated XSell")
      }
    )
  }

  def deleteXsell(id:Long) = Action.async { implicit request =>
    xsellRepo.delete(id).map { a =>
      Ok("Deleted Xsell")
    }
  }

}


