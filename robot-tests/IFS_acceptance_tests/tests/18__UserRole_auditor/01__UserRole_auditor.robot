*** Settings ***
Documentation     IFS-9884 Auditor role: create role
...
...               IFS-9885 Auditor role: modify role
...
...               IFS-9886 Auditor role: update journey in project setup
...
...               IFS-9887 Auditor role: modify journey
...
...               IFS-9882 download permission error
...
...               IFS-9986 Auditor bug bash: Auditors should not see 'in progress' applications in the wildcard search
...
...               IFS-10001 Auditor permission error on funding rules button
...
...               IFS-10000 Auditor gets permission error when tries to open payment milestone
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
${competitionName}           Rolling stock future developments
${competitionID}             ${competition_ids['${competitionName}']}
${applicationName1}          Super-EFFY - Super Efficient Forecasting of Freight Yields
${projectID1}                ${project_ids['${applicationName1}']}
${applicationName2}          London underground - enhancements to existing stock and logistics
${projectID2}                ${project_ids['${applicationName2}']}
${ktpCompetitionName}        KTP notifications
${ktpCompetitionId}          ${competition_ids['${ktpCompetitionName}']}
${ktpApplicationName}        KTP notifications application
${ktpApplicationId}          ${application_ids['${ktpApplicationName}']}
${auditorApplication}        Auditor application
${auditorProjectId}          ${project_ids['${auditorApplication}']}
${applicationName3}          Climate science the history of Greenland's ice
${competitionName3}          Predicting market trends programme
${competitionID3}            ${competition_ids['${competitionName3}']}
${applicationName4}          A subsidy control application in project setup
${projectID4}                ${project_ids['${applicationName4}']}
${sbriApplicationName2}      SBRI application
${sbriProjectId}             ${project_ids['${sbriApplicationName2}']}

*** Test Cases ***
Auditor can view correct number of competitions in live tab
    [Documentation]  IFS-9884  IFS-9885
    Given log in as a different user                           &{auditorCredentials}
    Then auditor views correct number of live competitions
    And the user should not see the element                    jQuery = a:contains("Upcoming")
    And the user should not see the element                    jQuery = a:contains("Non-IFS")

Auditor can view correct number of competitions in project setup and previous tabs
    [Documentation]  IFS-9885
    Given log in as a different user                                        &{ifs_admin_user_credentials}
    And ifs admin gets the counts of competitions in project setup tab
    And ifs admin gets the counts of competitions in previous tab
    When log in as a different user                                         &{auditorCredentials}
    Then page should contain element                                        jQuery = a:contains("${psTabCompCount}")
    And page should contain element                                         jQuery = a:contains("${previousTabCompCount}")

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
    And login to application                              Amy.Wigley@ukri.org     ${short_password}
    Then page should contain                              ${403_error_message}

Auditor can view Project detials in the Project setup
    [Documentation]  IFS-9886
    Given log in as a different user                            &{auditorCredentials}
    And the user navigates to the page                          ${SERVER}/project-setup-management/competition/${competitionID}/status/all
    When the user clicks the button/link                        jQuery = #table-project-status tr:nth-of-type(1) td:nth-of-type(1) a:contains("Complete")
    Then the user sees the read only view of project details

Auditor can view Project team in the project setup
    [Documentation]  IFS-9886
    Given the user navigates to the page                    ${SERVER}/project-setup-management/competition/${competitionID}/status/all
    When the user clicks the button/link                    jQuery = #table-project-status tr:nth-of-type(1) td:nth-of-type(2) a:contains("Complete")
    Then the user sees the read only view of project team

Auditor can view MO in the project setup
    [Documentation]  IFS-9886
    Given the user navigates to the page       ${SERVER}/project-setup-management/competition/${competitionID}/status/all
    When the user clicks the button/link       jQuery = #table-project-status tr:nth-of-type(1) td:nth-of-type(4) a:contains("Assigned")
    Then the user sees read only view of MO

Auditor can view the finance details of the organisations
    [Documentation]  IFS-9886  IFS-9887
    Then the user sees the read only view of completed finance checks
    And the user sees the read only view of under review finance checks

Auditor can view the bank details with the 'Complete' status for the organisations
    [Documentation]  IFS-9886
    Given the user navigates to the page       ${SERVER}/project-setup-management/project/${projectID1}/organisation/${Crystalrover_Id}/review-bank-details
    Then the user should see the element       jQuery = h2:contains("Crystalrover - Account details")
    And the user should not see the element    jQuery = button:contains("Approve bank account details")
    And the user should not see the element    jQuery = a:contains("Change bank account details")

Auditor can open and view the GOL for the organisations
    [Documentation]  IFS-9886    IFS-9882
    Given log in as a different user                    &{auditorCredentials}
    And the user navigates to the page                  ${SERVER}/project-setup-management/project/${auditorProjectId}/grant-offer-letter/send
    And the user should see the element                 jQuery = h1:contains("Grant offer letter")
    When the user clicks the button/link                jQuery = a:contains(".pdf (opens in a new window)")
    Then the user should not see an error in the page
    And the user should not see the element             jQuery = label:contains("Upload")

Auditor cannot view the bank details with the 'Review' status for the organisation
    [Documentation]  IFS-9886
    Given the user navigates to the page            ${SERVER}/project-setup-management/competition/${competitionID}/status/all
    Then the user cannot click the review button

Auditor can open and view the fEC model certificate in the project setup
    [Documentation]  IFS-9882
    Given Log in as a different user                     &{auditorCredentials}
    And the user navigates to the page                   ${SERVER}/management/competition/${ktpCompetitionId}/application/${ktpApplicationId}
    And the user clicks the button/link                  jQuery =button:contains("Finances summary")
    And the user clicks the button/link                  jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    And The user clicks the button/link                  jQuery = a:contains("Your fEC model")
    When the user clicks the button/link                 jQuery = a:contains(".pdf (opens in a new window)")
    Then the user should not see internal server and forbidden errors

