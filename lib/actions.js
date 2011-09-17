var Application, NoSuchElement, TODOS_URL, TODO_URL, TODO_XML, Todos, app, fs, log, renderAs, respondWith, response, _ref;
Application = require('stick').Application;
fs = require('fs');
response = require('ringo/jsgi/response');
_ref = require('./response'), respondWith = _ref.respondWith, renderAs = _ref.renderAs;
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
  var all, data, json, x;
  all = new ScriptableList(Todos.all());
  data = (function() {
    var _i, _len, _results;
    _results = [];
    for (_i = 0, _len = all.length; _i < _len; _i++) {
      x = all[_i];
      _results.push(JSON.parse(x.toJson()));
    }
    return _results;
  })();
  json = JSON.stringify(data);
  log.debug('Data:', json);
  return app.render('index.html', {
    title: 'Backbone Demo: Todos',
    all: json
  });
});
app.get(TODOS_URL, function(req) {
  var all, data, json, x;
  all = new ScriptableList(Todos.all());
  data = (function() {
    var _i, _len, _results;
    _results = [];
    for (_i = 0, _len = all.length; _i < _len; _i++) {
      x = all[_i];
      _results.push(JSON.parse(x.toJson()));
    }
    return _results;
  })();
  if (req.acceptsXml && !req.acceptsJson) {
    return renderAs.xml('todos.xml', {
      all: data
    });
  } else {
    json = JSON.stringify(data);
    log.debug('Data:', json);
    return respondWith.json(json);
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
    return renderAs.xml(TODO_XML, {
      data: JSON.parse(json)
    });
  } else {
    return respondWith.json(json);
  }
});
app.get(TODO_URL, function(req, id) {
  var json, todo;
  try {
    todo = Todos.get(id);
    json = String(todo.toJson());
    log.debug('Data:', json);
    if (req.acceptsXml && !req.acceptsJson) {
      return renderAs.xml(TODO_XML, {
        data: JSON.parse(json)
      });
    } else {
      return respondWith.json(json);
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
    return renderAs.xml(TODO_XML, {
      data: JSON.parse(json)
    });
  } else {
    return respondWith.json(json);
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
    if (err.javaException instanceof NoSuchElement) {
      return response.notFound("" + TODOS_URL + "/" + id);
    } else {
      throw err;
    }
  }
});