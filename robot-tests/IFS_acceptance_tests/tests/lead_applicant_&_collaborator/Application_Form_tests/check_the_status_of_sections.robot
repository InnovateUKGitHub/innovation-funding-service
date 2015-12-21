*** Settings ***
Documentation     -INFUND-1103: As an applicant I want the ‘Application summary’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do

Suite Teardown    User closes the browser
Test Setup        Login as user    &{lead_applicant_credentials}
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot





*** Variables ***






*** Test Cases ***

Check for green ticks and incomplete icons on the summary page
    [Tags]  Applicant   Summary     Application
    Given the applicant is logged in
    And the applicant is on the summary page
    When the applicant fills in foo section
    And the applicant does not have bar section filled in
    Then the applicant can go to the summary page
    And the applicant can see the green check marking that foo section has been filled in
    And the applicant can see the incomplete icon marking that bar section hasn't been filled in



*** Keywords ***

The applicant is logged in
    Log in as user      {username}      {password}

The applicant is on the summary page
    Applicant is on the 'project summary' question

The applicant fills in foo section
    Applicant goes to foo section
    Fill in the details

The applicant does not have bar section filled in
    Applicant goes to bar section
    Verify that the details are not filled in

The applicant can go to the summary page
    Applicant goes to the summary page
    

The applicant can see the green check marking that foo section has been filled in
    Page Should Contain     foo section         green check marker


The applicant can see the incomplete icon marking that bar section hasn't been filled in
    Page Should Contain     bar section         incomplete icon


