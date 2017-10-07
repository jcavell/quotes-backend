package enquiry

import javax.inject._

import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

class EnquiryWSClient @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  val headers = ("Content-Type" -> "application/json")

  def flagImported(enquiryId: Long) = {
    println("Flagging enquiry as imported: " + enquiryId)
    ws.url(s"http://localhost:9000/flag-mock-enquiry-imported/$enquiryId").
      addHttpHeaders(headers).
      post("")
  }
}


