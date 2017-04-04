*** Settings ***
Documentation     INFUND-3262: Wrong invitees show when invite new collaborators
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Invite the same partner under the lead organisation
    [Documentation]    INFUND-3262
    [Tags]
    When Create a new application    Partner one    partner@test.com    TEST ONE
    And Create a new application    Partner two    partner@test.com    TEST TWO
    Then the new application should show the correct partners

*** Keywords ***
Create a new application
    [Arguments]    ${NAME}    ${EMAIL}    ${APPLICATION NAME}
    the user navigates to the page    ${COMPETITION_DETAILS_URL}
    the user clicks the button/link    jQuery=.button:contains("Apply now")
    the user clicks the button/link    jQuery=.button:contains("Apply now")
    the user clicks the button/link    jQuery=Label:contains("Yes, I want to create a new application.")
    the user clicks the button/link    jQuery=.button:contains("Continue")
    the user clicks the button/link    jQuery=a:contains("Update Empire Ltd")
    the user clicks the button/link    jQuery=button:contains("Add new applicant")
    The user enters text to a text field    name=applicants[0].name    ${NAME}
    The user enters text to a text field    name=applicants[0].email   ${EMAIL}
    the user clicks the button/link    jQuery=button:contains("Update organisation")
    the user clicks the button/link    jQuery=a:contains("Begin application")
    the user clicks the button/link    link=Application details
    the user enters text to a text field    id=application_details-title    ${APPLICATION NAME}
    the user clicks the button/link    jQuery=button:contains("Save and return")

the new application should show the correct partners
    the user navigates to the page    ${DASHBOARD_URL}
    The user clicks the button/link    link=TEST ONE
    The user clicks the button/link    link=view team members and add collaborators
    The user should not see the text in the page    Partner two
    The user should see the text in the page    Partner one
    the user navigates to the page    ${DASHBOARD_URL}
    The user clicks the button/link    link=TEST TWO
    The user clicks the button/link    link=view team members and add collaborators
    The user should not see the text in the page    Partner one
    The user should see the text in the page    Partner two
