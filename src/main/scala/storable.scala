package com.robert42.todosng

import net.liftweb.common.Logger
import net.liftweb.record.Record
import net.liftweb.record.field._
import net.liftweb.json._
import net.liftweb.json.{Serialization, NoTypeHints}
import java.lang.IllegalArgumentException
import java.util.{Map => JdkMap, List => JdkList}

// API contract -- a.k.a. DAO.
trait Storable extends Logger {
  protected[this] implicit val formats = Serialization.formats(NoTypeHints)

  protected[this] val flagsErrMsg = "Either `create` or `update` must be `true`."

  protected[this] def makeFlagsErr = new IllegalArgumentException(flagsErrMsg)

  protected[this] def getFlags(flags: JdkMap[String, Boolean]): (Boolean, Boolean) =
    (flags get "create", flags get "update")

  def fromJson(json: String, flags: JdkMap[String, Boolean]): Record[_]
  def fromXml(xml: String, flags: JdkMap[String, Boolean]): Record[_]
  def get(id: String): Record[_]
  def all: JdkList[Record[_]]
  def allAsJson: String
  def remove(id: String): Unit
}

// Timestamps mixin.
trait Timestampable[OwnerType <: Record[OwnerType]] {
  object createdAt  extends DateTimeField(this.asInstanceOf[OwnerType]) {
    override def asJValue = JInt(value.getTimeInMillis)
  }
  object modifiedAt extends OptionalDateTimeField(this.asInstanceOf[OwnerType]) {
    override def asJValue = value match {
      case Some(value) => JInt(value.getTimeInMillis)
      case None        => JNull
    }
  }
}

// JSON serialization mixin.
trait JsonSerializable[OwnerType <: Record[OwnerType]] {
  def toJson = compact(render(this.asInstanceOf[OwnerType].asJValue))
}

// JSON data contracts.
abstract class JsonData
