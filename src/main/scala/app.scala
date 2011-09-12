package com.robert42.todosng

import net.liftweb.json._
import net.liftweb.json.Serialization.read
import com.foursquare.rogue.Rogue._
import org.bson.types.ObjectId
import scala.xml._

// Persistence layer interface.
object Todos extends Storable {
  implicit val formats = Serialization.formats(NoTypeHints)

  val TEXT  = "text"
  val ORDER = "order"
  val DONE  = "done"

  def createFromJson(json: String) = {
    val data   = read[TodoData](json)
    val record = Todo.createRecord
      .text(data.text)
      .order(data.order)
      .done(data.done)
      .save
    compact(render(record.asJValue))
  }

  def createFromXml(xml: String) = {
    val data   = XML.loadString(xml)
    val record = Todo.createRecord
      .text((data \ TEXT).text)
      .order((data \ ORDER).text.toInt)
      .done((data \ DONE).text.toBoolean)
      .save
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

  def updateFromJson(json: String) = {
    val data  = read[TodoAllData](json)
    val query = Todo where (_.id eqs new ObjectId(data._id))
    val modification = query modify
      (_.text setTo data.text) and
      (_.order setTo data.order) and
      (_.done setTo data.done)
    modification.updateOne
    val record = query.get.get
    compact(render(record.asJValue))
  }

  def updateFromXml(xml: String) = {
    val data  = XML.loadString(xml)
    val id    = (data \ "@id").text
    val query = Todo where (_.id eqs new ObjectId(id))
    val modification = query modify
      (_.text setTo (data \ TEXT).text) and
      (_.order setTo (data \ ORDER).text.toInt) and
      (_.done setTo (data \ DONE).text.toBoolean)
    modification.updateOne
    val record = query.get.get
    compact(render(record.asJValue))
  }

  def remove(id: String) = {
    val query  = Todo where (_.id eqs new ObjectId(id)) get
    val record = query.get
    record.delete_!
  }
}
