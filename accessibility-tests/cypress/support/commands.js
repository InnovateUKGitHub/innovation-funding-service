// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//

import {OPTIONS} from "../support/cypress_config";

Cypress.Commands.add('logout', () => {
  cy.get('a').contains("Sign out").click()
});

Cypress.Commands.add('login', (user) => {
  cy.visit('/');
  cy.contains('div', 'Email address').find('input').first().type(user);
  cy.contains('div', 'Password').find('input').first().type('Passw0rd');
  cy.get('form').contains('Sign in').click();

});

const add = url => {
  const id = url.replace(/[0-9]/g, '');
  const alreadyExist = pages.filter(value => value.id === id).length;
  if (!alreadyExist) {
    const page = {
      id: id,
      url: url
    };
    pages.push(page);
  }
};


const findPage = page => cy.visit(page.url).then(
  () => {
    cy.get('a')
      .each(($el, index, $list) => {
        const url = $el.attr('href');
        if (testable(url)) {
          add(url);
        }
      });
  });

const testable = url => url && url.startsWith('/') &&
  url.indexOf('Logout') === -1 &&
  url.indexOf('/print') === -1 &&
  url.indexOf('/download') === -1 &&
  url.indexOf('files/overheads') === -1 &&
  url.indexOf('profile/view') === -1 &&  // temp change to work around the issue described in IFS-6968, remove once complete
  url.indexOf('/bank-details/export') === -1 &&
  !/^\/application\/[0-9]*\/grant-agreement$/.test(url) &&
  url.indexOf('finance-check/generate/confirm') === -1 &&
  url.indexOf('spend-profile-export/csv') === -1 &&
  url.indexOf('/grant-offer-letter/template') === -1 &&
  url.indexOf('/files') === -1 &&
  url.indexOf('/grant-offer-letter/grant-offer-letter') === -1 &&
  url.indexOf('/grant-offer-letter/signed-grant-offer-letter') === -1 &&
  !isFile(url);

function isFile(url) {
  return url.includes(".pdf");
}

let pages = [];
Cypress.Commands.add('crawl', (startPage) => {
  pages = [];
  pages.push(startPage);
  const promise = findPage(pages[0]);
  let i;
  for (i = 0; i < 100; i++) {
    (function () {
      const x = i;
      promise.then(() => {
        if (pages[x]) {
          return findPage(pages[x])
        }
      });
    }());
  }
});

Cypress.Commands.add('testForAccessibility', (callback) => {
  callback(pages);
}, );

Cypress.Commands.add('checkAccessibilityOnPage', () => {
  cy.injectAxe();
  cy.checkA11y(OPTIONS);
});

