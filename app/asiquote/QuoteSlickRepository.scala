package asiquote

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import play.api.Logger

trait QuotesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Quotes(tag: Tag) extends Table[Quote](tag, "asi_quote") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def status = column[String]("status")
    def requestTimestamp = column[DateTime]("request_timestamp")
    def requestDateRequired = column[DateTime]("request_date_required")
    def requestProductId = column[String]("request_product_id")
    def requestCustomerFirstName = column[String]("request_customer_first_name")
    def requestCustomerLastName = column[String]("request_customer_last_name")
    def requestCustomerEmail = column[String]("request_customer_email")
    def requestCustomerTel = column[String]("request_customer_tel")
    def requestCompany = column[String]("request_company")
    def requestQuantity = column[Int]("request_quantity")
    def requestOtherRequirements = column[Option[String]]("request_other_requirements")
    def customerId = column[Int]("customer_id")
    def * = (id.?, status, requestTimestamp, requestDateRequired, requestProductId, requestCustomerFirstName, requestCustomerLastName, requestCustomerEmail, requestCustomerTel, requestCompany, requestQuantity, requestOtherRequirements, customerId) <> (Quote.tupled, Quote.unapply _)
  }

}

@Singleton()
class QuoteSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuotesComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quotes = TableQuery[Quotes]

  def all: Future[List[Quote]] = db.run(quotes.to[List].result)

  def insert(quote: Quote): Future[Quote] = {
    val action = quotes returning quotes.map {_.id} += quote

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quote.copy(id = Some(r))
        case Failure(e) => Logger.error(s"Error inserting quote: $e"); throw e
      }
    }
  }

  def update(quote: Quote): Future[Quote] = {
    val action = quotes.filter(_.id === quote.id).update(quote)

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quote
        case Failure(e) => Logger.error(s"Error updating quote: $e"); throw e
      }
    }
  }

  def delete(id: Int): Future[Unit] = db.run(quotes.filter(_.id === id).delete).map(_ => ())

}
