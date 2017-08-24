package product

import javax.inject.Inject

import anorm.SqlParser.get
import anorm.{Column, MetaDataItem, TypeDoesNotMatch, ~}
import db.DatabaseExecutionContext
import org.postgresql.util.PGobject
import play.api.db.DBApi
import play.api.libs.json.{JsValue, Json}

@javax.inject.Singleton
class ASIProductRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

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
    * Parse an ASIProduct from a ResultSet
    */
  val simple = {
    get[Option[Int]]("product.internal_id") ~
      get[JsValue]("product.raw_data") ~
      get[Long]("product.Id") ~
      get[String]("product.Name") ~
      get[String]("product.Description") map {
      case internalId ~ rawData ~ id ~ name ~ description =>
        ASIProduct(internalId, rawData, id, name, description)
    }
  }
}
