*** Settings ***
Documentation     IFS-9576 MO documents: 'Project setup' list - task management and filtering
...
...               IFS-9941 MO documents: Status, Filter and Link Visibility
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
&{emptyMonitoringOfficerDashboard}     email=Rupesh.Pereira@gmail.com  password=${short_password}
&{monitoringOfficerDashboard}          email=Bill.Mccoy@gmail.com  password=${short_password}


*** Test Cases ***
Monitoring officer can filter in-setup projects only
    [Documentation]  IFS-9576
    Given the user selects the checkbox                         projectInSetup
    When the user clicks the button/link                        id = update-documents-results-button
    Then check correct number of in-setup projects filtered     In setup

Monitoring officer can filter previous projects only
    [Documentation]  IFS-9576
    Given the user unselects the checkbox                       projectInSetup
    When the user selects the checkbox                          previousProject
    And the user clicks the button/link                         id = update-documents-results-button
    Then Check correct number of in-setup projects filtered     Previous

Monitoring officer can filter both in-setup and previous projects
    [Documentation]  IFS-9576
    Given the user selects the checkbox                       projectInSetup
    When the user clicks the button/link                      id = update-documents-results-button
    Then Check correct total number of projects displaying

Monitoring officer can view no results text when none of the projects been assigned to him
    [Documentation]  IFS-9576
    Given log in as a different user         &{emptyMonitoringOfficerDashboard}
    Then the user should see the element     jQuery = li:contains("please check your search criteria and try again")
    And the user should see the element      jQuery = h2:contains("No results found")
    And page should contain element          jQuery = h2:contains("0 projects")

Monitoring officer can filter projects based on complete document status
    [Documentation]  IFS-9941
    Given assign monitroing officer to the projects
    And the user uploads documents to the project
    And log in as a different user                       &{monitoringOfficerDashboard}
    When the user selects the checkbox                   documentsComplete
    And the user clicks the button/link                  id = update-documents-results-button
    Then check correct number of projects displaying     Complete  complete

Monitoring officer can filter projects based on incomplete document status
    [Documentation]  IFS-9941
    Given the user unselects the checkbox                documentsComplete
    When the user selects the checkbox                   documentsIncomplete
    And the user clicks the button/link                  id = update-documents-results-button
    Then check correct number of projects displaying     Incomplete   action

Monitoring officer can filter projects based on awaiting review document status
    [Documentation]  IFS-9941
    Given the user unselects the checkbox                documentsIncomplete
    When the user selects the checkbox                   documentsAwaitingReview
    And the user clicks the button/link                  id = update-documents-results-button
    Then check correct number of projects displaying     Awaiting review   action-required

Monitoring officer can filter projects based on complete document status and projects in setup
    [Documentation]  IFS-9941
    Given the user unselects the checkbox                                   documentsAwaitingReview
    When the user selects the checkbox                                      documentsComplete
    And the user selects the checkbox                                       projectInSetup
    And the user clicks the button/link                                     id = update-documents-results-button
    Then check correct number of combined filtering projects displaying
    And check correct total number of projects displaying

*** Keywords ***
Custom suite setup
    Connect to database  @{database}
    the user logs-in in new browser                         orville.gibbs@gmail.com  ${short_password}
    the user clicks the project setup tile if displayed

Custom suite teardown
    Disconnect from database
    the user closes the browser

Check correct number of in-setup projects filtered
    [Arguments]  ${filterName}
    ${elementCountOnPage} =    Get Element Count    jQuery = div strong.status-msg
    page should contain element     jQuery = .govuk-checkboxes__label:contains("${filterName} (${elementCountOnPage})")
    page should contain element     jQuery = h2:contains("${elementCountOnPage} project")

Check correct total number of projects displaying
    ${totalCountOnPage} =    Get Element Count    jQuery = div strong.status-msg
    page should contain element     jQuery = h2:contains("${totalCountOnPage} project")

Requesting application id
    [Arguments]  ${applicationName}
    get application id by name and set as suite variable     ${applicationName}

Assign monitroing officer to the projects
    log in as a different user                 &{ifs_admin_user_credentials}
    the user navigates to the page             ${server}/project-setup-management/monitoring-officer/view-all
    Search for MO                              Bill   Bill Mccoy
    Requesting application id                  Monitoring Officer - live project
    The internal user assign project to MO     ${application_id}  Monitoring Officer - live project
    Requesting application id                  Monitoring Officer - Incomplete documents project
    The internal user assign project to MO     ${application_id}  Monitoring Officer - Incomplete documents project
    Requesting application id                  Monitoring Officer - Awaiting review documents project
    The internal user assign project to MO     ${application_id}  Monitoring Officer - Awaiting review documents project
    Requesting application id                  Monitoring Officer - In setup project
    The internal user assign project to MO     ${application_id}  Monitoring Officer - In setup project
    Requesting application id                  Monitoring Officer - In setup complete document project
    The internal user assign project to MO     ${application_id}  Monitoring Officer - In setup complete document project

The user uploads documents to the project
    log in as a different user                            dave.adams@gmail.com     ${short_password}
    the user clicks the application tile if displayed
    the user clicks the button/link                       link = Monitoring Officer - Awaiting review documents project
    the user clicks the button/link                       link = Documents
    The user uploads the exploitation plan
    the user uploads the Collaboration agreement

Check correct number of projects displaying
    [Arguments]  ${documentStatus}  ${statusAction}
    ${documentStatusPageCount} =    Get Element Count    jQuery = .status-${statusAction}:contains("${documentStatus}")
    page should contain element     jQuery = .govuk-checkboxes__label:contains("${documentStatus} (${documentStatusPageCount})")
    page should contain element     jQuery = h2:contains("${documentStatusPageCount} project")

Check correct number of combined filtering projects displaying
    ${documentStatusPageCount} =    Get Element Count    jQuery = .status-complete:contains("Complete")
    page should contain element     jQuery = h2:contains("${documentStatusPageCount} project")