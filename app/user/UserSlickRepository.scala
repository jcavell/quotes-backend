package user

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}



trait UserComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Users(tag: Tag) extends Table[User](tag, "iuser") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def directPhone = column[Option[String]]("direct_phone")
    def mobilePhone = column[Option[String]]("mobile_phone")
    def active = column[Boolean]("active")
    def * = (id, name, email, directPhone, mobilePhone, active) <> (User.tupled, User.unapply _)
  }

}

@Singleton()
class UserSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UserComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val users = TableQuery[Users]

  def all: Future[List[User]] = {
    db.run(users.to[List].result)
  }

  def insert(user: User): Future[User] = {
    val action = users returning users.map {_.id} += user

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => user.copy(id = r)
        case Failure(e) => {
          Logger.error(s"SQL Error inserting ${user}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(id: Long, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(Some(id))
    db.run(users.filter(_.id === id).update(userToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(users.filter(_.id === id).delete).map(_ => ())

}
