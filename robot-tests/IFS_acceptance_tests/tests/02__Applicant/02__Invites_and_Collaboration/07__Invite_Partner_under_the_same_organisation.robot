*** Settings ***
Documentation     INFUND-3262: Wrong invitees show when invite new collaborators
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Invite the same partner under the lead organisation
    [Documentation]    INFUND-3262
    When Create a new application    Partner one    partner@test.com    TEST ONE
    And Create a new application    Partner two    partner@test.com    TEST TWO
    Then the new application should show the correct partners

*** Keywords ***
Create a new application
    [Arguments]    ${NAME}    ${EMAIL}    ${APPLICATION NAME}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("Yes I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jquery=li:nth-child(1) button:contains('Add another person')
    And The user should see the element    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1)
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(1) input    ${NAME}
    The user enters text to a text field    css=li:nth-child(1) tr:nth-of-type(2) td:nth-of-type(2) input    ${EMAIL}
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    ${APPLICATION NAME}
    And the user clicks the button/link    jQuery=button:contains("Save and return")

the new application should show the correct partners
    Given the user navigates to the page    ${DASHBOARD_URL}
    When The user clicks the button/link    link=TEST ONE
    And The user clicks the button/link    link=view team members and add collaborators
    Then The user should not see the text in the page    Partner two
    And The user should see the text in the page    Partner one
    When the user navigates to the page    ${DASHBOARD_URL}
    And The user clicks the button/link    link=TEST TWO
    And The user clicks the button/link    link=view team members and add collaborators
    Then The user should not see the text in the page    Partner one
    And The user should see the text in the page    Partner two
