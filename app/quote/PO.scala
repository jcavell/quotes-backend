package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class PO(id: Option[Long] = None,
              purchaseDate: Option[DateTime] = None,
              purchaseTitle: Option[String] = None,
              notes: Option[String] = None,
              supplierReference: Option[String],
              dateRequired: DateTime,
              supplierAddress: JsValue, // Snapshot
              deliveryAddress: JsValue, // Snapshot
              invoiceId: Long,
              supplier: Long,
              supplierContact: Long,
              userId: Long, // Rep as now
              active: Boolean = true)
