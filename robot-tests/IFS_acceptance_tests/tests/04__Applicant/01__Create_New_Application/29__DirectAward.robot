*** Settings ***
Documentation     IFS-11682  Direct Award: New competition type
...
...               IFS-11736 Direct awards - Content sweep
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/defaultResources.robot


*** Variables ***
${openEndedCompName}               Open ended Direct Award competition
${openEndedComp}                   Direct Award


*** Test Cases ***
the user creates a new open ended competiton
    [Documentation]  IFS-11682
    Given The user logs-in in new browser           &{Comp_admin1_credentials}
    When the user navigates to the page             ${CA_UpcomingComp}
    And the user clicks the button/link             jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details    ${openEndedCompName}  ${month}  ${nextyear}  ${compType_DirectAward}  STATE_AID  GRANT
    And the user clicks the button/link             link = Initial details
    Then the user should see the element            jQuery = dt:contains("Competition type") ~ dd:contains("Direct award")

the user should see open ended is selected by default for direct award competition type
    [Documentation]  IFS-11682
    When the user clicks the button/link                  link = Back to competition details
    And the user completes completion stage
    Then the user sees that the radio button is selected  alwaysOpen  true

The user completes open ended competiton
    [Documentation]  IFS-11736
    Given the user clicks the button/link                       link = Competition details
    And the competition admin creates open ended competition   ${business_type_id}  ${openEndedComp}  Open ended  ${compType_DirectAward}  STATE_AID  GRANT  PROJECT_SETUP  yes  50  true  collaborative

Send the email invite to the assessor for the competition using new content
    [Documentation]  IFS-9009
    When comp admin sends invite to assesor
    Then the user reads his email               ${webTestAssessorEmailAddress}  Invitation to be an assessor for competition: '${openEndedComp}'  We invite you to assess applications for the competition:

Lead applicant creates an application and checks the dashboard content
    [Documentation]  IFS-8850
    Given the user logs out if they are logged in
    And the lead user creates an always open application                                         Test   User   test.user1@gmail.com   ${applicationName}
    When the lead user completes project details, application questions and finances sections    COMPLETE   test.user1@gmail.com


*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user completes completion stage
    the user clicks the button/link     link = Milestones
    the user clicks the button twice    jQuery = label:contains("Project setup")
    the user clicks the button/link     jQuery = button:contains("Done")

