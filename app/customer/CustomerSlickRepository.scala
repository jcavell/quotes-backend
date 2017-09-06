package customer

import javax.inject.{Inject, Singleton}

import company.CompanySlickRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import play.api.Logger

trait CustomersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Customers(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def salutation = column[Option[String]]("salutation")
    def email = column[String]("email")
    def directPhone = column[Option[String]]("direct_phone")
    def mobilePhone = column[Option[String]]("mobile_phone")
    def source = column[Option[String]]("source")
    def position = column[Option[String]]("position")
    def isMainContact = column[Boolean]("is_main_contact")
    def twitter = column[Option[String]]("twitter")
    def facebook = column[Option[String]]("facebook")
    def linkedIn = column[Option[String]]("linked_in")
    def skype = column[Option[String]]("skype")
    def handlerId = column[Option[Int]]("handler_id")
    def companyId = column[Int]("company_id")
    def * = (id.?, firstName, lastName, salutation, email, directPhone, mobilePhone, source, position, isMainContact, twitter, facebook, linkedIn, skype, handlerId, companyId) <> (Customer.tupled, Customer.unapply _)
  }
}

@Singleton()
class CustomerSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, companyRepository: CompanySlickRepository)(implicit executionContext: ExecutionContext)
  extends CustomersComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val customers = TableQuery[Customers]

  def all: Future[List[Customer]] = db.run(customers.to[List].result)

  def allWithCompany = {
    val query = for {
      (customer, company) <- customers join companyRepository.companies on(_.companyId === _.id)
    } yield (customer, company)

    db.run(query.to[List].result)
  }

  def insert(customer: Customer): Future[Customer] = {
    val action = customers returning customers.map {_.id} += customer

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => customer.copy(id = Some(r))
        case Failure(e) => Logger.error(s"Error inserting customer: $e"); throw e
      }
    }
  }

  def findByEmail(email: String):Future[Option[Customer]] = db.run(customers.filter(_.email === email).result.headOption)

  def update(customer: Customer): Future[Customer] = {
    val action = customers.filter(_.id === customer.id).update(customer)

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => customer
        case Failure(e) => Logger.error(s"Error updating customer: $e"); throw e
      }
    }
  }

  def delete(id: Int): Future[Unit] = db.run(customers.filter(_.id === id).delete).map(_ => ())

}
