package address


case class Address(
                    id: Option[Long] = None,
                    name: String,
                    company: String,
                    fao: Option[String] = None,
                    line1: String,
                    line2: Option[String] = None,
                    line3: Option[String] = None,
                    townCity: String,
                    county: Option[String] = None,
                    postcode: String,
                    country: String = "United Kingdom",
                    active: Boolean = true)
