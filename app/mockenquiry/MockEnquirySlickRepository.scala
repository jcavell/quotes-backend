package mockenquiry

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import db.PgProfileWithAddons.api._
import enquiry.Enquiry

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait MockEnquiryComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class MockEnquiries(tag: Tag) extends Table[Enquiry](tag, "mock_enquiry") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def enquiryId = column[Long]("enquiry_id")
    def enquiryTimestamp = column[DateTime]("enquiry_timestamp")
    def internalProductId = column[Long]("internal_product_id")
    def productId = column[String]("product_id")
    def productName = column[String]("product_name")
    def brand = column[Option[String]]("brand")
    def colour = column[Option[String]]("colour")
    def customerName = column[String]("customer_name")
    def customerTelephone = column[String]("customer_telephone")
    def customerEmail= column[String]("customer_email")
    def company= column[String]("company")
    def dateRequired = column[DateTime]("date_required")
    def quantity= column[Int]("quantity")
    def repId = column[Int]("rep_id")
    def repEmail = column[String]("rep_email")
    def source = column[Option[String]]("source")
    def subject = column[Option[String]]("subject")
    def xsellProductIds = column[List[Long]]("xsell_product_ids")
    def otherRequirements= column[Option[String]]("other_requirements")
    def imported = column[Boolean]("imported")

    def * = (id, enquiryId, enquiryTimestamp, internalProductId, productId, productName, brand, colour, customerName, customerTelephone, customerEmail, company, dateRequired, quantity, repId, repEmail, source, subject, xsellProductIds, otherRequirements, imported) <> (Enquiry.tupled, Enquiry.unapply _)
  }

}

@Singleton()
class MockEnquirySlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends MockEnquiryComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val enquiries = TableQuery[MockEnquiries]

  def all: Future[List[Enquiry]] = db.run(enquiries.to[List].result)

  def allUnimported: Future[List[Enquiry]] = db.run(enquiries.filter(_.imported ===  false).to[List].result)


  def insert(enquiry: Enquiry): Future[Enquiry] = {
    val action = enquiries returning enquiries.map {_.id} += enquiry

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => enquiry.copy(id = r)
        case Failure(e) => {
          println(s"SQL Error inserting ${enquiry}, ${e.getMessage}")
          throw e
        }
      }
    }
  }

  def flagMockEnquiryImported(enquiryId: Long) = {
    val q = for { e <- enquiries if e.enquiryId === enquiryId } yield e.imported
    db.run(q.update(true))

  }

  def update(enquiry: Enquiry): Future[Enquiry] = {
    val mockQuoteRequestToUpdate: Enquiry = enquiry.copy(enquiry.id)
    db.run(enquiries.filter(_.id === enquiry.id).update(mockQuoteRequestToUpdate)).map(_ => (enquiry))
  }

  def delete(id: Long): Future[Unit] = db.run(enquiries.filter(_.id === id).delete).map(_ => ())

}
