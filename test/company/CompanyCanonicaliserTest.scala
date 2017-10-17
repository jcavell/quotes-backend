package company

import org.specs2.mutable.Specification

class CompanyCanonicaliserTest extends Specification {
  "Canonicalise company name" should {
    "make lowercase " in {
      CompanyCanonicaliser.canonicaliseName("HELLO") mustEqual "hello"
    }
    "remove spaces " in {
      CompanyCanonicaliser.canonicaliseName("hi there  boys") mustEqual "hithereboys"
    }
    "remove non alphanumeric " in {
      CompanyCanonicaliser.canonicaliseName("THE; box1! of :( doom") mustEqual "thebox1ofdoom"
    }
    "remove ltd, inc, incorporated or limited " in {
      CompanyCanonicaliser.canonicaliseName("Health Ltd Inc incorporated Limited Products") mustEqual "healthproducts"
    }
    "none returns empty string " in {
      CompanyCanonicaliser.canonicaliseName("NONE!") mustEqual ""
    }
    "N/A returns empty string " in {
      CompanyCanonicaliser.canonicaliseName("N/A") mustEqual ""
    }
  }
  "Canonicalise mobile phone" should {
    "remove non numberic characters " in {
      CompanyCanonicaliser.canonicalisePhone("(+44)720 123") mustEqual "44720123"
    }
  }
}
