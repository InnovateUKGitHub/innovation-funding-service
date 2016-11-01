*** Settings ***
Documentation     INFUND-1683 As a user of IFS application, if I attempt to perform an action that I am not authorised perform, I am redirected to authorisation failure page with appropriate message
...
...               INFUND-4569: Assessor permissions checks
...
...               INFUND-4562: Securing of services related to Assessor Journey changes - Sprint 14
Suite Teardown    TestTeardown User closes the browser
Test Teardown
Force Tags        Assessor
Resource          ../../../../resources/defaultResources.robot

*** Variables ***
${ASSESSOR_DASHBOARD}    ${SERVER}/assessment/assessor/dashboard
${ASSESSOR_OVERVIEW}    ${SERVER}/assessment/9
${ASSESSOR_ASSESSMENT_QUESTIONS}    ${SERVER}/assessment/9/question/43
${ASSESSOR_ASSESSMENT_QUESTIONS_11}    ${SERVER}/assessment/11/question/43
${ASSESSOR_REVIEW_SUMMARY}    ${SERVER}/assessment/9/summary
${ASSESSOR_ASSESSMENT_QUESTIONS_48}    ${SERVER}/assessment/9/question/48

*** Test Cases ***
Guest user can't access the assessor dashboard
    [Documentation]    INFUND-1683
    [Tags]
    [Setup]    the guest user opens the browser
    When the user navigates to the page    ${ASSESSOR_DASHBOARD}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the assessment's overview page
    [Documentation]    INFUND-1683
    [Tags]
    When the user navigates to the page    ${ASSESSOR_OVERVIEW}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the assessor's review application page
    [Documentation]    INFUND-1683
    [Tags]
    When the user navigates to the page    ${ASSESSOR_ASSESSMENT_QUESTIONS}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access the review summary page
    [Documentation]    INFUND-1683
    [Tags]
    When the user navigates to the page    ${ASSESSOR_REVIEW_SUMMARY}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}
    [Teardown]    the user closes the browser

Applicant can't access the assessment overview page
    [Documentation]    INFUND-1683
    [Tags]
    [Setup]    guest user log-in    &{collaborator2_credentials}
    Then the user navigates to the page and gets a custom error message    ${ASSESSOR_REVIEW_SUMMARY}    ${403_error_message}

Applicant can't access the assessor's dashboard page
    [Documentation]    INFUND-1683
    [Tags]    Pending
    [Setup]
    # Todo pending due to INFUND-4746
    Then the user navigates to the page and gets a custom error message    ${ASSESSOR_DASHBOARD}    You do not have the necessary permissions for your request

Applicant can't access the assessor's review application page
    [Documentation]    INFUND-1683
    [Tags]
    Then the user navigates to the page and gets a custom error message    ${ASSESSOR_ASSESSMENT_QUESTIONS}    You do not have the necessary permissions for your request

Applicant can't access the assessor's review application page (outside the question range)
    [Documentation]    INFUND-4568
    [Tags]
    Then the user navigates to the page and gets a custom error message    ${ASSESSOR_ASSESSMENT_QUESTIONS_48}    You do not have the necessary permissions for your request

Applicant can't access the review summary page
    [Documentation]    INFUND-1683
    [Tags]
    Then the user navigates to the page and gets a custom error message    ${ASSESSOR_REVIEW_SUMMARY}    You do not have the necessary permissions for your request
    [Teardown]    the user closes the browser

First Assessor shouldn't be able to see second assessor's assessments
    [Documentation]    INFUND-4569
    [Tags]
    [Setup]    guest user log-in    paul.plum@gmail.com    Passw0rd
    When the user navigates to the assessor page    ${Assessment_overview_11}
    Then The user should see permissions error message

First assessor shouldn't be able to access second assessor's application questions
    [Documentation]    INFUND-4568
    [Tags]
    When the user navigates to the assessor page    ${ASSESSOR_ASSESSMENT_QUESTIONS_11}
    Then The user should see permissions error message
    [Teardown]    the user closes the browser

Second assessor shouldn't be able to see first assessor's assessments
    [Documentation]    INFUND-4569
    [Tags]
    [Setup]    guest user log-in    felix.wilson@gmail.com    Passw0rd
    When the user navigates to the assessor page    ${Assessment_overview_9}
    Then The user should see permissions error message

Second assessor shouldn't be able to access first assessor's application questions
    [Documentation]    INFUND-4569
    [Tags]
    When the user navigates to the assessor page    ${Application_question_url}
    Then The user should see permissions error message
    [Teardown]    the user closes the browser

*** Keywords ***
