package company

import javax.inject.Inject

import anorm.SqlParser.get
import anorm.{SQL, ~}
import db.DatabaseExecutionContext
import play.api.db.DBApi

import scala.concurrent.Future


@javax.inject.Singleton
class CompanyRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  val simple = {
    get[Option[Int]]("company.id") ~
      get[String]("company.name") map {
      case id~name => Company(id, name)
    }
  }
}
