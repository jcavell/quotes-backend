package customer

import javax.inject._

import formats.CustomFormats._
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class CompanyAPIController @Inject()(companyRepository: CompanySlickRepository, customerSlickRepository: CustomerSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCompany(companyId: Long) = Action.async { implicit request =>

    val companyAndCustomers = for{
      company <- companyRepository.findById(companyId)
      customers <- customerSlickRepository.getCustomerRecords(maybeCompanyId = Some(companyId))
    } yield (company, customers)

    companyAndCustomers.map { sc =>
      val companyRecord = sc._1 match{
        case Some(s) => Some(CompanyRecord(s, sc._2))
        case None => None
      }
      val json = Json.toJson(companyRecord)
      Ok(json)
    }
  }

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


