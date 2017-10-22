package db

import java.sql.Timestamp

import slick.lifted.{CanBeQueryCondition, Rep}

case class Search(
                 searchField: Option[String] = None,
                 searchValue: Option[String] = None) {

  def containsSearchTerm = searchField.isDefined

  def getSearchValue(searchField: String, canonicaliser: (String) => String): Option[String] =
    if(this.searchField.contains(searchField)) this.searchValue.map(s => canonicaliser(s)) else None

  def getSearchValueAsLong(searchField: String): Option[Long] =
    if(this.searchField.contains(searchField)) this.searchValue.map(s => s.toLong) else None
}

object Search{
  def fromRequestMap(queryString: Map[String, Seq[String]]): Search = {
    Search(searchField = queryString.get("searchField") match {
      case Some(a) => Some(a.head)
      case None => None
    }, searchValue = queryString.get("searchValue") match {
      case Some(a) => Some(a.head)
      case None => None
    })
  }
}


case class Sort(
                 orderField: Option[String] = None,
                 orderValue: Option[String] = None,
                 orderAsc: Boolean = true) {

  def hasOrderAsc(field: String) = orderField.contains(field) && orderAsc

  def hasOrderDesc(field: String) = orderField.contains(field) && !orderAsc
}

object Sort{
  def fromRequestMap(queryString: Map[String, Seq[String]]): Sort = {
    Sort(orderField = queryString.get("orderField") match {
      case Some(a) => Some(a.head)
      case None => None
    }, orderAsc = queryString.get("orderAsc") match {
      case Some(a) => a.head.toLowerCase == "true"
      case None => true
    })
  }
}


case class CommonFields(
                         createdAt: Timestamp = new Timestamp(System.currentTimeMillis),
                         notes: Option[String] = None,
                         //history: Option[JsValue] = None,
                         active: Boolean = true
                       )

case class CommonColumns(
                          createdAt: Rep[Timestamp],
                          notes: Rep[Option[String]],
                          //history: Rep[Option[JsValue]],
                          active: Rep[Boolean]
                        )

trait CommonModel {
  def commonFields: CommonFields

  def createdAt = commonFields.createdAt

  def notes = commonFields.notes

  //def history = commonFields.history

  def active = commonFields.active
}
