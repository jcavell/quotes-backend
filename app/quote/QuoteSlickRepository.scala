package quote

import javax.inject.{Inject, Singleton}

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
    def status = column[String]("status")
    def title = column[String]("title")
    def quoteCreatedDate = column[DateTime]("quote_created_date")
    def quoteSentDate = column[Option[DateTime]]("quote_sent_date")
    def saleSentDate = column[Option[DateTime]]("sale_sent_date")
    def invoiceSentDate = column[Option[DateTime]]("invoice_sent_date")
    def quoteLossReason = column[Option[String]]("quote_loss_reason")
    def dateRequired = column[DateTime]("date_required")
    def customerName = column[String]("customer_name")
    def customerEmail = column[String]("customer_email")
    def paymentTerms = column[Option[String]]("payment_terms")
    def paymentDueDate = column[Option[DateTime]]("payment_due_date")
    def paymentStatus = column[Option[String]]("payment_status")
    def notes = column[Option[String]]("notes")
   // def specialInstructions = column[Option[String]]("special_instructions")
    def invoiceAddressId = column[Option[Long]]("invoice_address_id")
    def deliveryAddressId = column[Option[Long]]("delivery_address_id")
    def customerId = column[Long]("customer_id")
    def repId = column[Long]("rep_id")
    def assignedUserId = column[Option[Long]]("assigned_user_id")
    def enquiryId = column[Option[Long]]("enquiry_id")
    def active = column[Boolean]("active")

    def * = (id.?, status, title, quoteCreatedDate, quoteSentDate, saleSentDate, invoiceSentDate, quoteLossReason, dateRequired, customerName, customerEmail, paymentTerms, paymentDueDate, paymentStatus, notes, invoiceAddressId, deliveryAddressId, customerId, repId, assignedUserId, enquiryId, active) <> (Quote.tupled, Quote.unapply _)
  }
}

@Singleton()
class QuoteSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuotesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quotes = TableQuery[Quotes]

  def all: Future[List[Quote]] = db.run(quotes.to[List].result)

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
