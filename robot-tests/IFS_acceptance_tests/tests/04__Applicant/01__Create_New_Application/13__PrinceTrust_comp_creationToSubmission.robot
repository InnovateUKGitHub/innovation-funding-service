*** Settings ***
Documentation
...             IFS-2688 As a Portfolio manager I am able to create a Prince's Trust competition
...
Suite Setup     custom suite setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot


*** Variables ***
${comp_name}         The Prince Trust competition
${application_name}  The Prince Trust Application
${comp_type}         The Prince's Trust

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
        And Log in as a different user            &{RTO_lead_applicant_credentials}
        Then logged in user applies to competition                  ${comp_name}  3

Applicant submits his application
    [Documentation]  IFS-2688
    [Tags]
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${application_name}  ${tomorrowday}  ${month}  ${nextyear}
    Then the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    And the user should not see the element             jQuery=h2:contains("Finances")
    Then the applicant submits the application

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

The competition admin creates The Prince's Trust Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${comp_type}
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}  1  # 1 means 30%
    the user fills in the CS Milestones   ${month}  ${nextyear}
    the user marks the Application as done(Prince's Trust comp)
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  css=button[type="submit"]
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    the applicant completes application team
    then the user selects research category  Feasibility studies
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the user marks the Application as done(Prince's Trust comp)
    the user clicks the button/link  link=Application
    the user marks each question as complete    Application details
    the assessed questions are marked complete(EOI type)
    the user opts no finances for EOI comp
    the user clicks the button/link  jQuery=button:contains("Done")
    the user clicks the button/link  link=Competition setup
    the user should see the element  jQuery=div:contains("Application") ~ .task-status-complete

