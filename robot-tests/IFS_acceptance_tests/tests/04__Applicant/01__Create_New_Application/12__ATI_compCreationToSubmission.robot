*** Settings ***
Documentation   IFS-2396  ATI Competition type template
...
...             IFS-2332  Project Finance user is not able to download the overheads file
...
...             IFS-1497  As an applicant I am able to confirm the project location for my organisation
...
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource        ../../10__Project_setup/PS_Common.robot

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
    And the user selects the Terms and Conditions
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility       ${business_type_id}  1  # 1 means 30%
    And user fills in funding overide
    And the user fills in the CS Milestones        ${month}  ${nextyear}
    And the user marks the application as done     yes  ${compType_Programme}
    And the user fills in the CS Assessors
    When the user clicks the button/link           link=Public content
    Then the user fills in the Public content and publishes  ATI
    When the user clicks the button/link           link=Return to setup overview
    Then the user should see the element           jQuery=div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link           jQuery=a:contains("Complete")
    Then the user clicks the button/link           css=button[type="submit"]

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    [Tags]  HappyPath  MySQL
    When the competition is open                                 ${ATIcompetitionTitle}
    Then Lead Applicant applies to the new created competition   ${ATIcompetitionTitle}  &{lead_applicant_credentials}

Applicant submits his application
    [Documentation]  IFS-2286  IFS-2332  IFS-1497
    [Tags]  HappyPath
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${ATIapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${ATIapplicationTitle}
    And the user marks the finances as complete         ${ATIapplicationTitle}   Calculate  52,214  yes
    And the user checks the override value is applied
    Then the applicant submits the application

Moving ATI Competition to Project Setup
    [Documentation]  IFS-2332
    [Tags]
    [Setup]  Requesting the ID of this Competition
    When Log in as a different user    &{internal_finance_credentials}
    Then moving competition to Closed                  ${atiCompId}
    And making the application a successful project    ${atiCompId}  ${ATIapplicationTitle}
    And moving competition to Project Setup            ${atiCompId}

Applicant completes Project Details
    [Documentation]  IFS-2332
    [Tags]
    [Setup]  Requesting Project ID of this Project
    When log in as a different user              &{lead_applicant_credentials}
    Then project lead submits project address    ${ProjectID}

Project Finance is able to see the Overheads costs file
    [Documentation]  IFS-2332
    [Tags]
    Given Log in as a different user       &{internal_finance_credentials}
    When the user navigates to the page    ${SERVER}/project-setup-management/project/${ProjectID}/finance-check/
    And the user clicks the button/link    jQuery=tr:contains("Empire Ltd") td:nth-child(4) a:contains("Review")
    And the user clicks the button/link    jQuery=button:contains("Overhead costs")
    Then the user should see the element   jQuery=a:contains("${excel_file}")
    And the project finance user is able to download the Overheads file    ${ProjectID}  22
    # TODO IFS-2599 Raised to improve this as we cannot rely on hard-coded values.

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

Requesting the ID of this Competition
    ${atiCompId} =  get comp id from comp title  ${ATIcompetitionTitle}
    Set suite variable   ${atiCompId}

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name    ${ATIapplicationTitle}
    Set suite variable    ${ProjectID}

User fills in funding overide
    the user clicks the button/link   link = Eligibility
    the user clicks the button/link   css = .button[type=submit]
    the user clicks the button twice  css = label[for="comp-overrideFundingRules-yes"]
    the user selects the option from the drop-down menu  100%  id = fundingLevels
    the user clicks the button/link   jQuery = button:contains("Done")
    the user should see the element   jQuery = dt:contains("Set funding level") ~ dd:contains("100%")
    the user clicks the button/link   link = Competition setup

the user checks the override value is applied
    the user clicks the button/link     link = Your finances
    the user clicks the button/link     link = Your funding
    the user clicks the button/link     css = button[type=submit]
    the user should see the element     jQuery = .form-label:contains("maximum 100%")
    then the user selects the checkbox  agree-terms-page
    the user clicks the button/link     css = button[name=mark_section_as_complete]
    the user clicks the button/link     link = Application overview
