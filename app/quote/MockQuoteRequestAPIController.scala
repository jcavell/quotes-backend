package quote

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}

class MockQuoteRequestAPIController @Inject()(mockQuoteRequestRepository: MockQuoteRequestRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val mockQuoteRequestFormat = Json.format[MockQuoteRequest]

  def getMockQuoteRequests() = Action.async { implicit request =>
    mockQuoteRequestRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getUnimportedMockQuoteRequests() = Action.async { implicit request =>
    mockQuoteRequestRepository.allUnimported.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertMockQuoteRequest() = Action.async(parse.json) { implicit request =>
    request.body.validate[MockQuoteRequest].fold(
      errors => Future(BadRequest(errors.mkString)),
      mockQuoteRequest => {
        mockQuoteRequestRepository.insert(mockQuoteRequest).map { mockQuoteRequestWithId =>
          Ok(Json.toJson(mockQuoteRequestWithId))
        }
      }
    )
  }

  def deleteMockQuoteRequest(id: Int) = Action.async { implicit request =>
    mockQuoteRequestRepository.delete(id).map { a =>
      Ok("Deleted MockQuoteRequest")
    }
  }

}


