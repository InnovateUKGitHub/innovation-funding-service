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
...               INFUND-8397  Permission denied when submitting your project finances as a collaborator
...
...               IFS-401 Support team view of detailed finances in application form
...
...               IFS-802 Enable Innovation Lead user profile matching CSS permissions
...
...               IFS-2879: As a Research applicant I MUST accept the grant terms and conditions
...
...               IFS-3609 Extend internal view of application finances to other internal roles
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
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

*** Variables ***
${allApplicationsForRTOComp}  ${SERVER}/management/competition/${openCompetitionBusinessRTO}/applications/all

*** Test Cases ***
Calculations for Lead applicant
    [Documentation]    INFUND-524
    [Tags]
    When the user clicks the button/link  link = ${CLOSED_COMPETITION_APPLICATION_NAME}
    And the user expands the section      Finances summary
    Then the finance summary calculations should be correct
    And the finance Funding breakdown calculations should be correct

Calculations for the first collaborator
    [Documentation]    INFUND-524
    [Tags]
    [Setup]  log in as a different user   &{collaborator1_credentials}
    When the user clicks the button/link  link = ${CLOSED_COMPETITION_APPLICATION_NAME}
    And the user expands the section      Finances summary
    Then the finance summary calculations should be correct
    And the finance Funding breakdown calculations should be correct

Contribution to project and funding sought should not be negative number
    [Documentation]    INFUND-524
    [Tags]
    [Setup]  log in as a different user                       &{lead_applicant_credentials}
    When the user navigates to Your-finances page             ${OPEN_COMPETITION_APPLICATION_2_NAME}
    And the user fills in the project costs                   labour costs  n/a
    And the user enters the project location
    And the user fills in the organisation information        ${OPEN_COMPETITION_APPLICATION_2_NAME}  ${SMALL_ORGANISATION_SIZE}
    And the user checks your funding section for the project  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the contribution to project and funding sought should be 0 and not a negative number

Your Finance includes Finance summary table for lead applicant
    [Documentation]    INFUND-6893
    [Tags]
    [Setup]  log in as a different user            &{lead_applicant_credentials}
    When the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the finance summary table in Your project Finances has correct values for lead  £72,611  0%  0  8,000,000  0
    And the user clicks the button/link            link = Return to application overview

Your Finance includes Finance summary table for collaborator
    [Documentation]    INFUND-6893
    [Tags]
    [Setup]  log in as a different user            &{collaborator2_credentials}
    When the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the finance summary table in Your project Finances has correct values for collaborator  £990  0%  0  2,468  0
    And The user clicks the button/link            link = Return to application overview

Red warning should show when the finances are incomplete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]
    [Setup]  log in as a different user           &{lead_applicant_credentials}
    When the user navigates to the page           ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link           link = ${OPEN_COMPETITION_APPLICATION_2_NAME}
    And the user clicks the button/link           link = Finances overview
    Then the red warning should be visible

Green check should show when the finances are complete
    [Documentation]    INFUND-927, INFUND-894, INFUND-446
    [Tags]
    When the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = ${OPEN_COMPETITION_APPLICATION_2_NAME}
    When the user clicks the button/link  link = Finances overview
    Then Green check should be visible

Finance overview shows as incomplete
    [Documentation]  IFS-3820  IFS-3821
    Given The user clicks the button/link  link = Application overview
    And the user should see the element    jQuery = li:contains("Finances overview") .task-status-incomplete
    When the user clicks the button/link   link = Finances overview
    Then the user should see the element   css = .table-total-tick[src*="icon-alert"]

Collaborator marks finances as complete
    [Documentation]    INFUND-8397  IFS-2879
    [Tags]
    Given log in as a different user                 &{collaborator1_credentials}
    When the user navigates to Your-finances page    ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the user marks the finances as complete     ${OPEN_COMPETITION_APPLICATION_2_NAME}  labour costs  n/a  no

Finances overview shows as complete once all collaborators have marked as complete
    [Documentation]  IFS-3820
    Given the academic user marks finances as complete
    And log in as a different user          &{lead_applicant_credentials}
    When the user clicks the button/link    link = ${OPEN_COMPETITION_APPLICATION_2_NAME}
    Then the user should see the element    jQuery = li:contains("Finances overview") .task-status-complete

