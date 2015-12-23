*** Settings ***
Documentation     This test has been put last (with the 1.) because the other application tests depend on the application not being submitted.
...
...               -INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission.
...
...
...               -INFUND-185: As an applicant, on the application summary and pressing the submit application button, it should give me a message that I can no longer alter the application.
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Test Cases ***
Verify the "review and submit" button (Form)
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the applicant is in the form
    When the Applicant clicks the "Review and submit" button
    Then the applicant will navigate to the summary page

Verify the "Submit button" is disabled when the state of the application is not valid
    [Documentation]    INFUND-195
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the application is not valid
    When the Applicant is in the summary page
    Then the submit button should be disabled

Verify the Warning message when the applicant clicks the submit button in the summary page
    [Documentation]    INFUND-205, INFUND-195
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the application is valid
    When the applicant submits the application in the summary page
    Then the applicant should get a warning message
    [Teardown]    Mark Question as incomplete

Verify the successful submit page
    [Documentation]    INFUND-205
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the application is valid
    When the applicant submits the application
    Then the Applicant should navigate to the "submit confirmation" page
    and the page should have a confirmation text
    and the "Return to dashboard" button is visible
    and the "Return to dashboard" button navigates to the dashboard page
    and the link of the application should redirect to the submitted application page

*** Keywords ***
the applicant will navigate to the summary page
    Location Should Be    ${SUMMARY_URL}

the Applicant clicks the "Review and submit" button
    click element    link=Review & submit

the applicant submits the application
    Go To    ${APPLICATION_DETAILS_URL}
    Click Element    link=Review & submit
    Wait Until Element Is Visible    xpath=//*[@data-js-modal="modal-confirm-submit"]
    Click Element    xpath=//*[@data-js-modal="modal-confirm-submit"]
    Click Element    css=.modal-confirm-submit .button

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
    Click Element    xpath=//*[@aria-controls="collapsible-1"]
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
    Enter invalid data into Question field
    #Mark Question as complete

the Applicant is in the summary page
    sleep    1s
    focus    css=a.button
    Click Element    css=a.button

the submit button should be disabled
    Element Should Be Disabled    css=#content > div.alignright-button > button

the application is valid
    Go To    ${APPLICATION_DETAILS_URL}
    Enter valid data into Question field
    Mark Question as complete

the applicant is in the overview page
    Applicant goes to the Overview page

the overview page should have the "Review & Submit" button
    Page Should Contain Element    css=#content > div.alignright-button > a

the button should redirect to the summary page
    Click Element    css=#content > div.alignright-button > a
    Location Should Be    ${SUMMARY_URL}

the applicant is in the form
    go to    ${SCOPE_SECTION_URL}

Mark Question as complete
    Wait Until Element Is Visible    css=#form-input-12 div.textarea-footer > button[name="mark_as_complete"]
    Click Element    css=#form-input-12 div.textarea-footer > button[name="mark_as_complete"]

Mark Question as incomplete
    Go To    ${APPLICATION_DETAILS_URL}
    Wait Until Element Is Visible    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]
    Click Element    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]

Enter valid data into Question field
    Wait Until Element Is Visible    css=#form-input-12 .editor
    Clear Element Text    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    This is a valid text !@#

Enter invalid data into Question field
    Wait Until Element Is Visible    css=#form-input-12 .editor
    Clear Element Text    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 \ 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10 1 2 3 4 5 6 7 8 9 10

the link of the application should redirect to the submitted application page
    click link    link=A novel solution to an old problem
    Page Should Contain    Application status
