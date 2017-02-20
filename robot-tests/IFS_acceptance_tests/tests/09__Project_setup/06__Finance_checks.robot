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
Suite Setup       Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot
Resource          ../04__Applicant/FinanceSection_Commons.robot

*** Variables ***
${la_fromage_overview}    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}

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


Status of the Eligibility column (workaround for private beta competition)
    [Documentation]    INFUND-5190
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    Then The user should see the text in the page    Viability
    And The user should see the text in the page    Queries raised
    And The user should see the text in the page    Notes
    When the user should see the element    link=Review
    Then the user should see that the element is disabled    jQuery=.generate-spend-profile-main-button


Finance checks client-side validations
    [Documentation]    INFUND-5193
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user clicks the button/link     css=a.eligibility-0
    When the user enters text to a text field    name=costs[0].value    ${Empty}
    Then the user should see an error    Please enter a labour cost
    When the user enters text to a text field    name=costs[1].value    ${Empty}
    Then the user should see an error    Please enter an admin support cost
    When the user enters text to a text field    name=costs[2].value    ${Empty}
    Then the user should see an error    Please enter a materials cost
    When the user enters text to a text field    name=costs[3].value    ${Empty}
    Then the user should see an error    Please enter a capital usage cost
    When the user enters text to a text field    name=costs[4].value    ${Empty}
    Then the user should see an error    Please enter subcontracting cost
    When the user enters text to a text field    name=costs[5].value    ${Empty}
    Then the user should see an error    Please enter a travel and subsistence cost
    When the user enters text to a text field    name=costs[6].value    ${Empty}
    Then the user should see an error    Please enter any other cost
    When the user enters text to a text field    name=costs[0].value    -1
    And the user moves focus to the element    css=[for="costs-reviewed"]
    Then the user should see an error    This field should be 0 or higher
    And The user should not see the text in the page    Please enter a labour cost


Project Finance user can view academic Jes form
    [Documentation]     INFUND-5220
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check
    # note that we are viewing the file above rather than the same project as the other tests in this suite due to INFUND-6724
    When the user clicks the button/link    css=a.eligibility-2
    Then the user should see the text in the page    Download Je-S form
    When the user clicks the button/link    link=jes-form57.pdf
    Then the user should not see an error in the page
    [Teardown]    the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check


Viability checks are populated in the table
    [Documentation]    INFUND-4822, INFUND-7095
    [Tags]
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(3)    Not set
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2)    Review
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(3)    Not set
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(2)    N/A
    And the user should see the text in the element    jQuery=table.table-progress tr:nth-child(3) td:nth-child(3)    N/A

Project finance user can see the viability check page for the lead partner
    [Documentation]    INFUND-4831, INFUND-4830, INFUND-4825
    [Tags]
    when the user clicks the button/link    jQuery=table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")    # clicking the review button for the lead partner
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_COMPANY_NUMBER}


Project finance user can see the lead partner's information
    [Documentation]    INFUND-4825
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(1)    £302,510
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(2)    30%
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £211,757
    When the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £87,051
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
    And the user should see the checkbox    creditReportConfirmed
    And the user should see the checkbox    confirmViabilityChecked
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
    [Documentation]    INFUND-4831, INFUND-4830
    [Tags]
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2) a:contains("Review")
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_COMPANY_NUMBER}

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
    And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2) a:contains("Review")
    Then checkbox should be selected    creditReportConfirmed

Clicking cancel on the viability modal for partner
    [Documentation]    INFUND-4822, INFUND-4830
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Confirm viability")
    And the user clicks the button/link    jQuery=.buttonlink.js-close    # Clicking the cancel link on the modal
    Then the user should see the element    id=rag-rating
    And the user should see the checkbox    creditReportConfirmed
    And the user should see the checkbox    confirmViabilityChecked
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
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2) a:contains("Approved")


Eligibility checks are populated in the table
    [Documentation]    INFUND-4823
    [Tags]    Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
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
    And the user navigates to the page   ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/organisation/22/eligibility   # TODO to delete this when the new eligibility page is switched to
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}

Project finance user can see the lead partner's information about eligibility
    [Documentation]    INFUND-4832
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(1)    36 months
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)    £ 201,674
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(3)    30%
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(4)    £ 60,502
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(5)    £ 2,468
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(6)    £ 138,704


