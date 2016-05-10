*** Settings ***
Documentation     INFUND-2601  As a competition administrator I want a view of all applications at the 'Funders Panel' stage
Suite Setup       Log in as user    email=john.doe@innovateuk.test    password=Passw0rd
Suite Teardown    User closes the browser
Force Tags        Comp admin    Funders Panel
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***

${funders_panel_competition_url}        ${server}/management/competition/3

*** Test Cases ***

Comp admin can visit a competition page at "Funder's Panel" stage and the option to notify applicants is disabled
    [Documentation]     INFUND-2601
    When the user navigates to the page      ${funders_panel_competition_url}
    Then the user should see the text in the page       Funders Panel
    And the user should see the element     css=h2.bold-small.blue-block
    And the option to notify applicants is disabled


If a Fund Project option is chosen for each application then the option to notify applicants is enabled
    [Documentation]     INFUND-2601
    When the user selects the option from the drop-down menu   Yes       id=fund16
    And the user selects the option from the drop-down menu     No       id=fund17
    Then the option to notify applicants is enabled

Comp admin can unselect a Fund Project and the option to notify applicants become disabled
    [Documentation]     INFUND-2601
    When the user selects the option from the drop-down menu    -         id=fund16
    the option to notify applicants is disabled


*** Keywords ***

The option to notify applicants is disabled
    the user should see the element     css=#publish-funding-decision.button.disabled

The option to notify applicants is enabled
    the user should see the element     id=publish-funding-decision
    the user should not see the element     css=#publish-funding-decision.button.disabled
