package models

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class XSell(id: Option[Long] = None, productId: Long)


@javax.inject.Singleton
class XsellRepository @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  // -- Parsers

  /**
    * Parse an XSell from a ResultSet
    */
   val simple = {
    get[Option[Long]]("xsell.id") ~
      get[Long]("xsell.product_id")  map {
      case id ~ productId  => XSell(id, productId)
    }
  }


  /**
    * Return a List of xSells
    *
    */
  def list(): Future[List[XSell]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from xsell").as(simple *)
    }
  }(ec)



  /**
    * Update an xSell
    *
    * @param id     The xSell id
    * @param xSell The xSell values.
    */
  def update(id: Long, xSell: XSell) = Future {
    db.withConnection { implicit connection =>
      SQL(
        s"""
          update xsell
          set product_id = {$xSell.productId}
          where id = {$id}
        """
      ).executeUpdate()
    }
  }(ec)

  /**
    * Insert a new xSell
    *
    * @param xSell The xSell values
    */
  def insert(xSell: XSell) = Future {
    db.withConnection { implicit connection =>
      SQL(
        s"""
          insert into xsell values (
            (select next value for xsell_seq),
            ${xSell.productId}
          )
        """
      ).executeUpdate()
    }
  }(ec)

  /**
    * Delete an xSell
    *
    * @param id Id of the xSell to delete.
    */
  def delete(id: Long) = Future {
    db.withConnection { implicit connection =>
      SQL("delete from xsell where id = {$id}").executeUpdate()
    }
  }(ec)

}
