package quote

import javax.inject.{Inject, Singleton}

import address.AddressSlickRepository
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait QuotesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Quotes(tag: Tag) extends Table[Quote](tag, "quote") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def dateRequired = column[DateTime]("date_required")
    def customerName = column[String]("customer_name")
    def customerEmail = column[String]("customer_email")
    def specialInstructions = column[Option[String]]("special_instructions")
    def invoiceAddressId = column[Option[Long]]("invoice_address_id")
    def deliveryAddressId = column[Option[Long]]("delivery_address_id")
    def customerId = column[Long]("customer_id")
    def repId = column[Long]("rep_id")
    def enquiryId = column[Option[Long]]("enquiry_id")

    // common
    def createdDate = column[DateTime]("created_date")
    def notes = column[Option[String]]("notes")
    def active = column[Boolean]("active")

    def * = (id.?, title, dateRequired, customerName, customerEmail, specialInstructions, invoiceAddressId, deliveryAddressId, customerId, repId, enquiryId, createdDate, notes, active) <> (Quote.tupled, Quote.unapply _)
  }
}

@Singleton()
class QuoteSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, quoteMetaSlickRepository: QuoteMetaSlickRepository, addressSlickRepository: AddressSlickRepository)(implicit executionContext: ExecutionContext)
  extends QuotesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quotes = TableQuery[Quotes]

  def all: Future[List[Quote]] = db.run(quotes.to[List].result)

  def getQuoteRecord(quoteId: Long) = getQuoteRecords(Some(quoteId)) map (_.headOption)

  def getQuoteRecords(maybeQuoteId: Option[Long] = None) = {
    val query =
    for {
      (((quote, quoteMeta), invoiceAddress), deliveryAddress) <-
      quotes join
        quoteMetaSlickRepository.quoteMetas on (_.id === _.quoteId) joinLeft
        addressSlickRepository.addresses on (_._1.invoiceAddressId === _.id) joinLeft
        addressSlickRepository.addresses on (_._1._1.deliveryAddressId === _.id)
    } yield (quote, quoteMeta, invoiceAddress, deliveryAddress)

    maybeQuoteId match {
      case Some(quoteId) => db.run(query.filter(_._1.id === quoteId).result.map(l => l.map (t => QuoteRecord(t._1, t._2, t._3, t._4))))
      case None => db.run(query.to[List].result.map(l => l.map (t => QuoteRecord(t._1, t._2, t._3, t._4))))
    }
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
