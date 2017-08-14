package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import org.postgresql.util.PGobject
import play.api.db.DBApi
import anorm.Column
import play.api.libs.json._

case class Product(id: Option[Long] = None, productId: Long, name: String, cost: BigDecimal, currencyCode: String, data: JsValue)

@javax.inject.Singleton
class ProductRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  implicit def pgObjectColumnToJSValue: Column[JsValue] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta

      value match {
        case pgO: PGobject => Right(Json.parse(pgO.toString()));
        case _ => Left(TypeDoesNotMatch("Can't convert column"))
      }
    }

  private val db = dbapi.database("default")

  /**
    * Parse a Product from a ResultSet
    */
  private[models] val simple = {
    get[Option[Long]]("product.id") ~
      get[Long]("product.product_id") ~
      get[String]("product.name") ~
      get[BigDecimal]("product.cost") ~
      get[String]("product.currency_code") ~
      get[JsValue]("product.data") map {
      case id ~ productId ~ name ~ cost ~ currencyCode ~ data => Product(id, productId, name, cost, currencyCode, data)
    }
  }
}


