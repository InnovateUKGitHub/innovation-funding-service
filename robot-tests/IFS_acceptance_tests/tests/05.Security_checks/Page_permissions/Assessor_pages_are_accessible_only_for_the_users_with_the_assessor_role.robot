*** Settings ***
Documentation     INFUND-1683 As a user of IFS application, if I attempt to perform an action that I am not authorised perform, I am redirected to authorisation failure page with appropriate message
Test Teardown     TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${ASSESSOR_DASHBOARD}    ${SERVER}/assessor/dashboard
${ASSESSOR_COPMETITIONS_DETAILS}    ${SERVER}/assessor/competitions/1/applications
${ASSESSOR_REVIEW_APPLICATION}    ${SERVER}/assessor/competitions/1/applications/4
${ASSESSOR_DETAILS_PAGE}    ${SERVER}/assessor/competitions/1/applications/3

*** Test Cases ***
Guest user can't access the Assessor's dashboard page
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    #When the user enters the url of the assessors dashboard page
    When User navigates to the page    ${ASSESSOR_DASHBOARD}
    #Then the user should get the log-in page
    Then user should be redirected to the correct page    ${LOGIN_URL}

Guest user can't access the competitions details page
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    #When the user enters the url of the competitions details page
    When User navigates to the page    ${ASSESSOR_COPMETITIONS_DETAILS}
    #Then the user should get the log-in page
    Then user should be redirected to the correct page    ${LOGIN_URL}

Guest user can't access assessor's review application page
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    #When the user enters the url of the assessors review application page
    When User navigates to the page    ${ASSESSOR_REVIEW_APPLICATION}
    #Then the user should get the log-in page
    Then user should be redirected to the correct page    ${LOGIN_URL}

Guest user can't access the Assessors details page
    [Documentation]    INFUND-1683
    Given the guest user opens the browser
    #When the user enters the url of the Assessors details page
    When User navigates to the page    ${ASSESSOR_DETAILS_PAGE}
    #Then the user should get the log-in page
    Then user should be redirected to the correct page    ${LOGIN_URL}

Applicant can't access Assessor's dashboard page
    [Documentation]    INFUND-1683
    [Tags]    Pending
    #Pending infund-1753
    #Given the user is logged in as applicant
    Given guest user log-in    &{collaborator2_credentials}
    #When the user enters the url of the assessors dashboard page
    When User navigates to the page    ${ASSESSOR_DASHBOARD}
    Then User should get an error page    Oops, something went wrong

Applicant can't access the competitions details page
    [Documentation]    INFUND-1683
    [Tags]    Pending
    #Pending infund-1753
    #Given the user is logged in as applicant
    Given guest user log-in    &{collaborator2_credentials}
    #When the user enters the url of the competitions details page
    When User navigates to the page    ${ASSESSOR_COPMETITIONS_DETAILS}
    Then User should get an error page    Oops, something went wrong

Applicant can't access the Assessor's review application page
    [Documentation]    INFUND-1683
    #Given the user is logged in as applicant
    Given guest user log-in    &{collaborator2_credentials}
    #When the user enters the url of the assessors review application page
    When User navigates to the page    ${ASSESSOR_REVIEW_APPLICATION}
    Then User should get an error page    Oops, something went wrong

Applicant can't access the Assessor's details page
    [Documentation]    INFUND-1683
    #Given the user is logged in as applicant
    Given guest user log-in    &{collaborator2_credentials}
    #When the user enters the url of the Assessors details page
    When User navigates to the page    ${ASSESSOR_DETAILS_PAGE}
    Then User should get an error page    Oops, something went wrong

*** Keywords ***
#the user enters the url of the assessors dashboard page
#   go to    ${ASSESSOR_DASHBOARD}

#The user should get the log-in page
#   Page Should Contain    Sign in

#the user is logged in as applicant
#   Login as user    &{collaborator2_credentials}

#the user enters the url of the competitions details page
#   go to    ${ASSESSOR_COPMETITIONS_DETAILS}

#the user enters the url of the assessors review application page
 #   go to    ${ASSESSOR_REVIEW_APPLICATION}

 #the user enters the url of the Assessors details page
 #   go to    ${ASSESSOR_DETAILS_PAGE}

 #The user should get an error page
  #  Page Should Contain    Oops, something went wrong