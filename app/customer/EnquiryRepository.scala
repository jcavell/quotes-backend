package customer

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait EnquiryComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Enquiries(tag: Tag) extends Table[Enquiry](tag, "enquiry") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def requestId = column[Long]("request_id")
    def requestTimestamp = column[DateTime]("request_timestamp")
    def productId = column[String]("product_id")
    def customerFirstName = column[String]("customer_first_name")
    def customerLastName = column[String]("customer_last_name")
    def customerTel = column[String]("customer_tel")
    def customerEmail= column[String]("customer_email")
    def company= column[String]("company")
    def dateRequired = column[DateTime]("date_required")
    def quantity= column[Int]("quantity")
    def otherRequirements= column[Option[String]]("other_requirements")
    def imported = column[Boolean]("imported")

    def * = (id, requestId, requestTimestamp, productId, customerFirstName, customerLastName, customerTel, customerEmail, company, dateRequired, quantity, otherRequirements, imported) <> (Enquiry.tupled, Enquiry.unapply _)
  }

}
@Singleton()
class EnquiryRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends EnquiryComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val enquiries = TableQuery[Enquiries]

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


  def update(mockQuoteRequest: Enquiry): Future[Enquiry] = {
    val mockQuoteRequestToUpdate: Enquiry = mockQuoteRequest.copy(mockQuoteRequest.id)
    db.run(enquiries.filter(_.id === mockQuoteRequest.id).update(mockQuoteRequestToUpdate)).map(_ => (mockQuoteRequest))
  }

  def delete(id: Long): Future[Unit] = db.run(enquiries.filter(_.id === id).delete).map(_ => ())

}
