*** Settings ***
Documentation     IFS-10072 Improvements to Subsidy basis question
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot

*** Variables ***
${subsidyControlTacticalComp}   Subsidy control tactical competition
${tacticalApplication}          Subsidy control tactical application

*** Test Cases ***
Lead can mark the subsidy basis as complete when no partner invited
    [Documentation]    IFS-10072
    Given lead applicant creates a new application
    And the user clicks the button/link              link = Application details
    And the user completes application details       ${tacticalApplication}  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link             link = Subsidy basis
    And the user clicks the button twice             jQuery = label:contains("Yes")
    And the user clicks the button/link              id = application-question-complete
    Then the user should see the element             jQuery = p:contains("Yes")
    And the user clicks the button/link              link = Back to application overview
    And the user should see the element              jQuery = li:contains("Subsidy basis") > .task-status-complete

Lead subsidy basis question status marked as incomplete when partner not submitted their input for subsidy basis
    [Documentation]   IFS-10072
    Given the lead invites already registered user          ${collaborator1_credentials["email"]}  ${subsidyControlTacticalComp}
    When logging in and error checking                      jessica.doe@ludlow.co.uk  ${short_password}
    And the user clicks the button/link                     css = .govuk-button[type="submit"]    #Save and continue
    And log in as a different user                          &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    And the user clicks the button/link                     link = ${tacticalApplication}
    Then the user should see the element                    jQuery = li:contains("Subsidy basis") > .task-status-incomplete

Lead gets a validation message when partner does not input their subsidy basis
    [Documentation]   IFS-10072
    Given the user clicks the button/link    id = application-overview-submit-cta
    When the user clicks the button/link     id = accordion-questions-heading-1-1
    Then the user should see the element     jQuery = .warning-alert p:contains("The following organisations have not yet marked this question as complete:")
    And the user should see the element      jQuery = li:contains("Ludlow")
    And the user should see the element      jQuery = .section-incomplete + button:contains("Subsidy basis")


*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The user logs-in in new browser    &{lead_applicant_credentials}

the user completes application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element       jQuery = h1:contains("Application details")
    the user enters text to a text field  id = name  ${appTitle}
    the user enters text to a text field  id = startDate  ${tomorrowday}
    the user enters text to a text field  css = #application_details-startdate_month  ${month}
    the user enters text to a text field  css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field  css = [id="durationInMonths"]  24
    the user can mark the question as complete
    the user should see the element       jQuery = li:contains("Application details") > .task-status-complete

lead applicant creates a new application
    existing user creates a new application     ${subsidyControlTacticalComp}
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = label:contains("Empire Ltd")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user clicks the button twice   jQuery = label:contains("Empire Ltd")
    ...                             AND                 the user clicks the button/link    jQuery = button:contains("Save and continue")

Custom suite teardown
    The user closes the browser