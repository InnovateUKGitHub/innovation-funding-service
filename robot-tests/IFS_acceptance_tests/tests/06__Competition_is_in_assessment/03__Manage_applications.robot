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
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Variables ***
${Molecular_id}        ${application_ids['Molecular tree breeding']}
${Virtual_Reality_id}  ${application_ids['Living with Virtual Reality']}
${Paul_Plum_id}        169

*** Test Cases ***
View the list of the applications
    [Documentation]    INFUND-7042
    [Tags]
    Given The user clicks the button/link        link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link         jQuery=a:contains("Manage assessments")
    And the user see the correct key statistics
    And The key statistics counts should be correct
    And The user clicks the button/link          jQuery=a:contains("Manage applications")
    Then the application list is correct before changes
    [Teardown]  the user clicks the button/link  link=Manage assessments

View the list of assessors
    [Documentation]  IFS-319
    [Tags]
    When The user clicks the button/link  jQuery=a:contains("Manage assessors")
    Then the assessor list is correct before changes

Assessor link goes to the assessor profile
    [Documentation]  IFS-319
    [Tags]
    Given the user clicks the button/link  link=Paul Plum
    Then the user should see the element   jQuery=h1:contains("Assessor profile") ~ p:contains("Paul Plum")
    [Teardown]    the user clicks the button/link  link=Back

Filter assessors
    [Documentation]    IFS-399
    [Tags]
    Given the user selects the option from the drop-down menu  Materials and manufacturing  id=innovationSector
    And the user clicks the button/link                        jQuery=.button:contains("Filter")
    Then the user should not see the element                   jQuery=td:contains("Paul Plum")
    And the user should see the element                        jQuery=td:contains("Felix Wilson")
    And the user should see the element                        jQuery=td:contains("Jenna Diaz")
    Then the user selects the option from the drop-down menu   Academic  id=businessType
    And the user clicks the button/link                        jQuery=.button:contains("Filter")
    Then the user should see the element                   jQuery=td:contains("Felix Wilson")
    And the user should not see the element                        jQuery=td:contains("Jenna Diaz")
    [Teardown]    the user clicks the button/link  link=Clear all filters

View assessor progress page
    [Documentation]  IFS-321
    [Tags]
    Given the user clicks the button/link  jQuery=td:contains("Paul Plum") ~ td a:contains("View progress")
    Then The user should see the element   jQuery=h2:contains("Paul Plum")
    And the user should see the element    jQuery=h4:contains("Innovation area") ~ ul li:contains("Urban living") ~ li:contains("Smart infrastructure")
    And the user should see the element    jQuery=h4:contains("Type") ~ span:contains("Academic")
    And the user should see the element    jQuery=h2:contains("Assigned") + div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") + td:contains("Forest Universe") + td:contains("2")
    And the user should see the element    jQuery=h2:contains("Assigned") + div td:contains("${Molecular_id}") ~ td:contains("Yes") + td:contains("-") + td:contains("-")
    And the user should see the element    jQuery=h2:contains("Applications") ~ div td:contains("${Virtual_Reality_id}") + td:contains("Living with Virtual Reality") + td:contains("Caneplus")
    And the user should see the element    jQuery=h2:contains("Applications") ~ div td:contains("${Virtual_Reality_id}") ~ td:contains("0") + td:contains("0") + td:contains("0")

Selecting Review assessor link shows the assessor page
    [Documentation]  IFS-1046
    [Tags]
    Given the user clicks the button/link  link=Review assessor
    Then the user should see the element   jQuery=h3:contains("Name") + p:contains("Paul Plum")

Accepting the application changes the Accepted column
    [Documentation]  IFS-321
    [Tags]
    [Setup]  Log in as a different user   &{assessor_credentials}
    Given the user accepts the application
    And Log in as a different user        &{Comp_admin1_credentials}
    When the user navigates to the page   ${server}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}/assessors/${Paul_Plum_id}
    Then the user should see the element  jQuery=td:contains("${Molecular_id}") ~ td:contains("Yes") + td:contains("Yes")

