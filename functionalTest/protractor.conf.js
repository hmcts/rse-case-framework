const { exec } = require("child_process");
const { SpecReporter, StacktraceOption } = require('jasmine-spec-reporter');
var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');
var reporter = new HtmlScreenshotReporter({
  dest: 'build/functional/screenshots',
  filename: 'failure-report.html'
});

exports.config = {
  directConnect: true,
  framework: 'jasmine',
  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: [ "--headless", "--disable-gpu", "--disable-dev-shm-usage", "--no-sandbox", "--remote-debugging-port=9222", "--remote-debugging-address=0.0.0.0" ]
    }
  },
  specs: [
    'dist/*spec.js'
  ],
  // Setup the report before any tests start
  beforeLaunch: function() {
    return new Promise(function(resolve){
      reporter.beforeLaunch(resolve);
    });
  },
  // Close the report after all tests finish
  afterLaunch: function(exitCode) {
    return new Promise(function(resolve){
      reporter.afterLaunch(resolve.bind(this, exitCode));
    });
  },
  async onPrepare() {
    const fs = require('fs');
    fs.mkdirSync('build/functional', { recursive: true })
    require('jasmine-expect');
    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.json')
    });
    jasmine.getEnv().addReporter(reporter);
    jasmine.getEnv().addReporter(new SpecReporter({
      spec: {
        displayStacktrace: StacktraceOption.PRETTY
      }
    }));


    await browser.waitForAngularEnabled(false);
    while (true) {
      try {
        console.log("Waiting for login screen...")
        await browser.get('http://xui-manage-cases:3000');
        await browser.sleep(2000)
        await browser.driver.findElement(by.id('username'));
        console.log("login page loaded")
        break;
      } catch (error) {
        console.log("Login page not ready")
        await browser.sleep(1000)
      }
    }
    exec("sh create-user.sh", (error, stdout, stderr) => {
      if (error) {
        console.error(`error: ${error.message}`);
        return;
      }
      if (stderr) {
        console.error(`stderr: ${stderr}`);
        return;
      }
      console.log(`stdout: ${stdout}`);
    });
    await browser.sleep(1000)
    await browser.waitForAngularEnabled(true);
  }
};
