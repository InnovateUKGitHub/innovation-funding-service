*** Settings ***
Documentation     INFUND-3970 As a partner I want a spend profile page in Project setup so that I can access and share Spend profile information within my partner organisation before submitting to the Project Manager
...
...               INFUND-3764 As a partner I want to view a Spend Profile showing my partner organisation's eligible project costs divided equally over the duration of our project to begin review our project costs before submitting to the Project Manager
Suite Setup       the project finance user generates the spend profile table
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Variables ***

*** Test Cases ***
Lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]    HappyPath
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK

Lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page    January 2017
    And the user should see the text in the page    36 Months

Calculations in the spend profile table
    [Documentation]    INFUND-3764
    [Tags]    HappyPath
    Given the user should see the element    jQuery=div.spend-profile-table
    Then element should contain    css=div.spend-profile-table tr:nth-child(1) td:nth-child(38)    £ 104,354    #Labour
    Then element should contain    css=div.spend-profile-table tr:nth-child(2) td:nth-child(38)    £ 0    #Overheads
    Then element should contain    css=div.spend-profile-table tr:nth-child(3) td:nth-child(38)    £ 188,025    #Materials
    Then element should contain    css=div.spend-profile-table tr:nth-child(4) td:nth-child(38)    £ 0    #Capital usage
    Then element should contain    css=div.spend-profile-table tr:nth-child(5) td:nth-child(38)    £ 23,423    #Subcontracting
    Then element should contain    css=div.spend-profile-table tr:nth-child(6) td:nth-child(38)    £ 7,956    #Travel & subsistence
    Then element should contain    css=div.spend-profile-table tr:nth-child(7) td:nth-child(38)    £ 32,444    #Other costs
    #${duration} is No of Months + 1, due to header
    And the sum of tds equals the total    div.spend-profile-table    1    38    104354    # Labour
    And the sum of tds equals the total    div.spend-profile-table    3    38    188025    # Materials
    And the sum of tds equals the total    div.spend-profile-table    5    38    23423    # Subcontracting
    And the sum of tds equals the total    div.spend-profile-table    6    38    7956    # Travel & subsistence
    And the sum of tds equals the total    div.spend-profile-table    7    38    32444    # Other Costs
    [Teardown]    Logout as user

Non-lead partner can view spend profile page
    [Documentation]    INFUND-3970
    [Tags]
    [Setup]    Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user clicks the button/link    link=00000001: best riffs
    When the user clicks the button/link    link=Spend profile
    Then the user should not see an error in the page
    And the user should see the text in the page    Your project costs have been reviewed and confirmed by Innovate UK

Non-lead partner can see correct project start date and duration
    [Documentation]    INFUND-3970
    [Tags]
    Then the user should see the text in the page    1
    And the user should see the text in the page    January 2017
    And the user should see the text in the page    36 Months
    [Teardown]    Logout as user

*** Keywords ***
the project finance user generates the spend profile table
    log in as user    project.finance1@innovateuk.test    Passw0rd
    the user navigates to the page    ${server}/project-setup-management/project/1/spend-profile/summary    # For now we need to go to the url directly, as the project finance dashboard doesn't exist yet.
    the user clicks the button/link    jQuery=.button:contains("Generate Spend Profile")
    the user clicks the button/link    name=submit-app-details    # this second click is confirming the decision on the modal
    logout as user

the sum of tds equals the total
    [Arguments]    ${table}    ${row}    ${duration}    ${total}
    # This Keyword perforfms a for loop that iterates per column (in a specific row)
    # gets the sum of the cells and evaluates whether the sum of them equals their total
    ${sum} =    convert to number    0
    ${total} =    convert to number    ${total}
    : FOR    ${i}    IN RANGE    2    ${duration}    # due to header in the first column
    \    ${text} =    Get Text    jQuery=${table} tr:nth-child(${row}) td:nth-child(${i})
    \    ${formatted} =    Remove String    ${text}    ,    # Remove the comma from the number
    \    ${cell} =    convert to integer    ${formatted}
    \    ${sum} =    Evaluate    ${sum}+${cell}
    Should Be Equal As Integers    ${sum}    ${total}
