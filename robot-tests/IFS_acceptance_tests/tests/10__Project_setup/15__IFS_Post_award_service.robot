*** Settings ***
Documentation     IFS-6454  Ability to push projects through to ACC
...
...               IFS- 7017  Update notifications & IFS when a project is live to point them to ACC instead of _Connect
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${postAwardServiceCompetitionName}             Post Award Competition
${nonPostAwardServiceCompetitionName}          Non Post Award Competition
${postAwardServiceLink}                        Post award service
${postAwardServiceTitle}                       Choose the post award service
${postAwardServiceGuide}                       You cannot change the post award service for any projects in this competition that are already live.
${projectSetupPostAwardCompetitionName}        Post award service competition
${projectSetupPostAwardApplicationName}        Post award application
${projectSetupPostAwardCompetitionId}          ${competition_ids["${projectSetupPostAwardCompetitionName}"]}
${grantFundProjectSetupDashboard}              ${server}/project-setup-management/competition/${projectSetupPostAwardCompetitionId}/status/all
${projectSetupNonPostAwardCompetitionName}     Project setup loan comp
${projectSetupNonPostAwardCompetitionId}       ${competition_ids["${projectSetupNonPostAwardCompetitionName}"]}
${NonPostAwardProjectSetupDashboard}           ${server}/project-setup-management/competition/${projectSetupNonPostAwardCompetitionId}/status/all
${projectSetupConnectCompetitionName}          Connect competition
${projectSetupConnectApplicationName}          Connect application
${projectSetupConnectCompetitionId}            ${competition_ids["${projectSetupConnectCompetitionName}"]}
${connectCompetitionProjectSetupDashboard}     ${server}/project-setup-management/competition/${projectSetupConnectCompetitionId}/status/all
${viewAndUpdateCompetitionDetailsLink}         View and update competition details
${projectManagerEmailLeadOrganisation}         troy.ward@gmail.com
${financeContactEmailLeadOrganisation}         sian.ward@gmail.com
${otherTeamMemberLeadOrganisation}             megan.rowland@gmail.com
${financeContactPartnerOrganisation}           jake.reddy@gmail.com
${financeContactOtherPartnerOrganisation}      eve.smith@gmail.com
${projectLiveMessage}                          The project is now live.
${grnatServiceUrl}                             https://grants.innovateuk.org
${postAwardServiceUrl}                         ${server}/live-projects-landing-page

