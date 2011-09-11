# Accept header Stick middleware.
exports.middleware = (next, app) ->
  (req) ->
    accepts = req.headers.accept
    Object.defineProperties req,
      acceptsJson:
        get: ->
          /application\/json/.test accepts
        configurable: true
      acceptsXml:
        get: ->
          /application\/xml/.test accepts
        configurable: true
      acceptsHtml:
        get: ->
          /text\/html/.test accepts
        configurable: true
    next req
