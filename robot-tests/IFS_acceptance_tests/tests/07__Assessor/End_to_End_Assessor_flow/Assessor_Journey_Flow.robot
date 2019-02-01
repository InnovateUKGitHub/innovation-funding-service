*** Settings ***
Documentation     INFUND-8092 E2E for the Assessor Journey Flow
...
...               IFS-39 As a member of the competitions team I can resend a competition invite to an assessor so that assessor has a new invite
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../Assessor_Commons.robot

*** Test Cases ***
Invite a new Assessor to assess a competition
    [Documentation]    INFUND-8092
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser      &{Comp_admin1_credentials}
    Given the user clicks the button/link         link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link           jQuery = a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link           link = Invite
    And The internal user invites a user as an assessor    EtoE  ${Assessor_e2e["email"]}
    When the user clicks the button/link          jQuery = a:contains("Review and send invites")
    And the user enters text to a text field      id = message    This is custom text
    And the user clicks the button/link           jQuery = .govuk-button:contains("Send invite")
    And The user should see the element           jQuery = h2:contains("View assessors who have not yet responded or have declined the invite.")

Invited User gets an email to assess the competition
    [Documentation]    INFUND-8092
    [Tags]  HappyPath
    Then the user reads his email and clicks the link  ${Assessor_e2e["email"]}  Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'  This is custom text  1
    [Teardown]  Delete the emails from both test mailboxes

Resend the invite to the assessor again
    [Documentation]    IFS-39
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser  &{Comp_admin1_credentials}
    Given the user clicks the button/link     link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link       jQuery = a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link       link = Pending and declined
    And the user clicks the button/link       jQuery = tr:contains("EtoE") label
    When the user clicks the button/link      jQuery = button:contains("Resend invites")
    And the user clicks the button/link       jQuery = .govuk-button:contains("Send invite")
    [Teardown]  The user closes the browser

Resent email can be read by the invited user
     [Documentation]    IFS-39
     [Tags]  HappyPath
     [Setup]    The guest user opens the browser
     Then the user reads his email and clicks the link    ${test_mailbox_one}+AJE2E@gmail.com    Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'    Assessment period:  1

Invited user accepts the invitation and follows the registration flow
    [Documentation]    INFUND-8092
    [Tags]  HappyPath
    Given the user should see the element             jQuery = h1:contains("Invitation to assess '${IN_ASSESSMENT_COMPETITION_NAME}'")
    And the user selects the radio button             acceptInvitation  true
    And The user clicks the button/link               jQuery = button:contains("Confirm")
    When the user clicks the button/link              jQuery = .govuk-button:contains("Create account")
    And the user should see the element               jQuery = p strong:contains("${Assessor_e2e["email"]}")
    And The user fills and submits the registration form
    And the user clicks the button/link                     jQuery = a:contains("Sign into your account")
    Then the user should be redirected to the correct page  ${LOGGED_OUT_URL_FRAGMENT}

New assessor can login with the new account
    [Documentation]    INFUND-8092
    [Tags]  HappyPath
    Given Invited guest user log in       &{Assessor_e2e}
    Then The user should see the element  link = ${IN_ASSESSMENT_COMPETITION_NAME}

New assessor should have the correct innovation area
    [Documentation]    INFUND-8092
    When The user clicks the button/link          link = your skills
    And The user should see the element           jQuery = ul li:contains("Emerging technology")

New assessor has no assements
    [Documentation]  INFUND-9007
    [Tags]  HappyPath
    When The user navigates to the page           ${ASSESSOR_DASHBOARD_URL}
    And the user should see the element           jQuery = h3:contains("${IN_ASSESSMENT_COMPETITION_NAME}") ~ div:contains("There are currently no assessments for you to review.")

CompAdmin should see Assessor's profile and Innovation Area
    [Documentation]    INFUND-8092
    [Tags]  HappyPath
    [Setup]    Log in as a different user  &{Comp_admin1_credentials}
    Given the user clicks the button/link  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user clicks the button/link    jQuery = a:contains("Invite assessors to assess the competition")
    And the user clicks the button/link    link = Accepted
    When the user clicks the button/link   link = EtoE
    And the user should see the element    jQuery = h3:contains("Innovation areas") ~ .govuk-table th:contains("Emerging and enabling")

