*** Settings ***
Documentation     INFUND-544: As an applicant I want the ‘Application summary’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***
Status is updated after marking as complete
    [Documentation]    INFUND-544
    [Tags]    HappyPath


Status is updated after editing a section
    [Documentation]    INFUND-544
    [Tags]


*** Keywords ***
