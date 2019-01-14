*** Settings ***
Suite Setup     custom suite setup
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${comp_name}         Procurement competition
${appl_name}         Procurement app

*** Test Cases ***
Comp Admin creates procurement competition
    [Documentation]
    [Tags]
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then The competition admin creates procurement competition   ${rto_type_id}  ${comp_name}  procurement

Applicant applies to newly created The Prince's Trust competition
    [Documentation]  IFS-2688
    [Tags]    MySQL
    When the competition is open                      ${comp_name}
    And Log in as a different user                &{RTO_lead_applicant_credentials}
    Then logged in user applies to competition    ${comp_name}  3

Applicant submits his application
    [Documentation]  IFS-2688 IFS-3287
    [Tags]
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${appl_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${appl_name}
    And the user does not see state aid information
    And the user marks the finances as complete         ${appl_name}   Calculate  52,214  yes
    And the user checks the override value is applied
    And the user selects research category              Feasibility studies
    And the finance overview is marked as incomplete
    And the applicant submits the application

*** Keywords ***
The competition admin creates procurement competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page              ${CA_UpcomingComp}
    the user clicks the button/link             jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details    ${competition}  ${month}  ${nextyear}  Programme  2  PROCUREMENT
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility        ${orgType}  1  false  single-or-collaborative  # 1 means 30%
    the user fills in the CS Milestones         project-setup-completion-stage   ${month}   ${nextyear}
    the user marks the application as done      yes  ${compType_Programme}
    the user fills in the CS Assessors
    the user fills in the CS Documents in other projects
    the user clicks the button/link             link = Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link             link = Return to setup overview
    the user clicks the button/link             jQuery = a:contains("Complete")
    the user clicks the button/link             css = button[type="submit"]
    the user navigates to the page              ${CA_UpcomingComp}
    the user should see the element             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser