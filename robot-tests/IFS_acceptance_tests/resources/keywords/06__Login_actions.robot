*** Settings ***
Library           SauceLabs

*** Variables ***
${REMOTE_URL}     ${EMPTY}
${DESIRED_CAPABILITIES}    ${EMPTY}
${SERVER_AUTH}    ${EMPTY}
${TEST_TAGS}      ${EMPTY}
${FF_PROFILE}     ${CURDIR}/../firefox_config

*** Keywords ***
The user logs-in in new browser
    [Arguments]  ${email}  ${password}
    The guest user opens the browser
    Logging in and Error Checking  ${email}  ${password}

Log in as a different user
    [Arguments]    ${email}    ${password}
    logout as user
    the guest user inserts user email and password    ${email}    ${password}
    the guest user clicks the log-in button

Logging in and Error Checking
    [Arguments]    ${email}    ${password}
    The guest user inserts user email and password    ${email}    ${password}
    The guest user clicks the log-in button
    Wait Until Page Contains Without Screenshots    Dashboard
    the user should not see an error in the page

The guest user inserts user email and password
    [Arguments]    ${email}    ${password}
    Wait Until Element Is Visible Without Screenshots    id=username
    Wait Until Element Is Visible Without Screenshots    id=password
    Input Text    id=username    ${email}
    Input Password    id=password    ${password}

The guest user clicks the log-in button
    Wait Until Element Is Visible Without Screenshots    css=button[name="_eventId_proceed"]
    Click Button    css=button[name="_eventId_proceed"]

The guest user opens the browser
    Run keyword if    '${VIRTUAL_DISPLAY}' == 'true'    Start Virtual Display    1920    1080
    Run keyword if    '${SERVER_AUTH}' != ''    Open browser    ${PROTOCOL}${SERVER_AUTH}@${SERVER_BASE}    ${BROWSER}    remote_url=${REMOTE_URL}    desired_capabilities=${DESIRED_CAPABILITIES}
    Run keyword if    '${SERVER_AUTH}' == ''    Open browser    ${PROTOCOL}${SERVER_BASE}    ${BROWSER}    remote_url=${REMOTE_URL}    desired_capabilities=${DESIRED_CAPABILITIES}
    Run keyword if    '${REMOTE_URL}' != 'http://hub:4444/wd/hub'    Set Selenium Timeout    10
    Run keyword if    '${REMOTE_URL}' == 'http://hub:4444/wd/hub'    Set Selenium Timeout    10

The user closes the browser
    Run keyword if    '${SAUCELABS_RUN}' == 1    Get Sauce Labs Suite Report
    Close any open browsers

Logout as user
    the user clicks the button/link    id=proposition-name
    the user clicks the button/link    link=Sign out
    The user should be redirected to the correct page    ${LOGGED_OUT_URL_FRAGMENT}
    Run Keyword And Ignore Error Without Screenshots     Confirm Action

Get Sauce Labs Test Report
    Run Keyword And Ignore Error Without Screenshots    Report Sauce status    'IFS | ${PREV_TEST_NAME}'    ${PREV_TEST_STATUS}    ${TEST_TAGS}    ${REMOTE_URL}

Get Sauce Labs Suite Report
    Run Keyword And Ignore Error Without Screenshots    Report Sauce status    'IFS | ${SUITE_NAME}'    ${SUITE_STATUS}    ${SUITE_MESSAGE}    ${REMOTE_URL}

Close any open browsers
    Run Keyword And Ignore Error Without Screenshots    Close all browsers

the user cannot login with their new details
    [Arguments]    ${email}    ${password}
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ${email}
    Input Password    id=password    ${password}
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    ${unsuccessful_login_message}
    Page Should Contain    Your email/password combination doesn't seem to work.

the user logs out if they are logged in
    Run Keyword And Ignore Error Without Screenshots    log out as user
