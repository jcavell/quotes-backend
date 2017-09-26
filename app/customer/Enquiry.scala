package customer

import org.joda.time.DateTime

case class Enquiry(
                    id: Option[Int],
                    requestId: Int,
                    requestTimestamp: DateTime,
                    productId: String,
                    customerFirstName: String,
                    customerLastName: String,
                    customerTel: String,
                    customerEmail: String,
                    company: String,
                    dateRequired: DateTime,
                    quantity: Int,
                    otherRequirements: Option[String],
                    imported: Boolean = false)
