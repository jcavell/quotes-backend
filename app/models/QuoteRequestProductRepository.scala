package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class QuoteRequestProduct(id: Option[Long] = None, productId: Long, name: String, cost: BigDecimal, currencyCode: String)

@javax.inject.Singleton
class QuoteRequestProductRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
   * Parse a QuoteRequestProduct from a ResultSet
   */
  private[models] val simple = {
    get[Option[Long]]("quote_request_product.id") ~
    get[Long]("quote_request_product.product_id") ~
      get[String]("quote_request_product.name") ~
      get[BigDecimal]("quote_request_product.cost") ~
      get[String]("quote_request_product.currency_code") map {
      case id~productId~name~cost~currencyCode => QuoteRequestProduct(id, productId, name, cost, currencyCode)
    }
  }
}


