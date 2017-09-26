package product


case class Product(
                    id: Option[Long] = None,
                    sku: String,
                    description: String
                  ) {

}

