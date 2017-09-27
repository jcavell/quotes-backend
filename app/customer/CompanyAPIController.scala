package customer

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class CompanyAPIController @Inject()(companyRepository: CompanySlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val companyFormat = Json.format[Company]


  def getCompanies() = Action.async { implicit request =>
    companyRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertCompany() = Action.async(parse.json) { implicit request =>
    println("Validating company: " + request.body)
    request.body.validate[Company].fold(
      errors => Future(BadRequest(errors.mkString)),
      company => {
        companyRepository.insert(company).map { companyWithId =>
          Ok(Json.toJson(companyWithId))
        }
      }
    )
  }

  def updateCompany(id:Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Company].fold(
      errors => Future(BadRequest(errors.mkString)),
      company => {
        companyRepository.update(id, company).map { companyWithId =>
          Ok(Json.toJson(companyWithId))
        }
      }
    )
  }

  def deleteCompany(id:Long) = Action.async { implicit request =>
    companyRepository.delete(id).map { a =>
      Ok("Deleted Company")
    }
  }

}


