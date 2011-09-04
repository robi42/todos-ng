{Application} = require 'stick'
fs            = require 'fs'

publicPath    = module.resolve(fs.join('..', 'public'))
templatesPath = module.resolve(fs.join('..', 'templates'))

# Set up application.
app = exports.app = Application()
app.configure 'notfound', 'params', 'route', 'render'

# Configure dev env.
dev = app.env('dev')
dev.configure 'static', 'error'
dev.static publicPath

# Configure production env.
prod = app.env('production')
prod.configure 'gzip', 'etag', 'static', 'error'
prod.error.location = false
prod.static publicPath

# Configure templating.
app.render.base   = templatesPath
app.render.master = 'page.html'
