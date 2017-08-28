package person

import javax.inject._

import company.Company
import person.Person
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class PersonAPIController @Inject()(personRepository: PersonSlickRepository, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]
  implicit val companyFormat = Json.format[Company]
  implicit val personCompanyFormat = Json.format[(Person, Company)]

  def getPeople() = Action.async { implicit request =>
    personRepository.allWithCompany.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertPerson() = Action.async(parse.json) { implicit request =>
    println("Validating person: " + request.body)
    request.body.validate[Person].fold(
      errors => Future(BadRequest(errors.mkString)),
      person => {
        personRepository.insert(person).map { person =>
          Ok(Json.toJson(person))
        }
      }
    )
  }

  def updatePerson(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[Person].fold(
      errors => Future(BadRequest(errors.mkString)),
      person => {
        personRepository.update(person).map { personCompany =>
          Ok(Json.toJson(personCompany))
        }
      }
    )
  }

  def deletePerson(id: Int) = Action.async { implicit request =>
    personRepository.delete(id).map { a =>
      Ok("Deleted Person")
    }
  }
}


