*** Settings ***
Documentation     This test has been put last (with the 1.) because the other application tests depend on the application not being submitted.
...
...               -INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission.
...
...
...               -INFUND-185: As an applicant, on the application summary and pressing the submit application button, it should give me a message that I can no longer alter the application.
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Variables ***
${OVERVIEW_PAGE_APPLICATION_7}    ${SERVER}/application/7/
${SUMMARY_PAGE_APPLICATION_7}    ${SERVER}/application/7/summary
${SUBMITTED_PAGE_APPLICATION_7}    ${SERVER}/application/7/submit

*** Test Cases ***
Submit flow/ disabled submit button (incomplete application)
    [Documentation]    INFUND-195
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the applicant goes to the overview page of the application 7
    When the applicant clicks the review and submit button
    and the applicant redirects to the summary page
    and the applicant marks the first question as incomplete
    Then the submit button should be disabled

Submit flow (complete application)
    [Documentation]    INFUND-205
    ...
    ...    This test case test the submit modal(cancel option) and the the submit of the form, the confirmation page and the new status of the application
    [Tags]    Applicant    Submit    Review and Submit    Summary
    Given the applicant goes to the overview page of the application 7
    When the applicant clicks the review and submit button
    and the applicant redirects to the summary page
    And the applicant marks the first question as complete
    Then the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks Yes in the submit modal
    and the applicant redirects to the application submitted page

*** Keywords ***
the submit button should be disabled
    go to    ${SUMMARY_PAGE_APPLICATION_7}
    Element Should Be Disabled    css=.alignright-button button

Clear the Project summary field
    Wait Until Element Is Visible    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Sleep    2s

the applicant clicks Yes in the submit modal
    click element    link=Submit application
    click link    link=Yes, I want to submit my application

the applicant goes to the overview page of the application 7
    go to    ${OVERVIEW_PAGE_APPLICATION_7}

the applicant clicks the review and submit button
    click element    link=Review & submit

the applicant redirects to the summary page
    Location Should Be    ${SUMMARY_PAGE_APPLICATION_7}

the applicant marks the first question as incomplete
    Click Element    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(2)
    click element    css=#form-input-11 .button-secondary

the applicant marks the first question as complete
    click element    css=.section-overview section:nth-of-type(1) .collapsible:nth-of-type(2)
    click element    jQuery=button:contains("Mark as complete")
    sleep    1s

the applicant clicks the submit button and the clicks cancel in the submit modal
    click element    link=Submit application
    Click Element    jquery=button:contains("Cancel")

the applicant redirects to the application submitted page
    Location Should Be    ${SUBMITTED_PAGE_APPLICATION_7}
    Page Should Contain    Application submitted
