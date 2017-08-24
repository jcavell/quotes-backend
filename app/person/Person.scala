package person

import com.byteslounge.slickrepo.meta.Entity
import company.Company

case class Person(override val id: Option[Int], name: String,
                  email: String,
                  tel: String,
                  companyId: Int) extends Entity[Person, Int] {
  def withId(id: Int): Person = this.copy(id = Some(id))
}