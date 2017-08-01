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
...
...               INFUND-8397  Permission denied when submitting your finances as a collaborator
...
...               IFS-401 Support team view of detailed finances in application form
...
...               IFS-802 Enable Innovation Lead user profile matching CSS permissions
Suite Setup       The user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    Close browser and delete emails
Force Tags        Applicant  HappyPath
Default Tags
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot
Resource          ../../../10__Project_setup/PS_Common.robot
# For the testing of those Testing cases, the application that has been used is:
# CLOSED_COMPETITION_APPLICATION_NAME that is A new innovative solution
# of the Competition: Connected digital additive manufacturing
# For an Open Competition Application, we have used OPEN_COMPETITION_APPLICATION_2_NAME, which is
# Application:Planetary science Pluto's telltale heart
# from the Competition: Predicting market trends programme

*** Test Cases ***
Calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]
    When the user clicks the button/link  link=${CLOSED_COMPETITION_APPLICATION_NAME}
    And the user expands the section      Finances summary
    Then the finance summary calculations should be correct
    And the finance Funding breakdown calculations should be correct

Calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]
    [Setup]  log in as a different user   &{collaborator1_credentials}
    When the user clicks the button/link  link=${CLOSED_COMPETITION_APPLICATION_NAME}
    And the user expands the section      Finances summary
    Then the finance summary calculations should be correct
    And the finance Funding breakdown calculations should be correct

Contribution to project and funding sought should not be negative number
    [Documentation]    INFUND-524
    [Tags]
    [Setup]  log in as a different user                       &{lead_applicant_credentials}
    When the user navigates to Your-finances page             ${OPEN_COMPETITION_APPLICATION_2_NAME}
    And the user fills in the project costs                   ${OPEN_COMPETITION_APPLICATION_2_NAME}
    And the user fills in the organisation information        ${OPEN_COMPETITION_APPLICATION_2_NAME}  ${SMALL_ORGANISATION_SIZE}
    And the user checks your funding section for the project  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the contribution to project and funding sought should be 0 and not a negative number

Your Finance includes Finance summary table for lead applicant
    [Documentation]    INFUND-6893
    [Tags]
    [Setup]  log in as a different user            &{lead_applicant_credentials}
    When the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the finance summary table in Your Finances has correct values for lead
    And the user clicks the button/link            link=Return to application overview

Your Finance includes Finance summary table for collaborator
     [Documentation]    INFUND-6893
     [Tags]
     [Setup]  log in as a different user           &{collaborator2_credentials}
    When the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the finance summary table in Your Finances has correct values for collaborator
    And The user clicks the button/link            link=Return to application overview

Red warning should show when the finances are incomplete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]    HappyPath
    [Setup]  log in as a different user           &{lead_applicant_credentials}
    When the user navigates to the page           ${DASHBOARD_URL}
    And the user clicks the button/link           link=${OPEN_COMPETITION_APPLICATION_2_NAME}
    And the user clicks the button/link           link=Finances overview
    Then the red warning should be visible
    And the user should see the element           css=.warning-alert
    And the user should see the text in the page  The following organisations have not marked their finances as complete:

Green check should show when the finances are complete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]
    When the user navigates to the page   ${DASHBOARD_URL}
    And the user clicks the button/link   link=${OPEN_COMPETITION_APPLICATION_2_NAME}
    When the user clicks the button/link  link=Finances overview
    Then Green check should be visible

Collaborator marks finances as complete
    [Documentation]    INFUND-8397
    [Tags]
    log in as a different user                     &{collaborator1_credentials}
    When the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    the user marks the finances as complete        ${OPEN_COMPETITION_APPLICATION_2_NAME}
    [Teardown]  logout as user

