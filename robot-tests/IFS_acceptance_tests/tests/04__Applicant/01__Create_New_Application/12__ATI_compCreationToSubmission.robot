*** Settings ***
Documentation     IFS-2396  ATI Competition type template
...
...               IFS-2332  Project Finance user is not able to download the overheads file
...
...               IFS-1497  As an applicant I am able to confirm the project location for my organisation
...
...               IFS-3421  As a Lead applicant I am unable submit an ineligible application to a Collaborative competition
...
Suite Setup       Custom Suite Setup
Suite Teardown    Close browser and delete emails
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Variables ***
${ATIcompetitionTitle}  ATI Competition
${ATIapplicationTitle}  ATI application

*** Test Cases ***
Comp Admin creates an ATI competition
    [Documentation]  IFS-2396
    [Tags]
    Given The user logs-in in new browser          &{Comp_admin1_credentials}
    The competition admin creates competition      ${business_type_id}  ${ATIcompetitionTitle}  ATI  ${compType_Programme}  2  GRANT  project-setup-completion-stage  yes  1  true  collaborative
    User fills in funding overide

Applicant applies to newly created ATI competition
    [Documentation]  IFS-2286
    [Tags]  MySQL
    When the competition is open                                 ${ATIcompetitionTitle}
    And Log in as a different user            &{lead_applicant_credentials}
    Then logged in user applies to competition                  ${ATIcompetitionTitle}  1

Single applicant cannot submit his application to a collaborative comp
    [Documentation]  IFS-2286  IFS-2332  IFS-1497  IFS-3421
    [Tags]
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${ATIapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${ATIapplicationTitle}
    And the user does not see state aid information
    And the user marks the finances as complete         ${ATIapplicationTitle}   Calculate  52,214  yes
    And the user checks the override value is applied
    And the user selects research category              Feasibility studies
    And the finance overview is marked as incomplete
    And the application cannot be submited

Invite a collaborator and check the application can the be submitted
    [Documentation]  IFS-3421
    [Tags]
    Given the lead invites already registered user
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
    And the user clicks the button/link    jQuery = tr:contains("Empire Ltd") td:nth-child(4) a:contains("Review")
    And the user clicks the button/link    jQuery = button:contains("Overhead costs")
    Then the user should see the element   jQuery = a:contains("${excel_file}")
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
    the user clicks the button/link   link = ${ATIcompetitionTitle}
    the user clicks the button/link   link = View and update competition setup
    the user clicks the button/link   link = Eligibility
    the user clicks the button/link   css = .govuk-button[type=submit]
    the user clicks the button twice  css = label[for="comp-overrideFundingRules-yes"]
    the user selects the option from the drop-down menu  100%  id = fundingLevelPercentageOverride
    the user clicks the button/link   jQuery = button:contains("Done")
    the user should see the element   jQuery = dt:contains("Funding level") ~ dd:contains("100%")
    the user clicks the button/link   link = Competition setup
    the user clicks the button/link   jQuery = a:contains("Complete")
    the user clicks the button/link   css = button[type="submit"]

the user checks the override value is applied
    the user clicks the button/link     link = Your finances
    the user clicks the button/link     link = Your funding
    the user clicks the button/link     jQuery = button:contains("Edit your funding")
    the user should see the element     jQuery = span:contains("The maximum you can enter is 100%")
    the user selects the checkbox       agree-terms-page
    the user clicks the button/link     jQuery = button:contains("Mark as complete")
    the user clicks the button/link     link = Application overview

the finance overview is marked as incomplete
    the user clicks the button/link    link = Finances overview
    the user should see the element    jQuery = .warning-alert:contains("This competition only accepts collaborations. At least 2 partners must request funding.")
    the user clicks the button/link    link = Application overview

the application cannot be submited
    the user clicks the button/link                   link = Review and submit
    the user should see that the element is disabled  jQuery = button:contains("Submit application")
    the user clicks the button/link                   link = Application overview

the lead invites already registered user
    the user fills in the inviting steps           ${collaborator1_credentials["email"]}
    the user clicks the button/link                jQuery=button:contains("Save and return to application overview")
    Logout as user
    the user reads his email and clicks the link   ${collaborator1_credentials["email"]}   Invitation to collaborate in ${ATIcompetitionTitle}    You will be joining as part of the organisation    2
    the user clicks the button/link                link = Continue
    logging in and error checking                  &{collaborator1_credentials}
    the user clicks the button/link                css = .govuk-button[type="submit"]    #Save and continue
    the user clicks the button/link                link = Your finances
    the user marks the finances as complete        ${ATIapplicationTitle}   Calculate  52,214  yes
    Log in as a different user                     &{lead_applicant_credentials}
    the user clicks the button/link                link = ${ATIapplicationTitle}
    the applicant completes Application Team

the user does not see state aid information
    the user clicks the button/link      link = Your organisation
    the user should not see the element  link = eligible for state aid
    the user clicks the button/link      link = Your finances