package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class QuoteRequest(id: Option[Long] = None,
                        quoteTimestamp: Date,
                        dateRequired: Date,
                        quantity:Int,
                        otherRequirements: Option[String],
                        personId: Option[Long],
                        quoteRequestProductId: Option[Long])

/**
  * Helper for pagination.
  */

case class QuoteRequestAndPersonAndProduct(quoteRequest: QuoteRequest, company: Company, person: Person, quoteRequestProduct: QuoteRequestProduct)

case class QuoteRequestPage(items: Seq[QuoteRequestAndPersonAndProduct], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}


@javax.inject.Singleton
class QuoteRequestRepository @Inject()(dbapi: DBApi, quoteRequestProductRepository: QuoteRequestProductRepository, companyRepository: CompanyRepository, personRepository: PersonRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  // -- Parsers

  /**
    * Parse a Computer from a ResultSet
    */
   private val simple = {
    get[Option[Long]]("quote_request.id") ~
      get[Date]("quote_request.quote_timestamp") ~
      get[Date]("quote_request.date_required") ~
      get[Int]("quote_request.quantity") ~
     get[Option[String]]("quote_request.other_requirements") ~
      get[Option[Long]]("quote_request.person_id") ~
      get[Option[Long]]("quote_request.quote_request_product_id") map {
      case id ~ quoteTimestamp ~ dateRequested ~ quantity ~ otherRequirements ~ personId ~ quoteRequestProductId =>
        QuoteRequest(id, quoteTimestamp, dateRequested, quantity, otherRequirements, personId, quoteRequestProductId)
    }
  }

  /**
    * Parse a (Computer,Company) from a ResultSet
    */
  private val withQuoteRequestProduct = simple ~ (companyRepository.simple) ~ (personRepository.simple) ~ (quoteRequestProductRepository.simple ?) map {
    case quoteRequest ~ company ~ person ~ quoteRequestProduct => QuoteRequestAndPersonAndProduct(quoteRequest, company, person, quoteRequestProduct.get)
  }

  // -- Queries

  /**
    * Retrieve a person from the id.
    */
  def findById(id: Long): Future[Option[QuoteRequest]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from quote_request where id = $id").as(simple.singleOpt)
    }
  }(ec)

  /**
    * Return a page of (Computer,Company).
    *
    * @param page     Page to display
    * @param pageSize Number of persons per page
    * @param orderBy  Computer property used for sorting
    * @param filter   Filter applied on the name column
    */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[QuoteRequestPage] = Future {

    val offset = pageSize * page

    db.withConnection { implicit connection =>

      val quoteRequests = SQL(
        s"""
          select * from quote_request
          left join quote_request_product on quote_request.quote_request_product_id = quote_request_product.id
          left join person on quote_request.person_id = person.id
          left join company on person.company_id = company.id
          where quote_request_product.product_id::text like {filter} or quote_request_product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
          order by $orderBy nulls last
          limit $pageSize offset $offset
        """
      ).on('filter -> filter).as(withQuoteRequestProduct *)

      val totalRows = SQL(
        """
          select count(*) from quote_request
          left join quote_request_product on quote_request.quote_request_product_id = quote_request_product.id
           left join person on quote_request.person_id = person.id
            left join company on person.company_id = company.id
          where quote_request_product.product_id::text like {filter} or quote_request_product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      QuoteRequestPage(quoteRequests, page, offset, totalRows)
    }

  }(ec)


}
