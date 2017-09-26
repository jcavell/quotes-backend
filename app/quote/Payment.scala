package quote

import org.joda.time.DateTime

case class Payment(
                    id: Option[Long] = None,
                    invoiceId: Long,
                    amount: BigDecimal,
                    ref: Option[String],
                    paymentType: String, // BACs
                    paymentDate: DateTime
                  ) {

}
