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

var pages = [{id: '/applicant/dashboard', url:'/applicant/dashboard'}]
Cypress.Commands.add('crawl', () => {
  var promise = vistPage(pages[0])
  var i;
  for (i = 0; i < 100; i++) {
    (function () {
      var x = i;
      promise.then(function () {
        if (pages[x]) {
          return vistPage(pages[x])
        }
      });
    }());
  }
});

function vistPage(page) {
  return cy.visit(HOME + page.url).then(
    function () {
      cy.get('a')
        .each(function ($el, index, $list) {
          const url = $el.attr('href')
          if (url && url.startsWith('/') && url.indexOf('Logout') === -1  && url.indexOf('/print') === -1 && url.indexOf('/download') === -1 && url.indexOf('files/overheads') === -1) {
            const id = url.replace(/[0-9]/g, '');
            const matchingPages = pages.filter(function (value) {
              return value.id === id
            }).length;
            if (!matchingPages) {
              var page = {
                id: id,
                url: url
              };
              pages.push(page);
            }
          }
      });
    });
}

Cypress.Commands.add('getPages', (callback) => {
  callback(pages);
})

//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
