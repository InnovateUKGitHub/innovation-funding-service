*** Settings ***
Documentation     INFUND-544: As an applicant I want the ‘Application summary’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do
Suite Setup       Log in as user    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/keywords/Application_question_edit_actions.robot

*** Variables ***

*** Test Cases ***
Check that status is updated on the summary page after marking a section as complete
    [Documentation]    INFUND-544
    [Tags]    Applicant    Summary    Application
    Given applicant goes to the 'application summary' page for application 2
    And none of the sections are marked as complete
    When applicant goes to the 'economic benefit' question for application 2
    And the applicant adds some content and marks this section as complete
    And applicant goes to the 'application summary' page for application 2
    Then the applicant can see that the 'economic benefit' section is marked as complete

Check that status is updated on the summary page after editing a section so it is no longer complete
    [Documentation]    INFUND-544
    [Tags]    Applicant    Summary    Application
    Given applicant goes to the 'application summary' page for application 2
    And the applicant can see that the 'economic benefit' section is marked as complete
    When applicant goes to the 'economic benefit' question for application 2
    The Applicant Edits The "economic Benefit" Question
    And applicant goes to the 'application summary' page for application 2
    Then none of the sections are marked as complete

*** Keywords ***
the applicant can see that the 'economic benefit' section is marked as complete
    Page Should Contain    Complete

none of the sections are marked as complete
    Page Should Not Contain    Complete
