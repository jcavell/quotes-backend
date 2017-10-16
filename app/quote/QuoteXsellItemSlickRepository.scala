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
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def productId = column[Long]("product_id")
    def quoteId = column[Long]("quote_id")

    def * = (id.?, productId, quoteId) <> (QuoteXsellItem.tupled, QuoteXsellItem.unapply _)
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
    val action = quoteXsellItems returning quoteXsellItems.map {_.id} += quoteXsellItem

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quoteXsellItem.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }
  def findByQuoteId(quoteId: Long):Future[List[QuoteXsellItem]] = db.run(quoteXsellItems.filter(_.quoteId === quoteId).to[List].result)

  def delete(id: Long): Future[Unit] = db.run(quoteXsellItems.filter(_.id === id).delete).map(_ => ())
}
