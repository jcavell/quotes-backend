package quote

import org.joda.time.DateTime
import play.api.libs.json.JsValue

case class Quote(id: Option[Long] = None,
                 status: String, //
                 title: String,
                 quoteCreatedDate: DateTime,
                 quoteSentDate: Option[DateTime] = None,
                 saleSentDate: Option[DateTime] = None,
                 invoiceSentDate: Option[DateTime] = None,
                 quoteLossReason: Option[String] = None, // converted
                 dateRequired: DateTime,
                 customerName: String, // snapshot
                 customerEmail: String, // snapshot
                 paymentTerms: Option[String] = None, // pro-forma
                 paymentDueDate: Option[DateTime] = None,
                 paymentStatus: Option[String] = None, // unpaid, paid
                 notes: Option[String] = None,
                // specialInstructions: Option[String] = None,
                 invoiceAddressId: Option[Long] = None, // Invoice address just for this quote
                 deliveryAddressId: Option[Long] = None, // Delivery address just for this quote
                 customerId: Long, // Customer as now (references Company)
                 repId: Long, // Rep as now
                 assignedUserId: Option[Long] = None, // who is it assigned to?
                 enquiryId: Option[Long] = None,
               //  history: Option[JsValue] = None,
                 active: Boolean = true)
