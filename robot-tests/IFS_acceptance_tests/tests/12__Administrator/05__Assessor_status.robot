*** Settings ***
Documentation     Suite description
Suite Teardown    the user closes the browser
Force Tags        Administrator  CompAdmin
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Admin can view assessor status unavailable
    [Documentation]  IFS-7023
    [Setup]   The user logs-in in new browser           &{ifs_admin_user_credentials}
    Given the user clicks the button/link               link = Manage users
    When the user searches for an assessor              Isaac  unavailable
    And user should see the correct assessor status     Unavailable   The user is unavailable to work as an assessor until further notice
    Then the user should not see the element            jQuery = a[disabled="disabled"]:contains("Change role status")
    And the user should see the element                 link = Change role status

Support can view assessor status disabled
    [Documentation]  IFS-7023
    [Setup]   log in as a different user         &{support_user_credentials}
    Given the user clicks the button/link        link = Manage users
    When the user searches for an assessor       Kieran  disabled
    Then the user should see the element         jQuery = td:contains("Assessor") ~ td:contains("Disabled")
    And the user should not see the element      link = View role profile

Comp Admin can view assessor status
    [Documentation]  IFS-7021
    Given log in as a different user            &{Comp_admin1_credentials}
    When the user clicks the button/link        link = Assessor status
    Then the user should see the element        jQuery = h1:contains("Assessor status")

Comp Admin can search for assessor
    [Documentation]  IFS-7054
    Given the user enters text to a text field   id = filter  Kieran
    When the user clicks the button/link         css = input[type="submit"]
    And the user clicks the button/link          link = Role disabled (1)
    Then the user should see the element         jQuery = p:contains("Kieran Harper")

Comp admin can view details of assessor
    [Documentation]  IFS-7023
    Given the user clicks the button/link               link = View details
    When user should see the correct assessor status    Disabled  The user no longer works as an assessor.
    Then the user should not see the element            jQuery = a[disabled="disabled"]:contains("Change role status")
    And the user should see the element                 link = Change role status

Project finance can view details of an assessor
    [Documentation]  IFS-7024
    [Setup]   log in as a different user            &{internal_finance_credentials}
    Given the user clicks the button/link           link = Assessor status
    When the finance user searches for an assessor  myra.cole	  available
    And the user clicks the button/link             link = View role profile
    Then the user should see the element            jQuery = dd:contains("Available")
    And the user should not see the element         jQuery = dt:contains("Reason for status change")

Assessor with assigned assessments sees banner
    [Documentation]  IFS-7098
    Given the user should not see the element       css = .message-alert
    When the assessor is assigned an application
    Then the user should be blocked from changing the role profile

Assessor does not see banner when assesment closed
    [Documentation]  IFS-7098
    Given the assessment is closed
    Then the user should not be blocked from changing the role profile
    [Teardown]  reset assessment

Banner applications are removed from assessor
    [Documentation]  IFS-7098
    Given the assessor is removed from all applications
    Then the user should not be blocked from changing the role profile

*** Keywords ***
user should see the correct assessor status
    [Arguments]  ${status}  ${reason}
    the user should see the element   jQuery = td:contains("Assessor") ~ td:contains("${status}")
    the user clicks the button/link   link = View role profile
    the user should see the element   jQuery = dd:contains("${status}") ~ dd:contains("${reason}")

the user searches for an assessor
    [Arguments]  ${searchTerm}  ${status}
    the user enters text to a text field    id = filter  ${searchTerm}
    the user clicks the button/link         css = input[type="submit"]
    the user should see the element         jQuery = p:contains("${searchTerm}") ~ p:contains("Assessor (${status})")
    the user clicks the button/link         link = Edit

the finance user searches for an assessor
    [Arguments]  ${searchTerm}  ${status}
    the user enters text to a text field    id = filter  ${searchTerm}
    the user clicks the button/link         css = input[type="submit"]
    the user should see the element         jQuery = p:contains("${searchTerm}") ~ p:contains("Assessor")
    the user clicks the button/link         link = View details

the assessor is assigned an application
    the user navigates to the page       ${server}/management/assessment/competition/11/assessors/204
    the user clicks the button/link      jQuery = td:contains("Park living") ~ td button:contains("Assign")

the user should be blocked from changing the role profile
    the user clicks the button/link         link = Assessor status
    the user enters text to a text field    id = filter  myra
    the user clicks the button/link         css = input[type="submit"]
    the user clicks the button/link         link = View details
    the user clicks the button/link         link = View role profile
    the user should see the element         css = .message-alert

the assessor is removed from all applications
    the user navigates to the page       ${server}/management/assessment/competition/11/assessors/204
    the user clicks the button/link      jQuery = td:contains("Park living") ~ td button:contains("Remove")

the user should not be blocked from changing the role profile
    the user clicks the button/link         link = Assessor status
    the user enters text to a text field    id = filter  myra
    the user clicks the button/link         css = input[type="submit"]
    the user clicks the button/link         link = View details
    the user clicks the button/link         link = View role profile
    the user should not see the element     css = .message-alert

the assessment is closed
    the user clicks the button/link    link = Dashboard
    the user clicks the button/link    link = ${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link    jQuery = button:contains("Close assessment")

reset assessment
    Connect to Database  @{database}
    Execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE `type`='ASSESSMENT_CLOSED' AND `competition_id`='${IN_ASSESSMENT_COMPETITION}';
    Disconnect from database