*** Settings ***
Documentation     -INFUND-703: As a user and I have provided the wrong login details I want to have the email address kept so I don't need to retype it
Suite Setup       Run Keywords    The guest user opens the browser
...               AND    the user navigates to the page    ${LOGIN_URL}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Pending
Test Template     Email persists on invalid login
Resource          Guest_commons.robot
Resource          ../../resources/defaultResources.robot

*** Variables ***
${cookie_popup_message}     GOV.UK uses cookies to make the site simpler
${cookie_info_link}         https://www.gov.uk/help/cookies
${correct_email}            steve.smith@empire.com
${correct_password}         Passw0rd

*** Test Cases ***

The cookie warning appears for a new user
    [Documentation]     INFUND-1943
    [Tags]
    # TODO Pending until the next shib image drop shows the cookie message on dev
    When the guest user opens the browser
    Then the user should see the text in the page           ${cookie_popup_message}
    And the user should see the element                     xpath=//a[contains(@href, '${cookie_info_link')]

The warning disappears on refresh
    [Documentation]     INFUND-1943
    [Tags]
    # TODO Pending until the next shib image drop shows the cookie message on dev
    When the user reloads the page
    Then the user should not see the text in the page       ${cookie_popup_message}
    And the user should not see the element                 xpath=//a[contains(@href, '${cookie_info_link')]