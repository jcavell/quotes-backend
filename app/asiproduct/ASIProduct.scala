package asiproduct

import play.api.libs.json.JsValue

case class ASIProduct(internalId: Option[Long] = None, rawData: JsValue, Id: Long, Name: String, Description: String)

case class ASIQuoteProduct(id: Option[Long] = None, quoteId: Long, productInternalId: Long)
