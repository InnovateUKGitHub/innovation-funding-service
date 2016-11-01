*** Settings ***
Documentation     INFUND-406: As an applicant, and on the application form I have validation error, I cannot mark questions or sections as complete in order to submit my application
Suite Setup       log in and create new application if there is not one already
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***
Mark as complete is impossible for empty questions
    [Documentation]    -INFUND-406
    [Tags]    HappyPath    Pending
    # Pending due to INFUND-4094
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Project summary
    When the "Project Summary" question is empty
    And The user clicks the button/link    css=#form-input-11 .buttonlink[name="mark_as_complete"]
    Then the user should see the element    css=#form-input-11 .error-message
    And the user should see the element    css=.error-summary li

Error should not be visible when the text area is not empty
    [Documentation]    -INFUND-406
    [Tags]    HappyPath    Pending
    # Pending due to INFUND-4094
    When the "Project Summary" question is empty
    And the applicant inserts some text again in the "Project Summary" question
    Then applicant should be able to mark the question as complete
    And the applicant can click edit to make the section editable again

*** Keywords ***
the "Project Summary" question is empty
    the user enters text to a text field    css=#form-input-11 .editor    ${empty}
    mouse out    css=#form-input-11 .editor
    focus    name=mark_as_complete
    sleep    2s
    capture page screenshot
    the user reloads the page

the applicant inserts some text again in the "Project Summary" question
    The user enters text to a text field    css=#form-input-11 .editor    test if the applicant can mark the question as complete
    mouse out    css=#form-input-11 .editor
    Sleep    300ms

applicant should be able to mark the question as complete
    the user clicks the button/link    jQuery=button:contains("Mark as complete")
    the user should not see the element    css=#form-input-11 .error-message
    the user should not see the element    css=.error-summary li

the applicant can click edit to make the section editable again
    the user clicks the button/link    jQuery=button:contains("Edit")
    the user should see the element    jQuery=button:contains("Mark as complete")