Finance summary has total marked as complete
    [Documentation]  IFS-3821
    Given the user clicks the button/link    link = Finances overview
    Then the user should see the element     css = .table-total-tick[src*="icon-tick"]

Alert shows If the academic research participation is too high
    [Documentation]    INFUND-1436
    [Tags]
    [Setup]  logout as user
    Given Login new application invite academic    ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation
    When log in as a different user                ${test_mailbox_one}+academictest@gmail.com  ${correct_password}
    Then the user navigates to Your-finances page  Academic robot test application
    And The user clicks the button/link            link = Your project costs
    When the user enters text to a text field      css = [name$="incurredStaff"]  1000000
    And log in as a different user                 &{lead_applicant_credentials}
    And the user navigates to the finance overview of the academic
    Then the user should see the element           jQuery = .warning-alert h2:contains("The participation levels of this project are not within the required range")
    And the user navigates to the page             ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Academic robot test application
    And the user clicks the button/link            link = Review and submit
    And the user expands the section               Finances summary
    Then the user should see the element           jQuery = .warning-alert h2:contains("The participation levels of this project are not within the required range")

Alert should not show If research participation is below the maximum level
    [Documentation]    INFUND-1436
    [Tags]
    When lead enters a valid research participation value
    And the user navigates to the finance overview of the academic
    Then the user should not see the element       jQuery = .warning-alert:contains("The participation levels of this project are not within the required range")
    And the user navigates to the page             ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link            link = Academic robot test application
    And the user clicks the button/link            link = Review and submit
    And the user expands the section               Finances summary
    Then the user should not see the element       jQuery = .warning-alert:contains("The participation levels of this project are not within the required range")

Support User can see the read only finance summary
    [Documentation]  IFS-401
    [Tags]  Support  HappyPath
    [Setup]  log in as a different user       &{support_user_credentials}
    Given the user navigates to the finances of the application
    When the user should see the element      jQuery = .project-cost-breakdown tbody tr:nth-of-type(1) th:contains("View finances")
    And The user clicks the button/link       link = View finances
    Then The finance summary table in Your project Finances has correct values for lead  £200,903  30%  57,803  2,468  140,632

Support User can see the read only view of collaborator Your project costs for Labour, Overhead Costs and Materials
    [Documentation]  IFS-401
    [Tags]  Support  HappyPath
    Given the user clicks the button/link  link = Your project costs
    When the user verifies labour, overhead costs and materials
    Then the user verifies captial usage, subcontracting, travel and other costs

Support User can see the read only view of Your organisation
    [Documentation]  IFS-401
    [Tags]  Support
    When the user clicks the button/link           jQuery = a:contains("Your project finances")
    Then the user should see the element           css = .your-finances > p  # Please complete your project finances.
    When the user clicks the button/link           link = Your organisation
    Then the user should see the element           jQuery = dt:contains("Size") + dd:contains("Micro")
    And the user should see the element            jQuery = dt:contains("Turnover") + dd:contains("0")

Support User can see the read only view of Your funding
    [Documentation]  IFS-401
    [Tags]  Support
    Given the user navigates to the page  ${server}/management/competition/${openCompetitionRTO}/application/${application_ids["Water balance creates a threshold in soil pH at the global scale"]}
    And the user expands the section      Finances summary
    Then the user clicks the button/link  link = View finances
    When the user clicks the button/link  jQuery = a:contains("Your funding")
    Then the user should see the element  jQuery = dt:contains("Funding level") + dd:contains("30%")
    And the user should see the element   jQuery = th:contains("Lottery") ~ td:contains("£2,468")

Innovation lead can see read only summary link for each partner
    [Documentation]  IFS-802
    [Tags]  InnovationLead  HappyPath
    [Setup]  log in as a different user     &{innovation_lead_two}
    When the user navigates to the page     ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/applications/submitted
    And the user clicks the button/link     link = ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    And the user expands the section        Finances summary
    Then the user should see the element    jQuery = .project-cost-breakdown tr:contains("${EMPIRE_LTD_NAME}"):contains("View finances")
    And the user should see the element     jQuery = .project-cost-breakdown tr:contains("Ludlow"):contains("View finances")
    And the user should see the element     jQuery = .project-cost-breakdown tr:contains("EGGS"):contains("View finances")

