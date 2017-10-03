package purchase

import org.joda.time.DateTime

case class PO(id: Option[Long] = None,
              POSentDate: Option[DateTime] = None,
              purchaseTitle: Option[String] = None,
              supplierReference: Option[String],
              dateRequired: DateTime,
              invoiceReceived: Boolean,
              supplierAddressId: Long, // Snapshot supplier address just for this quote
              deliveryAddressId: Long, // Snapshot delivery address just for this PO
              invoiceId: Long,
              supplier: Long,
              supplierContact: Long,
              userId: Long, // Rep as now

              // common fields
              createdDate: Option[DateTime] = None,
              notes: Option[String] = None,
              active: Boolean = true
             )
