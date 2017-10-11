package address

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}


class AddressAPIController @Inject()(addressRepository: AddressSlickRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def insertAddress() = Action.async(parse.json) { implicit request =>
    println("Validating address: " + request.body)
    request.body.validate[Address].fold(
      errors => Future(BadRequest(errors.mkString)),
      address => {
        addressRepository.insert(address).map { address =>
          Ok(Json.toJson(address))
        }
      }
    )
  }

  def updateAddress(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Address].fold(
      errors => Future(BadRequest(errors.mkString)),
      address => {
        addressRepository.update(address).map { addressy =>
          Ok(Json.toJson(address))
        }
      }
    )
  }

  def deleteAddress(id: Long) = Action.async { implicit request =>
    addressRepository.delete(id).map { a =>
      Ok("Deleted Address")
    }
  }
}


