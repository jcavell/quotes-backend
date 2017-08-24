package product

import javax.inject.Inject

import anorm.SqlParser.get
import anorm.{Column, MetaDataItem, TypeDoesNotMatch, ~}
import db.DatabaseExecutionContext
import org.postgresql.util.PGobject
import play.api.db.DBApi
import play.api.libs.json.{JsValue, Json}

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
  val simple = {
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
