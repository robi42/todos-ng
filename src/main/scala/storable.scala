package com.robert42.todosng

import net.liftweb.json.{Serialization, NoTypeHints}
import java.lang.IllegalArgumentException
import java.util.{Map => JdkMap}

// API contract.
trait Storable {
  protected[this] implicit val formats = Serialization.formats(NoTypeHints)
  protected[this] val flagsErrMsg =
    "Either `create` or `update` must be `true`."
  protected[this] def makeFlagsErr = new IllegalArgumentException(flagsErrMsg)

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
