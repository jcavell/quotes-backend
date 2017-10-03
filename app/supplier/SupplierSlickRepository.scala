package supplier

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait SuppliersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Suppliers(tag: Tag) extends Table[Supplier](tag, "supplier") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def active = column[Boolean]("active")

    def * = (id.?, name, active) <> (Supplier.tupled, Supplier.unapply _)
  }

}
@Singleton()
class SupplierSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends SuppliersComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val suppliers = TableQuery[Suppliers]

  def all: Future[List[Supplier]] = db.run(suppliers.to[List].result)

  def insert(supplier: Supplier): Future[Supplier] = {
    val action = suppliers returning suppliers.map {_.id} += supplier

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => supplier.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }

  def findByName(name: String):Future[Option[Supplier]] = db.run(suppliers.filter(_.name === name).result.headOption)

  def update(id: Long, supplier: Supplier): Future[Supplier] = {
    val supplierToUpdate: Supplier = supplier.copy(Some(id))
    db.run(suppliers.filter(_.id === id).update(supplierToUpdate)).map(_ => (supplier))
  }

  def delete(id: Long): Future[Unit] = db.run(suppliers.filter(_.id === id).delete).map(_ => ())

}
