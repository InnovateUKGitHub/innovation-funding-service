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
  for (i = 0; i < 150; i++) {
    ((() => {
      let x = i;
      it('Page ' + x, function () {
        cy.testForAccessibility((pages) => {
          var page = pages[x];
          if (page) {
            cy.visit(`/${page.url}`);

            cy.checkAccessibilityOnPage();

            cy.get("body").then($body => {
              if ($body.find("form").length > 0) {
                cy.get('form').first().submit();
                cy.checkAccessibilityOnPage();
              }
            });
          }
        });
      });
    })());
  }

});
