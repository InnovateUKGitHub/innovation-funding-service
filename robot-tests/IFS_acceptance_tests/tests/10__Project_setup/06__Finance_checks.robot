*** Settings ***
Documentation     INFUND-5190 As a member of Project Finance I want to view an amended Finance Checks summary page so that I can see the projects and organisations requiring Finance Checks for the Private Beta competition
...
...               INFUND-5193 As a member of Project Finance I want to be able to approve the finance details that have been updated in the Finance Checks so that these details can be used to generate the default spend profile
...
...               INFUND-5220 As a member of Project Finance I want to be able to view project costs for academic organisations so that I can review funding during the Finance Checks for the Private Beta competition
...
...               INFUND-5852 As a Project Finance team member I want a link to create the export of bank details for a competition so that this can be delivered to Finance for entry into the Innovate UK Finance SUN system
...
...               INFUND-6149 mailto link is broken on the internal finance eligibility page
...
...               INFUND-7016 Finance checks page is missing Project title
...
...               INFUND-7026 For internal user, in finance checks RAG is not N/A in case of academic
...
...               INFUND-4822 As a project finance team member I want to be able to view a summary of progress through the finance checks section for each partner so I can review and navigate to the sections
...
...               INFUND-4829 As a project finance team member I want to be able to confirm whether a full credit report has been used to confirm an applicant organisation's viability for funding so that this may be kept on record as part of the decision-making process
...
...               INFUND-4831 As a project finance team member I want to be able to confirm that the partner organisation is viable for funding so that no further viability checks need be carried out
...
...               INFUND-4856 As a project finance team member I want to be able to view the RAG rating indicating the effort level carried out for the viability checks of each partner organisation so that I can appraise colleagues who may be expected to carry out future checks.
...
...               INFUND-7076 Generate spend profile available before Viability checks are all approved or N/A
...
...               INFUND-7095 Create NOT_APPLICABLE Viability state (and set for Academic Orgs upon Project creation)
...
...               INFUND-4830 As a project finance team member I want to be able to confirm that the appropriate viability finance checks have been carried out so I can approve the partner organisation as viable for funding
...
...               INFUND-4825 As a project finance team member I want to view details of each partner organisation so I can review their viability for funding
...
...               INFUND-7613 Date and user stamp not showing
...
...               INFUND-4820 As a project finance team member I want a page containing summary information for each project so that I can manage the Finance Checks section for each project in Project Setup
...
...               INFUND-7718 Content: Breadcrumb content for main project page to projects in setup is incorrect
...
...               INFUND-4832 As a project finance team member I want to view details of the requested funding for each partner organisation so I can review their eligibility for funding
...
...               INFUND-4834 As a project finance team member I want to be able to amend the details stored in Finance Checks for a partner organisation so that I can ensure the detailed finances are appropriate for the project to meet funding eligibility requirements
...
...               INFUND-4833 As a project finance team member I want to be able to view partner finance details supplied in the application form so that I can review or edit them if appropriate
...
...               INFUND-4839 As a project finance team member I want to be able to confirm the partner organisation is eligible for funding so that no further eligibility checks need to be carried out
...
...               INFUND-4823 As a project finance team member I want to be able to view the RAG rating for the viability and eligibility of each partner organisation if available so that I can be appraised of the effort level that may be expected to carry out the finance checks.
...
...               INFUND-7573 Partner view - main page - Finance Checks
...
...               INFUND-5508 As a member of Project Finance I want to see the Finance Checks Overview table updating with approved funding amounts so that I can confirm any amended figures before generating the Spend Profile
...
...               INFUND-7574 Partner view updated finances - Finance Checks Eligibility
...
...               INFUND-7577 Finance Checks - Overheads displayed in the expanded Overheads section of the partner’s project finances and Project Finance user can Edit, Save, Change selection from 0% to 20% to Calculate overhead, contains spreadsheet when uploaded
...
...               INFUND-7578 Organisation details - Headcount and Turnover
...
...               INFUND-8787 The Finance checks status in the external Project Setup dashboard.
...
...               INFUND-4846 As a Project finance team member, I want to view Finance overview and Finances summary for the consortium
...
...               INFUND-4837 Project finance team member able to view all originally submitted details of all partners against the revisions made during the Finance Checks eligibility section to make a clear comparison
...
...               INFUND-8778 Partners do not need to see percentages in the Finance checks section of PS, only financial sub-totals and total-costs are to be seen
...
...               INFUND-8880 Read only Detailed finances table for external user and View finances link should be missing for academic users
...
...               INFUND-9517 I can view and save the viability page in project setup management
...
...               INFUND-8501 As partner I want to be able to view all originally submitted application finance details against the revisions made during the Finance Checks eligibility section so that I can make a clear comparison
...
...               IFS-236 Queries - do not post until the Finance contact can view and respond to it within the service.
...
...               INFUND-7579 Maximum research participation exceeded
...
...               INFUND-7580 The participation levels of this project are within the required range
...
...               INFUND-654 Project Finance user has approved viability but date stamp is incorrect

Suite Setup       Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          PS_Common.robot
Resource          ../04__Applicant/Applicant_Commons.robot

*** Variables ***
${ELBOW_GREASE_PROJECT}    ${getProjectId("Elbow grease")}

*** Test Cases ***
Project Finance user can see the finance check summary page
    [Documentation]    INFUND-4821, INFUND-5476, INFUND-5507, INFUND-7016, INFUND-4820, INFUND-7718
    [Tags]  HappyPath
    [Setup]    Log in as a different user        &{internal_finance_credentials}
    Given the user navigates to the page          ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then the user should see the element          jQuery=table.table-progress
    And the user should see the element          jQuery=h2:contains("Finance checks")
    And the user should see the text in the page  Overview
    And the user should see the text in the page    ${funders_panel_application_1_title}
    And the table row has expected values
    And the user should see the element    link=Projects in setup

Project finance user cannot view viability section if this is not applicable for the org in question
    [Documentation]    INFUND-9517
    [Tags]
    When the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}/viability    ${404_error_message}

Status of the Eligibility column (workaround for private beta competition)
    [Documentation]    INFUND-5190
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then The user should see the text in the page    Viability
    And The user should see the text in the page    Queries raised
    And The user should see the text in the page    Notes
    When the user should see the element    link=Review
    Then the user should see that the element is disabled    jQuery=.generate-spend-profile-main-button

# Leaving this query test here as it has to be done before finance contacts and bank details are filled in
Query section is disabled before finance contacts have been selected
    [Documentation]    IFS-236
    [Tags]    HappyPath
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}/eligibility
    And the user clicks the button/link    jQuery=.button:contains("Queries")
    Then the user should see the element    jQuery=.button:contains("Post a new query")[disabled]
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}/eligibility
    And the user clicks the button/link    jQuery=.button:contains("Queries")
    Then the user should see the element    jQuery=.button:contains("Post a new query")[disabled]
    [Teardown]    finance contacts are selected and bank details are approved

Project Finance user can view academic Jes form
    [Documentation]     INFUND-5220
    [Tags]    HappyPath
    # note that we are viewing the file above rather than the same project as the other tests in this suite due to INFUND-6724
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    css=a.eligibility-1
    Then the user should see the text in the page    Download Je-S form
    When the user opens the link in new window   jes-form121.pdf
    Then the user goes back to the previous tab
    [Teardown]    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check

Project finance can see the within limit research participation level
    [Documentation]    INFUND-7580
    [Tags]
    When the user clicks the button/link  link=Project finance overview
    Then the user should see the text in the element   css=.list-eligibility dt:nth-of-type(1)   Maximum research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(1)    100 %
    And the user should see the text in the element    css=.list-eligibility dt:nth-of-type(2)    Current research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(2)    0.2 %
    And the user should see the text in the page       The research participation levels of this project are within the required range.
    When the user clicks the button/link               link=Finance checks
    And the user should not see the text in the page   The research participation levels of this project are within the required range.

Proj finance can see the maximum research participation level
    [Documentation]    INFUND-7579
    [Tags]
    When the user navigates to the page                ${server}/project-setup-management/project/${ELBOW_GREASE_PROJECT}/finance-check
    Then the user should see the text in the element   css=.list-eligibility dt:nth-of-type(1)   Maximum research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(1)    50 %
    And the user should see the text in the element    css=.list-eligibility dt:nth-of-type(2)    Current research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(2)    57.34 %
    And the user should see the text in the page       Maximum research participation exceeded
    When the user clicks the button/link               link=Project finance overview
    Then the user should see the text in the element   css=.list-eligibility dt:nth-of-type(1)   Maximum research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(1)    50 %
    And the user should see the text in the element    css=.list-eligibility dt:nth-of-type(2)    Current research participation
    And the user should see the text in the element    css=.list-eligibility dd:nth-of-type(2)    57.34 %
    And the user should see the text in the page       Maximum research participation exceeded
    And the user should see the text in the page       Please seek confirmation that the project is still eligible for funding.
    When the user clicks the button/link               link=Finance checks
    And the user should see the text in the page        Maximum research participation exceeded

Timestamp approval verification for viability and eligibility
    [Documentation]    INFUND-654
    [Tags]
    ${today}  get today
    Given the user clicks the button/link                    jQuery=table.table-progress a.viability-0
    And the user selects the checkbox                        project-viable
    And the user selects the option from the drop-down menu  Green  id=rag-rating
    And the user selects the checkbox                        creditReportConfirmed
    And the user clicks the button/link                      css=#confirm-button
    And the user clicks the button/link                      name=confirm-viability
    Then the user should see the text in the page            The partner's finance viability has been approved by Lee Bowman, ${today}
    When the user clicks the button/link                     link=Finance checks
    When the user clicks the button/link                     jQuery=table.table-progress a.eligibility-0
    And the user selects the checkbox                        project-eligible
    And the user selects the option from the drop-down menu  Green  id=rag-rating
    And the user selects the checkbox                        creditReportConfirmed
    And the user clicks the button/link                      css=#confirm-button
    And the user clicks the button/link                      name=confirm-eligibility
    Then the user should see the text in the page            The partner's finance eligibility has been approved by Lee Bowman, ${today}

