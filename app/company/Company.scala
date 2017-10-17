package company

case class Company(id: Option[Long] = None,
                   name: String,
                   canonicalName: String = "",
                   phone1: Option[String] = None,
                   canonicalPhone1: Option[String] = None,
                   phone2: Option[String] = None,
                   canonicalPhone2: Option[String] = None,
                   phone3: Option[String] = None,
                   canonicalPhone3: Option[String] = None,
                   website: Option[String] = None,
                   twitter: Option[String] = None,
                   facebook: Option[String] = None,
                   linkedIn: Option[String] = None,
                   source: Option[String] = None,
                   active: Boolean = true) {

  def copyWithCanonicalFields = copy(
    canonicalName = CompanyCanonicaliser.canonicaliseName(name),
    canonicalPhone1 = phone1.map(p => CompanyCanonicaliser.canonicalisePhone(p)),
    canonicalPhone2 = phone2.map(p => CompanyCanonicaliser.canonicalisePhone(p)),
    canonicalPhone3 = phone3.map(p => CompanyCanonicaliser.canonicalisePhone(p))
  )
}
