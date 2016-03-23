*** Settings ***
Documentation     -INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               -INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
...               -INFUND-877: As a collaborator I want to be able to mark application questions that have been assigned to me as complete, so that my lead applicant is aware of my progress
Test Teardown
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Verify the applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    Collaboration    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the applicant assigns the question to the collaborator    css=#form-input-12 .editor    test1233    Jessica Doe
    Then the user should see the notification    Question assigned successfully
    And the user should see the element    css=#form-input-12 .readonly
    And the question should contain the correct status/name    css=#form-input-12 .assignee span+span    Jessica Doe
    [Teardown]    User closes the browser


Lead applicant can see pending invitees in the assign list, but cannot assign questions to them
    [Tags]      Pending
    [Setup]     Guest user log-in       &{lead_applicant_credentials}
    When the user navigates to the page            ${SCOPE_URL}
    Then the user can see the pending invitee but can't assign to them
    [Teardown]      User closes the browser



Verify the field is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    Collaboration    HappyPath
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then The user should see the element    css=#form-input-12 .readonly
    [Teardown]    User closes the browser

Verify the field is enabled for the collaborator/assignee
    [Documentation]    INFUND-275
    [Tags]    Collaboration    Overview    HappyPath
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the browser notification    Steve Smith has assigned a question to you
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly
    And the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And the question should contain the correct status/name    css=#form-input-12 .assign-container    You

Verify the ' Last update message'
    [Documentation]    INFUND-280
    [Tags]    Collaboration
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the collaborator edits public description question
    Then the question should contain the correct status/name    css=#form-input-12 .textarea-footer    Last updated: Today by you

Verify the collaborator cannot assign the question
    [Documentation]     INFUND-839
    [Tags]      Collaboration
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then The user should not see the text in the page       Assign to


Verify collaborator can mark as ready for review
    [Documentation]    INFUND-877
    [Tags]    Collaboration    HappyPath
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the user clicks the button/link    jQuery=button:contains("Ready for review")
    Then the user should see the notification    Question assigned successfully
    And the user should see the text in the page    You have reassigned this question to

Verify the field is disabled for the collaborator
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the element    css=#form-input-12 .readonly
    [Teardown]    User closes the browser

Verify that the field has been reassigned to the lead applicant
    [Documentation]    INFUND-275
    [Tags]    Collaboration
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the browser notification    Jessica Doe has assigned a question to you
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly
    And the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And the question should contain the correct status/name    css=#form-input-12 .assign-container    You
    [Teardown]    User closes the browser


Verify that the appendices are assigned along with the question
    [Documentation]     INFUND-409
    [Tags]      Collaboration
    [Setup]     Guest user log-in   &{lead_applicant_credentials}
    Given the user navigates to the page     ${PROJECT_TEAM_URL}
    And the user can see the option to upload a file
    When the user assigns the question to Jessica Doe
    And the user can log out
    And the user can log in as Jessica Doe
    Then the user navigates to the page     ${PROJECT_TEAM_URL}
    And the user can see the option to upload a file
    And the user assigns the question to Steve Smith
    And the user can't see the option to upload a file



*** Keywords ***
the collaborator edits public description question
    Clear Element Text    css=#form-input-12 .editor
    Focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    collaborator's text
    Focus    css=.app-submit-btn
    Sleep    2s
    Reload Page

the question should contain the correct status/name
    [Arguments]    ${ELEMENT}    ${STATUS}
    Element Should Contain    ${ELEMENT}    ${STATUS}

the user can see the option to upload a file
    the user should see the text in the page       Upload

the user assigns the question to Jessica Doe
    the applicant assigns the question to the collaborator      css=#form-input-8 .editor   testtest    Jessica Doe
    reload page

the user can log out
    Logout as user

the user assigns the question to Steve Smith
    reload page
    And The user clicks the button/link         name=assign_question
    reload page

the user can log in as Jessica Doe
    guest user log-in   &{collaborator1_credentials}

the user can't see the option to upload a file
    the user should not see the text in the page        Upload


Then the user can see the pending invitee but can't assign to them
    