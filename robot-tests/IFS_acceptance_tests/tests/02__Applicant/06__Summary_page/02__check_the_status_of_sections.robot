*** Settings ***
Documentation     INFUND-544: As an applicant I want the ‘Application summary’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/Application_question_edit_actions.robot

*** Variables ***

*** Test Cases ***
Status is updated after mark as complete
    [Documentation]    INFUND-544
    [Tags]    Applicant    HappyPath
    Given the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user should not see the text in the page    Complete
    When the user navigates to the page    ${ECONOMIC_BENEFIT_URL_APPLICATION_2}
    And the applicant adds some content and marks this section as complete
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    Then the user should see the text in the page    Complete

Status is updated after editing a section
    [Documentation]    INFUND-544
    [Tags]
    Given the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user should see the text in the page    Complete
    When the user navigates to the page    ${ECONOMIC_BENEFIT_URL_APPLICATION_2}
    The Applicant Edits The "economic Benefit" Question
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    Then the user should not see the text in the page    Complete

*** Keywords ***
