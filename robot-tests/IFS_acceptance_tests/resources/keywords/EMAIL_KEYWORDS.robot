*** Settings ***
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/EMAIL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot

*** Keywords ***
The user verifies their email
    [Arguments]    ${verify_link}
    Go To    ${verify_link}
    Page Should Contain    Account verified

the user opens the mailbox and verifies the email from
    [Arguments]    ${receiver}
    run keyword if    ${docker}==1    the user opens the mailbox and verifies the local email from    ${receiver}
    run keyword if    ${docker}!=1    the user opens the mailbox and verifies the remote email from    ${receiver}

the user opens the mailbox and verifies the remote email from
    [Arguments]    ${receiver}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=Please verify your email address
    ${EMAIL_MATCH} =    Get Matches From Email    ${WHICH_EMAIL}    sign into your account and start your application
    log    ${EMAIL_MATCH}
    Should Not Be Empty    ${EMAIL_MATCH}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${WHICH EMAIL}
    log    ${LINK}
    ${VERIFY_EMAIL} =    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the mailbox and verifies the local email from
    [Arguments]    ${receiver}
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=Please verify your email address
    ${EMAIL_MATCH} =    Get Matches From Email    ${WHICH_EMAIL}    sign into your account and start your application
    log    ${EMAIL_MATCH}
    Should Not Be Empty    ${EMAIL_MATCH}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${WHICH EMAIL}
    log    ${LINK}
    ${VERIFY_EMAIL} =    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the mailbox and verifies the email
    run keyword if    ${docker}==1    the user opens the local mailbox and verifies the email
    run keyword if    ${docker}!=1    the user opens the remote mailbox and verifies the email

the user opens the remote mailbox and verifies the email
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the local mailbox and verifies the email
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the mailbox and reads his own email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}==1    the user opens the local mailbox and reads his own email    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user opens the remote mailbox and reads his own email    ${recipient}    ${subject}    ${pattern}

the user opens the local mailbox and reads his own email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH EMAIL}=  wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${WHICH EMAIL}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    ${ALLLINKS}=    Get Links From Email    ${WHICH EMAIL}
    log    ${ALLLINKS}
    ${LINK}=    Get From List    ${ALLLINKS}    1
    log    ${LINK}
    go to    ${LINK}
    Capture Page Screenshot
    delete email    ${WHICH EMAIL}
    close mailbox

the user opens the remote mailbox and reads his own email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${WHICH EMAIL} =  wait for email  sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${WHICH EMAIL}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    ${ALLLINKS}=    Get Links From Email    ${WHICH EMAIL}
    log    ${ALLLINKS}
    ${LINK}=    Get From List    ${ALLLINKS}    1
    log    ${LINK}
    go to    ${LINK}
    Capture Page Screenshot
    delete email    ${WHICH EMAIL}
    close mailbox

the user opens the mailbox and accepts the invitation to collaborate
    run keyword if    ${docker}==1    the user opens the local mailbox and accepts the invitation to collaborate
    run keyword if    ${docker}!=1    the user opens the remote mailbox and accepts the invitation to collaborate

the user opens the remote mailbox and accepts the invitation to collaborate
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${IFS_LINK}=    Get From List    ${LINK}    1
    log    ${IFS_LINK}
    go to    ${IFS_LINK}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the local mailbox and accepts the invitation to collaborate
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${LATEST} =    wait for email
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${IFS_LINK}=    Get From List    ${LINK}    1
    log    ${IFS_LINK}
    go to    ${IFS_LINK}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

Open mailbox and confirm received email
    [Arguments]    ${receiver}    ${PASSWORD}    ${PATTERN}    ${subject}
    run keyword if    ${docker}==1    open local mailbox and confirm received email    ${receiver}    ${PATTERN}    ${subject}
    run keyword if    ${docker}!=1    open remote mailbox and confirm received email    ${receiver}    ${PASSWORD}    ${PATTERN}    ${subject}

