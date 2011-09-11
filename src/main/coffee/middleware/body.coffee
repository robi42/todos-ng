# Request body Stick middleware.
exports.middleware = (next, app) ->
  (req) ->
    Object.defineProperty req, 'body',
      get: ->
        body ? @input.read().decodeToString('utf8')
      configurable: true
    next req
