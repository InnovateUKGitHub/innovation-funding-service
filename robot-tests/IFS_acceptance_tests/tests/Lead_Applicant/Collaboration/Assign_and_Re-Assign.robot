
*** Settings ***
Documentation     -INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               -INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
...               -INFUND-877: As a collaborator I want to be able to mark application questions that have been assigned to me as complete, so that my lead applicant is aware of my progress
Test Teardown     User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot


*** Test Cases ***
Verify the applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    Collaboration
    Given the Applicant can log in
    Given Applicant goes to the 'public description' question
    When the Applicant assigns the public description question to the collaborator    Jessica Doe
    Then the success message should show
    And the field of the public description question should be disabled
    And the question should show the assigned person's name


Verify the field is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    Given the second Collaborator is logged in
    When the second Collaborator is in the public description question
    Then the public description question should not be editable


Verify the field is enabled for the collaborator/assignee
    [Documentation]    INFUND-275
    [Tags]    Collaboration    Overview
    Given the Collaborator is in the public description section
    When the Collaborator gets the assigned notification
    Then the public description question should be assigned to the collaborator
    And the collaborator can see the 'assigned to you' in the overview page


Verify the ' Last update message'
    [Documentation]    INFUND-280
    [Tags]    Collaboration
    Given the collaborator is in the public description section
    When the collaborator edits public description question
    Then the 'Last update' message should be updated


Verify collaborator can mark as ready for review
    [Documentation]     INFUND-877
    [Tags]  Collaboration
    When the collaborator is in the public description section
    Then the collaborator can mark the question as ready for review

Verify the field is disabled for the collaborator
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    When the collaborator is in the public description section
    Then the field of the public description question should be disabled


Verify that the field has been reassigned to the lead applicant
    [Documentation]     INFUND-275
    [Tags]  Collaboration
    Given the Applicant can log in
    And Applicant goes to the 'public description' question
    When the Applicant gets the reassigned notification
    Then the public description question should be assigned to the applicant
    And the Applicant can see the 'Reassigned to: You' in the overview page

Verify that the applicant can assign a question and still mark it as complete
    [Documentation]     INFUND-877
    [Tags]      Collaboration   Pending
    # marked as pending because this functionality now seems to have been deleted! Possibly a bug?
    Given the Applicant can log in
    And Applicant goes to the 'public description' question
    When the Applicant assigns the public description question to the collaborator      Jessica Doe
    And the Applicant marks the public description question as complete
    Then the public description question shows as complete
    And the Applicant can mark the public description question as editable again



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

the question should show the assigned person's name
    Element Should Contain    css=#form-input-12 .assignee span+span    Jessica Doe

the collaborator can mark the question as ready for review
    Click Element       name=assign_question
    Wait Until Page Contains        You have reassigned this question to
    Wait Until Page Contains        Steve Smith


the Applicant gets the reassigned notification
    Wait Until Element Is Visible    css=#content > div.event-alert
    Element Should Contain    css=#content > div.event-alert > p    Jessica Doe has assigned a question to you


the public description question should be assigned to the applicant
        Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count
        Page Should Contain Element    css=#form-input-12 > div > div.textarea-wrapped.assigned-to-me.word-count > div.textarea-header > div
        Element Should Be Enabled    id=12


the Applicant can see the 'Reassigned to: You' in the overview page
        Applicant goes to the Overview page
        Element Should Contain    css=#form-input-12 .assign-container    You


the Applicant can log in
        Login as user   &{lead_applicant_credentials}

the Applicant marks the public description question as complete
    Click Button        name=mark_as_complete

the public description question shows as complete
    Wait Until Element Is Visible           mark_as_incomplete
    Page Should Contain     This question is
    Page Should Contain     marked as complete


the Applicant can mark the public description question as editable again
    Click Button    name=mark_as_incomplete
    Wait Until Element Is Visible       name=mark_as_complete