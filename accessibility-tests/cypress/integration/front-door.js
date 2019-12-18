import {OPTIONS} from "../support/cypress_config";
import {HOME} from "../support/system_config";

describe('Accessibility test - Front door', function () {

  it('home  page', function () {
    cy.visit(HOME);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });


  it('competition overview', function () {
    cy.visit(`${HOME}/competition/13/overview`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Competition search', function () {
    cy.visit(`${HOME}/competition/search`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Start new application', function () {
    cy.visit(`${HOME}/application/create/start-application/33`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });
});
