*** Settings ***
Documentation     IFS-7313  New completion stage for Procurement - Comp setup journey
...
...               IFS-7314  New completion stage for Procurement - applicant journey
...
...               IFS-7315  New completion stage- comp transfers to 'previous'
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${sbriType1CompetitionName}      SBRI Type 1 Competition
${sbriType1ApplicationTitle}     New Test Application for SBRI Users
&{sbriLeadCredentials}           email=${lead_applicant}    password=${short_password}
&{sbriPartnerCredentials}        email=trudy@gmail.com      password=${correct_password}

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

Comp admin complete the SBRI competition setup
    [Documentation]  IFS-7313
    Given the user completes all sections of competition setup     ${business_type_id}  SBRI Type 1  ${compType_Programme}  yes  1  true  collaborative
    When the user clicks the button/link                           jQuery = a:contains("Complete")
    And the user clicks the button/link                            jQuery = button:contains('Done')
    And the user navigates to the page                             ${CA_UpcomingComp}
    Then the user should see the element                           jQuery = h2:contains("Ready to open") ~ ul a:contains("${sbriType1CompetitionName}")

Comp admin sets SBRI competition to live
     [Documentation]  IFS-7313
     Given Get competition id and set open date to yesterday     ${sbriType1CompetitionName}
     When the user navigates to the page                         ${CA_Live}
     Then the user should see the element                        jQuery = h2:contains('Open') ~ ul a:contains('${sbriType1CompetitionName}')
     [Teardown]  Logout as user

Lead applicant can submit SBRI application
    [Documentation]  IFS-7314
    Given the user starts SBRI application and adds a partner organisation
    When uk lead applicant completes application form
    And partner submits application details
    And lead submits SBRI application
    Then the user should see the element     jQuery = h2:contains("What happens next?")

Awaitting assesment status should not display for SBRI submitted applications
    [Documentation]  IFS-7314
    When the user clicks the button/link         link = Applications
    Then the user should not see the element     jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-deadline-waiting:contains("Awaiting assessment")
    And the user should see the element          jQuery = .in-progress li:contains("${sbriType1ApplicationTitle}") .msg-progress:contains("Application submitted")

Internal users should only see key statistics and competition navigation links related to applications when the SBRI competition is open
    [Documentation]  IFS-7315
    #[Setup]  Requesting sbri competition ID
    Given log in as a different user                                                 &{internal_finance_credentials}
    When the user navigates to the page                                              ${server}/management/competition/${competitionId}
    Then the user should see only application related key statistics
    And the user should see only application related competition navigation links

Internal users should only see competition close completion milestones when the SBRI competition is open
    [Documentation]  IFS-7315
    Then the user should see only competition close completion stage milestones

Internal users should only see application related key statistics in applications page when the SBRI competition is open
    [Documentation]  IFS-7315
    When the user clicks the button/link                                                     link = Applications: All, submitted, ineligible
    Then the user should see only application related key statistics in applications page

Lead applicant can see SBRI applications in previous section when the competition is closed
    [Documentation]  IFS-7314
    Given moving competition to Closed        ${competitionId}
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
    Then the user should see the element     link = ${sbriType1CompetitionName}

Internal users can see SBRI application in previous tab with submitted status
    [Documentation]  IFS-7315
    Given the user navigates to the page      ${server}/management/competition/${competitionId}/previous
    When the user clicks the button/link      id = accordion-previous-heading-2
    Then the user should see the element      jQuery = td:contains("Submitted")

*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser            &{Comp_admin1_credentials}
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
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.day input  ${i}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.month input  ${month}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.year input  ${nextyear}
      \    ${i} =   Evaluate   ${i} + 1
    the user clicks the button/link              jQuery = button:contains("Done")

the user should see milestones section marked as complete
    the user clicks the button/link     link = Competition details
    the user should see the element     jQuery = div:contains("Milestones") ~ .task-status-complete

the user completes all sections of competition setup
    [Arguments]    ${orgType}  ${extraKeyword}  ${compType}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects procurement Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility              ${orgType}             ${researchParticipation}    ${researchCategory}  ${collaborative}  # 1 means 30%
    the user selects the organisational eligibility to no     false
    the user marks the Application as done                    ${projectGrowth}       ${compType}
    the user fills in the CS Assessors
    the user clicks the button/link                           link = Public content
    the user fills in the Public content and publishes        ${extraKeyword}
    the user clicks the button/link                           link = Return to setup overview

the user sign in and apply for SBRI Type 1 competition
    [Arguments]  ${user}  ${password}   ${competitionName}
    the user select the competition and starts application      ${competitionName}
    the user clicks the button/link                             jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    Logging in and Error Checking                               ${user}  ${password}

partner organisation accepts the invite to collaborate
    [Arguments]  ${email}  ${compName}  ${businessTypeId}
    Logout as user
    the user reads his email and clicks the link     ${email}  Invitation to collaborate in ${compName}  You are invited by  2
    The user clicks the button/link                  jQuery = a:contains("Yes, accept invitation")
    The user completes the new account creation      ${email}  ${businessTypeId}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

the user starts SBRI application and adds a partner organisation
    the user sign in and apply for SBRI Type 1 competition     ${lead_applicant}  ${short_password}  ${sbriType1CompetitionName}
    the user clicks the button/link                            id = save-organisation-button
    the user clicks the button/link                            link = Application team
    the user clicks the button/link                            link = Add a partner organisation
    the user adds a new partner                                Sbri Ltd  FName SName  trudy@gmail.com
    partner organisation accepts the invite to collaborate     trudy@gmail.com  ${sbriType1CompetitionName}  ${BUSINESS_TYPE_ID}

