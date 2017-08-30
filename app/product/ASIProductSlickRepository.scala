package product

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.JsValue
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import db.PgProfileWithAddons.api._

import play.api.Logger

trait ASIProductComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class ASIProducts(tag: Tag) extends Table[ASIProduct](tag, "product") {
    def internalId = column[Option[Int]]("internal_id", O.PrimaryKey, O.AutoInc)
    def rawData = column[JsValue]("raw_data")
    def id = column[Long]("id")
    def name = column[String]("name")
    def description = column[String]("description")

    def * = (internalId, rawData, id, name, description) <> (ASIProduct.tupled, ASIProduct.unapply _)
  }

  class ASIQuoteProducts(tag: Tag) extends Table[ASIQuoteProduct](tag, "quote_product") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def quoteId = column[Int]("quote_id")
    def productInternalId = column[Int]("product_internal_id")

    def * = (id, quoteId, productInternalId) <> (ASIQuoteProduct.tupled, ASIQuoteProduct.unapply _)
  }

}

@Singleton()
class ASIProductSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends ASIProductComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val asiProducts = TableQuery[ASIProducts]
  val asiQuoteProducts = TableQuery[ASIQuoteProducts]

  def all: Future[List[ASIProduct]] = {
    db.run(asiProducts.to[List].result)
  }


  def insertQuoteProduct(quoteId: Int, productInternalId: Int): Future[ASIQuoteProduct] = {
    val asiQuoteProduct = ASIQuoteProduct(None, quoteId, productInternalId)
    val action = asiQuoteProducts returning asiQuoteProducts.map {_.id} += asiQuoteProduct

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => asiQuoteProduct.copy(id = r)
        case Failure(e) => {
          Logger.error(s"SQL Error inserting ${asiQuoteProduct}, ${e.getMessage}")
          throw e
        }
      }
    }
  }

  def insert(asiProduct: ASIProduct): Future[ASIProduct] = {
    val action = asiProducts returning asiProducts.map {_.internalId} += asiProduct

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => asiProduct.copy(internalId = r)
        case Failure(e) => {
          Logger.error(s"SQL Error inserting ${asiProduct}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(internalId: Int, asiProduct: ASIProduct): Future[Unit] = {
    val asiProductToUpdate: ASIProduct = asiProduct.copy(Some(internalId))
    db.run(asiProducts.filter(_.internalId === internalId).update(asiProductToUpdate)).map(_ => ())
  }

  def delete(internalId: Int): Future[Unit] = db.run(asiProducts.filter(_.internalId === internalId).delete).map(_ => ())

}
