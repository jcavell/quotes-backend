package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class PO(id: Option[Int] = None,
              purchaseDate: Option[DateTime] = None,
              purchaseTitle: Option[String] = None,
              notes: Option[String] = None,
              supplierReference: Option[String],
              dateRequired: DateTime,
              supplierAddress: JsValue, // Snapshot
              deliveryAddress: JsValue, // Snapshot
              invoiceId: Int,
              supplier: Int,
              supplierContact: Int,
              userId: Int, // Rep as now
              active: Boolean = true)
