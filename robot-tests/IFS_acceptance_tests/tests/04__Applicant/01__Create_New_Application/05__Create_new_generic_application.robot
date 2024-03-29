*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
...
...               IFS-5920 Acceptance tests for T's and C's
...
...               IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...               IFS-8847 Always open competitions: new comp setup configuration
...
...               IFS-10172 Third party procurement: applicant-facing content changes
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${competitionName}  Generic competition for TsnCs

*** Test Cases ***
User can edit the assessed question
    [Documentation]    IFS-747
    [Tags]
    [Setup]  logged in user applies to competition    ${openGenericCompetition}  1
    Given the user should not see the element         a:contains("7.")  # This comp has only 1 question
    When the user clicks the button/link              link = 1. Generic question title
    Then the user should see the element              jQuery = button:contains("Mark")

CompAdmin creates a new Generic competition
    [Documentation]  IFS-3261  IFS-8779  IFS-8847
    [Tags]  HappyPath
    [Setup]  log in as a different user                  &{Comp_admin1_credentials}
    Given the competition admin creates competition      4  ${competitionName}  Generic  Generic  SUBSIDY_CONTROL  GRANT  PROJECT_SETUP  no  50  true  collaborative  No
    Then get competition id and set open date to yesterday  ${competitionName}

Applicant Applies to Generic competition and is able to see the Ts&Cs
    [Documentation]  IFS-1012  IFS-2879  IFS-5920  IFS-10172
    [Tags]  HappyPath
    [Setup]  Log in as a different user                          becky.mason@gmail.com  ${short_password}
    Given logged in user applies to competition                  ${competitionName}   4
    When the user clicks the button/link                         link = Application details
    Then the user fills in the Application details               Application Ts&Cs  ${tomorrowday}  ${month}  ${nextyear}
    When the user completes subsidy basis as subsidy control
    And the user clicks the button/link                          link = Award terms and conditions
    Then the user should see the element                         jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should see the element                          jQuery = .message-alert:contains("You must read and agree to the terms and conditions by ticking the box at the end of the page.")

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser  &{lead_applicant_credentials}
    Connect to database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database