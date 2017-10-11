package gazproduct

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import Implicits._

import scala.concurrent.{ExecutionContext, Future}


class GazProductAPIController @Inject()(gazProductGetter: GazProductGetter, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getProduct(productId: Long) = Action.async { implicit request =>
    gazProductGetter.get(productId)map { p =>
      val json = Json.toJson(p)
      Ok(json)
    }
  }

}


