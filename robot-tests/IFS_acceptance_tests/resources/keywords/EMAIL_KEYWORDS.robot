*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
the user reads his email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    ########################## READING EMAILS ##################################
    run keyword if    ${docker}==1    the user reads his email locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email remotely    ${recipient}    ${subject}    ${pattern}

the user reads his email from the second mailbox
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}==1    the user reads his email locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email from the second mailbox remotely    ${recipient}    ${subject}    ${pattern}

the user reads his email from the default mailbox
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    [Documentation]    Please note that we need to keep this keyword as some email addresses are alread in the database, so we cannot over-ride them with our own custom mailboxes during the tests
    run keyword if    ${docker}==1    the user reads his email locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email from the default remote mailbox    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_one}
    ...    ${test_mailbox_one_password}

the user reads his email from the second default mailbox
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    [Documentation]    Please note that we need to keep this keyword as some email addresses are alread in the database, so we cannot over-ride them with our own custom mailboxes during the tests
    run keyword if    ${docker}==1    the user reads his email locally    ${recipient}    ${subject}    ${pattern}
    run keyword if    ${docker}!=1    the user reads his email from the default remote mailbox    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_two}
    ...    ${test_mailbox_two_password}

the user reads his email locally
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=${local_imap}    port=${local_imap_port}    user=smtp    password=smtp    is_secure=False
    ${email_to_test}=  wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    check pattern in email    ${email_to_test}    ${pattern}
    Set Test Variable    ${email_to_test}

the user reads his email remotely
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    check pattern in email    ${email_to_test}    ${pattern}
    Set Test Variable    ${email_to_test}

the user reads his email from the second mailbox remotely
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_two}@gmail.com    password=${test_mailbox_two_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    check pattern in email    ${email_to_test}    ${pattern}

the user reads his email from the default remote mailbox
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${custom_mailbox}    ${custom_password}
    Open Mailbox    server=imap.googlemail.com    user=${custom_mailbox}@gmail.com    password=${custom_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    check pattern in email    ${email_to_test}    ${pattern}

check pattern in email
    [Arguments]    ${email_to_test}    ${pattern}
    ${HTML}=    get email body    ${email_to_test}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${email_to_test}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    delete email    ${email_to_test}
    close mailbox
    ####################### CLICKING EMAILED LINKS ############################

the user reads his email and clicks the link
    [Arguments]    ${recipient}    ${subject}    ${pattern}        ${link_number}=1
    run keyword if    ${docker}==1    the user reads his email and clicks the link locally    ${recipient}    ${subject}    ${pattern}    ${link_number}
    run keyword if    ${docker}!=1    the user reads his email and clicks the link remotely    ${recipient}    ${subject}    ${pattern}    ${link_number}

the user reads his email from the default mailbox and clicks the link
    [Arguments]    ${recipient}    ${subject}    ${pattern}        ${link_number}=1
    run keyword if    ${docker}==1    the user reads his email and clicks the link locally    ${recipient}    ${subject}    ${pattern}    ${link_number}
    run keyword if    ${docker}!=1    the user reads his email from the default mailbox and clicks the link remotely    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_one}    ${link_number}
    ...    ${test_mailbox_one_password}

the user reads his email from the second default mailbox and clicks the link
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${link_number}=1
    run keyword if    ${docker}==1    the user reads his email and clicks the link locally    ${recipient}    ${subject}    ${pattern}    ${link_number}
    run keyword if    ${docker}!=1    the user reads his email from the default mailbox and clicks the link remotely    ${recipient}    ${subject}    ${pattern}    ${test_mailbox_two}    ${link_number}
    ...    ${test_mailbox_two_password}

the user reads his email and clicks the link locally
    [Arguments]    ${recipient}    ${subject}    ${pattern}     ${link_number}
    Open Mailbox    server=${local_imap}    port=${local_imap_port}   user=smtp    password=smtp     is_secure=False
    ${email_to_test}=  wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    log    ${subject}
    the user reads the email and clicks the link    ${email_to_test}    ${pattern}    ${link_number}

the user reads his email and clicks the link remotely
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${link_number}
    Open Mailbox    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    the user reads the email and clicks the link    ${email_to_test}    ${pattern}    ${link_number}

the user reads his email from the default mailbox and clicks the link remotely
    [Arguments]    ${recipient}    ${subject}    ${pattern}    ${mailbox}    ${mailbox_password}    ${link_number}
    Open Mailbox    server=imap.googlemail.com    user=${mailbox}@gmail.com    password=${mailbox_password}
    ${email_to_test} =    wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=200
    log    ${subject}
    the user reads the email and clicks the link    ${email_to_test}    ${pattern}    ${link_number}

the user reads the email and clicks the link
    [Arguments]    ${email_to_test}    ${pattern}    ${link_number}
    ${HTML}=    get email body    ${email_to_test}
    log    ${HTML}
    ${MATCHES}=    Get Matches From Email    ${email_to_test}    ${pattern}
    log    ${MATCHES}
    Should Not Be Empty    ${MATCHES}
    ${ALL_LINKS}=    Get Links From Email    ${email_to_test}
    log    ${ALL_LINKS}
    ${LINK}=    Get From List    ${ALL_LINKS}    ${link_number}
    log    ${LINK}
    go to    ${LINK}
    delete email    ${email_to_test}
    close mailbox
    ######################## DELETING EMAILS #####################################

Delete the emails from both test mailboxes
    run keyword if    ${docker}==1    delete the emails from the local test mailbox    # Note that all emails come through to the same local mailbox, so we only need to delete from one mailbox here
    run keyword if    ${docker}!=1    delete the emails from both remote test mailboxes

delete the emails from both remote test mailboxes
    delete the emails from the default remote test mailbox
    Run Keyword and Ignore Error Without Screenshots    Remove All Emails    server=imap.googlemail.com    user=${test_mailbox_two}@gmail.com    password=${test_mailbox_two_password}    timeout=1

Delete the emails from the default test mailbox
    run keyword if    ${docker}==1    delete the emails from the local test mailbox
    run keyword if    ${docker}!=1    delete the emails from the default remote test mailbox

delete the emails from the default remote test mailbox
    Run Keyword and Ignore Error Without Screenshots    Remove All Emails    server=imap.googlemail.com    user=${test_mailbox_one}@gmail.com    password=${test_mailbox_one_password}    timeout=1

delete the emails from the local test mailbox
    Run Keyword and Ignore Error Without Screenshots   Remove All Emails    server=${local_imap}   port=${local_imap_port}   user=smtp    password=smtp   is_secure=False   timeout=1

Delete the emails from both default test mailboxes
    run keyword if    ${docker}==1    delete the emails from the local test mailbox    # Note that all emails come through to the same local mailbox, so we only need to delete from one mailbox here
    run keyword if    ${docker}!=1    delete the emails from both remote test mailboxes
