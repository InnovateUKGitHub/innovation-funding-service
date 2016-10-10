*** Settings ***
Documentation     INFUND-921 : As an applicant I want to be able to select a link from the competition web page to visit a competition further description page containing relevant links so that I can apply into the competition.
...
...               INFUND-2362: As a tester/developer I want to have a second competition with two applications so I will be able to test the competition status
Suite Setup       The guest user opens the browser
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
    [Tags]
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    link=full competition brief
    And The user clicks the button/link    link=full competition brief
    And the new window should have the competition brief

Non logged in users see the Apply now button
    [Documentation]    INFUND-921
    [Tags]    HappyPath
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    Then the user should see the element    jQuery=.button:contains('Apply now')

Apply button disable when competion is in assessment
    [Documentation]    INFUND-2312
    [Tags]    HappyPath
    When the user navigates to the page    ${COMPETITION_DETAILS_IN_ASSESSMENT}
    Then the element should be disabled    jQuery=.column-third .button:contains('Apply now')
    And the user should see the text in the page    This competition has now closed

*** Keywords ***
the new window should have the competition brief
    sleep    500ms
    Select Window    url=https://www.gov.uk/government/publications/funding-competition-connected-digital-additive-manufacturing/connected-digital-additive-manufacturing-competition-brief
    The user should see the text in the page    Dates and deadlines
    The user should see the text in the page    How to apply
