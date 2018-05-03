*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
User can edit six assesed questions
    [Documentation]    IFS-747
    [Tags]  HappyPath
    [Setup]  logged in user applies to competition  ${openGenericCompetition}
    Given the user should not see the element  a:contains("7.")  # This comp has only 1 question
    When the user clicks the button/link  link=1. Generic question title
    Then the user should see the element  jQuery=button:contains("Mark as complete")

CompAdmin creates a new Generic competition and moves it to Open
    [Documentation]    IFS-3261
    [Tags]
    [Setup]  log in as a different user    &{Comp_admin1_credentials}
    When The competition admin creates a competition for  1  Generic competition for TsnCs  Generic
    Then the competition moves to Open state


*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser  &{lead_applicant_credentials}

The competition admin creates a competition for
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_Generic}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}  1  # 1 means 30%
    the user fills in the CS Milestones   ${month}  ${nextyear}
    exit tests
    the internal user can see that the Generic competition has only one Application Question
    The user removes the Project details questions and marks the Application section as done  yes  Generic
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  css=button[type="submit"]
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")