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
...
...               IFS-1881 Project Setup internal project dashboard navigation
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Test Cases ***
Sections of Live Competitions
    [Documentation]    INFUND-3830 INFUND-3003
    Given the user should see the element      jQuery = h1:contains("All competitions")
    Then the user should see the element       jQuery = h2:contains("Open")
    And the user should see the element        jQuery = h2:contains("Closed")
    And the user should see the element        jQuery = h2:contains("Panel")
    And the user should see the element        jQuery = h2:contains("Inform")
    And the user should not see the element    link = ${READY_TO_OPEN_COMPETITION_NAME}
    # this step verifies that the ready to open competitions are not visible in other tabs

Live competition calculations
    [Documentation]    INFUND-3830
    Then the total calculation in dashboard should be correct    Open    //section[1]/ul/li
    And the total calculation in dashboard should be correct     Closed    //section[2]/ul/li
    And the total calculation in dashboard should be correct     In assessment    //section[3]/ul/li
    And the total calculation in dashboard should be correct     Panel    //section[4]/ul/li
    And the total calculation in dashboard should be correct     Inform    //section[5]/ul/li
    And the total calculation in dashboard should be correct     Live    //section/ul/li

Project setup Competitions and Calculations
    [Documentation]    INFUND-3831, INFUND-3003, INFUND-2610 INFUND-5176
    Given the user clicks the button/link       jQuery = a:contains(Project setup)
    Then the user should see competitions in project set up
    And The Project set up dashboard calculations should be correct   css = li.govuk-grid-row  Project setup   //section[1]/ul/li

