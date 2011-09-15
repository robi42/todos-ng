import com.robert42.todosng.{MongoConfig, Todo, Todos}
import org.junit.Test
import com.codahale.simplespec.Spec
import com.foursquare.rogue.Rogue._

class AppSpec extends Spec {
  MongoConfig.init

  class `A MongoDB document` {
    @Test def `can be CRUDed from JSON.` = {
      // Creation.
      val json = """{"text": "Something.", "order": 1, "done": false}"""
      Todos createFromJson json

      // Querying.
      val query = Todo where (_.text eqs "Something.") get
      val todo  = query.get
      assertTodo(todo)

      // Updating.
      val jsonForUpdate =
        """{"text":"Something else.","_id":"%s","order":2,"createdAt":%d,"done":true}"""
          .format(todo.id, todo.createdAt.value.getTimeInMillis)
      val updatedJson = Todos.updateFromJson(jsonForUpdate)
      assertUpdatedJson(updatedJson, jsonForUpdate)

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
      Todos createFromXml xml

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
      val updatedJson = Todos.updateFromXml(xmlForUpdate)
      assertUpdatedJson(updatedJson, jsonForUpdate)

      // Deletion.
      Todos remove todo.id.toString
    }

    def assertTodo(todo: Todo) = {
      todo.text.value  must equalTo("Something.")
      todo.order.value must equalTo(1)
      todo.done.value  must equalTo(false)
    }

    def assertUpdatedJson(updatedJson: String, jsonForUpdate: String) = {
      updatedJson must contain("modifiedAt")
      val jsonWithoutModifiedAt = updatedJson
        .replaceFirst(""""modifiedAt":\d+,""", "")
      jsonWithoutModifiedAt must equalTo(jsonForUpdate)
    }
  }
}
