*** Settings ***
Documentation     INFUND-1683 As a user of IFS application, if I attempt to perform an action that I am not authorised perform, I am redirected to authorisation failure page with appropriate message
Suite Teardown    The user closes the browser
Resource          ../../../../resources/defaultResources.robot

*** Variables ***
${APPLICATION_7_OVERVIEW_PAGE}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_4_NUMBER}
${APPLICATION_7_FORM}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_4_NUMBER}/form/question/428

*** Test Cases ***
Guest user can't access overview page
    [Documentation]    INFUND-1683
    [Setup]    The guest user opens the browser
    When the user navigates to the page    ${APPLICATION_7_OVERVIEW_PAGE}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

Guest user can't access application form
    [Documentation]    INFUND-1683
    When the user navigates to the page    ${APPLICATION_7_FORM}
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}
    [Teardown]    the user closes the browser

Applicant who is not team member can't access overview page
    [Documentation]    INFUND-1683
    Given The user logs-in in new browser  &{collaborator2_credentials}
    Then the user navigates to the page and gets a custom error message    ${APPLICATION_7_OVERVIEW_PAGE}   ${403_error_message}

Applicant who is not team member can't access application form page
    [Documentation]    INFUND-1683
    Then the user navigates to the page and gets a custom error message    ${APPLICATION_7_FORM}     ${403_error_message}
    [Teardown]    the user closes the browser

Assessor can't access the overview page
    [Documentation]    INFUND-1683
    [Setup]  The user logs-in in new browser  &{assessor_credentials}
    When the user navigates to the page and gets a custom error message    ${APPLICATION_7_OVERVIEW_PAGE}   ${403_error_message}

Assessor can't access the application form
    [Documentation]    INFUND-1683
    When the user navigates to the page and gets a custom error message    ${APPLICATION_7_FORM}    ${403_error_message}


