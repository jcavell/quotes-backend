package xsell

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import play.api.Logger

case class Xsell(id: Option[Long] = None, productId: Long)

trait XsellComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Xsells(tag: Tag) extends Table[Xsell](tag, "xsell") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def productId = column[Long]("product_id")

    def * = (id, productId) <> (Xsell.tupled, Xsell.unapply _)
  }

}

@Singleton()
class XsellSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends XsellComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val xsells = TableQuery[Xsells]

  def all: Future[List[Xsell]] = {
    db.run(xsells.to[List].result)
  }

  def insert(xsell: Xsell): Future[Xsell] = {
    val action = xsells returning xsells.map {_.id} += xsell

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => xsell.copy(id = r)
        case Failure(e) => {
          Logger.error(s"SQL Error inserting ${xsell}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(id: Long, xsell: Xsell): Future[Unit] = {
    val xsellToUpdate: Xsell = xsell.copy(Some(id))
    db.run(xsells.filter(_.id === id).update(xsellToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run(xsells.filter(_.id === id).delete).map(_ => ())

}
