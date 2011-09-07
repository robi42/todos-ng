# Request body Stick middleware.
exports.middleware = (next, app) ->
  (req) ->
    Object.defineProperty req, 'body',
      get: ->
        body ? req.input.read().decodeToString('utf8')
      configurable: true
    next req
