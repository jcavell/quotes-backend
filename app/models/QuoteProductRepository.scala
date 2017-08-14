package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class QuoteProduct(id: Option[Long] = None, productId: Long, name: String, cost: BigDecimal, currencyCode: String)

@javax.inject.Singleton
class QuoteProductRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
   * Parse a QuoteProduct from a ResultSet
   */
  private[models] val simple = {
    get[Option[Long]]("quote_product.id") ~
    get[Long]("quote_product.product_id") ~
      get[String]("quote_product.name") ~
      get[BigDecimal]("quote_product.cost") ~
      get[String]("quote_product.currency_code") map {
      case id~productId~name~cost~currencyCode => QuoteProduct(id, productId, name, cost, currencyCode)
    }
  }
}