External users can view finance checks status on dashboard
    [Documentation]    INFUND-4843, INFUND-8787
    [Tags]
    [Setup]    the user navigates to the page       ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Given log in as a different user        &{lead_applicant_credentials}  #Non finance contact
    Then check finance checks status on dashboard  waiting  Awaiting review
    When log in as a different user        &{collaborator2_credentials}   #Academic user
    Then check finance checks status on dashboard   waiting  Awaiting review
    When log in as a different user        &{collaborator1_credentials}   #Non Lead Partner
    Then check finance checks status on dashboard   waiting  Awaiting review
    When log in as a different user        &{successful_applicant_credentials}  #finance contact
    Then check finance checks status on dashboard  waiting  Awaiting review

Project finance user can view finance overview for the consortium
    [Documentation]    INFUND-4846
    [Tags]
    log in as a different user              &{internal_finance_credentials}
    When the user navigates to the page     ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    link=Project finance overview
    Then the user should see the element    jQuery=h1:contains("Finance overview")
    # the below figures are listed as:       RowNumber  StartDate      Duration    TotalProjectCost    GrantAppliedFor     OtherPublicSectorFunding    Total%Grant
    And the categories are verified for Overview section    1   1 Oct 2020  3 months    £ 503,248   £ 145,497    £ 6,170     29%

Project finance user can view finances summary for the consortium
    [Documentation]    INFUND-4846
    [Tags]
    Given the user should see the element                          jQuery=h3:contains("Finances summary")
    #Check finances summary for lead partner
    Then the user should see the text in the element               jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(1) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    And the Categories Are Verified For Finances Summary Section    1   £ 301,355   30%     £ 90,406    £ 3,702     £ 207,246
    #Check finances summary for academic user
    When the user should see the text in the element               jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(2) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    Then the Categories Are Verified For Finances Summary Section   2   £ 990   0%  £ 0     £ 0     £ 990
    #Check finances summary for non lead partner
    When the user should see the text in the element               jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(3) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    Then the Categories Are Verified For Finances Summary Section   3   £ 200,903   30%     £ 60,271    £ 2,468     £ 138,164
    #Check total
    When the user should see the text in the element               jQuery=h3:contains("Finances summary") + * tfoot tr:nth-of-type(1) th:nth-of-type(1)     Total
    And The Total Calculation For Finances Summary Are Verified     1   £ 503,248   £ 150,677    £ 6,170     £ 346,401

Project finance can see finance breakdown for different categories
    [Documentation]    INFUND-4846
    [Tags]
    Given the user navigates to the page                      ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link                      link=Project finance overview
    #Check finances summary for lead partner
    Then the user should see the text in the element          css=.form-group tbody tr:nth-of-type(1) th strong  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    # the below figures are in this order Total 	Labour 	Overheads 	Materials 	Capital usage 	Subcontracting cost  Travel and subsistence  Other costs
    And all the categories are verified   1   £ 301,355  £ 4,622  £ 0  £ 150,300  £ 828  £ 135,000  £ 8,955  £ 1,650
    #Check finances summary for academic user
    When the user should see the text in the element   css=.form-group tbody tr:nth-of-type(2) th strong  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    Then all the categories are verified  2   £ 990      £ 286 	 £ 154 	£ 66     £ 0    £ 0        £ 44     £ 440
    #Check finances summary for non lead partner
    When the user should see the text in the element   css=.form-group tbody tr:nth-of-type(3) th strong  ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    Then all the categories are verified  3   £ 200,903 	£ 3,081   £ 0   £ 100,200  £ 552  £ 90,000   £ 5,970  £ 1,100
    #Check total
    And the user should see the text in the element  css=.form-group tfoot tr:nth-of-type(1) td:nth-of-type(1) strong   	£ 503,248

IFS Admin user can review Lead partner's finance changes page before the revisions made
    [Documentation]    INFUND-4837, IFS-603
    [Tags]
    [Setup]  log in as a different user     &{ifs_admin_user_credentials}
    Given the user navigates to the page       ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link        css=a.eligibility-0
    And the user clicks the button/link         link=Review all changes to project finances
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    Then the categories are verified for Project finances section   1   £ 301,355   30%     £ 90,406    £ 3,702     £ 207,246
    # the below figures are listed as:     RowNumber   Labour    Overheads     Materials 	CapitalUsage    Subcontracting     TravelandSubsistence    OtherCosts
    And the categories are verified for Section changes    1   £ 0     £ 0      £ 0    £ 0      £ 0       £ 0        £ 0
    And the user should see the text in the element   css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(1)   Overall
    And the user should see the text in the element    css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(2)   0
    And the user clicks the button/link     jQuery=.button-secondary:contains("Return to eligibility")

IFS Admin user can review partner's finances before the revisions made
    [Documentation]    INFUND-4837, IFS-603
    [Tags]
    Given the user clicks the button/link       link=Finance checks
    When the user clicks the button/link        css=a.eligibility-2
    Then the user clicks the button/link        link=Review all changes to project finances
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    And the categories are verified for Project finances section   1   £ 200,903   30%     £ 60,271    £ 2,468     £ 138,164
    # the below figures are listed as:     RowNumber   Labour    Overheads     Materials 	CapitalUsage    Subcontracting     TravelandSubsistence    OtherCosts
    And the categories are verified for Section changes    1   £ 0     £ 0      £ 0    £ 0      £ 0       £ 0        £ 0
    And the user should see the text in the element   css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(1)   Overall
    And the user should see the text in the element    css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(2)   0

Lead Partner can review the external version of Finance Checks eligibility table
    [Documentation]    INFUND-8778, INFUND-8880
    [Tags]
    Given log in as a different user        &{lead_applicant_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user clicks the button/link    link=Finance checks
    When the user clicks the button/link    link=View finances
    Then the user should see the element    jQuery=h2:contains("Detailed finances")
    And the user verifies the financial sub-totals for external version under the Detailed-finances     £ 4,622    £ 0     £ 150,300    £ 828    £ 135,000    £ 8,955     £ 1,650
    Then the user should see the element    css=input[id="total-cost"][value="£ 301,355"]
    And the user clicks the button/link     link=Finance checks

Partner can review only the external version of Finance Checks eligibility table
    [Documentation]    INFUND-8778, INFUND-8880
    [Tags]
    Given log in as a different user        &{collaborator1_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user clicks the button/link    link=Finance checks
    When the user clicks the button/link    link=View finances
    Then the user should see the element    jQuery=h2:contains("Detailed finances")
    And the user verifies the financial sub-totals for external version under the Detailed-finances     £ 3,081    £ 0     £ 100,200    £ 552    £ 90,000    £ 5,970     £ 1,100
    Then the user should see the element    css=input[id="total-cost"][value="£ 200,903"]
    And the user clicks the button/link     link=Finance checks

Viability checks are populated in the table
    [Documentation]    INFUND-4822, INFUND-7095, INFUND-8778
    [Tags]
    Given log in as a different user    &{ifs_admin_user_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(3)    Not set
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2)    N/A
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(3)    N/A
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(3)    Not set

IFS Admin user can see the viability check page for the lead partner
    [Documentation]    INFUND-4831, INFUND-4830, INFUND-4825
    [Tags]
    [Setup]  log in as a different user    &{internal_finance_credentials}
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")    # clicking the review button for the lead partner
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_COMPANY_NUMBER}
    And the user should see the text in the element  css=.standard-definition-list dd:nth-of-type(3)  ${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_TURNOVER}   #turnover
    And the user should see the text in the element    css=.standard-definition-list dd:nth-of-type(4)  ${PROJECT_SETUP_APPLICATION_1_LEAD_COMPANY_HEADCOUNT}    #headcount

Project finance user can see the lead partner's information
    [Documentation]    INFUND-4825
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(1)    £301,355
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(2)    30%
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £210,948
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £86,704
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(5)    £3,702

Checking the approve viability checkbox enables RAG selection but not confirm viability button
    [Documentation]    INFUND-4831, INFUND-4856, INFUND-4830
    [Tags]
    When the user selects the checkbox    project-viable
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Confirm viability")

RAG choices update on the finance checks page
    [Documentation]    INFUND-4822, INFUND-4856
    [Tags]
    When the rag rating updates on the finance check page for lead for viability   Green
    And the rag rating updates on the finance check page for lead for viability   Amber
    And the rag rating updates on the finance check page for lead for viability   Red
    When the user selects the option from the drop-down menu    --    id=rag-rating
    Then the user should see the element    jQuery=.button.disabled:contains("Confirm viability")
    [Teardown]    the user selects the option from the drop-down menu    Green    id=rag-rating

Credit report information saves when leaving the page
    [Documentation]    INFUND-4829
    [Tags]
    When the user selects the checkbox    creditReportConfirmed
    And the user clicks the button/link    jQuery=.button-secondary:contains("Save and return to finance checks")
    And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
    Then checkbox should be selected    creditReportConfirmed

Clicking cancel on the viability modal
    [Documentation]    INFUND-4822, INFUND-4830
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    And the user clicks the button/link    jQuery=.buttonlink.js-close    # Clicking the cancel link on the modal
    Then the user should see the element    id=rag-rating
    And the user should see the element     css=[name="creditReportConfirmed"]:checked ~ label
    And the user should see the element     css=[name="confirmViabilityChecked"]:checked ~ label
    And the user should see the element    jQuery=.button-secondary:contains("Save and return to finance checks")

Confirming viability should show credit report info on a readonly page
    [Documentation]    INFUND-4829, INFUND-4830
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    And the user clicks the button/link    name=confirm-viability    # Clicking the confirm button on the modal
    Then the user should see the element    jQuery=.button-secondary:contains("Return to finance checks")
    And the user should not see the element    id=rag-rating
    And the user should not see the checkbox    confirmViabilityChecked
    And the user should see the text in the page    A credit report has been used together with the viability information shown here. This information is kept in accordance with Innovate UK audit requirements.
    And the user should see that the checkbox is disabled    creditReportConfirmed

Confirming viability should update on the finance checks page
    [Documentation]    INFUND-4831, INFUND-4822
    [Tags]
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Approved")

Project finance user can see the viability checks for the industrial partner
    [Documentation]    INFUND-4831, INFUND-4830, INFUND-7578
    [Tags]
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2) a:contains("Review")
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_NUMBER}
    And the user should see the text in the element  css=.standard-definition-list dd:nth-of-type(3)  ${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_TURNOVER}
    And the user should see the text in the element    css=.standard-definition-list dd:nth-of-type(4)  ${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_HEADCOUNT}

