*** Settings ***
Documentation     INFUND-2606 - As a competition administrator I want a view of all applications at the 'Assessor Feedback' stage so that I can publish their uploaded assessor feedback
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Force Tags        Pending
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***

Comp admin can see a list of applications at Assessor Feedback stage
    [Documentation]     INFUND-2606
    [Tags]
    When the user navigates to the page   ${server}/management/competition/3
    Then the user should see the text in the page       Assessor Feedback
    And the user should see the text in the page    Cheese is Great
    And the user should see the text in the page    Cheese is Good


The 'Fund project?' column title is now 'Funded' and isn't editable
    [Documentation]     INFUND-2606
    [Tags]
    When the user should see the text in the page   Funded
    And the user should not see the text in the page    Fund project?
    Then the user should not see the element    id=fund16
    And the user should not see the element     id=fund17


Publish assessor feedback button is now visible
    [Documentation]     INFUND-2606
    [Tags]
    When the user should not see the element       jQuery=.button:contain("Notify applicants")
    Then the user should see the element       jQuery=.button:contains("Publish assessor feedback")


*** Keywords ***

