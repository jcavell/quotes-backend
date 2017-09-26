package quote

case class QuoteLineItem(id: Option[Long] = None,
                         sku: String,
                         quantity: Int,
                         colour: Option[String] = None,
                         overrideDescription: Option[String],
                         priceIncludes: Option[String] = None,
                         cost: BigDecimal,
                         markup: BigDecimal,
                         sell: BigDecimal,
                         vat: BigDecimal = 0.2,
                         quoteId: Long,
                         supplierId: Long) {

}
