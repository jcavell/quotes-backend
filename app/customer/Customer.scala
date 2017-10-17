package customer

case class Customer(
                    id: Option[Long] = None,
                    name: String,
                    canonicalName: String = "",
                    email: String,
                    directPhone: Option[String] = None,
                    mobilePhone: Option[String] = None,
                    canonicalMobilePhone: Option[String] = None,
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
                  ) {

  def copyWithCanonicalFields = copy(
    email = CustomerCanonicaliser.canonicaliseEmail(email),
    canonicalName = CustomerCanonicaliser.canonicaliseName(name),
    canonicalMobilePhone = mobilePhone.map(m => CustomerCanonicaliser.canonicaliseMobile(m))
  )
}
