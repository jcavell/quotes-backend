package supplier

import address.Address
import user.User

case class ContactRecord(contact: Contact, supplier: Supplier, rep:Option[User], POAddress: Option[Address]) {

}
