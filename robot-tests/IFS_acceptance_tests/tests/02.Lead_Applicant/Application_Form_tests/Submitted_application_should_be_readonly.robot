*** Settings ***
Documentation     INFUND-1891: As the Service Delivery Manager I want to prevent applicants from editing any of their application content following successful submission so that the submitted content is considered final
Test Setup        The guest user opens the browser
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***



*** Test Cases ***
Verify the submitted application is readonly
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    [Tags]
    Given the user navigates to the page        ${APPLICATION_OVERVIEW_URL}
    And the user enters all questions and mark as complete


*** Keywords ***
the user enters all questions and mark as complete
    Given the user goes to Application details page
