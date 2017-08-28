
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import person.PersonSlickRepository

class ModelSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {
  import models._

  import scala.concurrent.ExecutionContext.Implicits.global

  // -- Date helpers
  
  def dateIs(date: java.util.Date, str: String) = {
    new java.text.SimpleDateFormat("yyyy-MM-dd").format(date) == str
  }
  
  // --

  def personRepo: PersonSlickRepository = app.injector.instanceOf(classOf[PersonSlickRepository])

  "Computer model" should {

    "be retrieved by id" in {
      whenReady(personRepo.findById(1)) { maybeComputer =>
        val macintosh = maybeComputer.get

        macintosh.name must equal("Jonny Cavell")
      }
    }

    "be listed along its companies" in {
        whenReady(personRepo.list()) { people =>

          people.total must equal(574)
          people.items must have length(10)
        }
    }

//    "be updated if needed" in {
//
//      val result = personRepo.findById(1).flatMap { computer =>
//        personRepo.update(21, Quote(name="The Macintosh",
//          introduced=None,
//          discontinued=None,
//          companyId=Some(1))).flatMap { _ =>
//          personRepo.findById(21)
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
