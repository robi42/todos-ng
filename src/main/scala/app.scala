package com.robert42.todosng

import net.liftweb.json._
import net.liftweb.json.Serialization.read
import com.foursquare.rogue.Rogue._
import org.bson.types.ObjectId
import scala.xml._
import java.util.Calendar
import java.util.{Map => JdkMap}
import java.lang.IllegalArgumentException

// Persistence layer interface.
object Todos extends Storable {
  implicit val formats = Serialization.formats(NoTypeHints)

  val FLAGS_ERROR = "Either `create` or `update` must be `true`."

  val TEXT  = "text"
  val ORDER = "order"
  val DONE  = "done"

  def fromJson(json: String, flags: JdkMap[String, Boolean]) = {
    val doCreate = flags.get("create")
    val doUpdate = flags.get("update")
    if (doCreate)
      createFromJson(read[TodoData](json))
    else if (doUpdate)
      updateFromJson(read[TodoAllData](json))
    else throw new IllegalArgumentException(FLAGS_ERROR)
  }

  private def createFromJson(data: TodoData) = {
    val record = Todo.createRecord
      .text(data.text)
      .order(data.order)
      .done(data.done)
      .save
    compact(render(record.asJValue))
  }

  private def updateFromJson(data: TodoAllData) = {
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
    val doCreate = flags.get("create")
    val doUpdate = flags.get("update")
    val data     = XML.loadString(xml)
    if (doCreate) createFromXml(data)
    else if (doUpdate) updateFromXml(data)
    else throw new IllegalArgumentException(FLAGS_ERROR)
  }

  private def createFromXml(data: NodeSeq) = {
    val record = Todo.createRecord
      .text((data \ TEXT).text)
      .order((data \ ORDER).text.toInt)
      .done((data \ DONE).text.toBoolean)
      .save
    compact(render(record.asJValue))
  }

  private def updateFromXml(data: NodeSeq) = {
    val id    = (data \ "@id").text
    val query = Todo where (_.id eqs new ObjectId(id))
    val modification = query modify
      (_.text setTo (data \ TEXT).text) and
      (_.order setTo (data \ ORDER).text.toInt) and
      (_.done setTo (data \ DONE).text.toBoolean) and
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
