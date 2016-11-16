*** Settings ***
Documentation     INFUND-1423 Going back from the 'create your account' page gives an error
Suite Setup       The guest user opens the browser
Suite Teardown
Test Teardown     The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Click the back button while on the create account page
    [Documentation]    INFUND-1423
    [Tags]
    Given the user navigates to the page    ${LOGIN_URL}
    When the user follows the flow to register their organisation
    And the user goes back to the previous page
    Then the user should be redirected to the correct page    ${confirm_organisation_url}

The user logs in and visits the create account page
    [Documentation]    INFUND-1423
    [Tags]
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    Then the user should see the text in the page    Your Profile

*** Keywords ***
