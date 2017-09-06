package formats

import company.Company
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import customer.{Customer, CustomerCompany}
import play.api.libs.json._
import play.api.libs.json.Writes.dateWrites
import product.ASIProduct
import quote.{Quote, QuotePage, QuoteWithProducts, Status}

object CustomFormats {

  implicit val jodaWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ssZ")
  implicit val jodaReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZ")
  implicit val jodaFormat: Format[DateTime] = Format(jodaReads, jodaWrites)
  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ssZ")

  implicit val customerFormat = Json.format[Customer]
  implicit val companyFormat = Json.format[Company]
  implicit val customerCompanyFormat = Json.format[CustomerCompany]

  implicit val quoteFormat = Json.format[Quote]
  implicit val productFormat = Json.format[ASIProduct]
  implicit val productCustomerPageFormat = Json.format[QuoteWithProducts]
  implicit val quotePageFormat = Json.format[QuotePage]
}
