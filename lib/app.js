var Application, MongoConfig, Props, app, dev, fs, log, prod, prof, publicPath;
Application = require('stick').Application;
fs = require('fs');
Props = net.liftweb.util.Props;
MongoConfig = com.robert42.todosng.MongoConfig;
log = require('ringo/logging').getLogger(module.id);
publicPath = module.resolve(fs.join('..', 'public'));
app = exports.app = Application();
app.configure('basicauth', 'notfound', 'mount');
dev = app.env('dev');
dev.configure('static', 'requestlog', 'error');
dev.requestlog.append = true;
dev.error.javaStack = true;
dev.static(publicPath);
prof = app.env('profiler');
prof.configure('static', 'requestlog', 'profiler', 'error');
prof.requestlog.append = true;
prof.error.javaStack = true;
prof.static(publicPath);
prod = app.env('production');
prod.configure('gzip', 'etag', 'static', 'error');
prod.error.location = false;
prod.static(publicPath);
app.mount('/', require('./actions'));
exports.init = function() {
  log.info('Init MongoDB config.');
  MongoConfig.init();
};
if (require.main === module) {
  require('ringo/httpserver').main(module.id);
}