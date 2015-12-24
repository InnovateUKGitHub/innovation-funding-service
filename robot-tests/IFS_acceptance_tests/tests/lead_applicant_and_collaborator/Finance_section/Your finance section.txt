*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** variables ***

*** Test Cases ***
Auto-save test for the "Grant" field
    [Tags]    Applicant    Autosave    Finance
    Given Applicant goes to the Your finances section
    Then auto-save should work for the "Grant" field
    and the grant value should be correct in the finance summary page

*** Keywords ***
the grant value should be correct in the finance summary page
    Applicant goes to the finance overview
    Element Should Contain    css=.form-group > table tr:nth-child(2) .numeric:nth-child(3)    25

auto-save should work for the "Grant" field
    Clear Element Text    id=cost-financegrantclaim
    focus    jQuery= button:contains('Mark as complete')
    Sleep    1s
    Reload Page
    ${input_value} =    Get Value    id=cost-financegrantclaim
    Should Be Equal As Strings    ${input_value}    0
    Clear Element Text    id=cost-financegrantclaim
    Input Text    id=cost-financegrantclaim    25
    focus    jQuery= button:contains('Mark as complete')
    Sleep    1s
    Reload Page
    focus    jQuery= button:contains('Mark as complete')
    ${input_value} =    Get Value    id=cost-financegrantclaim
    Should Be Equal As Strings    ${input_value}    25

the Applicant edits the "The Gross Annual Salary"
    Wait Until Page Contains Element    css=#labour-costs-table tbody td:nth-of-type(2) input
    Clear Element Text    css=#cost-labour-1-workingDays
    Input Text    css=#cost-labour-1-workingDays    256
    Input Text    css=#labour-costs-table tbody td:nth-of-type(2) input    2000
    Input Text    css=#labour-costs-table tbody td:nth-of-type(4) input    150
    Focus    css=#content > div.grid-row > div.column-two-thirds > form > div.alignright-button > a
