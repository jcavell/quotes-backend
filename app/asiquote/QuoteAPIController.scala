package asiquote

import javax.inject._

import formats.CustomFormats._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class QuoteAPIController @Inject()(quoteRepo: QuoteRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

//  def getQuotes(page: Int, orderBy: Int, filter: String, companyName: String, email: String) = Action.async { implicit request =>
//    val results =
//      if (!companyName.isEmpty) {
//        quoteRepo.listForCompany(page = page, orderBy = orderBy, companyName = companyName)
//      } else if (!email.isEmpty) {
//        quoteRepo.listForEmail(page = page, orderBy = orderBy, email = email)
//      }
//      else {
//        quoteRepo.listWithGenericFilter(page = page, orderBy = orderBy, filter = ("%" + filter + "%"))
//      }
//    results.map { page =>
//      val json = Json.toJson(page)
//      Ok(json)
//    }
//  }
}