Innovation lead cannot see inprogress applications
    [Documentation]  IFS-9986
    Given Log in as a different user                                       &{innovation_lead_one}
    When the user enters text to a text field                              id = searchQuery  ${applicationId3}
    And the user clicks the button/link                                    id = searchsubmit
    Then the user should not see the element                               jQuery = td:contains("${applicationName3}")
    
Stakeholder lead cannot see inprogress applications
    [Documentation]  IFS-9986
    Given Log in as a different user                                       &{stakeholder_user}
    And the user clicks the delivery partner tile if displayed
    When the user enters text to a text field                              id = searchQuery  ${applicationId3}
    And the user clicks the button/link                                    id = searchsubmit
    Then the user should not see the element                               jQuery = td:contains("${applicationName3}")
    And the user navigates to the page and gets a custom error message     ${server}/management/competition/${competitionID3}/application/${applicationId3}   ${403_error_message}

Auditor cannot see inprogress applications
    [Documentation]  IFS-9986
    Given Log in as a different user                                       &{auditorCredentials}
    When the user enters text to a text field                              id = searchQuery  ${applicationId3}
    And the user clicks the button/link                                    id = searchsubmit
    Then the user should not see the element                               jQuery = td:contains("${applicationName3}")

Auditor can view funding rules in project setup
    [Documentation]  IFS-10001
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${projectID4}/finance-check
    When the user clicks the button/link        jQuery = tr:nth-child(1) td:nth-child(2)
    Then the user should see the element        jQuery = h1:contains("Funding rules check")
    And the user should see the element         jQuery = dt:contains("Funding rules selected") ~ dd:contains("Subsidy control")
    And the user should not see the element     jQuery = a:contains("Edit")

Auditor can view payment milestones in project setup
    [Documentation]  IFS-10000
    Given the user navigates to the page        ${SERVER}/project-setup-management/project/${sbriProjectId}/finance-check
    When the user clicks the button/link        jQuery = tr:nth-child(1) td:nth-child(6) a:contains("Review")
    Then the user should see the element        jQuery = h1:contains("Payment milestones")
    And the user should see the element         jQuery = h3:contains("Payment milestone overview")
    And the user should not see the element     jQuery = a:contains("Edit payment milestones")

*** Keywords ***
Custom suite setup
    Connect to Database  @{database}
    the user logs-in in new browser                                    &{ifs_admin_user_credentials}
    ${applicationId3} =  get application id by name  ${applicationName3}
    Set suite variable  ${applicationId3}
    ifs admin gets the counts of competitions in live tab

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

the user sees the read only view of project details
    the user should see the element       jQuery = h2:contains("Project details")
    the user should not see the element   jQuery = a:contains("Manage project status")
    the user should not see the element   jQuery = a:contains("Edit")

the user sees the read only view of project team
    the user should see the element       jQuery = h1:contains("Project team")
    the user should not see the element   jQuery = button:contains("Add team member")

the user sees read only view of MO
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID1}/monitoring-officer
    the user should see the element        jQuery = h1:contains("Monitoring officer")
    the user should not see the element    jQuery = a:contains("Change monitoring officer")

the user sees the read only view of completed finance checks
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID1}/finance-check/organisation/${Crystalrover_Id}/viability
    the user should see the element        jQuery = h1:contains("Viability check for Crystalrover")
    the user should not see the element    jQuery = span:contains("Reset viability check")
    the user clicks the button/link        jQuery = a:contains("Return to finance checks")
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID1}/finance-check/organisation/${Crystalrover_Id}/eligibility
    the user should see the element        jQuery = h1:contains("Eligibility check for Crystalrover")
    the user should not see the element    jQuery = span:contains("Reset eligibility check")
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID1}/finance-check/organisation/${Crystalrover_Id}/query
    the user should see the element        jQuery = h1:contains("Queries for Crystalrover")
    the user should not see the element    jQuery = a:contains("Post a new query")
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID1}/finance-check/organisation/${Crystalrover_Id}/note
    the user should not see the element    id="post-new-note"

the user sees the read only view of under review finance checks
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID2}/finance-check/organisation/${Gabtype_Id}/viability
    the user should see the element        jQuery = h1:contains("Viability check for Gabtype")
    the user should not see the element    jQuery = span:contains("Reset viability check")
    the user should not see the element    id = confirm-button
    the user clicks the button/link        jQuery = a:contains("Return to finance checks")
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID2}/finance-check/organisation/${Gabtype_Id}/eligibility
    the user should see the element        jQuery = h1:contains("Eligibility check for Gabtype")
    the user should not see the element    jQuery = a:contains("Edit project costs")
    the user should not see the element    id = confirm-button
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID2}/finance-check/organisation/${Gabtype_Id}/query
    the user should see the element        jQuery = h1:contains("Queries for Gabtype")
    the user should not see the element    jQuery = a:contains("Post a new query")
    the user navigates to the page         ${SERVER}/project-setup-management/project/${projectID2}/finance-check/organisation/${Gabtype_Id}/note
    the user should not see the element    id="post-new-note"

the user cannot click the review button
    ${pagination} =   Run Keyword And Ignore Error Without Screenshots   The user should see the element in the paginated list    jQuery = tr:nth-of-type(2) td:nth-of-type(5).status.action
    run keyword if    ${pagination} == 'PASS'   the element should be disabled       jQuery = tr:nth-of-type(2) td:nth-of-type(5).status.action:contains("Review")
