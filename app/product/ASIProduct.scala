package product

import play.api.libs.json.JsValue

case class ASIProduct(internalId: Option[Int] = None, rawData: JsValue, Id: Long, Name: String, Description: String)
