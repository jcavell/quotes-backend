package supplier

case class Contact(
                    id: Option[Int] = None,
                    name: String,
                    email: String,
                    directPhone: Option[String] = None,
                    mobilePhone: Option[String] = None,
                    position: Option[String] = None,
                    isMainContact: Boolean = true,
                    supplierId: Int,
                    active: Boolean = true
) {}
