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
...
...
...               INFUND-1887 As the Service Delivery Manager, I want to send an email notification to an applicant once they have successfully submitted a completed application so they have confidence their application has been received by Innovate UK
...
...
...               INFUND-1786 As a lead applicant I would like view the submitting an application terms and conditions page so that I know what I am agreeing to
Suite Setup       Guest user log-in    email=worth.email.test+submit@gmail.com    password=Passw0rd
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
${PROJECT_SUMMARY_APPLICATION_7}    ${SERVER}/application/7/form/question/11
${PUBLIC_DESCRIPTION_APPLICATION_7}    ${SERVER}/application/7/form/question/12
${BUSINESS_OPPORTUNITY_APPLICATION_7}    ${SERVER}/application/7/form/question/1
${POTENTIAL_MARKET_APPLICATION_7}    ${SERVER}/application/7/form/question/2

*** Test Cases ***
Submit button disabled when the application is incomplete
    [Documentation]    INFUND-195
    [Tags]    Summary
    When the user navigates to the page    ${SUMMARY_PAGE_APPLICATION_7}
    And the applicant marks the first question as incomplete
    Then the user navigates to the page    ${SUMMARY_PAGE_APPLICATION_7}
    And the submit button should be disabled
    [Teardown]    And the applicant marks the first question as complete

Submit button disabled when finance section is incomplete
    [Documentation]    INFUND-927
    [Tags]    Summary
    Given the user navigates to the page    ${FINANCE_SECTION_7}
    When the user clicks the button/link    jQuery=button:contains("Edit")
    And the user navigates to the page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the submit button should be disabled
    [Teardown]    The user marks the finances as complete

Submit flow (complete application)
    [Documentation]    INFUND-205
    ...
    ...    INFUND-1887
    [Tags]    Summary    HappyPath
    Given the user navigates to the page    ${OVERVIEW_PAGE_APPLICATION_7}
    When the user clicks the button/link    link=Review & submit
    And the user should be redirected to the correct page    ${SUMMARY_PAGE_APPLICATION_7}
    Then the applicant accepts the terms and conditions
    And the applicant clicks the submit button and the clicks cancel in the submit modal
    And the applicant clicks Yes in the submit modal
    Then the user should be redirected to the correct page    ${SUBMITTED_PAGE_APPLICATION_7}
    And the user should see the text in the page    Application submitted

The applicant should get a confirmation email
    [Documentation]    INFUND-1887
    [Tags]    Email    HappyPath        Pending
    # Pending due to INFUND-2492
    Then the user should get a confirmation email

Submitted application is read only
    [Documentation]    INFUND-1938
    [Tags]
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    Link=Marking it as complete
    When the user clicks the button/link    Link=View application
    And the user is on the page    summary
    Then the user can check that the sections are read only

Status of the submitted application
    [Documentation]    INFUND-1137
    [Tags]
    When the user navigates to the page    ${DASHBOARD_URL}
    Then the user should see the text in the page    Application submitted
    And the user clicks the button/link    Link=Marking it as complete
    And the user should see the element    Link=View application
    And the user should see the element    Link=Print Application
    When the user clicks the button/link    Link=Print Application
    Then the user should be redirected to the correct page    ${SERVER}/application/7/print

*** Keywords ***
the applicant clicks Yes in the submit modal
    click element    jQuery=.button:contains("Submit application")
    click element    jQuery=input[value*="Yes, I want to submit my application"]

the applicant marks the first question as incomplete
    The user navigates to the page    ${PROJECT_SUMMARY_APPLICATION_7}
    The user clicks the button/link    name=mark_as_incomplete

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Submit application")
    click element    jQuery=.button:contains("Submit application")
    Click Element    jquery=button:contains("Cancel")

The user can check that the sections are read only
    The user navigates to the page    ${PUBLIC_DESCRIPTION_APPLICATION_7}
    Wait Until Element Is Visible    css=#form-input-12 .readonly
    Element Should Not Be Visible    jQuery=button:contains("Edit")
    The user navigates to the page    ${BUSINESS_OPPORTUNITY_APPLICATION_7}
    Wait Until Element Is Visible    css=#form-input-1 .readonly
    The user navigates to the page    ${POTENTIAL_MARKET_APPLICATION_7}
    Wait Until Element Is Visible    css=#form-input-2 .readonly

the user should get a confirmation email
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${LATEST} =    wait for email    fromEmail=noresponse@innovateuk.gov.uk
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${MATCHES1}=    Get Matches From Email    ${LATEST}    Congratulations, you have successfully submitted an application for funding to Innovate
    log    ${MATCHES1}
    Should Not Be Empty    ${MATCHES1}
    Delete All Emails
    close mailbox

the submit button should be disabled
    select Checkbox    id=agree-terms-page
    Element Should Be Disabled    jQuery=button:contains("Submit application")

the applicant accepts the terms and conditions
    Select Checkbox    id=agree-terms-page

The user marks the finances as complete
    Given the user navigates to the page    ${FINANCE_SECTION_7}
    When the user clicks the button/link    jQuery=button:contains("Mark all as complete")

the applicant marks the first question as complete
    The user navigates to the page    ${PROJECT_SUMMARY_APPLICATION_7}
    focus    css=#form-input-11 .editor
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    focus    css=#form-input-11 .editor
    Input Text    css=#form-input-11 .editor    Inputting text...
    The user clicks the button/link    jQuery=button:contains("Mark as complete")
