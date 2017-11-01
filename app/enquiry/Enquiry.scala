package enquiry

import org.joda.time.DateTime

case class Enquiry(
                    id: Option[Long],
                    enquiryId: Long,
                    enquiryTimestamp: DateTime,
                    productId: Long,
                    sku: String,
                    productName: String,
                    supplier: Option[String],
                    colour: Option[String],
                    customerName: String,
                    customerTelephone: String,
                    customerEmail: String,
                    company: String,
                    requiredDate: DateTime,
                    quantity: Int,
                    repId: Int,
                    repEmail: String,
                    source: Option[String],
                    subject: Option[String],
                    xsellProductIds: List[Long],
                    otherRequirements: Option[String],
                    imported: Boolean = false
                  )
