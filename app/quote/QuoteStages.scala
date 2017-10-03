package quote

object QuoteStages extends Enumeration {
  type QuoteStage = Value
  val QUOTE, SALES, INVOICE = Value
}