
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import customer.CustomerSlickRepository

class ModelSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {
  import models._

  import scala.concurrent.ExecutionContext.Implicits.global

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }
  
  // --

  def customerRepo: CustomerSlickRepository = app.injector.instanceOf(classOf[CustomerSlickRepository])

  "Computer model" should {

    "be retrieved by id" in {
      whenReady(customerRepo.findById(1)) { maybeComputer =>
        val macintosh = maybeComputer.get

        macintosh.name must equal("Jonny Cavell")
      }
    }

    "be listed along its companies" in {
        whenReady(customerRepo.list()) { customers =>

          customers.total must equal(574)
          customers.items must have length(10)
        }
    }

//    "be updated if needed" in {
//
//      val result = customerRepo.findById(1).flatMap { computer =>
//        customerRepo.update(21, Quote(name="The Macintosh",
//          introduced=None,
//          discontinued=None,
//          companyId=Some(1))).flatMap { _ =>
//          customerRepo.findById(21)
//        }
//      }
//
//      whenReady(result) { computer =>
//        val macintosh = computer.get
//
//        macintosh.name must equal("The Macintosh")
//        macintosh.introduced mustBe None
//      }
//    }
  }
}
