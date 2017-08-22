package product

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class ProductAPIController @Inject()(ws: WSClient, productGetter: ASIProductGetter, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val testProductFormat = Json.format[ASIProduct]

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def getASIProduct() = Action.async { implicit request =>
   productGetter.get(550517407).map { product =>
     Ok(Json.toJson(product))
   }
  }
}


