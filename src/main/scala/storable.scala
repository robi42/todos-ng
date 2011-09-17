package com.robert42.todosng

import net.liftweb.common.Logger
import net.liftweb.record.Record
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
  def remove(id: String): Unit
}
