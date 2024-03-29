package com.robert42.todosng

import net.liftweb.util.Props
import net.liftweb.mongodb._
import com.mongodb.{Mongo, ServerAddress}

// Mongo config setup.
object MongoConfig {
  def init = {
    val server = new ServerAddress(
       Props.get("mongo.host", "127.0.0.1"),
       Props.getInt("mongo.port", 27017)
    )
    val name = Props.get("mongo.db", "todosng")

    Props.mode match {
      case Props.RunModes.Production => {
        val pw = Props.get("mongo.pw", "")
        MongoDB.defineDbAuth(
          DefaultMongoIdentifier, new Mongo(server), name, name, pw
        )
      }
      case _ => MongoDB.defineDb(
        DefaultMongoIdentifier, new Mongo(server), name
      )
    }
  }
}
