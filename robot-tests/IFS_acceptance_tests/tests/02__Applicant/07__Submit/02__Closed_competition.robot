*** Settings ***
Documentation     INFUND-2312: Competition status in assessment
...
...               INFUND-3175: Applications in project setup still have a 'Review & submit' button
...
...               INFUND-3740: Buttons still show as if the application were editable
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Variables ***
${application_not_submitted_message}    This application has not been entered into the competition

*** Test Cases ***
Application shows as not submitted on the dashboard
    [Documentation]    INFUND-2741
    [Tags]
    Then the user should see that the application is not submitted
    And the user clicks the button/link    link=00000015: Spherical interactions in hyperdimensional space

Application shows as not submitted on the overview
    [Documentation]    INFUND-2742
    [Tags]
    When the user navigates to the page    ${SERVER}/application/15
    Then the user should see the text in the page    ${application_not_submitted_message}

Submit button should be disabled
    [Documentation]    INFUND-2742, INFUND-2312, INFUND-3175
    [Tags]
    When the user navigates to the page    ${SERVER}/application/15/summary
    Then the user should see the text in the page    This competition has already closed, you are no longer able to submit your application
    And the user should not see the element    jQuery=.button:contains("Submit application")
    And the user should not see the element    jQuery=.button:contains("Review & submit")

Applicant shouldn't see the Mark as complete-Edit-Save buttons
    [Documentation]    INFUND-3740
    Given the user navigates to the page    ${SERVER}/application/15/summary
    When The user clicks the button/link    jQuery=button:contains(Project summary)
    Then The user should not see the element    jQuery=.button:contains(Mark as complete)
    And the user should not see the element    jQuery=button:contains(Edit)
    And the user navigates to the page    ${SERVER}/application/15/form/question/44
    Then The user should not see the element    jQuery=button:contains(Save and return to application overview)

Already submitted application should not show error when the competition is closed
    [Documentation]    INFUND-3175
    [Tags]
    When the user navigates to the page    ${dashboard_url}
    And the user clicks the button/link    link=00000003: Mobile Phone Data for Logistics Analytics
    Then the user should not see the text in the page    Application not submitted

*** Keywords ***
the user should see that the application is not submitted
    the user should see the element    css=div.no.application-status.assessed
