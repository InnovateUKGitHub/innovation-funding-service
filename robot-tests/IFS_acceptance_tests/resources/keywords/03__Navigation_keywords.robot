*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user navigates to the page
    [Arguments]    ${TARGET_URL}
    Wait for autosave
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Go To    ${TARGET_URL}
    # Error checking
    the user should not see an error in the page

The user navigates to the page without the usual headers
    [Arguments]    ${TARGET_URL}
    Wait for autosave
    Go To    ${TARGET_URL}
    # Error checking
    the user should not see an error in the page

The user should be redirected to the correct page
    [Arguments]    ${URL}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Location Should Contain    ${URL}
    the user should not see an error in the page


the user should be redirected to the correct page without the usual headers
    [Arguments]    ${URL}
    Wait Until Keyword Succeeds Without Screenshots    30    200ms    Location Should Contain    ${URL}
    the user should not see an error in the page

The user should see the text in the page
    [Arguments]    ${VISIBLE_TEXT}
    Wait Until Page Contains Without Screenshots    ${VISIBLE_TEXT}
    the user should not see an error in the page

The user goes back to the previous page
    Wait for autosave
    Go Back

the user reloads the page
    Wait for autosave
    Reload Page
    # Error checking
    the user should not see an error in the page