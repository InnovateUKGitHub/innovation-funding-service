*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
Test Teardown     User closes the browser
Force Tags
Default Tags      Finance    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}    ${SERVER}/application/2
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}    ${SERVER}/application/2/form/section/7
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}    ${SERVER}/application/2/form/section/8

*** Test Cases ***
Finance summary page calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration
    Given the user logs in as lead applicant
    When the user goes to the finance summary of the Providing sustainable childcare application
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    And the user logs out

Finance summary calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration
    Given the user logs in as first collaborator
    And the user goes to the finance summary of the Providing sustainable childcare application
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    And the user logs out

Finance summary calculations for the second collaborator
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration
    Given the user logs in as second collaborator
    And the user goes to the finance summary of the Providing sustainable childcare application
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    and when the applicant enters a bigger funding amount
    then the contribution to project and funding sought should be 0
    And the user logs out

*** Keywords ***
The user logs in as lead applicant
    Login as user    &{lead_applicant_credentials}

the user goes to the finance summary of the Providing sustainable childcare application
    go to    ${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}
    click element    link=Finances overview

The user logs out
    Logout as user

The user logs in as first collaborator
    Login as user    &{collaborator1_credentials}

The user logs in as second collaborator
    Login as user    &{collaborator2_credentials}

the finance Project cost breakdown calculations should be correct
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £0
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(8) td:nth-of-type(1)    £180,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(8) td:nth-of-type(2)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(8) td:nth-of-type(3)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(8) td:nth-of-type(4)    £60,000

the finance summary calculations should be correct
    Element Should Contain    css=.finance-summary tr:nth-of-type(1) td:nth-of-type(1)    £180,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(2)    50%
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(3)    70%
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(4)    70%
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £84,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(1)    £30,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(1)    £66,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(2)    £30,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(3)    £18,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(4)    £18,000

when the applicant enters a bigger funding amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}
    Select Radio button    other_funding-otherPublicFunding-null    Yes
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    80000
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test2
    Sleep    1s

the contribution to project and funding sought should be 0
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(4)    £0
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(4)    £0
