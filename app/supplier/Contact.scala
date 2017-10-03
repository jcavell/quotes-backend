package supplier

case class Contact(
                    id: Option[Long] = None,
                    name: String,
                    email: String,
                    directPhone: Option[String] = None,
                    mobilePhone: Option[String] = None,
                    position: Option[String] = None,
                    isMainContact: Boolean = true,
                    supplierId: Long,
                    repId: Option[Long],
                    POAddressId: Option[Long],
                    active: Boolean = true
) {}
