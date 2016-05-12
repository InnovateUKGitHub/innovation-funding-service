*** Settings ***
Documentation     INFUND-2312: Competition status in assessment
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant    Submit
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Variables ***

${application_not_submitted_message}    This application has not been entered into the competition

*** Test Cases ***

The application shows as not submitted on the dashboard when the competition is closed
    [Documentation]     INFUND-2741
    [Tags]
    When the user navigates to the page     ${dashboard_url}
    Then the user should see that the application is not submitted
    And the user clicks the button/link     link=00000015: Spherical interactions in hyperdimensional space



The application shows as not submitted on the overview page when the competition is closed
    [Documentation]     INFUND-2742
    [Tags]
    When the user navigates to the page    ${SERVER}/application/15
    Then the user should see the text in the page        ${application_not_submitted_message}


Submit button should be disabled when the competition is closed
    [Documentation]    INFUND-2742, INFUND-2312: Competition status in assessment
    [Tags]
    When the user navigates to the page    ${SERVER}/application/15/summary
    Then the user should see the text in the page    This competition has already closed, you are no longer able to submit your application
    And the user should not see the element    jQuery=.button:contains("Submit application")

*** Keywords ***

the user should see that the application is not submitted
    the user should see the element    css=div.no.application-status.assessed