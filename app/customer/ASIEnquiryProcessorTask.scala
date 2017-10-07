package customer

import javax.inject.Inject

import akka.actor.ActorSystem
import asiproduct._
import asiquote.{ASIQuote, QuoteSlickRepository}
import mockenquiry.{MockEnquiry, MockEnquirySlickRepository}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import formats.CustomFormats._


class ASIEnquiryProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanySlickRepository, customerRepository: CustomerSlickRepository, quoteSlickRepository: QuoteSlickRepository, mockQuoteRequestRepository: MockEnquirySlickRepository, asiProductRepository: ASIProductSlickRepository)(implicit executionContext: ExecutionContext) {



  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    Logger.info("Looking for mock quote requests")
    getMockEnquiries()
  }

  def getMockEnquiries() = {
    ws.url("http://localhost:9000/unimported-mock-enquiries").
      get() map { response =>
      val mockEnquiries = Json.parse(response.body).as[Seq[MockEnquiry]];
      mockEnquiries.foreach(importEnquiry(_));
    }
  }


  def findOrAddCustomer(qr: MockEnquiry, company: Company):Future[Customer] = customerRepository.findByEmail(qr.customerEmail).flatMap { customerOption =>
    customerOption match {
      case Some(p) => Future(p)
      case _ => customerRepository.insert(Customer(firstName = qr.customerName, lastName = qr.customerName, email = qr.customerEmail, mobilePhone = Some(qr.customerTelephone), companyId = company.id.get))
    }
  }

  def flagMockEnquiryImported(qr: MockEnquiry): Future[MockEnquiry] = {
    mockQuoteRequestRepository.update(qr.copy(imported = true))
  }

  def findOrAddCompany(name: String): Future[Company] = companyRepository.findByName(name).flatMap { companyOption =>
    companyOption match {
      case Some(c) => Future(c)
      case None => companyRepository.insert(Company(name = name))
    }
  }

  def insertQuote(qr: MockEnquiry, customerId: Long): Future[ASIQuote] = {
    val quote = ASIQuote(status = "REQUESTED", requestTimestamp = qr.enquiryTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerFirstName = qr.customerName, requestCustomerLastName = qr.customerName, requestCustomerTel = qr.customerTelephone, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, customerId = customerId)

    val result = quoteSlickRepository.insert(quote)
    Logger.debug("Result from inserting into quote repo: " + result)
    result
  }

  def importEnquiry(qr: MockEnquiry) = {

    val quote = for {
      company <- findOrAddCompany(qr.company)
      customer <- findOrAddCustomer(qr, company)
      quote <- insertQuote(qr, customer.id.get)
      asi <- asiProductGetter.get(qr.productId)
      product <- asiProductRepository.insert(asi)
      quoteProduct <- asiProductRepository.insertQuoteProduct(quote.id.get, product.internalId.get)
      f <- flagMockEnquiryImported(qr)
    } yield quote

    Logger.debug(s"Inserted ${quote}")
  }
}

