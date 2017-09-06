package customer

case class Customer(
                    id: Option[Int] = None,
                    firstName: String,
                    lastName: String,
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
                    handlerId: Option[Int] = None,
                    companyId: Int
                  ) {}
