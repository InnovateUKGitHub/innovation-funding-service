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
Submit flow/ disabled submit button (incomplete application)
    [Documentation]    INFUND-195
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the application is not valid
    When the Applicant is in the summary page
    Then the submit button should be disabled
    [Teardown]    Project summary question should have a text

Submit flow (complete application)
    [Documentation]    INFUND-205
    ...
    ...    This test case test the submit modal(cancel option) and the the submit of the form, the confirmation page and the new status of the application
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the application is complete
    When the applicant submits the application in the summary page
    Then the applicant should get the submit modal
    When the applicant submits the application in the summary page
    and the applicant clicks Yes in the submit modal
    Then the Applicant should navigate to the "submit confirmation" page
    and the "Return to dashboard" button navigates to the dashboard page
    and the link of the application should redirect to the submitted application page

*** Keywords ***
the Applicant should navigate to the "submit confirmation" page
    Location Should Be    ${APPLICATION_SUBMITTED_URL}
    Page Should Contain    Application submitted

the "Return to dashboard" button navigates to the dashboard page
    Click Element    link=Return to dashboard
    Location Should Be    ${DASHBOARD_URL}

the applicant submits the application in the summary page
    Applicant goes to the Overview page
    Click Element    link=Review & submit
    Click Element    link=Submit application

the applicant should get the submit modal
    Wait Until Element Is Visible    css=.modal-confirm-submit
    Element Should Be Visible    css=body > div.modal-confirm-submit > div > a
    Element Should Be Visible    css=body > div.modal-confirm-submit > div > button
    Click Element    css=body > div.modal-confirm-submit > div > button

the application is not valid
    Applicant goes to the 'project summary' question
    Clear the Project summary field

the Applicant is in the summary page
    Applicant goes to the Overview page
    Click Element    link=Review & submit

the submit button should be disabled
    Element Should Be Disabled    css=.alignright-button button

Mark Question as complete
    Wait Until Element Is Visible    css=#form-input-11 div.textarea-footer > button[name="mark_as_complete"]
    Click Element    css=#form-input-11 div.textarea-footer > button[name="mark_as_complete"]

Enter valid data in the Project summary question
    Wait Until Element Is Visible    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Input Text    css=#form-input-11 .editor    This is a valid text !@#

Clear the Project summary field
    Wait Until Element Is Visible    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Sleep    2s

the link of the application should redirect to the submitted application page
    click link    link=A novel solution to an old problem
    Page Should Contain    Application status

Project summary question should have a text
    Applicant goes to the 'project summary' question
    Wait Until Element Is Visible    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Input Text    css=#form-input-11 .editor    This is a valid text !@#

Mark Question as incomplete
    Go To    ${APPLICATION_DETAILS_URL}
    Wait Until Element Is Visible    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]
    Click Element    css=#form-input-12 div.textarea-footer > button[name="mark_as_incomplete"]

the applicant clicks Yes in the submit modal
    click link    link=Yes, I want to submit my application

Applicant is in the sumary page
    Applicant goes to the Overview page
    Click Element    link=Review & submit

Click to mark the question as complete if the if the question is editable
    [Arguments]    ${Text_Area}
    ${status}    ${Value}=    Run Keyword And Ignore Error    Element Should Be Visible    jQuery=jQuery("button:contains('Mark as complete')");
    Run Keyword If    '${status}' == 'PASS'    Input Text    ${Text_Area}    test 123
    Run Keyword If    '${status}' == 'PASS'    Click element    jQuery=jQuery("button:contains('Mark as complete')");
    Run Keyword If    '${status}' == 'PASS'    Wait Until Element Is Visible    jQuery=jQuery("button:contains('Edit')");
