{Application} = require 'stick'
fs            = require 'fs'

publicPath    = module.resolve(fs.join('..', 'public'))
templatesPath = module.resolve(fs.join('..', 'templates'))

# Set up application.
app = exports.app = Application()
app.configure 'notfound', 'error', 'static',
  'params', 'route', 'render'
app.static publicPath
app.render.base   = templatesPath
app.render.master = 'page.html'

# Configure production env.
prod = app.env('production')
prod.configure 'notfound', 'error', 'gzip', 'etag',
  'static', 'params', 'route', 'render'
prod.error.location = false
