package customer

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait CompaniesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Companies(tag: Tag) extends Table[Company](tag, "company") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def phone1 = column[Option[String]]("phone1")
    def phone2 = column[Option[String]]("phone2")
    def phone3 = column[Option[String]]("phone3")
    def website = column[Option[String]]("website")
    def twitter = column[Option[String]]("twitter")
    def facebook = column[Option[String]]("facebook")
    def linkedIn = column[Option[String]]("linked_in")
    def source = column[Option[String]]("source")
    def active = column[Boolean]("active")

    def * = (id.?, name, phone1, phone2, phone3, website, twitter, facebook, linkedIn, source, active) <> (Company.tupled, Company.unapply _)
  }

}
@Singleton()
class CompanySlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CompaniesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val companies = TableQuery[Companies]

  def all: Future[List[Company]] = db.run(companies.to[List].result)

  def insert(company: Company): Future[Company] = {
    val action = companies returning companies.map {_.id} += company

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => company.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }

  def findByName(name: String):Future[Option[Company]] = db.run(companies.filter(_.name === name).result.headOption)

  def update(id: Long, company: Company): Future[Company] = {
    val companyToUpdate: Company = company.copy(Some(id))
    db.run(companies.filter(_.id === id).update(companyToUpdate)).map(_ => (company))
  }

  def delete(id: Long): Future[Unit] = db.run(companies.filter(_.id === id).delete).map(_ => ())

}
