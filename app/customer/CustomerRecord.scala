package customer

import address.Address
import company.Company
import user.User

case class CustomerRecord(
                           customer: Customer,
                           company: Company,
                           invoiceAddress: Option[Address],
                           deliveryAddress: Option[Address])
