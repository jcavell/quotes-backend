package quote

import org.joda.time.DateTime

case class Quote(id: Option[Int] = None,
                 status: String,
                 requestTimestamp: DateTime,
                 requestDateRequired: DateTime,
                 requestProductId: Long,
                 requestCustomerFirstName: String,
                 requestCustomerLastName: String,
                 requestCustomerEmail: String,
                 requestCustomerTel: String,
                 requestCompany: String,
                 requestQuantity:Int,
                 requestOtherRequirements: Option[String],
                 customerId: Int)