Checking the approve viability checkbox enables RAG selection but not confirm viability button for partner
    [Documentation]    INFUND-4831, INFUND-4856, INFUND-4830
    [Tags]
    When the user selects the checkbox    project-viable
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Confirm viability")

RAG choices update on the finance checks page for partner
    [Documentation]    INFUND-4822, INFUND-4856
    [Tags]
    When the rag rating updates on the finance check page for partner for viability    Green
    And the rag rating updates on the finance check page for partner for viability      Amber
    And the rag rating updates on the finance check page for partner for viability      Red
    When the user selects the option from the drop-down menu    --    id=rag-rating
    Then the user should see the element    jQuery=.button.disabled:contains("Confirm viability")
    [Teardown]    the user selects the option from the drop-down menu    Green    id=rag-rating

Credit report information saves when leaving the page for partner
    [Documentation]    INFUND-4829
    [Tags]
    When the user selects the checkbox    creditReportConfirmed
    And the user clicks the button/link    jQuery=.button-secondary:contains("Save and return to finance checks")
    And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2) a:contains("Review")
    Then checkbox should be selected    creditReportConfirmed

Clicking cancel on the viability modal for partner
    [Documentation]    INFUND-4822, INFUND-4830
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    And the user clicks the button/link    jQuery=.buttonlink.js-close    # Clicking the cancel link on the modal
    Then the user should see the element    id=rag-rating
    And the user should see the element    css=[name="creditReportConfirmed"]:checked ~ label
    And the user should see the element    css=[name="confirmViabilityChecked"]:checked ~ label
    And the user should see the element    jQuery=.button-secondary:contains("Save and return to finance checks")

Confirming viability should show credit report info on a readonly page for partner
    [Documentation]    INFUND-4829, INFUND-4830
    [Tags]
    ${today} =  get today
    When the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    And the user clicks the button/link    name=confirm-viability    # Clicking the confirm button on the modal
    Then the user should see the element    jQuery=.button-secondary:contains("Return to finance checks")
    And the user should see the text in the page  The partner's finance viability has been approved by Lee Bowman, ${today}
    And the user should not see the element    id=rag-rating
    And the user should not see the checkbox    confirmViabilityChecked
    And the user should see the text in the page    A credit report has been used together with the viability information shown here. This information is kept in accordance with Innovate UK audit requirements.
    And the user should see that the checkbox is disabled    creditReportConfirmed

Confirming viability should update on the finance checks page for partner
    [Documentation]    INFUND-4831, INFUND-4822
    [Tags]
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2) a:contains("Approved")

Eligibility checks are populated in the table
    [Documentation]    INFUND-4823
    [Tags]
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(4)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(5)    Not set
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(4)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(5)    Not set
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(4)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(5)    Not set

Project finance user can see the Eligibility check page for the lead partner
    [Documentation]    INFUND-4823
    [Tags]
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")    # clicking the review button for the lead partner
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}

Project finance user can see the lead partner's information about eligibility
    [Documentation]    INFUND-4832
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(1)    3 months
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)    £ 301,355
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(3)    30%
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(4)    £ 90,406
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(5)    £ 3,702
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(6)    £ 207,246

Finance checks eligibility validations
    [Documentation]    INFUND-4833
    [Tags]
    When the user clicks the button/link             jQuery=section:nth-of-type(1) button:contains("Labour")
    And the user clicks the button/link              jQuery=section:nth-of-type(1) a:contains("Edit")
    When the user enters text to a text field        css=[name^="labour-labourDaysYearly"]    -230
    Then the user should see the text in the page    This field should be 1 or higher
    When the user clicks the button/link             jQuery=section:nth-of-type(1) button[name=save-eligibility]
    Then the user should see the text in the page    This field should be 1 or higher
    And the user clicks the button/link             jQuery=section:nth-of-type(1) button:contains("Labour")
    And the user reloads the page
    When the user clicks the button/link             jQuery=section:nth-of-type(3) button:contains("Materials")
    And the user clicks the button/link              jQuery=section:nth-of-type(3) a:contains("Edit")
    When the user clicks the button/link             jQuery=section:nth-of-type(3) button[name=add_cost]
    When the user enters text to a text field        css=#material-costs-table tbody tr:nth-of-type(4) td:nth-of-type(2) input    100
    And the user clicks the button/link              jQuery=section:nth-of-type(3) button[name=save-eligibility]
    Then the user should see the text in the page    This field cannot be left blank
    And the user clicks the button/link             jQuery=section:nth-of-type(3) button:contains("Materials")
    And the user reloads the page
    When the user clicks the button/link             jQuery=section:nth-of-type(4) button:contains("Capital usage")
    And the user clicks the button/link              jQuery=section:nth-of-type(4) a:contains("Edit")
    When the user enters text to a text field        css=section:nth-of-type(4) #capital_usage div:nth-child(1) div:nth-of-type(6) input   200
    Then the user should see the text in the page    This field should be 100 or lower
    And the user clicks the button/link             jQuery=section:nth-of-type(4) button:contains("Capital usage")
    And the user reloads the page
    When the user clicks the button/link             jQuery=section:nth-of-type(6) button:contains("Travel and subsistence")
    And the user clicks the button/link              jQuery=section:nth-of-type(6) a:contains("Edit")
    When the user clicks the button/link            jQuery=section:nth-of-type(6) button[name=add_cost]
    And the user enters text to a text field         css=#travel-costs-table tbody tr:nth-of-type(4) td:nth-of-type(2) input    123
    When the user clicks the button/link             jQuery=section:nth-of-type(6) button[name=save-eligibility]
    Then the user should see the text in the page     This field cannot be left blank
    And the user clicks the button/link             jQuery=section:nth-of-type(6) button:contains("Travel and subsistence")
    And the user reloads the page
    When the user clicks the button/link             jQuery=section:nth-of-type(7) button:contains("Other costs")
    And the user clicks the button/link              jQuery=section:nth-of-type(7) a:contains("Edit")
    When the user clicks the button/link            jQuery=section:nth-of-type(7) button[name=add_cost]
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(2) td:nth-child(2) input  5000
    When the user clicks the button/link           jQuery=section:nth-of-type(7) button[name=save-eligibility]
    Then the user should see the text in the page    This field cannot be left blank
    And the user clicks the button/link             jQuery=section:nth-of-type(7) button:contains("Other costs")
    When the user clicks the button/link             link=Finance checks
    When the user clicks the button/link             jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")

Project finance user can amend all sections of eligibility for lead
    [Documentation]    INFUND-4834
    [Tags]
    When Project finance user amends labour details in eligibility for lead
    And Project finance user amends materials details in eligibility for lead
    And Project finance user amends capital usage details in eligibility for lead
    And Project finance user amends subcontracting usage details in eligibility for lead
    And Project finance user amends travel details in eligibility for lead
    And Project finance user amends other costs details in eligibility for lead

Project Finance user can edit and save Lead Partner's 20% of labour costs option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button
    And the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-default-percentage"] label
    Then the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 12,120")
    And the user should see the element     jQuery=section:nth-of-type(2) input[id^="section-total"][id$="default"]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("20%")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 12,120")
    And the user should see the element     jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user should see the element     jQuery=input[id^="total-cost"][value="£ 217,034"]

Project Finance user can Edit and Save Lead Partner's no overhead costs option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-none"] label
    Then the user should see the element     jQuery=h3:contains("No overhead costs")
    And the user should see the element     jQuery=p:contains("You are not currently applying for overhead costs")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("0%")
    And the user should see the element     jQuery=section:nth-of-type(2) button:contains("£ 0")

Project Finance user can edit and save Lead Partner's calculate overheads option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-total"] label
    And the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("0%")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 0")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user enters text to a text field     jQuery=section:nth-of-type(2) input[id^="cost-overheads"][id$="calculate"]  ${empty}
    And the user clicks the button/link     jQuery=section:nth-of-type(2) button:contains("Save")
    And the user clicks the button/link    jQuery=.button-secondary:contains("Return to finance checks")

Project Finance user can enter overhead values for Lead Partner manually
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    css=a.eligibility-0
    And the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user enters text to a text field     jQuery=section:nth-of-type(2) input[id^="cost-overheads"][id$="calculate"]  1954
    Then the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 1,954")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("3%")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Overhead costs")
    And the user should see the element     jQuery=input[id^="total-cost"][value="£ 206,867"]

