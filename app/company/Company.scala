package company

/**
  * Created by jcavell on 24/09/2017.
  */
case class Company(id: Option[Long] = None,
                   name: String,
                   phone1: Option[String] = None,
                   phone2: Option[String] = None,
                   phone3: Option[String] = None,
                   website: Option[String] = None,
                   twitter: Option[String] = None,
                   facebook: Option[String] = None,
                   linkedIn: Option[String] = None,
                   source: Option[String] = None,
                   active: Boolean = true) {
}
