import {AppPage} from './app.po';
import {$, browser, by, element, ElementFinder, logging, protractor} from 'protractor';
import path from "path";

describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should login via OIDC and display welcome message', () => {
    browser.waitForAngularEnabled(false);
    // Should direct to keycloak login.
    page.navigateTo();

    browser.wait(protractor.ExpectedConditions.presenceOf($('#username')), 10000);
    // Clear browser logs since a 401 error will be there from page load of logged out user.
    browser.manage().logs().get(logging.Type.BROWSER)

    element(by.id('username')).sendKeys('john');
    element(by.id('password')).sendKeys('p');
    element(by.id('kc-login')).click();
    browser.wait(protractor.ExpectedConditions.presenceOf($('#title-header')), 10000);
    expect(page.getTitleText()).toEqual('Manage Cases');
  });

  it('displays the case list', () => {
    browser.waitForAngularEnabled(true);
    page.navigateTo();
    expect(element(by.id('user-name')).getText()).toEqual('John Smith')
    let count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(3);
    count = element.all(by.cssContainingText('.govuk-table__cell', 'Created')).count();
    expect(count).toEqual(2);
  });

  it('case details has available actions', () => {
    browser.get(browser.baseUrl + '/cases/1');
    const count = element.all(by.css('.govuk-select option')).count();
    expect(count).toBeGreaterThanOrEqual(2);
  });

  it('can create a new case', () => {
    page.navigateTo();
    element(by.id('create-case-button')).click();

    element(by.id('claimantReference')).sendKeys('claimant-ref');
    element(by.id('defendantReference')).sendKeys('defendant-ref');
    element(by.id('submit-button')).click();

    // court details
    element(by.id('submit-button')).click();

    // Claimant details
    element(by.id('title')).sendKeys('Prof');
    element(by.id('firstName')).sendKeys('Foo');
    element(by.id('lastName')).sendKeys('Foo');
    element(by.id('dateOfBirth-day')).sendKeys('01');
    element(by.id('dateOfBirth-month')).sendKeys('01');
    element(by.id('dateOfBirth-year')).sendKeys('1980');
    element(by.id('submit-button')).click();

    // Defendant details
    element(by.id('title')).sendKeys('Prof');
    element(by.id('firstName')).sendKeys('Foo');
    element(by.id('lastName')).sendKeys('Foo');
    element(by.id('dateOfBirth-day')).sendKeys('01');
    element(by.id('dateOfBirth-month')).sendKeys('01');
    element(by.id('dateOfBirth-year')).sendKeys('1980');
    element(by.id('submit-button')).click();

    // Test previous button
    element(by.id('previous-button')).click();
    element(by.id('submit-button')).click();

    // Confirm answers
    element(by.id('submit-button')).click();
    expect(browser.getCurrentUrl()).toEndWith('/cases/3/history');
    // Check case appears in case list
    browser.get(browser.baseUrl);
    const count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(4);
  });

  it('displays case history', () => {
    browser.get(browser.baseUrl + '/cases/1');
    const count = element.all(by.css('.hmcts-timeline__item')).count();
    // Case creation and claim added by test data creator.
    expect(count).toEqual(4);
  });

  it('can create a case event', () => {
    browser.get(browser.baseUrl + '/cases/1/create-event?id=CloseCase');
    element(by.id('submit-button')).click();
    // Change answers
    element(by.id('change-0')).click();
    // Confirm answers
    element(by.id('submit-button')).click();

    // Submit
    element(by.id('submit-button')).click();
    expect(browser.getCurrentUrl()).toEndWith('/cases/1/history');
  });

  it('searches cases', () => {
    page.navigateTo();
    element(by.id('case-id-search-input')).sendKeys('1');
    element(by.id('search-button')).click();
    let count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(2); // Header and single row
  });

  it('supports bulk upload of citizens', () => {
    browser.get(browser.baseUrl + '/cases/2/citizens');
    element(by.id('bulk-add-citizens')).click();
    var fileToUpload = 'src/assets/citizens.csv',
      absolutePath = path.resolve(process.cwd(), fileToUpload);
    element(by.id('file')).sendKeys(absolutePath);
    element(by.id('submit-button')).click();
    // Check answers
    element(by.id('submit-button')).click();

    expect(browser.getCurrentUrl()).toEndWith('/cases/2/citizens');
    let count = element.all(by.css('.govuk-table__row')).count();
    // 10 rows should be displayed plus the header
    expect(count).toEqual(11);
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
