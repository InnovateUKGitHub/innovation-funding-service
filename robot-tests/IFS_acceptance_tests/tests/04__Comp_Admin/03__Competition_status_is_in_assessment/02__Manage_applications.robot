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
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
View the list of the applications
    [Documentation]    INFUND-7042
    [Tags]
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=.button:contains("Manage applications")
    Then the application list is correct before changes

View the available assessors
    [Documentation]    INFUND-7233
    [Tags]
    #TODO update these selectors once the tables on this page have unique class names
    Given the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    Then the user should see the element    jQuery=.column-two-thirds:contains("Assessors")
    And the available assessors information is correct

View the assigned list
    [Documentation]    INFUND-7230 INFUND-7038
    [Tags]
    Given The user should see the element    jQuery=tr:contains(There are no assessors assigned to this application.)
    When the user clicks the button/link    jQuery=tr:contains(Paul Plum) button:contains("Assign")
    Then the user should see the text in the page    Assigned (1)
    And the assigned list is correct before notification
    And the user clicks the button/link    jQuery=.link-back:contains("Allocate applications")
    Then the user should see the element    jQuery=tr:nth-child(1) td:nth-child(4):contains("1")

Remove an assigned user (Not notified)
    [Documentation]    INFUND-7230
    [Tags]
    Given the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove and notify")
    And the available assessors information is correct

Notify an assigned user
    [Documentation]    INFUND-7050
    [Tags]
    Given the user clicks the button/link    jQuery=h2:contains('Available assessors') ~ .table-overflow td:contains('Paul Plum') ~ td:nth-child(6)
    And the user clicks the button/link    jQuery=a:contains("Allocate applications")
    And the user clicks the button/link    jQuery=a:contains("Manage assessments")
    And the user clicks the button/link    jQuery=button:contains("Notify assessors")
    And the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the element should be disabled    jQuery=button:contains("Notify assessors")
    #TODO Check email once 7249 is done

Remove and notify an assessor (Notified)
    [Documentation]    INFUND-7232
    [Tags]
    Given the user clicks the button/link    jQuery=.button:contains("Manage applications")
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    When the user clicks the button/link    jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove and notify")
    And the user should see the text in the page    Previously assigned (1)
    And the previously assigned list is correct

Select the review applicaton button
    [Documentation]    INFUND-7046
    [Tags]
    When the user clicks the button/link    link=Review application
    Then the user should see the text in the page    Application Overview
    [Teardown]    The user navigates to the page    ${Application_management_dashboard}

The Application number should navigate to the Application Overview
    [Documentation]    INFUND-7042
    [Tags]
    When the user clicks the button/link    link=00000015
    Then The user should see the text in the page    00000015: Rainfall
    [Teardown]    The user navigates to the page    ${Application_management_dashboard}

The user can click view the partner information on the view progress screen
    [Documentation]    INFUND-7042, INFUND-7046
    [Tags]
    When The user clicks the button/link    jQuery=tr:nth-child(7) a:contains(View progress)
    Then The user should see the text in the page    00000021: Intelligent water system
    And the user should see the text in the page    University of Bath
    And the user should see the text in the page    Cardiff University

*** Keywords ***
the application list is correct before changes
    the user should see the element    jQuery=tr:nth-child(1) td:contains(Everyday Im Juggling Ltd)
    the user should see the element    jQuery=tr:nth-child(1) td:contains(Rainfall)
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("00000015")
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(2):contains("Rainfall")
    the user should see the element    jQuery=tr:nth-child(1) td:nth-child(3):contains("Everyday Im Juggling Ltd")
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(4):contains(${initial_application_assesors})
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(5):contains(${initial_application_assigned})
    #the user should see the element    jQuery=tr:nth-child(1) td:nth-child(5):contains(${initial+application_submitted})

the available assessors information is correct
    the user should see the element    jQuery=h2:contains('Available assessors') ~ .table-overflow td:nth-child(1):contains('Paul Plum')
    the user should see the element    jQuery=h2:contains('Available assessors') ~ .table-overflow td:nth-child(2):contains('Town Planning, Construction')
    #the user should see the element    jQuery=h2:contains('Available assessors') ~ .table-overflow td:nth-child(3):contains('8')
    #the user should see the element    jQuery=h2:contains('Available assessors') ~ .table-overflow td:nth-child(4):contains('4')
    #the user should see the element    jQuery=h2:contains('Available assessors') ~ .table-overflow td:nth-child(5):contains('0')

the assigned list is correct before notification
    the user should see the element    jQuery=tr:eq(1) td:nth-child(1):contains("Paul Plum")
    the user should see the element    jQuery=tr:eq(1) td:nth-child(2):contains("ACADEMIC")
    the user should see the element    jQuery=tr:eq(1) td:nth-child(3):contains("Urban living")
    the user should see the element    jQuery=tr:eq(1) td:nth-child(3):contains("Infrastructure")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(4):contains("9")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(5):contains("5")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(6):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(7):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(8):contains("-")
    #the user should see the element    jQuery=tr:eq(1) td:nth-child(9):contains("-")

the previously assigned list is correct
    the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(1):contains('Paul Plum')
    the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(2):contains('ACADEMIC')
    the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(3):contains('Urban living')
    the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(3):contains('Infrastructure')
    #the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(4):contains('8')
    #the user should see the element    jQuery=h2:contains('Previously assigned') ~ .table-overflow td:nth-child(5):contains('4')

Custom suite setup
    Guest user log-in    &{Comp_admin1_credentials}
    The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    The user clicks the button/link    jQuery=.button:contains("Manage applications")
    ${initial_application_assesors} =    Get Table Cell    css=.table-overflow    4    4    loglevel=INFO
    ${initial_application_assigned} =    Get Value    jQuery=tr:nth-child(1) td:nth-child(5)
    ${initial_application_submitted} =    Get Value    jQuery=tr:nth-child(1) td:nth-child(6)
    the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    ${initial_assessors_application} =    Get Value    jQuery=h2:contains('Available assessors') ~ .table-overflow td:contains('Paul Plum') ~ td:nth-child(4)
    ${initial_assessors_assigned} =    Get Value    jQuery=h2:contains('Available assessors') ~ .table-overflow td:contains('Paul Plum') ~ td:nth-child(5)
    Set suite variable    ${initial_application_assesors}
    Set suite variable    ${initial_application_assigned}
    Set suite variable    ${initial_application_submitted}
    Set suite variable    ${initial_assessors_application}
    Set suite variable    ${initial_assessors_assigned}
    The user navigates to the page    ${Application_management_dashboard}
