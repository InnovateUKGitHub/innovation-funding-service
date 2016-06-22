*** Settings ***
Documentation     INFUND-2945 As a Competition Executive I want to be able to create a new competition from the Competitions Dashboard so Innovate UK can create a new competition
...
...               INFUND-2982: Create a Competition: Step 1: Initial details
...
...               INFUND-2983: As a Competition Executive I want to be informed if the competition will fall under State Aid when I select a 'Competition type' in competition setup
...
...               INFUND-2984: As a Competition Executive I want the competition code field in the 'Initial details' tab in competition setup to generate based on open date and number of competitions in that month
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Comp admin
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
User can navigate to the competition setup form
    [Documentation]    INFUND-2945
    ...
    ...    INFUND-2982
    ...
    ...    INFUND-2983
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    Then the user redirects to the page    Competition Setup    Step 1: Initial competition details
    And The user should not see the element    css=#stateAid

Initial details server-side validations
    [Documentation]    INFUND-2982
    When the user clicks the button/link    jQuery=.button:contains("Done")
    Then The user should see an error    Please select a competition executive
    And the user should see an error    Please enter a opening month
    And the user should see an error    Please enter a opening year
    And the user should see an error    Please enter a opening day
    #And the user should see an error    Competition title validation error
    And the user should see an error    Please select a innovation sector
    And the user should see an error    Please select a innovation area
    And the user should see an error    Please select a competition type
    And the user should see an error    Please select a lead technologist
    #And the user should see an error    PAF number validation error
    #And the user should see an error    Budget code validation error
    And the user clicks the button/link    jQuery=.button:contains("Generate competition code")
    # Then The user should see the text in the page    Please fill in a correct date before generating the competition code

Initial details client-side validations
    [Documentation]    INFUND-2982
    When the user selects the option from the drop-down menu    Competition Executive One    id=executiveUserId
    Then The user should not see the text in the page    Please select a competition executive
    #When The user enters text to a text field    id=title    Competition title
    #Then The user should not see the text in the page    #Competition title error message
    When the user selects the option from the drop-down menu    Health and life sciences    id=innovationSectorCategoryId
    Then The user should not see the text in the page    Please select a innovation sector
    When the user selects the option from the drop-down menu    Advanced Therapies    id=innovationAreaCategoryId
    Then The user should not see the text in the page    Please select a innovation area
    When the user selects the option from the drop-down menu    Technology Inspired    id=competitionTypeId
    Then The user should not see the text in the page    Please select a competition type
    When the user selects the option from the drop-down menu    Competition Technologist One    id=LeadTechnologistUserId
    Then The user should not see the text in the page    Please select a lead technologist
    When The user enters text to a text field    id=pafNumber    2016
    #Then The user should not see the text in the page    #PAF error message
    #When The user enters text to a text field    Id=budgetCode    2004
    #Then The user should not see the text in the page    #Budget error message

The user should see the correct Staite aid status
    [Documentation]    INFUND-2982
    ...
    ...    INFUND-2983
    #the dropdown options are not correct. we need to adjust the tests when the correct option will be added
    When the user selects the option from the drop-down menu    Technology Inspired    id=competitionTypeId
    Then The user should see the element    css=.yes
    When the user selects the option from the drop-down menu    Additive Manufacturing    id=competitionTypeId
    Then The user should see the element    css=.no

Competition code generation
    [Documentation]    INFUND-2984
    When The user enters text to a text field    id=openingDateDay    01
    And The user enters text to a text field    Id=openingDateMonth    12
    And The user enters text to a text field    id=openingDateYear    2016
    And the user clicks the button/link    jQuery=.button:contains("Generate competition code")
    Then competition code should be correct

*** Keywords ***
competition code should be correct
    Wait Until Element Is Not Visible    jQuery=.button:contains("Generate competition code")
    ${input_value} =    Get Value    id=competitionCode
    Should Be Equal As Strings    ${input_value}    1612-1
