package quote

import address.Address

case class QuoteRecord(quote:Quote, quoteMeta: QuoteMeta, invoiceAddress: Option[Address], deliveryAddress: Option[Address]) {

}
