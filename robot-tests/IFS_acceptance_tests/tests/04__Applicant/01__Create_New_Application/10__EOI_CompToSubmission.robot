*** Settings ***
Documentation   Suite description
...
...             IFS-2192 As a Portfolio manager I am able to create an EOI competition
...
...             IFS-2196 As an applicant I am able to apply for an EOI competition
Suite Setup     custom suite setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

# This suite covers creation of EOI type competition and apply to it
*** Variables ***
${comp_name}         EOI comp
${EOI_application}   EOI Application

*** Test Cases ***
Comp Admin Creates EOI type competition
    [Documentation]  IFS-2192
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking                     &{Comp_admin1_credentials}
    Then The competition admin creates a EOI Comp     ${business_type_id}  ${comp_name}  EOI

Applicant applies to newly created EOI comp
    [Documentation]  IFS-2192  IFS-2196
    [Tags]  HappyPath
    [Setup]  the competition is open     ${comp_name}
    Lead Applicant applies to the new created competition    ${comp_name}
    the lead applicant invites a registered user

Applicant submits his application
    [Documentation]  IFS-2196
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link            link=Application details
    When the user fills in the Application details      ${EOI_application}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    and the lead applicant fills all the questions and marks as complete(EOI comp type)
    Then the user clicks the button/link                link=Review and submit
    and the user should not see the element             jQuery=h2:contains("Finances")

*** Keywords ***
Custom Suite Setup
    predefined date variables
    The guest user opens the browser

The competition admin creates a EOI Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}
    the user fills in the CS Milestones   ${month}  ${nextMonth}  ${nextyear}
    the user marks the Application as done  no  ${compType_EOI}
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(EOI comp type)
    the lead applicant marks every question as complete(EOI)   Project summary
    the lead applicant marks every question as complete(EOI)   Scope
    the lead applicant marks every question as complete(EOI)   Business opportunity and potential market
    the lead applicant marks every question as complete(EOI)   Innovation
    the lead applicant marks every question as complete(EOI)   Project team
    the lead applicant marks every question as complete(EOI)   Funding and adding value


