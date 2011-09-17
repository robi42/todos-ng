{Application}           = require 'stick'
fs                      = require 'fs'
response                = require 'ringo/jsgi/response'
{respondWith, renderAs} = require './response'
{Todos}                 = com.robert42.todosng
{NoSuchElementException: NoSuchElement} = java.util

log = require('ringo/logging').getLogger module.id

app = exports.app = Application()
app.configure require('./middleware/accepts'),
  require('./middleware/content-type'),
  require('./middleware/body'),
  'route', 'render'

# Configure templating.
app.render.base   = module.resolve fs.join('..', 'templates')
app.render.master = 'page.html'

TODOS_URL = '/todos'
TODO_URL  = "#{TODOS_URL}/:id"
TODO_XML  = 'todo.xml'

# Request/response handling.
app.get '/', ->
  all  = new ScriptableList Todos.all()
  data = (JSON.parse x.toJson() for x in all)
  json = JSON.stringify data
  log.debug 'Data:', json
  app.render 'index.html',
    title: 'Backbone Demo: Todos', all: json

app.get TODOS_URL, (req) ->
  all  = new ScriptableList Todos.all()
  data = (JSON.parse x.toJson() for x in all)
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml 'todos.xml', all: data
  else
    json = JSON.stringify data
    log.debug 'Data:', json
    respondWith.json json

app.post TODOS_URL, (req) ->
  todo =
    if req.contentType.isJson
      Todos.fromJson req.body, create: true
    else if req.contentType.isXml
      Todos.fromXml req.body, create: true
  json = String todo.toJson()
  log.debug 'Data:', json
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml TODO_XML, data: JSON.parse json
  else respondWith.json json

app.get TODO_URL, (req, id) ->
  try
    todo = Todos.get id
    json = String todo.toJson()
    log.debug 'Data:', json
    if req.acceptsXml and not req.acceptsJson
      renderAs.xml TODO_XML, data: JSON.parse json
    else respondWith.json json
  catch err
    if err.javaException instanceof NoSuchElement
      response.notFound "#{TODOS_URL}/#{id}"
    else throw err

app.put TODO_URL, (req) ->
  todo =
    if req.contentType.isJson
      Todos.fromJson req.body, update: true
    else if req.contentType.isXml
      Todos.fromXml req.body, update: true
  json = String todo.toJson()
  log.debug 'Data:', json
  if req.acceptsXml and not req.acceptsJson
    renderAs.xml TODO_XML, data: JSON.parse json
  else respondWith.json json

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
