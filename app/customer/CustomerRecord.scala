package customer

import address.Address
import company.Company
import user.User

case class CustomerRecord(
                           customer: Customer,
                           company: Option[Company],
                           invoiceAddress: Option[Address] = None,
                           deliveryAddress: Option[Address] = None)
