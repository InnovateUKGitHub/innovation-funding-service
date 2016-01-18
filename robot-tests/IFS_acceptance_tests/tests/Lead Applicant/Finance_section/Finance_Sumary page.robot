*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
Test Teardown     User closes the browser
Force Tags        Pending    Failing
Default Tags      Finance    Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot

*** Variables ***
${OVERVIEW_PAGE_X_APPLICATION}    ${SERVER}/application/1

*** Test Cases ***
Finance summary page calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration    Failing
    Given the user logs in as lead applicant
    When the user goes to the finance summary of the 'x' application
    Then the finance summary calculations should be correct
    and the finance Project cost breakdown calculations should be correct
    And the user logs out

Finance summary calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration    Failing
    Given the user logs in as first collaborator
    and the user goes to the finance summary of the 'x' application
    Then the finance summary calculations should be correct
    and the finance Project cost breakdown calculations should be correct
    And the user logs out

Finance summary calculations for the second collaborator
    [Documentation]    INFUND-524
    [Tags]    Finance    Finance Section    Collaboration    Failing
    Given the user logs in as second collaborator
    and the user goes to the finance summary of the 'x' application
    Then the finance summary calculations should be correct
    and the finance Project cost breakdown calculations should be correct
    And the user logs out

*** Keywords ***
The user logs in as lead applicant
    Login as user    &{lead_applicant_credentials}

the user goes to the finance summary of the 'x' application
    go to    ${OVERVIEW_PAGE_X_APPLICATION}
    #sleep    1s
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
    #Textfield Value Should Be    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(1)
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(1)    £84,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(1)    £30,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(1)    £66,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(2)    £30,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(3)    £18,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(5) td:nth-of-type(4)    £18,000
