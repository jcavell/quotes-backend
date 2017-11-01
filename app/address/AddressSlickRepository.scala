package address

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait AddressComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Addresses(tag: Tag) extends Table[Address](tag, "address") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def fao = column[Option[String]]("fao")
    def line1 = column[Option[String]]("line1")
    def line2 = column[Option[String]]("line2")
    def line3 = column[Option[String]]("line3")
    def townCity = column[Option[String]]("towncity")
    def county = column[Option[String]]("county")
    def postcode = column[Option[String]]("postcode")
    def country = column[String]("country")
    def active = column[Boolean]("active")
    def * = (id, name, fao, line1, line2, line3, townCity, county, postcode, country, active) <> (Address.tupled, Address.unapply _)
  }

}

@Singleton()
class AddressSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AddressComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val addresses = TableQuery[Addresses]

  def all: Future[List[Address]] = {
    db.run(addresses.to[List].result)
  }

  def insert(address: Address): Future[Address] = {
    val action = addresses returning addresses.map {_.id} += address

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => address.copy(id = r)
        case Failure(e) => {
          Logger.error(s"SQL Error inserting ${address}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(address: Address): Future[Address] = {
    val action = addresses.filter(_.id === address.id).update(address)

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => address
        case Failure(e) => Logger.error(s"Error updating address: $e"); throw e
      }
    }
  }

  def delete(id: Long): Future[Unit] = db.run(addresses.filter(_.id === id).delete).map(_ => ())

}
