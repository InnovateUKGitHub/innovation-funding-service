import {OPTIONS} from "../support/cypress_config";
import {HOME} from "../support/system_config";

describe('Accessibility test - Applicant', function () {

  beforeEach(function () {
    cy.login('steve.smith@empire.com');
  });

  afterEach(function () {
    cy.logout();
  });

  it('dashboard', function () {
    cy.injectAxe();
    // cy.checkA11y();
    cy.checkA11y(OPTIONS);
  });

  it('application overview', function () {
    cy.visit(`${HOME}/application/149`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('application team', function () {
    cy.visit(`${HOME}/application/149`);
    cy.contains("Application team").click();
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Add person to team', function () {
    cy.visit(`${HOME}/application/149/form/question/507/team`);
    cy.contains("Add person to Empire Ltd").click();
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('application details', function () {
    cy.visit(`${HOME}/application/149/form/question/508/application-details`);
    cy.contains("When do you wish to start your project"); // example of finding text
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Research category', function () {
    cy.visit(`${HOME}/application/149/form/question/509`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Project summary', function () {
    cy.visit(`${HOME}/application/149/form/question/510/generic`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Public description', function () {
    cy.visit(`${HOME}/application/149/form/question/511/generic`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Scope', function () {
    cy.visit(`${HOME}/application/149/form/question/512/generic`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

});
