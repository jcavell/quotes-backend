package quote

import address.Address
import customer.{Company, Customer}
import user.User

case class QuoteRecord(
                        quote:Quote,
                        quoteMeta: QuoteMeta,
                        customer: Customer,
                        company: Company,
                        rep: User,
                        assignedUser: Option[User] = None,
                        invoiceAddress: Option[Address] = None,
                        deliveryAddress: Option[Address] = None,
                        lineItems: Option[Seq[QuoteLineItem]] = None
                      ) {

}