*** Settings ***
Documentation     -INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               -INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Test Cases ***
Verify that the assign button is visible in the overview page
    [Tags]    Collaboration    Overview
    When the Applicant is in the overview page
    Then the assign button should be visible in the overview page

Verify the applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    Collaboration
    Given Applicant goes to the 'public description' question
    When the Applicant assigns the public description question to the collaborator    Jessica Doe
    Then the success message should show
    and the field of the public description question should be disabled
    and the question should show the assigned persons name

Verify the field is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    Given the second Collaborator is logged in
    When the second Collaborator is in the public description question
    Then the public description question should not be editable

Verify the field is enabled for the collaborator/assignee
    [Documentation]    INFUND-275
    [Tags]    Collaboration    Overview
    When the Collaborator is in the public description section
    Then the Collaborator gets the assigned notification
    and the public description question should be assigned to the collaborator
    and the collaborator can see the 'assigned to you' in the overview page

Verify the ' Last update message'
    [Documentation]    INFUND-280
    [Tags]    Collaboration
    When the collaborator marks the public description question as complete
    Then the 'Last update' message should be updated

Verify the field is disabled for the Collaborator
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    Then the field of the public description question should be disabled

Verify the Lead applicant can assign a question back to him self
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    [Setup]    Switch to the first browser
    Given the Applicant opens again the application form
    When the Applicant assigns the public description question to the collaborator    Jessica Doe
    and the field of the public description question should be disabled
    and the lead applicant re-assign the question to him self    Steve Smith

*** Keywords ***


When the Applicant assigns the public description question to the collaborator
    [Arguments]    ${assignee_name}
    Clear Element Text    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    lead Applicant's text 123...
    Click Element    css=#form-input-12 .assign-button button
    Click Element    xpath=//div[@id="form-input-12"]//button[contains(text(),"${assignee_name}")]

the success message should show
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert    Question assigned successfully

the field of the public description question should be disabled
    sleep    2
    Element Should Be Disabled    id=12

the Collaborator is in the public description section
    The guest user opens the browser
    Input Text    id=id_email    jessica.doe@ludlow.co.uk
    Input Password    id=id_password    test
    Click Element    css=input.button
    Applicant goes to the 'public description' question


the Collaborator gets the assigned notification
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert > p    Steve Smith has assigned a question to you

and the collaborator can see the 'assigned to you' in the overview page
    Applicant goes to the Overview page
    Element Should Contain    css=#form-input-12 .assign-container    You

the public description question should not be editable
    Element Should Be Disabled    id=12

the second Collaborator is logged in
    The guest user opens the browser
    Input Text    id=id_email    pete.tom@egg.com
    Input Password    id=id_password    test
    Click Element    css=input.button

the applicant clicks the assign to Lead applicant
    Switch to the first browser
    Applicant goes to the Application form
    Click Element    css=#form-input-12 > div > div.textarea-wrapped.marked-as-complete.word-count > div.textarea-footer > button
    Click Element    css=#form-input-12 > div > div.textarea-wrapped.word-count > div.textarea-footer > div > div.assign-button > button

the field should be re-assigned to the Applicant
    Element Should Be Enabled    css=#form-input-12 .editor
    Click Element    css=#form-input-12 > div > div.textarea-wrapped.word-count > div.textarea-footer > button

the public description question should be assigned to the collaborator
    Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count
    Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count > div.textarea-header > div
    Element Should Be Enabled    id=12

the second Collaborator is in the public description question
    Applicant goes to the 'public description' question

the collaborator marks the public description question as complete
    Applicant goes to the Application form
    Clear Element Text    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    collaborator's text
    Click Element    css=#form-input-12 .buttonlink

the 'Last update' message should be updated
    Element Should Contain    css=#form-input-12 > div > div.textarea-wrapped.marked-as-complete.word-count > div.textarea-header > p > small    Last updated: Today by you

the Applicant is in the overview page
    Applicant goes to the Overview page

the assign button should be visible in the overview page
    Page Should Contain Element    css=#section-1 .list-overview > li:nth-child(3) div.assign-button button

the question should show the assigned persons name
    Element Should Contain    css=#form-input-12 .assignee span+span   Jessica Doe

the lead applicant re-assign the question to him self
    [Arguments]    ${assignee_name}
    Reload Page
    Click Element    css=#form-input-12 > div > div.textarea-wrapped.word-count > div.textarea-footer > div > div.assign-button > button
    Click Element    xpath=//div[@id="form-input-12"]//button[contains(text(),"${assignee_name}")]
    Element Should Be Enabled    id=12

the Applicant opens again the application form
    Reload Page
    Applicant goes to the Application form
    Mark question 12 as editable
