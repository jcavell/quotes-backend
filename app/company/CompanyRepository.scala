package company

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait CompaniesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Companies(tag: Tag) extends Table[Company](tag, "company") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> (Company.tupled, Company.unapply _)
  }

}

@Singleton()
class CompanyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
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

  def update(id: Int, company: Company): Future[Company] = {
    val companyToUpdate: Company = company.copy(Some(id))
    db.run(companies.filter(_.id === id).update(companyToUpdate)).map(_ => (company))
  }

  def delete(id: Int): Future[Unit] = db.run(companies.filter(_.id === id).delete).map(_ => ())

}
