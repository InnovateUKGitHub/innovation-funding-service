*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user should see an error
    [Arguments]    ${ERROR_TEXT}
    IFS Run Keyword And Ignore Error    Mouse Out    css=input
    IFS Run Keyword And Ignore Error    Focus    jQuery=Button:contains("Mark as complete")
    IFS Run Keyword And Ignore Error    Focus    link=Contact us
    IFS Wait Until page contains element    jQuery=.error-message
    IFS Wait Until Page Contains    ${ERROR_TEXT}

The user should see a field error
    [Arguments]    ${ERROR_TEXT}
    IFS Wait Until page contains element    jQuery=.error-message:contains("${ERROR_TEXT}")    5s

The user should get an error page
    [Arguments]    ${ERROR_TEXT}
    IFS Wait Until page contains element    css=.error
    IFS Wait Until page contains    ${ERROR_TEXT}

browser validations have been disabled
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');jQuery('[maxlength]').removeAttr('maxlength');

the user cannot see a validation error in the page
    Element Should Not Be Visible    css=.error

The user should see a summary error
    [Arguments]    ${ERROR_TEXT}
    IFS Wait Until page contains element    jQuery=.error-summary:contains('${ERROR_TEXT}')    5s

The user should see a field and summary error
    [Arguments]    ${ERROR_TEXT}
    the user should see a field error    ${ERROR_TEXT}
    the user should see a summary error    ${ERROR_TEXT}
