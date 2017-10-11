package quote

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait QuotesLineItemComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class QuoteLineItems(tag: Tag) extends Table[QuoteLineItem](tag, "quote_line_item") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def productId = column[String]("product_id")
    def quantity = column[Int]("quantity")
    def colour = column[Option[String]]("colour")
    def description = column[Option[String]]("description")
    def priceIncludes = column[Option[String]]("price_includes")
    def cost = column[BigDecimal]("cost")
    def markup = column[BigDecimal]("markup")
    def sell = column[BigDecimal]("sell")
    def vat = column[BigDecimal]("vat")
    def quoteId = column[Long]("quote_id")
    def supplierId = column[Long]("supplier_id")

    def * = (id.?, productId, quantity, colour, description, priceIncludes, cost, markup, sell, vat, quoteId, supplierId) <> (QuoteLineItem.tupled, QuoteLineItem.unapply _)
  }

}

@Singleton()
class QuoteLineItemSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuotesLineItemComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quoteLineItems = TableQuery[QuoteLineItems]

  def all: Future[List[QuoteLineItem]] = db.run(quoteLineItems.to[List].result)

  def insert(quoteLineItem: QuoteLineItem): Future[QuoteLineItem] = {
    val action = quoteLineItems returning quoteLineItems.map {_.id} += quoteLineItem

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quoteLineItem.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }
  def findByQuoteId(quoteId: Long):Future[List[QuoteLineItem]] = db.run(quoteLineItems.filter(_.quoteId === quoteId).to[List].result)

  def update(id: Long, quoteLineItem: QuoteLineItem): Future[QuoteLineItem] = {
    val quoteLineItemToUpdate: QuoteLineItem = quoteLineItem.copy(Some(id))
    db.run(quoteLineItems.filter(_.id === id).update(quoteLineItemToUpdate)).map(_ => (quoteLineItem))
  }

  def delete(id: Long): Future[Unit] = db.run(quoteLineItems.filter(_.id === id).delete).map(_ => ())

}
