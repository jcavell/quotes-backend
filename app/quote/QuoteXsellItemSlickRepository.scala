package quote

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait QuoteXsellItemsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class QuoteXsellItems(tag: Tag) extends Table[QuoteXsellItem](tag, "quote_xsell_item") {
    def productId = column[Long]("product_id")
    def quoteId = column[Long]("quote_id")

    def * = (productId, quoteId) <> (QuoteXsellItem.tupled, QuoteXsellItem.unapply _)
  }

}

@Singleton()
class QuoteXsellItemSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuoteXsellItemsComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quoteXsellItems = TableQuery[QuoteXsellItems]

  def all: Future[List[QuoteXsellItem]] = db.run(quoteXsellItems.to[List].result)

  def insert(quoteXsellItem: QuoteXsellItem): Future[QuoteXsellItem] = {
    val action = quoteXsellItems returning quoteXsellItems += quoteXsellItem

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quoteXsellItem
        case Failure(e) => throw e
      }
    }
  }
  def findByQuoteId(quoteId: Long):Future[List[QuoteXsellItem]] = db.run(quoteXsellItems.filter(_.quoteId === quoteId).to[List].result)

  def delete(productId: Long): Future[Unit] = db.run(quoteXsellItems.filter(_.productId === productId).delete).map(_ => ())
}
