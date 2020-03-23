*** Settings ***
Documentation     INFUND-7042 As a member of the competitions team I can see list of applications with assessor statistics on the 'Manage Applications' dashboard so...
...
...               INFUND-7046 As a member of the competitions team I can view the application progress dashboard for an application so that I can see the application details
...
...               INFUND-7050 As a member of the competitions team I can notify assessors of their assigned applications by selecting 'Notify assessors' on 'In assessment' dashboard so that assessors know which applications they have to assess
...
...               INFUND-7038 As a member of the competitions team I can add an assessor to the 'Assign Assessors' list so that I can ensure an assessment has the correct number of assessors
...
...               INFUND-7233 As a member of the competitions team I can view the assessors list so that I can see who is available to assess the application
...
...               INFUND-7237 Implement Assessor Total Applications and Assigned Counts for Application Progress within Assessor Management
...
...               INFUND-7232 As a member of the competitions team I can view previously assigned assessors so I can see who has previously been removed from assessing the application
...
...               INFUND-8061 Filter and pagination on Allocate Applications (Closed competition) and Manage applications (In assessment) dashboards
...
...               IFS-319 View list of accepted assessors - In assessment state
...
...               IFS-1079 Remove an application - Closed and In assessment states
...
...               IFS-400 Filter by application number on Assessor progress dashboard - Closed and in assessments state
...
...               IFS-5915  Assessor Filter Option
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Variables ***
${Molecular_id}        ${application_ids['Molecular tree breeding']}
${Cryptocurrencies_id}  ${application_ids['Living with Cryptocurrencies']}
${Paul_Plum_id}        ${user_ids['maureen.moore@gmail.com']}
${Intelligent_water}   ${application_ids['Intelligent water system']}

*** Test Cases ***
View the list of the applications
    [Documentation]    INFUND-7042
    Given comp admin navigate to manage applications
    Then the application list is correct before changes
    [Teardown]  the user clicks the button/link  link = Manage assessments

View the list of assessors
    [Documentation]  IFS-319
    Given The user clicks the button/link  jQuery = a:contains("Manage assessors")
    Then the assessor list is correct before changes

Filter assessors
    [Documentation]    IFS-399  IFS-5915
    Given the user filter assessors by first or last name
    [Teardown]    the user clicks the button/link  link = Clear filter

View assessor progress page
    [Documentation]  IFS-321
    [Setup]  the user clicks the button/link  jQuery = a:contains("21 to 40")
    Given the user clicks the button/link  jQuery = td:contains("Maureen Moore") ~ td a:contains("View progress")
    Then the user should see details on assessors progress page

Selecting Review assessor link shows the assessor page
    [Documentation]  IFS-1046
    Given the user clicks the button/link  link = Review assessor
    Then the user should see the element   jQuery = dt:contains("Name") ~ dd:contains("Maureen Moore")
    [Teardown]  Assign application to maureen

Accepting the application changes the Accepted column
    [Documentation]  IFS-321
    [Setup]  Log in as a different user   maureen.moore@gmail.com  Passw0rd
    Given the user accepts the application
    And Log in as a different user        &{Comp_admin1_credentials}
    When the user navigates to the page   ${server}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}/assessors/${Paul_Plum_id}
    Then the user should see the element  jQuery = td:contains("${Molecular_id}") ~ td:contains("Yes") + td:contains("Yes")

Remove an assigned application (Notified)
    [Documentation]    INFUND-1079
    Given the user clicks the button/link     jQuery = td:contains("${Molecular_id}") ~ td:contains("Yes") ~ td:contains("Remove")
    When the user clicks the button/link      jQuery = button:contains("Remove assessor")
    Then the user should not see the element  jQuery = td:contains("${Molecular_id}") ~ td:contains("Yes") ~ td:contains("Remove")
    And the user should see the element       jQuery = h2:contains("Previously assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Reassign")
    And the user clicks the button/link       jQuery = .pagination-links a:contains("Next")

Reassign a removed application
    [Documentation]    INFUND-398
    Given the user clicks the button/link      jQuery = button:contains("Reassign")
    Then the user should not see the element   jQuery = h2:contains("Previously assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Reassign")
    And the user should see the element        jQuery = h2:contains("Assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Remove")

Assign an application to an assessor
    [Documentation]    IFS-811
    Given the user clicks the button/link             link = Allocate assessors
    And the user clicks the button/link               jQuery = a:contains("41 to")
    When the user clicks the button/link              jQuery = td:contains("Shaun Bradley") ~ td a:contains("View progress")
    Then the user should see the element              jQuery = h2:contains("Assigned (0)") + p:contains("No applications have been assigned to this assessor")
    When the user adds an application to an assessor  jQuery = tr:contains("36") :checkbox
    Then the user should see the element              jQuery = h2:contains("Assigned (1)") + .table-overflow tr:contains("36")

Filter by application number on the assessor page
    [Documentation]    IFS-400
    Given the user enters text to a text field  css = #filterSearch    ${Intelligent_water}
    When the user clicks the button/link        jQuery = button:contains("Filter")
    Then the user should see the element        jQuery = tr:nth-child(1) td:contains("${Intelligent_water}")
    And the user should not see the element     jQuery = .pagination-label:contains("Next")

