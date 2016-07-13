*** Settings ***
Documentation     INFUND-921 : As an applicant I want to be able to select a link from the competition web page to visit a competition further description page containing relevant links so that I can apply into the competition.
...
...               INFUND-2362: As a tester/developer I want to have a second competition with two applications so I will be able to test the competition status
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${COMPETITION_DETAILS_IN_ASSESSMENT}    ${SERVER}/competition/2/details

*** Test Cases ***
Competition brief link
    [Documentation]    INFUND-2448
    [Tags]    HappyPath
    Given the user navigates to the page    ${LOG_OUT}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    link=full competition brief

Non logged in users see the Apply now button
    [Documentation]    INFUND-921
    [Tags]    Applicant    HappyPath
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    jQuery=.button:contains('Apply now')

Apply button disable when competion is in assessment
    [Documentation]    INFUND-2312
    Given the user navigates to the page    ${LOG_OUT}
    When the user navigates to the page    ${COMPETITION_DETAILS_IN_ASSESSMENT}
    Then the element should be disabled    jQuery=.column-third .button:contains('Apply now')
    And the user should see the text in the page    This competition has now closed

*** Keywords ***
