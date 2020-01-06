*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
The user should see a field error
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    jQuery=.govuk-error-message:contains("${ERROR_TEXT}")    5s

The user should get an error page
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    css=.error
    Wait Until Page Contains Without Screenshots    ${ERROR_TEXT}

Browser validations have been disabled
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');jQuery('[maxlength]').removeAttr('maxlength');

The user cannot see a validation error in the page
    Element Should Not Be Visible    css=.error
    element should not be visible    css=.govuk-error-message
    element should not be visible    css=.govuk-error-summary

The user should see a summary error
    [Arguments]    ${ERROR_TEXT}
    Wait Until Page Contains Element Without Screenshots    jQuery=.govuk-error-summary:contains("${ERROR_TEXT}")    5s

The user should see a field and summary error
    [Arguments]    ${ERROR_TEXT}
    the user should see a field error    ${ERROR_TEXT}
    the user should see a summary error    ${ERROR_TEXT}

The user should not see an error in the page
    Page Should Not Contain    ${500_error_message}
    Page Should Not Contain    ${404_error_message}
    Page Should Not Contain    ${403_error_message}

The user navigates to the page and gets a custom error message
    [Arguments]    ${TARGET_URL}    ${CUSTOM_ERROR_MESSAGE}
    Go To    ${TARGET_URL}
    Page Should Contain    ${CUSTOM_ERROR_MESSAGE}

Specific user should not be able to access the page 403 error
    [Arguments]   ${url}   ${email}   ${password}
    log in as a different user   ${email}  ${password}
    The user navigates to the page and gets a custom error message   ${url}   ${403_error_message}
