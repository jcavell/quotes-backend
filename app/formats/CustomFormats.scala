package formats

import company.Company
import org.joda.time.DateTime
import person.{Person, PersonCompany}
import play.api.libs.json._
import play.api.libs.json.Writes.dateWrites
import product.ASIProduct
import quote.{Quote, QuotePage, QuoteWithProducts}

/**
  * Created by jcavell on 28/08/2017.
  */
object CustomFormats {
  implicit val jodaWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val jodaReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val jodaFormat: Format[DateTime] = Format(jodaReads, jodaWrites)
  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val companyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]

  implicit val quoteFormat = Json.format[Quote]
  implicit val productFormat = Json.format[ASIProduct]
  implicit val productPersonPageFormat = Json.format[QuoteWithProducts]
  implicit val quotePageFormat = Json.format[QuotePage]
}
