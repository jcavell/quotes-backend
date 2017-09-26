package product

case class ProductImage(id: Option[Long] = None,
                        productId: Long,
                        description: Option[String],
                        url: String,
                        heightPx: Option[Int],
                        widthPx: Option[Int]
                       ) {

}

