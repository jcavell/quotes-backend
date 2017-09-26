package user

case class User(
                 id: Int,
                 name: String,
                 email: String,
                 directPhone: Option[String],
                 mobilePhone: Option[String],
                 active: Boolean = true)
