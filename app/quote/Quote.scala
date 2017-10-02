package quote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 title: String,
                 dateRequired: DateTime,
                 customerName: String, // snapshot
                 customerEmail: String, // snapshot
                 specialInstructions: Option[String] = None,

                 // relations
                 invoiceAddressId: Option[Long] = None, // Invoice address just for this quote
                 deliveryAddressId: Option[Long] = None, // Delivery address just for this quote
                 customerId: Long, // Customer as now (references Company)
                 repId: Long, // Rep as now
                 enquiryId: Option[Long] = None,

                 // common fields
                 createdDate: DateTime,
                 notes: Option[String] = None,
                 active: Boolean = true
                )