Remove an assigned application (Notified)
    [Documentation]    INFUND-1079
    [Tags]
    Given the user clicks the button/link     jQuery=td:contains("${Molecular_id}") ~ td:contains("Yes") ~ td:contains("Remove")
    When the user clicks the button/link      jQuery=button:contains("Remove assessor")
    Then the user should not see the element  jQuery=td:contains("${Molecular_id}") ~ td:contains("Yes") ~ td:contains("Remove")
    And the user should see the element       jQuery=h2:contains("Previously assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Re-assign")
    And the user clicks the button/link       jQuery=.pagination-label:contains("Next")

Re-assign a removed application
    [Documentation]    INFUND-398
    [Tags]
    Given the user clicks the button/link      jQuery=button:contains("Re-assign")
    Then the user should not see the element   jQuery=h2:contains("Previously assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Re-assign")
    And the user should see the element        jQuery=h2:contains("Assigned") ~ div td:contains("${Molecular_id}") + td:contains("Molecular tree breeding") ~ td:contains("Remove")

Assign an application to an assessor
    [Documentation]    IFS-811
    [Tags]
    Given the user clicks the button/link  link=Allocate assessors
    When the user clicks the button/link   jQuery=td:contains("Shaun Bradley") ~ td a:contains("View progress")
    Then the user should see the element   jQuery=h2:contains("Assigned (0)") + p:contains("No applications have been assigned to this assessor")
    And the user clicks the button/link    jQuery=td:contains("36") ~ td button:contains("Assign")
    Then the user should see the element   jQuery=h2:contains("Assigned (1)") + .table-overflow tr:contains("36")

Filter by application number on the assessor page
    [Documentation]    IFS-400
    [Tags]
    Given the user enters text to a text field    css=#filterSearch    22
    When the user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("22")
    And the user should not see the element    jQuery=.pagination-label:contains(Next)


Filtering of the applications
    [Documentation]    INFUND-8061
    [Setup]  the user navigates to the page    ${SERVER}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}
    Given the user clicks the button/link      jQuery=a:contains("Manage applications")
    When The user enters text to a text field  css=#filterSearch    22
    and The user clicks the button/link        jQuery=button:contains("Filter")
    Then the user should see the element       jQuery=tr:nth-child(1) td:nth-child(1):contains("22")
    And The user clicks the button/link        link=Clear all filters
    then the user should not see the element   jQuery=tr:nth-child(1) td:nth-child(1):contains("22")

Application number navigates to Overview
    [Documentation]    INFUND-7042
    [Tags]
    When the user clicks the button/link           link=22
    Then The user should see the text in the page  Intelligent water system
    And the user should see the text in the page   University of Bath
    And the user should see the text in the page   Cardiff University
    [Teardown]    the user clicks the button/link  link=Back

View application progress page
    [Documentation]    INFUND-7042, INFUND-7046
    [Tags]
    Given the user clicks the button/link          jQuery=tr:nth-child(9) a:contains("View progress")
    Then The user should see the element           jQuery=h2:contains("Living with Augmented Reality")
    And the user should see the element            jQuery=h3:contains("Partners") ~ ul:contains("Tripplezap (Lead)")
    And the user should see the element            jQuery=h3:contains("Innovation area") ~ span:contains("Digital manufacturing")
    And the user should see the element            jQuery=p:contains("No assessors have been assigned to this application")
    And the user should see the element            jQuery=p:contains("No assessors have rejected this application.")
    And the user should see the element            jQuery=p:contains("No assessors were previously assigned to this application.")

Review the application
    [Documentation]    INFUND-7046
    [Tags]
    When the user clicks the button/link           link=Review application
    Then the user should see the text in the page  Application overview
    And the user should see the element            jQuery=dt:contains("Innovation area") + dd:contains("Digital manufacturing")
    [Teardown]    The user goes back to the previous page

View the available assessors
    [Documentation]    INFUND-7233\\
    [Tags]
    Then the user should see the element  jQuery=.column-two-thirds:contains("Assessors")
    And the user clicks the button/link   jQuery=.pagination-label:contains("Next")
    And the available assessors information is correct

