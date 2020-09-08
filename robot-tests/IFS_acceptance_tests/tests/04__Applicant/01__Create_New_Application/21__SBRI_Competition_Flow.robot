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
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${sbriType1ApplicationTitle}     SBRI type one application
${sbriType1CompetitionName}      SBRI Type 1 Competition
${openSBRICompetitionName}       SBRI type one competition
${openSBRICompetitionId}         ${competition_ids["${openSBRICompetitionName}"]}
&{sbriLeadCredentials}           email=troy.ward@gmail.com     password=${short_password}
&{sbriPartnerCredentials}        email=eve.smith@gmail.com     password=${short_password}
${sbriComp654Name}               The Sustainable Innovation Fund: SBRI phase 1
${sbriComp654Id}                 ${competition_ids["${sbriComp654Name}"]}
${sbriProjectName}               Procurement application 1
${sbriProjectId}                 ${project_ids["${sbriProjectName}"]}
${sbriProjectName2}              Procurement application 2
${sbriProjectId2}                ${project_ids["${sbriProjectName2}"]}
${sbriApplicationId}             ${application_ids["${sbriProjectName}"]}
${yourProjFinanceLink}           your project finances
${viewFinanceChangesLink}        View changes to finances
${inclusiveOfVATHeading}         Total project costs inclusive of VAT
${totalProjCosts}                Total project cost
${vatRegistered}                 Are you VAT registered
${totalWithVAT}                  £265,084
${totalWithoutVAT}               £220,903
${initialFunding}                £77,057
${revisedFunding}                £63,803
${vatTotal}                      £44,181
${currentAmount}                 Current amount
${fundingAppliedFor}             Funding applied for
${totalVAT}                      Total VAT

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

Awaiting assessment status should not display for SBRI submitted applications
    [Documentation]  IFS-7314
    When log in as a different user              &{sbriLeadCredentials}
    Then the user should not see the element     jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-deadline-waiting:contains("Awaiting assessment")
    And the user should see the element          jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-progress:contains("Application submitted")

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

Partner applicant can see SBRI applications in previous section when the competition is closed
    [Documentation]  IFS-7314
    When log in as a different user           &{sbriPartnerCredentials}
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
    Given the user clicks the button/link           link = Finance checks
    When the user clicks the button/link            css = .viability-0
    Then the user should not see the element        css = .table-overview
    [Teardown]  The user clicks the button/link     link = Finance checks

Internal user can generate spend profile
    [Documentation]   IFS-8048
    Given generate spend profile
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
    [Documentation]  IFS-8202
    Given internal user generates the contract     ${sbriProjectId2}
    When the user navigates to the page            ${server}/project-setup-management/competition/${sbriComp654Id}/status/all
    Then the user should see the element           jQuery = tr:contains("${sbriProjectName2}") td:contains("Pending")

External user of international org should not see bank details
    [Documentation]  IFS-8202
    Given log in as a different user             ${lead_international_email}	${short_password}
    When the user clicks the button/link         link = ${sbriProjectName2}
    Then the user should not see the element     jQuery = li:contains("Bank details")

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
    the user fills in the CS Initial details     ${competitionName}  ${month}  ${nextyear}  ${compType_Programme}  2   ${fundingType}

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
    the user clicks the button/link     link = Competition details
    the user should see the element     jQuery = div:contains("Milestones") ~ .task-status-complete

the user assign the stakeholder to the SBRI competition
    log in as a different user          &{ifs_admin_user_credentials}
    the user navigates to the page      ${server}/management/competition/setup/${openSBRICompetitionId}/manage-stakeholders
    the user clicks the button/link     jQuery = td:contains("Rayon Kevin") button[type="submit"]

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
    the user clicks the button/link         link = Finance checks
    the user should see the element         jQuery = dt:contains("${totalProjCosts}") ~ dd:contains("${totalWithoutVAT}") ~ dt:contains("${fundingAppliedFor}") ~ dd:contains("${initialFunding}") ~ dt:contains("${currentAmount}") ~ dd:contains("${revisedFunding}")
    the user clicks the button/link         css = .eligibility-0

the user should see calculations with VAT
    the user should see the element     jQuery = div:contains("${inclusiveOfVATHeading}") ~ div:contains("${totalWithVAT}")
    the user clicks the button/link     link = Finance checks
    the user should see the element     jQuery = dt:contains("${totalProjCosts}") ~ dd:contains("${totalWithVAT}") ~dt:contains("${fundingAppliedFor}") ~ dd:contains("${initialFunding}") ~ dt:contains("${currentAmount}") ~ dd:contains("${initialFunding}")
    the user clicks the button/link     css = .eligibility-0

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
    the user clicks the button/link     id = generate-spend-profile-modal-button

internal user generates the contract
    [Arguments]  ${projectID}
    the user navigates to the page     ${server}/project-setup-management/project/${projectID}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${contract_pdf}
    the user should see the element    jQuery = a:contains("Contract.pdf (opens in a new window)")
    #horrible hack but we need to wait for virus scanning
    sleep  5s
    the user selects the checkbox      confirmation
    the user clicks the button/link    jQuery = button:contains("Send Contract to project team")
    the user clicks the button/link    jQuery = button:contains("Send contract")