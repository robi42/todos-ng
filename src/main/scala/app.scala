package com.robert42.todosng

import Flags._
import TodoXmlData._
import net.liftweb.json._
import net.liftweb.json.Serialization.read
import com.foursquare.rogue.Rogue._
import org.bson.types.ObjectId
import xml._
import java.util.{Calendar, Map => JdkMap}

// Persistence layer interface.
object Todos extends Storable {
  def fromJson(json: String, flags: JdkMap[String, Boolean]) = {
    val doCreate = flags get Create.toString
    val doUpdate = flags get Update.toString
    if (doCreate)
      createFromJson(read[TodoJsonData](json))
    else if (doUpdate)
      updateFromJson(read[TodoAllJsonData](json))
    else throw makeFlagsErr
  }

  private def createFromJson(data: TodoJsonData) = {
    val record = Todo.createRecord
      .text(data.text)
      .order(data.order)
      .done(data.done)
      .save
    compact(render(record.asJValue))
  }

  private def updateFromJson(data: TodoAllJsonData) = {
    val query = Todo where (_.id eqs new ObjectId(data._id))
    val modification = query modify
      (_.text setTo data.text) and
      (_.order setTo data.order) and
      (_.done setTo data.done) and
      (_.modifiedAt setTo Calendar.getInstance)
    modification.updateOne
    val record = query.get.get
    compact(render(record.asJValue))
  }

  def fromXml(xml: String, flags: JdkMap[String, Boolean]) = {
    val doCreate = flags get Create.toString
    val doUpdate = flags get Update.toString
    val data     = XML loadString xml
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
    compact(render(record.asJValue))
  }

  private def updateFromXml(data: NodeSeq) = {
    val id    = (data \ IdAttr.toString).text
    val query = Todo where (_.id eqs new ObjectId(id))
    val modification = query modify
      (_.text setTo (data \ TextElem.toString).text) and
      (_.order setTo (data \ OrderElem.toString).text.toInt) and
      (_.done setTo (data \ DoneElem.toString).text.toBoolean) and
      (_.modifiedAt setTo Calendar.getInstance)
    modification.updateOne
    val record = query.get.get
    compact(render(record.asJValue))
  }

  def get(id: String) = {
    val query  = Todo where (_.id eqs new ObjectId(id)) get
    val record = query.get
    compact(render(record.asJValue))
  }

  def all = {
    val all = Todo.findAll.map(_.asJValue)
    compact(render(JArray(all)))
  }

  def remove(id: String) = {
    val query  = Todo where (_.id eqs new ObjectId(id)) get
    val record = query.get
    record.delete_!
  }
}
