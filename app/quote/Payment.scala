package quote

import org.joda.time.DateTime

case class Payment(
                    id: Option[Long] = None,
                    quoteId: Long,
                    amount: BigDecimal,
                    paymentRef: Option[String],
                    paymentType: String, // BACs
                    paymentDate: DateTime
                  ) {

}
