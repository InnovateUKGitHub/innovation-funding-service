*** Settings ***
Resource    ../defaultResources.robot

*** Keywords ***
The user verifies their email
    [Arguments]    ${verify_link}
    Go To    ${verify_link}
    Page Should Contain    Account verified

the user reads his email and clicks the link
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}==1    the user reads his email and clicks the link locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email and clicks the link remotely    ${recipient}    ${subject}    ${pattern}

the user reads his email and clicks the link locally
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

the user reads his email and clicks the link remotely
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

the user reads his email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}==1    the user reads his email locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email remotely    ${recipient}    ${subject}    ${pattern}

the user reads his email locally
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=ifs-local-dev    port=9876    user=smtp    password=smtp    is_secure=False
    ${WHICH EMAIL}=  wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${WHICH EMAIL}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    delete email    ${WHICH EMAIL}
    close mailbox

the user reads his email remotely
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${WHICH EMAIL} =  wait for email  sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    ${HTML}=    get email body    ${WHICH EMAIL}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${WHICH EMAIL}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    delete email    ${WHICH EMAIL}
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