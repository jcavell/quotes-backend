package gazproduct

import play.api.libs.json.Json

object Implicits{
  implicit val GazImageFormat = Json.format[GazImage]
  implicit val GazPriceFormat = Json.format[GazPrice]
  implicit val GazProductFormat = Json.format[GazProduct]
}

case class GazImage(
                     imageid: Long,
                     id: Long,
                     priority: Long,
                     `type`: String,
                     imagesize: String,
                     caption: String
                   )

case class GazPrice(
                     priceid: Long,
                     price: BigDecimal,
                     markup: BigDecimal,
                     qty: Int
                   )

case class GazProduct(
                       productid: Long,
                       brandid: Long,
                       supplierid: Long,
                       productcode: String,
                       productname: String,
                       description1: String,
                       images: Seq[GazImage] = List.empty,
                       prices: Seq[GazPrice] = List.empty
                     )
