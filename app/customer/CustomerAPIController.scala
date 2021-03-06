package customer

import javax.inject._

import db.{Search, Sort}
import formats.CustomFormats._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class CustomerAPIController @Inject()(customerRepository: CustomerSlickRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getCustomer(id: Long) = Action.async { implicit request =>
    customerRepository.getCustomerRecord(id).map { customer =>
      val json = Json.toJson(customer)
      Ok(json)
    }
  }

  def getCustomers() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    val sort = Sort.fromRequestMap(request.queryString)

    customerRepository.getCustomerRecords(search, sort).map { customers =>
      val json = Json.toJson(customers)
      Ok(json)
    }
  }

  def getCount() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    customerRepository.getCount(search).map { customers =>
      val json = Json.toJson(customers)
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

  def updateCustomer(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Customer].fold(
      errors => Future(BadRequest(errors.mkString)),
      customer => {
        customerRepository.update(customer).map { customerCompany =>
          Ok(Json.toJson(customerCompany))
        }
      }
    )
  }

  def deleteCustomer(id: Long) = Action.async { implicit request =>
    customerRepository.delete(id).map { a =>
      Ok("Deleted Customer")
    }
  }
}


