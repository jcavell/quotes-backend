package enquiry

import org.joda.time.DateTime

case class Enquiry(
                    id: Option[Long],
                    enquiryId: Long,
                    enquiryTimestamp: DateTime,
                    productId: Long,
                    sku: String,
                    productName: String,
                    supplier: Option[String] = None,
                    colour: Option[String] = None,
                    customerName: String,
                    customerTelephone: String,
                    customerEmail: String,
                    company: String,
                    requiredDate: DateTime,
                    quantity: Int,
                    repId: Int,
                    repEmail: String,
                    source: Option[String] = None,
                    subject: Option[String] = None,
                    xsellProductIds: List[Long] = List.empty,
                    otherRequirements: Option[String] = None,
                    imported: Boolean = false
                  )
