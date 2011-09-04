{Application} = require 'stick'
fs            = require 'fs'
{MongoConfig} = com.robert42.todosng

log        = require('ringo/logging').getLogger(module.id)
publicPath = module.resolve(fs.join('..', 'public'))

# Set up application.
app = exports.app = Application()
app.configure 'notfound', 'mount'

# Configure dev env.
dev = app.env('dev')
dev.configure 'static', 'requestlog', 'error'
dev.requestlog.append = true
dev.static publicPath

# Configure profiling env.
prof = app.env('profiler')
prof.configure 'static', 'requestlog', 'profiler', 'error'
prof.requestlog.append = true
prof.static publicPath

# Configure production env.
prod = app.env('production')
prod.configure 'gzip', 'etag', 'static', 'error'
prod.error.location = false
prod.static publicPath

# Mount actions of app.
app.mount '/', require('./actions')


# Script to run app from command line.
if require.main is module
  MongoConfig.init()
  require('ringo/httpserver').main module.id
