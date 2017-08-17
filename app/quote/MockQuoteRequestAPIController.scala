package quote

import javax.inject._

import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class MockQuoteRequestAPIController @Inject()(mockQuoteRequestsDao: MockQuoteRequestsDAO, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def timestampWrites(pattern: String): Writes[java.sql.Date] = new Writes[java.sql.Date] {
    def writes(d: java.sql.Date): JsValue = JsString(new java.text.SimpleDateFormat(pattern).format(d))
  }

  implicit val customTimestampWrites: Writes[java.sql.Date] = timestampWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]


  def getMockQuoteRequests() = Action.async { implicit request =>
    mockQuoteRequestsDao.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertMockQuoteRequest() = Action.async(parse.json) { implicit request =>
    request.body.validate[MockQuoteRequest].fold(
      errors => Future(BadRequest(errors.mkString)),
      mockQuoteRequest => {
        mockQuoteRequestsDao.insert(mockQuoteRequest).map { mockQuoteRequestWithId =>
          Ok(Json.toJson(mockQuoteRequestWithId))
        }
      }
    )
  }

  def deleteMockQuoteRequest(id: Long) = Action.async { implicit request =>
    mockQuoteRequestsDao.delete(id).map { a =>
      Ok("Deleted MockQuoteRequest")
    }
  }

}


