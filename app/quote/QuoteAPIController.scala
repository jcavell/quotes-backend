package quote

import javax.inject._

import company.Company
import person.{Person, PersonCompany}
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._
import product.Product

import scala.concurrent.ExecutionContext

class QuoteAPIController @Inject()(quoteRepo: QuoteRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val CompanyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]

  implicit val quoteFormat = Json.format[Quote]
  implicit val productFormat = Json.format[Product]
  implicit val productPersonPageFormat = Json.format[QuoteWithProducts]
  implicit val quotePageFormat = Json.format[QuotePage]

  def getQuotes(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    quoteRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }
}


