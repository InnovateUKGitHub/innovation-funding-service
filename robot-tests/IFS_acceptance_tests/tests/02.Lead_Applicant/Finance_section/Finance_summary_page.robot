*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
...
...
...               INFUND-435 As an applicant and I am on the finance summary, I want to see the partner details listed horizontally so I can see all partner details in the finance summary table
...
...
...               INFUND-927 As a lead partner i want the system to show me when all questions and sections (partner finances) are complete on the finance summary, so that i know i can submit the application
...
...
...               INFUND-894 As a lead partner I want to easily see whether or not my partner's finances are marked as complete, so that i can have the right level of confidence in the figures
Suite Teardown    User closes the browser
Test Teardown     Log out as user
Force Tags        Finance    Applicant    # these tests have been tagged as failing since the numbers no longer match up to the database - find out why!
Default Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}    ${SERVER}/application/2
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}    ${SERVER}/application/2/form/section/7
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}    ${SERVER}/application/2/form/section/8
${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}    ${SERVER}/application/7/form/section/8
${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}    ${SERVER}/application/7/form/section/7

*** Test Cases ***
Finance summary page calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]    Collaboration     Pending
    # Pending until the academic finances db update comes through
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When the user goes to the finance summary of the Providing sustainable childcare application
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct

Finance summary calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]    Collaboration     Pending
     # Pending until the academic finances db update comes through
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user goes to the finance summary of the Providing sustainable childcare application
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct

Finance summary calculations for the second collaborator
    [Documentation]    INFUND-524
    [Tags]    HappyPath     Pending
     # Pending until the academic finances db update comes through
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    And the user goes to the finance summary of the Providing sustainable childcare application
    When the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    And the applicant enters a bigger funding amount
    Then the contribution to project and funding sought should be 0 and not a negative number

Red warning should show when the finances are incomplete
    [Documentation]    INFUND-927
    ...
    ...    INFUND-894
    [Tags]    HappyPath    Failing
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}
    And the user clicks the button/link    css=[aria-controls="collapsible-1"]
    And the user clicks the button/link    jQuery=#collapsible-1 button:contains("Edit")
    Then the red warnng should be visible
    And the user should see the element    css=.warning-alert
    And the user should see the text in the page    The following organisations have not maked their finances as complete:
    [Teardown]

Green check should show when the applicant marks the finance as complete
    [Documentation]    INFUND-927
    ...
    ...    INFUND-894
    [Tags]    HappyPath    Failing
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}
    And the user clicks the button/link    css=[aria-controls="collapsible-1"]
    And the user clicks the button/link    jQuery=#collapsible-1 button:contains("Mark as complete")
    Then both green checks should be visible

*** Keywords ***
the user goes to the finance summary of the Providing sustainable childcare application
    the user navigates to the page    ${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}
    click element    link=Finances overview

the finance Project cost breakdown calculations should be correct
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(1) td:nth-of-type(3)    £0
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(4) td:nth-of-type(1)    £180,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(1) td:nth-of-type(1)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £60,000

the finance summary calculations should be correct
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(1)    £180,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(1) td:nth-of-type(2)    50%
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(2)    70%
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(2)    70%
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(3)    £84,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(4)    £30,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(5)    £66,000

the applicant enters a bigger funding amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}
    #Select Radio button    other_funding-otherPublicFunding-35-null    Yes
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    80000
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test2
    Sleep    1s

the contribution to project and funding sought should be 0 and not a negative number
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(3)    £0
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(5)    £0

both green checks should be visible
    the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(1) img[src="/images/field/tick-icon.png"]
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(2) img[src="/images/field/tick-icon.png"]

the red warnng should be visible
    go to    ${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(2) img[src="/images/warning-icon.png"]
