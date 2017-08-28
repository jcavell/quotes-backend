package person

import javax.inject.{Inject, Singleton}

import company.CompanySlickRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait PeopleComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class People(tag: Tag) extends Table[Person](tag, "person") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def tel = column[String]("tel")
    def companyId = column[Int]("company_id")
    def * = (id.?, name, email, tel, companyId) <> (Person.tupled, Person.unapply _)
  }
}

@Singleton()
class PersonSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, companyRepository: CompanySlickRepository)(implicit executionContext: ExecutionContext)
  extends PeopleComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val people = TableQuery[People]

  def all: Future[List[Person]] = db.run(people.to[List].result)

  def allWithCompany = {
    val query = for {
      (person, company) <- people join companyRepository.companies on(_.companyId === _.id)
    } yield (person, company)

    val x = db.run(query.to[List].result)
    x
  }

  def insert(person: Person): Future[Person] = {
    val action = people returning people.map {_.id} += person

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => person.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }

  def findByEmail(email: String):Future[Option[Person]] = db.run(people.filter(_.email === email).result.headOption)

  def update(person: Person): Future[Person] = {
    val personToUpdate: Person = person.copy(person.id)
    db.run(people.filter(_.id === person.id).update(personToUpdate)).map(_ => (person))
  }

  def delete(id: Int): Future[Unit] = db.run(people.filter(_.id === id).delete).map(_ => ())

}
