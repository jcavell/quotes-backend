package quote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 title: String,
                 requiredDate: DateTime,
                 specialInstructions: Option[String] = None,

                 // Customer snapshot data
                 companyName: String,
                 customerName: String,
                 customerEmail: String,
                 customerDirectPhone: Option[String] = None,
                 customerMobilePhone: Option[String] = None,

                 // Quote-specific relations - shapshots for this quote
                 enquiryId: Option[Long] = None,
                 invoiceAddressId: Option[Long] = None,
                 deliveryAddressId: Option[Long] = None,

                 // Ongoing relations that may change
                 customerId: Option[Long] = None, // Current customer
                 companyId: Option[Long] = None, // Current company


                 repEmail: String,
                 repId: Option[Long] = None, // Rep as now

                 // Common fields
                 createdDate: DateTime,
                 notes: Option[String] = None,
                 active: Boolean = true
                )
