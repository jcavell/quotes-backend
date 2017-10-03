package purchase

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}


class PurchaseAPIController @Inject()(PORepository: POSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getPOs() = Action.async { implicit request =>
    PORepository.getPORecords().map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  
  

  def insertPO() = Action.async(parse.json) { implicit request =>
    println("Validating PO: " + request.body)
    request.body.validate[PO].fold(
      errors => Future(BadRequest(errors.mkString)),
      PO => {
        PORepository.insert(PO).map { POWithId =>
          Ok(Json.toJson(POWithId))
        }
      }
    )
  }

  def updatePO(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[PO].fold(
      errors => Future(BadRequest(errors.mkString)),
      PO => {
        PORepository.update(id, PO).map { POWithId =>
          Ok(Json.toJson(POWithId))
        }
      }
    )
  }

  def deletePO(id: Long) = Action.async { implicit request =>
    PORepository.delete(id).map { a =>
      Ok("Deleted PO")
    }
  }

}


