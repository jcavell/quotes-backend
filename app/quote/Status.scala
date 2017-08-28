package quote

object Status extends Enumeration {
  type Status = Value
  val REQUESTED, WITH_CUSTOMER, WITH_DESIGN, WITH_ACCOUNTS = Value
}
