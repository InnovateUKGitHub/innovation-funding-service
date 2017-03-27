*** Settings ***
Documentation     INFUND-5182 As an assessor creating an account I need to supply details of my skills and expertise so that InnovateUK can assign me appropriate applications to assess.
...
...               INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
...
...               INFUND-7059 As an assessor I can view my skills page so I can decide if my skills need updating
Suite Setup       guest user log-in    ${test_mailbox_one}+jeremy.alufson@gmail.com    Passw0rd
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
    And The user should see the element    jQuery=.message-alert.extra-margin-bottom a:contains("your skills")    #this checks the alert message on the top od the page
    When the user clicks the button/link    jQuery=a:contains("your skills")
    And the user should see the text in the page    Innovation areas
    And the user enters multiple strings into a text field    id=skillAreas    w${SPACE}    101
    And the user clicks the button/link    jQuery=button:contains("Save and return to your skills")
    Then the user should see an error    Please select an assessor type.
    And the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.

Cancel button redirects to the read-only view without changes
    [Documentation]    INFUND-8009
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains(Cancel)
    Then the user should be redirected to the correct page    ${assessment_skills_url}
    [Teardown]    the user clicks the button/link    jQuery=a:contains("Edit")

Back button from edit page redirects to read only view
    [Documentation]    INFUND-8009
    [Tags]
    Given the user clicks the button/link    link=Your skills
    Then the user should be redirected to the correct page    ${assessment_skills_url}
    [Teardown]    the user clicks the button/link    jQuery=a:contains("Edit")

Server-side validations
    [Documentation]    INFUND-5182
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters multiple strings into a text field    id=skillAreas    w${SPACE}    102
    And the user clicks the button/link    jQuery=button:contains("Save and return to your skills")
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    And browser validations have been disabled
    And the user enters multiple strings into a text field    id=skillAreas    e    5001
    And the user clicks the button/link    jQuery=button:contains("Save and return to your skills")
    Then the user should see an error    This field cannot contain more than 5,000 characters.

Save Skills should redirect to the read-only view
    [Documentation]    INFUND-5182
    ...
    ...    INFUND-5432
    ...
    ...    INFUND-7059
    [Tags]    HappyPath
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters text to a text field    id=skillAreas    assessor skill areas text
    And the user clicks the button/link    jQuery=button:contains("Save and return to your skills")
    Then the user should be redirected to the correct page    ${assessment_skills_url}
    And the user sees the text in the element    id=skillAreas    assessor skill areas text
    And the user sees the text in the element    id=assessorType    Business
    And the user should see the text in the page    Materials, process and manufacturing design technologies

Your skills does not appear in dashboard alert
    [Documentation]    INFUND-5182
    [Tags]
    When the user clicks the button/link    link=Assessor dashboard
    Then The user should not see the element    jQuery=.message-alert a:contains('your skills')    #this checks the alert message on the top of the page
    [Teardown]    the user clicks the button/link    link=your skills

Return to assessor dashboard from skills page
    [Documentation]    INFUND-8009
    [Tags]
    When the user clicks the button/link    jQuery=a:contains(Return to assessor dashboard)
    Then the user should be redirected to the correct page     ${assessor_dashboard_url}

*** Keywords ***
The correct radio button should be selected
    Then radio button should be set to    assessorType    BUSINESS