Checking the approve eligibility checkbox enables RAG selection but not Approve eligibility button
    [Documentation]    INFUND-4839
    [Tags]
    When the user selects the checkbox    project-eligible
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")

RAG choices update on the finance checks page for eligibility
    [Documentation]    INFUND-4839, INFUND-4823
    [Tags]
    When the rag rating updates on the finance check page for lead for eligibility   Green
    And the rag rating updates on the finance check page for lead for eligibility    Amber
    And the rag rating updates on the finance check page for lead for eligibility   Red
    When the user selects the option from the drop-down menu    --    id=rag-rating
    Then the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")

Clicking cancel on the eligibility modal
    [Documentation]    INFUND-4839
    [Tags]
    When the user selects the option from the drop-down menu    Green    id=rag-rating
    And the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.buttonlink.js-close    # Clicking the cancel link on the modal
    Then the user should see the element    id=rag-rating
    And the user should see the element    css=[id="project-eligible"]:checked ~ label
    And the user should see the element    jQuery=.button-secondary:contains("Return to finance checks")

Confirming eligibility should show info on a readonly page
    [Documentation]    INFUND-4839, INFUND-7574
    [Tags]
    ${today} =  get today
    When the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    name=confirm-eligibility    # Clicking the confirm button on the modal
    Then the user should see the element    jQuery=a.button-secondary:contains("Return to finance checks")
    And the user should see the text in the page  The partner's finance eligibility has been approved by Lee Bowman, ${today}
    And the user should not see the element    id=rag-rating
    And the user should not see the checkbox    project-eligible

Confirming eligibility should update on the finance checks page
    [Documentation]    INFUND-4823
    [Tags]
    When the user clicks the button/link    jQuery=.button-secondary:contains("Return to finance checks")
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Approved")

Proj Finance is able to see the Finances amended
    [Documentation]  INFUND-8501
    [Tags]
    Given the user navigates to the page  ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${EMPIRE_LTD_ID}/eligibility
    Then the user clicks the button/link  link=View changes to finances
    When the user should see the element  css=#project-finance-changes-total
    Then the user should see the element  css=#project-finance-changes-section
    And the user should see the element   css=#project-finance-changes-submitted
    When the user should see the element  jQuery=h2:contains("Changes from submitted finances")
    Then the user should see the finance values amended by internal user

Project finance user can see updated finance overview after lead changes to eligibility
    [Documentation]    INFUND-5508
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £ 408,760
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £ 117,151
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(6)    29%

Project finance user can see the Eligibility check page for the partner
    [Documentation]    INFUND-4823
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(4) a:contains("Review")
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}

# The below test deviates to a different project to check 0% funding for a partner
Project finance user can see the partner's zero funding request
    [Documentation]    INFUND-9269
    [Tags]
    When the user navigates to the page                 ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/organisation/${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}/eligibility
    Then the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(2)    £ 200,903   # Total costs
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)     0%          # % Grant
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)     £ 0         # Funding sought
    [Teardown]    the user navigates to the page        ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}/eligibility

Project finance user can see the partner's information about eligibility
    [Documentation]    INFUND-4832
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(1)    3 months
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)    £ 200,903
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(3)    30%
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(4)    £ 60,271
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(5)    £ 2,468
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(6)    £ 138,164

Project finance user can amend all sections of eligibility for partner
    [Documentation]    INFUND-4834
    [Tags]
    When Project finance user amends labour details in eligibility for partner
    And Project finance user amends materials details in eligibility for partner
    And Project finance user amends capital usage details in eligibility for partner
    And Project finance user amends subcontracting usage details in eligibility for partner
    And Project finance user amends travel details in eligibility for partner
    And Project finance user amends other costs details in eligibility for partner

Project Finance user can edit and save partner's 20% of labour costs option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button
    And the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-default-percentage"] label
    Then the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 11,956")
    And the user should see the element     jQuery=section:nth-of-type(2) input[id^="section-total"][id$="default"]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("20%")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 11,956")
    And the user should see the element     jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user should see the element     jQuery=input[id^="total-cost"][value="£ 117,134"]

Project Finance user can edit and save Partner's no overhead costs option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-none"] label
    Then the user should see the element     jQuery=h3:contains("No overhead costs")
    And the user should see the element     jQuery=p:contains("You are not currently applying for overhead costs")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("0%")
    And the user should see the element     jQuery=section:nth-of-type(2) button:contains("£ 0")

Project Finance user can edit and save in Partner's calculate overheads option
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user clicks the button/link    jQuery=[data-target="overhead-total"] label
    And the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    Then the user should see the element    jQuery=section:nth-of-type(2) button span:contains("0%")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 0")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user enters text to a text field     jQuery=section:nth-of-type(2) input[id^="cost-overheads"][id$="calculate"]  ${empty}
    And the user clicks the button/link     jQuery=section:nth-of-type(2) button:contains("Save")
    And the user clicks the button/link    jQuery=.button-secondary:contains("Return to finance checks")

Project Finance user can enter overhead values for partner manually
    [Documentation]     INFUND-7577
    [Tags]
    When the user clicks the button/link    css=a.eligibility-2
    And the user clicks the button/link    jQuery=section:nth-of-type(2) a:contains("Edit")
    And the user enters text to a text field     jQuery=section:nth-of-type(2) input[id^="cost-overheads"][id$="calculate"]  9078
    Then the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Save")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("£ 9,078")
    And the user should see the element     jQuery=section:nth-of-type(2) button span:contains("15%")
    When the user clicks the button/link    jQuery=section:nth-of-type(2) button:contains("Overhead costs")
    And the user should see the element     jQuery=input[id^="total-cost"][value="£ 114,256"]

Project finance user can see the eligibility checks for the industrial partner
    [Documentation]    INFUND-4823
    [Tags]
    When the user clicks the button/link   link=Finance checks
    And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(4) a:contains("Review")
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}

Checking the approve eligibility checkbox enables RAG selection but not confirm viability button for partner
    [Documentation]    INFUND-4839
    [Tags]
    When the user selects the checkbox    project-eligible
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")

RAG choices update on the finance checks page for eligibility for partner
    [Documentation]    INFUND-4839, INFUND-4823
    [Tags]
    When the rag rating updates on the finance check page for partner for eligibility   Green
    And the rag rating updates on the finance check page for partner for eligibility    Amber
    And the rag rating updates on the finance check page for partner for eligibility    Red
    When the user selects the option from the drop-down menu    --    id=rag-rating
    Then the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")

Clicking cancel on the eligibility modal for partner
    [Documentation]    INFUND-4839
    [Tags]
    When the user selects the option from the drop-down menu    Green    id=rag-rating
    And the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.buttonlink.js-close    # Clicking the cancel link on the modal
    Then the user should see the element    id=rag-rating
    And the user should see the element    css=[id="project-eligible"]:checked ~ label
    And the user should see the element    jQuery=.button-secondary:contains("Return to finance checks")

Confirming eligibility should show info on a readonly page for partner
    [Documentation]    INFUND-4839, INFUND-7574
    [Tags]
    ${today} =  get today
    When the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    name=confirm-eligibility    # Clicking the confirm button on the modal
    Then the user should see the element    jQuery=.button-secondary:contains("Return to finance checks")
    And the user should see the text in the page  The partner's finance eligibility has been approved by Lee Bowman, ${today}
    And the user should not see the element    id=rag-rating
    And the user should not see the checkbox    project-eligible

Confirming partner eligibility should update on the finance checks page
    [Documentation]    INFUND-4823, INFUND-7076
    [Tags]
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(4) a:contains("Approved")
    And The user should see the element    jQuery=.generate-spend-profile-main-button
    And the user should see the element    xpath=//*[@class='button generate-spend-profile-main-button' and @disabled='disabled']

Project finance user can see updated finance overview after partner changes to eligibility
    [Documentation]    INFUND-5508
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/
    Then the user should see the text in the element   jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £ 322,113
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £ 91,157
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(6)    28%

Project finance can see updated finance breakdown for different categories
    [Documentation]    INFUND-4846
    [Tags]
    When the user clicks the button/link   link=Project finance overview
    #check breakdown for lead partner
    Then the user should see the text in the element   css=.form-group tbody tr:nth-of-type(1) th strong  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    # the below figures are in this order Total 	Labour 	Overheads 	Materials 	Capital usage 	Subcontracting cost  Travel and subsistence  Other costs
    And all the categories are verified  1   £ 206,867 	 £ 60,602  £ 1,954 	£ 52,100   £ 10,376   £ 65,000  £ 4,985   £ 11,850
    #check breakdown for academic user
    When the user should see the text in the element   css=.form-group tbody tr:nth-of-type(2) th strong  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    Then all the categories are verified  2   £ 990 	     £ 286 	   £ 154    £ 66       £ 0 	      £ 0 	    £ 44      £ 440
    #check breakdown for non lead partner
    When the user should see the text in the element   css=.form-group tbody tr:nth-of-type(3) th strong  ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    Then all the categories are verified  3   £ 114,256   £ 59,778  £ 9,078  £ 2,000    £ 10,100   £ 20,000  £ 2,000   £ 11,300
    #Check total
    And the user should see the text in the element  css=.form-group tfoot tr:nth-of-type(1) td:nth-of-type(1) strong   	£ 322,113
    [Teardown]    the user navigates to the page       ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check

