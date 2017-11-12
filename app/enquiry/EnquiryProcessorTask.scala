package enquiry

import javax.inject.Inject

import akka.actor.ActorSystem
import company.{Company, CompanyCanonicaliser, CompanySlickRepository}
import customer.{Customer, CustomerSlickRepository}
import formats.CustomFormats._
import mockenquiry.MockEnquirySlickRepository
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import quote._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}


class EnquiryProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, companyRepository: CompanySlickRepository, customerRepository: CustomerSlickRepository, mockEnquiryRepository: MockEnquirySlickRepository, enquiryRepository: EnquirySlickRepository, quoteSlickRepository: QuoteSlickRepository, quoteLineItemSlickRepository: QuoteLineItemSlickRepository, quoteXsellItemSlickRepository: QuoteXsellItemSlickRepository, quoteMetaSlickRepository: QuoteMetaSlickRepository, enquiryWSClient: EnquiryWSClient)(implicit executionContext: ExecutionContext) {


  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 10.minutes) {
    Logger.info("Looking for mock enquiries")
    getMockEnquiries()
  }

  def getMockEnquiries() = {
    ws.url("http://localhost:9000/unimported-mock-enquiries").
      get() map { response =>
      val mockEnquiries = Json.parse(response.body).as[Seq[Enquiry]]
      mockEnquiries.map(importEnquiry(_));
    }
  }


  def findCustomer(enquiry: Enquiry, company: Option[Company]):Future[Option[Customer]] = {
    company match{
      case Some(co) => customerRepository.findByCompanyAndEmail(co.id.get, enquiry.customerEmail)
      case None => Future(None)
    }
//    customerRepository.findByCompanyAndEmail(company.id.get, enquiry.customerEmail).flatMap { customerOption =>
//      customerOption match {
//        case Some(p) => Future(p)
//        case _ => customerRepository.insert(Customer(name = enquiry.customerName, email = enquiry.customerEmail, mobilePhone = Some(enquiry.customerTelephone), companyId = company.id.get, source = enquiry.source))
//      }
//    }
  }

  def findCompany(enquiry: Enquiry): Future[Option[Company]] = {
    val givenCompany = enquiry.company.toLowerCase
    val companyName = if(givenCompany.isEmpty || givenCompany == "none" || givenCompany == "na" || givenCompany == "n/a") enquiry.customerEmail else enquiry.company
    companyRepository.findByCanonicalName(CompanyCanonicaliser.canonicaliseName(companyName))
  }

  def generateQuote(enquiry: Enquiry, customer: Option[Customer]) = {
    val title = s"${enquiry.subject.getOrElse("")}: ${enquiry.productName}"
    Quote(title = title, requiredDate = enquiry.requiredDate, specialInstructions = enquiry.otherRequirements, repEmail = enquiry.repEmail, createdDate = DateTime.now, enquiryId = enquiry.id, customerId = customer.flatMap(_.id))
  }

  def insertQuote(enquiry: Enquiry, customer: Option[Customer]): Future[Quote] =
    quoteSlickRepository.insert(generateQuote(enquiry, customer))

  def insertQuoteMeta(quote: Quote): Future[QuoteMeta] = {
    val quoteMeta = QuoteMeta(status = QuoteStatuses.NEW, stage = QuoteStages.ENQUIRY, quoteId = quote.id.get)

    val result = quoteMetaSlickRepository.insert(quoteMeta)
    result
  }

  def insertQuoteLineItem(enquiry: Enquiry, quote: Quote): Future[QuoteLineItem] = {
    val quoteLineItem = QuoteLineItem(productId = enquiry.productId, quantity = enquiry.quantity, colour = enquiry.colour, quoteId = quote.id.get)

    val result = quoteLineItemSlickRepository.insert(quoteLineItem)
    result
  }


  def insertQuoteXsells(productIds: Seq[Long], quote: Quote): Future[Seq[QuoteXsellItem]] = {
    val xsells = productIds.map{ xsellId =>
      val result = quoteXsellItemSlickRepository.insert(
        QuoteXsellItem(productId = xsellId, quoteId = quote.id.get)
      )
      Logger.debug("Result from inserting into quote repo: " + result)
      result
    }
    Future.sequence(xsells)
  }

  def importEnquiry(enquiry: Enquiry) = {

    val f = for {
      insertedEnquiry <- enquiryRepository.insert(enquiry)
      company <- findCompany(insertedEnquiry)
      customer <- findCustomer(insertedEnquiry, company)
      quote <- insertQuote(insertedEnquiry, customer)
      quoteMeta <- insertQuoteMeta(quote)
      quoteLineItem <- insertQuoteLineItem(insertedEnquiry, quote)
      quoteXsells <- insertQuoteXsells(insertedEnquiry.xsellProductIds, quote)
      // asi <- asiProductGetter.get(qr.productId)
      // product <- asiProductRepository.insert(asi)
      // quoteProduct <- asiProductRepository.insertQuoteProduct(quote.id.get, product.internalId.get)
      f <- enquiryWSClient.flagImported(insertedEnquiry.enquiryId)
    } yield (f)

    // TODO remove the await and have proper transaction and error handling
 Await.result(f, 100 seconds)
  }
}