Finance checks eligibility validations
    [Documentation]    INFUND-4833
    [Tags]  Pending
    # TODO Error INFUND-8129 still exists so putting it to pending
    When the user clicks the button/link             jQuery=form section:nth-of-type(1) h3:contains("Labour")
    And the user clicks the button/link              jQuery=form section:nth-of-type(1) a:contains("Edit")
    When the user enters text to a text field        css=[name^="labour-labourDaysYearly"]    -230
    Then the user should see the text in the page    This field should be 1 or higher
    When the user clicks the button/link             jQuery=form section:nth-of-type(1) button[name=save-eligibility]
    Then the user should see the text in the page    This field should be 1 or higher
    And the user reloads the page
    When the user clicks the button/link             jQuery=form section:nth-of-type(3) h3:contains("Materials")
    And the user clicks the button/link              jQuery=form section:nth-of-type(3) a:contains("Edit")
    When the user enters text to a text field        css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    100
    And the user clicks the button/link              jQuery=form section:nth-of-type(3) button[name=save-eligibility]
    Then the user should see the text in the page    This field cannot be left blank
    And the user reloads the page
    When the user clicks the button/link             jQuery=form section:nth-of-type(4) h3:contains("Capital usage")
    And the user clicks the button/link              jQuery=form section:nth-of-type(4) a:contains("Edit")
    When the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(1) div:nth-of-type(6) input   200
    Then the user should see the text in the page    This field should be 100 or lower
    And the user reloads the page
    When the user clicks the button/link             jQuery=form section:nth-of-type(6) h3:contains("Travel and subsistence")
    And the user clicks the button/link              jQuery=form section:nth-of-type(6) a:contains("Edit")
    And the user enters text to a text field         css=#travel-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    123
    When the user clicks the button/link             jQuery=form section:nth-of-type(6) button[name=save-eligibility]
    Then the user should see the text in the page     This field cannot be left blank
    And the user reloads the page
    When the user clicks the button/link             jQuery=form section:nth-of-type(7) h3:contains("Other costs")
    And the user clicks the button/link              jQuery=form section:nth-of-type(7) a:contains("Edit")
    When the user clicks the button/link             jQuery=form section:nth-of-type(7) button[name=save-eligibility]
    Then the user should see the text in the page    This field cannot be left blank
    When the user clicks the button/link             link=Finance checks
  #  When the user clicks the button/link             jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Review")    # TODO this is to be uncommented once the switch to new eligibility page is done with 4823
    And the user navigates to the page               ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/organisation/22/eligibility   # TODO to delete this when the new eligibility page is switched to

Project finance user can amend all sections of eligibility
    [Documentation]    INFUND-4834
    [Tags]
    When Project finance user amends labour details in eligibility
    And Project finance user amends materials details in eligibility
    And Project finance user amends capital usage details in eligibility
    And Project finance user amends subcontracting usage details in eligibility
    And Project finance user amends travel details in eligibility
    And Project finance user amends other costs details in eligibility

Checking the approve eligibility checkbox enables RAG selection but not Approve eligibility button
    [Documentation]    INFUND-4839
    [Tags]
    When the user selects the checkbox    project-eligible
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")


RAG choices update on the finance checks page for eligibility
    [Documentation]    INFUND-4839, INFUND-4823
    [Tags]  Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
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
    And the user should see the checkbox    project-eligible
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
    [Tags]  Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Approved")

Project finance user can see updated finance overview after lead changes to eligibility
    [Documentation]    INFUND-5508
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check
    Then the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £ 322,786
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £ 92,593
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(6)    29%


Project finance user can see the Eligibility check page for the partner
    [Documentation]    INFUND-4823
    [Tags]
    Given the user navigates to the page    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check
    When the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Review")
    And the user navigates to the page  ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/organisation/39/eligibility   # TODO to delete this when the new eligibility page is switched to
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}

Project finance user can see the partner's information about eligibility
    [Documentation]    INFUND-4832
    [Tags]
    # Note the below figures aren't calculated, but simply brought forward from user-entered input during the application phase
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(1)    36 months
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)    £ 201,674
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(3)    0%
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(4)    £ 0
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(5)    £ 2,468
    When the user should see the text in the element    jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(6)    £ 199,206

