*** Settings ***
Documentation     INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
...
...               INFUND-6260 As product owner I need to have an overview of the cookies stored, so we satisfy the cookie law and GDS guidelines
Suite Setup       Run Keywords    The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../resources/defaultResources.robot    #TODO update cookie info link to ${SERVER}/info/cookies when new shib image is delivered

*** Test Cases ***
The cookie warning appears for a new user
    [Documentation]    INFUND-1943
    ...    ..
    ...    INFUND-6260
    [Tags]
    Then the user should see the element    id=global-cookie-message
    And the user should see the text in the page    GOV.UK uses cookies to make the site simpler
    And the user should see the element    link=Find out more about cookies
    And the user should see the element    css=a[href*='https://www.gov.uk/help/cookies']

The warning disappears on refresh
    [Documentation]    INFUND-1943 INFUND-6260
    [Tags]
    When the user reloads the page
    Then the user should not see the element    id=global-cookie-message
    And the user should not see the element    link=Find out more about cookies