Alert shows If the academic research participation is too high
    [Documentation]    INFUND-1436
    [Tags]    Email
    Given Login new application invite academic    ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${OPEN_COMPETITION_NAME}  You will be joining as part of the organisation
    When log in as a different user                ${test_mailbox_one}+academictest@gmail.com  ${correct_password}
    Then The user navigates to the academic application finances
    And The user clicks the button/link            link=Your project costs
    When the user enters text to a text field      id=incurred-staff  1000000
    And log in as a different user                 &{lead_applicant_credentials}
    And the user navigates to the finance overview of the academic
    Then the user should see the text in the page  The participation levels of this project are not within the required range
    And the user navigates to the page             ${DASHBOARD_URL}
    And the user clicks the button/link            link=Academic robot test application
    And the user clicks the button/link            link=Review and submit
    And the user clicks the button/link            jQuery=button:contains("Finances summary")
    Then the user should see the text in the page  The participation levels of this project are not within the required range

Alert should not show If research participation is below the maximum level
    [Documentation]    INFUND-1436
    [Tags]
    [Setup]
    When lead enters a valid research participation value
    And the user navigates to the finance overview of the academic
    Then the user should see the text in the page  The participation levels of this project are within the required range
    And the user navigates to the page             ${DASHBOARD_URL}
    And the user clicks the button/link            link=Academic robot test application
    And the user clicks the button/link            link=Review and submit
    And the user clicks the button/link            jquery=button:contains("Finances summary")
    Then the user should see the text in the page  The participation levels of this project are within the required range

Support User can see read only summary link for each partner
    [Documentation]  IFS-401
    [Tags]  Support
    [Setup]  log in as a different user     &{support_user_credentials}
    When the user navigates to the page     ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link     link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section        Finances summary
    Then the user should see the element    jQuery=.finance-summary tbody tr:nth-of-type(1) th:contains("${EMPIRE_LTD_NAME}"):contains("View finances")
    And the user should see the element     jQuery=.finance-summary tbody tr:nth-of-type(2) th:contains("Ludlow"):contains("View finances")
    And the user should see the element     jQuery=.finance-summary tbody tr:nth-of-type(3) th:contains("EGGS"):contains("View finances")

Support User can see read only summary for lead
    [Documentation]  IFS-401
    [Tags]  Support
    [Setup]  The user clicks the button/link       jQuery=.finance-summary tbody tr:nth-of-type(1) th a
    When the user should see the text in the page  Please complete your project finances.
    Then the finance summary table in Your Finances has correct values for lead

Support User can see read only summary for collaborator
    [Documentation]  IFS-401
    [Tags]  Support
    [Setup]  log in as a different user           &{support_user_credentials}
    When the user navigates to the page           ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link           link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section              Finances summary
    When the user clicks the button/link          jQuery=.finance-summary tbody tr:contains("EGGS") th a
    And the user should see the text in the page  Please complete your project finances.
    Then the finance summary table in Your Finances has correct values for collaborator

Support User can see read only view of collaborator Your project costs for Labour, Overhead Costs and Materials
    [Documentation]  IFS-401
    [Tags]  Support
    [Setup]  log in as a different user   &{support_user_credentials}
    When the user navigates to the page   ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link   link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section      Finances summary
    When the user clicks the button/link  jQuery=.finance-summary tbody tr:contains("Ludlow") th a
    Then the user should see the text in the page  Please complete your project finances.
    When the user clicks the button/link  jQuery=a:contains("Your project costs")
    And the user should see the text in the page  Provide the project costs for 'Ludlow'
    And the user clicks the button/link   jQuery=button:contains("Labour")
    Then the user should see the element  jQuery=dt:contains("Working days per year") ~ dd:contains("230")
    And the user should see the element   jQuery=.labour-costs-table td:contains("anotherrole")
    And the user should see the element   jQuery=.labour-costs-table td:contains("anotherrole") ~ td:contains("120000")
    And the user should see the element   jQuery=.labour-costs-table td:contains("anotherrole") ~ td:contains("100")
    And the user clicks the button/link   jQuery=button:contains("Labour")
    When the user clicks the button/link  jQuery=button:contains("Overhead costs")
    Then the user should see the element  jQuery=th:contains("20% of labour costs")
    When the user clicks the button/link  jQuery=button:contains("Materials")
    Then the user should see the element  jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("10")
    And the user should see the element   jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("100")
    And the user should see the element   jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("test")
    And the user clicks the button/link   jQuery=button:contains("Overhead costs")

