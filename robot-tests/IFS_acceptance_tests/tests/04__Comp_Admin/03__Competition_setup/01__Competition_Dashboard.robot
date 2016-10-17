*** Settings ***
Documentation     INFUND-3830: As a Competitions team member I want to view all competitions that are in the 'Live' state so I can keep track of them and access further details for each competition in that state
...
...               INFUND-3831 As a Competitions team member I want to view all competitions that are in ‘Project Setup’ so I can keep track of them and access further details for each competition in that state.
...
...               INFUND-3832 As a Competitions team member I want to view all competitions that are ‘upcoming’ so I can keep track of them and access further details for each competition in that state
...
...               INFUND-3829 As a Competitions team member I want a dashboard that displays all competitions in different states so I can manage and keep track of them
...
...               INFUND-3004 As a Competition Executive I want the competition to automatically open based on the date that has been provided in the competition open field in the setup phase.
...
...               INFUND-2610 As an internal user I want to be able to view and access all projects that have been successful within a competition so that I can track the project setup process
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot

*** Test Cases ***
Live Competitions
    [Documentation]    INFUND-3830
    ...
    ...    INFUND-3003
    Given the user should see the text in the page    All competitions
    Then the user should see the text in the page    Open
    And the user should see the text in the page    Closed
    And the user should see the text in the page    Panel
    And the user should see the text in the page    Inform
    And the user should not see the text in the page    Sarcasm Stupendousness    # this step verifies that the ready to open competitions are not visible in other tabs

Live competition calculations
    [Documentation]    INFUND-3830
    Then the total calculation in dashboard should be correct    Open    //section[1]/ul/li
    And the total calculation in dashboard should be correct    Closed    //section[2]/ul/li
    And the total calculation in dashboard should be correct    In assessment    //section[3]/ul/li
    And the total calculation in dashboard should be correct    Panel    //section[4]/ul/li
    And the total calculation in dashboard should be correct    Inform    //section[5]/ul/li
    And the total calculation in dashboard should be correct    Live    //section/ul/li

Project setup Competitions
    [Documentation]    INFUND-3831, INFUND-3003, INFUND-2610
    When the user clicks the button/link    jQuery=a:contains(Project setup)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    Then the user should see the text in the page    Project setup
    And the user should see the text in the page    Killer Riffs
    And the user should not see the text in the page    Sarcasm Stupendousness    # this step verifies that the ready to open competitions are not visible in other tabs

Project setup competition calculations
    [Documentation]    INFUND-3831
    Then the total calculation in dashboard should be correct    Project set up    //section[1]/ul/li
    And the total calculation in dashboard should be correct    Project set up    //section/ul/li

PS projects title and lead
    [Documentation]    INFUND-2610
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user should see the element    link=Killer Riffs
    When the user clicks the button/link    link=Killer Riffs
    Then the user should see the element    jQuery=h2:contains("Projects in setup")
    And the user should see the element    jQuery=tr:nth-child(1) th:contains("best riffs")
    And the user should see the element    jQuery=tr:nth-child(1) th:contains("Lead: Vitruvius Stonework Limited")
    And the user should see the element    jQuery=tr:nth-child(2) th:contains("better riffs")
    And the user should see the element    jQuery=tr:nth-child(2) th:contains("Lead: Guitar Gods Ltd")
    And the user should see the element    jQuery=tr:nth-child(3) th:contains("awesome riffs")
    And the user should see the element    jQuery=tr:nth-child(3) th:contains("Lead: Big Riffs And Insane Solos Ltd")

PS projects status
    [Documentation]    INFUND-2610
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link    link=Killer Riffs
    Then the user should see the element    jQuery=tr:nth-child(1):contains("best riffs")
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(1)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(4)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(6)
    [Teardown]    The user navigates to the page    ${COMP_ADMINISTRATOR_DASHBOARD}

Upcoming competitions
    [Documentation]    INFUND-3832
    When the user clicks the button/link    jQuery=a:contains(Upcoming)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    Then the user should see the text in the page    In preparation
    And the user should see the text in the page    Ready to open

Upcoming competitions calculations
    [Documentation]    INFUND-3832
    ...
    ...    INFUND-3003
    ...    INFUND-3876
    Then the total calculation in dashboard should be correct    In preparation    //section[1]/ul/li
    And the total calculation in dashboard should be correct    Ready to open    //section[2]/ul/li
    And the total calculation in dashboard should be correct    Upcoming    //ul[@class="list-overview"]

Upcoming competitions ready for open
    [Documentation]    INFUND-3003
    Then The user should see the text in the page    Sarcasm Stupendousness

Competition Opens automatically on date
    [Documentation]    INFUND-3004
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    Given the user should see the text in the page    Ready to open
    And The competition is ready to open
    When Change the open date of the Sarcasm Stupendousness in the database to one day before
    And the user navigates to the page    ${SERVER}/management/dashboard/live
    Then the user should see the text in the page    Open
    And The competition should be open
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2018-02-24 00:00:00' WHERE `id`='9';

Search existing applications
    [Documentation]    INFUND-3829
    When The user enters text to a text field    id=searchQuery    Juggling Craziness
    And The user clicks the button/link    css=#searchsubmit
    Then The user should see the text in the page    In assessment
    And the total calculation should be correct

Search non existing competition
    [Documentation]    INFUND-3829
    When The user enters text to a text field    id=searchQuery    aaaaaaaaaaaaaaaa
    And The user clicks the button/link    css=#searchsubmit
    Then the result should be correct    0 competitions with the term aaaaaaaaaaaaaa

Clearing filters should show all the competitions
    [Documentation]    INFUND-3829
    When The user clicks the button/link    link=Clear filters
    Then The user should see the element    jQuery=a:contains(Live)

*** Keywords ***
the total calculation should be correct
    [Documentation]    This keyword is for the total of the search results with or without second page
    ${pagination}    ${VALUE}=    run keyword and ignore error    Element Should Be Visible    name=page
    run keyword if    '${pagination}' == 'PASS'    check calculations on both pages
    run keyword if    '${pagination}' == 'FAIL'    check calculations on one page

check calculations on both pages
    ${NO_OF_COMP_Page_one}=    Get Matching Xpath Count    //section/div/ul/li    #gets the Xpaths from the first page
    The user clicks the button/link    name=page
    ${NO_OF_COMP_Page_two}=    Get Matching Xpath Count    //section/div/ul/li    #gets the Xpaths from the second page
    ${total_length}=    Evaluate    ${NO_OF_COMP_Page_one}+${NO_OF_COMP_Page_two}
    ${length_summary}=    Get text    css=.heading-xlarge    #gets the total number
    Should Be Equal As Integers    ${length_summary}    ${total_length}

the result should be correct
    [Arguments]    ${COMPETITION_RESULTS}
    Element Should Contain    css=#searchform p    ${COMPETITION_RESULTS}

check calculations on one page
    ${NO_OF_COMP_Page_one}=    Get Matching Xpath Count    //section/div/ul/li
    ${length_summary}=    Get text    css=.heading-xlarge    #gets the total number
    Should Be Equal As Integers    ${length_summary}    ${NO_OF_COMP_Page_one}

The competition is ready to open
    Then element should contain    jQuery=section:nth-child(4)    Sarcasm Stupendousness

The competition should be open
    And element should contain    jQuery=section:nth-child(3)    Sarcasm Stupendousness
