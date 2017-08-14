package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

case class Product(id: Option[Long] = None, productId: Long, name: String, cost: BigDecimal, currencyCode: String)

@javax.inject.Singleton
class ProductRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
   * Parse a Product from a ResultSet
   */
  private[models] val simple = {
    get[Option[Long]]("product.id") ~
    get[Long]("product.product_id") ~
      get[String]("product.name") ~
      get[BigDecimal]("product.cost") ~
      get[String]("product.currency_code") map {
      case id~productId~name~cost~currencyCode => Product(id, productId, name, cost, currencyCode)
    }
  }
}


