package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date
import play.api.libs.json.Writes.dateWrites

import scala.concurrent.ExecutionContext


class TestController @Inject()(personRepo: PersonRepository, quoteRequestRepo: QuoteRequestRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc)  {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val CompanyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[PersonCompany]

  implicit val pageFormat = Json.format[Page]
  implicit val quoteRequestFormat = Json.format[QuoteRequest]
  implicit val quoteRequestProductFormat = Json.format[QuoteRequestProduct]
  implicit val quoteRequestProductPersonPageFormat = Json.format[QuoteRequestAndPersonAndProduct]

  implicit val quoteRequestPageFormat = Json.format[QuoteRequestPage]



  def get(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    personRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getQuoteRequests(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    quoteRequestRepo.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")).map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }
}


