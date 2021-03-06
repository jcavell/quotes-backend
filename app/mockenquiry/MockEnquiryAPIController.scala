package mockenquiry

import javax.inject._

import enquiry.Enquiry
import formats.CustomFormats._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class MockEnquiryAPIController @Inject()(mockEnquiryRepository: MockEnquirySlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getMockEnquiries() = Action.async { implicit request =>
    mockEnquiryRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def getUnimportedMockEnquiries() = Action.async { implicit request =>
    mockEnquiryRepository.allUnimported.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def flagImported(id: Long) = Action.async { implicit request =>
    mockEnquiryRepository.flagMockEnquiryImported(id).map { _ =>
      val json = Json.toJson(true)
      Ok(json)
    }
  }

  def insertMockEnquiry() = Action.async(parse.json) { implicit request =>
    request.body.validate[Enquiry].fold(
      errors => Future(BadRequest(errors.mkString)),
      mockEnquiry => {
        mockEnquiryRepository.insert(mockEnquiry).map { mockEnquiryWithId =>
          Created(Json.toJson(mockEnquiryWithId))
        }
      }
    )
  }

  def deleteMockEnquiry(id: Long) = Action.async { implicit request =>
    mockEnquiryRepository.delete(id).map { a =>
      Ok("Deleted Mock Enquiry")
    }
  }

}


