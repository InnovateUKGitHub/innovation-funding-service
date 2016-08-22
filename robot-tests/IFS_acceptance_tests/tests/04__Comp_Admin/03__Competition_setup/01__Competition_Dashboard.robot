*** Settings ***
Documentation     INFUND-3830: As a Competitions team member I want to view all competitions that are in the 'Live' state so I can keep track of them and access further details for each competition in that state
...
...               INFUND-3831 As a Competitions team member I want to view all competitions that are in ‘Project Setup’ so I can keep track of them and access further details for each competition in that state.
...
...               INFUND-3832 As a Competitions team member I want to view all competitions that are ‘upcoming’ so I can keep track of them and access further details for each competition in that state
...
...               INFUND-3829 As a Competitions team member I want a dashboard that displays all competitions in different states so I can manage and keep track of them
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    CompSetup
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

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
    Then the calculations should be correct    Open    //section[1]/ul/li
    And the calculations should be correct    Closed    //section[2]/ul/li
    And the calculations should be correct    In assessment    //section[3]/ul/li
    And the calculations should be correct    Panel    //section[4]/ul/li
    And the calculations should be correct    Inform    //section[5]/ul/li
    And the calculations should be correct    Live    //section/ul/li

Project setup Competitions
    [Documentation]    INFUND-3831
    ...
    ...    INFUND-3003
    When the user clicks the button/link    jQuery=a:contains(Project set up)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    Then the user should see the text in the page    Project set up
    And the user should see the text in the page    Killer Riffs
    And the user should not see the text in the page    Sarcasm Stupendousness    # this step verifies that the ready to open competitions are not visible in other tabs

Project setup competition calculations
    [Documentation]    INFUND-3831
    Then the calculations should be correct    Project set up    //section[1]/ul/li
    And the calculations should be correct    Project set up    //section/ul/li

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
    Then the calculations should be correct    In preparation    //section[1]/ul/li
    And the calculations should be correct    Ready to open    //section[2]/div/div/ul/li
    And the calculations should be correct    Upcoming    //ul[@class="list-overview"]

Upcoming competitions ready for open
    [Documentation]    INFUND-3003
    Then The user should see the text in the page    Sarcasm Stupendousness

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
the calculations should be correct
    [Arguments]    ${Status}    ${Section_Xpath}
    [Documentation]    This keyword uses 2 arguments. The first one is about the status of the competition and the second is about the Xpath of the section.
    ${NO_OF_COMP}=    Get Matching Xpath Count    ${Section_Xpath}
    Page Should Contain    ${Status} (${NO_OF_COMP})

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
