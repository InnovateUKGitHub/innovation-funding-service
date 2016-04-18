*** Settings ***
Documentation     INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
...               INFUND-877: As a collaborator I want to be able to mark application questions that have been assigned to me as complete, so that my lead applicant is aware of my progress
...
...               INFUND-2219 As a collaborator I do not want to be able to submit an application so that only the lead applicant has authority to do so
Suite Teardown    TestTeardown User closes the browser
Test Teardown
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${invitee_name}    michael

*** Test Cases ***
Lead applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the applicant assigns the question to the collaborator    css=#form-input-12 .editor    test1233    Jessica Doe
    Then the user should see the notification    Question assigned successfully
    And the user should see the element    css=#form-input-12 .readonly
    And the question should contain the correct status/name    css=#form-input-12 .assignee span+span    Jessica Doe
    [Teardown]    User closes the browser

The question is disabled for other collaborators
    [Documentation]    INFUND-275
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then The user should see the element    css=#form-input-12 .readonly
    [Teardown]    User closes the browser

The question is disabled on the summary page for other collaborators
    [Documentation]    INFUND-2302
    [Tags]
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    Given the user navigates to the page    ${SUMMARY_URL}
    When the user clicks the button/link    jQuery=button:contains("Public description")
    Then the user should see the element    css=#form-input-12 .readonly
    And the user should not see the element    jQuery=button:contains("Ready for review")
    [Teardown]    User closes the browser

The question is enabled for the assignee
    [Documentation]    INFUND-275
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the browser notification    Steve Smith has assigned a question to you
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly
    And the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And the question should contain the correct status/name    jQuery=#section-1 .section:nth-child(3) .assign-container    You

The question is enabled on the summary page for the assignee
    [Documentation]    INFUND-2302
    [Tags]    Summary
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    Given the user navigates to the page    ${SUMMARY_URL}
    When the user clicks the button/link    jQuery=button:contains("Public description")
    And the user should see the element    jQuery=button:contains("Ready for review")

'Last update' message is correctly updating
    [Documentation]    INFUND-280
    [Tags]
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the collaborator edits the 'public description' question
    Then the question should contain the correct status/name    css=#form-input-12 .textarea-footer    Last updated: Today by you

Collaborator should see the review button instead of the review and submit
    [Documentation]    INFUND-2451
    When the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    Then the user should not see the element    jQuery=.button:contains("Review & submit")
    And the user clicks the button/link    jQuery=.button:contains("Review")
    And the user should be redirected to the correct page    ${SUMMARY_URL}
    And the user should not see the element    jQuery=.button:contains("Submit application")
    And the user should see the text in the page    All sections must be marked as complete before the application can be submitted. Only the lead applicant is able to submit the application
    [Teardown]

Collaborators cannot assign a question
    [Documentation]    INFUND-839
    [Tags]
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then The user should not see the text in the page    Assign to

Collaborators should not be able to edit application details
    [Documentation]    INFUND-2298
    When the user navigates to the page    ${APPLICATION_DETAILS_URL}
    Then the user should see the element    css=#application_details-title[readonly]
    And the user should see the element    css=#application_details-startdate_day[readonly]
    And the user should not see the element    jQuery=button:contains("Mark as complete")

Collaborators can mark as ready for review
    [Documentation]    INFUND-877
    [Tags]    HappyPath
    Given the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the user clicks the button/link    jQuery=button:contains("Ready for review")
    Then the user should see the notification    Question assigned successfully
    And the user should see the text in the page    You have reassigned this question to

Collaborator cannot edit after marking ready for review
    [Documentation]    INFUND-275
    [Tags]
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the element    css=#form-input-12 .readonly
    [Teardown]    User closes the browser

The question can be reassigned to the lead applicant
    [Documentation]    INFUND-275
    [Tags]
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then the user should see the browser notification    Jessica Doe has assigned a question to you
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly
    And the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    And the question should contain the correct status/name    jQuery=#section-1 .section:nth-child(3) .assign-container    You
    [Teardown]    User closes the browser

Appendices are assigned along with the question
    [Documentation]    INFUND-409
    [Tags]
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${INNOVATION_URL}
    And the user should see the text in the page    Upload
    When the user assigns the question to Jessica Doe
    And logout as user
    And guest user log-in    &{collaborator1_credentials}
    Then the user navigates to the page    ${INNOVATION_URL}
    And the user should see the text in the page    Upload
    And the user assigns the question to the lead applicant
    And the user should not see the text in the page    Upload

*** Keywords ***
the collaborator edits the 'public description' question
    Clear Element Text    css=#form-input-12 .editor
    Focus    css=#form-input-12 .editor
    Input Text    css=#form-input-12 .editor    collaborator's text
    Focus    css=.app-submit-btn
    Sleep    1s
    Reload Page

the question should contain the correct status/name
    [Arguments]    ${ELEMENT}    ${STATUS}
    Element Should Contain    ${ELEMENT}    ${STATUS}

the user assigns the question to Jessica Doe
    the applicant assigns the question to the collaborator    css=#form-input-6 .editor    testtest    Jessica Doe
    reload page

the user assigns the question to the lead applicant
    the user reloads the page
    the user clicks the button/link    name=assign_question
    the user reloads the page
