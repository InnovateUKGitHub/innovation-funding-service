*** Settings ***
Documentation     This test has been put last (with the 1.) because the other application tests depend on the application not being submitted.
...
...               -INFUND-172: As a lead applicant and I am on the application summary, I can submit the application, so I can verify it that it is ready for submission.
...
...
...               -INFUND-185: As an applicant, on the application summary and pressing the submit application button, it should give me a message that I can no longer alter the application.
...
...
...               -INFUND-927 As a lead partner i want the system to show me when all questions and sections (partner finances) are complete on the finance summary, so that i know i can submit the application
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant    Submit
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Variables ***
${OVERVIEW_PAGE_APPLICATION_7}    ${SERVER}/application/7/
${SUMMARY_PAGE_APPLICATION_7}    ${SERVER}/application/7/summary
${SUBMITTED_PAGE_APPLICATION_7}    ${SERVER}/application/7/submit
${FINANCE_SECTION_7}    ${SERVER}/application/7/form/section/7

*** Test Cases ***
Submit button disabled when the application is incomplete
    [Documentation]    INFUND-195
    [Tags]    Summary
    Given the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    When the user clicks the button/link    link=Review & submit
    Then the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    and the applicant marks the first question as incomplete
    Then the user navigates to the page    ${SUMMARY_PAGE_APPLICATION_7}
    And the user should see the element    css=.alignright-button button
    [Teardown]    The applicant marks the first question as complete

Submit button disabled when finance section is incomplete
    [Documentation]    INFUND-927
    [Tags]    Summary
    Given the user navigates to the page    ${FINANCE_SECTION_7}
    When the applicant marks the finance section as incomplete
    And the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    And the user clicks the button/link    link=Review & submit
    And the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the user should see the element    css=.alignright-button button
    [Teardown]    The applicant marks the first finance section as complete

Submit flow (complete application)
    [Documentation]    INFUND-205
    ...
    ...    This test case test the submit modal(cancel option) and the the submit of the form, the confirmation page and the new status of the application
    [Tags]    Summary    HappyPath
    Given the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    When the user clicks the button/link    link=Review & submit
    And the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page    ${SUBMITTED_PAGE_APPLICATION_7}
    And the user should see the text in the page    Application submitted

*** Keywords ***
the applicant clicks Yes in the submit modal
    click element    link=Submit application
    click link    link=Yes, I want to submit my application

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

the applicant marks the finance section as incomplete
    Click Element    css=[aria-controls="collapsible-1"]
    click element    jQuery=#collapsible-1 button:contains("Edit")

The applicant marks the first finance section as complete
    go to    ${FINANCE_SECTION_7}
    Click Element    css=[aria-controls="collapsible-1"]
    click element    jQuery=#collapsible-1 button:contains("Mark as complete")
