{Application}                  = require 'stick'
fs                             = require 'fs'
response                       = require 'ringo/jsgi/response'
{respondWithJson, renderAsXml} = require './response'
{Todos}                        = com.robert42.todosng
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
  json = Todos.allAsJson()
  log.debug 'Data:', json
  app.render 'index.html',
    title: 'Backbone Demo: Todos', all: json

app.get TODOS_URL, (req) ->
  json = Todos.allAsJson()
  log.debug 'Data:', json
  if req.acceptsXml and not req.acceptsJson
    data = JSON.parse json
    renderAsXml 'todos.xml', all: data
  else
    respondWithJson json

app.post TODOS_URL, (req) ->
  todo =
    if req.contentType.isJson
      Todos.fromJson req.body, create: true
    else if req.contentType.isXml
      Todos.fromXml req.body, create: true
  json = String todo.toJson()
  log.debug 'Data:', json
  if req.acceptsXml and not req.acceptsJson
    renderAsXml TODO_XML, data: JSON.parse json
  else respondWithJson json

app.get TODO_URL, (req, id) ->
  try
    todo = Todos.get id
    json = String todo.toJson()
    log.debug 'Data:', json
    if req.acceptsXml and not req.acceptsJson
      renderAsXml TODO_XML, data: JSON.parse json
    else respondWithJson json
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
    renderAsXml TODO_XML, data: JSON.parse json
  else respondWithJson json

app.del TODO_URL, (req, id) ->
  try
    Todos.remove id
    if req.acceptsXml and not req.acceptsJson
      response.xml ''
    else respondWithJson ''
  catch err
    if err.javaException instanceof NoSuchElement
      response.notFound "#{TODOS_URL}/#{id}"
    else throw err
