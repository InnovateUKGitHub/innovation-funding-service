*** Settings ***
Documentation   IFS-2396  ATI Competition type template
...
...             IFS-2332  Project Finance user is not able to download the overheads file
...
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource        ../../../resources/keywords/User_actions.robot
Resource        /Users/fahadahmed/innovation-funding-service/robot-tests/IFS_acceptance_tests/tests/10__Project_setup/PS_Common.robot

*** Variables ***
${ATIcompetitionTitle}  ATI Competition
${ATIapplicationTitle}  ATI application

*** Test Cases ***
Comp Admin creates an ATI competition
    [Documentation]  IFS-2396
    [Tags]  HappyPath
    Given The user logs-in in new browser          &{Comp_admin1_credentials}
    And the user navigates to the page             ${CA_UpcomingComp}
    When the user clicks the button/link           link=Create competition
    Then the user fills in the CS Initial details  ${ATIcompetitionTitle}  ${month}  ${nextyear}  Aerospace Technology Institute
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility       ${business_type_id}
    And the user fills in the CS Milestones        ${month}  ${nextyear}
    And the user marks the application as done     yes  ${compType_Programme}
    And the user fills in the CS Assessors
    When the user clicks the button/link           link=Public content
    Then the user fills in the Public content and publishes  ATI
    When the user clicks the button/link           link=Return to setup overview
    Then the user should see the element           jQuery=div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link           jQuery=a:contains("Complete")
    Then the user clicks the button/link           jQuery=a:contains("Done")

Requesting the ID of this Competition
    [Documentation]  IFS-2332
    [Tags]  MySql
    ${atiCompId} =  get comp id from comp title  ${ATIcompetitionTitle}
    Set suite variable   ${atiCompId}

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    [Tags]  HappyPath  MySQL
    When the competition is open                                 ${ATIcompetitionTitle}
    Then Lead Applicant applies to the new created competition   ${ATIcompetitionTitle}  &{lead_applicant_credentials}

Applicant submits his application
    [Documentation]  IFS-2286  IFS-2332
    [Tags]  HappyPath
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${ATIapplicationTitle}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${ATIapplicationTitle}
    And the user marks the finances as complete         ${ATIapplicationTitle}   Calculate  52,214  yes
    Then the applicant submits the application

Moving ATI Competition to Project Setup
    [Documentation]  IFS-2332
    [Tags]
    Log in as a different user    &{internal_finance_credentials}
    moving competition to Closed    ${atiCompId}
    making the application a successful project    ${atiCompId}  ${ATIapplicationTitle}
    moving competition to Project Setup    ${atiCompId}

Requesting Project ID of this Project
    [Documentation]  IFS-2332
    [Tags]
    ${atiProjectID} =  get project id by name    ${ATIapplicationTitle}
    Set suite variable    ${atiProjectID}

Applicant completes Project Details
    [Documentation]  IFS-2332
    [Tags]
    log in as a different user    &{lead_applicant_credentials}
    project lead submits project address    ${atiProjectID}

Requesting Organisation ID from this Application
    ${ATIorganisationID} =  get organisation id by name    ${ATIapplicationTitle}
    Set suite variable    ${ATIorganisationID}


Project Finance is able to see the Overheads costs file
    [Documentation]  IFS-2332
    [Tags]  CompAdmin
    Given Log in as a different user  &{internal_finance_credentials}
    When the user navigates to the page    ${SERVER}/project-setup-management/project/${atiProjectID}/finance-check/${ATIorganisationID}/eligibility
    And the user clicks the button/link    jQuery=button:contains("Overhead costs")
    Then the project finance user is able to download the Overheads file

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser