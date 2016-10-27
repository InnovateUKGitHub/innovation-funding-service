*** Settings ***
Documentation     INFUND-5182 As an assessor creating an account I need to supply details of my skills and expertise so that InnovateUK can assign me appropriate applications to assess.
...
...               INFUND-1481 As an assessor I need to review and accept the Innovate UK Assessor contract so that I am able to assess a competition
...
...               INFUND-5628 As an assessor I want to be able to monitor my contract expiry so that I can be sure that I am eligible to assess competitions.
Suite Setup       guest user log-in    &{assessor2_credentials}
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
Your Skills: client-side validations
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your skills")
    Given the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    Please select an assessor type
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.

Your Skills: server-side validations
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=label:contains("Business")
    Given the user enters multiple strings into a text field    id=skillAreas    word${SPACE}    101
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    Maximum word count exceeded. Please reduce your word count to 100.
    Given the user enters multiple strings into a text field    id=skillAreas    e    5001
    When the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should see an error    This field cannot contain more than 5,000 characters

Your Skills: save new skills and business type redirects to dashboard
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=label:contains("Business")
    When the user enters text to a text field    id=skillAreas    assessor skill areas text
    And the user clicks the button/link    jQuery=button:contains("Continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Your Skills: skills and business type are saved correctly
    [Documentation]    INFUND-5182
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your skills")
    Then radio button should be set to    assessorType    BUSINESS
    Then the user sees the text in the element    id=skillAreas    assessor skill areas text
    And the user clicks the button/link    link=Back to assessor dashboard

Your Contract: client-side validations
    [Documentation]    INFUND-1481
    [Tags]
    Given the user clicks the button/link    jQuery=a:contains("Your contract")
    When the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should see an error    Please agree to the terms and conditions

Your Contract: Download terms and conditions
    [Documentation]    INFUND-1481
    When the user clicks the button/link    link=Download terms of contract
    Then the user should be redirected to the correct page without the usual headers    ${Server}/assessment/documents/AssessorServicesAgreementContractIFSAug2016.pdf
    And The user goes back to the previous page
    [Teardown]    The user navigates to the page    ${Server}/assessment/profile/terms

Your Contract: server-side validations and redirect to dashboard
    [Documentation]    INFUND-1481
    [Tags]
    When the user selects the checkbox    id=agreesToTerms1
    And the user should not see an error in the page
    And the user clicks the button/link    jQuery=button:contains("Save and continue")
    Then the user should be redirected to the correct page    ${assessor_dashboard_url}

Your Contract: Agreement Confirmation
    [Documentation]    INFUND-5628
    Then the user clicks the button/link    jQuery=a:contains("Your contract")
    Then the user should see the text in the page    You signed the contract on
