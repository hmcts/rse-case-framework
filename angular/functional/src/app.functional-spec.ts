import {AppPage} from './app.po';
import {browser, by, element, logging, protractor} from 'protractor';

describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getTitleText()).toEqual('Unspecified Claims');
  });

  it('displays the case list', () => {
    page.navigateTo();
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
    element(by.id('submit-button')).click();

    // Defendant details
    element(by.id('title')).sendKeys('Prof');
    element(by.id('firstName')).sendKeys('Foo');
    element(by.id('lastName')).sendKeys('Foo');
    element(by.id('submit-button')).click();

    // Test previous button
    element(by.id('previous-button')).click();
    element(by.id('submit-button')).click();

    // Confirm answers
    element(by.id('submit-button')).click();
    expect(browser.getCurrentUrl()).toEndWith('/cases/3');
    // Check case appears in case list
    browser.get(browser.baseUrl);
    const count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(4);
  });

  it('displays case history', () => {
    browser.get(browser.baseUrl + '/cases/1');
    const count = element.all(by.css('.hmcts-timeline__item')).count();
    expect(count).toEqual(1);
  });

  it('can create a case event', () => {
    browser.get(browser.baseUrl + '/cases/1/create-event?id=AddNotes');
    element(by.id('submit-button')).click();
    // Change answers
    element(by.id('change-0')).click();
    // Confirm answers
    element(by.id('submit-button')).click();

    // Submit
    element(by.id('submit-button')).click();
    expect(browser.getCurrentUrl()).toEndWith('/cases/1');
  });

  it('searches cases', () => {
    page.navigateTo();
    element(by.id('case-id-search-input')).sendKeys('1');
    element(by.id('search-button')).click();
    let count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(2); // Header and single row
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
