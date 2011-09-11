var Application, app, fs, renderBase;
Application = require('stick').Application;
fs = require('fs');
app = exports.app = Application();
app.configure('render');
renderBase = getRepository(module.resolve(fs.join('..', 'templates', 'api')));
module.exports = {
  respondWith: {
    json: function(data) {
      return {
        status: 200,
        headers: {
          'Content-Type': 'application/json; charset=utf-8'
        },
        body: [data]
      };
    }
  },
  renderAs: {
    xml: function(template, data) {
      return app.render(template, data, {
        base: renderBase,
        master: 'master.xml',
        contentType: 'application/xml'
      });
    }
  }
};