Support User can see read only view of collaborator Your project costs for rest of the categories
    [Documentation]  IFS-401
    [Tags]  Support
    When the user clicks the button/link  jQuery=button:contains("Capital usage")
    Then the user should see the element  jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("some description")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("New")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("10")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(4):contains("5000")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(5):contains("25")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(6):contains("100")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(7):contains("£ 4975")
    And the user clicks the button/link   jQuery=button:contains("Capital usage")
    When the user clicks the button/link  jQuery=button:contains("Subcontracting costs")
    Then the user should see the element  jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("SomeName")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("Netherlands")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("Quality Assurance")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(4):contains("£ 1,000")
    And the user clicks the button/link   jQuery=button:contains("Subcontracting costs")
    When the user clicks the button/link  jQuery=button:contains("Travel and subsistence")
    Then the user should see the element  jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("test")
    And the user should see the element   jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("10")
    And the user should see the element   jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("100")
    And the user clicks the button/link   jQuery=button:contains("Travel and subsistence")
    When the user clicks the button/link  jQuery=button:contains("Other costs")
    Then the user should see the element  jQuery=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("some other costs")
    And the user should see the element   jQuery=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("50")

Support User can see read only view of Your organisation
    [Documentation]  IFS-401
    [Tags]  Support
    When the user clicks the button/link           jQuery=a:contains("Your finances")
    Then the user should see the text in the page  Please complete your project finances.
    When the user clicks the button/link           jQuery=a:contains("Your organisation")
    Then the user should see the text in the page  Organisation size
    And the user should see the element            jQuery=dt:contains("Turnover") + dd:contains("150")
    And the user should see the element            jQuery=dt:contains("employees") + dd:contains("0")

Support User can see read only view of Your funding
    [Documentation]  IFS-401
    [Tags]  Support
    When the user clicks the button/link    jQuery=a:contains("Your finances")
    Then the user should see the text in the page     Please complete your project finances.
    When the user clicks the button/link    jQuery=a:contains("Your funding")
    Then the user should see the element    jQuery=dt:contains("Funding level") + dd:contains("45%")
    And the user should see the element     jQuery=p:contains("No other funding")

Innovation lead can see read only summary link for each partner
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    [Setup]  log in as a different user     &{innovation_lead_one}
    When the user navigates to the page     ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link     link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section        Finances summary
    Then the user should see the element    jQuery=.finance-summary tbody tr:nth-of-type(1) th:contains("${EMPIRE_LTD_NAME}"):contains("View finances")
    And the user should see the element     jQuery=.finance-summary tbody tr:nth-of-type(2) th:contains("Ludlow"):contains("View finances")
    And the user should see the element     jQuery=.finance-summary tbody tr:nth-of-type(3) th:contains("EGGS"):contains("View finances")

Innovation lead can see read only summary for lead
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    [Setup]  The user clicks the button/link  jQuery=.finance-summary tbody tr:nth-of-type(1) th a
    When the user should see the text in the page       Please complete your project finances.
    Then the finance summary table in Your Finances has correct values for lead

Innovation lead can see read only summary for collaborator
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user navigates to the page     ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link     link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section        Finances summary
    When the user clicks the button/link    jQuery=.finance-summary tbody tr:contains("EGGS") th a
    And the user should see the text in the page      Please complete your project finances.
    Then the finance summary table in Your Finances has correct values for collaborator