Filtering of the applications
    [Documentation]    INFUND-8061
    [Setup]  the user navigates to the page    ${SERVER}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}
    Given the user clicks the button/link      jQuery = a:contains("Manage applications")
    Then the user filter by application name

Application number navigates to Overview
    [Documentation]    INFUND-7042
    Given the user clicks the button/link           link = ${Intelligent_water}
    Then The user should see the element           jQuery = .govuk-caption-l:contains("Intelligent water system")
    And the user should see the element            jQuery = h1:contains("Application overview")
    [Teardown]    the user clicks the button/link  link = Back to manage applications

View application progress page
    [Documentation]    INFUND-7042, INFUND-7046
    Given the user clicks the button/link          jQuery = td:contains("Living with Cryptocurrencies") ~ td:contains("View progress")
    Then the user should see details on application progress page

Review the application
    [Documentation]    INFUND-7046
    Given the user clicks the button/link  link = Review application
    Then the user should see the element  jQuery = h1:contains("Application overview")
    [Teardown]    The user goes back to the previous page

View the available assessors
    [Documentation]    INFUND-7233
    Given the user should see the element  jQuery = .govuk-table__header:contains("Assessor")
    When the user clicks the button/link   jQuery = .pagination-links a:contains('2')
    Then the available assessors information is correct

View the application assigned list
    [Documentation]    INFUND-7230 INFUND-7038
    [Setup]  the user should see the element  jQuery = h2:contains("Living with Cryptocurrencies")
    Given The user should see the element  jQuery = p:contains("No assessors have been assigned to this application.")
    Then the user assign application to an assessor

Remove an assigned user (Not notified)
    [Documentation]    INFUND-7230
    [Tags]
    Given the user clicks the button/link  jQuery = td:contains("Living with Cryptocurrencies") ~ td:contains("View progress")
    Then the user clicks the button/link   jQuery = td:contains("Maureen Moore") ~ td:contains("Remove")

Notify an assigned user
    [Documentation]    INFUND-7050
    [Setup]  the user clicks the button/link   jQuery = .pagination-links a:contains('2')
    Given the user adds an assessor to application  assessor-row-17
    Then the comp admin notify an assessor

Assessor should see the assigned application
    [Documentation]    INFUND-7050
    [Setup]    Log in as a different user  maureen.moore@gmail.com  Passw0rd
    Given The user clicks the button/link   link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then The user should see the element   Link = Living with Cryptocurrencies

Remove and notify an assessor (Notified)
    [Documentation]    INFUND-7232
    [Setup]    Log in as a different user         &{Comp_admin1_credentials}
    Given comp admin navigate to manage applications   ${IN_ASSESSMENT_COMPETITION_NAME}
    Then the user removes assessor from assigned application and notify
    And the previously assigned list is correct

Assessor should not see the removed application
    [Documentation]    INFUND-7232
    [Setup]    Log in as a different user     maureen.moore@gmail.com  Passw0rd
    When The user clicks the button/link      link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then The user should not see the element  Link = Living with Cryptocurrencies

Reassign and notify an assessor (Notified)
    [Documentation]    INFUND-7048
    [Tags]
    [Setup]    Log in as a different user          &{Comp_admin1_credentials}
    Given comp admin navigate to manage applications    ${IN_ASSESSMENT_COMPETITION_NAME}
    When the user resign assessor to an application
    Then the comp admin notify an assessor

Assessor should see the reassigned application
    [Documentation]    INFUND-7050
    [Setup]    Log in as a different user   maureen.moore@gmail.com  Passw0rd
    Given The user clicks the button/link   link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then The user should see the element   Link = Living with Cryptocurrencies

*** Keywords ***
the application list is correct before changes
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(1):contains("158")
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(2):contains("Application for load 2")
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(3):contains("Mo Juggling Mo Problems Ltd")
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(4):contains("4")
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(5):contains("4")
    the user should see the element    jQuery = tr:nth-child(2) td:nth-child(6):contains("0")

the available assessors information is correct
    the user should see the element  jQuery = tr:contains("Mabel Robinson") td:contains("2") + td:contains("0") + td:contains("0")
    # TODO Add some skills too IFS-1298

the assigned list is correct before notification
    the user should see the element  jQuery = .assessors-assigned td:nth-child(1):contains("Maureen Moore") ~ td:contains("Digital manufacturing") ~ td:contains("6") + td:contains("3")

the previously assigned list is correct
    the user should see the element    jQuery = .assessors-previous td:contains("Maureen Moore") ~ td:contains("Digital manufacturing")
    the user should see the element    jQuery = .assessors-previous td:contains("Maureen Moore") ~ td:contains("5") + td:contains("2")

the assessor list is correct before changes
    the user should see the element  jQuery = td:contains("Jerrilyn Stevens") ~ td:contains("10") ~ td:contains("10") ~ td:contains("10") ~ td:contains("0") ~ td:contains("View progress")

