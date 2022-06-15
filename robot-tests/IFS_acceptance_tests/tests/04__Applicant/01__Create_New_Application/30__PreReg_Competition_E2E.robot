*** Settings ***
Documentation     IFS-12065 Pre-Registration (Applicant Journey) Apply to an expression of interest application
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${hecpPreregCompName}       Horizon Europe Guarantee Competition For Pre Registration

*** Test Cases ***
Applicants should view EOI related content on competition
    [Arguments]  IFS-12065
    Given the user navigates to the page        ${frontDoor}
    When the user enters text to a text field   id = keywords   Pre Registration
    And the user clicks the button/link         id = update-competition-results-button
    Then the user should see the element        jQuery = li:contains("Horizon Europe Guarantee Competition For Pre Registration") div:contains("Refer to competition date for competition submission deadlines.")


*** Keywords ***
Requesting IDs of this hecp pre reg competition
    [Arguments]  ${competitionName}
    ${hecpPreregCompId} =  get comp id from comp title  ${hecpPreregCompName}
    Set suite variable  ${hecpPreregCompId}

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database