Innovation lead can see read only summary for lead
    [Documentation]  IFS-802
    [Tags]  InnovationLead  HappyPath
    [Setup]  The user clicks the button/link          css = .project-cost-breakdown tbody tr:nth-of-type(1) th a
    When the user should see the element              jQuery = p:contains("Please complete your project finances.")
    Then the finance summary table in Your project Finances has correct values for lead  £200,903  30%  57,803  2,468  140,632

Innovation lead can see read only summary for collaborator
    [Documentation]  IFS-802
    [Tags]  InnovationLead  HappyPath
    When the user navigates to the page             ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/applications/submitted
    And the user clicks the button/link             link = ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    And the user expands the section                Finances summary
    When the user clicks the button/link            jQuery = .project-cost-breakdown tbody tr:contains("EGGS") th a
    And the user should see the element             jQuery = p:contains("Please complete your project finances.")
    Then the finance summary table in Your project Finances has correct values for collaborator  £990  100  0  2,468  0

Innovation lead can see read only view of collaborator Your project costs for Labour, Overhead Costs and Materials
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user navigates to the page             ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/applications/submitted
    And the user clicks the button/link             link = ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    And the user expands the section                Finances summary
    When the user clicks the button/link            jQuery = .project-cost-breakdown tbody tr:contains("Ludlow") th a
    Then the user should see the element            jQuery = p:contains("Please complete your project finances.")
    When the user clicks the button/link            jQuery = a:contains("Your project costs")
    And the user should see the element             jQuery = h2:contains("Provide the project costs for 'Ludlow'")
    When User verifies labour, overhead costs and materials for innovation lead
    Then User verifies captial usage, subcontracting, travel and other costs for innovation lead

Innovation lead can see read only view of Your organisation
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user clicks the button/link           jQuery = a:contains("Your project finances")
    Then the user should see the element           jQuery = p:contains("Please complete your project finances.")
    When the user clicks the button/link           jQuery = a:contains("Your organisation")
    Then the user should see the element           jQuery = dt:contains("Size") + dd:contains("Micro")
    And the user should see the element            jQuery = dt:contains("employees") + dd:contains("4560")

Innovation lead can see read only view of Your funding
    [Documentation]  IFS-802
    [Tags]  InnovationLead
    When the user clicks the button/link           jQuery = a:contains("Your project finances")
    Then the user should see the element           jQuery = p:contains("Please complete your project finances.")
    When the user clicks the button/link           jQuery = a:contains("Your funding")
    Then the user should see the element           jQuery = dt:contains("Funding level") + dd:contains("30%")
    And the user should see the element            jQuery = th:contains("Lottery") ~ td:contains("£2,468")

IFS Admin views the finance summary
    [Documentation]  IFS-3609
    [Tags]  HappyPath
    [Setup]  log in as a different user     &{ifs_admin_user_credentials}
    Given the user navigates to the finances of the application
    When the user clicks the button/link    link = View finances
    Then the finance summary table in Your project Finances has correct values for lead    £200,903  30%  57,803  2,468  140,632

A user other than an CSS or IFS Admin cannot view the finances of an application that has not yet been submitted
    [Documentation]  IFS-3609
    [Setup]  log in as a different user         &{internal_finance_credentials}
    Given the user navigates to the finances of the application
    Then the user should not see the element    jQuery = a:contains("View finances")

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser  &{lead_applicant_credentials}
    Connect to database  @{database}

the finance summary calculations should be correct
    the user should see the element  jQuery = .finance-summary tbody tr:last-of-type:contains("£328,571")
    the user should see the element  jQuery = .finance-summary tbody tr:last-of-type:contains("57,803")
    the user should see the element  jQuery = .finance-summary tbody tr:last-of-type:contains("504,936")
    the user should see the element  jQuery = .finance-summary tbody tr:last-of-type:contains("140,632")

