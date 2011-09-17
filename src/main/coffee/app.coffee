{Application} = require 'stick'
fs            = require 'fs'
{Props}       = net.liftweb.util
{MongoConfig} = com.robert42.todosng

log        = require('ringo/logging').getLogger module.id
publicPath = module.resolve fs.join('..', 'public')

# Set up application.
app = exports.app = Application()
app.configure 'basicauth', 'notfound', 'mount'

# Configure dev env.
dev = app.env('dev')
dev.configure 'static', 'requestlog', 'error'
dev.requestlog.append = true
dev.error.javaStack   = true
dev.static publicPath

# Configure profiling env.
prof = app.env('profiler')
prof.configure 'static', 'requestlog', 'profiler', 'error'
prof.requestlog.append = true
prof.error.javaStack   = true
prof.static publicPath

# Configure production env.
prod = app.env('production')
prod.configure 'gzip', 'etag', 'static', 'error'
prod.error.location = false
prod.static publicPath

# Configure auth.
# app.basicauth '/',
#   Props.get('basicauth.user').openTheBox(),
#   Props.get('basicauth.sha1').openTheBox()

# Mount actions of app.
app.mount '/', require('./actions')

# Init MongoDB config on server init.
exports.init = ->
  log.info 'Init MongoDB config.'
  MongoConfig.init()
  return

# Script to run app from command line.
if require.main is module
  require('ringo/httpserver').main module.id
