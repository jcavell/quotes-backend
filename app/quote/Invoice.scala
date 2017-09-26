package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Invoice(id: Option[Long] = None,
                   invoiceTitle: String,
                   invoiceDate: Option[DateTime] = None,
                   dateRequired: DateTime,
                   customerOrderNumber: Option[String],
                   invoiceAddress: JsValue,
                   deliveryAddress: JsValue,
                   customer: JsValue,
                   paymentTerms: String, // pro-forma
                   paymentDueDate: DateTime,
                   paymentStatus: String, // unpaid, paid
                   notes: Option[String] = None,
                   customerId: Long, // Customer as is now
                   userId: Long, // Rep as is now
                   quoteId: Long, // Quote must be immutable after invoice
                   active: Boolean)
