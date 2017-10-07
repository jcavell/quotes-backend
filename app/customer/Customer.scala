package customer

case class Customer(
                    id: Option[Long] = None,
                    name: String,
                    salutation: Option[String] = None,
                    email: String,
                    directPhone: Option[String] = None,
                    mobilePhone: Option[String] = None,
                    source: Option[String] = None,
                    position: Option[String] = None,
                    isMainContact: Boolean = true,
                    twitter: Option[String] = None,
                    facebook: Option[String] = None,
                    linkedIn: Option[String] = None,
                    skype: Option[String] = None,
                    active: Boolean = true,
                    repId: Option[Long] = None,
                    companyId: Long,
                    invoiceAddressId: Option[Long] = None,
                    deliveryAddressId: Option[Long] = None
                  ) {}
