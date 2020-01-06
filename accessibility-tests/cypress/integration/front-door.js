import {OPTIONS} from "../support/cypress_config";

describe('Accessibility test - Front door', function () {

  it('home  page', function () {
    cy.visit('/');
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });


  it('competition overview', function () {
    cy.visit(`/competition/13/overview`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Competition search', function () {
    cy.visit(`/competition/search`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });

  it('Start new application', function () {
    cy.visit(`/application/create/start-application/33`);
    cy.injectAxe();
    cy.checkA11y(OPTIONS);
  });
});
