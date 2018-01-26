*** Settings ***
Documentation   Suite description
...
...             IFS-2688 As a Portfolio manager I am able to create a Prince's Trust competition
Suite Setup     custom suite setup
Suite Teardown  Close browser and delete emails
Force Tags      compAdmin  Applicant  Assessor
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot


*** Variables ***
${comp_name}         The Prince Trust competition
${application_name}  The Prince Trust Application

*** Test Cases ***
Comp Admin creates The Prince's Trust type competition
    [Documentation]  IFS-2688
    [Tags]
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then The competition admin creates The Prince's Trust Comp   ${rto_type_id}  ${comp_name}  Prince

Applicant applies to newly created The Prince's Trust competition
    [Documentation]  IFS-2688
    [Tags]    MySQL
    When the competition is open                                 ${comp_name}
    Then Lead Applicant applies to the new created competition   ${comp_name}  &{RTO_lead_applicant_credentials}

Applicant submits his application
    [Documentation]  IFS-2688
    [Tags]
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${application_name}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    and the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    and the user should not see the element             jQuery=h2:contains("Finances")
    Then the applicant submits the application

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

The competition admin creates The Prince's Trust Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}  1  # 1 means 30%
    the user fills in the CS Milestones   ${month}  ${nextyear}
    the user marks the Application as done  no  ${compType_EOI}
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    the lead applicant marks every question as complete   Project summary
    the lead applicant marks every question as complete   Scope
    # Please note there will be changes to the questions shortly , so for now using EOI questions array
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

Milestones are updated in database to move competition to assessment state
    ${competitionId} =  get comp id from comp title  ${comp_name}
    Set suite variable  ${competitionId}
    the assessment start period changes in the db in the past   ${competitionId}
