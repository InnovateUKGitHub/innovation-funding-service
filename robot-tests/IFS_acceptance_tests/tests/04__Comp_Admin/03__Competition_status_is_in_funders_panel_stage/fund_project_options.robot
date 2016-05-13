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
${dialogue_warning_message}

*** Test Cases ***

Comp admin can visit a competition page at "Funder's Panel" stage and the option to notify applicants is disabled
    [Documentation]     INFUND-2601
    When the user navigates to the page      ${funders_panel_competition_url}
    Then the user should see the text in the page       Funders Panel
    And the user should see the element     css=h2.bold-small.blue-block
    And the option to notify applicants is disabled


If a Fund Project option is chosen for each application then the option to notify applicants is enabled
    [Documentation]     INFUND-2601
    [Tags]
    When the user selects the option from the drop-down menu   Yes       id=fund16
    And the user selects the option from the drop-down menu     No       id=fund17
    Then the option to notify applicants is enabled

Comp admin can unselect a Fund Project and the option to notify applicants become disabled
    [Documentation]     INFUND-2601
    [Tags]
    When the user selects the option from the drop-down menu    -         id=fund16
    Then the option to notify applicants is disabled

Pushing the notify applicants button brings up a warning dialogue
    [Documentation]     INFUND-2646
    [Tags]
    [Setup]     The user selects the option from the drop-down menu     Yes     id=fund16
    When the user clicks the button/link    jQuery=.button:contains("Notify applicants")
    Then the user should see the text in the page   ${dialogue_warning_message}
    And the user should see the element     jQuery=.button:contains("Cancel")
    And the user should see the element     jQuery=.button:contains("Notify applicants")


Choosing cancel on the dialogue goes back to the Funder's Panel page
    [Documentation]     INFUND-2646
    [Tags]
    When the user clicks the button/link        jQuery=.button:contains("Cancel")
    Then the user should be redirected to the correct page  ${funders_panel_competition_url}
    And the user should see the text in the page    Funders Panel
    [Teardown]     The user clicks the button/link     jQuery=.button:contains("Notify applicants")


Choosing Notify applicants on the dialogue redirects to the Assessor feedback page
    [Documentation]     INFUND-2646
    [Tags]
    When the user clicks the button/link    name=publish
    Then the user should be redirected to the correct page      ${funders_panel_competition_url}
    And the user should see the text in the page    Assessor Feedback

Once applicants are notified, the whole state of the competition changes to Assessor feedback
    [Documentation]     INFUND-2646
    [Tags]
    Then the user should see the text in the page      Projects in setup
    And the user should see the text in the page    Assessor Feedback



*** Keywords ***

The option to notify applicants is disabled
    the user should see the element     css=#publish-funding-decision.button.disabled

The option to notify applicants is enabled
    the user should see the element     id=publish-funding-decision
    the user should not see the element     css=#publish-funding-decision.button.disabled
