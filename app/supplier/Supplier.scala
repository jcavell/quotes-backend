package supplier


case class Supplier(id: Option[Int] = None,
                    name: String,
                    phone1: Option[String] = None,
                    phone2: Option[String] = None,
                    phone3: Option[String] = None,
                    source: Option[String] = None,
                    active: Boolean = true) {
}
