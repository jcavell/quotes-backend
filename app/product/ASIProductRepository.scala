package product

import java.sql.PreparedStatement
import javax.inject.Inject

import anorm.SqlParser.get
import anorm.{Column, MetaDataItem, SQL, ToStatement, TypeDoesNotMatch, ~}
import db.DatabaseExecutionContext
import org.postgresql.util.PGobject
import play.api.db.DBApi
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future

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

  implicit object jsonToStatement extends ToStatement[JsValue] {
    def set(s: PreparedStatement, i: Int, json: JsValue): Unit = {
      val jsonObject = new org.postgresql.util.PGobject()
      jsonObject.setType("json")
      jsonObject.setValue(Json.stringify(json))
      s.setObject(i, jsonObject)
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

  def findById(id: Int): Future[Option[ASIProduct]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from product where internal_id = $id").as(simple.singleOpt)
    }
  }(ec)


  def insert(product: ASIProduct): Option[Long] = {
    println("Inserting product: " + product)
      db.withConnection { implicit connection =>
        SQL("insert into product (raw_data, Id, Name, Description) values ({rawData}, {Id}, {Name}, {Description})")
          .on(
            'Id -> product.Id,
            'Name -> product.Name,
            'Description -> product.Description,
            'rawData -> product.rawData
          ).executeInsert()
      }
  }
}