Project finance can approve academic eligibility
    [Documentation]    INFUND-4428
    [Tags]      HappyPath
    ${today} =  get today
    When the user clicks the button/link     jQuery=table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Review")
    Then the user should see the text in the page   Je-S Form overview
    When the user selects the checkbox    project-eligible
    And the user selects the option from the drop-down menu    Green    id=rag-rating
    And the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    name=confirm-eligibility    # Clicking the confirm button on the modal
    Then the user should see the text in the page  The partner's finance eligibility has been approved by Lee Bowman, ${today}
    And the user should not see the element    id=rag-rating
    And the user should not see the checkbox    project-eligible
    And the user clicks the button/link    link=Finance checks

Project finance user can view Updated finance overview for the consortium
    [Documentation]    INFUND-4846
    [Tags]
    When the user clicks the button/link    link=Project finance overview
    Then the user should see the element    jQuery=h1:contains("Finance overview")
    # the below figures are listed as:       RowNumber  StartDate      Duration    TotalProjectCost    GrantAppliedFor     OtherPublicSectorFunding    Total%Grant
    And the categories are verified for Overview section    1   1 Oct 2020  3 months    £ 322,113   £ 91,157    £ 6,170     28%

Project finance user can view updated finances summary for the consortium
    [Documentation]    INFUND-4846
    [Tags]
    Given the user should see the element   jQuery=h3:contains("Finances summary")
    #check summary for lead partner
    Then the user should see the text in the element    jQuery=h3:contains("Finances summary") + * table tbody tr:nth-of-type(1) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    And the Categories Are Verified For Finances Summary Section   1   £ 206,867   30%     £ 62,060    £ 3,702     £ 141,105
    #check breakdown for academic user
    When the user should see the text in the element    jQuery=h3:contains("Finances summary") + * table tbody tr:nth-of-type(2) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    Then the Categories Are Verified For Finances Summary Section   2   £ 990   0%  £ 0     £ 0     £ 990
    #check breakdown for non lead partner
    When the user should see the text in the element    jQuery=h3:contains("Finances summary") + * table tbody tr:nth-of-type(3) th:nth-of-type(1) strong      ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    Then the Categories Are Verified For Finances Summary Section   3   £ 114,256   30%     £ 34,277    £ 2,468     £ 77,511
    #check total
    And the user should see the text in the element    jQuery=h3:contains("Finances summary") + * table tfoot tr:nth-of-type(1) th:nth-of-type(1)     Total
    And The Total Calculation For Finances Summary Are Verified    1   £ 322,113   £ 96,337    £ 6,170     £ 219,606

Project finance user can view Lead Partner's changes to finances
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Finance checks
    When the user clicks the button/link        css=a.eligibility-0
    And the user clicks the button/link        link=View changes to finances
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    Then the categories are verified for Project finances section   1   £ 206,867   30%     £ 62,060    £ 3,702     £ 141,105
    # the below figures are listed as:     RowNumber   Labour    Overheads     Materials 	CapitalUsage    Subcontracting     TravelandSubsistence    OtherCosts
    And the categories are verified for Section changes    1   £ 55,980     £ 1,954      £ -98,200    £ 9,548      £ -70,000       £ -3,970        £ 10,200

#1.materials section
Project finance user can view Lead partner's changes for Materials
    [Documentation]    INFUND-4837
    [Tags]
    # the below figures are listed as:     RowNumber      Action      Section
    Given the user verifies the action and section for revised finances      16  Change  Materials
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section  18  Cost per item  5010  100
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     19  -49,100

#2.overheads section
Project finance user can view Lead partner's changes for Overheads
    [Documentation]    INFUND-4837
    [Tags]
    Given the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(13) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances       13  Change  Overheads
    And the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(14) th:nth-of-type(1)  Amount
    And the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(14) td:nth-of-type(2)  1954
    And the revised cost is verified for the specified section     15  0

#3.capital usage section
Project finance user can view Lead partner's changes for capital usage
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link                           link=Eligibility
    When the user clicks the button/link        link=View changes to finances
    And the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(28) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances       28    Change    Capital usage
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section       29     New or existing     Existing    New
    And the revised categories are verified for specified Section        31     Net present value   1060    10600
    And the revised categories are verified for specified Section        32     Residual value      600     500
    And the revised categories are verified for specified Section        33     Utilisation     60      50
    And the revised categories are verified for specified Section        34     Net cost    276.00      5050.00
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     35      4,774

#4.other costs section
Project finance user can view Lead partner's changes for other costs
    [Documentation]    INFUND-4837
    [Tags]
    Given the user moves focus to the element                            jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(79) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    When the user verifies the action and section for revised finances   79  Change  Other costs
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section        80  Total  550  5000
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section          81  4,450

#5.Travel and subsistence section
Project finance user can view Lead partner's changes for Travel and subsistence
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link                               link=Eligibility
    When the user clicks the button/link                                link=View changes to finances
    And the user moves focus to the element                           jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(67) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances   67  Change  Travel and subsistence
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section       68  Number of times  15  10
    And the revised categories are verified for specified Section       69  Cost each  199  100
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section         70  -1,985

#6.Subcontracting section
Project finance user can view Lead partner's changes for Subcontracting
    [Documentation]    INFUND-4837
    [Tags]
    # the below figures are listed as:     RowNumber      Action      Section
    Given the user moves focus to the element                               jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(52) td:nth-of-type(1)
    When the user verifies the action and section for revised finances      52  Change  Subcontracting
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section           54  Role  To develop stuff  Develop
    And the revised categories are verified for specified Section           55  Cost  45000  10600
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section             56  -34,400

#7. Labour section
Project finance user can view Lead partner's changes for Labour
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link                                   link=Eligibility
    When the user clicks the button/link                                    link=View changes to finances
    And the user moves focus to the element                                jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(1) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances      1  Change  Labour
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section           2  Gross annual salary  100  120000
    And the revised categories are verified for specified Section           3  Days to be spent  200  100
    And the revised cost is verified for the specified section              4  52,087
    And the user should see the text in the element                         css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(1)   Overall
    And the user should see the text in the element                         css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(2)   -94,488
    And the user clicks the button/link                                     jQuery=.button-secondary:contains("Return to eligibility")

Project finance user can view Partner's changes to finances
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Finance checks
    When the user clicks the button/link        css=a.eligibility-2
    And the user clicks the button/link        link=View changes to finances
    # the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
    When the categories are verified for Project finances section       1   £ 114,256   30%     £ 34,277    £ 2,468     £ 77,511
    # the below figures are listed as:     RowNumber   Labour    Overheads     Materials 	CapitalUsage    Subcontracting     TravelandSubsistence    OtherCosts
    Then the categories are verified for Section changes                1   £ 56,697     £ 9,078      £ -98,200    £ 9,548      £ -70,000       £ -3,970        £ 10,200

#1.materials section
Project finance user can view partner's changes for Materials
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Eligibility
    When the user clicks the button/link        link=View changes to finances
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances       16  Change  Materials
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section      18  Cost per item  5010  100
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     19  -49,100

#2.overheads section
Project finance user can view Partner's changes Overheads
    [Documentation]    INFUND-4837
    [Tags]
    Given the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(13) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    When the user verifies the action and section for revised finances      13  Change  Overheads
    Then the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(14) th:nth-of-type(1)  Amount
    And the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(14) td:nth-of-type(2)  9078
    And the revised cost is verified for the specified section     15  0

#3.capital usage section
Project finance user can view partner's revised changes for capital usage
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Eligibility
    When the user clicks the button/link        link=View changes to finances
    Then the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(28) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances       28    Change    Capital usage
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section       29     New or existing     Existing    New
    And the revised categories are verified for specified Section        31     Net present value   1060    10600
    And the revised categories are verified for specified Section        32     Residual value      600     500
    And the revised categories are verified for specified Section        33     Utilisation     60      50
    And the revised categories are verified for specified Section        34     Net cost    276.00      5050.00
    # the below figures are listed as:      RowNumber       Cost
    Then the revised cost is verified for the specified section     35      4,774

#4.other costs section
Project finance user can view partner's revised changes other costs
    [Documentation]    INFUND-4837
    [Tags]
    Given the user moves focus to the element       jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(79) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    When the user verifies the action and section for revised finances       79  Change  Other costs
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section       80  Total  550  5000
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     81  4,450

#5.Travel and subsistence section
Project finance user can view partner's revised changes for travel and subsistence
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Eligibility
    When the user clicks the button/link        link=View changes to finances
    And the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(67) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances      67      Change      Travel and subsistence
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section       68      Number of times     15      10
    And the revised categories are verified for specified Section       69      Cost each       199     100
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     70      -1,985

#6.Subcontracting section
Project finance user can view partner's revised changes for Subcontracting
    [Documentation]    INFUND-4837
    [Tags]
    # the below figures are listed as:     RowNumber      Action      Section
    Given the user moves focus to the element       jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(52) td:nth-of-type(1)
    When the user verifies the action and section for revised finances      52   Change  Subcontracting
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    Then the revised categories are verified for specified Section      54  Role  To develop stuff  Develop
    And the revised categories are verified for specified Section       55  Cost  45000  10600
    # the below figures are listed as:      RowNumber       Cost
    And the revised cost is verified for the specified section     56  -34,400

