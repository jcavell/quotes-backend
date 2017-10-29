package quote

import customer.CustomerRecord
import user.User

case class QuoteRecord(
                        quote:Quote,
                        quoteMeta: QuoteMeta,
                        customerRecord: CustomerRecord,
                        lineItems: Option[Seq[QuoteLineItem]] = None,
                        xsellItems: Option[Seq[QuoteXsellItem]] = None,
                        rep: Option[User] = None,
                        assignedUser: Option[User] = None
                      ) {

}
