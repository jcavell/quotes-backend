package person

import javax.inject.{Inject, Singleton}

import com.byteslounge.slickrepo.meta.Keyed
import com.byteslounge.slickrepo.repository.Repository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.BaseTypedType
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class PersonDaoWrapper @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val personDao = new PersonDAO(profile)
  def findAll() = db.run(personDao.findAll())
  def insert(person: Person) = db.run(personDao.save(person))
  def update(person: Person) = db.run(personDao.update(person))
  def delete(id: Int) = db.run(personDao.findOne(id)).map (person => person match {
    case Some(somePerson) => db.run(personDao.delete(somePerson)); id
    case _ => throw new RuntimeException("No person found with id " + id)
  })
}

//@Singleton()
//class PersonDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
//  extends Repository[Person, Int](dbConfigProvider.get[JdbcProfile].profile)
//    with HasDatabaseConfigProvider[JdbcProfile] {
class PersonDAO(override val driver: JdbcProfile) extends Repository[Person, Int](driver) {

  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[People]
  type TableType = People

  class People(tag: slick.lifted.Tag) extends Table[Person](tag, "person") with Keyed[Int] {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def email = column[String]("email")

    def tel = column[String]("tel")

    def companyId = column[Option[Int]]("company_id")

    def * = (id.?, name, email, tel, companyId) <> ((Person.apply _).tupled, Person.unapply)
  }
}