Project finance user can amend all sections of eligibility for partner
    [Documentation]    INFUND-4834
    [Tags]
    When Project finance user amends labour details in eligibility
    And Project finance user amends materials details in eligibility
    And Project finance user amends capital usage details in eligibility
    And Project finance user amends subcontracting usage details in eligibility
    And Project finance user amends travel details in eligibility
    And Project finance user amends other costs details in eligibility

Project finance user can see the eligibility checks for the industrial partner
    [Documentation]    INFUND-4823
    [Tags]  Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
    When the user clicks the button/link   link=Finance checks
    And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Review")
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}

Checking the approve eligibility checkbox enables RAG selection but not confirm viability button for partner
    [Documentation]    INFUND-4839
    [Tags]
    When the user navigates to the page  ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/organisation/39/eligibility  # TODO to be removed after 4823
    And the user selects the checkbox    project-eligible
    Then the user should see the element    id=rag-rating
    And the user should see the element    jQuery=.button.disabled:contains("Approve eligible costs")

RAG choices update on the finance checks page for eligibility for partner
    [Documentation]    INFUND-4839, INFUND-4823
    [Tags]  Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
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
    And the user should see the checkbox    project-eligible
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
    And the user clicks the button/link    link=Finance checks

Confirming eligibility should update on the finance checks page
    [Documentation]    INFUND-4823
    [Tags]  Pending
    # TODO Pending due to INFUND-4823 as story has not yet been merged to dev
    When the user clicks the button/link    link=Finance checks
    Then the user should see the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Approved")


Approve Eligibility: Lead partner organisation
    [Documentation]    INFUND-5193, INFUND-6149
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/organisation/22
    And the user should see the element    xpath=//a[contains(@href,'mailto:${test_mailbox_one}+fundsuccess@gmail.com')]
    When the user fills in project costs
    And the user selects the checkbox    costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    jQuery=.button:contains("Return to finance checks")    #Check that also the button works
    Then the user sees the text in the element    css=a.eligibility-0    Approved

Project finance user can see updated finance overview after partner changes to eligibility
    [Documentation]    INFUND-5508
    [Tags]
    When the user navigates to the page    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check/
    Then the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(3)    £ 241,236
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(4)    £ 68,128
    And the user should see the text in the element    jQuery=.table-overview tr:nth-child(1) td:nth-child(6)    28%

Approve Eligibility: Collaborator partner organisation
    [Documentation]    INFUND-5193
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check/
    When the user clicks the button/link    css=a.eligibility-1
    When the user fills in project costs
    And the user selects the checkbox    costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve eligible costs")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    And the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=a.eligibility-1    Approved

Approve Eligibility and verify Viability and RAG: Academic partner organisation
    [Documentation]    INFUND-5193, INFUND-7026, INFUND-7095
    [Tags]    HappyPath
    When the user clicks the button/link    css=a.eligibility-2
    And the user selects the checkbox    costs-reviewed
    Then the user clicks the button/link    jQuery=.button:contains("Approve finances")
    And the user clicks the button/link    jQuery=.approve-eligibility-modal .button:contains("Approve eligible costs")
    Then the user should see the text in the page    The partner finance eligibility has been approved
    And The user clicks the button/link    link=Finance checks
    Then the user sees the text in the element    css=a.eligibility-2    Approved
    And The user should see the element    jQuery=.generate-spend-profile-main-button
    And the user should see the text in the element  css=span.viability-rag-2    N/A
    And the user should see the text in the element  css=span.viability-2    N/A

Generate spend profile button enabled after viability checks are completed
    [Documentation]    INFUND-7076
    [Tags]
    When the user should not see the element    xpath=//*[@class='button generate-spend-profile-main-button' and @disabled='disabled']

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]      HappyPath
    [Setup]    Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
#    And the user should not see the element    link = Bank details
#    And the user should not see the element    link = Finance checks
#    And the user should not see the element    link = Spend profile
#    And the user should not see the element    link = Grant offer letter
    # MO link is not added because suite fails when ran independently
    #TODO please update links when working on INFUND-6815

Status updates correctly for internal user's table
     [Documentation]    INFUND-4049,INFUND-5543
     [Tags]      HappyPath
     [Setup]    log in as a different user   &{Comp_admin1_credentials}
     When the user navigates to the page    ${server}/project-setup-management/competition/${FUNDERS_PANEL_COMPETITION}/status
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
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    # This is added to HappyPath because CompAdmin should NOT have access to FC page
    Then the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check    You do not have the necessary permissions for your request


