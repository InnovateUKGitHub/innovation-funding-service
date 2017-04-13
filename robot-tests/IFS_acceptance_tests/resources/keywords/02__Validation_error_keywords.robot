*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user should see an error
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    Mouse Out    css=input
    Run Keyword And Ignore Error Without Screenshots    Focus    jQuery=Button:contains("Mark as complete")
    Run Keyword And Ignore Error Without Screenshots    Focus    link=Contact us
    Wait Until Page Contains Element Without Screenshots    jQuery=.error-message
    Wait Until Page Contains Without Screenshots    ${ERROR_TEXT}

The user should see a field error
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    jQuery=.error-message:contains("${ERROR_TEXT}")    5s

The user should get an error page
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    css=.error
    Wait Until Page Contains Without Screenshots    ${ERROR_TEXT}

browser validations have been disabled
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');jQuery('[maxlength]').removeAttr('maxlength');

the user cannot see a validation error in the page
    Element Should Not Be Visible    css=.error

The user should see a summary error
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    jQuery=.error-summary:contains('${ERROR_TEXT}')    5s

The user should see a field and summary error
    [Arguments]    ${ERROR_TEXT}
    the user should see a field error    ${ERROR_TEXT}
    the user should see a summary error    ${ERROR_TEXT}

the user should not see an error in the page
    Page Should Not Contain    Error
    Page Should Not Contain    something went wrong
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request