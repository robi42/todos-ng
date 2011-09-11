var Application, NoSuchElementException, TODOS_URL, TODO_URL, TODO_XML, Todos, app, fs, log, renderAs, respondWith, response, _ref;
Application = require('stick').Application;
fs = require('fs');
response = require('ringo/jsgi/response');
_ref = require('./response'), respondWith = _ref.respondWith, renderAs = _ref.renderAs;
NoSuchElementException = java.util.NoSuchElementException;
Todos = com.robert42.todosng.Todos;
log = require('ringo/logging').getLogger(module.id);
app = exports.app = Application();
app.configure(require('./middleware/accepts'), require('./middleware/body'), 'route', 'render');
app.render.base = module.resolve(fs.join('..', 'templates'));
app.render.master = 'page.html';
TODOS_URL = '/todos';
TODO_URL = "" + TODOS_URL + "/:id";
TODO_XML = 'todo.xml';
app.get('/', function() {
  return app.render('index.html', {
    title: 'Backbone Demo: Todos',
    all: Todos.all()
  });
});
app.get(TODOS_URL, function(req) {
  var all;
  all = Todos.all();
  log.debug('Data:', all);
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAs.xml('todos.xml', {
      all: JSON.parse(all)
    });
  } else {
    return respondWith.json(all);
  }
});
app.post(TODOS_URL, function(req) {
  var todo;
  todo = Todos.create(req.body);
  log.debug('Data:', todo);
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAs.xml(TODO_XML, {
      data: JSON.parse(todo)
    });
  } else {
    return respondWith.json(todo);
  }
});
app.get(TODO_URL, function(req, id) {
  var todo;
  try {
    todo = Todos.get(id);
    log.debug('Data:', todo);
    if (req.acceptsXml && !req.acceptsJson) {
      return renderAs.xml(TODO_XML, {
        data: JSON.parse(todo)
      });
    } else {
      return respondWith.json(todo);
    }
  } catch (err) {
    if (err.javaException instanceof NoSuchElementException) {
      return response.notFound("" + TODOS_URL + "/" + id);
    } else {
      throw err;
    }
  }
});
app.put(TODO_URL, function(req) {
  var todo;
  todo = Todos.update(req.body);
  log.debug('Data:', todo);
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAs.xml(TODO_XML, {
      data: JSON.parse(todo)
    });
  } else {
    return respondWith.json(todo);
  }
});
app.del(TODO_URL, function(req, id) {
  try {
    Todos.remove(id);
    if (req.acceptsXml && !req.acceptsJson) {
      return response.xml('');
    } else {
      return respondWith.json('');
    }
  } catch (err) {
    if (err.javaException instanceof NoSuchElementException) {
      return response.notFound("" + TODOS_URL + "/" + id);
    } else {
      throw err;
    }
  }
});