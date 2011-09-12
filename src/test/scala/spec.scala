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
      todo.text.value  must equalTo("Something.")
      todo.order.value must equalTo(1)
      todo.done.value  must equalTo(false)

      // Updating.
      val jsonForUpdate =
        """{"order":2,"_id":"%s","text":"Something else.","done":true}"""
          .format(todo.id)
      val updatedJson = Todos.updateFromJson(jsonForUpdate)
      updatedJson must equalTo(jsonForUpdate)

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
      todo.text.value  must equalTo("Something.")
      todo.order.value must equalTo(1)
      todo.done.value  must equalTo(false)

      // Updating.
      def makeXmlForUpdate(id: String) =
        <todo id={id}>
          <text type="string">Something else.</text>
          <order type="int">2</order>
          <done type="boolean">true</done>
        </todo>
          .toString
      val jsonForUpdate =
        """{"order":2,"_id":"%s","text":"Something else.","done":true}"""
          .format(todo.id)
      val xmlForUpdate = makeXmlForUpdate(todo.id.toString)
      val updatedJson = Todos.updateFromXml(xmlForUpdate)
      updatedJson must equalTo(jsonForUpdate)

      // Deletion.
      Todos remove todo.id.toString
    }
  }
}
