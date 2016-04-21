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

*** Test Cases ***
Submit button should be disabled when the competition is closed
    [Documentation]    INFUND-2312: Competition status in assessment
    When the user navigates to the page    ${SERVER}/application/15/summary
    Then the user should see the text in the page    This competition has already closed, you are no longer able to submit your application
    And the user should not see the element    jQuery=.button:contains("Submit application")
