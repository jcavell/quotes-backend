package quote

import javax.inject.Inject

import akka.actor.ActorSystem
import company.{Company, CompanySlickRepository}
import org.joda.time.{DateTime, LocalDate}
import person.{Person, PersonSlickRepository}
import play.api.libs.json.{Format, JodaReads, JodaWrites, Json}
import play.api.libs.ws.WSClient
import product._
import formats.CustomFormats._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class QuoteRequestProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanySlickRepository, personRepository: PersonSlickRepository, quoteSlickRepository: QuoteSlickRepository, mockQuoteRequestRepository: MockQuoteRequestRepository, asiProductRepository: ASIProductSlickRepository)(implicit executionContext: ExecutionContext) {

  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    println("Looking for mock quote requests")
    getMockQuoteRequests()
  }

  def getMockQuoteRequests() = {
    ws.url("http://localhost:9000/unimported-mock-quote-requests").
      get() map { response =>
      val mockQuoteRequests = Json.parse(response.body).as[Seq[MockQuoteRequest]];
      mockQuoteRequests.foreach(importQuoteRequest(_));
    }
  }


  def findOrAddPerson(qr: MockQuoteRequest, company: Company):Future[Person] = personRepository.findByEmail(qr.customerEmail).flatMap { personOption =>
    personOption match {
      case Some(p) => Future(p)
      case _ => personRepository.insert(Person(name = qr.customerName, email = qr.customerEmail, tel = qr.customerTel, companyId = company.id.get))
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

  def insertQuote(qr: MockQuoteRequest, personId: Int): Future[Quote] = {
    val quote = Quote(status = "REQUESTED", requestTimestamp = qr.requestTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerName = qr.customerName, requestCustomerTel = qr.customerTel, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, personId = personId)

    val result = quoteSlickRepository.insert(quote)
    println("Result from inserting into quote repo: " + result)
    result
  }

  def importQuoteRequest(qr: MockQuoteRequest) = {

    val quote = for {
      company <- findOrAddCompany(qr.company)
      person <- findOrAddPerson(qr, company)
      quote <- insertQuote(qr, person.id.get)
      asi <- asiProductGetter.get(qr.productId)
      product <- asiProductRepository.insert(asi)
      quoteProduct <- asiProductRepository.insertQuoteProduct(quote.id.get, product.internalId.get)
      f <- flagMockQuoteRequestImported(qr)
    } yield quote

    println("Done")
  }
}

