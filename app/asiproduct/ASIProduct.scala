package asiproduct

import play.api.libs.json.JsValue

case class ASIProduct(internalId: Option[Int] = None, rawData: JsValue, Id: Long, Name: String, Description: String)

case class ASIQuoteProduct(id: Option[Int] = None, quoteId: Int, productInternalId: Int)
