*** Settings ***
Documentation     INFUND-524 As an applicant I want to see the finance summary updated and recalculated as each partner adds their finances.
...
...               INFUND-435 As an applicant and I am on the finance summary, I want to see the partner details listed horizontally so I can see all partner details in the finance summary table
...
...               INFUND-927 As a lead partner i want the system to show me when all questions and sections (partner finances) are complete on the finance summary, so that i know i can submit the application
...
...               INFUND-894 As a lead partner I want to easily see whether or not my partner's finances are marked as complete, so that i can have the right level of confidence in the figures
...
...               INFUND-438: As an applicant and I am filling in the finance details I want a fully working Other funding section
...
...               INFUND-1436 As a lead applicant I want to be able to view the ratio of research participation costs in my consortium so I know my application is within the required range
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Default Tags
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Variables ***
${OVERVIEW_PAGE_PROVIDING_SUSTAINABLE_CHILDCARE_APPLICATION}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/form/section/187  #Your finances page
${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}    ${SERVER}/application/${OPEN_COMPETITION_APPLICATION_2}/form/section/198
${applicationPluto}  ${OPEN_COMPETITION_APPLICATION_2_NAME}

*** Test Cases ***
Calculations for Lead applicant
    [Documentation]    INFUND-524
    ...
    ...    This test case still use the old application after the refactoring. We need to add an extra collaborator in the newly created application for this.
    [Tags]
    When the user navigates to the page    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct
    [Teardown]    The user closes the browser

Calculations for the first collaborator
    [Documentation]    INFUND-524
    ...    This test case still use the old application after the refactoring
    [Tags]
    [Setup]    Guest user log-in    &{collaborator1_credentials}
    When the user navigates to the page    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SUMMARY}
    Then the finance summary calculations should be correct
    And the finance Project cost breakdown calculations should be correct

Contribution to project and funding sought should not be negative number
    [Documentation]    INFUND-524
    ...
    ...    This test case still use the old application after the refactoring
    [Tags]    Pending
    # TODO Pending due to INFUND-8706
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    When the user navigates to Your-finances page  ${applicationPluto}
    And the user fills in the project costs
    And the user fills in the organisation information  ${applicationPluto}
    And the user checks your funding section for the project  ${applicationPluto}
    Then the contribution to project and funding sought should be 0 and not a negative number

Your Finance includes Finance summary table for lead applicant
    [Documentation]    INFUND-6893
    [Tags]    HappyPath
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    When the user navigates to Your-finances page  ${applicationPluto}
    Then The user should see the text in the page     Your finances
    And the finance summary table in Your Finances has correct values for lead
    And the user clicks the button/link       link=Return to application overview

Your Finance includes Finance summary table for collaborator
     [Documentation]    INFUND-6893
     [Tags]
     [Setup]  log in as a different user    &{collaborator2_credentials}
    When the user navigates to Your-finances page  ${applicationPluto}
    Then The user should see the text in the page     Your finances
    And the finance summary table in Your Finances has correct values for collaborator
    And The user clicks the button/link        link=Return to application overview

Red warning should show when the finances are incomplete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]    HappyPath
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Finances overview
    Then the red warning should be visible
    And the user should see the element    css=.warning-alert
    And the user should see the text in the page    The following organisations have not marked their finances as complete:

Green check should show when the finances are complete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]
    [Setup]
    #TODO   investigate intermmitent failure
    When the user navigates to Your-finances page    Robot test application
    And the user marks the finances as complete     Robot test application
    Then the user redirects to the page    Please provide information about your project.    Application overview
    And the user clicks the button/link    link=Finances overview
    Then Green check should be visible
    [Teardown]    The user closes the browser

Alert shows If the academic research participation is too high
    [Documentation]    INFUND-1436
    [Tags]    Email
    [Setup]    Login new application invite academic    ${test_mailbox_one}+academictest@gmail.com    Invitation to collaborate in ${OPEN_COMPETITION_NAME}    You will be joining as part of the organisation
    Given guest user log-in    ${test_mailbox_one}+academictest@gmail.com  ${correct_password}
    And The user navigates to the academic application finances
    And The user clicks the button/link       link=Your project costs
    When the user enters text to a text field      id=incurred-staff    1000
    And log in as a different user  &{lead_applicant_credentials}
    And the user navigates to the finance overview of the academic
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    And the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Review and submit
    And the user clicks the button/link    jQuery=button:contains("Finances summary")
    Then the user should see the text in the page    The participation levels of this project are not within the required range
    [Teardown]

