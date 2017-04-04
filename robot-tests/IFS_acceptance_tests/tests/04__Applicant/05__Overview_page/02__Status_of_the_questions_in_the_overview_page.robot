*** Settings ***
Documentation     INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section
...
...               INFUND-1072: As an Applicant I want to see the Application Overview page redesigned so that they meet the agreed style
Suite Setup       Login new application invite academic    ${test_mailbox_one}+academictest@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation
Suite Teardown    TestTeardown User closes the browser
Force Tags        Email    Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Status changes when we assign a question
    [Documentation]    INFUND-39
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Project summary
    When the Applicant edits the Project summary
    And the applicant assigns the Project Summary    Arsene Wenger
    Then the assign status should be correct for the Project Summary
    And the blue flag should not be visible

Re-assign is possible from the overview page
    [Documentation]    INFUND-39
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    When the applicant assigns the Project Summary question from the overview page    Steve Smith
    Then a blue flag should be visible for the Project Summary in overview page
    And the assign button should say Assigned to you

*** Keywords ***
the Applicant edits the Project summary
    Clear Element Text    css=#form-input-1039 .editor
    The user enters text to a text field    css=#form-input-1039 .editor    Check last updated date@#$
    Focus    css=.app-submit-btn
    wait for autosave

the assign status should be correct for the Project Summary
    the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=Academic robot test application
    the user should see the element    jQuery=li:contains("Project summary") > .assign-container button
    Element Should Contain    jQuery=li:contains("Project summary") > .assign-container button    Arsene Wenger

the applicant assigns the Project Summary question from the overview page
    [Arguments]    ${assignee_name}
    the user clicks the button/link    jQuery=li:contains("Project summary") .assign-button button
    the user clicks the button/link    jQuery=li:contains("Project summary") button:contains("${assignee_name}")
    Sleep    500ms    # otherwise it stops while Assigning..

the applicant assigns the Project Summary
    [Arguments]    ${assignee_name}
    the user clicks the button/link    css=#form-input-1039 .assign-button button
    the user clicks the button/link    jQuery=button:contains("${assignee_name}")

a blue flag should be visible for the Project Summary in overview page
    Wait Until Page Does Not Contain Without Screenshots    Assigning to Steve Smith...    10s
    The user should see the element    jQuery=li:contains("Project summary") > .assign-container

the blue flag should not be visible
    the user should not see the element    jQuery=li:contains("Project summary") > .action-required

the assign button should say Assigned to you
    Element Should Contain    jQuery=li:contains("Project summary") > .assign-container.action-required button    you
