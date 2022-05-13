*** Settings ***
Documentation     INFUND-406: As an applicant, and on the application form I have validation error, I cannot mark questions or sections as complete in order to submit my application
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Test Cases ***
Mark as complete is impossible for empty questions
    [Documentation]    INFUND-406
    [Tags]
    Given the user navigates to the page    ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link     link = Robot test application - validation
    And the user clicks the button/link     link = Public description
    When the "Project Summary" question is empty
    And The user clicks the button/link     css = .button-clear[name="complete"]
    Then the user should see the element    css = .govuk-error-message
    And the user should see the element     css = .govuk-error-summary li
    And the error should not be visible when the text area is not empty

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    Connect to database  @{database}
    the user logs-in in new browser   &{lead_applicant_credentials}
    Create a new application          Robot test application - validation   1

Custom suite teardown
    The user closes the browser
    Disconnect from database

the error should not be visible when the text area is not empty
    the applicant inserts some text again in the "Project Summary" question
    the applicant should be able to mark the question as complete
    the applicant can click edit to make the section editable again

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

the applicant should be able to mark the question as complete
    the user clicks the button/link        jQuery = button:contains("Mark")
    the user should not see the element    css = .textarea-wrapped .govuk-error-message
    the user should not see the element    css = .govuk-error-summary li

the applicant can click edit to make the section editable again
    the user clicks the button/link    jQuery = button:contains("Edit")
    the user should see the element    jQuery = button:contains("Mark")

Create a new application
    [Arguments]  ${Application_title}   ${orgType}
    the user select the competition and starts application      ${openCompetitionBusinessRTO_name}
    check if there is an existing application in progress for this competition
    the user clicks the button/link                             link=Apply with a different organisation
    the user selects the radio button                           organisationTypeId  ${orgType}
    the user clicks the button/link                             jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    ITV  ITV PLC
    the user clicks the button/link                             link=Application details
    wait until keyword succeeds without screenshots             10 s  200 ms  Input Text  css=[id="name"]  ${Application_title}
    the user clicks the button/link                             jQuery=button:contains("Save and return")