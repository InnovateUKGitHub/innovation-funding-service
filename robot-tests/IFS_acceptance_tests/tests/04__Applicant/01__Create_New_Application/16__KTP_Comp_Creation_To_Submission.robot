*** Settings ***
Documentation  IFS-7146  KTP - New funding type
...
...            IFS-7147  KTP - New set of Terms & Conditions
...
...            IFS-7148  Replace maximum funding level drop down menu with free type field in comp setup
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Variables ***
${KTPcompetitionTitle}  KTP Competition
${KTPapplicationTitle}  KTP Application

*** Test Cases ***
Comp Admin creates an KTP competition
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given The user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates competition      ${business_type_id}  ${KTPcompetitionTitle}  KTP  ${compType_Programme}  2  KTP  project-setup-completion-stage  yes  1  true  single

Comp Admin is able to see KTP funding type has been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user clicks the button/link      link = ${KTPcompetitionTitle}
    Given the user clicks the button/link         link = View and update competition setup
    When the user clicks the button/link          link = Initial details
    Then the user should see the element          jQuery = dt:contains("Funding type") ~ dd:contains("Knowledge Transfer Partnership (KTP)")
    [Teardown]  the user clicks the button/link   link = Competition setup

Comp Admin is able to see KTP T&C's have been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link     link = Terms and conditions
    Then the user should see the element      link = Knowledge Transfer Partnership (KTP)

Applicant applies to newly created KTP competition
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given get competition id and set open date to yesterday  ${KTPcompetitionTitle}
    When log in as a different user                          &{lead_applicant_credentials}
    Then logged in user applies to competition               ${KTPcompetitionTitle}  1

Applicant is able to complete and submit an application
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user completes the application
    Then the applicant submits the application

Moving KTP Competition to Project Setup
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given Log in as a different user                   &{internal_finance_credentials}
    Then moving competition to Closed                  ${competitionId}
    And making the application a successful project    ${competitionId}  ${KTPapplicationTitle}
    And moving competition to Project Setup            ${competitionId}

*** Keywords ***
The user completes the application
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                 ${KTPapplicationTitle}
    the user marks the finances as complete                  ${KTPapplicationTitle}   Calculate  52,214  yes
    the user accept the competition terms and conditions     Return to application overview

The user completes the research category
    [Arguments]  ${res_category}
    the user clicks the button/link      link=Research category
    the user selects the checkbox        researchCategory
    the user clicks the button/link      jQuery=label:contains("${res_category}")
    the user clicks the button/link      id=application-question-complete
    the user should see the element      jQuery=li:contains("Research category") > .task-status-complete

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Requesting Project ID of this Project
    ${ProjectID} =  get project id by name    ${KTPapplicationTitle}
    Set suite variable    ${ProjectID}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database