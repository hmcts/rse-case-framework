// @ts-check
// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/lib/config.ts

const { SpecReporter, StacktraceOption } = require('jasmine-spec-reporter');

/**
 * @type { import("protractor").Config }
 */
exports.config = {
  allScriptsTimeout: 20000,
  resultJsonOutputFile:'./build/functional/testResults.json',
  specs: [
    './src/**/*.functional-spec.ts'
  ],
  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: [ "--headless", "--disable-gpu", "--disable-dev-shm-usage", "--no-sandbox", "--remote-debugging-port=9222", "--remote-debugging-address=0.0.0.0" ]
    }
  },
  directConnect: true,
  baseUrl: 'http://localhost:8080/',
  framework: 'jasmine',
  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 30000,
    print: function() {}
  },
  async onPrepare() {
    var fs = require('fs')
    fs.mkdirSync('build/functional', { recursive: true })
    require('jasmine-expect');
    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.json')
    });
    jasmine.getEnv().addReporter(new SpecReporter({
      spec: {
        displayStacktrace: StacktraceOption.PRETTY
      }
    }));
    // TODO: check backend availability.
    await new Promise(resolve => setTimeout(resolve, 10000));
  }
};
