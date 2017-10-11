package enquiry

import org.joda.time.DateTime

case class Enquiry(
                    id: Option[Long],
                    enquiryId: Long,
                    enquiryTimestamp: DateTime,
                    internalProductId: Long,
                    productId: String,
                    productName: String,
                    brand: Option[String],
                    colour: Option[String],
                    customerName: String,
                    customerTelephone: String,
                    customerEmail: String,
                    company: String,
                    dateRequired: DateTime,
                    quantity: Int,
                    repId: Int,
                    repEmail: String,
                    source: Option[String],
                    subject: Option[String],
                    xsellProductIds: List[Long],
                    otherRequirements: Option[String],
                    imported: Boolean = false
                  )
