*** Settings ***
Documentation     INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
...
...               INFUND-6260 As product owner I need to have an overview of the cookies stored, so we satisfy the cookie law and GDS guidelines
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Guest
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
The cookie warning appears for a new user
    [Documentation]  INFUND-1943, INFUND-6260
    Then the user should see the element            id = global-cookie-message
    And the user should see the element             jQuery = p:contains("GOV.UK uses cookies to make the site simpler")
    And the user should see the element             link = Find out more about cookies
    And the user should see the element             css=a[href*='info/cookies']

The warning disappears on refresh
    [Documentation]    INFUND-1943 INFUND-6260
    When the user reloads the page
    Then the user should not see the element    id = global-cookie-message
    And the user should not see the element     jquery = #global-cookie-message a:contains("Find out more about cookies")