the finance Funding breakdown calculations should be correct
    the user should see the element  jQuery = .project-cost-breakdown th:contains("${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}") + td:contains("£126,678")
    the user should see the element  jQuery = .project-cost-breakdown th:contains("${organisationLudlowName}") + td:contains("£200,903")
    the user should see the element  jQuery = .project-cost-breakdown th:contains("${organisationEggsName}") + td:contains("£990")
    the user should see the element  jQuery = .project-cost-breakdown th:contains("Total") + td:contains("£328,571")

the finance summary table in Your project Finances has correct values for lead
    [Arguments]  ${project_costs}  ${grant}  ${funding_sought}  ${other_funding}  ${contribution}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(1)  Total project costs
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(1)  ${project_costs}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(2)  Funding level (%)
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(2)  ${grant}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(3)  Funding sought
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(3)  ${funding_sought}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(4)  Other public sector funding
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(4)  ${other_funding}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(5)  Contribution to project
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(5)  ${contribution}

the finance summary table in Your project Finances has correct values for collaborator
    [Arguments]  ${project_costs}  ${grant}  ${funding_sought}  ${other_funding}  ${contribution}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(1)  Total project costs
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(1)  ${project_costs}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(2)  Funding level (%)
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(2)  ${grant}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(3)  Funding sought
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(3)  ${funding_sought}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(4)  Other public sector funding
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(4)  ${other_funding}
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) th:nth-of-type(5)  Contribution to project
    the user sees the text in the element  css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(5)  ${contribution}

the contribution to project and funding sought should be 0 and not a negative number
    the user navigates to Your-finances page  ${OPEN_COMPETITION_APPLICATION_2_NAME}
    the user sees the text in the element     css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(3)  0
    the user sees the text in the element     css = .govuk-form-group tr:nth-of-type(1) td:nth-of-type(5)  0

Green check should be visible
    Page Should Contain Image  css = .finance-summary tr:nth-of-type(1) img[src*="/images/ifs-images/icons/icon-tick"]

the red warning should be visible
    the user should see the element  jQuery = .warning-alert h2:contains("not marked their finances as complete")

Lead enters a valid research participation value
    the user navigates to Your-finances page  Academic robot test application
    the user clicks the button/link                   link = Your project costs
    run keyword and ignore error without screenshots  the user clicks the button/link  jQuery = .button-clear:contains("Edit")
    the user clicks the button/link                   jQuery = button:contains("Labour")
    the user should see the element                   name = add_cost
    the user clicks the button/link                   jQuery = button:contains('Add another role')
    the user should see the element                   css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field              css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input  Test
    wait for autosave
    The user enters text to a text field              css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input  1200000000
    wait for autosave
    the user enters text to a text field              css = #labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input  1000
    wait for autosave
    the user selects the checkbox                     stateAidAgreed
    the user clicks the button/link                   jQuery = button:contains('Mark as complete')
    wait for autosave

the user checks Your Funding section for the project
    [Arguments]  ${Application}
    the user clicks the button/link  link = Your funding
    ${Research_category_selected} =   run keyword and return status without screenshots  Element Should Be Visible  link = Your funding
    Run Keyword if  '${Research_category_selected}' == 'False'  the user selects research area via Your Funding section  ${Application}
    Run Keyword if  '${Research_category_selected}' == 'True'  the user fills in the funding information with bigger amount  ${Application}

the user selects research area via Your Funding section
    [Arguments]  ${Application}
    the applicant completes the application details  ${Application}  ${tomorrowday}  ${month}  ${nextyear}
    then the user selects research category          Feasibility studies
    the user fills in the funding information with bigger amount  ${Application}

the user fills in the funding information with bigger amount
    [Documentation]    Check if the Contribution to project and the Funding sought remain £0 and not minus
    [Arguments]  ${Application}
    the user navigates to Your-finances page  ${Application}
    the user clicks the button/link           link = Your funding
    the user selects the radio button         requestingFunding   true
    the user enters text to a text field      css = [name^="grantClaimPercentage"]  30
    click element                             jQuery = label:contains("Yes")
    the user enters text to a text field      css = #other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(3) input  8000000
    the user enters text to a text field      css = #other-funding-table tbody tr:nth-of-type(1) td:nth-of-type(1) input  test2
    the user selects the checkbox             agree-terms-page
    the user clicks the button/link           jQuery = button:contains("Mark as complete")

