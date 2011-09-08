exports.middleware = function(next, app) {
  return function(req) {
    Object.defineProperty(req, 'body', {
      get: function() {
        return typeof body !== "undefined" && body !== null ? body : this.input.read().decodeToString('utf8');
      },
      configurable: true
    });
    return next(req);
  };
};