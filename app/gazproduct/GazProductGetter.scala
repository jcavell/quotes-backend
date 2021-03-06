package gazproduct

import javax.inject._

import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import play.api.libs.json._
import play.api.libs.ws._
import Implicits._

import scala.concurrent.{ExecutionContext, Future}

class GazProductGetter @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  private val username = "eb"
  private val pw = "SquiresNest1!"
  private val encodedCredentials = BaseEncoding.base64.encode((this.username + ':' + this.pw).getBytes(Charsets.UTF_8))

  private val headers = ("Authorization" -> ("Basic " + encodedCredentials))

  // private val baseURL = "https://www.everythingbranded.co.uk/siteadmin/api/product"
  private val baseURL = "http://localhost:3001/products"

  def get(productId: Long): Future[Option[GazProduct]] = {

    ws.url(s"$baseURL/$productId").
      addHttpHeaders(headers).
      get() map { response =>

      val bodyJson = Json.parse(response.body)

     (bodyJson \ "productid").toOption match {
        case Some(productId) =>
          val productid = (bodyJson \ "productid").get.as[Long]
          val brandid = (bodyJson \ "brandid").get.as[Long]
          val supplierid = (bodyJson \ "supplierid").get.as[Long]
          val productcode = (bodyJson \ "productcode").get.as[String]
          val productname = (bodyJson \ "productname").get.as[String]
          val description1 = (bodyJson \ "description1").get.as[String]
          val prices = (bodyJson \ "prices").get.as[List[GazPrice]]
          val images = (bodyJson \ "images").get.as[List[GazImage]]
          Some(GazProduct(productid, brandid, supplierid, productcode, productname, description1, images, prices))
        case None => None
      }
    }
  }
}


