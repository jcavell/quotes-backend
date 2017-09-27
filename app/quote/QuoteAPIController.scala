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

  def getQuote(quoteId: Long) = Action.async { implicit request =>
    quoteRepository.get(quoteId).map { page =>
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

  def updateQuote(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[Quote].fold(
      errors => Future(BadRequest(errors.mkString)),
      quote => {
        quoteRepository.update(id, quote).map { quoteWithId =>
          Ok(Json.toJson(quoteWithId))
        }
      }
    )
  }

  def deleteQuote(id: Long) = Action.async { implicit request =>
    quoteRepository.delete(id).map { a =>
      Ok("Deleted Quote")
    }
  }



  def getLineItems(quoteId: Long) = Action.async { implicit request =>
    quoteLineItemRepository.findByQuoteId(quoteId).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertQuoteLineItem() = Action.async(parse.json) { implicit request =>
    println("Validating quoteLineItem: " + request.body)
    request.body.validate[QuoteLineItem].fold(
      errors => Future(BadRequest(errors.mkString)),
      quoteLineItem => {
        quoteLineItemRepository.insert(quoteLineItem).map { quoteLineItemWithId =>
          Ok(Json.toJson(quoteLineItemWithId))
        }
      }
    )
  }

  def updateQuoteLineItem(id: Long) = Action.async(parse.json) { implicit request =>
    request.body.validate[QuoteLineItem].fold(
      errors => Future(BadRequest(errors.mkString)),
      quoteLineItem => {
        quoteLineItemRepository.update(id, quoteLineItem).map { quoteLineItemWithId =>
          Ok(Json.toJson(quoteLineItemWithId))
        }
      }
    )
  }

  def deleteQuoteLineItem(id: Long) = Action.async { implicit request =>
    quoteLineItemRepository.delete(id).map { a =>
      Ok("Deleted QuoteLineItem")
    }
  }

}


