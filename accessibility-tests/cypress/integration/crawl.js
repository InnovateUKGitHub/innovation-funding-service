import {OPTIONS} from "../support/cypress_config";
import {HOME} from "../support/system_config";


describe('Accessibility test - Applicant crawl', function () {

  before(function() {
    cy.login('steve.smith@empire.com');
    cy.crawl();
  });

  after(function () {
    cy.logout();
  });

  let i;
  for (i = 0; i < 50; i++) {
    (function () {
      let x = i;
      it('Page ' + x, function () {
        cy.testForAccessibility(pages => {
          var page = pages[x];
          if (page) {
            cy.visit(HOME + page.url);
            cy.injectAxe();
            cy.checkA11y(OPTIONS);
          }
        });
      });
    }());
  }

});
