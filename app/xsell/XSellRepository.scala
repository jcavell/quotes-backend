package xsell

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class Xsell(id: Int, productId: Long)

trait XsellsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Xsells(tag: Tag) extends Table[Xsell](tag, "xsell") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def productId = column[Long]("product_id")

    def * = (id, productId) <> (Xsell.tupled, Xsell.unapply _)
  }

}

@Singleton()
class XsellsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends XsellsComponent
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
          println(s"SQL Error inserting ${xsell}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(id: Int, xsell: Xsell): Future[Unit] = {
    val xsellToUpdate: Xsell = xsell.copy(id)
    db.run(xsells.filter(_.id === id).update(xsellToUpdate)).map(_ => ())
  }

  def delete(id: Int): Future[Unit] = db.run(xsells.filter(_.id === id).delete).map(_ => ())

}
