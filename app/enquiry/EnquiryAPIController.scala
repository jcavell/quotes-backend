package enquiry

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import formats.CustomFormats._

import scala.concurrent.{ExecutionContext, Future}

class EnquiryAPIController @Inject()(enquiryRepository: EnquirySlickRepository, enquiryProcessorTask: EnquiryProcessorTask, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getEnquiries() = Action.async { implicit request =>
    enquiryRepository.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }


  def deleteEnquiry(id: Long) = Action.async { implicit request =>
    enquiryRepository.delete(id).map { a =>
      Ok("Deleted Enquiry")
    }
  }

  def importEnquiries() = Action.async { implicit request =>
    enquiryProcessorTask.getMockEnquiries().map { a =>
      Ok("Imported enquiries: " + a)
    }
  }

}


