*** Variables ***
${REMOTE_URL}     ${EMPTY}
${DESIRED_CAPABILITIES}    ${EMPTY}
${SERVER_AUTH}    ${EMPTY}
${PROTOCOL}    http://

*** Keywords ***
Login as user
    [Arguments]    ${email}    ${password}
    The guest user opens the browser
    The guest user inserts user email & password    ${email}    ${password}
    The guest user clicks the log-in button

The guest user inserts user email & password
    [Arguments]    ${USERNAME}    ${PSW}
    Input Text    id=id_email    ${USERNAME}
    Input Password    id=id_password    ${PSW}

The guest user clicks the log-in button
    Click Button    css=input.button

The guest user opens the browser
    Open browser  about:  ${BROWSER}
    ...  remote_url=${REMOTE_URL}
    ...  desired_capabilities=${DESIRED_CAPABILITIES}  
    Run keyword if  '${SERVER_AUTH}' != ''    Go to    ${PROTOCOL}${SERVER_AUTH}@${SERVER_BASE}
    Run keyword if  '${SERVER_AUTH}' == ''    Go to    ${PROTOCOL}${SERVER_BASE}


User closes the browser
    Close all browsers

Logout as user
    Click Element    link=Logout
