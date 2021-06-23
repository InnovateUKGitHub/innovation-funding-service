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
Auditor can view correct number of competitions in live tab
    [Documentation]  IFS-9884  IFS-9885
    Given log in as a different user                                    &{auditorCredentials}
    Then auditor views correct number of live competitions
    And the user should not see the element                             jQuery = a:contains("Upcoming")
    And the user should not see the element                             jQuery = a:contains("Non-IFS")

Auditor can view correct number of competitions in project setup tab
    [Documentation]  IFS-9885
    When the user clicks the button/link                     jQuery = a:contains(Project setup)
    Then page should contain element                         jQuery = a:contains("${psTabCompCount}")

Auditor can view correct number of competitions in previous tab
    [Documentation]  IFS-9885
    When the user clicks the button/link                    jQuery = a:contains(Previous)
    Then page should contain element                        jQuery = a:contains("${previousTabCompCount}")

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
    the user logs-in in new browser                                    &{ifs_admin_user_credentials}
    ifs admin gets the counts of competitions in live tab
    ifs admin gets the counts of competitions in project setup tab
    ifs admin gets the counts of competitions in previous tab

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

Ifs admin gets the counts of competitions in live tab
    ${openCompCount} =              get text        jQuery = section:nth-child(1) h2
    set suite variable  ${openCompCount}
    ${closedCompCount} =            get text        jQuery = section:nth-child(2) h2
    set suite variable  ${closedCompCount}
    ${inAssessmentCompCount} =      get text        jQuery = section:nth-child(3) h2
    set suite variable  ${inAssessmentCompCount}
    ${panelCompCount} =             get text        jQuery = section:nth-child(4) h2
    set suite variable  ${panelCompCount}
    ${informCompCount} =            get text        jQuery = section:nth-child(5) h2
    set suite variable  ${informCompCount}
    ${liveTabCompCount} =    get text               id = section-1
    set suite variable  ${liveTabCompCount}

Ifs admin gets the counts of competitions in project setup tab
    ${psTabCompCount} =    get text           id = section-4
    set suite variable  ${psTabCompCount}

Ifs admin gets the counts of competitions in previous tab
    ${previousTabCompCount} =    get text           id = section-5
    set suite variable  ${previousTabCompCount}

Auditor views correct number of live competitions
    page should contain element     jQuery = a:contains("${liveTabCompCount}")
    page should contain element     jQuery = h2:contains("${openCompCount}")
    page should contain element     jQuery = h2:contains("${closedCompCount}")
    page should contain element     jQuery = h2:contains("${inAssessmentCompCount}")
    page should contain element     jQuery = h2:contains("${panelCompCount}")
    page should contain element     jQuery = h2:contains("${informCompCount}")
