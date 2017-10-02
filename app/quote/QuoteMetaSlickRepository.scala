package quote

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import com.github.tototoshi.slick.PostgresJodaSupport._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


trait QuoteMetasComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class QuoteMetas(tag: Tag) extends Table[QuoteMeta](tag, "quote_meta") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def status = column[String]("status")
    def stage = column[String]("stage")
    def quoteLossReason = column[Option[String]]("quote_loss_reason")
    def quoteSentDate = column[Option[DateTime]]("quote_sent_date")
    def saleSentDate = column[Option[DateTime]]("sale_sent_date")
    def invoiceSentDate = column[Option[DateTime]]("invoice_sent_date")
    def paymentTerms = column[Option[String]]("payment_terms")
    def paymentDueDate = column[Option[DateTime]]("payment_due_date")
    def paymentStatus = column[Option[String]]("payment_status")
    def assignedGroup = column[Option[String]]("assigned_group")
    def assignedUserId = column[Option[Long]]("assigned_user_id")
    def quoteId = column[Long]("quote_id")
    def * = (id.?, status, stage, quoteLossReason, quoteSentDate, saleSentDate, invoiceSentDate, paymentTerms, paymentDueDate, paymentStatus, assignedGroup, assignedUserId, quoteId) <> (QuoteMeta.tupled, QuoteMeta.unapply _)
  }
}

@Singleton()
class QuoteMetaSlickRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuoteMetasComponent
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val quoteMetas = TableQuery[QuoteMetas]

  def all: Future[List[QuoteMeta]] = db.run(quoteMetas.to[List].result)

  def get(quotemetaId: Long):Future[Option[QuoteMeta]] = db.run(quoteMetas.filter(_.id === quotemetaId).result.headOption)

  def insert(quotemeta: QuoteMeta): Future[QuoteMeta] = {
    val action = quoteMetas returning quoteMetas.map {_.id} += quotemeta

    db.run(action.asTry).map { result =>
      result match {
        case Success(r) => quotemeta.copy(id = Some(r))
        case Failure(e) => throw e
      }
    }
  }


  def update(id: Long, quotemeta: QuoteMeta): Future[QuoteMeta] = {
    val quotemetaToUpdate: QuoteMeta = quotemeta.copy(Some(id))
    db.run(quoteMetas.filter(_.id === id).update(quotemetaToUpdate)).map(_ => (quotemeta))
  }

  def delete(id: Long): Future[Unit] = db.run(quoteMetas.filter(_.id === id).delete).map(_ => ())

}
