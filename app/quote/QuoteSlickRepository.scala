package quote

import javax.inject.{Inject, Singleton}

import address.{AddressCreator, AddressSlickRepository}
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import company.CompanySlickRepository
import customer.{CustomerRecord, CustomerSlickRepository}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import user.UserSlickRepository

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
    def customerId = column[Long]("customer_id")
    def enquiryId = column[Option[Long]]("enquiry_id")
    def invoiceAddressId = column[Option[Long]]("invoice_address_id")
    def deliveryAddressId = column[Option[Long]]("delivery_address_id")

    def repEmail = column[String]("rep_email")
    def repId = column[Option[Long]]("rep_id")

    // common
    def createdDate = column[DateTime]("created_date")
    def notes = column[Option[String]]("notes")
    def active = column[Boolean]("active")

    def * = (id.?, title, requiredDate, specialInstructions, enquiryId, customerId, invoiceAddressId, deliveryAddressId, repEmail, repId, createdDate, notes, active) <> (Quote.tupled, Quote.unapply _)
  }
}

@Singleton()
class QuoteSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, quoteMetaSlickRepository: QuoteMetaSlickRepository, customerSlickRepository: CustomerSlickRepository, companySlickRepository: CompanySlickRepository, userSlickRepository: UserSlickRepository, addressSlickRepository: AddressSlickRepository)(implicit executionContext: ExecutionContext)
  extends QuotesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quotes = TableQuery[Quotes]

  def all: Future[List[Quote]] = db.run(quotes.to[List].result)

  def getQuoteRecord(quoteId: Long) = getQuoteRecords(Some(quoteId)) map (_.headOption)

  def getQuoteRecords(maybeQuoteId: Option[Long] = None) = {
    val query =
    for {
      ((((((quote, quoteMeta), customer), company), rep), invoiceAddress), deliveryAddress) <-
      quotes join // t1.t1
        quoteMetaSlickRepository.quoteMetas on (_.id === _.quoteId) join  // t1.t2
        customerSlickRepository.customers on (_._1.customerId === _.id) join  // t2
        companySlickRepository.companies on (_._2.companyId === _.id) joinLeft  // t3
        userSlickRepository.users on (_._1._1._1.repId === _.id) joinLeft  // t4
        userSlickRepository.users on (_._1._1._1._2.assignedUserId === _.id) joinLeft  // t5
        addressSlickRepository.addresses on (_._1._1._1._1._1.invoiceAddressId === _.id) joinLeft  // t6
        addressSlickRepository.addresses on (_._1._1._1._1._1._1.deliveryAddressId === _.id)  // t7
    } yield (quote, quoteMeta, customer, company, rep, invoiceAddress, deliveryAddress)

    val queryWithFilter = if(maybeQuoteId.isDefined) query.filter(_._1._1.id === maybeQuoteId.get) else query
    db.run(queryWithFilter.
      to[List].result.
      map(l => l.map (t =>
        QuoteRecord(t._1._1, t._1._2, CustomerRecord(t._2, t._3, AddressCreator.getOrDefaultAddress(t._6, "Invoice address", t._3.name), AddressCreator.getOrDefaultAddress(t._7, "Delivery address", t._3.name)), None, None, t._4, t._5))))
  }

  def get(quoteId: Long):Future[Option[Quote]] = db.run(quotes.filter(_.id === quoteId).result.headOption)

  def insert(quote: Quote): Future[Quote] = {
    val action = quotes returning quotes.map {_.id} += quote

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
