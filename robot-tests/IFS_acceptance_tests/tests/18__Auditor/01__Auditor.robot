*** Settings ***
Documentation     IFS-9884 Auditor role: create role
...
...               IFS-9885 Auditor role: modify role
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Variables ***
${competitionToSearch}       Project Setup Comp 17
${applicationToSearch}       PSC application 17
${applicationIdToSearch}     ${application_ids["${applicationToSearch}"]}


*** Test Cases ***
Auditor can view correct number of live, project setup and previous tab competitions
    [Documentation]  IFS-9884  IFS-9885
    Given the user logs-in in new browser                               &{auditorCredentials}
    Then the user views correct number of coompetitions in live tab
    And the user should not see the element                             jQuery = a:contains("Upcoming")
    And the user should not see the element                             jQuery = a:contains("Non-IFS")

Auditor can view correct number of competitions in project setup
    [Documentation]  IFS-9885
    [Setup]  Get number of competitions in project setup
    Given the user clicks the button/link                       jQuery = a:contains(Project setup)
    Then page should contain element                            jQuery = a:contains("Project setup (${psCompCount})")

Auditor can search for competition
    [Documentation]  IFS-9885
    Given the user enters text to a text field    searchQuery   	${competitionToSearch}
    When the user clicks the button/link          id = searchsubmit
    And the user clicks the button/link           link = ${competitionToSearch}
    Then the user should see the element          jQuery = .govuk-heading-s:contains("${applicationToSearch}")

Auditor can search for an application number
    [Documentation]  IFS-9885
    Given the user clicks the button/link         id = dashboard-navigation-link
    And the user enters text to a text field      searchQuery   	${applicationIdToSearch}
    When the user clicks the button/link          id = searchsubmit
    And the user clicks the button/link           link = ${applicationIdToSearch}
    Then the user should see the element          jQuery = span:contains("${applicationToSearch}")
    And the user should see the element           jQuery = button:contains("Application team")

Auditor can not apply to a competition as an applicant
    [Documentation]  IFS-9885
    Given the user select the competition and starts application     ${openCompetitionPerformance_name}
    Then page should contain                                         ${403_error_message}

Auditor can not be added as a collaborator to an application
    [Documentation]  IFS-9885
    Given log in as a different user                      &{lead_applicant_credentials}
    And existing user starts a new application            ${openCompetitionPerformance_name}  ${EMPIRE_LTD_ID}   Choose the lead organisation
    When the lead invites already registered user         ${auditorCredentials["email"]}  ${openCompetitionPerformance_name}
    And the guest user inserts user email and password    Amy.Wigley@ukri.org     ${short_password}
    And the guest user clicks the log-in button
    Then page should contain                              ${403_error_message}


*** Keywords ***
Custom suite setup
    Connect to Database  @{database}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user views correct number of coompetitions in live tab
    the total calculation in dashboard should be correct     Open    //section[1]/ul/li
    the total calculation in dashboard should be correct     Closed    //section[2]/ul/li
    the total calculation in dashboard should be correct     In assessment    //section[3]/ul/li
    the total calculation in dashboard should be correct     Panel    //section[4]/ul/li
    the total calculation in dashboard should be correct     Inform    //section[5]/ul/li
    the total calculation in dashboard should be correct     Live    //section/ul/li

Get number of competitions in project setup
    ${psCompCount} =   Get count of project setup competitions
    Set suite variable  ${psCompCount}

Get count of project setup competitions
    ${result} =  query  SELECT COUNT(*) FROM `${database_name}`.`competition` where `project_setup_started` is NOT null;
    ${result} =  get from list  ${result}  0
    ${compCount} =   get from list  ${result}  0
    [Return]  ${compCount}