exports.middleware = function(next, app) {
  return function(req) {
    var contentType;
    contentType = req.headers['content-type'];
    req.contentType = {};
    Object.defineProperties(req.contentType, {
      isJson: {
        get: function() {
          return /application\/json/.test(contentType);
        },
        configurable: true
      },
      isXml: {
        get: function() {
          return /application\/xml/.test(contentType);
        },
        configurable: true
      }
    });
    return next(req);
  };
};