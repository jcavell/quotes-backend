package quote

import javax.inject.{Inject, Singleton}

import org.joda.time.{DateTime}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import com.github.tototoshi.slick.PostgresJodaSupport._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

case class MockQuoteRequest(id: Option[Int], requestId: Int, requestTimestamp: DateTime, productId: Long, customerName: String, customerTel: String, customerEmail:String, company: String, dateRequired: DateTime, quantity: Int, otherRequirements: Option[String], imported: Boolean = false)

trait MockQuoteRequestComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class MockQuoteRequests(tag: Tag) extends Table[MockQuoteRequest](tag, "mock_quote_request") {
    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def requestId = column[Int]("request_id")
    def requestTimestamp = column[DateTime]("request_timestamp")
    def productId = column[Long]("product_id")
    def customerName = column[String]("customer_name")
    def customerTel = column[String]("customer_tel")
    def customerEmail= column[String]("customer_email")
    def company= column[String]("company")
    def dateRequired = column[DateTime]("date_required")
    def quantity= column[Int]("quantity")
    def otherRequirements= column[Option[String]]("other_requirements")
    def imported = column[Boolean]("imported")

    def * = (id, requestId, requestTimestamp, productId, customerName, customerTel, customerEmail, company, dateRequired, quantity, otherRequirements, imported) <> (MockQuoteRequest.tupled, MockQuoteRequest.unapply _)
  }

}

@Singleton()
class MockQuoteRequestRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends MockQuoteRequestComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val mockQuoteRequests = TableQuery[MockQuoteRequests]

  def all: Future[List[MockQuoteRequest]] = db.run(mockQuoteRequests.to[List].result)

  def allUnimported: Future[List[MockQuoteRequest]] = db.run(mockQuoteRequests.filter(_.imported ===  false).to[List].result)


  def insert(mockQuoteRequest: MockQuoteRequest): Future[MockQuoteRequest] = {
    val action = mockQuoteRequests returning mockQuoteRequests.map {_.id} += mockQuoteRequest

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => mockQuoteRequest.copy(id = r)
        case Failure(e) => {
          println(s"SQL Error inserting ${mockQuoteRequest}, ${e.getMessage}")
          throw e
        }
      }
    }
  }


  def update(mockQuoteRequest: MockQuoteRequest): Future[MockQuoteRequest] = {
    val mockQuoteRequestToUpdate: MockQuoteRequest = mockQuoteRequest.copy(mockQuoteRequest.id)
    db.run(mockQuoteRequests.filter(_.id === mockQuoteRequest.id).update(mockQuoteRequestToUpdate)).map(_ => (mockQuoteRequest))
  }

  def delete(id: Int): Future[Unit] = db.run(mockQuoteRequests.filter(_.id === id).delete).map(_ => ())

}
