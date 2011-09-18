var Application, NoSuchElement, TODOS_URL, TODO_URL, TODO_XML, Todos, app, fs, log, renderAsXml, respondWithJson, response, _ref;
Application = require('stick').Application;
fs = require('fs');
response = require('ringo/jsgi/response');
_ref = require('./response'), respondWithJson = _ref.respondWithJson, renderAsXml = _ref.renderAsXml;
Todos = com.robert42.todosng.Todos;
NoSuchElement = java.util.NoSuchElementException;
log = require('ringo/logging').getLogger(module.id);
app = exports.app = Application();
app.configure(require('./middleware/accepts'), require('./middleware/content-type'), require('./middleware/body'), 'route', 'render');
app.render.base = module.resolve(fs.join('..', 'templates'));
app.render.master = 'page.html';
TODOS_URL = '/todos';
TODO_URL = "" + TODOS_URL + "/:id";
TODO_XML = 'todo.xml';
app.get('/', function() {
  var json;
  json = Todos.allAsJson();
  log.debug('Data:', json);
  return app.render('index.html', {
    title: 'Backbone Demo: Todos',
    all: json
  });
});
app.get(TODOS_URL, function(req) {
  var data, json;
  json = Todos.allAsJson();
  log.debug('Data:', json);
  if (req.acceptsXml && !req.acceptsJson) {
    data = JSON.parse(json);
    return renderAsXml('todos.xml', {
      all: data
    });
  } else {
    return respondWithJson(json);
  }
});
app.post(TODOS_URL, function(req) {
  var json, todo;
  todo = req.contentType.isJson ? Todos.fromJson(req.body, {
    create: true
  }) : req.contentType.isXml ? Todos.fromXml(req.body, {
    create: true
  }) : void 0;
  json = String(todo.toJson());
  log.debug('Data:', json);
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAsXml(TODO_XML, {
      data: JSON.parse(json)
    });
  } else {
    return respondWithJson(json);
  }
});
app.get(TODO_URL, function(req, id) {
  var json, todo;
  try {
    todo = Todos.get(id);
    json = String(todo.toJson());
    log.debug('Data:', json);
    if (req.acceptsXml && !req.acceptsJson) {
      return renderAsXml(TODO_XML, {
        data: JSON.parse(json)
      });
    } else {
      return respondWithJson(json);
    }
  } catch (err) {
    if (err.javaException instanceof NoSuchElement) {
      return response.notFound("" + TODOS_URL + "/" + id);
    } else {
      throw err;
    }
  }
});
app.put(TODO_URL, function(req) {
  var json, todo;
  todo = req.contentType.isJson ? Todos.fromJson(req.body, {
    update: true
  }) : req.contentType.isXml ? Todos.fromXml(req.body, {
    update: true
  }) : void 0;
  json = String(todo.toJson());
  log.debug('Data:', json);
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAsXml(TODO_XML, {
      data: JSON.parse(json)
    });
  } else {
    return respondWithJson(json);
  }
});
app.del(TODO_URL, function(req, id) {
  try {
    Todos.remove(id);
    if (req.acceptsXml && !req.acceptsJson) {
      return response.xml('');
    } else {
      return respondWithJson('');
    }
  } catch (err) {
    if (err.javaException instanceof NoSuchElement) {
      return response.notFound("" + TODOS_URL + "/" + id);
    } else {
      throw err;
    }
  }
});