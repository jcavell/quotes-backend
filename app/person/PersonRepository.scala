package person

import javax.inject.Inject

import anorm.SqlParser.{get, scalar}
import anorm.{SQL, ~}
import company.CompanyRepository
import db.DatabaseExecutionContext
import play.api.db.DBApi

import scala.concurrent.Future

@javax.inject.Singleton
class PersonRepository @Inject()(dbapi: DBApi, companyRepository: CompanyRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  // -- Parsers

  /**
    * Parse a Person from a ResultSet
    */
   val simple = {
    get[Option[Int]]("person.id") ~
      get[String]("person.name") ~
      get[String]("person.email") ~
      get[String]("person.tel") ~
      get[Option[Int]]("person.company_id") map {
      case id ~ name ~ email ~ tel ~ companyId =>
        Person(id, name, email, tel, companyId)
    }
  }

  /**
    * Parse a (Person,Company) from a ResultSet
    */
  private val withCompany = simple ~ (companyRepository.simple ?) map {
    case person ~ company => (person, company)
  }

  // -- Queries

  /**
    * Retrieve a Person from the id.
    */
  def findById(id: Int): Future[Option[Person]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from person where id = $id").as(simple.singleOpt)
    }
  }(ec)

  /**
    * Return a page of PersonCompany
    *
    * @param page     Page to display
    * @param pageSize Number of persons per page
    * @param orderBy  Computer property used for sorting
    * @param filter   Filter applied on the name column
    */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[Page] = Future {

    val offset = pageSize * page

    db.withConnection { implicit connection =>

      val persons = SQL(
        s"""
          select * from person
          left join company on person.company_id = company.id
          where person.name like {filter} or person.email like {filter} or company.name like {filter}
          order by $orderBy nulls last
          limit $pageSize offset $offset
        """
      ).on('filter -> filter).as(withCompany *)

      val totalRows = SQL(
        """
          select count(*) from person
          left join company on person.company_id = company.id
          where person.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      Page(persons.map(t => PersonCompany(t._1, t._2.get)), page, offset, totalRows)
    }

  }(ec)



  /**
    * Update a person.
    *
    * @param id     The person id
    * @param person The person values.
    */
  def update(id: Int, person: Person) = Future {
    db.withConnection { implicit connection =>
      SQL(
        s"""
          update person
          set name = {$person.name}, email = {$person.email}, tel = {$person.tel}, company_id = {$person.companyId}
          where id = {$id}
        """
      ).executeUpdate()
    }
  }(ec)

  /**
    * Insert a new person.
    *
    * @param person The person values.
    */
  def insert(person: Person) = Future {
    db.withConnection { implicit connection =>
      SQL(
        s"""
          insert into person values (
            (select next value for person_seq),
            ${person.name}, ${person.email}, ${person.tel}, ${person.companyId}
          )
        """
      ).executeUpdate()
    }
  }(ec)

  /**
    * Delete a person.
    *
    * @param id Id of the person to delete.
    */
  def delete(id: Int) = Future {
    db.withConnection { implicit connection =>
      SQL("delete from person where id = {$id}").executeUpdate()
    }
  }(ec)

}
