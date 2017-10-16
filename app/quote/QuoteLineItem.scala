package quote

case class QuoteLineItem(id: Option[Long] = None,
                         productId: Long,
                         quantity: Int,
                         colour: Option[String] = None,
                         description: Option[String] = None,
                         priceIncludes: Option[String] = None,
                         cost: Option[BigDecimal] = None,
                         markup: Option[BigDecimal] = None,
                         vat: BigDecimal = 0.2,
                         quoteId: Long,
                         supplierId: Option[Long] = None) {

}
