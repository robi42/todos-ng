package com.robert42.todosng

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._

// Model definitions.
trait Storable {
  def createFromJson(json: String): String
  def createFromXml(xml: String): String
  def get(id: String): String
  def all: String
  def updateFromJson(json: String): String
  def updateFromXml(xml: String): String
  def remove(id: String): Unit
}

case class TodoData(text: String, order: Int, done: Boolean)
case class TodoAllData(_id: String, text: String, order: Int, done: Boolean)

class Todo private() extends MongoRecord[Todo] with ObjectIdPk[Todo] {
  def meta = Todo

  object text  extends StringField(this, 12)
  object order extends IntField(this)
  object done  extends BooleanField(this)
}

object Todo extends Todo with MongoMetaRecord[Todo]
