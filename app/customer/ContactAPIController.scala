package customer

import javax.inject._

import company.Company
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class CustomerAPIController @Inject()(customerRepository: CustomerSlickRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val customerFormat = Json.format[Customer]
  implicit val companyFormat = Json.format[Company]
  implicit val customerCompanyFormat = Json.format[(Customer, Company)]

  def getCustomers() = Action.async { implicit request =>
    customerRepository.allWithCompany.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertCustomer() = Action.async(parse.json) { implicit request =>
    println("Validating customer: " + request.body)
    request.body.validate[Customer].fold(
      errors => Future(BadRequest(errors.mkString)),
      customer => {
        customerRepository.insert(customer).map { customer =>
          Ok(Json.toJson(customer))
        }
      }
    )
  }

  def updateCustomer(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[Customer].fold(
      errors => Future(BadRequest(errors.mkString)),
      customer => {
        customerRepository.update(customer).map { customerCompany =>
          Ok(Json.toJson(customerCompany))
        }
      }
    )
  }

  def deleteCustomer(id: Int) = Action.async { implicit request =>
    customerRepository.delete(id).map { a =>
      Ok("Deleted Customer")
    }
  }
}