PS projects title and lead
    [Documentation]    INFUND-2610, IFS-1881
    When the internal user navigates to the project setup competition       ${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element                                    link = All projects
    And the user should see the element                                     jQuery = tr:nth-child(1) th:contains("Elbow grease")
    And the user should see the element                                     jQuery = tr:nth-child(1) th:contains("Lead: Big Riffs And Insane Solos Ltd")
    And the user should see the element                                     jQuery = tr:nth-child(2) th:contains("${PS_PD_Application_Id}")
    And the user should see the element                                     jQuery = tr:nth-child(2) th:contains("Lead: ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}")
    And the user should see the element                                     jQuery = tr:nth-child(3) th:contains("Office Chair for Life")
    And the user should see the element                                     jQuery = tr:nth-child(3) th:contains("Lead: Guitar Gods Ltd")

PS projects status page
    [Documentation]    INFUND-2610, IFS-1881
    When the internal user navigates to the project setup competition       ${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element                                    jQuery = tr:nth-child(2):contains("${PS_PD_Application_Title}")
    And the user should see the element                                     link = All projects
    [Teardown]    The user navigates to the page                            ${COMP_ADMINISTRATOR_DASHBOARD}

Upcoming competitions
    [Documentation]    INFUND-3832
    When the user clicks the button/link    jQuery = a:contains(Upcoming)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    Then the user should see the element    jQuery = h2:contains("In preparation")
    And the user should see the element     jQuery = h2:contains("Ready to open")

Upcoming competitions calculations
    [Documentation]    INFUND-3832  INFUND-3003  INFUND-3876
    Then the total calculation in dashboard should be correct    In preparation    //section[1]/ul/li
    And the total calculation in dashboard should be correct     Ready to open     //section[2]/ul/li
    And the total calculation in dashboard should be correct     Upcoming          //section/ul/li

Competition Opens automatically on date
    [Documentation]    INFUND-3004
    [Tags]
    Given the user should see the element    jQuery = h2:contains('Ready to open') ~ ul a:contains('${READY_TO_OPEN_COMPETITION_NAME}')
    When update milestone to yesterday       ${READY_TO_OPEN_COMPETITION}  OPEN_DATE
    When the user navigates to the page      ${CA_Live}
    Then the user should see the element     jQuery = h2:contains('Open') ~ ul a:contains('${READY_TO_OPEN_COMPETITION_NAME}')
    [Teardown]  Return the competition's milestones to their initial values           ${READY_TO_OPEN_COMPETITION}  ${READY_TO_OPEN_COMPETITION_OPEN_DATE_DB}  ${READY_TO_OPEN_COMPETITION_CLOSE_DATE_DB}

Search existing applications
    [Documentation]    INFUND-3829
    When The user enters text to a text field    id = searchQuery    ${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link          css = #searchsubmit
    Then The user should see the element         jQuery = h2:contains("In assessment")
    And the total calculation should be correct

Search non existing competition
    [Documentation]    INFUND-3829
    When The user enters text to a text field    id = searchQuery    aaaaaaaaaaaaaaaa
    And The user clicks the button/link          css = #searchsubmit
    Then the result should be correct            0 competitions with the term aaaaaaaaaaaaaa

Clearing filters should show all the competitions
    [Documentation]    INFUND-3829
    When The user clicks the button/link    link = Clear filters
    Then The user should see the element    jQuery = a:contains(Live)

Non IFS competitions
    [Documentation]    INFUND-7963
    When the user clicks the button/link    jQuery = a:contains(Non-IFS)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    Then the user should see the element    jQuery = h2:contains("Non-IFS competitions ")
    And the user should see the element     link = ${NON_IFS_COMPETITION_NAME}

Non IFS competitions do not appear in search results
    [Documentation]    INFUND-7963
    When The user enters text to a text field    id = searchQuery    ${NON_IFS_COMPETITION_NAME}
    And The user clicks the button/link          css = #searchsubmit
    Then the result should be correct            0 competitions with the term ${NON_IFS_COMPETITION_NAME}

*** Keywords ***
The user should see competitions in project set up
    # We have used the JQuery selector for the link because the title will change according to the competitions number
    the user should see the element       jQuery = .govuk-heading-m:contains("Project setup ")
    the user should see the element        link = Project setup loan comp
    the user should see the element        jQuery =.govuk-body:contains("5 projects")
    the user should not see the element    link = ${READY_TO_OPEN_COMPETITION_NAME}
    # this step verifies that the ready to open competitions are not visible in other tabs

The Project set up dashboard calculations should be correct
    [Arguments]    ${comp_List_Locator}   ${TEXT}   ${Section_Xpath}
    ${pagination} =   Run Keyword And Ignore Error Without Screenshots  the user clicks the button/link  name = page
    run keyword if  ${pagination} == 'PASS'  Check number of competitions on both pages  ${comp_List_Locator}  ${TEXT}  ${Section_Xpath}
    run keyword if  ${pagination} == 'FAIL'  Check number of competitions on one page  ${comp_List_Locator}  ${TEXT}  ${Section_Xpath}

Check number of competitions on one page
    [Arguments]   ${comp_List_Locator}  ${TEXT}   ${Section_Xpath}
    ${NO_OF_COMP_OR_APPL} =  Get Element Count    ${Section_Xpath}
    ${element} =  Get Webelements      ${comp_List_Locator}
    ${length_list} =  Get Length       ${element}
    Should Be Equal As Integers    ${NO_OF_COMP_OR_APPL}    ${length_list}

Check number of competitions on both pages
    [Arguments]    ${comp_List_Locator}  ${TEXT}   ${Section_Xpath}
    ${NO_OF_COMP_OR_APPL} =  Get Element Count    ${Section_Xpath}
    ${element_page_two} =    Get Webelements    ${comp_List_Locator}
    ${length_list_page_two} =    Get Length    ${element_page_two}
    the user clicks the button/link    link = Next
    ${element} =  Get Webelements    ${comp_List_Locator}
    ${length_list} =  Get Length    ${element}
    ${total_length} =  Evaluate    ${length_list}}+${length_list_page_two}
    Should Be Equal As Integers    ${NO_OF_COMP_OR_APPL}   ${total_length}

Custom suite setup
    Connect to Database  @{database}
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Change the milestone in the database to tomorrow     ${READY_TO_OPEN_COMPETITION}  OPEN_DATE

the total calculation should be correct
    [Documentation]    This keyword is for the total of the search results with or without second page
    ${pagination}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    name=page
    run keyword if    '${pagination}' == 'PASS'    check calculations on both pages
    run keyword if    '${pagination}' == 'FAIL'    check calculations on one page

check calculations on both pages
    ${NO_OF_COMP_Page_one} =    Get Element Count    //section/div/ul/li    #gets the Xpaths from the first page
    The user clicks the button/link    name = page
    ${NO_OF_COMP_Page_two} =    Get Element Count    //section/div/ul/li    #gets the Xpaths from the second page
    ${total_length} =    Evaluate    ${NO_OF_COMP_Page_one}+${NO_OF_COMP_Page_two}
    ${length_summary} =    Get text    css = .govuk-body span     #gets the total number
    Should Be Equal As Integers    ${length_summary}    ${total_length}

the result should be correct
    [Arguments]    ${COMPETITION_RESULTS}
    Element Should Contain    css = #searchform p    ${COMPETITION_RESULTS}

check calculations on one page
    ${NO_OF_COMP_Page_one} =    Get Element Count    //section/div/ul/li
    ${length_summary} =    Get text    css = form .govuk-body span    #gets the total number
    Should Be Equal As Integers    ${length_summary}    ${NO_OF_COMP_Page_one}

Custom suite teardown
    the user closes the browser
    Disconnect from database