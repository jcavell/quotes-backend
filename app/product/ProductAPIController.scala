package product

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class ProductAPIController @Inject()(ws: WSClient, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val testProductFormat = Json.format[ASIProduct]

  def getASIProduct() = Action.async { implicit request =>
    ws.url("https://api.asicentral.com/v1/products/5399926.json").
      addHttpHeaders("Authorization" -> "AsiMemberAuth client_id=500057384&client_secret=fde3381a96af18c43d4ce2d73667585c").
      get() map { response =>
      val productJsValue = Json.parse(response.body)
      val id = (productJsValue \ "Id").get.as[Long]
      val name = (productJsValue \ "Name").get.as[String]
      val description = (productJsValue \ "Description").get.as[String]

      val testProduct = ASIProduct(id, name, description)
      Ok(Json.toJson(testProduct))
    }
  }
}


