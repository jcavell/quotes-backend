package quote

import javax.inject._

import formats.CustomFormats._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

class QuoteAPIController @Inject()(quoteRepo: QuoteRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getQuotes(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    quoteRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }
}


