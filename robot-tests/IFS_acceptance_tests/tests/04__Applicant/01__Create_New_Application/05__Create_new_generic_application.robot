*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot
Resource          ../../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${competitionName}  Generic competition for TsnCs

*** Test Cases ***
User can edit the assesed question
    [Documentation]    IFS-747
    [Tags]
    [Setup]  logged in user applies to competition    ${openGenericCompetition}  1
    Given the user should not see the element         a:contains("7.")  # This comp has only 1 question
    When the user clicks the button/link              link = 1. Generic question title
    Then the user should see the element              jQuery = button:contains("Mark as complete")

CompAdmin creates a new Generic competition
    [Documentation]  IFS-3261
    [Tags]  HappyPath
    [Setup]  log in as a different user                &{Comp_admin1_credentials}
    The competition admin creates competition      4  ${competitionName}  Generic  Generic  2  GRANT  project-setup-completion-stage  no  1  true  collaborative

Requesting the id of this Competition and moving to Open
    [Documentation]  IFS-3261
    ...   retrieving the id of the competition so that we can use it in urls
    [Tags]  MySQL  HappyPath
    ${competitionId} =  get comp id from comp title  ${competitionName}
    Set suite variable  ${competitionId}
    The competition moves to Open state  ${competitionId}

Applicant Applies to Generic competition and is able to see the Ts&Cs
    [Documentation]  IFS-1012  IFS-2879
    [Tags]  HappyPath
    [Setup]  Log in as a different user             becky.mason@gmail.com  ${short_password}
    Given logged in user applies to competition     ${competitionName}   4
    When the user clicks the button/link            link = Application details
    Then the user fills in the Application details  Application Ts&Cs  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link            link = View the competition terms and conditions
    Then the user should see the element            jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser  &{lead_applicant_credentials}