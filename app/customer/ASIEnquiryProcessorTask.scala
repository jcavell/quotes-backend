package customer

import javax.inject.Inject

import akka.actor.ActorSystem
import asiproduct._
import asiquote.{ASIQuote, QuoteSlickRepository}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import formats.CustomFormats._


class ASIEnquiryProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanySlickRepository, customerRepository: CustomerSlickRepository, quoteSlickRepository: QuoteSlickRepository, mockQuoteRequestRepository: EnquiryRepository, asiProductRepository: ASIProductSlickRepository)(implicit executionContext: ExecutionContext) {



  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    Logger.info("Looking for mock quote requests")
    getMockEnquiries()
  }

  def getMockEnquiries() = {
    ws.url("http://localhost:9000/unimported-mock-enquiries").
      get() map { response =>
      val mockEnquiries = Json.parse(response.body).as[Seq[Enquiry]];
      mockEnquiries.foreach(importEnquiry(_));
    }
  }


  def findOrAddCustomer(qr: Enquiry, company: Company):Future[Customer] = customerRepository.findByEmail(qr.customerEmail).flatMap { customerOption =>
    customerOption match {
      case Some(p) => Future(p)
      case _ => customerRepository.insert(Customer(firstName = qr.customerFirstName, lastName = qr.customerLastName, email = qr.customerEmail, mobilePhone = Some(qr.customerTel), companyId = company.id.get))
    }
  }

  def flagMockEnquiryImported(qr: Enquiry): Future[Enquiry] = {
    mockQuoteRequestRepository.update(qr.copy(imported = true))
  }

  def findOrAddCompany(name: String): Future[Company] = companyRepository.findByName(name).flatMap { companyOption =>
    companyOption match {
      case Some(c) => Future(c)
      case None => companyRepository.insert(Company(name = name))
    }
  }

  def insertQuote(qr: Enquiry, customerId: Long): Future[ASIQuote] = {
    val quote = ASIQuote(status = "REQUESTED", requestTimestamp = qr.requestTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerFirstName = qr.customerFirstName, requestCustomerLastName = qr.customerLastName, requestCustomerTel = qr.customerTel, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, customerId = customerId)

    val result = quoteSlickRepository.insert(quote)
    Logger.debug("Result from inserting into quote repo: " + result)
    result
  }

  def importEnquiry(qr: Enquiry) = {

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

