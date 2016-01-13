*** Settings ***
Documentation     INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
Suite Setup       Log in as user    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${OTHER_FUNDING_SOURCE}    My mate Dave
${OTHER_FUNDING_DATE}    12-2008
${OTHER_FUNDING_AMOUNT}    10000

*** Test Cases ***
Add details for another source of funding and verify that these details have bee autosaved
    [Documentation]    INFUND-438
    [Tags]    Applicant    Application    Finances    Other funding    Failing
    Given Applicant goes to the Your finances section
    And Applicant selects 'Yes' for other funding
    And Applicant chooses to add another source of funding
    When Applicant can see a new row
    And Applicant enters some details into this row
    And Applicant chooses to add yet another source of funding
    And the applicant enters some details into the second row
    Then the total of the other funding should be correct
    Then Applicant can leave the 'Your finances' page but the details are still saved
    And applicant selects 'No' for other funding
    And applicant can see that the 'No' radio button is selected
    And applicant cannot see the 'other funding' details

*** Keywords ***
Applicant can see that the 'No' radio button is selected
    Radio Button Should Be Set To    other_funding-otherPublicFunding-54    No

Applicant selects 'Yes' for other funding
    Select Radio button    other_funding-otherPublicFunding-54    Yes

Applicant chooses to add another source of funding
    Click Link    Add another source of funding
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Sleep    2s

Applicant chooses to add yet another source of funding
    Select Radio button    other_funding-otherPublicFunding-54    Yes
    Click Link    Add another source of funding
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    Sleep    2s
    Click Element    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    Sleep    2s

Applicant selects 'No' for other funding
    Select Radio button    other_funding-otherPublicFunding-54    No

Applicant can see a new row
    Element Should Be Visible    id=other-funding-table

Applicant enters some details into this row
    Sleep    5s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    Sleep    5s

Applicant can leave the 'Your finances' page but the details are still saved
    Reload Page
    Sleep    5s
    #Alert Should Be Present
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Textfield Should Contain    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}

Applicant cannot see the 'other funding' details
    Page Should Not Contain    ${OTHER_FUNDING_SOURCE}
    Page Should Not Contain    ${OTHER_FUNDING_DATE}
    Page Should Not Contain    ${OTHER_FUNDING_AMOUNT}

the applicant enters some details into the second row
    Sleep    5s
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    ${OTHER_FUNDING_DATE}
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(3) input
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(3) input    ${OTHER_FUNDING_AMOUNT}
    Input Text    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(1) input    ${OTHER_FUNDING_SOURCE}
    Wait Until Element Is Visible    css=#other-funding-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    focus    css=.app-submit-btn
    Sleep    5s

the total of the other funding should be correct
    Textfield Value Should Be    id=other-funding-total    £ 20,000
