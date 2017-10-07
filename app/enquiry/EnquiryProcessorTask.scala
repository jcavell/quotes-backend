package enquiry

import javax.inject.Inject

import akka.actor.ActorSystem
import asiquote.ASIQuote
import company.{Company, CompanySlickRepository}
import customer.{Customer, CustomerSlickRepository}
import formats.CustomFormats._
import mockenquiry.MockEnquirySlickRepository
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class EnquiryProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, companyRepository: CompanySlickRepository, customerRepository: CustomerSlickRepository, mockEnquiryRepository: MockEnquirySlickRepository, enquiryRepository: EnquirySlickRepository, enquiryWSClient: EnquiryWSClient)(implicit executionContext: ExecutionContext) {



  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    Logger.info("Looking for mock enquiries")
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
      case _ => customerRepository.insert(Customer(name = qr.customerName, email = qr.customerEmail, mobilePhone = Some(qr.customerTelephone), companyId = company.id.get))
    }
  }

  def findOrAddCompany(name: String): Future[Company] = companyRepository.findByName(name).flatMap { companyOption =>
    companyOption match {
      case Some(c) => Future(c)
      case None => companyRepository.insert(Company(name = name))
    }
  }

  def insertQuote(qr: Enquiry, customerId: Long) = {
    val quote = ASIQuote(status = "REQUESTED", requestTimestamp = qr.enquiryTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerFirstName = qr.customerName, requestCustomerLastName = qr.customerName, requestCustomerTel = qr.customerTelephone, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, customerId = customerId)

   // val result = quoteSlickRepository.insert(quote)
   // Logger.debug("Result from inserting into quote repo: " + result)
   // result
  }

  def importEnquiry(enquiry: Enquiry) = {

    val insertedEnquiry = for {
      company <- findOrAddCompany(enquiry.company)
      customer <- findOrAddCustomer(enquiry, company)
      insertedEnquiry <- enquiryRepository.insert(enquiry.copy(imported = true))
      // quote <- insertQuote(qr, customer.id.get)
      // asi <- asiProductGetter.get(qr.productId)
      // product <- asiProductRepository.insert(asi)
      // quoteProduct <- asiProductRepository.insertQuoteProduct(quote.id.get, product.internalId.get)
      f <- enquiryWSClient.flagImported(enquiry.enquiryId)
    } yield insertedEnquiry

    Logger.debug(s"Inserted ${insertedEnquiry}")
  }
}

