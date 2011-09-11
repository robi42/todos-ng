exports.middleware = function(next, app) {
  return function(req) {
    var accepts;
    accepts = req.headers.accept;
    Object.defineProperties(req, {
      acceptsJson: {
        get: function() {
          return /application\/json/.test(accepts);
        },
        configurable: true
      },
      acceptsXml: {
        get: function() {
          return /application\/xml/.test(accepts);
        },
        configurable: true
      },
      acceptsHtml: {
        get: function() {
          return /text\/html/.test(accepts);
        },
        configurable: true
      }
    });
    return next(req);
  };
};