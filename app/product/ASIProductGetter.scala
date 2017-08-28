package product

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

class ASIProductGetter @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  val headers = ("Authorization" -> "AsiMemberAuth client_id=500057384&client_secret=fde3381a96af18c43d4ce2d73667585c")

  def get(productId: Long): Future[ASIProduct] = {
    println("Getting productId from ASI CENTRAL: " + productId);
    ws.url(s"https://api.asicentral.com/v1/products/$productId.json").
      addHttpHeaders(headers).
      get() map { response =>
      val productJsValue = Json.parse(response.body)
      val id = (productJsValue \ "Id").get.as[Long]
      val name = (productJsValue \ "Name").get.as[String]
      val description = (productJsValue \ "Description").get.as[String]

      val testProduct = ASIProduct(rawData = productJsValue, Id = id, Name = name, Description = description)
      testProduct
    }
  }
}


