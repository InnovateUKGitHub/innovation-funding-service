*** Settings ***
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          GLOBAL_LIBRARIES.robot
Resource          GLOBAL_VARIABLES.robot
Resource          Login_actions.robot
Resource          USER_CREDENTIALS.robot

*** Test Cases ***
Verify the "Review and submit" button (overview page)
    [Documentation]    INFUND-195
    [Tags]    Applicant
    When the applicant is in the overview page
    Then the overview page should have the "Review & Submit" button
    and the button should redirect to the summary page

Verify the "review and submit" button (Form)
    [Tags]    Applicant
    Given the applicant is in the form
    When the Applicant clicks the "Review and submit" button
    Then the applicant will navigate to the summary page

Verify the Warning message when the applicant clicks the submit button in the summary page
    [Documentation]    INFUND-205, INFUND-195
    [Tags]    Applicant
    Comment    Given the application is valid
    Comment    When the applicant submits the application in the summary page
    Comment    Then the applicant should get a warning message

Verify the successful submit page
    [Documentation]    INFUND-205
    [Tags]    Applicant
    Given the application is valid
    When the applicant submits the application
    Then the Applicant should navigate to the "submit confirmation" page
    and the page should have a confirmation text
    and the "Return to dashboard" button is visible
    and the "Return to dashboard" button navigates to the dashboard page

Verify the "Submit button" is disabled when the state of the application is not valid
    [Documentation]    INFUND-195
    [Tags]    Applicant
    Given the application is not valid
    When the Applicant is in the summary page
    Then the submit button should be disabled

*** Keywords ***
the applicant will navigate to the summary page
    Location Should Be    ${SUMMARY_URL}

the Applicant clicks the "Review and submit" button
    click element    link=Review & submit

the applicant submits the application
    Go To    ${APPLICATION_DETAILS_URL}
    Click Element    css=#content > div.grid-row > div.column-two-thirds > form > div.alignright-button > a
    Click Element    css=#content > div.alignright-button > a
    Click Element    css=body > div.modal-confirm-submit > div > a

the Applicant should navigate to the "submit confirmation" page
    Location Should Be    ${APPLICATION_SUBMITTED_URL}

the "Return to dashboard" button is visible
    Page Should Contain Element    link=Return to dashboard

the "Return to dashboard" button navigates to the dashboard page
    Click Element    link=Return to dashboard
    Location Should Be    ${DASHBOARD_URL}

the page should have a confirmation text
    Page Should Contain    Application submitted

the applicant submits the application in the summary page
    Go To    ${DASHBOARD_URL}
    Click Element    link=A novel solution to an old problem
    Click Element    link=Application details
    Click Element    link=Review & submit
    Click Element    link=Submit application

the applicant should get a warning message
    Wait Until Element Is Visible    css=.modal-confirm-submit
    Element Should Be Visible    css=body > div.modal-confirm-submit > div > a
    Element Should Be Visible    css=body > div.modal-confirm-submit > div > button
    Click Element    css=body > div.modal-confirm-submit > div > button

the application is not valid
    Go To    ${APPLICATION_DETAILS_URL}
    Wait Until Element Is Visible    css=#question-12 textarea
    Clear Element Text    css=#question-12 textarea
    Input Text    css=#question-12 textarea    1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10
    Focus    css=.app-submit-btn
    Sleep    2s

the Applicant is in the summary page
    Click Element    css=a.button

the submit button should be disabled
    Element Should Be Disabled    css=#content > div.alignright-button > button

the application is valid
    Go To    ${APPLICATION_DETAILS_URL}
    Wait Until Element Is Visible    css=#question-12 textarea
    Clear Element Text    css=#question-12 textarea
    Focus    css=.app-submit-btn
    Input Text    css=#question-12 textarea    This is a valid text !@#3
    Focus    css=.app-submit-btn
    Sleep    4s

the applicant is in the overview page
    Go To    ${APPLICATION_OVERVIEW_URL}

the overview page should have the "Review & Submit" button
    Page Should Contain Element    css=#content > div.alignright-button > a

the button should redirect to the summary page
    Click Element    css=#content > div.alignright-button > a
    Location Should Be    ${SUMMARY_URL}

the applicant is in the form
    go to    ${SCOPE_SECTION_URL}
