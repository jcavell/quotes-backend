package product

import play.api.libs.json.JsValue

/**
  * Created by jcavell on 16/08/2017.
  */
case class Product(id: Option[Long] = None, productId: Long, name: String, cost: BigDecimal, currencyCode: String, data: JsValue)
