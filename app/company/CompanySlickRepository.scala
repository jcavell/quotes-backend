package company

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Created by jcavell on 07/10/2017.
  */
@Singleton()
class CompanySlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CompaniesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val companies = TableQuery[Companies]

  def findById(id: Long):Future[Option[Company]] = db.run(companies.filter(_.id === id).result.headOption)
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
