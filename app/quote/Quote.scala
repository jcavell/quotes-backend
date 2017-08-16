package quote

import java.util.Date

case class Quote(id: Option[Long] = None,
                 status: String,
                 requestTimestamp: Date,
                 requestDateRequired: Date,
                 requestProductId: Long,
                 requestCustomerName: String,
                 requestCustomerEmail: String,
                 requestCustomerTel: String,
                 requestCompany: String,
                 requestQuantity:Int,
                 requestOtherRequirements: Option[String],
                 personId: Option[Long])
