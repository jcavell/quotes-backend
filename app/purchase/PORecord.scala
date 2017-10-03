package purchase

import address.Address
import quote.Quote
import supplier.{Contact, Supplier}
import user.User

case class PORecord(po: PO, quote: Quote, supplier: Supplier, contact: Contact, rep:User, supplierAddress: Option[Address], deliveryAddress: Option[Address]) {

}
