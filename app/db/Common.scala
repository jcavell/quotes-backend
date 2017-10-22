package db

import java.sql.Timestamp

import slick.lifted.Rep


case class SearchAndSort(
                          orderField:Option[String] = None,
                          orderValue: Option[String] = None,
                          orderAsc: Boolean = true,
                          searchTerm: Option[String] = None){

  def hasOrderAsc(field: String) = orderField.contains(field) && orderAsc
  def hasOrderDesc(field: String) = orderField.contains(field) && !orderAsc

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