Innovation lead can see read only view of collaborator Your project costs for Labour, Overhead Costs and Materials
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user navigates to the page       ${server}/management/competition/${OPEN_COMPETITION}/applications/all
    And the user clicks the button/link       link=${OPEN_COMPETITION_APPLICATION_2_NUMBER}
    And the user expands the section          Finances summary
    When the user clicks the button/link      jQuery=.finance-summary tbody tr:contains("Ludlow") th a
    Then the user should see the text in the page  Please complete your project finances.
    When the user clicks the button/link      jQuery=a:contains("Your project costs")
    And the user should see the text in the page   Provide the project costs for 'Ludlow'
    And the user clicks the button/link       jQuery=button:contains("Labour")
    Then the user should see the element      jQuery=dt:contains("Working days per year") ~ dd:contains("230")
    And the user should see the element       jQuery=.labour-costs-table td:contains("anotherrole")
    And the user should see the element       jQuery=.labour-costs-table td:contains("anotherrole") ~ td:contains("120000")
    And the user should see the element       jQuery=.labour-costs-table td:contains("anotherrole") ~ td:contains("100")
    And the user collapses the section        Labour
    When the user clicks the button/link      jQuery=button:contains("Overhead costs")
    Then the user should see the element      jQuery=th:contains("20% of labour costs")
    And the user clicks the button/link       jQuery=button:contains("Overhead costs")
    When the user expands the section         Materials
    Then the user should see the element      jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("10")
    And the user should see the element       jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("100")
    And the user should see the element       jQuery=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("test")
    And the user collapses the section        Materials

Innovation lead can see read only view of collaborator Your project costs for rest of the categories
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user clicks the button/link  jQuery=button:contains("Capital usage")
    Then the user should see the element  jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("some description")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("New")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("10")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(4):contains("5000")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(5):contains("25")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(6):contains("100")
    And the user should see the element   jQuery=#capital-usage-table tbody tr:nth-of-type(1) td:nth-of-type(7):contains("£ 4975")
    And the user clicks the button/link   jQuery=button:contains("Capital usage")
    When the user clicks the button/link  jQuery=button:contains("Subcontracting costs")
    Then the user should see the element  jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("SomeName")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("Netherlands")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("Quality Assurance")
    And the user should see the element   jQuery=#subcontracting-table tbody tr:nth-of-type(1) td:nth-of-type(4):contains("£ 1,000")
    When the user clicks the button/link  jQuery=button:contains("Subcontracting costs")
    And the user collapses the section    Subcontracting costs
    When the user expands the section     Travel and subsistence
    Then the user should see the element  jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("test")
    And the user should see the element   jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("10")
    And the user should see the element   jQuery=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3):contains("100")
    And the user collapses the section    Subcontracting costs
    When the user expands the section     Other costs
    Then the user should see the element  jQuery=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1):contains("some other costs")
    And the user should see the element   jQuery=#other-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2):contains("50")

Innovation lead can see read only view of Your organisation
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user clicks the button/link           jQuery=a:contains("Your finances")
    Then the user should see the text in the page  Please complete your project finances.
    When the user clicks the button/link           jQuery=a:contains("Your organisation")
    Then the user should see the text in the page  Organisation size
    And the user should see the element            jQuery=dt:contains("Turnover") + dd:contains("150")
    And the user should see the element            jQuery=dt:contains("employees") + dd:contains("0")

Innovation lead can see read only view of Your funding
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user clicks the button/link           jQuery=a:contains("Your finances")
    Then the user should see the text in the page  Please complete your project finances.
    When the user clicks the button/link           jQuery=a:contains("Your funding")
    Then the user should see the element           jQuery=dt:contains("Funding level") + dd:contains("45%")
    And the user should see the element            jQuery=p:contains("No other funding")

*** Keywords ***
the finance summary calculations should be correct
    the user should see the element  jQuery=.finance-summary tbody tr:last-of-type:contains("£349,046")
    the user should see the element  jQuery=.finance-summary tbody tr:last-of-type:contains("£58,793")
    the user should see the element  jQuery=.finance-summary tbody tr:last-of-type:contains("£502,468")
    the user should see the element  jQuery=.finance-summary tbody tr:last-of-type:contains("£140,632")