the user accepts the application
    the user clicks the button/link  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link  link = Molecular tree breeding
    the user selects the radio button  assessmentAccept  true
    the user clicks the button/link  jQuery = button:contains("Confirm")

the user filter assessors by first or last name
    the user navigates to the page                         ${server}/management/assessment/competition/11/assessors
    the user enters text to a text field                   id = assessorNameFilter   Je
    the user clicks the button/link                        jQuery = .govuk-button:contains("Filter")
    the user should see the element                        jQuery = td:contains("Jeannie Newton")
    the user should see the element                        jQuery = td:contains("Jesse Stevens")
    the user should see the element                        jQuery = td:contains("Jenna Diaz")
    the user should not see the element                    jQuery = td:contains("Alexis Colon")

the user should see details on assessors progress page
    the user should see the element    jQuery = h2:contains("Maureen Moore")
    the user should see the element    jQuery = h4:contains("Innovation area") ~ ul li:contains("Digital manufacturing")
    the user should see the element    jQuery = h4:contains("Type") ~ span:contains("Unknown")
    the user should see the element    jQuery = h2:contains("Assigned") + div td:contains("22") + td:contains("Intelligent water system") + td:contains("Mo Juggling Mo Problems Ltd") + td:contains("3")
    the user should see the element    jQuery = h2:contains("Assigned") + div td:contains("22") ~ td:contains("Yes") + td:contains("Yes") + td:contains("-")
    the user should see the element    jQuery = h2:contains("Applications") ~ div td:contains("${Cryptocurrencies_id}") + td:contains("Living with Cryptocurrencies") + td:contains("Moveis")
    the user should see the element    jQuery = h2:contains("Applications") ~ div td:contains("${Cryptocurrencies_id}") ~ td:contains("0") + td:contains("0") + td:contains("0")

the user filter by application name
    the user enters text to a text field   css = #filterSearch    ${Intelligent_water}
    the user clicks the button/link        jQuery = button:contains("Filter")
    the user should see the element        jQuery = tr:nth-child(1) td:nth-child(1):contains("${Intelligent_water}")
    the user clicks the button/link        link = Clear all filters
    the user should not see the element    jQuery = tr:nth-child(1) td:nth-child(1):contains("${Intelligent_water}")

the user should see details on application progress page
    the user should see the element       jQuery = h2:contains("Living with Cryptocurrencies")
    the user should see the element       jQuery = h3:contains("Partners") ~ ul:contains("Moveis (Lead)")
    the user should see the element       jQuery = h3:contains("Innovation area") ~ span:contains("Digital manufacturing")
    the user should see the element       jQuery = p:contains("No assessors have been assigned to this application")
    the user should see the element       jQuery = p:contains("No assessors have rejected this application.")
    the user should see the element       jQuery = p:contains("No assessors were previously assigned to this application.")

the user assign application to an assessor
    the user adds an assessor to application  assessor-row-17
    the user should see the element           jQuery = h2:contains("Assigned (1)")
    the assigned list is correct before notification
    the user clicks the button/link           link = Allocate applications
    the user should see the element           jQuery = td:contains("Living with Cryptocurrencies") ~ td:nth-child(4):contains("1")

the comp admin notify an assessor
    the user clicks the button/link    link = Allocate applications
    the user clicks the button/link    link = Manage assessments
    the user clicks the button/link    link = Competition
    the user clicks the button/link    jQuery = button:contains("Notify assessors")
    the element should be disabled     jQuery = button:contains("Notify assessors")

the comp admin notify an assessor2
    the user clicks the button/link    link = Allocate assessors
    the user clicks the button/link    link = Manage assessments
    the user clicks the button/link    link = Competition
    the user clicks the button/link    jQuery = button:contains("Notify assessors")
    the element should be disabled     jQuery = button:contains("Notify assessors")

the user removes assessor from assigned application and notify
    the user clicks the button/link          jQuery = td:contains("Living with Cryptocurrencies") ~ td:contains("View progress")
    the user clicks the button/link          jQuery = td:contains("Maureen Moore") ~ td:contains("Remove")
    the user clicks the button/link          jQuery = .button-clear:contains("Cancel")
    the user should not see the element      jQuery = button:contains("Remove assessor")
    the user clicks the button/link          jQuery = td:contains("Maureen Moore") ~ td:contains("Remove")
    the user clicks the button/link          jQuery = button:contains("Remove assessor")
    the user should see the element          jQuery = h2:contains("Previously assigned (1)")

the user resign assessor to an application
    the user clicks the button/link          jQuery = td:contains("Living with Cryptocurrencies") ~ td:contains("View progress")
    the user should see the element          jQuery = h2:contains("Previously assigned (1)")
    the user clicks the button/link          jQuery = tr:contains("Maureen Moore") button:contains("Reassign")
    the user should see the element          jQuery = h2:contains("Assigned (1)")
    the assigned list is correct before notification

Assign application to maureen
    the user clicks the button/link  link = Back to assessor progress
    the user clicks the button/link  jQuery = td:contains("${Molecular_id}") ~ td:contains("Assign")
    then the comp admin notify an assessor2
