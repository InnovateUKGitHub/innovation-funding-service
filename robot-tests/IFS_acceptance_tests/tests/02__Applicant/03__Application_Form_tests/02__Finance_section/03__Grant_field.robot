*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***

*** Test Cases ***
Auto-save test for the "Grant" field
    [Tags]    Applicant    Autosave    Finance    HappyPath
    Given the user navigates to the page    ${YOUR_FINANCES_URL}
    Then auto-save should work for the "Grant" field
    And the grant value should be correct in the finance summary page

*** Keywords ***
the grant value should be correct in the finance summary page
    The user navigates to the page    ${FINANCES_OVERVIEW_URL}
    Element Should Contain    css=.finance-summary tr:nth-of-type(1) td:nth-of-type(2)    25

auto-save should work for the "Grant" field
    Clear Element Text    id=cost-financegrantclaim
    focus    jQuery= button:contains('complete')
    Sleep    500ms
    Reload Page
    ${input_value} =    Get Value    id=cost-financegrantclaim
    Should Be Equal As Strings    ${input_value}    0
    Clear Element Text    id=cost-financegrantclaim
    Input Text    id=cost-financegrantclaim    25
    focus    jQuery= button:contains('complete')
    Sleep    300ms
    Reload Page
    focus    jQuery= button:contains('complete')
    ${input_value} =    Get Value    id=cost-financegrantclaim
    Should Be Equal As Strings    ${input_value}    25
