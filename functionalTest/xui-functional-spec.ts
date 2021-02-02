import {AppPage} from './app.po';
import {$, browser, by, element, logging, protractor} from 'protractor';

describe('xui end to end tests', function() {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should login via OIDC and display welcome message', () => {
    browser.waitForAngularEnabled(false);
    // Should direct to keycloak login.
    browser.get('http://xui-manage-cases:3000');

    browser.wait(protractor.ExpectedConditions.presenceOf($('#username')), 10000);
    // Clear browser logs since a 401 error will be there from page load of logged out user.
    browser.manage().logs().get(logging.Type.BROWSER)

    element(by.id('username')).sendKeys('super@gmail.com');
    element(by.id('password')).sendKeys('p');
    $('button').click()
    browser.wait(protractor.ExpectedConditions.presenceOf($('#wb-jurisdiction')), 10000);
  });


  it('should add a todo', function() {
    browser.get('https://angularjs.org');

    element(by.model('todoList.todoText')).sendKeys('write first protractor test');
    element(by.css('[value="add"]')).click();

    var todoList = element.all(by.repeater('todo in todoList.todos'));
    expect(todoList.count()).toEqual(3);
    expect(todoList.get(2).getText()).toEqual('write first protractor test');

    // You wrote your first test, cross it off the list
    todoList.get(2).element(by.css('input')).click();
    var completedAmount = element.all(by.css('.done-true'));
    expect(completedAmount.count()).toEqual(2);
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
