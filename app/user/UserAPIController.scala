package user

import javax.inject._

import play.api.libs.json.Writes.dateWrites
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class UserAPIController @Inject()(userRepo: UserSlickRepository, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val customDateWrites: Writes[java.util.Date] = dateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")
  implicit val userFormat = Json.format[User]


  def getUsers() = Action.async { implicit request =>
    userRepo.all.map { page =>
      val json = Json.toJson(page)
      Ok(json)
    }
  }

  def insertUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => Future(BadRequest(errors.mkString)),
      user => {
        userRepo.insert(user).map { userWithId =>
          Ok(Json.toJson(userWithId))
        }
      }
    )
  }

  def updateUser(id: Long) = Action(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => BadRequest(errors.mkString),
      user => {
        userRepo.update(id, user)
        Ok("Updated User")
      }
    )
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    userRepo.delete(id).map { a =>
      Ok("Deleted User")
    }
  }

}


