package address


case class Address(
                    id: Option[Long] = None,
                    name: String,
                    fao: Option[String] = None,
                    line1: Option[String] = None,
                    line2: Option[String] = None,
                    line3: Option[String] = None,
                    townCity: Option[String] = None,
                    county: Option[String] = None,
                    postcode: Option[String] = None,
                    country: String = "United Kingdom",
                    active: Boolean = true)

object AddressCreator{
  def getOrDefaultAddress(address: Option[Address], name: String): Option[Address] ={
    if(address.isDefined) address else Some(new Address(name = name))
  }
}
