{Application} = require 'stick'
fs            = require 'fs'
{respondWith} = require './response'
{Todos}       = com.robert42.todosng

log = require('ringo/logging').getLogger(module.id)

app = exports.app = Application()
app.configure require('./body'), 'route', 'render'

# Configure templating.
app.render.base   = module.resolve(fs.join('..', 'templates'))
app.render.master = 'page.html'

TODOS_URL = '/todos'
TODO_URL  = "#{TODOS_URL}/:id"

# Request/response handling.
app.get '/', ->
  app.render 'index.html',
    title: 'Backbone Demo: Todos', all: Todos.all()

app.get TODOS_URL, ->
  respondWith.json Todos.all()

app.post TODOS_URL, (req) ->
  respondWith.json Todos.create(req.body)

app.get TODO_URL, (req, id) ->
  respondWith.json Todos.get(id)

app.put TODO_URL, (req) ->
  respondWith.json Todos.update(req.body)

app.del TODO_URL, (req, id) ->
  Todos.remove id
  respondWith.json ''
