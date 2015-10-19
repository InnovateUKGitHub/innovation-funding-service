*** Settings ***
Documentation     -INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               -INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
...
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          GLOBAL_LIBRARIES.robot
Resource          GLOBAL_VARIABLES.robot
Resource          Login_actions.robot
Resource          USER_CREDENTIALS.robot
Resource          Applicant_actions.robot

*** Test Cases ***
Verify that the assign button is visible in the overview page
    [Tags]    Collaborator
    When the Applicant is in the overview page
    Then the collaborators should be visible in the overview page

Verify the applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    Collaborator
    Given the Applicant is in the application details section
    When the Applicant assigns the question 11 to the collaborator    Jessica Doe
    Then the success message should show
    and the field of the question 11 should be disabled
    and the last update should show the collaborators name

Verify the field is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    Collaborator
    Given the second Collaborator is logged in
    When the second Collaborator is in the application overview page
    Then the question 11 should not be editable

Verify the field is enabled for the collaborator/assignee
    [Documentation]    INFUND-275
    [Tags]    Collaborator
    When the Collaborator is in the Application details section
    Then the Collaborator gets the assigned notification
    and question 11 should be assigned to the collaborator
    and the collaborator can see the 'assigned to you' in the overview page

Verify the ' Last update message'
    [Documentation]    INFUND-280
    When the collaborator marks the question 11 as complete
    Then the 'Last update' message should be updated

Verify the field is disabled for the Collaborator
    [Documentation]    INFUND-275
    [Tags]    Collaborator
    Then the field of the question 11 should be disabled

Verify the Lead applicant can assign a question back to him self
    [Documentation]    INFUND-275
    [Setup]    Switch to the first browser
    Given the Applicant opens again the application form
    when the Applicant assigns the question 11 to the collaborator    Jessica Doe
    and the field of the question 11 should be disabled
    and the lead applicant re-assign the question to him self    Steve Smith

*** Keywords ***
the Applicant is in the application details section
    Applicant goes to the Application form

When the Applicant assigns the question 11 to the collaborator
    [Arguments]    ${assignee_name}
    Clear Element Text    id=11
    Input Text    id=11    lead Applicant's text 123...
    Click Element    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-footer > div > div.assign-button > button
    Click Element    xpath=//div[@id="question-11"]//button[contains(text(),"${assignee_name}")]

the success message should show
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert    Question assigned successfully
    Capture Page Screenshot

the field of the question 11 should be disabled
    Element Should Be Disabled    id=11
    sleep    2

the Collaborator is in the Application details section
    open browser    ${SERVER}
    Input Text    id=id_email    jessica.doe@ludlow.co.uk
    Input Password    id=id_password    test
    Click Element    css=#content > div > div:nth-child(1) > form > input
    Applicant goes to the Application form

the Collaborator gets the assigned notification
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert > p    Steve Smith has assigned a question to you

and the collaborator can see the 'assigned to you' in the overview page
    Applicant goes to the Overview page
    Click Element    css=#content > form > div > h2:nth-child(1) > button
    Page Should Contain Element    css=#collapsible-1 > ul > li:nth-child(2) > div > div.column-third > div

the question 11 should not be editable
    Element Should Be Disabled    id=11
    Close Browser

the second Collaborator is logged in
    open browser    ${SERVER}
    Input Text    id=id_email    pete.tom@egg.com
    Input Password    id=id_password    test
    Click Element    css=#content > div > div:nth-child(1) > form > input

the applicant clicks the assign to Lead applicant
    Switch to the first browser    1
    Applicant goes to the Application form
    Click Element    css=#question-11 > div > div.textarea-wrapped.marked-as-complete.word-count > div.textarea-footer > button
    Click Element    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-footer > div > div.assign-button > button

the field should be re-assigned to the Applicant
    Element Should Be Enabled    id=11
    Click Element    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-footer > button

question 11 should be assigned to the collaborator
    Page Should Contain Element    css=#question-11 > div > div.textarea-wrapped.assigned-to-me.word-count
    Page Should Contain Element    css=#question-11 > div > div.textarea-wrapped.assigned-to-me.word-count > div.textarea-header > div
    Element Should Be Enabled    id=11

the second Collaborator is in the application overview page
    Applicant goes to the Application form

the collaborator marks the question 11 as complete
    Applicant goes to the Application form
    Clear Element Text    id=11
    Input Text    id=11    collaborator's text
    Click Element    css=#question-11 .buttonlink

the 'Last update' message should be updated
    Element Should Contain    css=#question-11 > div > div.textarea-wrapped.marked-as-complete.word-count > div.textarea-header > p > small    Last updated: Today by you

the Applicant is in the overview page
    Applicant goes to the Overview page

the collaborators should be visible in the overview page
    Click Element    css=#content > form > div > h2:nth-child(1) > button
    Page Should Contain Element    css=#collapsible-1 > ul > li:nth-child(3) > div > div.column-third > div > div > div.assign-button > button
    Capture Page Screenshot

the last update should show the collaborators name
    Element Should Contain    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-header > p > small    Last updated: Today by you

the lead applicant re-assign the question to him self
    [Arguments]    ${assignee_name}
    Reload Page
    Click Element    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-footer > div > div.assign-button > button
    Click Element    xpath=//div[@id="question-11"]//button[contains(text(),"${assignee_name}")]
    Element Should Be Enabled    id=11
    Close Browser

the Applicant opens again the application form
    Reload Page
    Applicant goes to the Application form
    Mark question 11 as editable
