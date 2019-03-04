*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
########################## READING EMAILS ##################################
The user reads his email
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    ${email_to_test}=    get email    ${recipient}    ${subject}
    email contains pattern    ${email_to_test}    ${pattern}
    delete email    ${email_to_test}
    close mailbox

####################### CLICKING EMAILED LINKS ############################
The user reads his email and clicks the link
    [Arguments]    ${recipient}    ${subject}    ${pattern}        ${link_number}=1
    ${email_to_test}=    get email    ${recipient}    ${subject}
    email contains pattern    ${email_to_test}    ${pattern}
    click the email link    ${email_to_test}    ${link_number}
    delete email    ${email_to_test}
    close mailbox

######################## DELETING EMAILS #####################################
Delete the emails from the local test mailbox
    Run Keyword and Ignore Error Without Screenshots   Remove All Emails    server=${local_imap}   port=${local_imap_port}   user=smtp    password=smtp   is_secure=False   timeout=1


######################## PRIVATE KEYWORDS ################################
get email
    [Arguments]    ${recipient}    ${subject}
    Open Mailbox    server=${local_imap}    port=${local_imap_port}   user=smtp    password=smtp     is_secure=False
    ${email_to_test}=  wait for email    sender=${sender}    recipient=${recipient}    subject=${subject}    timeout=90
    #log ${subject}
    [return]    ${email_to_test}

email contains pattern
    [Arguments]    ${email}    ${pattern}
    ${html}=    get email body    ${email}
    #log    ${html}
    ${matches}=    Get Matches From Email    ${email}    ${pattern}
    #log    ${matches}
    Should Not Be Empty    ${MATCHES}

click the email link
    [Arguments]    ${email}    ${link_number}
    ${all_links}=    Get Links From Email    ${email}
    #log    ${all_links}
    ${link}=    Get From List    ${all_links}    ${link_number}
    #log    ${LINK}
    go to    ${link}