#7. Labour section
Project finance user can view partner's revised changes for Labour
    [Documentation]    INFUND-4837
    [Tags]
    Given the user clicks the button/link       link=Eligibility
    When the user clicks the button/link        link=View changes to finances
    And the user moves focus to the element    jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(1) td:nth-of-type(1)
    # the below figures are listed as:     RowNumber      Action      Section
    Then the user verifies the action and section for revised finances      1    Change    Labour
    # the below figures are listed as:      RowNumber       Detail      Submitted     Updated
    And the revised categories are verified for specified Section      2  Gross annual salary  100  120000
    And the revised categories are verified for specified Section       3  Days to be spent  200  100
    And the revised cost is verified for the specified section     4  52,087
    And the user should see the text in the element    css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(1)   Overall
    And the user should see the text in the element    css=.project-changes tfoot tr:nth-of-type(1) th:nth-of-type(2)   -86,647
    And the user clicks the button/link     jQuery=.button-secondary:contains("Return to eligibility")

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]
    [Setup]    log in as a different user   &{collaborator1_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    And the user should see the element     jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the element     jQuery=ul li.complete:nth-child(2)
    And the user should see the element     jQuery=ul li.complete:nth-child(4)
    And the user should see the element     jQuery=ul li.complete:nth-child(5)
    And the user should see the element     jQuery=ul li.read-only:nth-child(6)

Status updates correctly for internal user's table
     [Documentation]    INFUND-4049,INFUND-5543
     [Tags]      HappyPath
     [Setup]    log in as a different user   &{Comp_admin1_credentials}
     When the user navigates to the page    ${server}/project-setup-management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}/status
     Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok      # Project details
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.action      # MO
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status       # Bank details
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action     # Finance checks are actionable from the start-workaround for Private beta assessment
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status            # Spend Profile
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.waiting  # Other Docs
     And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status          # GOL

Other internal users do not have access to Finance checks
    [Documentation]    INFUND-4821
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    # This is added to HappyPath because CompAdmin should NOT have access to FC page
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check    ${403_error_message}

Finance contact can access the external view of the finance checks page
    [Documentation]    INFUND-7573, INFUND 8787
    [Tags]
    [Setup]    Log in as a different user   &{successful_applicant_credentials}
    Given the user clicks the button/link   jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user should see the element    jQuery=ul li.complete:nth-of-type(5):contains("We will review your financial information.")
    And the user should see the element     jQuery=ul li.complete:nth-of-type(5):contains("Completed")
    When the user clicks the button/link    link=Finance checks
    Then the user should not see an error in the page
    And the user should see the text in the page   The finance checks have been completed and your finances approved.

Lead Partner can view finance checks page
    [Documentation]    INFUND-7573, INFUND 8787
    [Tags]
    Given log in as a different user        &{lead_applicant_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user should see the element    jQuery=li.complete:contains("Finance")
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=.success-alert:contains("your finances approved.")

Lead partner can view only the external version of finance checks eligibility table
    [Documentation]    INFUND-8778, INFUND-8880
    [Tags]
    When the user clicks the button/link    link=View finances
    Then the user should see the element    jQuery=h2:contains("Detailed finances")
    And the user verifies the financial sub-totals for external version under the Detailed-finances     £ 60,602    £ 1,954     £ 52,100    £ 10,376    £ 65,000    £ 4,985     £ 11,850
    And the user should see the element    css=input[id="total-cost"][value="£ 206,867"]

Lead Partner can see the Finances amended
    [Documentation]  INFUND-8501
    [Tags]
    When the user clicks the button/link  link=View changes to finances
    Then the user should see the finance values amended by internal user

Academic user can view Finance checks page
    [Documentation]     INFUND-8787, INFUND-8880
    [Tags]
    Given log in as a different user        &{collaborator2_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user should see the element    jQuery=ul li.complete:nth-of-type(5):contains("We will review your financial information.")
    And the user should see the element     jQuery=ul li.complete:nth-of-type(5):contains("Completed")
    When the user clicks the button/link    link=Finance checks
    Then the user should see the text in the page   The finance checks have been completed and your finances approved.
    And the user should not see the text in the page    View finances
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/partner-organisation/${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}/finance-checks/eligibility    ${404_error_message}
    Then the user clicks the button/link    link=your dashboard

Non Lead Partner can view finance checks page
    [Documentation]     INFUND-8787
    [Tags]
    Given log in as a different user        &{collaborator1_credentials}
    When the user clicks the button/link    jQuery=.projects-in-setup a:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")
    Then the user should see the element    jQuery=ul li.complete:nth-of-type(5):contains("We will review your financial information.")
    And the user should see the element     jQuery=ul li.complete:nth-of-type(5):contains("Completed")
    When the user clicks the button/link    link=Finance checks
    And the user should see the text in the page   The finance checks have been completed and your finances approved.

Non Lead-Partner can view only the external version of finance checks eligibility table
    [Documentation]    INFUND-8778, INFUND-8880
    [Tags]
    When the user clicks the button/link    link=View finances
    Then the user should see the element    jQuery=h2:contains("Detailed finances")
    And the user verifies the financial sub-totals for external version under the Detailed-finances     £ 59,778    £ 9,078     £ 2,000    £ 10,100    £ 20,000    £ 2,000     £ 11,300
    And the user should see the element    css=input[id="total-cost"][value="£ 114,256"]

*** Keywords ***
the table row has expected values
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(2)    3 months
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(3)    £ 503,248
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(4)    £ 145,497
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(5)    £ 6,170
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(6)    29%

the user fills in project costs
    Input Text    name=costs[0].value    £ 8,000
    Input Text    name=costs[1].value    £ 2,000
    Input Text    name=costs[2].value    £ 10,000
    Input Text    name=costs[3].value    £ 10,000
    Input Text    name=costs[4].value    £ 10,000
    Input Text    name=costs[5].value    £ 10,000
    Input Text    name=costs[6].value    £ 10,000
    the user moves focus to the element    css=[for="costs-reviewed"]
    the user sees the text in the element    css=tfoot td    £ 60,000
    the user should see that the element is disabled    jQuery=.button:contains("Approve eligible costs")

project finance approves Viability for
    [Arguments]  ${partner}
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(${partner}) td:nth-child(2) a:contains("Review")
    Then the user should see the element    jQuery=h2:contains("Credit report")
    And the user selects the checkbox       costs-reviewed
    When the user should see the element    jQuery=h2:contains("Approve viability")
    Then the user selects the checkbox      project-viable
    And the user moves focus to the element  link=Contact us
    When the user selects the option from the drop-down menu  Green  id=rag-rating
    Then the user clicks the button/link    css=#confirm-button
    And the user clicks the button/link     jQuery=.modal-confirm-viability .button:contains("Confirm viability")

the rag rating updates on the finance check page for lead for viability
   [Arguments]    ${rag_rating}
   When the user selects the option from the drop-down menu    ${rag_rating}    id=rag-rating
   And the user clicks the button/link    jQuery=.button-secondary:contains("Save and return to finance checks")
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(3)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
   And the user should see the element    jQuery=.button:contains("Confirm viability"):not(.disabled)    # Checking here both that the button exists and that it isn't disabled

the rag rating updates on the finance check page for partner for viability
   [Arguments]    ${rag_rating}
   When the user selects the option from the drop-down menu    ${rag_rating}    id=rag-rating
   And the user clicks the button/link    jQuery=.button-secondary:contains("Save and return to finance checks")
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(3)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2) a:contains("Review")
   And the user should see the element    jQuery=.button:contains("Confirm viability"):not(.disabled)    # Checking here both that the button exists and that it isn't disabled

the rag rating updates on the finance check page for lead for eligibility
   [Arguments]    ${rag_rating}
   When the user selects the option from the drop-down menu    ${rag_rating}    id=rag-rating
   And the user clicks the button/link    jQuery=.button-secondary:contains("Return to finance checks")
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(5)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")
   And the user should see the element    jQuery=.button:contains("Approve eligible costs"):not(.disabled)    # Checking here both that the button exists and that it isn't disabled

the rag rating updates on the finance check page for partner for eligibility
   [Arguments]    ${rag_rating}
   When the user selects the option from the drop-down menu    ${rag_rating}    id=rag-rating
   And the user clicks the button/link    jQuery=.button-secondary:contains("Return to finance checks")
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(5)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(3) td:nth-child(4) a:contains("Review")
   And the user should see the element    jQuery=.button:contains("Approve eligible costs"):not(.disabled)    # Checking here both that the button exists and that it isn't disabled

verify total costs of project
    [Arguments]    ${total_costs}
    the user should see the text in the element      jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)     ${total_costs}

verify percentage and total
    [Arguments]  ${section}  ${percentage}  ${total}
    the user should see the element           jQuery=section:nth-of-type(${section}) button span:contains("${percentage}")
    the user should see the element            jQuery=section:nth-of-type(${section}) input[data-calculation-rawvalue^='${total}']

the user adds data into labour row
    [Arguments]  ${row_number}  ${descrption}  ${salary}  ${days}
    the user enters text to a text field        css=.labour-costs-table tr:nth-of-type(${row_number}) td:nth-of-type(1) input    ${descrption}
    the user enters text to a text field        css=.labour-costs-table tr:nth-of-type(${row_number}) td:nth-of-type(2) input    ${salary}
    the user enters text to a text field        css=.labour-costs-table tr:nth-of-type(${row_number}) td:nth-of-type(4) input    ${days}

the user adds data into materials row
    [Arguments]  ${row_number}  ${item}  ${qty}  ${cost_of_item}
    the user enters text to a text field        css=#material-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(1) input    ${item}
    the user enters text to a text field        css=#material-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(2) input    ${qty}
    the user enters text to a text field        css=#material-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(3) input    ${cost_of_item}

the user adds capital usage data into row
    [Arguments]  ${row_number}  ${description}  ${net_value}  ${residual_value}  ${utilization}
    the user enters text to a text field        jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(1) textarea   ${description}
    Click Element                               jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(2) label:nth-of-type(1)
    the user enters text to a text field        jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(3) input    12
    the user enters text to a text field        jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(4) input  ${net_value}
    the user enters text to a text field        jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(5) input   ${residual_value}
    the user enters text to a text field        jQuery=#capital_usage div:nth-child(${row_number}) div:nth-of-type(6) input   ${utilization}

