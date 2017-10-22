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
    "remove ltd at end" in {
      CompanyCanonicaliser.canonicaliseName("Health Products Ltd.") mustEqual "healthproducts"
    }
    "remove inc at end" in {
      CompanyCanonicaliser.canonicaliseName("Health Products Inc.") mustEqual "healthproducts"
    }
    "remove limited at end" in {
      CompanyCanonicaliser.canonicaliseName("Health Products Limited") mustEqual "healthproducts"
    }
    "remove Incorporated. at end" in {
      CompanyCanonicaliser.canonicaliseName("Health Products Incorporated") mustEqual "healthproducts"
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
