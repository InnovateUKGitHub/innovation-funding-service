*** Settings ***
Documentation     INFUND-5182 As an assessor creating an account I need to supply details of my skills and expertise so that InnovateUK can assign me appropriate applications to assess.
...
...               INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       guest user log-in    jeremy.alufson@innovateuk.test    Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot
Resource          ../../../resources/variables/PASSWORD_VARIABLES.robot

*** Test Cases ***
Client-side validations
    [Documentation]    INFUND-5182
    ...
    ...    INFUND-5432
    [Tags]    HappyPath
    Given The user should see the text in the page    Complete your assessor account
    And The user should see the element    link=your skills    #his checks the alert message on the top od the page
    When the user clicks the button/link    jQuery=a:contains("your skills")
    And The user should see the element    link=Edit your skills
    When the user clicks the button/link    jQuery=a:contains("Edit your skills")
    And the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    And the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see an error    Please select an assessor type
    And the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.

Server-side validations
    [Documentation]    INFUND-5182
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    And the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And browser validations have been disabled
    And the user enters multiple strings into a text field    id=skillAreas    e    5001
    And the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see an error    This field cannot contain more than 5,000 characters

Save Skills should redirect to dashboard
    [Documentation]    INFUND-5182
    ...
    ...    INFUND-5432
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters text to a text field    id=skillAreas    assessor skill areas text
    And the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}
    And The user should not see the element    jQuery=.message-alert a:contains('your skills')    #his checks the alert message on the top od the page

Skills are saved correctly
    [Documentation]    INFUND-5182
    [Tags]
    When the user clicks the button/link    jQuery=a:contains("your skills")
    And the user sees the text in the element    id=assessorType    Business
    And the user sees the text in the element    id=skillAreas    assessor skill areas text
    [Teardown]    And the user clicks the button/link    link=Back to assessor dashboard

*** Keywords ***
The correct radio button should be selected
    Then radio button should be set to    assessorType    BUSINESS