the finance Funding breakdown calculations should be correct
    the user should see the element  jQuery=.project-cost-breakdown th:contains("${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}") + td:contains("£147,153")
    the user should see the element  jQuery=.project-cost-breakdown th:contains("${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}") + td:contains("£200,903")
    the user should see the element  jQuery=.project-cost-breakdown th:contains("${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}") + td:contains("£990")
    the user should see the element  jQuery=.project-cost-breakdown th:contains("Total") + td:contains("£349,046")

the finance summary table in Your Finances has correct values for lead
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(1)  Total project costs
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(1)  £72,611
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(2)  % Grant
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(2)  30%
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(3)  Funding sought
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(3)  £0
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(4)  Other public sector funding
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(4)  £8,000,000
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(5)  Contribution to project
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(5)  £0

the finance summary table in Your Finances has correct values for collaborator
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(1)  Total project costs
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(1)  £495
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(2)  % Grant
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(2)  100%
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(3)  Funding sought
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(3)  £990
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(4)  Other public sector funding
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(4)  £0
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) th:nth-of-type(5)  Contribution to project
    the user sees the text in the element  css=.form-group tr:nth-of-type(1) td:nth-of-type(5)  £0

the contribution to project and funding sought should be 0 and not a negative number
    the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    the user sees the text in the element     css=.form-group tr:nth-of-type(1) td:nth-of-type(3)  £0
    the user sees the text in the element     css=.form-group tr:nth-of-type(1) td:nth-of-type(5)  £0

Green check should be visible
    Page Should Contain Image  css=.finance-summary tr:nth-of-type(1) img[src*="/images/field/tick-icon"]

the red warning should be visible
    the user should see the element  jQuery=.warning-alert h2:contains("not marked their finances as complete")

Lead enters a valid research participation value
    the user navigates to the academic application finances
    the user clicks the button/link                   link=Your project costs
    run keyword and ignore error without screenshots  the user clicks the button/link  jQuery=.buttonlink:contains("Edit")
    the user clicks the button/link                   jQuery=button:contains("Labour")
    the user should see the element                   name=add_cost
    the user clicks the button/link                   jQuery=button:contains('Add another role')
    the user should see the element                   css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field              css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input  Test
    wait for autosave
    the user enters large text to a text field        css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input  1200000000
    wait for autosave
    the user enters text to a text field              css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input  1000
    wait for autosave
    the user selects the checkbox                     id=agree-state-aid-page
    the user clicks the button/link                   jQuery= button:contains('Mark as complete')
    wait for autosave

the user checks Your Funding section for the project
    [Arguments]  ${Application}
    ${Research_category_selected}=  Run Keyword And Return Status  Element Should Be Visible  link=Your funding
    Run Keyword if  '${Research_category_selected}' == 'False'  the user selects research area via Your Funding section  ${Application}
    Run Keyword if  '${Research_category_selected}' == 'True'  the user fills in the funding information with bigger amount  ${Application}

the user selects research area via Your Funding section
    [Arguments]  ${Application}
    the applicant completes the application details                   application details
    And the user fills in the funding information with bigger amount  ${Application}

the user fills in the funding information with bigger amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    [Arguments]  ${Application}
    the user navigates to Your-finances page  ${Application}
    the user clicks the button/link           link=Your funding
    the user enters text to a text field      css=#cost-financegrantclaim  30
    click element                             jQuery=label:contains("Yes")
    the user enters text to a text field      css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input  8000000
    the user enters text to a text field      css=#other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input  test2
    the user selects the checkbox             agree-terms-page
    the user clicks the button/link           jQuery=button:contains("Mark as complete")

the user expands the section
    [Arguments]  ${section}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=button:contains("${section}")[aria-expanded="false"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link  jQuery=button:contains("${section}")[aria-expanded="false"]

the user collapses the section
    [Arguments]  ${section}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=button:contains("${section}")[aria-expanded="true"]
    run keyword if  '${status}'=='PASS'  the user clicks the button/link  jQuery=button:contains("${section}")[aria-expanded="true"]