package company

import javax.inject.Inject

import anorm.SqlParser.get
import anorm.{SQL, ~}
import db.DatabaseExecutionContext
import play.api.db.DBApi

import scala.concurrent.Future

/**
  * Created by jcavell on 16/08/2017.
  */
@javax.inject.Singleton
class CompanyRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
   * Parse a Company from a ResultSet
   */
  val simple = {
    get[Option[Long]]("company.id") ~
      get[String]("company.name") map {
      case id~name => Company(id, name)
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Future[Seq[(String,String)]] = Future(db.withConnection { implicit connection =>
    SQL("select * from company order by name").as(simple *).
      foldLeft[Seq[(String, String)]](Nil) { (cs, c) =>
      c.id.fold(cs) { id => cs :+ (id.toString -> c.name) }
    }
  })(ec)

}
