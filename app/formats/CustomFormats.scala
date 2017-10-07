package formats

import address.Address
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import customer._
import play.api.libs.json._
import play.api.libs.json.Writes.dateWrites
import asiproduct.ASIProduct
import asiquote.{ASIQuote, ASIStatus, QuotePage, QuoteWithProducts}
import mockenquiry.MockEnquiry
import purchase.{PO, PORecord}
import quote.PaymentStatuses.PaymentStatus
import quote._
import supplier.{Contact, Supplier, SupplierRecord}
import user.User

object CustomFormats {

  implicit val jodaWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ssZ")
  implicit val jodaReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")
  implicit val jodaFormat: Format[DateTime] = Format(jodaReads, jodaWrites)
  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ssZ")

  implicit val paymentStatusesReads = Reads.enumNameReads(PaymentStatuses)
  implicit val quoteStatusesReads = Reads.enumNameReads(QuoteStatuses)
  implicit val quoteStagesReads = Reads.enumNameReads(QuoteStages)

  implicit val addressFormat = Json.format[Address]
  implicit val quoteLineItemFormat = Json.format[QuoteLineItem]
  implicit val quoteFormat = Json.format[Quote]
  implicit val quoteMetaFormat = Json.format[QuoteMeta]

  implicit val customerFormat = Json.format[Customer]
  implicit val companyFormat = Json.format[Company]
  implicit val userFormat = Json.format[User]
  implicit val customerCompanyFormat = Json.format[CustomerRecord]
  implicit val quoteRecordFormat = Json.format[QuoteRecord]
  implicit val companyRecordFormat = Json.format[CompanyRecord]


  implicit val SupplierFormat = Json.format[Supplier]
  implicit val ContactFormat = Json.format[Contact]
  implicit val POFormat = Json.format[PO]
  implicit val PORecordFormat = Json.format[PORecord]
  implicit val supplierRecordFormat = Json.format[SupplierRecord]

  implicit val asiQuoteFormat = Json.format[ASIQuote]
  implicit val productFormat = Json.format[ASIProduct]
  implicit val productCustomerPageFormat = Json.format[QuoteWithProducts]
  implicit val quotePageFormat = Json.format[QuotePage]

  implicit val mockEnquiryFormat = Json.format[MockEnquiry]
}
