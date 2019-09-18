*** Settings ***
Documentation     INFUND-406: As an applicant, and on the application form I have validation error, I cannot mark questions or sections as complete in order to submit my application
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Test Cases ***
Mark as complete is impossible for empty questions
    [Documentation]    INFUND-406
    [Tags]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = Robot test application
    And the user clicks the button/link     link = Public description
    When the "Project Summary" question is empty
    And The user clicks the button/link     css = .button-clear[name="complete"]
    Then the user should see the element    css = .govuk-error-message
    And the user should see the element     css = .govuk-error-summary li

Error should not be visible when the text area is not empty
    [Documentation]    INFUND-406
    [Tags]
    When the "Project Summary" question is empty
    And the applicant inserts some text again in the "Project Summary" question
    Then applicant should be able to mark the question as complete
    And the applicant can click edit to make the section editable again

*** Keywords ***
the "Project Summary" question is empty
    the user enters text to a text field    css = .textarea-wrapped .editor    ${empty}
    mouse out                               css = .textarea-wrapped .editor
    wait for autosave
    Set Focus To Element                    link = Contact us
    the user reloads the page

the applicant inserts some text again in the "Project Summary" question
    The user enters text to a text field    css = .textarea-wrapped .editor    test if the applicant can mark the question as complete
    mouse out    css = .textarea-wrapped .editor
    wait for autosave

applicant should be able to mark the question as complete
    the user clicks the button/link        jQuery = button:contains("Mark")
    the user should not see the element    css = .textarea-wrapped .govuk-error-message
    the user should not see the element    css = .govuk-error-summary li

the applicant can click edit to make the section editable again
    the user clicks the button/link    jQuery = button:contains("Edit")
    the user should see the element    jQuery = button:contains("Mark")
