package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class Quote(id: Option[Long] = None,
                 requestTimestamp: Date,
                 requestDateRequired: Date,
                 requestProductId: Long,
                 requestCustomerName: String,
                 requestCustomerEmail: String,
                 requestCustomerTel: String,
                 requestCompany: String,
                 requestQuantity:Int,
                 requestOtherRequirements: Option[String],
                 personId: Option[Long],
                 quoteProductId: Option[Long])


case class QuoteAndPersonAndProduct(quote: Quote, company: Company, person: Person, quoteProduct: QuoteProduct)

case class QuotePage(quotes: Seq[QuoteAndPersonAndProduct], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + quotes.size) < total)
}


@javax.inject.Singleton
class QuoteRepository @Inject()(dbapi: DBApi, quoteProductRepository: QuoteProductRepository, companyRepository: CompanyRepository, personRepository: PersonRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  // -- Parsers

  /**
    * Parse a Computer from a ResultSet
    */
   private val simple = {
      get[Option[Long]]("quote.id") ~
      get[Date]("quote.request_timestamp") ~
      get[Date]("quote.request_date_required") ~
     get[Long]("quote.request_product_id") ~
      get[String]("quote.request_customer_name") ~
      get[String]("quote.request_customer_email") ~
      get[String]("quote.request_customer_tel") ~
      get[String]("quote.request_company") ~
      get[Int]("quote.request_quantity") ~
      get[Option[String]]("quote.request_other_requirements") ~
      get[Option[Long]]("quote.person_id") ~
      get[Option[Long]]("quote.quote_product_id") map {
      case id ~ quoteTimestamp ~ dateRequired ~ productId ~ customerName ~ customerEmail ~ customerTel ~ company ~ quantity ~ otherRequirements ~ personId ~ quoteProductId =>
        Quote(id, quoteTimestamp, dateRequired, productId, customerName, customerEmail, customerTel, company, quantity, otherRequirements, personId, quoteProductId)
    }
  }

  /**
    * Parse a (Computer,Company) from a ResultSet
    */
  private val withQuoteProduct = simple ~ (companyRepository.simple) ~ (personRepository.simple) ~ (quoteProductRepository.simple ?) map {
    case quote ~ company ~ person ~ quoteProduct => QuoteAndPersonAndProduct(quote, company, person, quoteProduct.get)
  }

  // -- Queries

  /**
    * Retrieve a person from the id.
    */
  def findById(id: Long): Future[Option[Quote]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from quote where id = $id").as(simple.singleOpt)
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
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[QuotePage] = Future {

    val offset = pageSize * page

    db.withConnection { implicit connection =>

      val quotes = SQL(
        s"""
          select * from quote
          left join quote_product on quote.quote_product_id = quote_product.id
          left join person on quote.person_id = person.id
          left join company on person.company_id = company.id
          where quote_product.product_id::text like {filter} or quote_product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
          order by $orderBy nulls last
          limit $pageSize offset $offset
        """
      ).on('filter -> filter).as(withQuoteProduct *)

      val totalRows = SQL(
        """
          select count(*) from quote
          left join quote_product on quote.quote_product_id = quote_product.id
           left join person on quote.person_id = person.id
            left join company on person.company_id = company.id
          where quote_product.product_id::text like {filter} or quote_product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
        """
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      QuotePage(quotes, page, offset, totalRows)
    }

  }(ec)


}
