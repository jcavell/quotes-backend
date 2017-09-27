package supplier


case class Supplier(id: Option[Long] = None,
                    name: String,
                    active: Boolean = true) {
}
