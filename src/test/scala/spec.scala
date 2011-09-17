import com.robert42.todosng.{MongoConfig, Todo, Todos}
import org.junit.Test
import com.codahale.simplespec.Spec
import com.foursquare.rogue.Rogue._
import collection.mutable.{Map => MMap}
import collection.JavaConversions._
import java.util.Calendar

class AppSpec extends Spec {
  MongoConfig.init

  class `A MongoDB document` {
    @Test def `can be CRUDed from JSON.` = {
      // Creation.
      val json = """{"text": "Something.", "order": 1, "done": false}"""
      Todos.fromJson(json, MMap("create" -> true))

      // Querying.
      val query = Todo where (_.text eqs "Something.") get
      val todo  = query.get
      assertTodo(todo)

      // Updating.
      val jsonForUpdate =
        """{"text":"Something else.","_id":"%s","order":2,"createdAt":%d,"done":true}"""
          .format(todo.id, todo.createdAt.value.getTimeInMillis)
      val updatedTodo = Todos.fromJson(jsonForUpdate, MMap("update" -> true))
      assertTodo(updatedTodo, updated = true)

      // Deletion.
      Todos remove todo.id.toString
    }

    @Test def `can be CRUDed from XML.` = {
      // Creation.
      val xml = <todo>
                  <text type="string">Something.</text>
                  <order type="int">1</order>
                  <done type="boolean">false</done>
                </todo>
                  .toString
      Todos.fromXml(xml, MMap("create" -> true))

      // Querying.
      val query = Todo where (_.text eqs "Something.") get
      val todo  = query.get
      assertTodo(todo)

      // Updating.
      def makeXmlForUpdate(id: String) =
        <todo id={id}>
          <text type="string">Something else.</text>
          <order type="int">2</order>
          <done type="boolean">true</done>
        </todo>
          .toString
      val xmlForUpdate  = makeXmlForUpdate(todo.id.toString)
      val jsonForUpdate =
        """{"text":"Something else.","_id":"%s","order":2,"createdAt":%d,"done":true}"""
          .format(todo.id, todo.createdAt.value.getTimeInMillis)
      val updatedTodo = Todos.fromXml(xmlForUpdate, MMap("update" -> true))
      assertTodo(updatedTodo, updated = true)

      // Deletion.
      Todos remove todo.id.toString
    }

    def assertTodo(todo: Todo, updated: Boolean = false) = {
      if (!updated) {
        todo.text.value       must equalTo("Something.")
        todo.order.value      must equalTo(1)
        todo.done.value       must equalTo(false)
        todo.modifiedAt.value must be(None)
      } else {
        todo.text.value       must equalTo("Something else.")
        todo.order.value      must equalTo(2)
        todo.done.value       must equalTo(true)
        todo.modifiedAt.value must not be(None)
      }
      todo.createdAt.value    must not equalTo(null)
    }
  }
}
