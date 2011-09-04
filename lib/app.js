var MongoConfig, TODOS_URL, TODO_URL, Todos, app, log, respondWith, _ref;
respondWith = require('./response').respondWith;
_ref = com.robert42.todosng, MongoConfig = _ref.MongoConfig, Todos = _ref.Todos;
app = exports.app = require('./config').app;
log = require('ringo/logging').getLogger(module.id);
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
if (require.main === module) {
  MongoConfig.init();
  require('ringo/httpserver').main(module.id);
}