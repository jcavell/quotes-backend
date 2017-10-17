package customer

import org.specs2.mutable.Specification

class CustomerCanonicaliserTest extends Specification {
  "Canonicalise name" should {
    "make lowercase " in {
      CustomerCanonicaliser.canonicaliseName("Jonny") mustEqual "jonny"
    }
    "remove spaces " in {
      CustomerCanonicaliser.canonicaliseName("Jonny Cavell") mustEqual "jonnycavell"
    }
    "remove non alphanumeric " in {
      CustomerCanonicaliser.canonicaliseName("Jonny . D Buggy") mustEqual "jonnydbuggy"
    }
    "remove title " in {
      CustomerCanonicaliser.canonicaliseName("Mr. Cavell") mustEqual "cavell"
    }
    "not remove Draco " in {
      CustomerCanonicaliser.canonicaliseName("Draco Malfoy") mustEqual "dracomalfoy"
    }
  }
  "Canonicalise email" should {
    "make lowercase " in {
      CustomerCanonicaliser.canonicaliseEmail("Jonny.Cavell@GMAIL.com") mustEqual "jonny.cavell@gmail.com"
    }
    "remove spaces " in {
      CustomerCanonicaliser.canonicaliseEmail("jonny.cavell@gmail.com ") mustEqual "jonny.cavell@gmail.com"
    }
  }
  "Canonicalise mobile phone" should {
    "remove non numberic characters " in {
      CustomerCanonicaliser.canonicaliseMobile("(+44)720 123") mustEqual "44720123"
    }
  }
}
