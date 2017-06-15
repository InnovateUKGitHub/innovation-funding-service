*** Settings ***
Documentation     IFS-362 As an IFS user I am able to find links to general guidance links via the footer both when signed in or not signed in.
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Guest
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Guest user can click on the footer links
    [Documentation]    IFS-362
    [Tags]
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user tries the footer links    ${frontDoor}
    Then the user navigates to the page    ${LOGIN_URL}
    And the guest user inserts user email & password    {lead_applicant}    ${correct_password}
    When the guest user clicks the log-in button
    Then the user tries the footer links    ${DASHBOARD_URL}
    And the user navigates to the page    ${LOGIN_URL}
    Then the user should see the text in the page    Sign in
    And the user tries the footer links    ${LOGIN_URL}

*** Keywords ***
the user tries the footer links
    [Arguments]    ${page}
    Given the user navigates to the page    ${page}
    When the user clicks the button/link    link=Innovate UK
    Then the user should see the element    jQuery=h1 img[alt$="Innovate UK"]
    And the user tries the link    ${page}    Innovation funding advice    Business innovation: what funding you can apply for
    And the user tries the link    ${page}    Events    Events
    And the user tries the link    ${page}    Connect to innovation experts    Innovation: connect to experts, specialist support and facilities
    And the user tries the link    ${page}    Innovate UK blog    Blog
    And the user tries the link    ${page}    GOV.UK accessibility    Accessibility
    And the user tries the link    ${page}    Terms and conditions    Terms and conditions
    And the user tries the link    ${page}    Contact us    Contact us
    When the user navigates to the page    ${page}
    And the user clicks the button/link    link=Sign up for competition updates
    Then the user should see the text in the page    Sign up for email notifications
    And the user tries the link    ${page}    Latest funding opportunities    Innovation competitions
    And the user tries the link    ${page}    Find out more about cookies    Cookies

the user tries the link
    [Arguments]    ${page}    ${link}    ${header_one}
    Given the user navigates to the page    ${page}
    When the user clicks the button/link    link=${link}
    Then the user should see the element    jQuery=h1:contains(${header_one})