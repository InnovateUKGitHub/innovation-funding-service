*** Settings ***
Documentation   IFS-2284 Assign new Ts and Cs for APC competition type template
...             IFS-2286 APC Competition type template
Suite Setup     Custom Suite Setup
Suite Teardown  Close browser and delete emails
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot


*** Variables ***
${apcCompetitionTitle}  Advanced Propulsion Centre Competition
${apcApplicationTitle}  Advanced Propulsion Centre Application


*** Test Cases ***
Comp Admin creates an APC competition
    [Documentation]  IFS-2284, IFS-2286
    [Tags]  HappyPath
    Given The user logs-in in new browser          &{Comp_admin1_credentials}
    And the user navigates to the page             ${CA_UpcomingComp}
    When the user clicks the button/link           link=Create competition
    Then the user fills in the CS Initial details  ${apcCompetitionTitle}  ${month}  ${nextyear}  Advanced Propulsion Centre
    And the user fills in the CS Funding Information
    And the user fills in the CS Eligibility      ${RTO_TYPE_ID}
    And the user fills in the CS Milestones       ${month}  ${nextyear}
    And the user fills in the CS Application section with custom questions  yes  ${compType_APC}
    And the user fills in the CS Assessors
    When the user clicks the button/link           link=Public content
    Then the user fills in the Public content and publishes  APC
    When the user clicks the button/link           link=Return to setup overview
    Then the user should see the element           jQuery=div:contains("Public content") ~ .task-status-complete
    When the user clicks the button/link           jQuery=a:contains("Complete")
    Then the user clicks the button/link           jQuery=a:contains("Done")

Requesting the id of this Competition
    [Documentation]  retrieving the id of the competition so that we can use it in urls
    [Tags]  MySQL
    ${apcCompetitionId} =  get comp id from comp title  ${apcCompetitionTitle}
    Set suite variable  ${apcCompetitionId}

*** Keywords ***
Custom Suite Setup
    ${month} =          get tomorrow month
    set suite variable  ${month}
    ${nextyear} =       get next year
    Set suite variable  ${nextyear}
    ${tomorrowday} =    get tomorrow day
    Set suite variable  ${tomorrowday}
    The guest user opens the browser