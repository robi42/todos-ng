{respondWith}        = require './response'
{MongoConfig, Todos} = com.robert42.todosng
app = exports.app    = require('./config').app

TODOS_URL = '/todos'
TODO_URL  = "#{TODOS_URL}/:id"

# Request/response handling.
app.get '/', (req) ->
  app.render 'index.html', title: 'Backbone Demo: Todos'

app.get TODOS_URL, ->
  respondWith.json Todos.all()

app.post TODOS_URL, (req) ->
  json = JSON.stringify(req.postParams)
  respondWith.json Todos.create(json)

app.get TODO_URL, (req, id) ->
  respondWith.json Todos.get(id)

app.put TODO_URL, (req, id) ->
  req.postParams._id = id
  json = JSON.stringify(req.postParams)
  respondWith.json Todos.update(json)

app.del TODO_URL, (req, id) ->
  Todos.remove id
  respondWith.json ''


# Script to run app from command line.
if require.main is module
  MongoConfig.init()
  require('ringo/httpserver').main module.id
