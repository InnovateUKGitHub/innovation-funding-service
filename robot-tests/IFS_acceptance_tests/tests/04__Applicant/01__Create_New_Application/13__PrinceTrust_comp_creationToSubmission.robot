*** Settings ***
Documentation   IFS-2688 As a Portfolio manager I am able to create a Prince's Trust competition
...
...             IFS-3287 As a Portfolio Manager I am able to switch off requirement for Research category
Suite Setup     Custom suite setup
Suite Teardown  Custom suite teardown
Resource        ../../../resources/defaultResources.robot
Resource        ../../../resources/common/Applicant_Commons.robot
Resource        ../../../resources/common/Competition_Commons.robot


*** Variables ***
${comp_name}         The Prince Trust competition
${application_name}  The Prince Trust Application
${comp_type}         The Prince's Trust

*** Test Cases ***
Comp Admin creates The Prince's Trust type competition
    [Documentation]  IFS-2688
    [Tags]
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then the competition admin creates The Prince's Trust Comp   ${rto_type_id}  ${comp_name}  Prince

Applicant applies to newly created The Prince's Trust competition
    [Documentation]  IFS-2688
    [Tags]
    Given get competition id and set open date to yesterday  ${comp_name}
    And Log in as a different user                           &{RTO_lead_applicant_credentials}
    Then logged in user applies to competition               ${comp_name}  3

Applicant submits his application
    [Documentation]  IFS-2688  IFS-3287  IFS-5920
    [Tags]
    Given the user clicks the button/link                      link = Application details
    When the user fills in the Application details             ${application_name}  ${tomorrowday}  ${month}  ${nextyear}
    Then the applicant completes application team
    And the lead applicant answers the four sections as complete
    And the user accept the competition terms and conditions    Return to application overview
    And the user should not see the element                     jQuery = h2:contains("Finances")
    Then the applicant submits the application

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

The competition admin creates The Prince's Trust Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page                          ${CA_UpcomingComp}
    the user clicks the button/link                         jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                ${competition}  ${month}  ${nextyear}  ${comp_type}  2  GRANT
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  1  false  single-or-collaborative  # 1 means 30%
    the user selects the organisational eligibility to no   false
    the user fills in the CS Milestones                     release-feedback-completion-stage   ${month}   ${nextyear}
    the user marks the Application as done(Prince's Trust comp)
    the user fills in the CS Assessors
    the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         css = button[type="submit"]
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(Prince's Trust comp type)
    the applicant completes application team
    then the user selects research category  Feasibility studies
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the user marks the Application as done(Prince's Trust comp)
    the user clicks the button/link             link=Application
    the user marks each question as complete    Application details
    the assessed questions are marked complete(EOI type)
    the user opts no finances for EOI comp
    the user clicks the button/link             jQuery=button:contains("Done")
    the user clicks the button/link             link=Competition setup
    the user should see the element             jQuery=div:contains("Application") ~ .task-status-complete

the lead applicant answers the four sections as complete
    the lead applicant marks every question as complete  1. Business opportunity and potential market
    the lead applicant marks every question as complete  2. Innovation
    the lead applicant marks every question as complete  3. Project team
    the lead applicant marks every question as complete  4. Funding and adding value

Custom suite teardown
    Close browser and delete emails
    Disconnect from database