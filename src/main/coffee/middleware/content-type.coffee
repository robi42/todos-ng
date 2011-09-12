# Content type header Stick middleware.
exports.middleware = (next, app) ->
  (req) ->
    contentType = req.headers['content-type']
    req.contentType = {}
    Object.defineProperties req.contentType,
      isJson:
        get: ->
          /application\/json/.test contentType
        configurable: true
      isXml:
        get: ->
          /application\/xml/.test contentType
        configurable: true
    next req
