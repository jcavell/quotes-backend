package quote

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import company.Company
import db.DatabaseExecutionContext
import org.joda.time.DateTime
import customer.Customer
import play.api.db.DBApi
import product.{ASIProduct, ASIProductAnormRepository}
import quote.Status.Status

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.{Failure, Try}


case class QuoteWithProducts(quote: Quote, company: Company, customer: Customer, products: ListBuffer[ASIProduct])

case class QuotePage(quotes: Seq[QuoteWithProducts], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + quotes.size) < total)
}


@javax.inject.Singleton
class QuoteRepository @Inject()(dbapi: DBApi, productRepository: ASIProductAnormRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")


  /**
    * Parse a Quote from a ResultSet
    */
  private val simple = {
    get[Option[Int]]("quote.id") ~
      get[String]("quote.status") ~
      get[DateTime]("quote.request_timestamp") ~
      get[DateTime]("quote.request_date_required") ~
      get[Long]("quote.request_product_id") ~
      get[String]("quote.request_customer_first_name") ~
      get[String]("quote.request_customer_last_name") ~
      get[String]("quote.request_customer_email") ~
      get[String]("quote.request_customer_tel") ~
      get[String]("quote.request_company") ~
      get[Int]("quote.request_quantity") ~
      get[Option[String]]("quote.request_other_requirements") ~
      get[Int]("quote.customer_id") map {
      case id ~ status ~ quoteTimestamp ~ dateRequired ~ productId ~ customerFirstName ~ customerLastName ~ customerEmail ~ customerTel ~ company ~ quantity ~ otherRequirements ~ customerId =>
        Quote(id, status, quoteTimestamp, dateRequired, productId, customerFirstName, customerLastName, customerEmail, customerTel, company, quantity, otherRequirements, customerId)
    }
  }

  val companySimple = {
    get[Option[Int]]("company.id") ~
      get[String]("company.name") map {
      case id ~ name => Company(id, name)
    }
  }

  /**
    * Parse a Customer from a ResultSet
    */
  val customerSimple = {
    get[Option[Int]]("customer.id") ~
      get[String]("customer.first_name") ~
      get[String]("customer.last_name") ~
      get[Option[String]]("customer.salutation") ~
      get[String]("customer.email") ~
      get[Option[String]]("customer.direct_phone") ~
      get[Option[String]]("customer.mobile_phone") ~
      get[Option[String]]("customer.source") ~
      get[Option[String]]("customer.position") ~
      get[Boolean]("customer.is_main_contact") ~
      get[Option[String]]("customer.twitter") ~
      get[Option[String]]("customer.facebook") ~
      get[Option[String]]("customer.linked_in") ~
      get[Option[String]]("customer.skype") ~
      get[Option[Int]]("customer.handler_id") ~
      get[Int]("customer.company_id") map {
      case id ~ firstName ~ lastName ~ salutation ~ email ~ directPhone ~ mobilePhone ~ source ~ position ~ isMainContact ~ twitter ~ facebook ~ linkedIn ~ skype ~ handlerId ~ companyId =>
        Customer(id, firstName, lastName, salutation, email, directPhone, mobilePhone, source, position, isMainContact, twitter, facebook, linkedIn, skype, handlerId, companyId)
    }
  }


  val joinQuery =
    """left join quote_product on quote_product.quote_id = quote.id
      |          left join product on product.internal_id = quote_product.product_internal_id
      |          left join customer on quote.customer_id = customer.id
      |          left join company on customer.company_id = company.id""".stripMargin


  val groupBy = "group by quote.id, product.internal_id, customer.id, company.id, quote_product.Id"

  /**
    * Parse a (Customer,Company) from a ResultSet
    */
  private val customerWithCompany = simple ~ (companySimple ?) map {
    case customer ~ company => (customer, company)
  }

  /**
    * Parse a (Quote, Company, Customer, Product) from a ResultSet
    */
  private def withProduct = simple ~ (companySimple) ~ (customerSimple) ~ (productRepository.simple) map {
    case quote ~ company ~ customer ~ product => (quote, company, customer, product)
  }

  // -- Queries

  /**
    * Retrieve a Quote from the id.
    */
  def findById(id: Int): Future[Option[Quote]] = Future {
    db.withConnection { implicit connection =>
      SQL("select * from quote where id = $id").as(simple.singleOpt)
    }
  }(ec)

  /**
    * Return a QuotePage of QuoteWithProducts
    *
    * @param page     Page to display
    * @param pageSize Number of Quotes per page
    * @param orderBy  Field for sorting
    * @param filter   Filter applied on various columns
    */
  def listWithGenericFilter(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    val whereFilter = "where product.Id::text like {filter} or product.name like {filter} or customer.first_name like {filter} or customer.last_name like {filter} or customer.email like {filter} or company.name like {filter}"
    val wherePlaceholders = 'filter -> filter
    list(page, pageSize, orderBy, whereFilter, wherePlaceholders)
  }

  def listForCompany(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, companyName: String) = {
    val whereFilter = "where company.name = {companyName}"
    val wherePlaceholders = 'companyName -> companyName
    list(page, pageSize, orderBy, whereFilter, wherePlaceholders)
  }

  def listForEmail(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, email: String) = {
    val whereFilter = "where email = {email}"
    val wherePlaceholders = 'email -> email
    list(page, pageSize, orderBy, whereFilter, wherePlaceholders)
  }

  private def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, where: String, wherePlaceholders: (Symbol, String)): Future[QuotePage] = Future {

    val offset = pageSize * page

    db.withConnection { implicit connection =>

      val quotes = SQL(
        s"""
          select * from quote
          $joinQuery
          $where
          $groupBy
          order by $orderBy nulls last
          limit $pageSize offset $offset
        """
      ).on(wherePlaceholders).as(withProduct *).foldLeft(List.empty[QuoteWithProducts]) { (z, f) =>
        val (quote, customer, company, product) = f

        z.find(_.quote.id == quote.id) match {
          case None => z :+ QuoteWithProducts(quote, customer, company, ListBuffer(product))
          case Some(quoteWithProducts) => quoteWithProducts.products.append(product); z
        }
      }

      val totalRows = SQL(
        s"""
            select count(distinct quote.id) from quote
            $joinQuery
            $where
        """.stripMargin
      ).on(wherePlaceholders).as(scalar[Long].single)

      QuotePage(quotes, page, offset, totalRows)
    }

  }(ec)
}