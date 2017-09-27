package customer

import javax.inject.{Inject, Singleton}

import address.AddressSlickRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import play.api.Logger
import user.UserSlickRepository

trait CustomersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Customers(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
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
    def active = column[Boolean]("active")
    def repId = column[Option[Long]]("rep_id")
    def companyId = column[Long]("company_id")
    def invoiceAddressId = column[Option[Long]]("invoice_address_id")
    def deliveryAddressId = column[Option[Long]]("delivery_address_id")
    def * = (id.?, firstName, lastName, salutation, email, directPhone, mobilePhone, source, position, isMainContact, twitter, facebook, linkedIn, skype, active, repId, companyId, invoiceAddressId, deliveryAddressId) <> (Customer.tupled, Customer.unapply _)
  }
}

@Singleton()
class CustomerSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, companyRepository: CompanySlickRepository, userRepository: UserSlickRepository, addressRepository: AddressSlickRepository)(implicit executionContext: ExecutionContext)
  extends CustomersComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val customers = TableQuery[Customers]

  def all: Future[List[Customer]] = db.run(customers.to[List].result)

  def allWithCompanyAndRep = {
    val query = for {
      ((((customer, company), rep), invoiceAddress), deliveryAddress) <-
        customers join
        companyRepository.companies on(_.companyId === _.id) join
        userRepository.users on(_._1.repId === _.id) joinLeft
        addressRepository.addresses on(_._1._1.invoiceAddressId === _.id) joinLeft
          addressRepository.addresses on(_._1._1._1.deliveryAddressId === _.id)
    } yield (customer, company, rep, invoiceAddress, deliveryAddress)

    db.run(query.to[List].result.map(l => l.map (t => CustomerRecord(t._1, t._2, t._3, t._4, t._5))))
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

  def delete(id: Long): Future[Unit] = db.run(customers.filter(_.id === id).delete).map(_ => ())

}
