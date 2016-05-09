*** Settings ***
Documentation     INFUND-1683 As a user of IFS application, if I attempt to perform an action that I am not authorised perform, I am redirected to authorisation failure page with appropriate message
Test Teardown     TestTeardown User closes the browser
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***
${ASSESSOR_DASHBOARD}    ${SERVER}/assessor/dashboard
${ASSESSOR_COMPETITIONS_DETAILS}    ${SERVER}/assessor/competitions/1/applications
${ASSESSOR_REVIEW_APPLICATION}    ${SERVER}/assessor/competitions/1/applications/4
${ASSESSOR_DETAILS_PAGE}    ${SERVER}/assessor/competitions/1/applications/3


*** Test Cases ***
Guest user can't access the assessor dashboard
    [Documentation]    INFUND-1683
    [Tags]  Assessor
    Given the guest user opens the browser
    When the user navigates to the page    ${ASSESSOR_DASHBOARD}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the competitions details page
    [Documentation]    INFUND-1683
    [Tags]      Assessor
    Given the guest user opens the browser
    When the user navigates to the page    ${ASSESSOR_COMPETITIONS_DETAILS}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the assessor's review application page
    [Documentation]    INFUND-1683
    [Tags]      Assessor
    Given the guest user opens the browser
    When the user navigates to the page    ${ASSESSOR_REVIEW_APPLICATION}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the assessor's details page
    [Documentation]    INFUND-1683
    [Tags]      Assessor
    Given the guest user opens the browser
    When the user navigates to the page    ${ASSESSOR_DETAILS_PAGE}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Applicant can't access the assessor's dashboard page
    [Documentation]    INFUND-1683
    [Tags]    Pending
    #Pending due to INFUND-1753
    Given guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page and gets a custom error message    ${ASSESSOR_DASHBOARD}    ${403_error_message}


Applicant can't access the competitions details page
    [Documentation]    INFUND-1683
    [Tags]    Pending
    #Pending due to INFUND-1753
    Given guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page and gets a custom error message    ${ASSESSOR_COMPETITIONS_DETAILS}     ${403_error_message}

Applicant can't access the assessor's review application page
    [Documentation]    INFUND-1683
    Given guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page and gets a custom error message    ${ASSESSOR_REVIEW_APPLICATION}    ${404_error_message}


Applicant can't access the assessor's details page
    [Documentation]    INFUND-1683
    Given guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page and gets a custom error message   ${ASSESSOR_DETAILS_PAGE}       ${404_error_message}


*** Keywords ***

