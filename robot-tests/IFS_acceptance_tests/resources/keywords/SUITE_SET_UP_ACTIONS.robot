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
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Robot test application
    #${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Should Be Equal As Integers    ${NUMBER_OF_APPLICATIONS}    3
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user

Create new application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

log in and create new application for collaboration if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Invite robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new invite application with the same user

Create new invite application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invite robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Applicant navigates to the finances of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Your finances

The user redirects to the page
    [Arguments]    ${TEXT1}    ${TEXT2}
    Wait Until Keyword Succeeds    10    500ms    Page Should Contain    ${TEXT1}
    Page Should Contain    ${TEXT2}
    Page Should Not Contain    error
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible    id=global-header
    Page Should Contain    BETA

The user navigates to the summary page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Review & submit

The user navigates to the overview page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
