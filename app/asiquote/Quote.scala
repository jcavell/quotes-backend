package asiquote

import org.joda.time.DateTime

case class Quote(id: Option[Long] = None,
                 status: String,
                 requestTimestamp: DateTime,
                 requestDateRequired: DateTime,
                 requestProductId: String,
                 requestCustomerFirstName: String,
                 requestCustomerLastName: String,
                 requestCustomerEmail: String,
                 requestCustomerTel: String,
                 requestCompany: String,
                 requestQuantity:Int,
                 requestOtherRequirements: Option[String],
                 customerId: Long)
