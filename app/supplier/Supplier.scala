package supplier


case class Supplier(id: Option[Long] = None,
                    name: String,
                    phone1: Option[String] = None,
                    phone2: Option[String] = None,
                    phone3: Option[String] = None,
                    active: Boolean = true) {
}
