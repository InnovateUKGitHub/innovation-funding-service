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
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
View the list of the applications
    [Documentation]    INFUND-7042
    [Tags]
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=a:contains("Assessor management: Assignments")
    Then the application list is correct before changes

Filtering of the applications
    [Documentation]    INFUND-8061
    When The user enters text to a text field    css=#filterSearch    22
    and The user clicks the button/link    jQuery=button:contains(Filter)
    Then the user should see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("22")
    And The user clicks the button/link    link=Clear all filters
    then the user should not see the element    jQuery=tr:nth-child(1) td:nth-child(1):contains("22")

Application number navigates to Overview
    [Documentation]    INFUND-7042
    [Tags]
    When the user clicks the button/link    link=22
    Then The user should see the text in the page    22: Intelligent water system
    And the user should see the text in the page    University of Bath
    And the user should see the text in the page    Cardiff University
    [Teardown]    the user clicks the button/link    link=Back

View application progress page
    [Documentation]    INFUND-7042, INFUND-7046
    [Tags]
    Given the user clicks the button/link    jQuery=tr:nth-child(9) a:contains(View progress)
    Then The user should see the text in the page    29: Living with Augmented Reality
    And the user should see the text in the page    Tripplezap (Lead)
    And the user should see the element             jQuery=h3:contains("Innovation area") ~ span:contains("Nanotechnology / nanomaterials")
    And the user should see the text in the page    No assessors have been assigned to this application.
    And the user should see the text in the page    No assessors have rejected this application.
    And the user should see the text in the page    No assessors were previously assigned to this application.

Review the application
    [Documentation]    INFUND-7046
    [Tags]
    When the user clicks the button/link    link=Review application
    Then the user should see the text in the page    Application overview
    And the user should see the element    jQuery=dt:contains("Innovation area") + dd:contains("Nanotechnology / nanomaterials")
    [Teardown]    The user goes back to the previous page

View the available assessors
    [Documentation]    INFUND-7233\\
    [Tags]
    Then the user should see the element    jQuery=.column-two-thirds:contains("Assessors")
    And the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    And the available assessors information is correct

View the assigned list
    [Documentation]    INFUND-7230 INFUND-7038
    [Tags]
    Given The user should see the element    jQuery=p:contains(No assessors have been assigned to this application.)
    When the user clicks the button/link    jQuery=tr:contains(Paul Plum) button:contains("Assign")
    Then the user should see the text in the page    Assigned (1)
    And the assigned list is correct before notification
    And the user clicks the button/link    jQuery=.link-back:contains("Allocate applications")
    Then the user should see the element    jQuery=tr:nth-child(9) td:nth-child(4):contains("1")

Remove an assigned user (Not notified)
    [Documentation]    INFUND-7230
    [Tags]
    Given the user clicks the button/link    jQuery=tr:nth-child(9) a:contains(View progress)
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove assessor")
    And the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    And the available assessors information is correct

Notify an assigned user
    [Documentation]    INFUND-7050
    [Tags]
    Given the user clicks the button/link    jQuery=tr:contains(Paul Plum) button:contains("Assign")
    And the user clicks the button/link    jQuery=a:contains("Allocate applications")
    And the user clicks the button/link    jQuery=a:contains("Competition")
    And the user clicks the button/link    jQuery=button:contains("Notify assessors")
    And the element should be disabled    jQuery=button:contains("Notify assessors")
    #TODO Check email once 7249 is done

Assessor should see the assigned application
    [Documentation]    INFUND-7050
    [Setup]    Log in as a different user    email=paul.plum@gmail.com    password=Passw0rd
    When The user clicks the button/link    link=Sustainable living models for the future
    Then The user should see the element    Link=Living with Augmented Reality

Remove and notify an assessor (Notified)
    [Documentation]    INFUND-7232
    [Tags]
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery=a:contains("Assessor management: Assignments")
    And the user clicks the button/link    jQuery=tr:nth-child(9) a:contains(View progress)
    When the user clicks the button/link    jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link    jQuery=.buttonlink:contains(Cancel)
    And the user should not see the element    jQuery=button:contains("Remove assessor")
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove assessor")
    And the user should see the text in the page    Previously assigned (1)
    And the previously assigned list is correct
    #TODO Check email once 7249 is done

Assessor should not see the removed application
    [Documentation]    INFUND-7232
    [Setup]    Log in as a different user    email=paul.plum@gmail.com    password=Passw0rd
    When The user clicks the button/link    link=Sustainable living models for the future
    Then The user should not see the element    Link=Living with Augmented Reality

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
    the user should see the element    jQuery=.assessors-assigned td:nth-child(2):contains("ACADEMIC")
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
    the user should see the element    jQuery=.assessors-previous td:nth-child(2):contains('ACADEMIC')
    the user should see the element    jQuery=.assessors-previous td:nth-child(3):contains('Urban living')
    the user should see the element    jQuery=.assessors-previous td:nth-child(3):contains('infrastructure')
    #the user should see the element    jQuery=.assessors-previous td:nth-child(4):contains('8')
    #the user should see the element    jQuery=.assessors-previous td:nth-child(5):contains('4')
    #TODO checks disabled due toINFUND-7745
