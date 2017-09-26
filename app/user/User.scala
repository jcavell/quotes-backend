package user

case class User(
                 id: Option[Long] = None,
                 name: String,
                 email: String,
                 directPhone: Option[String],
                 mobilePhone: Option[String],
                 active: Boolean = true)