View the application assigned list
    [Documentation]    INFUND-7230 INFUND-7038
    [Tags]
    Given The user should see the element          jQuery=p:contains("No assessors have been assigned to this application.")
    When the user clicks the button/link           jQuery=tr:contains("Paul Plum") button:contains("Assign")
    Then the user should see the text in the page  Assigned (1)
    And the assigned list is correct before notification
    And the user clicks the button/link            jQuery=.link-back:contains("Allocate applications")
    Then the user should see the element           jQuery=tr:nth-child(9) td:nth-child(4):contains("1")

Remove an assigned user (Not notified)
    [Documentation]    INFUND-7230
    [Tags]
    Given the user clicks the button/link  jQuery=tr:nth-child(9) a:contains("View progress")
    And the user clicks the button/link    jQuery=tr:nth-child(1) button:contains("Remove")
    And the user clicks the button/link    jQuery=.pagination-label:contains("Next")
    And the available assessors information is correct

Notify an assigned user
    [Documentation]    INFUND-7050
    [Tags]
    Given the user clicks the button/link  jQuery=tr:contains("Paul Plum") button:contains("Assign")
    And the user clicks the button/link    link=Allocate applications
    And the user clicks the button/link    link=Manage assessments
    And the user clicks the button/link    link=Competition
    And the user clicks the button/link    jQuery=button:contains("Notify assessors")
    And the element should be disabled     jQuery=button:contains("Notify assessors")
    #TODO Check email once 7249 is done

Assessor should see the assigned application
    [Documentation]    INFUND-7050
    [Setup]    Log in as a different user  &{assessor_credentials}
    When The user clicks the button/link   link=Sustainable living models for the future
    Then The user should see the element   Link=Living with Augmented Reality

Remove and notify an assessor (Notified)
    [Documentation]    INFUND-7232
    [Tags]
    [Setup]    Log in as a different user         &{Comp_admin1_credentials}
    Given The user clicks the button/link         link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link           jQuery=a:contains("Manage assessments")
    And the user clicks the button/link           jQuery=a:contains("Manage applications")
    And the user clicks the button/link           jQuery=tr:nth-child(9) a:contains("View progress")
    When the user clicks the button/link          jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link           jQuery=.buttonlink:contains("Cancel")
    And the user should not see the element       jQuery=button:contains("Remove assessor")
    And the user clicks the button/link           jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link           jQuery=button:contains("Remove assessor")
    And the user should see the text in the page  Previously assigned (1)
    And the previously assigned list is correct
    #TODO Check email once 7249 is done

Assessor should not see the removed application
    [Documentation]    INFUND-7232
    [Setup]    Log in as a different user     &{assessor_credentials}
    When The user clicks the button/link      link=Sustainable living models for the future
    Then The user should not see the element  Link=Living with Augmented Reality

Reassign and notify an assessor (Notified)
    [Documentation]    INFUND-7048
    [Tags]
    [Setup]    Log in as a different user          &{Comp_admin1_credentials}
    Given The user clicks the button/link          link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link            jQuery=a:contains("Manage assessments")
    And the user clicks the button/link            jQuery=a:contains("Manage applications")
    And the user clicks the button/link            jQuery=tr:nth-child(9) a:contains("View progress")
    And the user should see the text in the page   Previously assigned (1)
    And the user clicks the button/link            jQuery=tr:contains("Paul Plum") button:contains("Reassign")
    Then the user should see the text in the page  Assigned (1)
    And the assigned list is correct before notification
    And the user clicks the button/link            link=Allocate applications
    And the user clicks the button/link            link=Manage assessments
    And the user clicks the button/link            link=Competition
    And the user clicks the button/link            jQuery=button:contains("Notify assessors")
    And the element should be disabled             jQuery=button:contains("Notify assessors")

Assessor should see the reassigned application
    [Documentation]    INFUND-7050
    [Setup]    Log in as a different user  &{assessor_credentials}
    When The user clicks the button/link   link=Sustainable living models for the future
    Then The user should see the element   Link=Living with Augmented Reality

