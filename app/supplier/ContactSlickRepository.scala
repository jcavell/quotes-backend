package supplier

import javax.inject.{Inject, Singleton}

import address.AddressSlickRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import play.api.Logger
import user.UserSlickRepository

trait ContactsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Contacts(tag: Tag) extends Table[Contact](tag, "contact") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def directPhone = column[Option[String]]("direct_phone")
    def mobilePhone = column[Option[String]]("mobile_phone")
    def position = column[Option[String]]("position")
    def isMainContact = column[Boolean]("is_main_contact")
    def supplierId = column[Long]("supplier_id")
    def repId = column[Option[Long]]("rep_id")
    def POAddressId = column[Option[Long]]("po_address_id")
    def active = column[Boolean]("active")
    def * = (id.?, name, email, directPhone, mobilePhone, position, isMainContact,  supplierId, repId, POAddressId, active) <> (Contact.tupled, Contact.unapply _)
  }
}

@Singleton()
class ContactSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, supplierRepository: SupplierSlickRepository, userRepository: UserSlickRepository, addressRepository: AddressSlickRepository)(implicit executionContext: ExecutionContext)
  extends ContactsComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val contacts = TableQuery[Contacts]

  def findBySupplierId(supplierId: Long):Future[Seq[Contact]] = db.run(contacts.filter(_.supplierId === supplierId).result)

  def all: Future[List[Contact]] = db.run(contacts.to[List].result)

  def getContactRecords = {
    val query = for {
      (((contact, supplier), rep), poAddress) <-
        contacts join
        supplierRepository.suppliers on(_.supplierId === _.id) joinLeft
        userRepository.users on(_._1.repId === _.id) joinLeft
        addressRepository.addresses on(_._1._1.POAddressId === _.id)
    } yield (contact, supplier, rep, poAddress)

    db.run(query.to[List].result.map(l => l.map (t => ContactRecord(t._1, t._2, t._3, t._4))))
  }

  def insert(contact: Contact): Future[Contact] = {
    val action = contacts returning contacts.map {_.id} += contact

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => contact.copy(id = Some(r))
        case Failure(e) => Logger.error(s"Error inserting contact: $e"); throw e
      }
    }
  }

  def findByEmail(email: String):Future[Option[Contact]] = db.run(contacts.filter(_.email === email).result.headOption)

  def update(contact: Contact): Future[Contact] = {
    val action = contacts.filter(_.id === contact.id).update(contact)

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => contact
        case Failure(e) => Logger.error(s"Error updating contact: $e"); throw e
      }
    }
  }

  def delete(id: Long): Future[Unit] = db.run(contacts.filter(_.id === id).delete).map(_ => ())

}
