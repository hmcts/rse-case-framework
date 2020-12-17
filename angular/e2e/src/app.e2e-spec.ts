import { AppPage } from './app.po';
import { browser, logging } from 'protractor';
import {by, element} from 'protractor';

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
    var count = element.all(by.css('.govuk-table__row')).count();
    expect(count).toEqual(3);
    count = element.all(by.cssContainingText('.govuk-table__cell', 'Created')).count();
    expect(count).toEqual(2);
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
