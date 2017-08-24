package quote

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import company.Company
import db.DatabaseExecutionContext
import person.Person
import play.api.db.DBApi
import product.{ASIProduct, ASIProductRepository}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future


case class QuoteWithProducts(quote: Quote, company: Company, person: Person, products: ListBuffer[ASIProduct])

case class QuotePage(quotes: Seq[QuoteWithProducts], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + quotes.size) < total)
}


@javax.inject.Singleton
class QuoteRepository @Inject()(dbapi: DBApi, productRepository: ASIProductRepository)(implicit ec: DatabaseExecutionContext) {

  private val db = dbapi.database("default")

  /**
    * Parse a Quote from a ResultSet
    */
   private val simple = {
      get[Option[Int]]("quote.id") ~
      get[String]("quote.status") ~
      get[Date]("quote.request_timestamp") ~
      get[Date]("quote.request_date_required") ~
      get[Long]("quote.request_product_id") ~
      get[String]("quote.request_customer_name") ~
      get[String]("quote.request_customer_email") ~
      get[String]("quote.request_customer_tel") ~
      get[String]("quote.request_company") ~
      get[Int]("quote.request_quantity") ~
      get[Option[String]]("quote.request_other_requirements") ~
      get[Int]("quote.person_id") map {
      case id ~ status ~ quoteTimestamp ~ dateRequired ~ productId ~ customerName ~ customerEmail ~ customerTel ~ company ~ quantity ~ otherRequirements ~ personId  =>
        Quote(id, status, quoteTimestamp, dateRequired, productId, customerName, customerEmail, customerTel, company, quantity, otherRequirements, personId)
    }
  }

  val companySimple = {
    get[Option[Int]]("company.id") ~
      get[String]("company.name") map {
      case id~name => Company(id, name)
    }
  }

  /**
    * Parse a Person from a ResultSet
    */
  val personSimple = {
    get[Option[Int]]("person.id") ~
      get[String]("person.name") ~
      get[String]("person.email") ~
      get[String]("person.tel") ~
      get[Int]("person.company_id") map {
      case id ~ name ~ email ~ tel ~ companyId =>
        Person(id, name, email, tel, companyId)
    }
  }

  /**
    * Parse a (Person,Company) from a ResultSet
    */
  private val personWithCompany = simple ~ (companySimple ?) map {
    case person ~ company => (person, company)
  }

  /**
    * Parse a (Quote, Company, Person, Product) from a ResultSet
    */
  private def withProduct = simple ~ (companySimple) ~ (personSimple) ~ (productRepository.simple) map {
    case quote ~ company ~ person ~ product => (quote, company, person, product)
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
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Future[QuotePage] = Future {

    val offset = pageSize * page

    db.withConnection { implicit connection =>

      val quotes = SQL(
        s"""
          select * from quote
          left join quote_product on quote_product.quote_id = quote.id
          left join product on product.internal_id = quote_product.product_internal_id
          left join person on quote.person_id = person.id
          left join company on person.company_id = company.id
          where product.Id::text like {filter} or product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
         group by quote.id, product.internal_id, person.id, company.id, quote_product.Id
          order by $orderBy nulls last
          limit $pageSize offset $offset
        """
      ).on('filter -> filter).as(withProduct *).foldLeft(List.empty[QuoteWithProducts]) { (z, f) =>
        val (quote, person, company, product) = f

        z.find(_.quote.id == quote.id) match{
          case None => z :+ QuoteWithProducts(quote, person, company, ListBuffer(product))
          case Some(quoteWithProducts) => quoteWithProducts.products.append(product); z
        }
      }

      val totalRows = SQL(
        s"""
            select count(distinct quote.id) from quote
            left join quote_product on quote_product.quote_id = quote.id
            left join product on product.internal_id = quote_product.product_internal_id
            left join person on quote.person_id = person.id
            left join company on person.company_id = company.id
            where product.Id::text like {filter} or product.name like {filter} or person.name like {filter} or person.email like {filter} or company.name like {filter}
        """.stripMargin
      ).on(
        'filter -> filter
      ).as(scalar[Long].single)

      QuotePage(quotes, page, offset, totalRows)
    }

  }(ec)
}