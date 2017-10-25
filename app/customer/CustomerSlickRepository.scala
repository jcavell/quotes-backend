package customer

import javax.inject.{Inject, Singleton}

import address.{Address, AddressSlickRepository}
import company.{Company, CompanySlickRepository}
import db.{Search, Sort}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import play.api.Logger
import slick.model.Column
import user.UserSlickRepository

import scala.collection.mutable.ListBuffer

trait CustomersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Customers(tag: Tag) extends Table[Customer](tag, "customer") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def canonicalName = column[String]("canonical_name")
    def email = column[String]("email")
    def directPhone = column[Option[String]]("direct_phone")
    def mobilePhone = column[Option[String]]("mobile_phone")
    def canonicalMobilePhone = column[Option[String]]("canonical_mobile_phone")
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
    def * = (id.?, name, canonicalName, email, directPhone, mobilePhone, canonicalMobilePhone, source, position, isMainContact, twitter, facebook, linkedIn, skype, active, repId, companyId, invoiceAddressId, deliveryAddressId) <> (Customer.tupled, Customer.unapply _)
  }
}

@Singleton()
class CustomerSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, companyRepository: CompanySlickRepository, userRepository: UserSlickRepository, addressRepository: AddressSlickRepository)(implicit executionContext: ExecutionContext)
  extends CustomersComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val customers = TableQuery[Customers]

  def all: Future[List[Customer]] = db.run(customers.to[List].result)

  def getCustomerRecord(customerId: Long) = getCustomerRecords(
      Search(Some("customerId"), Some(customerId.toString)),
      Sort()
  ) map (_.headOption)

  def getCustomerRecords(search: Search, sort: Sort) = {
    val query = for {
      (((customer, company), invoiceAddress), deliveryAddress) <-
      customers join
        companyRepository.companies on (_.companyId === _.id) joinLeft
        // userRepository.users on(_._1.repId === _.id) joinLeft
        addressRepository.addresses on (_._1.invoiceAddressId === _.id) joinLeft
        addressRepository.addresses on (_._1._1.deliveryAddressId === _.id)
    } yield (customer, company, invoiceAddress, deliveryAddress)

    val queryList = query.to[List]
    val queryWithSearch =
      if (search.containsSearchTerm) {
        queryList.filter(t =>
          List(
            search.getSearchValueAsLong("id").map(id => t._1.id === id),
            search.getSearchValueAsLong("companyId").map(companyId => t._1.companyId === companyId),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseName).map(multi => t._1.canonicalName like s"%${multi.toLowerCase}%"),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseEmail).map(multi => t._1.email like s"%${multi.toLowerCase}%")
        ).collect(
          { case Some(criteria) => criteria }).
          reduceLeft(_ || _)
        )
      }
      else queryList

    db.run(queryWithSearch.sortBy(t =>
      if (sort.hasOrderDesc("name")) t._1.canonicalName.desc
      else if (sort.hasOrderAsc("email")) t._1.email.asc
      else if (sort.hasOrderDesc("email")) t._1.email.desc
      else t._1.name.asc
    ).
      result.map(l => l.map(t => CustomerRecord(t._1, t._2, t._3, t._4))))
 }


  def getCount(search: Search) = {
    val query = for {
      (customer, company) <- customers join
        companyRepository.companies on (_.companyId === _.id)
    } yield (customer, company)

    val queryList = query.to[List]
    val queryWithSearch =
      if (search.containsSearchTerm) {
        queryList.filter(t =>
          List(
            search.getSearchValueAsLong("id").map(id => t._1.id === id),
            search.getSearchValueAsLong("companyId").map(companyId => t._1.companyId === companyId),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseName).map(multi => t._1.canonicalName like s"%${multi.toLowerCase}%"),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseEmail).map(multi => t._1.email like s"%${multi.toLowerCase}%")
          ).collect(
            { case Some(criteria) => criteria }).
            reduceLeft(_ || _)
        )
      }
      else queryList

    db.run(queryWithSearch.result.map(l => l.length))
  }

  def insert(cust: Customer): Future[Customer] = {

    val customer = cust.copyWithCanonicalFields

    val action = customers returning customers.map {_.id} += customer

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => customer.copy(id = Some(r))
        case Failure(e) => Logger.error(s"Error inserting customer: $e"); throw e
      }
    }
  }

  def findByCompanyAndEmail(companyId: Long, email: String):Future[Option[Customer]] = db.run(customers.filter(cust => cust.email === email && cust.companyId === companyId).result.headOption)

  def update(cust: Customer): Future[Customer] = {
    val customer = cust.copyWithCanonicalFields
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