Alert should not show If research participation is below the maximum level
    [Documentation]    INFUND-1436
    [Tags]
    [Setup]    Log in as a different user   &{lead_applicant_credentials}
    When Lead enters a valid research participation value
    And the user navigates to the finance overview of the academic
    Then the user should see the text in the page    The participation levels of this project are within the required range
    And the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Review and submit
    And the user clicks the button/link    jquery=button:contains("Finances summary")
    Then the user should see the text in the page    The participation levels of this project are within the required range


*** Keywords ***

the finance Project cost breakdown calculations should be correct
    the user sees the text in the element    css=.project-cost-breakdown tr:nth-of-type(1) td:nth-of-type(3)    £0
    the user sees the text in the element    css=.project-cost-breakdown tr:nth-of-type(4) td:nth-of-type(1)    £201,398
    the user sees the text in the element    css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £100,452
    the user sees the text in the element    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £495

the finance summary calculations should be correct
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(1)    £201,398
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(1) td:nth-of-type(2)    30%
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(2) td:nth-of-type(2)    30%
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(3) td:nth-of-type(2)    100%
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(3)    £58,298
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(4)    £2,468
    the user sees the text in the element    css=.finance-summary tr:nth-of-type(4) td:nth-of-type(5)    £140,632

the finance summary table in Your Finances has correct values for lead
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(1)    Total project costs
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(1)    £100,452
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(2)    % Grant
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(2)    30%
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(3)    Funding sought
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(3)    £28,901
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(4)    Other public sector funding
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(4)    £1,234
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(5)    Contribution to project
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(5)    £70,316

the finance summary table in Your Finances has correct values for collaborator
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(1)    Total project costs
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(1)    £495
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(2)    % Grant
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(2)    100%
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(3)    Funding sought
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(3)    £495
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(4)    Other public sector funding
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(4)    £0
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) th:nth-of-type(5)    Contribution to project
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(5)    £0

the contribution to project and funding sought should be 0 and not a negative number
    the user navigates to the page    ${PROVIDING_SUSTAINABLE_CHILDCARE_FINANCE_SECTION}
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(3)    £0
    the user sees the text in the element    css=.form-group tr:nth-of-type(1) td:nth-of-type(5)     £0

Green check should be visible
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(1) img[src*="/images/field/tick-icon"]

the red warning should be visible
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Finances overview
    Page Should Contain Image    css=.finance-summary tr:nth-of-type(1) img[src*="/images/warning-icon"]

Lead enters a valid research participation value
    When the user navigates to the academic application finances
    the user clicks the button/link       link=Your project costs
    the user clicks the button/link    jQuery=button:contains("Labour")
    the user should see the element    name=add_cost
    the user clicks the button/link    jQuery=button:contains('Add another role')
    the user should see the element    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    Test
    wait for autosave
    the user enters large text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    1200000000
    wait for autosave
    the user enters text to a text field    css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    1000
    wait for autosave
    then the user selects the checkbox      id=agree-state-aid-page
    the user clicks the button/link        jQuery= button:contains('Mark as complete')
    wait for autosave

the user checks Your Funding section for the project
    [Arguments]  ${Application}
    ${Research_category_selected}=  Run Keyword And Return Status    Element Should Be Visible   link=Your funding
    Run Keyword if   '${Research_category_selected}' == 'False'     the user selects research area via Your Funding section    ${Application}
    Run Keyword if   '${Research_category_selected}' == 'True'      the user fills in the funding information with bigger amount     ${Application}

the user selects research area via Your Funding section
    [Arguments]  ${Application}
    the applicant completes the application details     application details
    And the user fills in the funding information with bigger amount     ${Application}

the user fills in the funding information with bigger amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    [Arguments]  ${Application}
    the user navigates to Your-finances page   ${Application}
    the user clicks the button/link       link=Your funding
    the user enters text to a text field  css=#cost-financegrantclaim  30
    click element                         jQuery=label:contains("Yes")
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    8000000
    the user enters text to a text field    css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test2
    the user selects the checkbox         agree-terms-page
    the user clicks the button/link       jQuery=button:contains("Mark as complete")
