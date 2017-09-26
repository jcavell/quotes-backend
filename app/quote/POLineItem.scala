package quote

case class POLineItem(id: Option[Long] = None,
                      sku: String,
                      quantity: Int,
                      colour: Option[String] = None,
                      overrideDescription: Option[String],
                      priceIncludes: Option[String] = None,
                      cost: BigDecimal,
                      vat: BigDecimal = 0.2,
                      POId: Long) {

}
