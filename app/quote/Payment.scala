package quote

import org.joda.time.DateTime

case class Payment(
                    id: Option[Int] = None,
                    invoiceId: Int,
                    amount: BigDecimal,
                    ref: Option[String],
                    paymentType: String, // BACs
                    paymentDate: DateTime
                  ) {

}
