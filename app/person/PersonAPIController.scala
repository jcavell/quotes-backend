package person

import javax.inject._

import company.Company
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext


class PersonAPIController @Inject()(personRepo: PersonRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val CompanyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]
  implicit val pageFormat = Json.format[Page]


  def get(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    personRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }
}


