*** Settings ***
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot

*** Keywords ***
log in and create new application if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${NUMBER_OF_APPLICATIONS}=    Get matching xpath count    xpath=//*[@class='in-progress']/ul/li
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Should Be Equal As Integers    ${NUMBER_OF_APPLICATIONS}    3
    Run Keyword If    '${status}' == 'PASS'    Create new application with the same user

Create new application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
