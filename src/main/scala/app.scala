package com.robert42.todosng

import TodoXmlData._
import net.liftweb.record.Record
import net.liftweb.json.Serialization.read
import com.foursquare.rogue.Rogue._
import org.bson.types.ObjectId
import xml._
import collection.JavaConversions.asJavaList
import java.util.{Calendar, Map => JdkMap, List => JdkList}

// Persistence layer interface.
object Todos extends Storable {
  def fromJson(json: String, flags: JdkMap[String, Boolean]) = {
    val (doCreate, doUpdate) = getFlags(flags)
    if (doCreate)
      createFromJson(read[TodoCreateJson](json))
    else if (doUpdate)
      updateFromJson(read[TodoUpdateJson](json))
    else throw makeFlagsErr
  }

  private def createFromJson(data: TodoCreateJson) = {
    val record = Todo.createRecord
      .text(data.text)
      .order(data.order)
      .done(data.done)
      .save
    debug("Record: %s" format record)
    record
  }

  private def updateFromJson(data: TodoUpdateJson) = {
    val modify = Todo where (_.id eqs new ObjectId(data._id)) findAndModify
      (_.text setTo data.text) and
      (_.done setTo data.done) and
      (_.modifiedAt setTo Calendar.getInstance)
    val record = modify.updateOne(returnNew = true).get
    debug("Record: %s" format record)
    record
  }

  def fromXml(xml: String, flags: JdkMap[String, Boolean]) = {
    val (doCreate, doUpdate) = getFlags(flags)
    val data = XML loadString xml
    if (doCreate) createFromXml(data)
    else if (doUpdate) updateFromXml(data)
    else throw makeFlagsErr
  }

  private def createFromXml(data: NodeSeq) = {
    val record = Todo.createRecord
      .text((data \ TextElem.toString).text)
      .order((data \ OrderElem.toString).text.toInt)
      .done((data \ DoneElem.toString).text.toBoolean)
      .save
    debug("Record: %s" format record)
    record
  }

  private def updateFromXml(data: NodeSeq) = {
    val id    = (data \ IdAttr.toString).text
    val modify = Todo where (_.id eqs new ObjectId(id)) findAndModify
      (_.text setTo (data \ TextElem.toString).text) and
      (_.done setTo (data \ DoneElem.toString).text.toBoolean) and
      (_.modifiedAt setTo Calendar.getInstance)
    val record = modify.updateOne(returnNew = true).get
    debug("Record: %s" format record)
    record
  }

  def get(id: String) = {
    val query  = Todo where (_.id eqs new ObjectId(id)) get
    val record = query.get
    debug("Record: %s" format record)
    record
  }

  def all = {
    val all: JdkList[Record[_]] = Todo.findAll
    debug("Records: %s" format all)
    all
  }

  def remove(id: String) = {
    val query  = Todo where (_.id eqs new ObjectId(id)) get
    val record = query.get
    debug("Record: %s" format record)
    record.delete_!
  }
}
