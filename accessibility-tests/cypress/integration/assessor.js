import {OPTIONS} from "../support/cypress_config";

describe('Accessibility test - Assessor', function () {

  beforeEach(function () {
    cy.login('paul.plum@gmail.com');
  });

  afterEach(function () {
    cy.logout();
  });

  it('assessor reject', function () {
    //Finds  IFS-6566
    cy.visit(`/assessment/232`);
    cy.contains("Unable to assess this application?").click();
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });


});
