package quote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 title: String,
                 requiredDate: DateTime,
                 specialInstructions: Option[String] = None,

                 enquiryId: Option[Long] = None,
                 customerId: Option[Long],

                 repEmail: String,
                 repId: Option[Long] = None, // Rep as now

                 // Common fields
                 createdDate: DateTime,
                 notes: Option[String] = None,
                 active: Boolean = true
                )
