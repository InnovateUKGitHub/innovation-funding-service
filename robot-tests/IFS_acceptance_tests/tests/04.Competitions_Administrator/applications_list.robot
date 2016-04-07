*** Settings ***
Documentation     INFUND-2135 As a Competition Administrator I want to be able to view a listing of applications for an open competition, so that I have the latest status of the applications
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    User closes the browser
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Force Tags          Pending


*** Test Cases ***

The application list shows on the commpetition management overview page
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management
    Given the user navigates to the page        ${APPLICATIONS_LIST}
    When the user

The correct number of applications is showing in the application list
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

The correct columns show for the application list table (application no, project title, lead, status, percentage complete)
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

The applications can be sorted by application number
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

The applications can be sorted by project title
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

The applications can be sorted by project lead
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

The applications can be sorted by percentage complete
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management

Clicking on an application takes the competitions manager to a view of that application
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management


*** Keywords ***
