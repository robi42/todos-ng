package com.robert42.todosng

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.json._
import java.util.{Map => JdkMap}

// API contract.
trait Storable {
  def fromJson(json: String, flags: JdkMap[String, Boolean]): String
  def fromXml(xml: String, flags: JdkMap[String, Boolean]): String
  def get(id: String): String
  def all: String
  def remove(id: String): Unit
}

// For `Storable#from[Json|Xml]` flags handling.
object Flags extends Enumeration("create", "update") {
  type Flags = Value
  val  Create, Update = Value
}

// For JSON serialization.
sealed abstract class JsonData
final case class TodoJsonData(text: String, order: Int, done: Boolean)
  extends JsonData
final case class TodoAllJsonData(_id: String, text: String, order: Int, done: Boolean)
  extends JsonData

// For XML serialization.
object TodoXmlData extends Enumeration("text", "order", "done") {
  type TodoXmlData = Value
  val  TextElem, OrderElem, DoneElem = Value
}

// Model definitions.
class Todo private() extends MongoRecord[Todo] with ObjectIdPk[Todo] {
  def meta = Todo

  object text       extends StringField(this, 12)
  object order      extends IntField(this)
  object done       extends BooleanField(this)
  object createdAt  extends DateTimeField(this) {
    override def asJValue = JInt(value.getTimeInMillis)
  }
  object modifiedAt extends OptionalDateTimeField(this) {
    override def asJValue = value match {
      case Some(value) => JInt(value.getTimeInMillis)
      case None        => JNull
    }
  }
}

object Todo extends Todo with MongoMetaRecord[Todo]
