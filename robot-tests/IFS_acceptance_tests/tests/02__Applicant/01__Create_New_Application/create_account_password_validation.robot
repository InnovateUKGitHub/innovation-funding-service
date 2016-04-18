*** Settings ***
Documentation     -INFUND-1147: Further acceptance tests for the create account page
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${blacklisted_password}    Password123
${blacklisted_password_message}    Password is too weak
${lower_case_password}    thisisallinlowercase1
${lower_case_message}    Password must contain at least one lower case letter
${upper_case_password}    THISISALLINUPPERCASE2
${upper_case_message}    Password must contain at least one upper case letter
${no_numbers_password}    thishasnonumbers
${no_numbers_message}    Password must contain at least one number
${personal_info_password}    Smith123
${personal_info_message}    ${EMPTY}

*** Test Cases ***
Invalid password (from the blacklist)
    [Documentation]    INFUND-1147
    [Tags]    Account
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${blacklisted_password}
    And the user enters text to a text field    id=retypedPassword    ${blacklisted_password}
    And the user submits their information
    Then the user should see the text in the page    ${blacklisted_password_message}
    And The user should see the text in the page    We were unable to create your account
    And the user cannot login with the invalid password    ${blacklisted_password}

Invalid password (all lower case)
    [Documentation]    INFUND-1147
    [Tags]    Account   Failing
    # Note that the copy for this message is wrong - so it will start failing once that copy changes. Can be simply fixed with a change to ${lower_case_message} above
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${lower_case_password}
    And the user enters text to a text field    id=retypedPassword    ${lower_case_password}
    And the user submits their information
    Then the user should see the text in the page    ${lower_case_message}
    And The user should see the text in the page    We were unable to create your account
    And the user cannot login with the invalid password    ${lower_case_password}

Invalid password (all upper case)
    [Documentation]    INFUND-1147
    [Tags]    Account       Failing
    # Note that the copy for this message is wrong - so it will start failing once that copy changes. Can be simply fixed with a change to ${upper_case_message} above
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${upper_case_password}
    And the user enters text to a text field    id=retypedPassword    ${upper_case_password}
    And the user submits their information
    Then the user should see the text in the page    ${upper_case_message}
    And The user should see the text in the page    We were unable to create your account
    And the user cannot login with the invalid password    ${upper_case_password}

Invalid password (no numbers)
    [Documentation]    INFUND-1147
    [Tags]    Account       Failing
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${no_numbers_password}
    And the user enters text to a text field    id=retypedPassword    ${no_numbers_password}
    And the user submits their information
    Then the user should see the text in the page    ${no_numbers_message}
    And The user should see the text in the page    We were unable to create your account
    And the user cannot login with the invalid password    ${no_numbers_password}

Invalid password (personal information)
    [Documentation]    INFUND-1147
    [Tags]    Account    Pending
    # Pending since this validation doesn't seem to exist INFUND-2366
    Given the user navigates to the page    ${ACCOUNT_CREATION_FORM_URL}
    When the user enters text to a text field    id=firstName    John
    And the user enters text to a text field    id=lastName    Smith
    And the user enters text to a text field    id=phoneNumber    01141234567
    And the user enters text to a text field    id=email    ewan+40@hiveit.co.uk
    And the user enters text to a text field    id=password    ${personal_info_password}
    And the user enters text to a text field    id=retypedPassword    ${personal_info_password}
    And the user submits their information
    Then the user should see the text in the page    ${personal_info_message}
    And The user should see the text in the page    We were unable to create your account
    And the user cannot login with the invalid password    ${personal_info_password}

*** Keywords ***
the user submits their information
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Select Checkbox    termsAndConditions
    Submit Form

the user cannot login with the invalid password
    [Arguments]    ${invalid_password}
    The user navigates to the page    ${LOGIN_URL}
    Input Text    id=username    ewan+40@hiveit.co.uk
    Input Password    id=password    ${invalid_password}
    Click Button    css=button[name="_eventId_proceed"]
    Execute Javascript    jQuery('form').attr('novalidate','novalidate');
    Click Button    css=button[name="_eventId_proceed"]
    Page Should Contain    Your login was unsuccessful because of the following issue(s)
    Page Should Contain    Your username/password combination doesn't seem to work
