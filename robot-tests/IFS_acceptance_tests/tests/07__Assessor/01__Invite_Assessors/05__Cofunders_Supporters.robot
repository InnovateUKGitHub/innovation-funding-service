*** Settings ***
Documentation  IFS-8414 Internal user - View co funder feedback progress - list view
...
...            IFS-8407 Internal user - View co funder feedback
...
...            IFS-8402 Co funder dashboard - competition level

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${cofunderApplicationTitle}     KTP application
${cofunderOrg}                  The University of Surrey
${newApplication}               New application

*** Test Cases ***
The cofunder can see the sections in the cofunding dashboard
    [Documentation]  IFS-8402
    Given Logging in and Error Checking     hubert.cumberdale@salad-fingers.com  Passw0rd
    When the user clicks the button/link    jQuery = h2:contains("Co-funding")
    Then the user should see the element    jQuery = h2:contains("Competitions to review")
    And the user should see the element     jQuery = h2:contains("Upcoming competitions to review")

The cofunder should see a newly created application from the dashboard
    [Documentation]  IFS-8402
    Given the user select the competition and starts application     KTP new competition
    input text                                                       id = knowledgeBase        ${cofunderOrg}
    When the user clicks the button/link                             jQuery = ul li:contains("${cofunderOrg}")
    And the user clicks the button/link                              jQuery = button:contains("Confirm")
    Then the user clicks the button/link                             id = knowledge-base-confirm-organisation-cta
    And the user clicks the button/link                              link = Application details
    Then the user enters text to a text field                        css = [id = "name"]    ${newApplication}
    And the user enters text to a text field                         css = [id = "durationInMonths"]    3
    Then the user clicks the button/link                             jQuery = button:contains("Mark as complete")
    And the user clicks the button/link                              link = Dashboard
    Then the user clicks the button/link                             jQuery = h2:contains("Applications")
    And the user should see the element                              jQuery = a:contains("${newApplication}")

The internal user can view a co-funder application by searching with an application number
    [Documentation]  IFS-8414
    [Setup]  the user requesting the application id
    Given Log in as a different user                    &{ifs_admin_user_credentials}
    And the user navigates to the page                  ${server}/management/competition/99/cofunders/view
    When the user enters text to a text field           id=applicationFilter    ${cofunderApplicationID}
    And the user clicks the button/link                 jQuery = button:contains("Filter")
    And the user clicks the button/link                 link = ${cofunderApplicationID}
    Then the user should see the element                jQuery = h1:contains("Application overview")

The ifs admin views the feedback of the application
    [Documentation]   IFS-8407
    Given the user clicks the button/link           link = Back to co-funders
    When the user clicks the button/link            jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

The comp admin views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{Comp_admin1_credentials}
    And the user navigates to the page              ${server}/management/competition/99/cofunders/view
    When And the user clicks the button/link        link = ${cofunderApplicationID}
    And the user clicks the button/link             link = Back to co-funders
    And the user clicks the button/link             jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

The finance manager views the feedback of the application
    [Documentation]   IFS-8407
    Given Log in as a different user                &{internal_finance_credentials}
    And the user navigates to the page              ${server}/management/competition/99/cofunders/view
    When And the user clicks the button/link        link = ${cofunderApplicationID}
    And the user clicks the button/link             link = Back to co-funders
    And the user clicks the button/link             jQuery = td:contains("${cofunderApplicationTitle}") ~ td:contains("View feedback")
    Then the user can view the cofunder review

*** Keywords ***
Custom suite setup
    The guest user opens the browser
    Connect To Database   @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user requesting the application id
    ${cofunderApplicationID} =  get application id by name    ${cofunderApplicationTitle}
    Set Suite Variable  ${cofunderApplicationID}

the user can view the cofunder review
    the user should see the element     jQuery = h1:contains("Co-funder review")
    the user should see the element     jQuery = h2:contains("Accepted")
    the user should see the element     jQuery = h2:contains("Declined")
    the user should see the element     jQuery = h2:contains("Pending review")