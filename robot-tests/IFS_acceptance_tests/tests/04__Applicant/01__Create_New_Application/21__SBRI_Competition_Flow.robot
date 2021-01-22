*** Settings ***
Documentation     IFS-7313  New completion stage for Procurement - Comp setup journey
...
...               IFS-7314  New completion stage for Procurement - applicant journey
...
...               IFS-7315  New completion stage- comp transfers to 'previous'
...
...               IFS-8127  SBRI Type 4: Finance check design improvements
...
...               IFS-8126  SBRI Type 4: Project setup VAT
...
...               IFS-8048  SBRI Type 4: Spend profile for pilot SBRI competition into project setup
...
...               IFS-8012  SBRI Type 4: Project finance view of assessor feedback
...
...               IFS-8202  SBRI - Ability to generate a contract for an international applicant
...
...               IFS-8199  SBRI Type 4: email notification content changes
...
...               IFS-8198  SBRI Type 4: Contract section content changes for procurements (replacing GOL)
...
...               IFS-8942  SBRI Milestones - Edit project duration in project setup
...
...               IFS-8965  SBRI Milestones - JS Milestones Page & validation - Application
...
...               IFS-8943  SBRI Milestones - Ability to raise queries / notes in project setup
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${sbriMilestonesApplicationTitle}     SBRI payment milestones application
${sbriType1ApplicationTitle}          SBRI type one application
${sbriType1CompetitionName}           SBRI Type 1 Competition
${openSBRICompetitionName}            SBRI type one competition
${openSBRICompetitionId}              ${competition_ids["${openSBRICompetitionName}"]}
&{sbriLeadCredentials}                email=troy.ward@gmail.com     password=${short_password}
&{sbriPartnerCredentials}             email=eve.smith@gmail.com     password=${short_password}
${sbriComp654Name}                    The Sustainable Innovation Fund: SBRI phase 1
${sbriComp654Id}                      ${competition_ids["${sbriComp654Name}"]}
${sbriProjectName}                    Procurement application 1
${sbriProjectId}                      ${project_ids["${sbriProjectName}"]}
${sbriProjectName2}                   Procurement application 2
${sbriProjectId2}                     ${project_ids["${sbriProjectName2}"]}
${sbriApplicationId}                  ${application_ids["${sbriProjectName}"]}
${sbriApplicationId2}                 ${application_ids["${sbriProjectName2}"]}
${yourProjFinanceLink}                your project finances
${viewFinanceChangesLink}             View changes to finances
${inclusiveOfVATHeading}              Total project costs inclusive of VAT
${totalProjCosts}                     Total project cost
${vatRegistered}                      Are you VAT registered
${totalWithVAT}                       £265,084
${totalWithoutVAT}                    £220,903
${initialFunding}                     £265,084
${revisedFunding}                     £218,435
${vatTotal}                           £44,181
${currentAmount}                      Current amount
${fundingAppliedFor}                  Funding applied for
${totalVAT}                           Total VAT
${payment_query_title}                Payment Milestone Query

*** Test Cases ***
Comp admin saves the completition stage with competition close option
    [Documentation]  IFS-7313
    Given the user completes initial details of the competition        ${sbriType1CompetitionName}  PROCUREMENT
    When the user navigates to completition stage
    And the user saves the completion stage with competition close     COMPETITION_CLOSE
    Then the user should see competition close in read only page       Competition close

Comp admin edits the completition stage with competition close option
    [Documentation]  IFS-7313
    Given the user clicks the button/link                               jQuery = button:contains("Edit")
    When the user saves the completion stage with competition close     COMPETITION_CLOSE
    Then the user should see competition close in read only page        Competition close

Comp admin complete the SBRI milestones
    [Documentation]  IFS-7313
    Given the user clicks the button/link                                  jQuery = span:contains("Milestones")
    When the user fills in the competition close Milestones
    Then the user should see the correct inputs in the Milestones form
    And the user should see milestones section marked as complete

Project duration validation in application payment milestones if project duration not completed in application details
    [Documentation]  IFS-8938  IFS-8965
    Given log in as a different user                &{sbriLeadCredentials}
    And the user creates a new sbri application
    When the user clicks the button/link            link = Your project finances
    And the user clicks the button/link             link = Your payment milestones
    Then the user should see the element            jQuery = li:contains("provide a project duration")
    And the user should see the element             link = application details

