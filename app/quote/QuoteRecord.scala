package quote

import address.Address
import company.Company
import customer.{Customer}
import user.User

case class QuoteRecord(
                        quote:Quote,
                        quoteMeta: QuoteMeta,
                        customer: Option[Customer],
                        company: Option[Company],
                        rep: Option[User],
                        assignedUser: Option[User] = None,
                        invoiceAddress: Option[Address] = None,
                        deliveryAddress: Option[Address] = None,
                        lineItems: Option[Seq[QuoteLineItem]] = None,
                        xsellItems: Option[Seq[QuoteXsellItem]] = None
                      ) {

}
