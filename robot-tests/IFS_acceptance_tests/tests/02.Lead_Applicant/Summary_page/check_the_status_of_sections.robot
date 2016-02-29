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
Check that status is updated on the summary page after marking a section as complete
    [Documentation]    INFUND-544
    [Tags]    Applicant    Summary    Application    HappyPath    Pending
    # Pending because of INFUND-2017
    Given the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And none of the sections are marked as complete
    When the user navigates to the page    ${ECONOMIC_BENEFIT_URL_APPLICATION_2}
    And the applicant adds some content and marks this section as complete
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    Then the applicant can see that the 'economic benefit' section is marked as complete

Check that status is updated on the summary page after editing a section so it is no longer complete
    [Documentation]    INFUND-544
    [Tags]    Applicant    Summary    Application    Pending
    # Pending because of INFUND-2017
    Given the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the applicant can see that the 'economic benefit' section is marked as complete
    When the user navigates to the page    ${ECONOMIC_BENEFIT_URL_APPLICATION_2}
    The Applicant Edits The "economic Benefit" Question
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    Then none of the sections are marked as complete

*** Keywords ***
the applicant can see that the 'economic benefit' section is marked as complete
    Page Should Contain    Complete

none of the sections are marked as complete
    Page Should Not Contain    Complete
