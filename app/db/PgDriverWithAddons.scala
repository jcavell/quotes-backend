package db

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}
import quote.Status
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
    implicit val statusTypeMapper = createEnumJdbcType("quote_status", Status)
    implicit val statusListTypeMapper = createEnumListJdbcType("quote_status", Status)

    implicit val statusColumnExtensionMethodsBuilder = createEnumColumnExtensionMethodsBuilder(Status)
    implicit val statusOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(Status)
  }

}

object PgProfileWithAddons extends PgProfileWithAddons