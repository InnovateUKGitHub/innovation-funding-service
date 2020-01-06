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
import {HOME} from "./system_config";

Cypress.Commands.add('logout', () => {
  cy.get('a').contains("Sign out").click()
});

Cypress.Commands.add('login', (user) => {
  cy.visit(HOME);
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


const vistPage = page => cy.visit(HOME + page.url).then(
  () => {
    cy.get('a')
      .each(($el, index, $list) => {
        const url = $el.attr('href');
        if (testable(url)) {
          add(url);
        }
      });
  });

const testable = url => url && url.startsWith('/') && url.indexOf('Logout') === -1 && url.indexOf('/print') === -1 && url.indexOf('/download') === -1 && url.indexOf('files/overheads') === -1;



let pages = [{id: '/applicant/dashboard', url: '/applicant/dashboard'}];
Cypress.Commands.add('crawl', () => {
  const promise = vistPage(pages[0]);
  let i;
  for (i = 0; i < 100; i++) {
    (function () {
      const x = i;
      promise.then(function () {
        if (pages[x]) {
          return vistPage(pages[x])
        }
      });
    }());
  }
});

Cypress.Commands.add('testForAccessibility', (callback) => {
  callback(pages);
});
