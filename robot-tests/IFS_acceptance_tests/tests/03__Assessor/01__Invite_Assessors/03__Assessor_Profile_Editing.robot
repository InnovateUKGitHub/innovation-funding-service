*** Settings ***
Documentation     INFUND-5182 - As an assessor creating an account I need to supply details of my skills and expertise so that InnovateUK can assign me appropriate applications to assess.
Suite Setup       guest user log-in    ${assessor2_credentials["email"]}    ${assessor2_credentials["password"]}
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/variables/PASSWORD_VARIABLES.robot

*** Variables ***

*** Test Cases ***
Edit skills and expertise: client-side validations
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your skills")
    Given the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    Please select an assessor type
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.

Edit skills and expertise: server-side validations
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=label:contains("Business")
    Given the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    Given the user enters a long random alphanumeric string into a text field    id=skillAreas    5001
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    This field cannot contain more than 5,000 characters

Edit skills and expertise: save new skills and business type redirects to dashboard
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters text to a text field    id=skillAreas    assessor skill areas text
    And the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Edit skills and expertise: skills and business type are saved correctly
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your skills")
    Then radio button should be set to    assessorType    BUSINESS
    Then the user sees the text in the element    id=skillAreas    assessor skill areas text