Finance contact can access the external view of the finance checks page
    [Documentation]    INFUND-7573
    [Tags]    HappyPath
    [Setup]    Log in as a different user    ${test_mailbox_one}+fundsuccess@gmail.com    Passw0rd
    Given the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_HEADER}
    When the user clicks the button/link    link=Finance checks
    Then the user should see the text in the page    Innovate UK are reviewing your finances and may contact you with any queries
    And the user should not see an error in the page


Non finance contact can't view finance checks page
    [Documentation]    INFUND-7573
    [Tags]
    [Setup]    Log in as a different user    steve.smith@empire.com    Passw0rd
    When the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_HEADER}
    Then the user should not see the element    link=Finance checks
    And the user navigates to the page and gets a custom error message    ${server}/project-setup/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/partner-organisation/${EMPIRE_LTD_ID}/finance-checks    ${403_error_message}


*** Keywords ***
the table row has expected values
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(2)    3 months
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(3)    £ 505,174
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(4)    £ 146,075
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(5)    £ 6,170
    the user sees the text in the element    jQuery=.table-overview tbody td:nth-child(6)    29%

Moving ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    the users fill out project details

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup if it isn't already
    guest user log-in    lee.bowman@innovateuk.test    Passw0rd
    the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    ${update_comp}    ${value}=    Run Keyword And Ignore Error Without Screenshots    the user should not see the text in the page    ${FUNDERS_PANEL_COMPETITION_NAME}
    run keyword if    '${update_comp}' == 'PASS'    the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup

the project finance user moves ${FUNDERS_PANEL_COMPETITION_NAME} into project setup
    the user navigates to the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/funding
    the user selects the option from the drop-down menu    Yes    id=fund28
    the user selects the option from the drop-down menu    No    id=fund29
    the user clicks the button/link    jQuery=.button:contains("Notify applicants")
    the user clicks the button/link    name=publish
    the user navigates to the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/funding
    the user should see the text in the page    Assessor Feedback
    the user can see the option to upload a file on the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_1}
    the user uploads the file    id=assessorFeedback    ${valid_pdf}
    the user can see the option to upload a file on the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_2}
    the user uploads the file    id=assessorFeedback    ${valid_pdf}
    the user navigates to the page    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/funding
    the user clicks the button/link    jQuery=.button:contains("Publish assessor feedback")
    the user clicks the button/link    name=publish

the users fill out project details
    When Log in as a different user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    When Log in as a different user    steve.smith@empire.com    Passw0rd
    Then the user navigates to the page    ${la_fromage_overview}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project Manager
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=button:contains("Submit")

the user fills in project costs
    Input Text    name=costs[0].value    £ 8,000
    Input Text    name=costs[1].value    £ 2,000
    Input Text    name=costs[2].value    £ 10,000
    Input Text    name=costs[3].value    £ 10,000
    Input Text    name=costs[4].value    £ 10,000
    Input Text    name=costs[5].value    £ 10,000
    Input Text    name=costs[6].value    £ 10,000
    the user moves focus to the element    css=[for="costs-reviewed"]
    the user sees the text in the element    css=#content tfoot td    £ 60,000
    the user should see that the element is disabled    jQuery=.button:contains("Approve eligible costs")

project finance approves Viability for
    [Arguments]  ${partner}
    Given the user navigates to the page    ${server}/project-setup-management/project/${FUNDERS_PANEL_APPLICATION_1_PROJECT}/finance-check
    And the user should see the element     jQuery=table.table-progress tr:nth-child(${partner}) td:nth-child(2) a:contains("Review")
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
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(3)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(2) a:contains("Review")
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
   Then the user should see the text in the element    jQuery=table.table-progress tr:nth-child(2) td:nth-child(5)    ${rag_rating}
   And the user clicks the button/link    jQuery=table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Review")
   And the user should see the element    jQuery=.button:contains("Approve eligible costs"):not(.disabled)    # Checking here both that the button exists and that it isn't disabled


verify total costs of project
    [Arguments]    ${total_costs}
    the user should see the text in the element      jQuery=.table-overview tbody tr:nth-child(1) td:nth-child(2)     ${total_costs}

