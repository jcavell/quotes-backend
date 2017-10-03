package db

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}
import asiquote.ASIStatus
import quote.{PaymentStatuses, QuoteStages, QuoteStatuses}
import slick.basic.Capability
import slick.driver.JdbcProfile


trait PgProfileWithAddons extends ExPostgresProfile
  with PgEnumSupport
  with PgPlayJsonSupport {

  val pgjson = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with EnumImplicits
    with JsonImplicits {
    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }

  trait EnumImplicits {
    implicit val statusTypeMapper = createEnumJdbcType("quote_status", ASIStatus)
    implicit val statusListTypeMapper = createEnumListJdbcType("quote_status", ASIStatus)
    implicit val statusColumnExtensionMethodsBuilder = createEnumColumnExtensionMethodsBuilder(ASIStatus)
    implicit val statusOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(ASIStatus)

    implicit val paymentStatusesTypeMapper = createEnumJdbcType("payment_status", PaymentStatuses)
    implicit val paymentStatusesListMapper = createEnumListJdbcType("payment_status", PaymentStatuses)
    implicit val paymentStatusesMethodsBuilder = createEnumColumnExtensionMethodsBuilder(PaymentStatuses)
    implicit val paymentStatusesExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(PaymentStatuses)

    implicit val quoteStatusesTypeMapper = createEnumJdbcType("quote_status", QuoteStatuses)
    implicit val quoteStatusesListMapper = createEnumListJdbcType("quote_status", QuoteStatuses)
    implicit val quoteStatusesMethodsBuilder = createEnumColumnExtensionMethodsBuilder(QuoteStatuses)
    implicit val quoteStatusesExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(QuoteStatuses)

    implicit val quoteStagesTypeMapper = createEnumJdbcType("quote_stage", QuoteStages)
    implicit val quoteStagesListMapper = createEnumListJdbcType("quote_stage", QuoteStages)
    implicit val quoteStagesMethodsBuilder = createEnumColumnExtensionMethodsBuilder(QuoteStages)
    implicit val quoteStagesExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(QuoteStages)
  }

}

object PgProfileWithAddons extends PgProfileWithAddons
