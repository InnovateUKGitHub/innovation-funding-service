import {OPTIONS} from "../support/cypress_config";

describe('Accessibility test - Applicant crawl', function () {

  before(() => {
    cy.login('steve.smith@empire.com');
    cy.crawl({id: '/applicant/dashboard', url: '/applicant/dashboard'});
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

            cy.get("body").then($body => {
              if ($body.find("form").length > 0) {   //evaluates as true
              cy.get('form').first().submit();
              }
            });

            cy.injectAxe();
            cy.checkA11y(OPTIONS);
          }
        });
      });
    })());
  }

});
