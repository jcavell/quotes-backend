package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Quote(id: Option[Int] = None,
                 quoteTitle: String,
                 quoteDate: DateTime,
                 customerOrderNumber: Option[String],
                 contactAddress: JsValue, // Snapshot
                 customer: JsValue, // Snapshot
                 quoteLossReason: Option[String], // converted
                 notes: Option[String] = None,
                 enquiryId: Option[Int] = None,
                 userId: Int, // Rep as now
                 companyId: Int, // Company as now
                 customerId: Int, // Customer as now
                 history: JsValue,
                 active: Boolean = true)
