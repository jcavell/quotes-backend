package db

import java.sql.Timestamp

import slick.lifted.Rep

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
