*** Settings ***
Documentation     IFS-6454  Ability to push projects through to ACC
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${postAwardServiceCompetitionName}             Post Award Service Competition
${nonPostAwardServiceCompetitionName}          Non Post Award Service Competition
${postAwardServiceLink}                        Post award service
${postAwardServiceTitle}                       Choose the post award service
${postAwardServiceGuide}                       You cannot change the post award service for any projects in this competition that are already live.
${projectSetupPostAwardCompetitionName}        Post award service competition
${projectSetupPostAwardCompetitionId}          ${competition_ids["${projectSetupPostAwardCompetitionName}"]}
${grantFundProjectSetupDashboard}              ${server}/project-setup-management/competition/${projectSetupPostAwardCompetitionId}/status/all
${projectSetupNonPostAwardCompetitionName}     Project setup loan comp
${projectSetupNonPostAwardCompetitionId}       ${competition_ids["${projectSetupNonPostAwardCompetitionName}"]}
${NonPostAwardProjectSetupDashboard}           ${server}/project-setup-management/competition/${projectSetupNonPostAwardCompetitionId}/status/all
${viewAndUpdateCompetitionDetailsLink}         View and update competition details

*** Test Cases ***
Competition Setup - Post award service link should not display for any other funding type except grant funding
     Given the user clicks create a competition button
     When the user fills in the CS Initial details         ${nonPostAwardServiceCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  KTP
     Then the user should not see the element              link = ${postAwardServiceLink}

Competition Setup - Ifs admin can access post award service form details for grant funding type competition
     Given the user clicks create a competition button
     When the user fills in the CS Initial details           ${postAwardServiceCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     And the user clicks the button/link                     link = ${postAwardServiceLink}
     Then the user check for post award service fields
     And the user sees that the radio button is selected     postAwardService    CONNECT

Competition Setup - Ifs admin can save the post award service selection
     When the user selects the radio button      postAwardService  IFS_POST_AWARD
     And the user clicks the button/link         css = [value="Save and return to competition"]
     Then the user check for selected value      IFS_POST_AWARD

Competition Setup - comp admin should not see the post award service link for any fundtype
     Given log in as a different user             &{Comp_admin1_credentials}
     And the user navigates to the page           ${CA_UpcomingComp}
     When the user clicks the button/link         link = ${postAwardServiceCompetitionName}
     Then the user should not see the element     link = ${postAwardServiceLink}

Competition Setup - Finance manager should not see the post award service link for any fund type
     Given log in as a different user             &{internal_finance_credentials}
     And the user navigates to the page           ${CA_UpcomingComp}
     When the user clicks the button/link         link = ${postAwardServiceCompetitionName}
     Then the user should not see the element     link = ${postAwardServiceLink}

Project Setup - IFS admin can edit the post award service options
     Given log in as a different user                           &{ifs_admin_user_credentials}
     And the user navigates to the page                         ${grantFundProjectSetupDashboard}
     When the user clicks the button/link                       link = ${viewAndUpdateCompetitionDetailsLink}
     And the user edits form with post award service option
     Then the user check for selected value                     IFS_POST_AWARD

Project Setup - view and update competition details link should not display for comp admin
     Given log in as a different user             &{Comp_admin1_credentials}
     When the user navigates to the page          ${grantFundProjectSetupDashboard}
     Then the user should not see the element     link = ${viewAndUpdateCompetitionDetailsLink}

Project Setup - view and update competition details link should not display for finance manager
     Given log in as a different user             &{internal_finance_credentials}
     When the user navigates to the page          ${grantFundProjectSetupDashboard}
     Then the user should not see the element     link = ${viewAndUpdateCompetitionDetailsLink}

Project Setup - Post award service link should not display for any other fund type except grant fund
     Given log in as a different user             &{ifs_admin_user_credentials}
     And the user navigates to the page           ${NonPostAwardProjectSetupDashboard}
     When the user clicks the button/link         link = ${viewAndUpdateCompetitionDetailsLink}
     Then the user should not see the element     link = ${postAwardServiceLink}


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