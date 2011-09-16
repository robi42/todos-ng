package com.robert42.todosng

import net.liftweb.common.Logger
import net.liftweb.json.{Serialization, NoTypeHints}
import java.lang.IllegalArgumentException
import java.util.{Map => JdkMap}

// API contract -- a.k.a. DAO.
trait Storable extends Logger {
  protected[this] implicit val formats = Serialization.formats(NoTypeHints)

  protected[this] val flagsErrMsg = "Either `create` or `update` must be `true`."

  protected[this] def makeFlagsErr = new IllegalArgumentException(flagsErrMsg)

  protected[this] def getFlags(flags: JdkMap[String, Boolean]): (Boolean, Boolean) =
    (flags get "create", flags get "update")

  def fromJson(json: String, flags: JdkMap[String, Boolean]): String
  def fromXml(xml: String, flags: JdkMap[String, Boolean]): String
  def get(id: String): String
  def all: String
  def remove(id: String): Unit
}
