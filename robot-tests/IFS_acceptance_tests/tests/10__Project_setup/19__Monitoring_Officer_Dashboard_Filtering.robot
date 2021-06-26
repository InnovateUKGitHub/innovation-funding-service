*** Settings ***
Documentation     IFS-9576 MO documents: 'Project setup' list - task management and filtering
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
&{emptyMonitoringOfficerDashboard}     email=Rupesh.Pereira@gmail.com  password=${short_password}


*** Test Cases ***
Monitoring officer can filter in-setup projects only
    [Documentation]  IFS-9576
    Given the user selects the checkbox                         projectInSetup
    When the user clicks the button/link                        id = update-competition-results-button
    Then Check correct number of in-setup projects filtered     In setup

Monitoring officer can filter previous projects only
    [Documentation]  IFS-9576
    Given the user unselects the checkbox                       projectInSetup
    When the user selects the checkbox                          previousProject
    And the user clicks the button/link                         id = update-competition-results-button
    Then Check correct number of in-setup projects filtered     Previous

Monitoring officer can filter both in-setup and previous projects
    [Documentation]  IFS-9576
    Given the user selects the checkbox                       projectInSetup
    When the user clicks the button/link                      id = update-competition-results-button
    Then Check correct total number of projects displaying

Monitoring officer can view no results text when none of the projects been assigned to him
    [Documentation]  IFS-9576
    Given log in as a different user         &{emptyMonitoringOfficerDashboard}
    Then the user should see the element     jQuery = .icon-info:contains("No results found")
    And page should contain element          jQuery = h2:contains("0 projects")

*** Keywords ***
Custom suite setup
    the user logs-in in new browser     orville.gibbs@gmail.com  ${short_password}

Custom suite teardown
    the user closes the browser

Check correct number of in-setup projects filtered
    [Arguments]  ${filterName}
    ${elementCountOnPage} =    Get Element Count    jQuery = .status
    page should contain element     jQuery = .govuk-checkboxes__label:contains("${filterName} (${elementCountOnPage})")
    page should contain element     jQuery = h2:contains("${elementCountOnPage} projects")

Check correct total number of projects displaying
    ${elementCountOnPage} =    Get Element Count    jQuery = .status
    page should contain element     jQuery = h2:contains("${elementCountOnPage} projects")