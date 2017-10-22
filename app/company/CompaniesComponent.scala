package company

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

/**
  * Created by jcavell on 07/10/2017.
  */
trait CompaniesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class Companies(tag: Tag) extends Table[Company](tag, "company") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def canonicalName = column[Option[String]]("canonical_name")
    def phone1 = column[Option[String]]("phone1")
    def canonicalPhone1 = column[Option[String]]("canonical_phone1")
    def phone2 = column[Option[String]]("phone2")
    def canonicalPhone2 = column[Option[String]]("canonical_phone2")
    def phone3 = column[Option[String]]("phone3")
    def canonicalPhone3 = column[Option[String]]("canonical_phone3")
    def website = column[Option[String]]("website")
    def twitter = column[Option[String]]("twitter")
    def facebook = column[Option[String]]("facebook")
    def linkedIn = column[Option[String]]("linked_in")
    def source = column[Option[String]]("source")
    def active = column[Boolean]("active")

    def * = (id.?, name, canonicalName, phone1, canonicalPhone1, phone2, canonicalPhone2, phone3, canonicalPhone3, website, twitter, facebook, linkedIn, source, active) <> (Company.tupled, Company.unapply _)
  }

}
