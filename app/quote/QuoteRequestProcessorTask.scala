package quote

import javax.inject.Inject

import akka.actor.ActorSystem
import company.{Company, CompanyRepository}
import org.joda.time.{DateTime, LocalDate}
import person.{Person, PersonRepository}
import play.api.libs.json.{Format, JodaReads, JodaWrites, Json}
import play.api.libs.ws.WSClient
import product.{ASIProduct, ASIProductGetter, ASIProductRepository}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


class QuoteRequestProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanyRepository, personRepository: PersonRepository, quoteSlickRepository: QuoteSlickRepository, mockQuoteRequestRepository: MockQuoteRequestRepository, asiProductRepository: ASIProductRepository)(implicit executionContext: ExecutionContext) {

  implicit val jodaWrites = JodaWrites.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val jodaReads = JodaReads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val jodaFormat: Format[DateTime] = Format(jodaReads, jodaWrites)
  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]
  implicit val mockQuoteRequestsReads = Json.reads[MockQuoteRequest]

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 1.minutes) {
    println("Looking for mock quote requests")
    getMockQuoteRequests()
  }

  def getMockQuoteRequests() = {
    ws.url("http://localhost:9000/mock-quote-requests").
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

  def flagMockQuoteRequestImported(qr: MockQuoteRequest): Future[Option[Long]] = {
    // TODO WS call to flag as imported
   Future(Some(1))
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

  def insertProduct(asiProduct: ASIProduct): Future[Option[Long]] = {
    val internalId = asiProductRepository.insert(asiProduct)

    // TODO insert quote_product
    //   internalId.map(id => )
    Future(internalId)
  }

  def importQuoteRequest(qr: MockQuoteRequest) = {

    val quote = for {
      c <- findOrAddCompany(qr.company)
      p <- findOrAddPerson(qr, c)
      q <- insertQuote(qr, p.id.get)
      asi <- asiProductGetter.get(qr.productId)
      pr <- insertProduct(asi)
      f <- flagMockQuoteRequestImported(qr)
    } yield q

    println("Done")
  }
}

