*** Settings ***
Documentation     INFUND-2135 As a Competition Administrator I want to be able to view a listing of applications for an open competition, so that I have the latest status of the applications
Suite Setup       Log in as user    &{Comp_admin1_credentials}
Suite Teardown    User closes the browser
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Force Tags


*** Test Cases ***

The application list shows on the commpetition management overview page
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management
    When the user navigates to the page        ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    Then the user should see the text in the page       Application list


The correct number of applications is showing in the application list
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management
    When the user navigates to the page     ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    Then the user should see the text in the page       7 applications


The correct columns show for the application list table (application no, project title, lead, status, percentage complete)
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management
    When the user navigates to the page     ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    Then the user should see the text in the page    Application no.
    And the user should see the text in the page     Project title
    And the user should see the text in the page     Lead
    And the user should see the text in the page     Status
    And the user should see the text in the page     Percentage complete


The applications can be sorted by application number
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management      Pending
    Given the user navigates to the page     ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    When the application list is sorted by      Application no.
    Then the applications should be sorted by application number


The applications can be sorted by project title
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management      Pending
     Given the user navigates to the page     ${COMP_MANAGEMENT_APPLICATIONS_LIST}
     When the application list is sorted by     Project title
     Then the applications should be sorted by project title


The applications can be sorted by project lead
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management      Pending
    Given the user navigates to the page     ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    When the application list is sorted by      Lead
    Then the applications should be sorted by project lead


The applications can be sorted by percentage complete
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management      Pending
    Given the user navigates to the page     ${COMP_MAMNAGEMENT_APPLICATIONS_LIST}
    When the application list is sorted by      Percentage complete
    Then the applications should be sorted by percentage complete

Clicking on an application takes the competitions manager to a view of that application
    [Documentation]     INFUND-2135: listing of applications for an open competition
    [Tags]      Competition management      Pending
    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    When the user clicks the button/link     link=00000001
    Then the user should be redirected to the correct page      ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}




*** Keywords ***


the application list is sorted by
    [Arguments]     ${sorting_factor}
    Select From List    name=sort     ${sorting_factor}

the applications should be sorted by application number
    wait until element is visible       foo

the applications should be sorted by project title
    wait until element is visible       foo

the applications should be sorted by Project lead
    wait until element is visible       foo

the applications should be sorted by Percentage complete
    wait until element is visible       foo



