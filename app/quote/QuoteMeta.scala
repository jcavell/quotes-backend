package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue
import quote.PaymentStatuses.PaymentStatus
import quote.QuoteStages.QuoteStage
import quote.QuoteStatuses.QuoteStatus


object PaymentStatuses extends Enumeration {
  type PaymentStatus = Value
  val UNPAID, PART_PAID, PAID = Value
}

case class QuoteMeta(id: Option[Long] = None,
                     status: QuoteStatus, // NEW,
                     stage: QuoteStage, // QUOTE, SALES, INVOICE
                     quoteLossReason: Option[String] = None, // CONVERTED
                     quoteSentDate: Option[DateTime] = None,
                     saleSentDate: Option[DateTime] = None,
                     invoiceSentDate: Option[DateTime] = None,
                     paymentTerms: Option[String] = None, // pro-forma
                     paymentDueDate: Option[DateTime] = None,
                     paymentStatus: Option[PaymentStatus] = None, // UNPAID, PART_PAID, PAID
                     assignedGroup: Option[String], // REP, DESIGN, CUSTOMER
                     assignedUserId: Option[Long] = None,
                     //history: Option[JsValue] = None,
                     quoteId: Long
                )
