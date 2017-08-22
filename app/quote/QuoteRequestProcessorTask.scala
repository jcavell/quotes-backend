package quote

import javax.inject.{Inject, Named}

import akka.actor.{ActorRef, ActorSystem}
import person.Person
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import product.{ASIProduct, ASIProductGetter}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


class QuoteRequestProcessorTask @Inject()(actorSystem: ActorSystem, ws: WSClient, asiProductGetter: ASIProductGetter)(implicit executionContext: ExecutionContext) {

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

  def importQuoteRequest(qr: MockQuoteRequest) = {

    //    val company =
    //    val person = Person
    //
    //    val quote = Quote(status = "REQUESTED", requestTimestamp = qr.requestTimestamp, requestDateRequired = qr.dateRequired, requestProductId = qr.productId, requestCustomerName = qr.customerName, requestCustomerTel = qr.customerTel, requestCustomerEmail = qr.customerEmail, requestCompany = qr.company, requestQuantity = qr.quantity, requestOtherRequirements = qr.otherRequirements);
    //  }

    println("Getting ASI product")
    asiProductGetter.get(qr.productId).map{ product =>
      println("Got product: " + product)
    }
    println("Done")
  }
}