Applicant can add payment milestones on completing application details with project duration
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link               link = application details
    When the user fills in SBRI Application details     ${sbriMilestonesApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    And the user clicks the button/link                 link = Your project finances
    And the user clicks the button/link                 link = Your payment milestones
    Then the user should see the element                jQuery = button:contains("Add another project milestone")
    And the user should see the element                 jQuery = h1:contains("Payment milestones")
    And the user should see the element                 id= mark-all-as-complete

Applicant should see project cost banner in payment milestones when the project costs not completed
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link                            link = Your project finances
    When the user fills the procurement project costs                Calculate  52,214
    And the user selects the radio button                            vatForm.registered  true
    And the user clicks the button/link                              link = Your project finances
    And the user clicks the button/link                              link = Your payment milestones
    Then the user should see total project costs and banner info

Applicant should not see project cost banner in payment milestones when the project costs completed
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link        link = Your project finances
    And the user clicks the button/link          link = Your project costs
    When the user clicks the button/link         css = label[for="stateAidAgreed"]
    And the user clicks the button/link          jQuery = button:contains("Mark as complete")
    And the user clicks the button/link          link = Your payment milestones
    Then the user should not see the element     jQuery = p:contains("Your project costs of £72,839 have not been marked as complete.")

Payment milestones validations: empty fileds
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link           jQuery = button:contains(Open all)
    When the user clicks the button/link            id = mark-all-as-complete
    And the user should see validation messages

Payment milestones validations: payment milestone cost is less than project cost
    [Documentation]  IFS-8938  IFS-8965
    Given the user selects the option from the drop-down menu     1  css = select[id^="milestones"][id$="month"]
    And the user enters text to a text field                      css = input[id^="milestones"][id$="payment"]    1000
    And the user enters text to a text field                      css = textarea[id^="milestones"][id$="taskOrActivity"]   Task Or Activity 1
    When the user clicks the button/link                          id = mark-all-as-complete
    Then the user should see a field and summary error            Your payment milestones are lower than 100% of your project costs. You must increase your payment requests or adjust your project costs.

Payment milestones validations: payment milestone cost is more than project cost
    [Documentation]  IFS-8938  IFS-8965
    When the user enters text to a text field              css = input[id^="milestones"][id$="payment"]    100000
    And the user clicks the button/link                    id = mark-all-as-complete
    Then the user should see a field and summary error     Your payment milestones exceeds 100% of your project costs. You must lower your payment requests or adjust your project costs.

Applicant adds a first payment milestone
    [Documentation]  IFS-8938  IFS-8965
    Given applicant fills in payment milestone                  accordion-finances-content  1  Milestone 1  10000   Task Or Activity 1   Deliverable 1   Success Criteria 1
    When the user clicks the button/link                        jQuery = button:contains("Save and return to project finances")
    Then applicant views saved payment milestones               1  £10,000  Milestone 1  13.73%  £10,000  13.73%
    And applicant views saved payment milestones subsection     Task Or Activity 1   Deliverable 1   Success Criteria 1

Applicant adds another payment milestone
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link                           jQuery = button:contains("Add another project milestone")
    And the user clicks the button/link                             jQuery = button:contains("Open all")
    When applicant fills in payment milestone                       accordion-finances-content-unsaved  5  Milestone 2  62839   Task Or Activity 2   Deliverable 2   Success Criteria 2
    And the user clicks the button/link                             id = mark-all-as-complete
    Then applicant views saved payment milestones                   5  £62,839  Milestone 2  86.27%  £72,839  100%
    And applicant views readonly payment milestones subsections     Task Or Activity 2   Deliverable 2   Success Criteria 2
    And the user should see the element                             jQuery = li:contains("Your payment milestones") > .task-status-complete

Applicant can edit and remove the payment milestone
    [Documentation]  IFS-8938  IFS-8965
    Given the user clicks the button/link           link = Your payment milestones
    When the user clicks the button/link            jQuery = button:contains("Edit your payment milestones")
    And the user clicks the button/link             jQuery = button:contains("Add another project milestone")
    And the user enters text to a text field        css = [id^="accordion-finances-content-unsaved"] input[id^="milestones"][id$="description"]   Milestone to remove
    And the user clicks the button/link             jQuery = div[id='accordion-finances'] div:nth-of-type(4) .js-remove-row:contains("Remove")
    Then the user should not see the element        jQuery = div h4:contains("Milestone") ~ div button:contains("Milestone to remove")
    [Teardown]  the user clicks the button/link     id = mark-all-as-complete

Awaiting assessment status should not display for SBRI submitted applications
    [Documentation]  IFS-7314
    When log in as a different user              &{sbriLeadCredentials}
    Then the user should not see the element     jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-deadline-waiting:contains("Awaiting assessment")
    And the user should see the element          jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-progress:contains("Submitted")

Innovation Lead should only see key statistics and competition navigation links related to applications when the SBRI competition is open
    [Documentation]  IFS-7315
    Given log in as a different user                                                 &{innovation_lead_one}
    When the user navigates to the page                                              ${server}/management/competition/${openSBRICompetitionId}
    Then the user should only see application related key statistics
    And the user should only see application related competition navigation links

Innovation Lead should only see competition close completion milestones when the SBRI competition is open
    [Documentation]  IFS-7315
    Then the user should only see competition close completion stage milestones

Innovation Lead should only see application related key statistics in applications page when the SBRI competition is open
    [Documentation]  IFS-7315
    When the user clicks the button/link                                                     link = Applications: Submitted, ineligible
    Then the user should only see application related key statistics in applications page

Stakeholder should only see key statistics and competition navigation links related to applications when the SBRI competition is open
    [Documentation]  IFS-7315
    Given the user assign the stakeholder to the SBRI competition
    And log in as a different user                                                    &{stakeholder_user}
    When the user navigates to the page                                               ${server}/management/competition/${openSBRICompetitionId}
    Then the user should only see application related key statistics
    And the user should only see application related competition navigation links

Stakeholder should only see competition close completion milestones when the SBRI competition is open
    [Documentation]  IFS-7315
    Then the user should only see competition close completion stage milestones

Stakeholder should only see application related key statistics in applications page when the SBRI competition is open
    [Documentation]  IFS-7315
    When the user clicks the button/link                                                     link = Applications: Submitted, ineligible
    Then the user should only see application related key statistics in applications page

Internal users should only see key statistics and competition navigation links related to applications when the SBRI competition is open
    [Documentation]  IFS-7315
    Given log in as a different user                                                 &{internal_finance_credentials}
    When the user navigates to the page                                              ${server}/management/competition/${openSBRICompetitionId}
    Then the user should only see application related key statistics
    And the user should only see application related competition navigation links

Internal users should only see competition close completion milestones when the SBRI competition is open
    [Documentation]  IFS-7315
    Then the user should only see competition close completion stage milestones

Internal users should only see application related key statistics in applications page when the SBRI competition is open
    [Documentation]  IFS-7315
    When the user clicks the button/link                                                     link = Applications: All, submitted, ineligible
    Then the user should only see application related key statistics in applications page

Lead applicant can see SBRI applications in previous section when the competition is closed
    [Documentation]  IFS-7314
    Given moving competition to Closed        ${openSBRICompetitionId}
    When log in as a different user           &{sbriLeadCredentials}
    Then the user should see the element      jQuery = .previous li:contains("${sbriType1ApplicationTitle}") .msg-progress:contains("Application submitted")

Internal users can see SBRI competition in previous tab
    [Documentation]  IFS-7315
    Given log in as a different user         &{ifs_admin_user_credentials}
    When the user clicks the button/link     jQuery = a:contains("Previous")
    Then the user should see the element     link = ${openSBRICompetitionName}

Internal users can see SBRI application in previous tab with submitted status
    [Documentation]  IFS-7315
    Given the user navigates to the page      ${server}/management/competition/${openSBRICompetitionId}/previous
    When the user clicks the button/link      id = accordion-previous-heading-2
    Then the user should see the element      jQuery = td:contains("Submitted")

External user finance overview link is not shown
    [Documentation]    IFS-8127
    Given log in as a different user             &{becky_mason_credentials}
    When the user navigates to the page          ${server}/project-setup/project/${sbriProjectId}/finance-checks
    Then the user should not see the element     jQuery = project finance overview

External user finances details are correct
    [Documentation]    IFS-8127   IFS-8126
    When the user clicks the button/link                             link = ${yourProjFinanceLink}
    Then the user should not see the element                         link = ${viewFinanceChangesLink}
    And the user should not see the element                          css = table-overview
    And the external user should see the correct VAT information

External user should not see the spend profile section
    [Documentation]  IFS-8048
    When the user navigates to the page          ${server}/project-setup/project/${sbriProjectId}
    Then the user should not see the element     jQuery = h2:contains("Spend profile")

Comp admin should not see feedback on the application
    [Documentation]  IFS-8012
    Given Log in as a different user             &{Comp_admin1_credentials}
    When the user navigates to the page          ${server}/management/competition/${sbriComp654Id}/application/${sbriApplicationId}
    Then the user should not see the element     jQuery = span:contains("Average score 7.0") ~ button:contains("Business opportunity")
    And the user should not see the element      jQuery = h2:contains("Assessor feedback") ~ ul li:contains("Assessor 1") p:contains("Perfect application")

Project finance should see the feedback on the application
    [Documentation]  IFS-8012
    Given Log in as a different user         &{internal_finance_credentials}
    When the user navigates to the page      ${server}/management/competition/${sbriComp654Id}/application/${sbriApplicationId}
    Then the user should see the element     jQuery = span:contains("Average score 7.0") ~ button:contains("Business opportunity")
    And the user should see the element      jQuery = h2:contains("Assessor feedback") ~ ul li:contains("Assessor 1") p:contains("Perfect application")

Internal user finance checks page
    [Documentation]    IFS-8127
    When the user navigates to the page                                 ${server}/project-setup-management/project/${sbriProjectId}/finance-check
    Then the user should see the correct data on finance check page

The project finance user is shown a validation message when duration is blank
    [Documentation]    IFS-8942
    Given the user clicks the button/link                  link = Edit
    When Clear Element Text                                durationInMonths
    And the user clicks the button/link                    jQuery = button:contains("Save and return to project finances")
    Then the user should see a field and summary error     This field cannot be left blank.

The project finance user is shown a validation message when duration is less than allowed
    [Documentation]    IFS-8942
    Given the user enters text to a text field             id = durationInMonths  1
    When the user clicks the button/link                   jQuery = button:contains("Save and return to project finances")
    Then the user should see a field and summary error     This cannot be less than the stated payment milestones. You will need to adjust these to change the duration.

The project finance user sets the duration back to a valid value
   [Documentation]    IFS-8942
    Given the user enters text to a text field             id = durationInMonths  3
    When the user clicks the button/link                   jQuery = button:contains("Save and return to project finances")
    Then the user should see the element                   jQuery = dd:contains("3 months")

Internal user eligibility page
    [Documentation]    IFS-8127
    When the user clicks the button/link         css = .eligibility-0
    Then the user should not see the element     css = .table-overview
    And the user should not see the element      link = Review all changes to project finances

Internal user can set VAT to no
    [Documentation]    IFS-8126
    Given the user clicks the button/link                 jQuery = div:contains("${vatRegistered}") ~ div a:contains("Edit")
    When the user selects the radio button                vatForm.registered  false
    And the user clicks the button/link                   jQuery = div:contains("${inclusiveOfVATHeading}") ~ div button:contains("Save")
    Then the user should see calculations without VAT

Internal user can set VAT to yes
    [Documentation]    IFS-8126
    Given the user clicks the button/link              jQuery = div:contains("${vatRegistered}") ~ div a:contains("Edit")
    When the user selects the radio button             vatForm.registered  true
    And the user clicks the button/link                jQuery = div:contains("${inclusiveOfVATHeading}") ~ div button:contains("Save")
    Then the user should see calculations with VAT

Internal user viability page
    [Documentation]    IFS-8127
    Given the user clicks the button/link           link = Back to finance checks
    When the user clicks the button/link            css = .viability-0
    Then the user should not see the element        css = .table-overview
    [Teardown]  The user clicks the button/link     link = Back to finance checks

Project finance sends a payment milestone query to lead organisation
    [Documentation]     IFS-8943
    Given Log in as a different user                                    &{ifs_admin_user_credentials}
    And the user navigates to the page                                  ${server}/project-setup-management/project/${sbriProjectId}/finance-check/organisation/116/query
    When the project finance user post a payment milestone query
    Then the user should see the element                                jQuery = button:contains("${payment_query_title}")

Project lead is able to view pending query on project dashboard
    [Documentation]     IFS-8943
    Given Log in as a different user          &{sbriProjectFinanceCredentials}
    When the user navigates to the page       ${server}/project-setup/project/${sbriProjectId}
    Then the user should see the element      jQuery = span:contains("Pending query")

Project lead responds to pending queries
    [Documentation]  IFS-8943
    Given the user navigates to the page         ${server}/project-setup/project/${sbriProjectId}/finance-checks
    When the user clicks the button/link         id = post-new-response-1
    Then the user responds to the query

Internal user can generate spend profile
    [Documentation]   IFS-8048
    Given Log in as a different user          &{internal_finance_credentials}
    And the user navigates to the page        ${server}/project-setup-management/project/${sbriProjectId}/finance-check
    When generate spend profile
    Then the user should see the element      css = .success-alert

Internal user should not see spend profile section
    [Documentation]  IFS-8048
    When the user navigates to the page          ${server}/project-setup-management/competition/${sbriComp654Id}/status/all
    Then the user should not see the element     jQuery = th:contains("Spend profile")
    And the data is in the database correctly

Internal user should see bank details complete for an international applicant
    [Documentation]  IFS-8202
    Given the user navigates to the page     ${server}/project-setup-management/competition/${sbriComp654Id}/status/all
    When the user clicks the button/link     jQuery = tr:contains("Procurement application 2") td:nth-of-type(5).status.ok
    Then the user should see the element     jQuery = span:contains("No action required")

Contract section is enabled without bank details
    [Documentation]  IFS-8202
    Given the user navigates to the page     ${server}/project-setup-management/project/${sbriProjectId2}/finance-check
    When generate spend profile
    And the user navigates to the page       ${server}/project-setup-management/competition/${sbriComp654Id}/status/all
    Then the user should see the element     jQuery = tr:contains("${sbriProjectName2}") td:contains("Review")

Internal user can send the contract
    [Documentation]  IFS-8202  IFS-8199  IFS-8198
    Given internal user generates the contract     ${sbriProjectId2}
    When the user navigates to the page            ${server}/project-setup-management/competition/${sbriComp654Id}/status/all
    Then the user should see the element           jQuery = tr:contains("${sbriProjectName2}") td:contains("Pending")
    And the user reads his email                   ${lead_international_email}     Your contract is available for project ${sbriApplicationId2}     We are pleased to inform you that your contract is now ready for you to sign

Check that the VAT value shows on finance table
    [Documentation]  IFS-8321
    Given log in as a different user             &{becky_mason_credentials}
    When the user navigates to the page          ${server}/project-setup/project/${sbriProjectId}/finance-checks/overview
    Then the user should see the element         jQuery = th:contains("Total VAT")

External user of international org should not see bank details
    [Documentation]  IFS-8202
    Given log in as a different user             ${lead_international_email}	${short_password}
    When the user clicks the button/link         link = ${sbriProjectName2}
    Then the user should not see the element     jQuery = li:contains("Bank details")

External user can upload the contract
     [Documentation]  IFS-8199  IFS-8198
     Given applicant uploads the contract
     When the internal user approve the contract     ${sbriProjectId2}
     Then the user reads his email                   ${lead_international_email}     Contract approved for project ${sbriApplicationId2}    We have accepted your signed contract for your project

*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser     &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database

the user completes initial details of the competition
    [Arguments]    ${competitionName}   ${fundingType}
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     ${competitionName}  ${month}  ${nextyear}  ${compType_Programme}  SUBSIDY_CONTROL   ${fundingType}

the user navigates to completition stage
    the user clicks the button/link     link = Initial details
    the user clicks the button/link     jQuery = span:contains("Completion stage")

the user saves the completion stage with competition close
    [Arguments]     ${completionStage}
    the user selects the radio button     selectedCompletionStage   ${completionStage}
    the user clicks the button/link       jQuery = button:contains("Done")

the user should see competition close in read only page
    [Arguments]     ${completionStageValue}
    the user clicks the button/link     jQuery = span:contains("Completion stage")
    the user should see the element     jQuery = strong:contains("${completionStageValue}")

the user fills in the competition close Milestones
    ${i} =  Set Variable   1
     :FOR   ${ELEMENT}   IN    @{sbriType1Milestones}
      \    the user enters text to a text field     jQuery = th:contains("${ELEMENT}") ~ td.day input  ${i}
      \    the user enters text to a text field     jQuery = th:contains("${ELEMENT}") ~ td.month input  ${month}
      \    the user enters text to a text field     jQuery = th:contains("${ELEMENT}") ~ td.year input  ${nextyear}
      \    ${i} =   Evaluate   ${i} + 1
    the user clicks the button/link     jQuery = button:contains("Done")

the user should see milestones section marked as complete
    the user clicks the button/link     link = Back to competition details
    the user should see the element     jQuery = div:contains("Milestones") ~ .task-status-complete

the user assign the stakeholder to the SBRI competition
    log in as a different user          &{ifs_admin_user_credentials}
    the user navigates to the page      ${server}/management/competition/setup/${openSBRICompetitionId}/manage-stakeholders
    the user clicks the button/link     jQuery = td:contains("Rayon Kevin") button[type="submit"]

the project finance user post a payment milestone query
    the user clicks the button/link                         link = Post a new query
    the user selects the option from the drop-down menu     Payment milestones      section
    the user enters text to a text field                    id = queryTitle  ${payment_query_title}
    the user enters text to a text field                    css = .editor    Payment milestone query
    the user clicks the button/link                         id = post-query

the user should only see application related key statistics
    the user should not see the element     jQuery = small:contains("Assessors invited")
    the user should not see the element     jQuery = small:contains("Invitations accepted")
    the user should not see the element     jQuery = small:contains("Applications per assessor")
    the user should see the element         jQuery = small:contains("Applications started")
    the user should see the element         jQuery = small:contains("Applications beyond 50%")
    the user should see the element         jQuery = small:contains("Applications submitted")

the user should only see application related competition navigation links
    ${STATUS}    ${VALUE} =     Run Keyword And Ignore Error Without Screenshots    the user should see the element    jQuery = a:contains("Applications: All, submitted, ineligible")
    Run Keyword If    '${status}' == 'FAIL'    the user should see the element    jQuery = a:contains("Applications: Submitted, ineligible")
    the user should not see the element     jQuery = a:contains("Invite assessors to assess the competition")
    the user should not see the element     jQuery = a:contains("Manage assessments")
    the user should not see the element     jQuery = a:contains("Manage assessment panel")
    the user should not see the element     jQuery = a:contains("Manage interview panel")
    the user should not see the element     jQuery = a:contains("Input and review funding decision")

the user should only see competition close completion stage milestones
    the user should see the element         jQuery = h3:contains("Open date")
    the user should see the element         jQuery = h3:contains("Briefing event")
    the user should see the element         jQuery = h3:contains("Submission date")
    the user should not see the element     jQuery = h3:contains("Allocate assessors")
    the user should not see the element     jQuery = h3:contains("Assessor briefing")
    the user should not see the element     jQuery = h3:contains("Assessor accepts")
    the user should not see the element     jQuery = h3:contains("Assessor deadline")
    the user should not see the element     jQuery = h3:contains("Line draw")
    the user should not see the element     jQuery = h3:contains("Assessment panel")
    the user should not see the element     jQuery = h3:contains("Panel date")
    the user should not see the element     jQuery = h3:contains("Funders panel")
    the user should not see the element     jQuery = h3:contains("Notifications")
    the user should not see the element     jQuery = h3:contains("Release feedback")

the user should only see application related key statistics in applications page
    the user should not see the element     jQuery = small:contains("Assessors invited")
    the user should see the element         jQuery = small:contains("Applications beyond 50%")
    the user should see the element         jQuery = small:contains("Applications submitted")
    the user should see the element         jQuery = small:contains("Ineligible applications")

the user should see the correct data on finance check page
    the user should see the element         jQuery = dt:contains("${totalProjCosts}") ~ dd:contains("${totalWithVAT}") ~dt:contains("${fundingAppliedFor}") ~ dd:contains("${initialFunding}") ~ dt:contains("${currentAmount}") ~ dd:contains("${initialFunding}")
    the user should not see the element     jQuery = dt:contains("Other public sector funding")
    the user should not see the element     jQuery = dt:contains("Total percentage grant")
    the user should not see the element     jQuery = a:contains("View"):contains("finances")

the user should see calculations without VAT
    the user should not see the element     jQuery = label:contains("${inclusiveOfVATHeading}")
    the user clicks the button/link         link = Back to finance checks
    the user should see the element         jQuery = dt:contains("${totalProjCosts}") ~ dd:contains("${totalWithoutVAT}") ~ dt:contains("${fundingAppliedFor}") ~ dd:contains("${initialFunding}") ~ dt:contains("${currentAmount}") ~ dd:contains("${totalWithoutVAT}")
    the user clicks the button/link         css = .eligibility-0

the user should see calculations with VAT
    the user should see the element     jQuery = div:contains("${inclusiveOfVATHeading}") ~ div:contains("${totalWithVAT}")
    the user clicks the button/link     link = Back to finance checks
    the user should see the element     jQuery = dt:contains("${totalProjCosts}") ~ dd:contains("${totalWithVAT}") ~dt:contains("${fundingAppliedFor}") ~ dd:contains("${initialFunding}") ~ dt:contains("${currentAmount}") ~ dd:contains("${initialFunding}")
    the user clicks the button/link     css = .eligibility-0

the user responds to the query
    the user enters text to a text field   css = .editor  Responding to query
    the user clicks the button/link        jQuery = .govuk-button:contains("Post response")
    the user should see the element        jQuery = p:contains("Your response has been sent and will be reviewed by Innovate UK.")

the external user should see the correct VAT information
    the user should see the element     jQuery = legend:contains("${vatRegistered}") ~ span:contains("Yes")
    the user should see the element     jQuery = div:contains("${totalVAT}") ~ div:contains("${vatTotal}")
    the user should see the element     jQuery = div:contains("${inclusiveOfVATHeading}") ~ div:contains("${totalWithVAT}")

the data is in the database correctly
     ${month1Costs} =  get spend profile value     other costs   ${sbriProjectId}  0
     ${month2Costs} =  get spend profile value     other costs   ${sbriProjectId}  1
     ${month3Costs} =  get spend profile value     other costs   ${sbriProjectId}  2
     ${month1VAT} =    get spend profile value     VAT   ${sbriProjectId}  0
     ${month2VAT} =    get spend profile value     VAT   ${sbriProjectId}  1
     ${month3VAT} =    get spend profile value     VAT   ${sbriProjectId}  2
     Should Be Equal As Integers   ${month1Costs}   55228
     Should Be Equal As Integers   ${month2Costs}   0
     Should Be Equal As Integers   ${month3Costs}   165675
     Should Be Equal As Integers   ${month1VAT}     11046
     Should Be Equal As Integers   ${month2VAT}     0
     Should Be Equal As Integers   ${month3VAT}     33135

Generate spend profile
    confirm viability                   0
    confirm eligibility                 0
    the user clicks the button/link     css = .generate-spend-profile-main-button

internal user generates the contract
    [Arguments]  ${projectID}
    the user navigates to the page     ${server}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${contract_pdf}
    the user should see the element    jQuery = a:contains("Contract.pdf (opens in a new window)")
    #horrible hack but we need to wait for virus scanning
    sleep  5s
    the user selects the checkbox      confirmation
    the user clicks the button/link    jQuery = button:contains("Send contract to project team")
    the user clicks the button/link    jQuery = button:contains("Send contract")

the user creates a new sbri application
    the user select the competition and starts application     ${openSBRICompetitionName}
    the user selects the radio button                          createNewApplication  true      #Yes, I want to create a new application.
    the user clicks the button/link                            jQuery = .govuk-button:contains("Continue")
    the user clicks the button/link                            css = .govuk-button[type="submit"]    #Save and continue

the user fills in SBRI Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element                        jQuery = h1:contains("Application details")
    the user enters text to a text field                   css = [id="name"]  ${appTitle}
    the user enters text to a text field                   id = startDate  ${tomorrowday}
    the user enters text to a text field                   css = #application_details-startdate_month  ${month}
    the user enters text to a text field                   css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field                   css = [id="durationInMonths"]  24
    the user selects the value from the drop-down menu     INNOVATE_UK_WEBSITE   id = competitionReferralSource
    the user selects the radio button                      START_UP_ESTABLISHED_FOR_LESS_THAN_A_YEAR   company-age-less-than-one
    the user selects the value from the drop-down menu     BANKS_AND_INSURANCE   id = companyPrimaryFocus
    the user can mark the question as complete
    the user should see the element                        jQuery = li:contains("Application details") > .task-status-complete

the user should see total project costs and banner info
    the user should see the element     jQuery = p:contains("Your project costs of £72,839 have not been marked as complete.")
    the user should see the element     jQuery = dt:contains("Total project costs")+ dd:contains("£72,839")
    the user should see the element     jQuery = span:contains("What should I put as a payment milestone?")
    the user should see the element     jQuery = p:contains("Enter the milestone and deliverable information. Where appropriate, link with a payment request.")

the user should see validation messages
    the user should see a field and summary error     Number of months completed must be selected.
    the user should see a field and summary error     You must state the milestone task or activity.
    the user should see a field and summary error     You must state the payment requested in pounds (£).
