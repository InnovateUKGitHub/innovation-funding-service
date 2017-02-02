*** Settings ***
Documentation  INFUND-6390 As an Applicant I will be invited to add project costs, organisation and funding details via links within the 'Finances' section of my application
...
...            INFUND-6393 As an Applicant I will be invited to add Staff count and Turnover where the include projected growth table is set to 'No' within the Finances page of Competition setup
Suite Setup    Custom Suite Setup
Force Tags     Applicant  CompAdmin
Resource       ../../../resources/defaultResources.robot
Resource       ../../04__Comp_Admin/CompAdmin_Commons.robot

*** Variables ***

*** Test Cases ***
# For the testing of the story INFUND-6393, we need to create New Competition in order to apply the new Comp Setup fields
# Then continue with the applying to this Competition, in order to see the new Fields applied
Comp Admin creates new Competition
    [Documentation]  INFUND-6393
    [Tags]  HappyPath
    [Setup]  guest user log-in  &{Comp_admin1_credentials}
    Given the user navigates to the page  ${CA_UpcomingComp}
    When the user clicks the button/link  jQuery=.button:contains("Create competition")
    Then the user fills in the Initial details  ${day}  ${month}  ${year}


*** Keywords ***
Custom Suite Setup
    ${day} =  get tomorrow day
    Set suite variable  ${day}
    ${month} =  get tomorrow month
    set suite variable  ${month}
    ${year} =  get tomorrow year
    Set suite variable  ${year}