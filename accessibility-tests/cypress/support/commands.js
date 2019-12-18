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