the user adds subcontracting data into row
    [Arguments]  ${row_number}  ${name}  ${cost}
    the user enters text to a text field        css=#subcontracting div:nth-child(${row_number}) div:nth-of-type(1) input   ${name}
    the user enters text to a text field        css=#subcontracting div:nth-child(${row_number}) div:nth-of-type(2) input   UK
    the user enters text to a text field        css=#subcontracting div:nth-child(${row_number}) div:nth-of-type(3) textarea   Develop
    the user enters text to a text field        css=#subcontracting div:nth-child(${row_number}) div:nth-of-type(4) input   ${cost}

the user adds travel data into row
    [Arguments]  ${row_number}  ${description}  ${number_of_times}  ${cost}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(1) input    ${description}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(2) input    ${number_of_times}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(3) input    ${cost}

Project finance user amends labour details in eligibility for partner
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button:contains("Labour")
    Then the user should see the element            jQuery=section:nth-of-type(1) button span:contains("2%")
    When the user clicks the button/link            jQuery=section:nth-of-type(1) a:contains("Edit")
    Then the user should see the element            css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    When the user clears the text from the element  css=[name^="labour-labourDaysYearly"]
    And the user enters text to a text field        css=[name^="labour-labourDaysYearly"]    230
    And the user adds data into labour row          1  test  120000  100
    Then verify percentage and total                1  20%  53734
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button:contains("Add another role")
    And the user adds data into labour row          13  test  14500  100
    Then verify percentage and total                1  22%  60039
    When the user clicks the button/link            css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(5) button
    Then verify percentage and total                1  22%  59778
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button[name=save-eligibility]
    Then verify total costs of project              £ 257,600
    And the user should see the element             jQuery=section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element         jQuery=section:nth-of-type(1) button[name=save-eligibility]

Project finance user amends materials details in eligibility for partner
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button:contains("Materials")
    Then verify percentage and total                3  39%  100200
    When the user clicks the button/link            jQuery=section:nth-of-type(3) a:contains("Edit")
    And the user adds data into materials row       1  test  10  100
    Then verify percentage and total                3  25%  51100
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button[name=add_cost]
    And the user adds data into materials row       3  test  10  100
    Then verify percentage and total                3  25%  52100
    When the user clicks the button/link            css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                3  1%  2000
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button[name=save-eligibility]
    Then verify total costs of project              £ 159,400
    And the user should see the element            jQuery=section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element        jQuery=section:nth-of-type(3) button[name=save-eligibility]

Project finance user amends capital usage details in eligibility for partner
    When the user clicks the button/link            jQuery=section:nth-of-type(4) button:contains("Capital usage")
    Then the user should see the element            jQuery=section:nth-of-type(4) button span:contains("0%")
    When the user clicks the button/link            jQuery=section:nth-of-type(4) a:contains("Edit")
    And the user adds capital usage data into row   1  test  10600  500  50
    Then verify percentage and total                4  3%  5326
    When the user clicks the button/link            jQuery=section:nth-of-type(4) button[name=add_cost]
    And the user adds capital usage data into row   3  test  10600  500  50
    Then verify percentage and total                4  6%  10376
    When the user clicks the button/link            css=section:nth-of-type(4) #capital_usage div:nth-child(2) button
    Then verify percentage and total                 4  6%  10100
    When the user clicks the button/link           jQuery=section:nth-of-type(4) button[name=save-eligibility]
    Then verify total costs of project             £ 168,948
    And the user should see the element           jQuery=section:nth-of-type(4) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(4) button[name=save-eligibility]

Project finance user amends subcontracting usage details in eligibility for partner
    When the user clicks the button/link            jQuery=section:nth-of-type(5) button:contains("Subcontracting costs")
    Then the user should see the element            jQuery=section:nth-of-type(5) button span:contains("53%")
    And the user should see the element            jQuery=section:nth-of-type(5) input[value*='90,000']
    When the user clicks the button/link            jQuery=section:nth-of-type(5) a:contains("Edit")
    And the user adds subcontracting data into row   1  test  10600
    Then verify percentage and total                 5  41%  55600
    When the user clicks the button/link            jQuery=section:nth-of-type(5) button[name=add_cost]
    And the user adds subcontracting data into row   3  test  9400
    Then verify percentage and total                 5  45%  65000
    When the user clicks the button/link            css=section:nth-of-type(5) #subcontracting div:nth-child(2) button
    Then verify percentage and total                 5  18%  20000
    When the user clicks the button/link           jQuery=section:nth-of-type(5) button[name=save-eligibility]
    Then verify total costs of project              £ 98,948
    And the user should see the element           jQuery=section:nth-of-type(5) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(5) button[name=save-eligibility]

Project finance user amends travel details in eligibility for partner
    Given the user clicks the button/link           jQuery=section:nth-of-type(6) button:contains("Travel and subsistence")
    Then the user should see the element            jQuery=section:nth-of-type(6) button span:contains("6%")
    And the user should see the element            jQuery=section:nth-of-type(6) input[value*='5,970']
    When the user clicks the button/link            jQuery=section:nth-of-type(6) a:contains("Edit")
    And the user adds travel data into row          1  test  10  100
    Then verify percentage and total                 6  4%  3985
    When the user clicks the button/link            jQuery=section:nth-of-type(6) button[name=add_cost]
    And the user adds travel data into row          3  test  10  100
    Then verify percentage and total                 6  5%  4985
    When the user clicks the button/link            css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                 6  2%  2000
    When the user clicks the button/link           jQuery=section:nth-of-type(6) button[name=save-eligibility]
    Then verify total costs of project            £ 94,978
    And the user should see the element           jQuery=section:nth-of-type(6) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(6) button[name=save-eligibility]

Project finance user amends other costs details in eligibility for partner
    When the user clicks the button/link            jQuery=section:nth-of-type(7) button:contains("Other costs")
    Then the user should see the element            jQuery=section:nth-of-type(7) button span:contains("1%")
    And the user should see the element            jQuery=section:nth-of-type(7) input[value*='1,100']
    When the user clicks the button/link            jQuery=section:nth-of-type(7) a:contains("Edit")
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(2) input  5000
    Then verify percentage and total                 7  6%  5550
    When the user clicks the button/link            jQuery=section:nth-of-type(7) button[name=add_cost]
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(3) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(3) td:nth-child(2) input  5750
    Then verify percentage and total                 7  11%  11300
    When the user should see the element           css=#other-costs-table tr:nth-of-type(2) td:nth-of-type(3) button
    When the user clicks the button/link           jQuery=section:nth-of-type(7) button[name=save-eligibility]
    Then verify total costs of project            £ 105,178
    And the user should see the element           jQuery=section:nth-of-type(7) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(7) button[name=save-eligibility]

Project finance user amends labour details in eligibility for lead
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button:contains("Labour")
    Then the user should see the element            jQuery=section:nth-of-type(1) button span:contains("2%")
    When the user clicks the button/link            jQuery=section:nth-of-type(1) a:contains("Edit")
    Then the user should see the element            css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    When the user clears the text from the element  css=[name^="labour-labourDaysYearly"]
    And the user enters text to a text field        css=[name^="labour-labourDaysYearly"]    230
    And the user adds data into labour row          1  test  120000  100
    Then verify percentage and total                1  15%  5455
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button:contains("Add another role")
    And the user adds data into labour row          19  test  14500  100
    Then verify percentage and total                1  16%  60863
    When the user clicks the button/link            css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(5) button
    Then verify percentage and total                1  16%  60602
    When the user clicks the button/link            jQuery=section:nth-of-type(1) button[name=save-eligibility]
    Then verify total costs of project              £ 357,335
    And the user should see the element             jQuery=section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element         jQuery=section:nth-of-type(1) button[name=save-eligibility]

Project finance user amends materials details in eligibility for lead
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button:contains("Materials")
    Then verify percentage and total                3  42%  150300
    When the user clicks the button/link            jQuery=section:nth-of-type(3) a:contains("Edit")
    And the user adds data into materials row       1  test  10  100
    Then verify percentage and total                3  33%  101200
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button[name=add_cost]
    And the user adds data into materials row       4  test  10  100
    Then verify percentage and total                3  33%  102200
    When the user clicks the button/link            css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                3  19%  52100
    When the user clicks the button/link            jQuery=section:nth-of-type(3) button[name=save-eligibility]
    Then verify total costs of project              £ 259,135
    And the user should see the element            jQuery=section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element        jQuery=section:nth-of-type(3) button[name=save-eligibility]

Project finance user amends capital usage details in eligibility for lead
    When the user clicks the button/link            jQuery=section:nth-of-type(4) button:contains("Capital usage")
    Then the user should see the element            jQuery=section:nth-of-type(4) button span:contains("0%")
    When the user clicks the button/link            jQuery=section:nth-of-type(4) a:contains("Edit")
    And the user adds capital usage data into row   1  test  10600  500  50
    Then verify percentage and total                4  2%  5602
    When the user clicks the button/link            jQuery=section:nth-of-type(4) button[name=add_cost]
    And the user adds capital usage data into row   4  test  10600  500  50
    Then verify percentage and total                4  4%  10652
    When the user clicks the button/link            css=section:nth-of-type(4) #capital_usage div:nth-child(2) button
    Then verify percentage and total                 4  4%  10376
    When the user clicks the button/link           jQuery=section:nth-of-type(4) button[name=save-eligibility]
    Then verify total costs of project             £ 268,683
    And the user should see the element           jQuery=section:nth-of-type(4) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(4) button[name=save-eligibility]

