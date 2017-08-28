package quote

import org.joda.time.DateTime

case class Quote(id: Option[Int] = None,
                 status: String,
                 requestTimestamp: DateTime,
                 requestDateRequired: DateTime,
                 requestProductId: Long,
                 requestCustomerName: String,
                 requestCustomerEmail: String,
                 requestCustomerTel: String,
                 requestCompany: String,
                 requestQuantity:Int,
                 requestOtherRequirements: Option[String],
                 personId: Int)
