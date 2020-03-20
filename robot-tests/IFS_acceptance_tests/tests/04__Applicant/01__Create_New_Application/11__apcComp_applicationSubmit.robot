*** Settings ***
Documentation     IFS-2284 Assign new Ts and Cs for APC competition type template
...
...               IFS-2286 APC Competition type template
...
...               IFS-1497  As an applicant I am able to confirm the project location for my organisation
...
...               IFS-4221  As an applicant I am only able to invite contributors to a single project type competition application
...
...               IFS-5920 Acceptance tests for T's and C's
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${apcCompetitionTitle}         Advanced Propulsion Centre Competition
${apcApplicationTitle}         Advanced Propulsion Centre Application
${submittedapplication}        ${server}/management/competition
${termaandconditionsHeading}   Innovate UK grant terms and conditions for an Advanced Propulsion Centre UK Ltd competition

*** Test Cases ***
Comp Admin creates an APC competition
    [Documentation]  IFS-2284, IFS-2286
    Given The user logs-in in new browser           &{Comp_admin1_credentials}
    Then the competition admin creates competition  ${business_type_id}  ${apcCompetitionTitle}  APC  ${compType_APC}  1  GRANT  project-setup-completion-stage  yes  1  true  single

Applicant applies to newly created APC competition
    [Documentation]  IFS-2286  IFS-4221  IFS-4222
    [Setup]   get competition id and set open date to yesterday  ${apcCompetitionTitle}
    Given Log in as a different user                        &{lead_applicant_credentials}
    Then logged in user applies to competition              ${apcCompetitionTitle}  1
    And the applicant cannot add a collaborator to a single comp
    And the applicant sees single comp finance summary

Applicant submits his application
    [Documentation]  IFS-2286  IFS-5920
    Given the user clicks the button/link               link = Application details
    When the user fills in the Application details      ${apcApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    Then the lead applicant fills all the questions and marks as complete(APC)
    When the user navigates to Your-finances page       ${apcApplicationTitle}
    And the user marks the finances as complete         ${apcApplicationTitle}   labour costs  54,000  yes
    And the user accept the competition terms and conditions    Return to application overview
    Then the applicant submits the application

The internal user checks accepted terms and conditions for submitted application
    [Documentation]  IFS-5920
    [Setup]  Get competitions id and set it as suite variable   ${apcCompetitionTitle}
    ${acpapplicationId} =  get application id by name  ${apcApplicationTitle}
    Given log in as a different user                   &{Comp_admin1_credentials}
    Then the internal user should see read only view of terms and conditions   ${submittedapplication}/${competitionId}/applications/submitted   ${acpapplicationId}   ${termaandconditionsHeading}

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Get competitions id and set it as suite variable
    [Arguments]  ${competitionTitle}
    ${competitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${competitionId}

the lead applicant fills all the questions and marks as complete(APC)
    the user marks the project details as complete
    the applicant completes application team
    :FOR  ${ELEMENT}    IN    @{APC_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the applicant cannot add a collaborator to a single comp
    the user clicks the button/link      link = Application team
    the user should not see the element  link = Add a partner organisation
    the user clicks the button/link      link = Application overview

the applicant sees single comp finance summary
    the user clicks the button/link      link = Finances overview
    the user should see the element      jQuery = .warning-alert:contains("You have not marked your project finances as complete")
    the user should not see the element  jQuery = .finance-summary th[scope = "row"]:contains("Total")
    the user clicks the button/link      link = Application overview


Custom suite teardown
    Close browser and delete emails
    Disconnect from database
