*** Settings ***
Documentation     INFUND-8092 E2E for the Assessor Journey Flow
Suite Setup       Delete the emails from both test mailboxes
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin    Assessor    HappyPath    Email
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Invite a new Assessor to assess a competition
    [Documentation]    INFUND-8092
    [Setup]    Guest user log-in    &{Comp_admin1_credentials}
    Given the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery=a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link    link=Invite
    And the user clicks the button/link    jQuery=span:contains("Add a non-registered assessor to your list")
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(1) input    E2E
    And The user enters text to a text field    css=#invite-table tr:nth-of-type(1) td:nth-of-type(2) input    ${test_mailbox_one}+AJE2E@gmail.com
    And the user selects the option from the drop-down menu    Emerging and enabling    css=.js-progressive-group-select
    And the user selects the option from the drop-down menu    Emerging technology    id=grouped-innovation-area
    And the user clicks the button/link    jQuery=.button:contains("Add assessors to list")
    When the user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Invite individual)
    And the user clicks the button/link    jQuery=.button:contains(Send invite)
    And The user should see the element    jQuery=span:contains("Add a non-registered assessor to your list")
    [Teardown]    The user closes the browser

Invited User gets an email to assess the competition
    [Documentation]    INFUND-8092
    [Tags]
    [Setup]    The guest user opens the browser
    User reads the email and clicks the link to accept the assessment    ${test_mailbox_one}+AJE2E@gmail.com    Invitation to assess 'Sustainable living models for the future'    Assessment period:
    [Teardown]    Delete the emails from both test mailboxes

Invited user accepts the invitation and follows the registration flow
    [Documentation]    INFUND-8092
    Given the user should see the text in the page    Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'
    And the user clicks the button/link    jQuery=.button:contains("Yes, create account")
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user should see the text in the page    ${test_mailbox_one}+AJE2E@gmail.com
    And The user fills and submits the registration form
    And the user clicks the button/link    jQuery=a:contains("Sign into your account")
    Then the user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}

New assessor can login with the new account
    [Documentation]    INFUND-8092
    When The user enters text to a text field    id=username    ${test_mailbox_one}+AJE2E@gmail.com
    And The user enters text to a text field    id=password    Passw0rd123
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    Then The user should see the element    link=${IN_ASSESSMENT_COMPETITION_NAME}

New assessor should have the correct innovation area
    [Documentation]    INFUND-8092
    When The user clicks the button/link    link=your skills
    And The user should see the text in the page    Emerging technology

New assessor has no assements
    [Documentation]  INFUND-9007
    When The user navigates to the page    ${assessor_dashboard_url}
    And the user should see the text in the page    There are currently no assessments for you to review.

CompAdmin should see Assessor's profile and Innovation Area
    [Documentation]    INFUND-8092
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    Given the user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery=a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link    link=Overview
    When the user clicks the button/link    link=E2E
    And the user should see the text in the page    Emerging technology

CompAdmin Invites assessor to assess an application
    [Setup]    The user clicks the button/link    link=My dashboard
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link    jQuery=a:contains("Assessor management: Assignments")
    And the user clicks the button/link    jQuery=tr:nth-child(1) a:contains(View progress)
    And the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    And the user clicks the button/link    jQuery=.pagination-label:contains(Next)
    When the user clicks the button/link    jQuery=tr:contains(Tom Fister) button:contains("Assign")
    And the user clicks the button/link    jQuery=a:contains("Allocate applications")
    And the user clicks the button/link    jQuery=a:contains("Competition")
    And the user clicks the button/link    jQuery=button:contains("Notify assessors")
    And the element should be disabled    jQuery=button:contains("Notify assessors")
    [Teardown]    The user closes the browser

New assessor has one assessment to accept
    [Documentation]  INFUND-9007
    [Setup]   Guest user log-in    ${test_mailbox_one}+AJE2E@gmail.com    Passw0rd123
    Then The user navigates to the page    ${assessor_dashboard_url}
    And the user should see the text in the page    1 applications awaiting acceptance

Assessor is notified by Email
    [Setup]    The guest user opens the browser
    Given the user reads his email and clicks the link    ${test_mailbox_one}+AJE2E@gmail.com    Your applications for the competition    You have been allocated some applications

Assessor accepts the invite for the Application
    When The user enters text to a text field    id=username    ${test_mailbox_one}+AJE2E@gmail.com
    And The user enters text to a text field    id=password    Passw0rd123
    And the user clicks the button/link    css=button[name="_eventId_proceed"]
    When The user clicks the button/link    Link=Park living
    And The user clicks the button/link    jQuery=button:contains("Accept")
    Then the user should be redirected to the correct page    ${Assessor_application_dashboard}

New assessor has one assessment
    [Documentation]  INFUND-9007
    When The user navigates to the page    ${assessor_dashboard_url}
    And the user should see the text in the page    1 applications to assess

*** Keywords ***
User reads the email and clicks the link to accept the assessment
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}==1    open email locally assessor    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    open email remotely assessor    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_one}
    ...    ${test_mailbox_one_password}

open email locally assessor
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=${local_imap}    port=${local_imap_port}   user=smtp    password=smtp     is_secure=False
    ${email_to_test}=    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    click the link assessor    ${email_to_test}    ${pattern}

open email remotely assessor
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${mailbox}    ${mailbox_password}
    Open Mailbox    server=imap.googlemail.com    user=${mailbox}@gmail.com    password=${mailbox_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    click the link assessor    ${email_to_test}    ${pattern}

click the link assessor
    [Arguments]    ${email_to_test}    ${pattern}
    [Documentation]    This keyword reads the email and uses regex to find the invite link
    ${HTML}=    get email body    ${email_to_test}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${email_to_test}    ${pattern}
    Should Not Be Empty    ${MATCHES}
    ${ALL_LINKS}=    Get Regexp Matches    ${HTML}    (https:\/\/.*\/assessment\/invite\/competition\/[A-Za-z0-9-]*)
    ${LINK}=    Get From List    ${ALL_LINKS}    1
    Log    ${LINK}
    go to    ${LINK}
    delete email    ${email_to_test}
    close mailbox


The user fills and submits the registration form
    When The user enters text to a text field    id=firstName    Tom
    And The user enters text to a text field    id=lastName    Fister
    And the user enters text to a text field    id=phoneNumber    1234567891011
    And The user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    And the user should see the element    id=addressForm.selectedPostcodeIndex
    And the user clicks the button/link    css=#select-address-block button
    And The user enters text to a text field    id=password    Passw0rd123
    And The user enters text to a text field    id=retypedPassword    Passw0rd123
    And the user clicks the button/link    jQuery=button:contains("Continue")
