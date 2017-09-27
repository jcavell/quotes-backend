package customer

import address.Address
import user.User

case class CustomerRecord(
                           customer: Customer,
                           company: Company,
                           rep: User,
                           invoiceAddress: Option[Address],
                           deliveryAddress: Option[Address])