verify percentage and total
    [Arguments]  ${section}  ${percentage}  ${total}
    the user should see the element           jQuery=form section:nth-of-type(${section}) h3 span:contains("${percentage}")
    the user should see the element            jQuery=form section:nth-of-type(${section}) input[data-calculation-rawvalue^='${total}']

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
    the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(1) textarea   ${description}
    Click Element                               css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(2) label:nth-of-type(1)
    the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(3) input    12
    the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(4) input  ${net_value}
    the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(5) input   ${residual_value}
    the user enters text to a text field        css=form section:nth-of-type(4) #capital_usage div:nth-child(${row_number}) div:nth-of-type(6) input   ${utilization}

the user adds subcontracting data into row
    [Arguments]  ${row_number}  ${name}  ${cost}
    the user enters text to a text field        css=form section:nth-of-type(5) #subcontracting div:nth-child(${row_number}) div:nth-of-type(1) input   ${name}
    the user enters text to a text field        css=form section:nth-of-type(5) #subcontracting div:nth-child(${row_number}) div:nth-of-type(2) input   UK
    the user enters text to a text field        css=form section:nth-of-type(5) #subcontracting div:nth-child(${row_number}) div:nth-of-type(3) textarea   Develop
    the user enters text to a text field        css=form section:nth-of-type(5) #subcontracting div:nth-child(${row_number}) div:nth-of-type(4) input   ${cost}


the user adds travel data into row
    [Arguments]  ${row_number}  ${description}  ${number_of_times}  ${cost}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(1) input    ${description}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(2) input    ${number_of_times}
    the user enters text to a text field        css=#travel-costs-table tbody tr:nth-of-type(${row_number}) td:nth-of-type(3) input    ${cost}

Project finance user amends labour details in eligibility
    When the user clicks the button/link            jQuery=form section:nth-of-type(1) h3:contains("Labour")
    Then the user should see the element            jQuery=form section:nth-of-type(1) h3 span:contains("2%")
    When the user clicks the button/link            jQuery=form section:nth-of-type(1) a:contains("Edit")
    Then the user should see the element            css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    When the user clears the text from the element  css=[name^="labour-labourDaysYearly"]
    And the user enters text to a text field        css=[name^="labour-labourDaysYearly"]    230
    And the user adds data into labour row          1  test  120000  100
    Then verify percentage and total                1  21%  53734
    When the user clicks the button/link            jQuery=form section:nth-of-type(1) button:contains("Add another role")
    And the user adds data into labour row          13  test  14500  100
    Then verify percentage and total                1  23%  60039
    When the user clicks the button/link            css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(5) button
    Then verify percentage and total                1  23%  59778
    When the user clicks the button/link            jQuery=form section:nth-of-type(1) button[name=save-eligibility]
    Then verify total costs of project              £ 272,545
    And the user should see the element             jQuery=form section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element         jQuery=form section:nth-of-type(1) button[name=save-eligibility]


Project finance user amends materials details in eligibility
    When the user clicks the button/link            jQuery=form section:nth-of-type(3) h3:contains("Materials")
    Then verify percentage and total                3  37%  100200
    When the user clicks the button/link            jQuery=form section:nth-of-type(3) a:contains("Edit")
    And the user adds data into materials row       1  test  10  100
    Then verify percentage and total                3  25%  51100
    When the user clicks the button/link            jQuery=form section:nth-of-type(3) button[name=add_cost]
    And the user adds data into materials row       3  test  10  100
    Then verify percentage and total                3  25%  52100
    When the user clicks the button/link            css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                3  1%  2000
    When the user clicks the button/link            jQuery=form section:nth-of-type(3) button[name=save-eligibility]
    Then verify total costs of project              £ 174,345
    And the user should see the element            jQuery=form section:nth-of-type(1) a:contains("Edit")
    And the user should not see the element        jQuery=form section:nth-of-type(3) button[name=save-eligibility]

Project finance user amends capital usage details in eligibility
    When the user clicks the button/link            jQuery=form section:nth-of-type(4) h3:contains("Capital usage")
    Then the user should see the element            jQuery=form section:nth-of-type(4) h3 span:contains("0%")
    And the user should see the element            jQuery=form section:nth-of-type(4) input[value*='552']
    When the user clicks the button/link            jQuery=form section:nth-of-type(4) a:contains("Edit")
    And the user adds capital usage data into row   1  test  10600  500  50
    Then verify percentage and total                4  3%  5326
    When the user clicks the button/link            jQuery=form section:nth-of-type(4) button[name=add_cost]
    And the user adds capital usage data into row   4  test  10600  500  50
    Then verify percentage and total                4  6%  10376
    When the user clicks the button/link            css=form section:nth-of-type(4) #capital_usage div:nth-child(2) button
    Then verify percentage and total                 4  6%  10100
    When the user clicks the button/link           jQuery=form section:nth-of-type(4) button[name=save-eligibility]
    Then verify total costs of project             £ 183,893
    And the user should see the element           jQuery=form section:nth-of-type(4) a:contains("Edit")
    And the user should not see the element       jQuery=form section:nth-of-type(4) button[name=save-eligibility]


