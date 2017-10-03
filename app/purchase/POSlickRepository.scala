package purchase

import javax.inject.{Inject, Singleton}

import address.AddressSlickRepository
import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import customer.{CompanySlickRepository, CustomerSlickRepository}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import quote.{QuoteRecord, QuoteSlickRepository}
import slick.jdbc.JdbcProfile
import supplier.{ContactSlickRepository, SupplierSlickRepository}
import user.UserSlickRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait POsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class POs(tag: Tag) extends Table[PO](tag, "po") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def poSentDate = column[Option[DateTime]]("po_sent_date")

    def purchaseTitle = column[Option[String]]("purchase_title")

    def supplierReference = column[Option[String]]("supplier_reference")

    def dateRequired = column[DateTime]("date_required")

    def invoiceReceived = column[Boolean]("invoice_received")

    def supplierAddressId = column[Option[Long]]("supplier_address_id")

    def deliveryAddressId = column [Option[Long]]("delivery_address_id")

    def quoteId = column[Long]("quote_id")

    def supplierId = column[Long]("supplier_id")

    def contactId = column[Long]("contact_id")

    def repId = column[Long]("rep_id")

    // common
    def createdDate = column[DateTime]("created_date")

    def notes = column[Option[String]]("notes")

    def active = column[Boolean]("active")

    def * = (id.?, poSentDate, purchaseTitle, supplierReference, dateRequired, invoiceReceived, supplierAddressId, deliveryAddressId, quoteId, supplierId, contactId, repId, createdDate, notes, active) <> (PO.tupled, PO.unapply _)
  }

}

@Singleton()
class POSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, quoteSlickRepository: QuoteSlickRepository, userSlickRepository: UserSlickRepository, addressSlickRepository: AddressSlickRepository, supplierSlickRepository: SupplierSlickRepository, contactSlickRepository: ContactSlickRepository)(implicit executionContext: ExecutionContext)
  extends POsComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val pos = TableQuery[POs]

  def all: Future[List[PO]] = db.run(pos.to[List].result)

  def getPORecord(quoteId: Long) = getPORecords(Some(quoteId)) map (_.headOption)

  def getPORecords(maybePOId: Option[Long] = None) = {
    val query =
      for {
        ((((((po, quote), supplier), contact), rep), supplierAddress), deliveryAddress) <-
        pos join // 1
          quoteSlickRepository.quotes on (_.quoteId === _.id) join //2
          supplierSlickRepository.suppliers on(_._1.supplierId === _.id) join //3
      contactSlickRepository.contacts on (_._1._1.contactId === _.id) join //4
          userSlickRepository.users on (_._1._1._1.repId === _.id) joinLeft //5
          addressSlickRepository.addresses on (_._1._1._1._1.supplierAddressId === _.id) joinLeft //6
          addressSlickRepository.addresses on (_._1._1._1._1._1.deliveryAddressId === _.id) //7
      } yield (po, quote, supplier, contact, rep, supplierAddress, deliveryAddress)


    db.run(query.to[List].result.map(l => l.map (t => PORecord(t._1, t._2, t._3, t._4, t._5, t._6, t._7))))
  }

  def get(quoteId: Long): Future[Option[PO]] = db.run(pos.filter(_.id === quoteId).result.headOption)

  def insert(quote: PO): Future[PO] = {
    val action = pos returning pos.map {
      _.id
    } += quote

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quote.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }


  def update(id: Long, quote: PO): Future[PO] = {
    val quoteToUpdate: PO = quote.copy(Some(id))
    db.run(pos.filter(_.id === id).update(quoteToUpdate)).map(_ => (quote))
  }

  def delete(id: Long): Future[Unit] = db.run(pos.filter(_.id === id).delete).map(_ => ())

}