*** Keywords ***
the application list is correct before changes
    the user should see the element    jQuery=tr:nth-child(1) td:contains(The Best Juggling Company)
    the user should see the element    jQuery=tr:nth-child(1) td:contains(Park living)
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("19")
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(2):contains("Park living")
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(3):contains("The Best Juggling Company")
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(4):contains(${initial_application_assesors})
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(5):contains(${initial_application_assigned})
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(5):contains(${initial+application_submitted})
    #TODO checks disabled due toINFUND-7745

the available assessors information is correct
    the user should see the element    jQuery=.assessors-available td:nth-child(1):contains('Paul Plum')
    the user should see the element    jQuery=.assessors-available td:nth-child(2):contains('Town Planning, Construction')
    #the user should see the element    jQuery=.assessors-available td:nth-child(3):contains('8')
    #the user should see the element    jQuery=.assessors-available td:nth-child(4):contains('4')
    #the user should see the element    jQuery=.assessors-available td:nth-child(5):contains('0')
    #TODO checks disabled due toINFUND-7745

the assigned list is correct before notification
    the user should see the element    jQuery=.assessors-assigned td:nth-child(1):contains("Paul Plum")
    the user should see the element    jQuery=.assessors-assigned td:nth-child(2):contains("Academic")
    the user should see the element    jQuery=.assessors-assigned td:nth-child(3):contains("Urban living")
    the user should see the element    jQuery=.assessors-assigned td:nth-child(3):contains("infrastructure")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(4):contains("9")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(5):contains("5")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(6):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(7):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(8):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(9):contains("-")
    #TODO checks disabled due toINFUND-7745

the previously assigned list is correct
    the user should see the element    jQuery=.assessors-previous td:nth-child(1):contains('Paul Plum')
    the user should see the element    jQuery=.assessors-previous td:nth-child(2):contains('Academic')
    the user should see the element    jQuery=.assessors-previous td:nth-child(3):contains('Urban living')
    the user should see the element    jQuery=.assessors-previous td:nth-child(3):contains('infrastructure')
    #the user should see the element    jQuery=.assessors-previous td:nth-child(4):contains('8')
    #the user should see the element    jQuery=.assessors-previous td:nth-child(5):contains('4')
    #TODO checks disabled due toINFUND-7745

the user see the correct key statistics
    the user should see the element    jQuery=small:contains("Total assignments")
    the user should see the element    jQuery=small:contains("Assignments awaiting response")
    the user should see the element    jQuery=small:contains("Assignments accepted")
    the user should see the element    jQuery=small:contains("Assessments started")
    the user should see the element    jQuery=small:contains("Assessments completed")

The key statistics counts should be correct
    ${TOTAL_ASSIGNMENT}=    Get text    css=.column-fifth:nth-child(1) span
    Should Be Equal As Integers    ${TOTAL_ASSIGNMENT}    14
    ${AWAITING}=    Get text    css=.column-fifth:nth-child(2) span
    Should Be Equal As Integers    ${AWAITING}    7
    ${ACCEPTED}=    Get text    css=.column-fifth:nth-child(3) span
    Should Be Equal As Integers    ${ACCEPTED}    6
    ${STARTED}=    Get text    css=.column-fifth:nth-child(4) span
    Should Be Equal As Integers    ${STARTED}    0
    ${SUBMITTED}=    Get text    css=.column-fifth:nth-child(5) span
    Should Be Equal As Integers    ${SUBMITTED}    1

the assessor list is correct before changes
    the user should see the element  jQuery=td:contains("Paul Plum") ~ td:contains("Town Planning, Construction") ~ td:contains("7") ~ td:contains("7") ~ td:contains("3") ~ td:contains("0")

the user accepts the application
    the user clicks the button/link  link=${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link  link=Molecular tree breeding
    the user selects the radio button  assessmentAccept  true
    the user clicks the button/link  jQuery=button:contains("Confirm")