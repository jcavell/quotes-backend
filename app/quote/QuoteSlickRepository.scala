package quote

import javax.inject.{Inject, Singleton}

import address.{Address, AddressCreator, AddressSlickRepository}
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import company.{Company, CompanySlickRepository}
import customer.{Customer, CustomerCanonicaliser, CustomerRecord, CustomerSlickRepository}
import db.Search
import enquiry.{Enquiry, EnquirySlickRepository}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import user.{User, UserSlickRepository}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait QuotesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Quotes(tag: Tag) extends Table[Quote](tag, "quote") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def requiredDate = column[DateTime]("required_date")

    def specialInstructions = column[Option[String]]("special_instructions")

    // Quote relations
    def customerId = column[Option[Long]]("customer_id")

    def enquiryId = column[Option[Long]]("enquiry_id")

    def repEmail = column[String]("rep_email")

    def repId = column[Option[Long]]("rep_id")

    // common
    def createdDate = column[DateTime]("created_date")

    def notes = column[Option[String]]("notes")

    def active = column[Boolean]("active")

    def * = (id.?, title, requiredDate, specialInstructions, enquiryId, customerId, repEmail, repId, createdDate, notes, active) <> (Quote.tupled, Quote.unapply _)
  }

}

@Singleton()
class QuoteSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, protected val quoteMetaSlickRepository: QuoteMetaSlickRepository, protected val customerSlickRepository: CustomerSlickRepository, protected val companySlickRepository: CompanySlickRepository, protected val userSlickRepository: UserSlickRepository, protected val addressSlickRepository: AddressSlickRepository, protected val enquirySlickRepository: EnquirySlickRepository)(implicit executionContext: ExecutionContext)
  extends QuotesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quotes = TableQuery[Quotes]

  def all: Future[List[Quote]] = db.run(quotes.to[List].result)

   def getQuoteRecord(quoteId: Long) = {
     val queryWithFilter = getQuoteQuery().filter(_._1.id === quoteId)
     runQueryAndMapResults(queryWithFilter) map (_.headOption)
   }

  def getQuoteQuery() = {
    val query =
      for {
        ((((((((quote, quoteMeta), customer), company), assignedUser), rep), enquiry), invoiceAddress), deliveryAddress) <-
        quotes join // t1
          quoteMetaSlickRepository.quoteMetas on (_.id === _.quoteId) joinLeft // t2
          customerSlickRepository.customers on (_._1.customerId === _.id) joinLeft // t3
          companySlickRepository.companies on (_._2.map(_.companyId) === _.id) joinLeft // t4
          enquirySlickRepository.enquiries on (_._1._1._1.enquiryId === _.id) joinLeft // t5
          userSlickRepository.users on (_._1._1._1._2.assignedUserId === _.id) joinLeft // t6
          userSlickRepository.users on (_._1._1._1._1._1.repId === _.id) joinLeft // t7
          addressSlickRepository.addresses on (_._1._1._1._1._2.flatMap(_.invoiceAddressId) === _.id) joinLeft // t8
          addressSlickRepository.addresses on (_._1._1._1._1._1._2.flatMap(_.deliveryAddressId) === _.id) // t9
      } yield (quote, quoteMeta, customer, company, assignedUser, rep, enquiry, invoiceAddress, deliveryAddress)
    query
  }

  def getQuoteQueryWithSearch(search: Search) = {
    val query = getQuoteQuery()
    val queryWithSearch =
      if (search.containsSearchTerm) {
        query.filter(t =>
          List(
         //   search.getSearchValueAsLong("id").map(id => t._1._1.id === id),
    //        search.getSearchValueAsLong("customerId").map(customerId => t._1._1.customerId === customerId),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseName).map(multi => t._3.map(_.canonicalName) like s"%${multi.toLowerCase}%"),
            search.getSearchValue("multi", CustomerCanonicaliser.canonicaliseEmail).map(multi => t._3.map(_.email) like s"%${multi.toLowerCase}%")
          ).collect(
            { case Some(criteria) => criteria }).
            reduceLeft(_ || _)
        )
      }
      else query

    queryWithSearch
  }

  def getCount(search: Search) = {
    val query = getQuoteQueryWithSearch(search)
    db.run(query.result.map(l => l.length))
  }



  def getQuoteRecords(search: Search) = runQueryAndMapResults(getQuoteQueryWithSearch(search))


  def runQueryAndMapResults(query:Query[(QuoteSlickRepository.this.Quotes, QuoteSlickRepository.this.quoteMetaSlickRepository.QuoteMetas, Rep[Option[QuoteSlickRepository.this.customerSlickRepository.Customers]], Rep[Option[QuoteSlickRepository.this.companySlickRepository.Companies]], Rep[Option[QuoteSlickRepository.this.enquirySlickRepository.Enquiries]], Rep[Option[QuoteSlickRepository.this.userSlickRepository.Users]], Rep[Option[QuoteSlickRepository.this.userSlickRepository.Users]], Rep[Option[QuoteSlickRepository.this.addressSlickRepository.Addresses]], Rep[Option[QuoteSlickRepository.this.addressSlickRepository.Addresses]]), (Quote, QuoteMeta, Option[Customer], Option[Company], Option[Enquiry], Option[User], Option[User], Option[Address], Option[Address]), scala.Seq]) = {
    db.run(query.
      to[List].result.
      map(l =>
        l.map { t =>
          val customerRecord = t._3 match { // match on customer
            case Some(customer) =>
              CustomerRecord(customer, t._4.get, AddressCreator.getOrDefaultAddress(t._8, "Invoice address"), AddressCreator.getOrDefaultAddress(t._9, "Delivery address"))
            case None =>  // no customer, attempt to create new Customer from Enquiry (with empty Company)
              t._5 match { // match on Enquiry
                case Some(enquiry) => CustomerRecord(Customer(name = enquiry.customerName, email = enquiry.customerEmail, mobilePhone = Some(enquiry.customerTelephone), companyId = 0, source = enquiry.source), Company(name = enquiry.company))
                case None => CustomerRecord(Customer(name = "", email = "", companyId = 0), Company(name = "")) // no enquiry found, initialise empty customer
              }
          }
          QuoteRecord(t._1, t._2, t._5, customerRecord, None, None, t._6, t._7)
        }
      )
    )
  }

  def get(quoteId: Long): Future[Option[Quote]] = db.run(quotes.filter(_.id === quoteId).result.headOption)

  def insert(quote: Quote): Future[Quote] = {
    val action = quotes returning quotes.map {
      _.id
    } += quote

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quote.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }


  def update(id: Long, quote: Quote): Future[Quote] = {
    val quoteToUpdate: Quote = quote.copy(Some(id))
    db.run(quotes.filter(_.id === id).update(quoteToUpdate)).map(_ => (quote))
  }

  def delete(id: Long): Future[Unit] = db.run(quotes.filter(_.id === id).delete).map(_ => ())

}
