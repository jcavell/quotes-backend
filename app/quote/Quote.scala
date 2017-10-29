package quote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 title: String,
                 requiredDate: DateTime,
                 specialInstructions: Option[String] = None,

                 enquiryId: Option[Long] = None,
                 customerId: Long,
                 invoiceAddressId: Option[Long] = None,
                 deliveryAddressId: Option[Long] = None,

                 repEmail: String,
                 repId: Option[Long] = None, // Rep as now

                 // Common fields
                 createdDate: DateTime,
                 notes: Option[String] = None,
                 active: Boolean = true
                )
