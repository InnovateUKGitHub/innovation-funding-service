*** Settings ***
Documentation     -INFUND-1147: Further acceptance tests for the create account page
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Test Template     Invalid Password Check
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${blacklisted_password}             Password123
${blacklisted_password_message}     Password is too weak
${lower_case_password}              thisisallinlowercase1
${lower_case_message}               Passwords must contain at least one lower case letter
${upper_case_password}              THISISALLINUPPERCASE2
${upper_case_message}               Passwords must contain at least one lower case letter
${no_numbers_password}              thishasnonumbers
${no_numbers_message}               Password must contain at least one number
${personal_info_password}           Smith123
${personal_info_message}
${special_chars_password}           Pass w0rd
${special_chars_message}            Special chars not allowed





*** Test Cases ***    password
Invalid password (from the blacklist)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      ${blacklisted_password}       ${blacklisted_password_message}

Invalid password (all lower case)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      # Note that the copy for this message is wrong - so it will start failing once that copy changes. Can be simply fixed with a change to ${lower_case_message} above
                      ${lower_case_password}        ${lower_case_message}

Invalid password (all upper case)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      ${upper_case_password}        ${upper_case_message}

Invalid password (no numbers)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      ${no_numbers_password}        ${no_numbers_message}

Invalid password (special characters)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      ${special_chars_password}      ${special_chars_message}


Invalid password (personal information)
                      [Documentation]                 INFUND-1147
                      [Tags]                          Account
                      ${personal_info_password}     ${personal_info_message}



*** Keywords ***

Invalid Password Check
    [Arguments]    ${invalid_password}          ${password_error_message}
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${invalid_password}
    And the user enters text to a text field    id=retypedPassword    ${invalid_password}
    And the user submits their information
    Then the user should see the text in the page            ${password_error_message}
    And The user should see the text in the page            We were unable to create your account
    And the user cannot login with the invalid password    ${invalid_password}

the user submits their information
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Select Checkbox    termsAndConditions
    Submit Form

the user cannot login with the invalid password
    [Arguments]    ${invalid_password}
    go to    ${LOGIN_URL}
    Input Text       id=username       ewan+40@hiveit.co.uk
    Input Password    id=password    ${invalid_password}
    Click Button    css=button[name="_eventId_proceed"]

    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work