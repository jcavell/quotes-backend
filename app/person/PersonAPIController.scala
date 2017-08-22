package person

import javax.inject._

import person.Person
import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class PersonAPIController @Inject()(personDao: PersonDaoWrapper, cc:ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val personFormat = Json.format[Person]


  def getPeople() = Action.async { implicit request =>
    personDao.findAll().map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertPerson() = Action.async(parse.json) { implicit request =>
    println("Validating person: " + request.body)
    request.body.validate[Person].fold(
      errors => Future(BadRequest(errors.mkString)),
      person => {
        personDao.insert(person).map { personWithId =>
          Ok(Json.toJson(personWithId))
        }
      }
    )
  }

  def updatePerson(id: Int) = Action(parse.json) { implicit request =>
    request.body.validate[Person].fold(
      errors => BadRequest(errors.mkString),
      person => {
        personDao.update(person)
        Ok("Updated Person")
      }
    )
  }

  def deletePerson(id: Int) = Action.async { implicit request =>
    personDao.delete(id).map { a =>
      Ok("Deleted Person")
    }
  }
}


