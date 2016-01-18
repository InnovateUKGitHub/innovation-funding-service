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
Verify the applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    Collaboration
    Given Applicant goes to the 'public description' question
    When the Applicant assigns the public description question to the collaborator    Jessica Doe
    Then the success message should show
    and the field of the public description question should be disabled
    and the question should show the assigned persons name
    [Teardown]    User closes the browser

Verify the field is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    Given the second Collaborator is logged in
    When the second Collaborator is in the public description question
    Then the public description question should not be editable
    [Teardown]    User closes the browser

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
    When the collaborator edits public description question
    Then the 'Last update' message should be updated

Verify collaborator mark as complete
    When the collaborator marks question as complete

Verify the field is disabled for the Collaborator
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    Then the field of the public description question should be disabled
    [Teardown]    User closes the browser

*** Keywords ***
the Applicant assigns the public description question to the collaborator
    [Arguments]    ${assignee_name}
    focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    lead Applicant's text 123...
    Click Element    css=#form-input-12 .assign-button button
    Click Element    xpath=//div[@id="form-input-12"]//button[contains(text(),"${assignee_name}")]

the success message should show
    Wait Until Element Is Visible    css=#content > div.event-alert
    Wait Until Page Contains    Question assigned successfully

the field of the public description question should be disabled
    Wait Until Element Is Visible    css=#form-input-12 .readonly

the Collaborator is in the public description section
    The guest user opens the browser
    Input Text    id=id_email    jessica.doe@ludlow.co.uk
    Input Password    id=id_password    test
    Click Element    css=input.button
    Applicant goes to the 'public description' question

the Collaborator gets the assigned notification
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert > p    Steve Smith has assigned a question to you

the collaborator can see the 'assigned to you' in the overview page
    Applicant goes to the Overview page
    Element Should Contain    css=#form-input-12 .assign-container    You

the public description question should not be editable
    Wait Until Element Is Visible    css=#form-input-12 .readonly

the second Collaborator is logged in
    The guest user opens the browser
    Input Text    id=id_email    pete.tom@egg.com
    Input Password    id=id_password    test
    Click Element    css=input.button

the public description question should be assigned to the collaborator
    Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count
    Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count > div.textarea-header > div
    Element Should Be Enabled    id=12

the second Collaborator is in the public description question
    Applicant goes to the 'public description' question

the collaborator edits public description question
    Applicant goes to the Application form
    Clear Element Text    css=#form-input-12 .editor
    Focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    collaborator's text
    Focus    css=.app-submit-btn
    Sleep    2s
    Reload Page

the 'Last update' message should be updated
    Element Should Contain    css=#form-input-12 .textarea-footer    Last updated: Today by you

the question should show the assigned persons name
    Element Should Contain    css=#form-input-12 .assignee span+span    Jessica Doe

the collaborator marks question as complete
    Click Element       name=assign_question
    #Wait Until Element Is Visible   css=#form-input-12 .buttonlink[name="mark_as_complete"]
    #Click Element    css=#form-input-12 .buttonlink[name="mark_as_complete"]

#the Applicant assigns the public description question to the collaborator
 #   [Arguments]    ${arg1}