Project finance user amends subcontracting usage details in eligibility
    When the user clicks the button/link            jQuery=form section:nth-of-type(5) h3:contains("Subcontracting costs")
    Then the user should see the element            jQuery=form section:nth-of-type(5) h3 span:contains("49%")
    And the user should see the element            jQuery=form section:nth-of-type(5) input[value*='90,000']
    When the user clicks the button/link            jQuery=form section:nth-of-type(5) a:contains("Edit")
    And the user adds subcontracting data into row   1  test  10600
    Then verify percentage and total                 5  41%  55600
    When the user clicks the button/link            jQuery=form section:nth-of-type(5) button[name=add_cost]
    And the user adds subcontracting data into row   3  test  9400
    Then verify percentage and total                 5  45%  65000
    When the user clicks the button/link            css=form section:nth-of-type(5) #subcontracting div:nth-child(2) button
    Then verify percentage and total                 5  20%  20000
    When the user clicks the button/link           jQuery=form section:nth-of-type(5) button[name=save-eligibility]
    Then verify total costs of project              £ 113,893
    And the user should see the element           jQuery=form section:nth-of-type(5) a:contains("Edit")
    And the user should not see the element       jQuery=form section:nth-of-type(5) button[name=save-eligibility]

Project finance user amends travel details in eligibility
    Given the user clicks the button/link           jQuery=form section:nth-of-type(6) h3:contains("Travel and subsistence")
    Then the user should see the element            jQuery=form section:nth-of-type(6) h3 span:contains("5%")
    And the user should see the element            jQuery=form section:nth-of-type(6) input[value*='5,970']
    When the user clicks the button/link            jQuery=form section:nth-of-type(6) a:contains("Edit")
    And the user adds travel data into row          1  test  10  100
    Then verify percentage and total                 6  4%  3985
    When the user clicks the button/link            jQuery=form section:nth-of-type(6) button[name=add_cost]
    And the user adds travel data into row          3  test  10  100
    Then verify percentage and total                 6  5%  4985
    When the user clicks the button/link            css=#travel-costs-table tbody tr:nth-of-type(2) td:nth-of-type(5) button
    Then verify percentage and total                 6  2%  2000
    When the user clicks the button/link           jQuery=form section:nth-of-type(6) button[name=save-eligibility]
    Then verify total costs of project            £ 109,923
    And the user should see the element           jQuery=form section:nth-of-type(6) a:contains("Edit")
    And the user should not see the element       jQuery=form section:nth-of-type(6) button[name=save-eligibility]

Project finance user amends other costs details in eligibility
    When the user clicks the button/link            jQuery=form section:nth-of-type(7) h3:contains("Other costs")
    Then the user should see the element            jQuery=form section:nth-of-type(7) h3 span:contains("1%")
    And the user should see the element            jQuery=form section:nth-of-type(7) input[value*='1,100']
    When the user clicks the button/link            jQuery=form section:nth-of-type(7) a:contains("Edit")
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(1) td:nth-child(2) input  5000
    Then verify percentage and total                 7  6%  5550
    When the user clicks the button/link            jQuery=form section:nth-of-type(7) button[name=add_cost]
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(3) td:nth-child(1) textarea  some other costs
    And the user enters text to a text field        jQuery=#other-costs-table tr:nth-child(3) td:nth-child(2) input  5750
    Then verify percentage and total                 7  11%  11300
    When the user should see the element           css=#other-costs-table tr:nth-of-type(2) td:nth-of-type(3) button
    When the user clicks the button/link           jQuery=form section:nth-of-type(7) button[name=save-eligibility]
    Then verify total costs of project            £ 120,123
    And the user should see the element           jQuery=form section:nth-of-type(7) a:contains("Edit")
    And the user should not see the element       jQuery=form section:nth-of-type(7) button[name=save-eligibility]