open remote mailbox and confirm received email
    [Arguments]    ${receiver}    ${PASSWORD}    ${PATTERN}    ${subject}
    [Documentation]    This Keyword searches the correct email using regex
    Open Mailbox    server=imap.googlemail.com    user=${receiver}    password=${PASSWORD}
    ${WHICH_EMAIL}=    wait for email    subject=${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${EMAIL_MATCH}=    Get Matches From Email    ${WHICH_EMAIL}    ${PATTERN}
    log    ${EMAIL_MATCH}
    Should Not Be Empty    ${EMAIL_MATCH}
    close mailbox

open local mailbox and confirm received email
    [Arguments]    ${receiver}    ${PATTERN}    ${subject}
    [Documentation]    This Keyword searches the correct email using regex
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH_EMAIL}=    wait for email    subject=${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${EMAIL_MATCH} =    Get Matches From Email    ${WHICH_EMAIL}    ${PATTERN}
    log    ${EMAIL_MATCH}
    Should Not Be Empty    ${EMAIL_MATCH}
    close mailbox

Delete the emails from both test mailboxes
    run keyword if    ${docker}==1    delete the emails from the local test mailbox    # Note that all emails come through to the same local mailbox, so we only need to delete from one mailbox here
    run keyword if    ${docker}!=1    delete the emails from both remote test mailboxes

delete the emails from both remote test mailboxes
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    Delete All Emails
    close mailbox
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_two}@gmail.com    password=${test_mailbox_two_password}
    Delete All Emails
    close mailbox

Delete the emails from the main test mailbox
    run keyword if    ${docker}==1    delete the emails from the local test mailbox
    run keyword if    ${docker}!=1    delete the emails from the main remote test mailbox

delete the emails from the main remote test mailbox
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    Delete All Emails
    close mailbox

delete the emails from the local test mailbox
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    Delete All Emails
    close mailbox

Delete the emails from both main test mailboxes
    run keyword if    ${docker}==1    delete the emails from the local test mailbox    # Note that all emails come through to the same local mailbox, so we only need to delete from one mailbox here
    run keyword if    ${docker}!=1    delete the emails from both main remote test mailboxes

delete the emails from both main remote test mailboxes
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    Delete All Emails
    close mailbox
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test.two@gmail.com    password=testtest1
    Delete All Emails
    close mailbox

the user should get a confirmation email
    [Arguments]    ${receiver}    ${password}    ${content}    ${subject}
    run keyword if    ${docker}==1    the user should get a local confirmation email    ${receiver}    ${content}    ${subject}
    run keyword if    ${docker}!=1    the user should get a remote confirmation email    ${receiver}    ${password}    ${content}    ${subject}

the user should get a remote confirmation email
    [Arguments]    ${receiver}    ${password}    ${content}    ${subject}
    Open Mailbox    server=imap.googlemail.com    user=${receiver}    password=${password}
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES1}=    Get Matches From Email    ${WHICH EMAIL}    ${content}
    log    ${MATCHES1}
    Should Not Be Empty    ${MATCHES1}
    delete email    ${WHICH EMAIL}
    close mailbox

the user should get a local confirmation email
    [Arguments]    ${receiver}    ${content}    ${subject}
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES1}=    Get Matches From Email    ${WHICH EMAIL}    ${content}
    log    ${MATCHES1}
    Should Not Be Empty    ${MATCHES1}
    delete email    ${WHICH EMAIL}
    close mailbox

the user opens the remote mailbox and clicks the reset link
    [Arguments]    ${receiver}
    Open Mailbox    server=imap.googlemail.com    user=worth.email.test@gmail.com    password=testtest1
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=Reset your password
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${WHICH EMAIL}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user opens the mailbox and clicks the reset link
    [Arguments]    ${receiver}
    run keyword if    ${docker}==1    the user opens the local mailbox and clicks the reset link    ${receiver}
    run keyword if    ${docker}!=1    the user opens the remote mailbox and clicks the reset link    ${receiver}

the user opens the local mailbox and clicks the reset link
    [Arguments]    ${receiver}
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH EMAIL} =    wait for email    toemail=${receiver}    subject=Reset your password
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${WHICH EMAIL}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox

the user verifies the email
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${LATEST} =    wait for email    fromEmail=noresponse@innovateuk.gov.uk
    ${HTML}=    get email body    ${LATEST}
    log    ${HTML}
    ${LINK}=    Get Links From Email    ${LATEST}
    log    ${LINK}
    ${VERIFY_EMAIL}=    Get From List    ${LINK}    1
    log    ${VERIFY_EMAIL}
    go to    ${VERIFY_EMAIL}
    Capture Page Screenshot
    Delete All Emails
    close mailbox
