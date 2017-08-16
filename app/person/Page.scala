package person

/**
  * Created by jcavell on 16/08/2017.
  */
case class Page(items: Seq[PersonCompany], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
