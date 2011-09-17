{Application} = require 'stick'
fs            = require 'fs'

app = exports.app = Application()
app.configure 'render'

renderApiBase = getRepository(module.resolve(
  fs.join '..', 'templates', 'api'
))

# Response helpers.
module.exports =
  respondWith:
    json: (data) ->
      status: 200
      headers:
        'Content-Type':
          'application/json; charset=utf-8'
      body: [data]

  renderAs:
    xml: (template, data) ->
      app.render template, data,
        base:        renderApiBase
        master:      'master.xml'
        contentType: 'application/xml'
