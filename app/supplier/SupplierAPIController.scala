package supplier

import javax.inject._

import formats.CustomFormats._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class SupplierAPIController @Inject()(supplierRepository: SupplierSlickRepository, contactSlickRepository: ContactSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getSupplier(supplierId: Long) = Action.async { implicit request =>

    val supplierAndContacts = for{
      supplier <- supplierRepository.findById(supplierId)
      contacts <- contactSlickRepository.findBySupplierId(supplierId)
    } yield (supplier, contacts)

    supplierAndContacts.map { sc =>
      val supplierRecord = sc._1 match{
        case Some(s) => Some(SupplierRecord(s, sc._2))
        case None => None
      }
      val json = Json.toJson(supplierRecord)
      Ok(json)
    }
  }

  def getSuppliers() = Action.async { implicit request =>
    supplierRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertSupplier() = Action.async(parse.json) { implicit request =>
    println("Validating supplier: " + request.body)
    request.body.validate[Supplier].fold(
      errors => Future(BadRequest(errors.mkString)),
      supplier => {
        supplierRepository.insert(supplier).map { supplierWithId =>
          Ok(Json.toJson(supplierWithId))
        }
      }
    )
  }

  def updateSupplier(id:Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Supplier].fold(
      errors => Future(BadRequest(errors.mkString)),
      supplier => {
        supplierRepository.update(id, supplier).map { supplierWithId =>
          Ok(Json.toJson(supplierWithId))
        }
      }
    )
  }

  def deleteSupplier(id:Long) = Action.async { implicit request =>
    supplierRepository.delete(id).map { a =>
      Ok("Deleted Supplier")
    }
  }

}


