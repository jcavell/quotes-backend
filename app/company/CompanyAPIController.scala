package company

import javax.inject._

import customer.CustomerSlickRepository
import db.{Search, Sort}
import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}


class CompanyAPIController @Inject()(companyRepository: CompanySlickRepository, customerSlickRepository: CustomerSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCompany(companyId: Long) = Action.async { implicit request =>

    val companyAndCustomers = for{
      company <- companyRepository.findById(companyId)
      customers <- customerSlickRepository.getCustomerRecords(Search(Some("companyId"), Some(companyId.toString)), Sort())
    } yield (company, customers)

    companyAndCustomers.map { cc =>
      val companyRecord = cc._1 match{
        case Some(s) => Some(CompanyRecord(s, cc._2))
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


  def search() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    val sort = Sort.fromRequestMap(request.queryString)

    companyRepository.search(search, sort).map { companies =>
      val json = Json.toJson(companies)
      Ok(json)
    }
  }

  def count() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    val sort = Sort.fromRequestMap(request.queryString)

    companyRepository.count(search, sort).map { customers =>
      val json = Json.toJson(customers)
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