CompAdmin Invites assessor to assess an application
    [Tags]  HappyPath
    [Setup]    The user clicks the button/link  link = Dashboard
    Given The user clicks the button/link       link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link         jQuery = a:contains("Manage assessments")
    And the user clicks the button/link         jQuery = a:contains("Manage applications")
    And the user clicks the button/link         jQuery = tr:nth-child(1) a:contains("View progress")
    And the user clicks the button/link         jQuery = .pagination-part-title:contains("21")
    And the user clicks the button/link         jQuery = .pagination-part-title:contains("41")
    When the user clicks the button/link        jQuery = tr:contains("Tom Fister") button:contains("Assign")
    And the user clicks the button/link         jQuery = a:contains("Allocate applications")
    And the user clicks the button/link         jQuery = a:contains("Manage assessments")
    And the user clicks the button/link         jQuery = a:contains("Competition")
    And the user clicks the button/link         jQuery = button:contains("Notify assessors")
    And the element should be disabled          jQuery = button:contains("Notify assessors")

New assessor has one assessment to accept
    [Documentation]  INFUND-9007
    [Tags]  HappyPath
    [Setup]   Log in as a different user          &{Assessor_e2e}
    Then The user navigates to the page           ${ASSESSOR_DASHBOARD_URL}
    And the user should see the element           jQuery = .action-required:contains("1 applications awaiting acceptance")

Assessor is notified by Email
    [Tags]  HappyPath
    [Setup]    The guest user opens the browser
    Given the user reads his email and clicks the link  ${Assessor_e2e["email"]}    Your applications for the competition    You have been allocated some applications

Assessor accepts the invite for the Application
    [Tags]  HappyPath
    Given Invited guest user log in                         &{Assessor_e2e}
    When The user clicks the button/link                    Link = Park living
    And the user selects the radio button                   assessmentAccept  true
    And The user clicks the button/link                     jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page  ${Assessor_application_dashboard}

New assessor has one assessment
    [Documentation]  INFUND-9007
    When The user navigates to the page    ${ASSESSOR_DASHBOARD_URL}
    And the user should see the element    jQuery = .action-required:contains("1 applications to assess")

*** Keywords ***
User reads the email and clicks the link to accept the assessment
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker} == 1    open email locally assessor    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker} != 1    open email remotely assessor    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_one}
    ...    ${test_mailbox_one_password}

open email locally assessor
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server = ${local_imap}    port = ${local_imap_port}   user = smtp    password = smtp     is_secure = False
    ${email_to_test} =     wait for email    sender = ${sender}    recipient = ${recipient}    subject = ${subject}    timeout = 90
    log    ${subject}
    click the link assessor    ${email_to_test}    ${pattern}

open email remotely assessor
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${mailbox}    ${mailbox_password}
    Open Mailbox    server = imap.googlemail.com    user = ${mailbox}@gmail.com    password = ${mailbox_password}
    ${email_to_test} =    wait for email    sender = ${sender}    recipient = ${recipient}    subject = ${subject}    timeout = 200
    log    ${subject}
    click the link assessor    ${email_to_test}    ${pattern}

click the link assessor
    [Arguments]    ${email_to_test}    ${pattern}
    [Documentation]    This keyword reads the email and uses regex to find the invite link
    ${HTML} =     get email body    ${email_to_test}
    log    ${HTML}
    ${MATCHES} =     Get Matches From Email    ${email_to_test}    ${pattern}
    Should Not Be Empty    ${MATCHES}
    ${ALL_LINKS} =     Get Regexp Matches    ${HTML}    (https:\/\/.*\/assessment\/invite\/competition\/[A-Za-z0-9-]*)
    ${LINK} =     Get From List    ${ALL_LINKS}    1
    Log    ${LINK}
    go to    ${LINK}
    delete email    ${email_to_test}
    close mailbox


The user fills and submits the registration form
    the user enters text to a text field   id = firstName    Tom
    the user enters text to a text field   id = lastName    Fister
    the user enters text to a text field   id = phoneNumber    1234567891011
    the user enters text to a text field   id = addressForm.postcodeInput    BS14NT
    the user clicks the button/link        id = postcode-lookup
    the user selects the index from the drop-down menu  1  id=addressForm.selectedPostcodeIndex
    the user should see the element        id = addressForm.selectedPostcodeIndex
    the user enters text to a text field   id = password    ${correct_password}
    the user clicks the button/link        jQuery = button:contains("Continue")
