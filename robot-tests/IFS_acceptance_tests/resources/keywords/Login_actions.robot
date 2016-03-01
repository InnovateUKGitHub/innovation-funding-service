*** Settings ***
Library           SauceLabs

*** Variables ***
${REMOTE_URL}     ${EMPTY}
${DESIRED_CAPABILITIES}    ${EMPTY}
${SERVER_AUTH}    ${EMPTY}
${TEST_TAGS}      ${EMPTY}
${FF_PROFILE}     ${CURDIR}/../firefox_config

*** Keywords ***
Guest user log-in
    [Arguments]    ${email}    ${password}
    The guest user opens the browser
    The guest user inserts user email & password    ${email}    ${password}
    The guest user clicks the log-in button
    sleep    500ms
    Page should not contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You are not authorised to perform the requested action

The guest user inserts user email & password
    [Arguments]    ${USERNAME}    ${PSW}
    Input Text    id=id_email    ${USERNAME}
    Input Password    id=id_password    ${PSW}

The guest user clicks the log-in button
    Click Button    css=input.button

The guest user opens the browser
    Start Virtual Display    1920    1080
    Run keyword if    '${SERVER_AUTH}' != ''    Open browser    ${PROTOCOL}${SERVER_AUTH}@${SERVER_BASE}    ${BROWSER}    ff_profile_dir=${FF_PROFILE}    remote_url=${REMOTE_URL}
    ...    desired_capabilities=${DESIRED_CAPABILITIES}
    Run keyword if    '${SERVER_AUTH}' == ''    Open browser    ${PROTOCOL}${SERVER_BASE}    ${BROWSER}    ff_profile_dir=${FF_PROFILE}    remote_url=${REMOTE_URL}
    ...    desired_capabilities=${DESIRED_CAPABILITIES}
    run keyword and ignore error    Log into Shib

Log into Shib
    Input Text    id=username    steve.smith@empire.com
    Input Text    id=password    test
    click element    css=button[type=submit]
    Sleep    2s
    Location Should Be    ${LOGIN_URL}

TestTeardown User closes the browser
    Run keyword if      '${REMOTE_URL}' != ''        Get Sauce Labs Test Report
    Close any open browsers

User closes the browser
    Run keyword if    '${REMOTE_URL}' != ''          Get Sauce Labs Suite Report
    Close any open browsers

Logout as user
    Click Element    link=Logout

Get Sauce Labs Test Report
    Run keyword and ignore error     Report Sauce status    'IFS | ${PREV_TEST_NAME}'    ${PREV_TEST_STATUS}    ${TEST_TAGS}    ${REMOTE_URL}


Get Sauce Labs Suite Report
    Run keyword and ignore error     Report Sauce status    'IFS | ${SUITE_NAME}'    ${SUITE_STATUS}    ${SUITE_MESSAGE}    ${REMOTE_URL}

Close any open browsers
    Run keyword and ignore error        Close all browsers