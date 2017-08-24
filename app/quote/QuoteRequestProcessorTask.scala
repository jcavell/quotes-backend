package quote

import javax.inject.{Inject, Named}

import akka.actor.{ActorRef, ActorSystem}
import company.{Company, CompanyRepository}
import person.{Person, PersonRepository}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import product.{ASIProduct, ASIProductGetter}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


class QuoteRequestProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter, companyRepository: CompanyRepository, personRepository: PersonRepository, quoteRepository: QuoteRepository, mockQuoteRequestRepository: MockQuoteRequestRepository)(implicit executionContext: ExecutionContext) {

  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]
  implicit val mockQuoteRequestsReads = Json.reads[MockQuoteRequest]

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 10.minutes) {
    println("Looking for mock quote requests")
    //getMockQuoteRequests()
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

  def flagMockQuoteRequestImported(qr: MockQuoteRequest): Future[Boolean] = {
    // TODO WS call to flag as imported
    Future(true)
  }

  def findOrAddCompany(name: String): Future[Company] = companyRepository.findByName(name).flatMap { companyOption =>
    companyOption match {
      case Some(c) => Future(c)
      case None => companyRepository.insert(Company(name = name))
    }
  }

  def insertQuote(qr: MockQuoteRequest, personId: Int) = {
    val quote = Quote(status = "REQUESTED", requestTimestamp = qr.requestTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerName = qr.customerName, requestCustomerTel = qr.customerTel, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements, personId = personId)
    // TODO insert
    Future(1)
  }

  def importQuoteRequest(qr: MockQuoteRequest) = {

    val quote = for {
      c <- findOrAddCompany(qr.company)
      p <- findOrAddPerson(qr, c)
      q <- insertQuote(qr, p.id.get)
      asi <- asiProductGetter.get(qr.productId)
      f <- flagMockQuoteRequestImported(qr)
    } yield q

    println("Done")
  }
}

