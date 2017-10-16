package quote

object QuoteStages extends Enumeration {
  type QuoteStage = Value
  val ENQUIRY, QUOTE, SALES, INVOICE = Value
}