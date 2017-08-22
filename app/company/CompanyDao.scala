package company

import javax.inject.{Inject, Singleton}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.BaseTypedType
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton()
class CompanyDaoWrapper @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val companyDao = new CompanyDAO(profile)
  def findAll() = db.run(companyDao.findAll())
  def insert(company: Company) = db.run(companyDao.save(company))
  def update(company: Company) = db.run(companyDao.update(company))
  def delete(id: Int) = db.run(companyDao.findOne(id)).map (company => company match {
    case Some(someCompany) => db.run(companyDao.delete(someCompany)); id
    case _ => throw new RuntimeException("No company found with id " + id)
  })
}

class CompanyDAO(override val driver: JdbcProfile) extends Repository[Company, Int](driver) {

  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[People]
  type TableType = People

  class People(tag: slick.lifted.Tag) extends Table[Company](tag, "company") with Keyed[Int] {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id.?, name) <> ((Company.apply _).tupled, Company.unapply)
  }
}

