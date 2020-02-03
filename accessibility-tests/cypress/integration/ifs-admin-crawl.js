import {OPTIONS} from "../support/cypress_config";

describe('Accessibility test - IFS Admin crawl', function () {

  before(() => {
    cy.login('arden.pimenta@innovateuk.test');
    cy.crawl({id: '/management/dashboard/live', url: '/management/dashboard/live'});
  });

  after(() => {
    cy.logout();
  });

  let i;
  for (i = 0; i < 50; i++) {
    ((() => {
      let x = i;
      it('Page ' + x, function () {
        cy.testForAccessibility((pages) => {
          var page = pages[x];
          if (page) {
            cy.visit(`/${page.url}`);
            cy.injectAxe();
            cy.checkA11y(OPTIONS);
          }
        });
      });
    })());
  }

});
