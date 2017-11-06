package quote

import javax.inject._

import db.{Search, Sort}
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}


class QuoteAPIController @Inject()(quoteRepository: QuoteSlickRepository, quoteLineItemRepository: QuoteLineItemSlickRepository, quoteXsellItemRepository: QuoteXsellItemSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getQuotes() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    val sort = Sort.fromRequestMap(request.queryString)
    quoteRepository.getQuoteRecords(search, sort).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getCount() = Action.async { implicit request =>
    val search = Search.fromRequestMap(request.queryString)
    quoteRepository.getCount(search).map { customers =>
      val json = Json.toJson(customers)
      Ok(json)
    }
  }

  def getQuote(quoteId: Long) = Action.async { implicit request =>
    val quoteAndLineItems = for{
      quoteRecord <- quoteRepository.getQuoteRecord(quoteId)
      quoteLineItems <- quoteLineItemRepository.findByQuoteId(quoteId)
    } yield (quoteRecord, quoteLineItems)

    quoteAndLineItems.map { ql =>
      val page = ql._1 match{
        case Some(quoteRecord) => Some(quoteRecord.copy(lineItems = Some(ql._2)))
        case None => None
      }

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



  def getXsellItems(quoteId: Long) = Action.async { implicit request =>
    quoteXsellItemRepository.findByQuoteId(quoteId).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertQuoteXsellItem() = Action.async(parse.json) { implicit request =>
    println("Validating quote xsell: " + request.body)
    request.body.validate[QuoteXsellItem].fold(
      errors => Future(BadRequest(errors.mkString)),
      quotexsell => {
        quoteXsellItemRepository.insert(quotexsell).map { quotexsellWithId =>
          Ok(Json.toJson(quotexsellWithId))
        }
      }
    )
  }


  def deleteQuotexsell(id: Long) = Action.async { implicit request =>
    quoteXsellItemRepository.delete(id).map { a =>
      Ok("Deleted quote xsell")
    }
  }

}


