package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Quote(id: Option[Long] = None,
                 quoteTitle: String,
                 quoteDate: DateTime,
                 customerOrderNumber: Option[String],
                 contactAddress: JsValue, // Snapshot
                 customer: JsValue, // Snapshot
                 quoteLossReason: Option[String], // converted
                 notes: Option[String] = None,
                 enquiryId: Option[Long] = None,
                 userId: Long, // Rep as now
                 companyId: Long, // Company as now
                 customerId: Long, // Customer as now
                 history: JsValue,
                 active: Boolean = true)
