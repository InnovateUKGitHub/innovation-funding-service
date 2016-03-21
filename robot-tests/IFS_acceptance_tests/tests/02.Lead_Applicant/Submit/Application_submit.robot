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
...
...               -INFUND-1137 As an applicant I want to be shown confirmation information when I submit my application submission so I can confirm this has been sent and I can be given guidance for the next stages
Suite Setup       Guest user log-in    &{worth_test_credentials}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant    Submit
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Variables ***
${OVERVIEW_PAGE_APPLICATION_7}             ${SERVER}/application/7/
${SUMMARY_PAGE_APPLICATION_7}              ${SERVER}/application/7/summary
${SUBMITTED_PAGE_APPLICATION_7}            ${SERVER}/application/7/submit
${FINANCE_SECTION_7}                       ${SERVER}/application/7/form/section/7
${PROJECT_SUMMARY_APPLICATION_7}           ${SERVER}/application/7/form/question/11
${PUBLIC_DESCRIPTION_APPLICATION_7}        ${SERVER}/application/7/form/question/12
${BUSINESS_OPPORTUNITY_APPLICATION_7}      ${SERVER}/application/7/form/question/1
${POTENTIAL_MARKET_APPLICATION_7}          ${SERVER}/application/7/form/question/2




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
    And the applicant marks the first question as complete

Submit button disabled when finance section is incomplete
    [Documentation]    INFUND-927
    [Tags]    Summary       Pending
    Given the user navigates to the page    ${FINANCE_SECTION_7}
    # When the applicant marks the finance section as incomplete
    And the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    And the user clicks the button/link    link=Review & submit
    And the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the user should see the element    css=.alignright-button button
    [Teardown]    The applicant marks the first finance section as complete

Submit flow (complete application)
    [Documentation]    INFUND-205
    ...
    ...    This test case test the submit modal(cancel option) and the the submit of the form, the confirmation page and the new status of the application
    [Tags]    Summary    HappyPath      Pending
    # Note that this step is pending since we need the updated finances secitons marked as complete for each partner in the webtest database
    # TODO EC - Get in touch with Nico to arrange this
    Given the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    When the user clicks the button/link    link=Review & submit
    And the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page    ${SUBMITTED_PAGE_APPLICATION_7}
    And the user should see the text in the page    Application submitted

Status of the submitted application
    [Documentation]    INFUND-1137
    [Tags]      Pending
    # Note that this step is pending since we need the updated finances sections marked as complete for each partner in the webtest database
    # TODO EC - Get in touch with Nico to arrange this
    When the user navigates to the page    ${DASHBOARD_URL}
    Then the status of the "Marking it as complete" application should be submitted
    And the user clicks the button/link    Link=Marking it as complete
    And the user should see the element    Link=View application
    And the user should see the element    Link=Print Application
    When the user clicks the button/link    Link=Print Application
    Then the user should be redirected to the correct page    ${SERVER}/application/7/print


Submitted application is read only
    [Documentation]         INFUND-1938
    [Tags]          Pending
    # Note that this step is pending since we need the updated finances sections marked as complete for each partner in the webtest database
    Given the user navigates to the page            ${DASHBOARD_URL}
    And the user clicks the button          Link=Marking it as complete
    When the user clicks the button      Link=View Application
    And the user is on the page         summary
    Then the user can check that the sections are read only



*** Keywords ***
the applicant clicks Yes in the submit modal
    click element    link=Submit application
    click link    link=Yes, I want to submit my application

the applicant marks the first question as incomplete
    The user navigates to the page      ${PROJECT_SUMMARY_APPLICATION_7}
    The user clicks the button/link     name=mark_as_incomplete


the applicant marks the first question as complete
    The user navigates to the page      ${PROJECT_SUMMARY_APPLICATION_7}
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    focus    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    Inputting text...
    The user clicks the button/link     name=mark_as_complete


the applicant clicks the submit button and the clicks cancel in the submit modal
    click element    link=Submit application
    Click Element    jquery=button:contains("Cancel")

the applicant marks the finance section as incomplete
     The user navigates to the page     ${FINANCE_SECTION_7}
     The user clicks the button/link    name=mark_section_as_incomplete


The applicant marks the first finance section as complete
     The user navigates to the page     ${FINANCE_SECTION_7}
     The user clicks the button/link    name=mark_section_as_complete

Then the status of the "Marking it as complete" application should be submitted
    Element Should Contain    css=li:nth-child(4)    Application submitted



The user can check that the sections are read only
    The user navigates to the page      ${PUBLIC_DESCRIPTION_APPLICATION_7}
    Wait Until Element Is Visible        css=#form-input-12 .readonly
    The user navigates to the page      ${BUSINESS_OPPORTUNITY_APPLICATION_7}
    Wait Until Element Is Visible        css=#form-input-1 .readonly
    The user navigates to the page      ${POTENTIAL_MARKET_APPLICATION_7}
    Wait Until Element Is Visible        css=#form-input-12 .readonly