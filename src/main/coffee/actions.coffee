{Application}           = require 'stick'
fs                      = require 'fs'
response                = require 'ringo/jsgi/response'
{respondWith, renderAs} = require './response'
{Todos}                 = com.robert42.todosng
{NoSuchElementException: NoSuchElement} = java.util

log = require('ringo/logging').getLogger(module.id)

app = exports.app = Application()
app.configure require('./middleware/accepts'),
  require('./middleware/content-type'),
  require('./middleware/body'),
  'route', 'render'

# Configure templating.
app.render.base   = module.resolve(fs.join('..', 'templates'))
app.render.master = 'page.html'

TODOS_URL = '/todos'
TODO_URL  = "#{TODOS_URL}/:id"
TODO_XML  = 'todo.xml'

# Request/response handling.
app.get '/', ->
  app.render 'index.html',
    title: 'Backbone Demo: Todos', all: Todos.all()

app.get TODOS_URL, (req) ->
  all = Todos.all()
  log.debug 'Data:', all
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml 'todos.xml', all: JSON.parse(all)
  else respondWith.json all

app.post TODOS_URL, (req) ->
  todo =
    if req.contentType.isJson
      Todos.createFromJson req.body
    else if req.contentType.isXml
      Todos.createFromXml req.body
  log.debug 'Data:', todo
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml TODO_XML, data: JSON.parse(todo)
  else respondWith.json todo

app.get TODO_URL, (req, id) ->
  try
    todo = Todos.get(id)
    log.debug 'Data:', todo
    if req.acceptsXml and not req.acceptsJson
      renderAs.xml TODO_XML, data: JSON.parse(todo)
    else respondWith.json todo
  catch err
    if err.javaException instanceof NoSuchElement
      response.notFound "#{TODOS_URL}/#{id}"
    else throw err

app.put TODO_URL, (req) ->
  todo =
    if req.contentType.isJson
      Todos.updateFromJson req.body
    else if req.contentType.isXml
      Todos.updateFromXml req.body
  log.debug 'Data:', todo
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml TODO_XML, data: JSON.parse(todo)
  else respondWith.json todo

app.del TODO_URL, (req, id) ->
  try
    Todos.remove id
    if req.acceptsXml and not req.acceptsJson
      response.xml ''
    else respondWith.json ''
  catch err
    if err.javaException instanceof NoSuchElement
      response.notFound "#{TODOS_URL}/#{id}"
    else throw err
