*** Settings ***
Documentation       INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Suite Teardown      User closes the browser
Force Tags          Pending
# This is a skeleton accpetance test suite, marked as pending until functionality arrives for INFUND-1436
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot


*** Variables ***



*** Test Cases ***

If research participation is too high, an error is shown on the form and also the overview page
    [Documentation]     INFUND-1436
    [Tags]              Research percentage     Validation      Finances
    Given the applicant logs in
    When the applicant puts the research participation too high
    Then there is an error message on the finances form
    And there is an error message on the overview page
    And the applicant can log out


If research participation is below the maximum level, no error is shown
    [Documentation]     INFUND-1436
    [Tags]              Research percentage     Validation      Finances
    Given the first collaborator logs in
    When the first collaborator puts the research participation lower
    And the applicant logs in
    Then there is no error message on the finances form
    And there is no error message on the overview page
    And the first collaborator can log out



*** Keywords ***

The applicant logs in
    Login as user      &{lead_applicant_credentials}

The applicant puts the research participation too high
    Pending

There is an error message on the finances form
    Page Should Contain     error

There is an error message on the overview page
    Go To       ${application_overview_url}
    Page Should Contain         error

The applicant can log out
    Logout as user

The first collaborator logs in
    Login as user       &{collaborator1_credentials}

The first collaborator puts the research participation lower
    Pending
    Logout as user

There is no error message on the finances form
    Page Does Not Contain       error

There is no error message on the overview page
    Go To       ${application_overview_url}
    Page Does Not Contain       error

The first collaborator can log out
    Logout as user
