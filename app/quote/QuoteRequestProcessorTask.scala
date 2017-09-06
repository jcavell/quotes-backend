package quote

import javax.inject.Inject

import akka.actor.ActorSystem
import company.{Company, CompanySlickRepository}
import customer.{Customer, CustomerSlickRepository}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import product._
import formats.CustomFormats._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger


class QuoteRequestProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanySlickRepository, customerRepository: CustomerSlickRepository, quoteSlickRepository: QuoteSlickRepository, mockQuoteRequestRepository: MockQuoteRequestRepository, asiProductRepository: ASIProductSlickRepository)(implicit executionContext: ExecutionContext) {

  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    Logger.info("Looking for mock quote requests")
    getMockQuoteRequests()
  }

  def getMockQuoteRequests() = {
    ws.url("http://localhost:9000/unimported-mock-quote-requests").
      get() map { response =>
      val mockQuoteRequests = Json.parse(response.body).as[Seq[MockQuoteRequest]];
      mockQuoteRequests.foreach(importQuoteRequest(_));
    }
  }


  def findOrAddCustomer(qr: MockQuoteRequest, company: Company):Future[Customer] = customerRepository.findByEmail(qr.customerEmail).flatMap { customerOption =>
    customerOption match {
      case Some(p) => Future(p)
      case _ => customerRepository.insert(Customer(firstName = qr.customerFirstName, lastName = qr.customerLastName, email = qr.customerEmail, mobilePhone = Some(qr.customerTel), companyId = company.id.get))
    }
  }

  def flagMockQuoteRequestImported(qr: MockQuoteRequest): Future[MockQuoteRequest] = {
    mockQuoteRequestRepository.update(qr.copy(imported = true))
  }

  def findOrAddCompany(name: String): Future[Company] = companyRepository.findByName(name).flatMap { companyOption =>
    companyOption match {
      case Some(c) => Future(c)
      case None => companyRepository.insert(Company(name = name))
    }
  }

  def insertQuote(qr: MockQuoteRequest, customerId: Int): Future[Quote] = {
    val quote = Quote(status = "REQUESTED", requestTimestamp = qr.requestTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerFirstName = qr.customerFirstName, requestCustomerLastName = qr.customerLastName, requestCustomerTel = qr.customerTel, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, customerId = customerId)

    val result = quoteSlickRepository.insert(quote)
    Logger.debug("Result from inserting into quote repo: " + result)
    result
  }

  def importQuoteRequest(qr: MockQuoteRequest) = {

    val quote = for {
      company <- findOrAddCompany(qr.company)
      customer <- findOrAddCustomer(qr, company)
      quote <- insertQuote(qr, customer.id.get)
      asi <- asiProductGetter.get(qr.productId)
      product <- asiProductRepository.insert(asi)
      quoteProduct <- asiProductRepository.insertQuoteProduct(quote.id.get, product.internalId.get)
      f <- flagMockQuoteRequestImported(qr)
    } yield quote

    Logger.debug(s"Inserted ${quote}")
  }
}

