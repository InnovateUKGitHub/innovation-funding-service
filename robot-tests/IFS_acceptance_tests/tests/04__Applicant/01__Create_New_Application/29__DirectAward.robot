*** Settings ***
Documentation     IFS-11682  Direct Award: New competition type
...
<<<<<<< HEAD
...               IFS-11736 Direct awards - Content sweep
...

=======
...               IFS-11735 Direct Awards: Public Content scale back
...
>>>>>>> 03f6aeea0fe14a88262d66301662b02293da28cc
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown

Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot


*** Variables ***
${openEndedCompName}        Open ended Direct Award competition
${webTestCompName}          Direct award competition
${webTestCompID}            ${competition_ids["${webTestCompName}"]}
${applicationName}          Direct award Application


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
    Given the user clicks the button/link                  link = Back to competition details
    When the user completes completion stage
    Then the user sees that the radio button is selected   alwaysOpen  true

the user should see inviteonly selected as default option in competition information and search
    [Documentation]  IFS-11735
    Given the user clicks the button/link                  link = Back to competition details
    When the user clicks the button/link                   link = Public content
    And the user clicks the button/link                    link = Competition information and search
    Then the user sees that the radio button is selected   publishSetting  invite

the user create a new application
    [Documentation]  IFS-11736
    Given the user logs out if they are logged in
    And the user navigates to the page            ${server}/competition/${webTestCompID}/overview
    When existing user creates a new application  ${webTestCompName}
    Then the user should see the element          jQuery = dt:contains("Award:")

the user checks the application details
    [Documentation]  IFS-11736
    When the user completes the application details           ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}  25
    And the user clicks the button/link                       link = Application details
    Then the user should see the element                      jQuery = dt:contains("Award name")

the user checks the scope of application
     [Documentation]  IFS-11736
     Given the user clicks the button/link  link = Back to application overview
     And the user clicks the button/link    link= Scope
     Then the user should see the element   jQuery = h3:contains("How does your project align with the scope of this award?")

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

the user completes the application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}  ${projectDuration}
    the user clicks the button/link             link = Application details
    the user should see the element             jQuery = h1:contains("Application details")
    the user enters text to a text field        id = name  ${appTitle}
    the user enters text to a text field        id = startDate  ${tomorrowday}
    the user enters text to a text field        css = #application_details-startdate_month  ${month}
    the user enters text to a text field        css = #application_details-startdate_year  ${nextyear}
    the user should see the element             jQuery = label:contains("Project duration in months")
    the user enters text to a text field        css = [id="durationInMonths"]  ${projectDuration}
    the user clicks the button/link             id = application-question-complete
    the user clicks the button/link             link = Back to application overview

