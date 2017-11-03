package enquiry

import org.joda.time.DateTime
import org.specs2.mutable.Specification

/**
  * Created by jcavell on 02/11/2017.
  */
class EnquiryProcessorTaskTest extends Specification {

  "Generate quote" should {
    "insertQuote" in {
      val enquiry = new Enquiry(enquiryId = 100, enquiryTimestamp = DateTime.now, productId = 100, sku = "100", customerName = "Jonnty Cavell", customerEmail = "jonny.cavell@gmail.com", customerTelephone = "123123", company = "Comp Comp", requiredDate = DateTime.now, quantity = 5, repId = 5, repEmail = "adfaf", productName = "jon's prod", id = None)
      ok
    }

  }
}
