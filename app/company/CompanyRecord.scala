package company

import customer.CustomerRecord

/**
  * Created by jcavell on 07/10/2017.
  */
case class CompanyRecord(company: Company, customers: Seq[CustomerRecord]) {
}
