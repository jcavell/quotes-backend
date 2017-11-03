package company

import javax.inject.{Inject, Singleton}

import db.{Search, Sort}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton()
class CompanySlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CompaniesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val companies = TableQuery[Companies]

  def findById(id: Long):Future[Option[Company]] = db.run(companies.filter(_.id === id).result.headOption)
  def all: Future[List[Company]] = db.run(companies.to[List].result)

  def insert(co: Company): Future[Company] = {

    val company = co.copyWithCanonicalFields
    val action = companies returning companies.map {_.id} += company

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => company.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }

  def search(search: Search, sort: Sort): Future[List[Company]] = db.run(getSearchQuery(search, sort))
  
  def count(search: Search, sort: Sort): Future[Int] = db.run(getSearchQuery(search, sort).map(_.length))
  

  def getSearchQuery(search: Search, sort: Sort) = {
    val query = companies.to[List]
    val queryWithSearch =
      if (search.containsSearchTerm) {
        query.filter(t => List(search.getSearchValue("name", CompanyCanonicaliser.canonicaliseName).
            map(name => t.canonicalName like s"%${name.toLowerCase}%")).
            collect(
            { case Some(criteria) => criteria }).
            reduceLeft(_ || _)
        )
      } else query

    queryWithSearch.
      sortBy(t => (t.name.asc, t.id.asc)).
      drop(((search.page -1) * search.rpp)).take(search.rpp).
      result
  }

  def findByCanonicalName(canonicalName: String):Future[Option[Company]] = db.run(companies.filter(_.canonicalName === canonicalName).result.headOption)
  def findByName(name: String):Future[Option[Company]] = db.run(companies.filter(_.name === name).result.headOption)

  def update(id: Long, co: Company): Future[Company] = {
    val company = co.copyWithCanonicalFields
    val companyToUpdate: Company = company.copy(Some(id))
    db.run(companies.filter(_.id === id).update(companyToUpdate)).map(_ => (company))
  }

  def delete(id: Long): Future[Unit] = db.run(companies.filter(_.id === id).delete).map(_ => ())
}
