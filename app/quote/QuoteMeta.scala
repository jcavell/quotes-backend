package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class QuoteMeta(id: Option[Long] = None,
                     status: String, // NEW,
                     stage: String, // QUOTE, SALES, INVOICE
                     quoteLossReason: Option[String] = None, // CONVERTED
                     quoteSentDate: Option[DateTime] = None,
                     saleSentDate: Option[DateTime] = None,
                     invoiceSentDate: Option[DateTime] = None,
                     paymentTerms: Option[String] = None, // pro-forma
                     paymentDueDate: Option[DateTime] = None,
                     paymentStatus: Option[String] = None, // UNPAID, PART_PAID, PAID
                     assignedGroup: Option[String], // REP, DESIGN, CUSTOMER
                     assignedUserId: Option[Long] = None,
                     //history: Option[JsValue] = None,
                     quoteId: Long
                )