Project finance user amends subcontracting usage details in eligibility for lead
    When the user clicks the button/link            jQuery=section:nth-of-type(5) button:contains("Subcontracting costs")
    Then the user should see the element            jQuery=section:nth-of-type(5) button span:contains("50%")
    And the user should see the element            jQuery=section:nth-of-type(5) input[value*='135,000']
    When the user clicks the button/link            jQuery=section:nth-of-type(5) a:contains("Edit")
    And the user adds subcontracting data into row   1  test  10600
    Then verify percentage and total                 5  43%  100600
    When the user clicks the button/link            jQuery=section:nth-of-type(5) button[name=add_cost]
    And the user adds subcontracting data into row   4  test  9400
    Then verify percentage and total                 5  45%  110000
    When the user clicks the button/link            css=section:nth-of-type(5) #subcontracting div:nth-child(2) button
    When the user clicks the button/link           jQuery=section:nth-of-type(5) button[name=save-eligibility]
    Then verify total costs of project              £ 198,683
    And the user should see the element           jQuery=section:nth-of-type(5) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(5) button[name=save-eligibility]

Project finance user amends travel details in eligibility for lead
    Given the user clicks the button/link           jQuery=section:nth-of-type(6) button:contains("Travel and subsistence")
    Then the user should see the element            jQuery=section:nth-of-type(6) button span:contains("5%")
    And the user should see the element            jQuery=section:nth-of-type(6) input[value*='8,955']
    When the user clicks the button/link            jQuery=section:nth-of-type(6) a:contains("Edit")
    And the user adds travel data into row          1  test  10  100
    Then verify percentage and total                 6  4%  6970
    When the user clicks the button/link            jQuery=section:nth-of-type(6) button[name=add_cost]
    And the user adds travel data into row          4  test  10  100
    Then verify percentage and total                 6  4%  7970
    When the user clicks the button/link            css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                 6  2%  4985
    When the user clicks the button/link           jQuery=section:nth-of-type(6) button[name=save-eligibility]
    Then verify total costs of project            £ 194,713
    And the user should see the element           jQuery=section:nth-of-type(6) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(6) button[name=save-eligibility]

Project finance user amends other costs details in eligibility for lead
    When the user clicks the button/link            jQuery=section:nth-of-type(7) button:contains("Other costs")
    Then the user should see the element            jQuery=section:nth-of-type(7) button span:contains("1%")
    And the user should see the element            jQuery=section:nth-of-type(7) input[value*='1,650']
    When the user clicks the button/link            jQuery=section:nth-of-type(7) a:contains("Edit")
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(2) input  5000
    Then verify percentage and total                 7  3%  6100
    When the user clicks the button/link            jQuery=section:nth-of-type(7) button[name=add_cost]
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(4) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(4) td:nth-child(2) input  5750
    Then verify percentage and total                 7  6%  11850
    When the user should see the element           css=#other-costs-table tr:nth-of-type(2) td:nth-of-type(3) button
    When the user clicks the button/link           jQuery=section:nth-of-type(7) button[name=save-eligibility]
    Then verify total costs of project            £ 204,913
    And the user should see the element           jQuery=section:nth-of-type(7) a:contains("Edit")
    And the user should not see the element       jQuery=section:nth-of-type(7) button[name=save-eligibility]

the categories are verified for Overview section
    [Arguments]  ${row_number}  ${start_date}  ${duration}  ${total_project_cost}  ${grant_applied_for}  ${other_public_sector_fund}  ${total_percent_grant}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(1)  ${start_date}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(2)  ${duration}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(3)  ${total_project_cost}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(4)  ${grant_applied_for}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(5)  ${other_public_sector_fund}
    the user should see the text in the element     css=.table-overview tr:nth-of-type(${row_number}) td:nth-of-type(6)  ${total_percent_grant}

the categories are verified for Finances summary section
    [Arguments]  ${row_number}  ${total_costs}  ${percentage_grant}  ${funding_sought}  ${other_public_sector_funding}  ${contribution_to_project}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(1) strong   ${total_costs}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)  ${percentage_grant}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(3)  ${funding_sought}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(4)  ${other_public_sector_funding}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(5)  ${contribution_to_project}

the Total calculation for Finances summary are verified
    [Arguments]  ${row_number}  ${allPartners_totalcost}   ${allPartners_fundingSought}   ${allPartners_otherPublicSectorFunding}  ${allPartners_contributionToProject}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tfoot tr:nth-of-type(${row_number}) td:nth-of-type(1) strong  ${allPartners_totalcost}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tfoot tr:nth-of-type(${row_number}) td:nth-of-type(3) strong  ${allPartners_fundingSought}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tfoot tr:nth-of-type(${row_number}) td:nth-of-type(4) strong  ${allPartners_otherPublicSectorFunding}
    the user should see the text in the element     jQuery=h3:contains("Finances summary") + * tfoot tr:nth-of-type(${row_number}) td:nth-of-type(5) strong  ${allPartners_contributionToProject}

all the categories are verified
    [Arguments]  ${row_number}  ${total}  ${labour}  ${overheads}  ${materials}  ${capital_usage}  ${subcontracting}  ${travel}   ${other_costs}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(1) strong  ${total}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)  ${labour}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(3)  ${overheads}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(4)  ${materials}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(5)  ${capital_usage}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(6)  ${subcontracting}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(7)  ${travel}
    the user should see the text in the element   css=.form-group tbody tr:nth-of-type(${row_number}) td:nth-of-type(8)  ${other_costs}

# the below figures are listed as:     RowNumber   TotalCosts    % Grant     FundingSought 	OtherPublicSectorFunding    ContributionToProject
the categories are verified for Project finances section
    [Arguments]  ${row_number}  ${total_costs}  ${percentage_grant}  ${funding_sought}  ${other_public_sector_funding}  ${contribution_to_project}
    the user should see the text in the element     jQuery=h2:contains("Project finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(1)   ${total_costs}
    the user should see the text in the element     jQuery=h2:contains("Project finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)   ${percentage_grant}
    the user should see the text in the element     jQuery=h2:contains("Project finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(3)   ${funding_sought}
    the user should see the text in the element     jQuery=h2:contains("Project finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(4)   ${other_public_sector_funding}
    the user should see the text in the element     jQuery=h2:contains("Project finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(5)   ${contribution_to_project}

# the below figures are listed as:     RowNumber   Labour    Overheads     Materials 	CapitalUsage    Subcontracting     TravelandSubsistence    OtherCosts
the categories are verified for section changes
    [Arguments]  ${row_number}  ${labour}  ${overheads}  ${materials}  ${capital_usage}  ${sub_contracting}  ${travel_and_subsistence}  ${other_costs}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(1)   ${labour}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)   ${overheads}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(3)   ${materials}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(4)   ${capital_usage}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(5)   ${sub_contracting}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(6)   ${travel_and_subsistence}
    the user should see the text in the element     jQuery=h2:contains("Section changes") + div tbody tr:nth-of-type(${row_number}) td:nth-of-type(7)   ${other_costs}

the user verifies the action and section for revised finances
    [Arguments]  ${row_number}  ${action}  ${section}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(1)   ${action}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)   ${section}

the revised cost is verified for the specified section
    [Arguments]  ${row_number}  ${cost}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(1)   ${cost}

the revised categories are verified for Other-costs Section
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(1) th:nth-of-type(1)   Description and justification of cost
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(1) td:nth-of-type(3)   Some more costs
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(1) td:nth-of-type(4)   some other costs
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(2) th:nth-of-type(1)   Total
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(2) td:nth-of-type(1)   550
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(2) td:nth-of-type(2)   5000
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(3) td:nth-of-type(1)   4,450

# the below figures are listed as:     RowNumber    Detail   Submitted    Updated
the revised categories are verified for specified Section
    [Arguments]  ${row_number}  ${detail}  ${submitted}  ${updated}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) th:nth-of-type(1)   ${detail}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(1)   ${submitted}
    the user should see the text in the element     jQuery=h2:contains("Changes from submitted finances") + * tbody tr:nth-of-type(${row_number}) td:nth-of-type(2)   ${updated}

the user verifies the financial sub-totals for external version under the Detailed-finances
    [Arguments]  ${labour}  ${overheads}  ${materials}  ${capital_usage}  ${sub_contracting}  ${travel_and_subsistence}  ${other_costs}
    the user should see the text in the element     css=section:nth-of-type(1) h3 button span   ${labour}
    the user should see the text in the element     css=section:nth-of-type(2) h3 button span   ${overheads}
    the user should see the text in the element     css=section:nth-of-type(3) h3 button span   ${materials}
    the user should see the text in the element     css=section:nth-of-type(4) h3 button span   ${capital_usage}
    the user should see the text in the element     css=section:nth-of-type(5) h3 button span   ${sub_contracting}
    the user should see the text in the element     css=section:nth-of-type(6) h3 button span   ${travel_and_subsistence}
    the user should see the text in the element     css=section:nth-of-type(7) h3 button span   ${other_costs}

the user should see the finance values amended by internal user
    the user should see the element  jQuery=#project-finance-changes-submitted tr:contains("Gross") td:contains("120000")
    the user should see the element  jQuery=#project-finance-changes-submitted tr:contains("Amount") td:contains("1954")
    the user should see the element  jQuery=#project-finance-changes-submitted tr:contains("Net cost") td:contains("276.00") + td:contains("5050.00")
    the user should see the element  jQuery=#project-finance-changes-submitted tr:contains("Overall") th:contains("-94,488")

check finance checks status on dashboard
    [Arguments]  ${selector}  ${status}
    When the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_TITLE}
    Then the user should see the element    link=Finance checks
    And the user should see the element     jQuery=ul li.${selector}:nth-of-type(5):contains("We will review your financial information.")
    And the user should see the element     jQuery=ul li.${selector}:nth-of-type(5):contains(${status})