*** Test Cases ***
Competition Setup - Post award service link should not display for any other funding type except grant funding
     [Documentation]   IFS-6454
     Given the user clicks create a competition button
     When the user fills in the CS Initial details         ${nonPostAwardServiceCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  KTP
     Then the user should not see the element              link = ${postAwardServiceLink}

Competition Setup - Ifs admin can access post award service form details for grant funding type competition
     [Documentation]   IFS-6454
     Given the user clicks create a competition button
     When the user fills in the CS Initial details           ${postAwardServiceCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     And the user clicks the button/link                     link = ${postAwardServiceLink}
     Then the user check for post award service fields
     And the user sees that the radio button is selected     postAwardService    CONNECT

Competition Setup - Ifs admin can save the post award service selection
     [Documentation]   IFS-6454
     When the user selects the radio button      postAwardService  IFS_POST_AWARD
     And the user clicks the button/link         css = [value="Save and return to competition"]
     Then the user check for selected value      IFS_POST_AWARD

Competition Setup - comp admin should not see the post award service link for any fundtype
     [Documentation]   IFS-6454
     Given log in as a different user             &{Comp_admin1_credentials}
     And the user navigates to the page           ${CA_UpcomingComp}
     When the user clicks the button/link         link = ${postAwardServiceCompetitionName}
     Then the user should not see the element     link = ${postAwardServiceLink}

Competition Setup - Finance manager should not see the post award service link for any fund type
     [Documentation]   IFS-6454
     Given log in as a different user             &{internal_finance_credentials}
     And the user navigates to the page           ${CA_UpcomingComp}
     When the user clicks the button/link         link = ${postAwardServiceCompetitionName}
     Then the user should not see the element     link = ${postAwardServiceLink}

Project Setup - IFS admin can edit the post award service options
     [Documentation]   IFS-6454
     Given log in as a different user                           &{ifs_admin_user_credentials}
     And the user navigates to the page                         ${grantFundProjectSetupDashboard}
     When the user clicks the button/link                       link = ${viewAndUpdateCompetitionDetailsLink}
     And the user edits form with post award service option
     Then the user check for selected value                     IFS_POST_AWARD

Project Setup - view and update competition details link should not display for comp admin
     [Documentation]   IFS-6454
     Given log in as a different user             &{Comp_admin1_credentials}
     When the user navigates to the page          ${grantFundProjectSetupDashboard}
     Then the user should not see the element     link = ${viewAndUpdateCompetitionDetailsLink}

Project Setup - view and update competition details link should not display for finance manager
     [Documentation]   IFS-6454
     Given log in as a different user             &{internal_finance_credentials}
     When the user navigates to the page          ${grantFundProjectSetupDashboard}
     Then the user should not see the element     link = ${viewAndUpdateCompetitionDetailsLink}

Project Setup - Post award service link should not display for any other fund type except grant fund
     [Documentation]   IFS-6454
     Given log in as a different user             &{ifs_admin_user_credentials}
     And the user navigates to the page           ${NonPostAwardProjectSetupDashboard}
     When the user clicks the button/link         link = ${viewAndUpdateCompetitionDetailsLink}
     Then the user should not see the element     link = ${postAwardServiceLink}

Emails - Project manager and finance contacts should receive an email notification stating project is live with a link to setup projcet page
     [Setup]  Request a project id of post award service application
     When the internal user approve the GOL                                            ${postAwardServiceProjectID}
     Then project manager and finance contact should receive an email notification

Applicant - Other team members should see message project is live on GOL approval
     [Documentation]  IFS-7017
     Given log in as a different user            ${otherTeamMemberLeadOrganisation}         ${short_password}
     When the user navigates to the page         ${server}/project-setup/project/${postAwardServiceProjectID}
     Then the user should see the element        jQuery = p:contains("${projectLiveMessage}")

Applicant - Project manager should see message project is live with review its progress link on GOL approval
     [Documentation]  IFS-7017
     Given log in as a different user         ${projectManagerEmailLeadOrganisation}     ${short_password}
     When the user navigates to the page      ${server}/project-setup/project/${postAwardServiceProjectID}
     Then the user should see the element     jQuery = p:contains("${reviewProgressMessage}")
     And the user should see the element      link = ${reviewProgressLink}

Applicant - Finance contact of lead organisation should see message project is live with review its progress link on GOL approval
     [Documentation]  IFS-7017
     Given log in as a different user         ${financeContactEmailLeadOrganisation}     ${short_password}
     when the user navigates to the page      ${server}/project-setup/project/${postAwardServiceProjectID}
     Then the user should see the element     jQuery = p:contains("${reviewProgressMessage}")
     And the user should see the element      link = ${reviewProgressLink}

Applicant - Finance contact of partner organisation should see message project is live with review its progress link on GOL approval
     [Documentation]  IFS-7017
     Given log in as a different user         ${financeContactPartnerOrganisation}       ${short_password}
     When the user navigates to the page      ${server}/project-setup/project/${postAwardServiceProjectID}
     Then the user should see the element     jQuery = p:contains("${reviewProgressMessage}")
     And the user should see the element      link = ${reviewProgressLink}

Applicant - User should be redirected to IFS post award service on click review its progress for post award service applications
     [Documentation]  IFS-7017
     Given log in as a different user                       ${projectManagerEmailLeadOrganisation}     ${short_password}
     When the user navigates to the page                    ${server}/project-setup/project/${postAwardServiceProjectID}
     And the user clicks the button/link                    link = ${reviewProgressLink}
     Then Url should contain live projects landing page

Applicant - User should be redirected to grant application service on click review its progress for connect applications
     [Documentation]  IFS-7017
     [Setup]  Request a project id of connect service application
     Given the user navigates to the page               ${server}/dashboard-selection
     And the internal user approve the GOL              ${connectServiceProjectID}
     When applicant clicks review its progress link     ${connectServiceProjectID}
     Then the user should see the element               link = _connect

Applicant - User should be redirected to IFS post award service on click projects tile in dashboard for post award service applications
     [Documentation]  IFS-7017
     Given the user navigates to the page                        ${server}/dashboard-selection
     And log in as a different user                              ${projectManagerEmailLeadOrganisation}     ${short_password}
     And the user clicks the live project tile in dashboard      id = dashboard-link-LIVE_PROJECTS_USER
     Then Url should contain live projects landing page


*** Keywords ***
Custom Suite Setup
    Connect to database  @{database}
    The user logs-in in new browser   &{ifs_admin_user_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database

the user clicks create a competition button
    the user navigates to the page      ${CA_UpcomingComp}
    the user clicks the button/link     jQuery = .govuk-button:contains("Create competition")

the user check for post award service fields
    the user should see the element     jQuery = h1:contains("${postAwardServiceTitle}")
    the user should see the element     jQuery = p:contains("${postAwardServiceGuide}")
    the user should see the element     jQuery = legend:contains("${postAwardServiceTitle}:")
    the user should see the element     css = [for="postAwardService-IFS_POST_AWARD"]
    the user should see the element     css = [for="postAwardService-CONNECT"]
    the user should see the element     css = [value="Save and return to competition"]
    the user should see the element     css = [value="Save and return to competition"]
    the user should see the element     link = Back to competition details

the user check for selected value
    [Arguments]  ${postAwardLabel}
    the user clicks the button/link                     link = ${postAwardServiceLink}
    the user sees that the radio button is selected     postAwardService    ${postAwardLabel}

the user edits form with post award service option
     And the user clicks the button/link      link = ${postAwardServiceLink}
     And the user selects the radio button    postAwardService  IFS_POST_AWARD
     And the user clicks the button/link      css = [value="Save and return to competition"]

Request a project id of post award service application
     ${postAwardServiceProjectID} =      get project id by name             ${projectSetupPostAwardApplicationName}
     Set suite variable                  ${postAwardServiceProjectID}

Request a project id of connect service application
     ${connectServiceProjectID} =      get project id by name               ${projectSetupConnectApplicationName}
     Set suite variable                ${connectServiceProjectID}

Requesting application ID of post award service application
     ${postAwardServiceApplicationID} =      get application id by name         ${projectSetupPostAwardApplicationName}
     Set suite variable                  ${postAwardServiceApplicationID}

the user should check for message and link
     [Arguments]    ${url}  ${message}
     the user should see the element     jQuery = p:contains("${message}")
     the user should see the element     link = ${reviewProgressLink}

Url should contain live projects landing page
    ${Url} =   Get Location
    Run Keyword And Ignore Error Without Screenshots  should be equal as strings  ${Url}   ${postAwardServiceUrl}

applicant clicks review its progress link
    [Arguments]  ${projectID}
    log in as a different user          ${projectManagerEmailLeadOrganisation}     ${short_password}
    the user navigates to the page      ${server}/project-setup/project/${projectID}
    the user clicks the button/link     link = ${reviewProgressLink}

project manager and finance contact should receive an email notification
    Requesting application ID of post award service application
    the user reads his email     ${projectManagerEmailLeadOrganisation}      Grant offer letter approved for project ${postAwardServiceApplicationID}   We have accepted your signed grant offer letter for your project:
    the user reads his email     ${financeContactEmailLeadOrganisation}      Grant offer letter approved for project ${postAwardServiceApplicationID}   We have accepted your signed grant offer letter for your project:
    the user reads his email     ${financeContactPartnerOrganisation}        Grant offer letter approved for project ${postAwardServiceApplicationID}   We have accepted your signed grant offer letter for your project:
    the user reads his email     ${financeContactOtherPartnerOrganisation}   Grant offer letter approved for project ${postAwardServiceApplicationID}   We have accepted your signed grant offer letter for your project:

the user clicks the live project tile in dashboard
    [Arguments]   ${locator}
    :FOR    ${i}    IN RANGE  10
    \  ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    the user should see the element     ${locator}
    \  Exit For Loop If  '${status}'=='PASS'
    \  run keyword if  '${status}'=='FAIL'   log in as a different user  ${projectManagerEmailLeadOrganisation}     ${short_password}
    \  ${i} =  Set Variable  ${i + 1}
    the user clicks the button/link       ${locator}
