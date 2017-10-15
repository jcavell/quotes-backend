package quote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 title: String,
                 dateRequired: DateTime, // TODO rename requiredDate
                 customerName: String, // TODO delete
                 customerEmail: String, // TODO delete
                 specialInstructions: Option[String] = None,

                 // quote-specific relations
                 invoiceAddressId: Option[Long] = None, // Invoice address just for this quote
                 deliveryAddressId: Option[Long] = None, // Delivery address just for this quote
                 customerId: Long, // Customer (references Company) just for this quote
                 enquiryId: Option[Long] = None,

                 repId: Long, // Rep as now

                 // common fields
                 createdDate: DateTime,
                 notes: Option[String] = None,
                 active: Boolean = true
                )
