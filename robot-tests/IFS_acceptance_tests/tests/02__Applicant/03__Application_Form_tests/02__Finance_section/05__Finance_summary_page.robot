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
...
...
...               INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
...
...
...               INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Suite Teardown    User closes the browser
Force Tags        Finances
Default Tags
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***
${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}    ${SERVER}/application/2
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}    ${SERVER}/application/2/form/section/7
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}    ${SERVER}/application/2/form/section/8
${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}    ${SERVER}/application/7/form/section/8
${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}    ${SERVER}/application/7/form/section/7
${OVERVIEW_MARK_AS_COMPLETE}    ${SERVER}/application/7

*** Test Cases ***
Calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    [Teardown]    Log out as user

Calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    And the applicant enters a bigger funding amount
    Then the contribution to project and funding sought should be 0 and not a negative number
    [Teardown]    Log out as user

Red warning should show when the finances are incomplete
    [Documentation]    INFUND-927
    ...
    ...    INFUND-894
    ...
    ...    INFUND-446
    [Tags]    HappyPath
    [Setup]    Guest user log-in    email=worth.email.test+submit@gmail.com    password=Passw0rd
    Given the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}
    When the user clicks the button/link    jQuery=button:contains("Edit")
    Then the red warnng should be visible
    And the user should see the element    css=.warning-alert
    And the user should see the text in the page    The following organisations have not marked their finances as complete:

Green check should show when the finances are complete
    [Documentation]    INFUND-927
    ...
    ...    INFUND-894
    ...
    ...    INFUND-446
    [Tags]    HappyPath
    Given the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SECTION}
    When the user clicks the button/link    jQuery=.button:contains("Mark all as complete")
    Then the user should be redirected to the correct page    ${OVERVIEW_MARK_AS_COMPLETE}
    And the user navigates to the page    ${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}
    And both green checks should be visible

Alert should show If the research participation of the academic partner is too high
    [Documentation]    INFUND-1436
    [Tags]    HappyPath
    [Setup]    Guest user log-in    &{collaborator2_credentials}
    When the user navigates to the page    ${your_finances_url_application_2}
    And the user enters text to a text field    id=incurred-staff    1000000000
    And Guest user log-in    &{lead_applicant_credentials}
    And the user navigates to the page    ${FINANCES_OVERVIEW_URL_APPLICATION_2}
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user clicks the button/link    jquery=button:contains("Finances Summary")
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    [Teardown]    Academics partner enters a valid resaerch participation value

Alert should not show If research participation is below the maximum level
    [Documentation]    INFUND-1436
    [Tags]    HappyPath
    [Setup]    Given Guest user log-in    &{collaborator1_credentials}
    When the first collaborator edits financial details to bring down the research participation level
    And Guest user log-in    &{lead_applicant_credentials}
    And the user navigates to the page    ${FINANCES_OVERVIEW_URL_APPLICATION_2}
    Then the user should see the text in the page    The participation levels of this project are within the required range
    And the user navigates to the page    ${APPLICATION_2_SUMMARY_URL}
    And the user clicks the button/link    jquery=button:contains("Finances Summary")
    Then the user should see the text in the page    The participation levels of this project are within the required range
    [Teardown]    User closes the browser

*** Keywords ***
the finance Project cost breakdown calculations should be correct
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(1) td:nth-of-type(3)    £0
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(4) td:nth-of-type(1)    £129,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(1) td:nth-of-type(1)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £60,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £9,000

the finance summary calculations should be correct
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(1)    £129,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(1) td:nth-of-type(2)    50%
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(2)    70%
    Element Should Contain    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(2)    100%
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(3)    £61,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(4)    £20,000
    Element Should Contain    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(5)    £48,000

the applicant enters a bigger funding amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}
    #Select Radio button    other_funding-otherPublicFunding-35-null    Yes
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    80000
    Input Text    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test2
    #Sleep    300ms
    Execute Javascript    jQuery('form').attr('data-test','true');

the contribution to project and funding sought should be 0 and not a negative number
    go to    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(3)    £0
    Element Should Contain    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(5)    £0

both green checks should be visible
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(1) img[src="/images/field/tick-icon.png"]
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(2) img[src="/images/field/tick-icon.png"]

the red warnng should be visible
    go to    ${MARKING_IT_AS_COMPLETE_FINANCE_SUMMARY}
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(1) img[src="/images/warning-icon.png"]

The first collaborator edits financial details to bring down the research participation level
    the user navigates to the page    ${your_finances_url_application_2}
    Click Element    jQuery=button:contains("Labour")
    Wait Until Element Is Visible    name=add_cost
    Click Element    jQuery=button:contains('Add another role')
    Wait Until Page Contains Element    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    Input Text    css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    Sleep    1s
    focus    css=.app-submit-btn

Academics partner enters a valid resaerch participation value
    And Guest user log-in    &{collaborator2_credentials}
    And the user navigates to the page    ${your_finances_url_application_2}
    And the user enters text to a text field    id=incurred-staff    1000
    User closes the browser
