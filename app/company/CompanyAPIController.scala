package company

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class CompanyAPIController @Inject()(companyDao: CompanyDaoWrapper, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val companyFormat = Json.format[Company]


  def getCompanies() = Action.async { implicit request =>
    companyDao.findAll().map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertCompany() = Action.async(parse.json) { implicit request =>
    println("Validating company: " + request.body)
    request.body.validate[Company].fold(
      errors => Future(BadRequest(errors.mkString)),
      company => {
        companyDao.insert(company).map { companyWithId =>
          Ok(Json.toJson(companyWithId))
        }
      }
    )
  }

  def updateCompany(id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => BadRequest(errors.mkString),
      company => {
        companyDao.update(company)
        Ok("Updated Company")
      }
    )
  }

  def deleteCompany(id: Int) = Action.async { implicit request =>
    companyDao.delete(id).map { a =>
      Ok("Deleted Company")
    }
  }

}


