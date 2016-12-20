*** Settings ***
Documentation     -INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
Suite Setup       Run Keywords    The guest user opens the browser
...               AND    the user navigates to the page    ${LOGIN_URL}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../resources/defaultResources.robot

*** Variables ***
${cookie_popup_message}     GOV.UK uses cookies to make the site simpler
${cookie_info_link}         https://www.gov.uk/help/cookies
#TODO update cookie info link to ${SERVER}/info/cookies when new shib image is delivered
${correct_email}            steve.smith@empire.com
${correct_password}         Passw0rd

*** Test Cases ***

The cookie warning appears for a new user
    [Documentation]     INFUND-1943 INFUND-6260
    [Tags]
    When the guest user opens the browser
    And the user should see the element    id=global-cookie-message
    Then the user should see the text in the page           ${cookie_popup_message}
    And the user should see the element                     css=a[href*='${cookie_info_link}']


The warning disappears on refresh
    [Documentation]     INFUND-1943 INFUND-6260
    [Tags]
    When the user reloads the page
    Then the user should not see the element       id=global-cookie-message
    And the user should not see the element                 css=a[href*='${cookie_info_link}']