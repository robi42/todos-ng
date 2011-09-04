var Application, TODOS_URL, TODO_URL, Todos, app, fs, log, respondWith;
Application = require('stick').Application;
fs = require('fs');
respondWith = require('./response').respondWith;
Todos = com.robert42.todosng.Todos;
log = require('ringo/logging').getLogger(module.id);
app = exports.app = Application();
app.configure('params', 'route', 'render');
app.render.base = module.resolve(fs.join('..', 'templates'));
app.render.master = 'page.html';
TODOS_URL = '/todos';
TODO_URL = "" + TODOS_URL + "/:id";
app.get('/', function() {
  return app.render('index.html', {
    title: 'Backbone Demo: Todos',
    all: Todos.all()
  });
});
app.get(TODOS_URL, function() {
  return respondWith.json(Todos.all());
});
app.post(TODOS_URL, function(req) {
  var json;
  json = JSON.stringify(req.postParams);
  return respondWith.json(Todos.create(json));
});
app.get(TODO_URL, function(req, id) {
  return respondWith.json(Todos.get(id));
});
app.put(TODO_URL, function(req, id) {
  var json;
  req.postParams._id = id;
  json = JSON.stringify(req.postParams);
  return respondWith.json(Todos.update(json));
});
app.del(TODO_URL, function(req, id) {
  Todos.remove(id);
  return respondWith.json('');
});