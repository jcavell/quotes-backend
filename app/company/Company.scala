package company

import com.byteslounge.slickrepo.meta.Entity


case class Company(id: Option[Int] = None, name: String) extends Entity[Company, Int] {
  def withId(id: Int): Company = this.copy(id = Some(id))
}