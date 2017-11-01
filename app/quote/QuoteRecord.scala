package quote

import customer.CustomerRecord
import enquiry.Enquiry
import user.User

case class QuoteRecord(
                        quote:Quote,
                        quoteMeta: QuoteMeta,
                        enquiry: Option[Enquiry],
                        customerRecord: Option[CustomerRecord],
                        lineItems: Option[Seq[QuoteLineItem]] = None,
                        xsellItems: Option[Seq[QuoteXsellItem]] = None,
                        rep: Option[User] = None,
                        assignedUser: Option[User] = None
                      ) {

}