User verifies captial usage, subcontracting, travel and other costs for innovation lead
    the user expands the section     Capital usage
    the user should see the element  jQuery = #capital-usage-table td:contains("Depreciating Stuff") + td:contains("Existing") + td:contains("12") + td:contains("2,120")
    the user should see the element  jQuery = #capital-usage-table td:contains("Depreciating Stuff") ~ td:contains("1,200") + td:contains("60") + td:contains("£552")
    the user collapses the section   Capital usage
    the user expands the section     Subcontracting costs
    the user should see the element  jQuery = #subcontracting-table td:contains("Developers") + td:contains("UK") + td:contains("To develop stuff") + td:contains("£90,000")
    the user collapses the section   Subcontracting costs
    the user expands the section     Travel and subsistence
    the user should see the element  jQuery = #travel-costs-table td:contains("To visit colleagues") + td:contains("15") + td:contains("398") + td:contains("£5,970")
    the user collapses the section   Travel and subsistence
    the user expands the section     Other costs
    the user should see the element  jQuery = #other-costs-table td:contains("Some more costs") + td:contains("1,100")
    the user collapses the section   Other costs

User verifies labour, overhead costs and materials for innovation lead
    the user expands the section     Labour
    the user should see the element  jQuery = dt:contains("Working days per year") ~ dd:contains("123")
    the user should see the element  jQuery = .labour-costs-table td:contains("Role 1") ~ td:contains("200") ~ td:contains("325")
    the user collapses the section   Labour
    the user expands the section     Overhead costs
    the user should see the element  jQuery = #accordion-finances-content-2 span:contains("No overhead costs")
    the user expands the section     Overhead costs
    the user collapses the section   Overhead costs
    the user expands the section     Materials
    the user should see the element  jQuery = #material-costs-table td:contains("Generator") + td:contains("10") + td:contains("10,020") + td:contains("100,200")
    the user collapses the section   Materials

the user verifies captial usage, subcontracting, travel and other costs
    the user expands the section     Capital usage
    the user should see the element  jQuery = #capital-usage-table td:contains("Depreciating Stuff") + td:contains("Existing") + td:contains("12") + td:contains("2,120")
    the user collapses the section   Capital usage
    the user expands the section     Subcontracting costs
    the user should see the element  jQuery = #subcontracting-table td:contains("Developers") + td:contains("UK") + td:contains("To develop stuff") + td:contains("£90,000")
    the user collapses the section   Subcontracting costs
    the user expands the section     Travel and subsistence
    the user should see the element  jQuery = #travel-costs-table td:contains("To visit colleagues") + td:contains("15") + td:contains("398") + td:contains("£5,970")
    the user collapses the section   Travel and subsistence
    the user expands the section     Other costs
    the user should see the element  jQuery = #other-costs-table td:contains("Some more costs") + td:contains("1,100")
    the user collapses the section   Other costs

The user verifies labour, overhead costs and materials
    the user expands the section     Labour
    the user should see the element  jQuery = dt:contains("Working days per year") ~ dd:contains("123")
    the user should see the element  jQuery = .labour-costs-table td:contains("Role 1") ~ td:contains("200") ~ td:contains("2")
    the user collapses the section   Labour
    the user should see the element  jQuery = #accordion-finances-heading-2 span:contains("£0")
    the user expands the section     Materials
    the user should see the element  jQuery = #material-costs-table td:contains("Generator") + td:contains("10") + td:contains("10,020") + td:contains("£100,200")
    the user collapses the section   Materials

the user navigates to the finances of the application
    the user navigates to the page   ${allApplicationsForRTOComp}
    the user clicks the button/link  link = ${createApplicationOpenCompetitionApplication1Number}
    the user expands the section     Finances summary

the academic user marks finances as complete
    log in as a different user                 &{collaborator2_credentials}
    the user navigates to Your-finances page   ${OPEN_COMPETITION_APPLICATION_2_NAME}
    the user clicks the button/link            link = Your project costs
    the user selects the checkbox              termsAgreed
    the user clicks the button/link            jQuery = button:contains("Mark as complete")
    the user enters the project location
    the user clicks the button/link            link = Your funding
    the user marks your funding section as complete

Custom suite teardown
    Close browser and delete emails
    Disconnect from database
