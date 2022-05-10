*** Settings ***
Documentation     IFS-11682  Direct Award: New competition type
...
...               IFS-11736 Direct awards - Content sweep
...
...               IFS-11735 Direct Awards: Public Content scale back
...

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

the user should see invite only selected as default option in competition information and search
    [Documentation]  IFS-11735
    Given the user clicks the button/link                  link = Back to competition details
    When the user clicks the button/link                   link = Public content
    And the user clicks the button/link                    link = Competition information and search
    Then the user sees that the radio button is selected   publishSetting  invite

the user create a new application
    [Documentation]  IFS-11736
    Given the user logs out if they are logged in
    And the user navigates to the page                      ${server}/competition/${webTestCompID}/overview
    And the lead user creates new application               Test   User   test.user@gmail.com   ${applicationName}
    Then the user should see the element                    jQuery = dt:contains("Award:")

the user checks the application details
    [Documentation]  IFS-11736
    When the user completes the application details           ${applicationName}  ${tomorrowday}  ${month}  ${nextyear}  25
    And the user clicks the button/link                       link = Application details
    Then the user should see the element                      jQuery = dt:contains("Award name")

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

the lead user creates new application
    [Arguments]   ${firstName}   ${lastName}   ${email}   ${applicationName}
    the user select the competition and starts application          ${webTestCompName}
    the user clicks the button/link                                 link = Continue and create an account
    the user selects the radio button                               organisationTypeId    radio-${BUSINESS_TYPE_ID}
    the user clicks the button/link                                 jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House            ASOS  ASOS PLC
    the user should be redirected to the correct page               ${SERVER}/registration/register
    the user enters the details and clicks the create account       ${firstName}  ${lastName}  ${email}  ${short_password}
    the user reads his email and clicks the link                    ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page               ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                 link = Sign in
    Logging in and Error Checking                                   ${email}  ${short_password}
    the user clicks the button/link                                 link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}