uk lead applicant completes application form
    log in as a different user                                      &{sbriLeadCredentials}
    the user navigates to the page                                  ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link                                 link = Untitled application (start here)
    the user fills in procurement Application details               ${sbriType1ApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the lead applicant fills all the questions and marks as complete(programme)
    the user selects research category from funding                 Feasibility studies
    the user navigates to Your-finances page                        ${sbriType1ApplicationTitle}
    the user marks the finance as complete                          ${sbriType1ApplicationTitle}   Calculate  52,214
    the user accept the competition terms and conditions            Return to application overview

the user marks the finance as complete
    [Arguments]  ${application}  ${overheadsCost}  ${totalCosts}
    the user fills the procurement project costs                          ${overheadsCost}  ${totalCosts}
    the user checks the VAT calculations
    the user enters the project location
    the user fills the organisation details with Project growth table     ${application}  ${SMALL_ORGANISATION_SIZE}
    the user clicks the button/link                                       link = Back to application overview
    the user should see the element                                       jQuery = li:contains("Your project finances") > .task-status-complete

the user fills in procurement Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user clicks the button/link       link = Application details
    the user enters text to a text field  css = [id="name"]  ${appTitle}
    the user enters text to a text field  id = startDate  ${tomorrowday}
    the user enters text to a text field  css = #application_details-startdate_month  ${month}
    the user enters text to a text field  css = #application_details-startdate_year  ${nextyear}
    the user enters text to a text field  css = [id="durationInMonths"]  24
    the user selects the value from the drop-down menu   INNOVATE_UK_WEBSITE   id = competitionReferralSource
    the user selects the radio button     START_UP_ESTABLISHED_FOR_LESS_THAN_A_YEAR   company-age-less-than-one
    the user selects the value from the drop-down menu   BANKS_AND_INSURANCE   id = companyPrimaryFocus
    the user clicks the button twice      css = label[for="resubmission-no"]
    the user should not see the element   link = Choose your innovation area
    the user clicks the button/link       id = application-question-complete
    the user clicks the button/link       link = Back to application overview
    the user should see the element       jQuery = li:contains("Application details") > .task-status-complete

the user adds a new partner
    [Arguments]   ${partnerOrgName}  ${persFullName}  ${email}
    the user enters text to a text field     id = organisationName  ${partnerOrgName}
    the user enters text to a text field     id = name  ${persFullName}
    the user enters text to a text field     id = email  ${email}
    the user clicks the button/link          jQuery = .govuk-button:contains("Invite partner organisation")
    the user should see the element          jQuery = h2:contains(${partnerOrgName})

the user selects research category from funding
    [Arguments]  ${res_category}
    the user clicks the button/link   link = Research category
    the user clicks the button twice  jQuery = label:contains("${res_category}")
    the user clicks the button/link   id = application-question-complete
    the user clicks the button/link   link = Back to application overview
    the user should see the element   jQuery = li:contains("Research category") > .task-status-complete

the user checks the VAT calculations
    the user clicks the button/link                css = label[for="stateAidAgreed"]
    the user clicks the button/link                jQuery = button:contains("Mark as complete")
    the user should see a field and summary error  Select if you are VAT registered
    the user selects the radio button              vatForm.registered  false
    the user clicks the button/link                jQuery = button:contains("Mark as complete")
    the user clicks the button/link                link = Back to application overview
#   the user clicks the button/link                link = Review and submit
#   the user clicks the button/link                link = Application overview
    the user clicks the button/link                link = Your project finances

partner submits application details
    log in as a different user                               &{sbriPartnerCredentials}
    the user clicks the button/link                          link = ${sbriType1ApplicationTitle}
    the user clicks the button/link                          link = Your project finances
    the user marks the finance as complete                   ${sbriType1ApplicationTitle}  Calculate  52,214
    the user accept the competition terms and conditions     Return to application overview

Requesting sbri competition ID
    ${sbriCompetitionId} =  get comp id from comp title    ${sbriType1CompetitionName}
    Set suite variable      ${sbriCompetitionId}

lead submits SBRI application
    Log in as a different user                      &{sbriLeadCredentials}
    the user clicks the button/link                 link = ${sbriType1ApplicationTitle}
    the applicant submits the application

the user should see only application related key statistics
    the user should not see the element     jQuery = small:contains("Assessors invited")
    the user should not see the element     jQuery = small:contains("Invitations accepted")
    the user should not see the element     jQuery = small:contains("Applications per assessor")
    the user should see the element         jQuery = small:contains("Applications started")
    the user should see the element         jQuery = small:contains("Applications beyond 50%")
    the user should see the element         jQuery = small:contains("Applications submitted")

the user should see only application related competition navigation links
    the user should see the element         jQuery = a:contains("Applications: All, submitted, ineligible")
    the user should not see the element     jQuery = a:contains("Invite assessors to assess the competition")
    the user should not see the element     jQuery = a:contains("Manage assessments")
    the user should not see the element     jQuery = a:contains("Manage assessment panel")
    the user should not see the element     jQuery = a:contains("Manage interview panel")
    the user should not see the element     jQuery = a:contains("Input and review funding decision")

the user should see only competition close completion stage milestones
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

the user should see only application related key statistics in applications page
    the user should not see the element     jQuery = small:contains("Assessors invited")
    the user should see the element         jQuery = small:contains("Applications beyond 50%")
    the user should see the element         jQuery = small:contains("Applications submitted")
    the user should see the element         jQuery = small:contains("Ineligible applications")

