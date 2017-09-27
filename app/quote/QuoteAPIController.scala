package quote

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}


class QuoteAPIController @Inject()(quoteRepository: QuoteSlickRepository, quoteLineItemRepository: QuoteLineItemSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getQuotes() = Action.async { implicit request =>
    quoteRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getQuoteLineItems(quoteId: Long) = Action.async { implicit request =>
    quoteLineItemRepository.findByQuoteId(quoteId).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertQuote() = Action.async(parse.json) { implicit request =>
    println("Validating quote: " + request.body)
    request.body.validate[Quote].fold(
      errors => Future(BadRequest(errors.mkString)),
      quote => {
        quoteRepository.insert(quote).map { quoteWithId =>
          Ok(Json.toJson(quoteWithId))
        }
      }
    )
  }

  def updateQuote(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[Quote].fold(
      errors => Future(BadRequest(errors.mkString)),
      quote => {
        quoteRepository.update(id, quote).map { quoteWithId =>
          Ok(Json.toJson(quoteWithId))
        }
      }
    )
  }

  def deleteQuote(id: Int) = Action.async { implicit request =>
    quoteRepository.delete(id).map { a =>
      Ok("Deleted Quote")
    }
  }

}


