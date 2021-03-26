*** Settings ***
Documentation     IFS-8994  Two new sets of terms & conditions required
...
...               IFS-9137  Update Subsidy control T&Cs for Innovate UK & ATI
...
...               IFS-9214 Add dual T&Cs to Subsidy Control Competitions
...
...               IFS-9116 Applicant Subsidy Basis Questionnaire and Declaration Confirmation (Application)
...
...               IFS-9233 Applicant can view and accept the correct T&Cs based on their determined Funding Rules
...
...               IFS-9200 View Correct T&Cs for each Organisation (Internal and External Users)
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${atiSubsidyControl}                 Aerospace Technology Institute (ATI) - Subsidy control (opens in a new window)
${atiStateAid}                       Aerospace Technology Institute (ATI) (opens in a new window)
${innovateUKSubsidyControl}          Innovate UK - Subsidy control (opens in a new window)
${innovateUKStateAid}                Innovate UK (opens in a new window)
${subsidyControlFundingComp}         Subsidy control competition
${leadSubsidyControlApplication}     Subsidy control application
${leadStateAidApplication}           State aid application
&{scLeadApplicantCredentials}        email=janet.howard@example.com     password=${short_password}
${subsidyControlCompetitionId}       ${competition_ids["${subsidyControlFundingComp}"]}
${assessor1_to_add}                  Addison Shannon
${assessor2_to_add}                  Alexis Colon
${assessor1_email}                   addison.shannon@gmail.com
${assessor2_email}                   alexis.colon@gmail.com

*** Test Cases ***
Creating a new comp to confirm ATI subsidy control T&C's
    [Documentation]  IFS-8994  IFS-9137  IFS-9124
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  47
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user selects the radio button       termsAndConditionsId  35
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Aerospace Technology Institute (ATI) - Subsidy control")
    And the user should see the element         jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Aerospace Technology Institute (ATI)")

ATI subsidy control t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    Given the user clicks the button/link                link = Return to setup overview
    And the user clicks the button/link                  link = Terms and conditions
    And the user clicks the button/link                  jQuery = button:contains("Edit")
    When the user clicks the button/link                 link = ${atiSubsidyControl}
    And select window                                    title = Terms and conditions of an ATI Programme grant - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an ATI Programme grant")
    And the user should see the element                  jQuery = li:contains("State Aid and Subsidy Control obligations")
    [Teardown]   the user closes the last opened tab

ATI State aid t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    When the user clicks the button/link        jQuery = button:contains("Done")
    And the user clicks the button/link         link = ${atiStateAid}
    And select window                           title = Terms and conditions of an ATI Programme grant - Innovation Funding Service
    Then the user should see the element        jQuery = h1:contains("Terms and conditions of an ATI Programme grant")
    And the user should see the element         jQuery = li:contains("State Aid obligations")
    And the user closes the last opened tab
    And the user clicks the button/link         jQuery = button:contains("Done")

ATI subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Creating a new comp to confirm Innovateuk subsidy control T&C's
    [Documentation]  IFS-8994  IFS-9137
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  44
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user selects the radio button       termsAndConditionsId  34
    Then the user clicks the button/link        jQuery = button:contains("Done")
    And the user should see the element         jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Innovate UK - Subsidy control")
    And the user should see the element         jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Innovate UK")

Innovateuk subsidy control t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    When the user clicks the button/link                 jQuery = button:contains("Edit")
    And the user clicks the button/link                  link = ${innovateUKSubsidyControl}
    And select window                                    title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element                  jQuery = li:contains("Subsidy Control/ State aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk State aid t&c's are correct
    [Documentation]  IFS-8994
    When the user clicks the button/link                 jQuery = button:contains("Done")
    And the user clicks the button/link                  link = ${innovateUKStateAid}
    And select window                                    title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element                  jQuery = li:contains("State Aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     jQuery = button:contains("Done")
    And the user clicks the button/link      link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Subsidy basis question should not display for EOI competition applications
    [Documentation]  IFS-9116
    Given log in as a different user                  &{scLeadApplicantCredentials}
    When existing user creates a new application      Expression of Interest: Quantum Computing algorithms for combating antibiotic resistance through simulation
    Then the user should not see the element          link = Subsidy basis

Lead applicant can not accept the terms and conditions without determining subsidy basis type
    [Documentation]  IFS-9116
    Given existing user creates a new application     ${subsidyControlFundingComp}
    And the user clicks the button/link               link = Application details
    And the user fills in the Application details     ${leadStateAidApplication}  ${tomorrowday}  ${month}  ${nextyear}
    When the user clicks the button/link              link = Award terms and conditions
    Then the user should see the element              link = Subsidy basis

Lead applicant can not complete funding details without determining subsidy basis type
    [Documentation]  IFS-9116
    Given the user clicks the button/link     link = Back to application overview
    When the user clicks the button/link      link = Your project finances
    And the user clicks the button/link       link = Your funding
    Then the user should see the element      link = subsidy basis
    And the user should see the element       link = research category
    And the user should see the element       link = your organisation

Subsidy basis validation messages should display on continuing without selecting the answer
    [Documentation]  IFS-9116
    Given the user clicks the button/link                  link = subsidy basis
    And the user clicks the button/link                    jQuery = button:contains("Next")
    When the user clicks the button/link                   jQuery = button:contains("Next")
    Then the user should see a field and summary error     You must select an answer.

Lead applicant declares subsidy basis as Northern Ireland Protocol when activites have a direct link to Northern Ireland
    [Documentation]  IFS-9116
    Given the user selects the subsidy basis option       Yes
    When the user completes subsidy basis declaration
    Then the user should see the element                  jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see the element                   jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("Yes")

Lead applicant declares subsidy basis as Northern Ireland Protocol when trading goods through Northern Ireland
    [Documentation]  IFS-9116
    Given the user starts the subsidy section again
    When the user selects the subsidy basis option       No
    And the user selects the subsidy basis option        Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                 jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see the element                  jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("No")
    And the user should see the element                  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+ td:contains("Yes")

Partner applicant can not accept the terms and conditions without determining subsidy basis type
    [Documentation]  IFS-9116
    Given the user clicks the button/link              link = Back to application overview
    And the lead invites already registered user       ${collaborator1_credentials["email"]}  ${subsidyControlFundingComp}
    When logging in and error checking                 jessica.doe@ludlow.co.uk  ${short_password}
    And the user clicks the button/link                css = .govuk-button[type="submit"]    #Save and continue
    And the user clicks the button/link                link = Award terms and conditions
    Then the user should see the element               link = Subsidy basis

Partner applicant can not complete funding details without determining subsidy basis type
    [Documentation]  IFS-9116
    Given the user clicks the button/link     link = Back to application overview
    When the user clicks the button/link      link = Your project finances
    And the user clicks the button/link       link = Your funding
    Then the user should see the element      link = subsidy basis
    And the user should see the element       link = your organisation

Partner applicant declares subsidy basis as EU-UK Trade and Cooperation Agreement
    [Documentation]  IFS-9116
    Given the user clicks the button/link                link = subsidy basis
    And the user clicks the button/link                  jQuery = button:contains("Next")
    When the user selects the subsidy basis option       No
    And the user selects the subsidy basis option        No
    And the user completes subsidy basis declaration
    Then the user should see the element                 jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("EU-UK Trade and Cooperation Agreement")
    And the user should see the element                  jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("No")
    And the user should see the element                  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+ td:contains("No")

Lead applicant completes state aid subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user                                                    &{scLeadApplicantCredentials}
    And the user clicks the button/link                                                 link = ${leadStateAidApplication}
    When the applicant completes Application Team
    And the applicant marks EDI question as complete
    And the lead applicant fills all the questions and marks as complete(programme)
    And the user navigates to Your-finances page                                        ${leadStateAidApplication}
    Then the user marks the finances as complete                                        ${leadStateAidApplication}  labour costs  54,000  yes

Lead applicant accepts state aid terms and conditions based on NI declaration
    [Documentation]  IFS-9233
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should not see the element       jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Partner completes project finances and terms and conditions of state aid application
    [Documentation]  IFS-9116
    Given log in as a different user                                jessica.doe@ludlow.co.uk  ${short_password}
    When the user navigates to Your-finances page                   ${leadStateAidApplication}
    Then the user marks the subsidy contol finances as complete     ${leadStateAidApplication}  labour costs  54,000  yes

Partner applicant can accept subsidy control terms and conditions based on NI declaration
    [Documentation]  IFS-9233
    And the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element         jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element          jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Lead applicant submits state aid subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user             &{scLeadApplicantCredentials}
    When the user clicks the button/link         link = ${leadStateAidApplication}
    And the user clicks the button/link          link = Review and submit
    Then the user should not see the element     jQuery = .task-status-incomplete
    And the user clicks the button/link          jQuery = .govuk-button:contains("Submit application")

Lead applicant creates subsidy control subsidy basis application and declares subsidy basis as EU-UK Trade and Cooperation Agreement
    [Documentation]  IFS-9116
    Given existing user creates a new application        ${subsidyControlFundingComp}
    And the user clicks the button/link                  link = Application details
    And the user fills in the Application details        ${leadSubsidyControlApplication}  ${tomorrowday}  ${month}  ${nextyear}
    And the user clicks the button/link                  link = Subsidy basis
    And the user clicks the button/link                  jQuery = button:contains("Next")
    When the user selects the subsidy basis option       No
    And the user selects the subsidy basis option        No
    And the user completes subsidy basis declaration
    Then the user should see the element                 jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("EU-UK Trade and Cooperation Agreement")
    And the user should see the element                  jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("No")
    And the user should see the element                  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+ td:contains("No")

Partner applicant declares subsidy basis as Northern Ireland Protocol when activites have a direct link to Northern Ireland
    [Documentation]  IFS-9116
    Given the user clicks the button/link                 link = Back to application overview
    And the lead invites already registered user          ${collaborator1_credentials["email"]}  ${subsidyControlFundingComp}
    When logging in and error checking                    jessica.doe@ludlow.co.uk  ${short_password}
    And the user clicks the button/link                   id = save-organisation-button
    And the user clicks the button/link                   link = Subsidy basis
    And the user clicks the button/link                   jQuery = button:contains("Next")
    And the user selects the subsidy basis option         Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                  jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see the element                   jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("Yes")

Partner applicant declares subsidy basis as Northern Ireland Protocol when trading goods through Northern Ireland
    [Documentation]  IFS-9116
    Given the user starts the subsidy section again
    When the user selects the subsidy basis option       No
    And the user selects the subsidy basis option        Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                 jQuery = p:contains("Based on your answers, your subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see the element                  jQuery = td:contains("Will the activities that you want Innovate UK to support, have a direct link to Northern Ireland?")+ td:contains("No")
    And the user should see the element                  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+ td:contains("Yes")

Lead applicant completes subsidy control subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user                                                    &{scLeadApplicantCredentials}
    And the user clicks the button/link                                                 link = ${leadSubsidyControlApplication}
    When the applicant completes Application Team
    And the applicant marks EDI question as complete
    And the lead applicant fills all the questions and marks as complete(programme)
    And the user completes the application research category                            Feasibility studies
    And the user navigates to Your-finances page                                        ${leadSubsidyControlApplication}
    Then the user marks the subsidy contol finances as complete                         ${leadSubsidyControlApplication}  labour costs  54,000  yes

Lead applicant can accept subsidy control terms and conditions based on NI declaration
    [Documentation]  IFS-9223
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element           jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Partner completes project finances and terms and conditions of subsidy control application
    [Documentation]  IFS-9116
    Given log in as a different user                  jessica.doe@ludlow.co.uk  ${short_password}
    When the user navigates to Your-finances page     ${leadSubsidyControlApplication}
    Then the user marks the finances as complete      ${leadSubsidyControlApplication}  labour costs  54,000  yes

Partner applicant can accept state aid terms and conditions based on NI declaration
    [Documentation]  IFS-9223
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should not see the element       jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Lead applicant submits subsidy control subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user             &{scLeadApplicantCredentials}
    When the user clicks the button/link         link = ${leadSubsidyControlApplication}
    And the user clicks the button/link          link = Review and submit
    Then the user should not see the element     jQuery = .task-status-incomplete
    And the user clicks the button/link          jQuery = .govuk-button:contains("Submit application")

IFS admin can view the terms and conditions accepted by both the applicants
    [Documentation]  IFS-9200
    [Setup]  Requesting Application ID of this application
    Given log in as a different user                                                      &{ifs_admin_user_credentials}
    When the user navigates to the page                                                   ${server}/management/competition/${competitionId}/application/${subsidycontrolApplicationID}
    And the user clicks the button/link                                                   id = accordion-questions-heading-4-1
    Then the user can see the terms and conditions for the lead and partner applicant

Internal users can view the terms and conditions accepted by the applicants
    [Documentation]  IFS-9200
    [Setup]  Requesting Application ID of this application
    Given log in as a different user                                                      &{innovation_lead_one}
    When the user navigates to the page                                                   ${server}/management/competition/${competitionId}/application/${subsidycontrolApplicationID}
    Then the user can see the terms and conditions for the lead and partner applicant

Comp admin assigns assessors to the competition and assigns the application to an assessor
    [Documentation]  IFS-9200
    [Setup]  update milestone to yesterday                                                ${subsidyControlCompetitionId}  SUBMISSION_DATE
    Given log in as a different user                                                      &{ifs_admin_user_credentials}
    And the user navigates to the page                                                    ${server}/management/competition/${subsidyControlCompetitionId}/assessors/find
    When the user invites assessors to assess the subsidy control competition
    And the assessors accept the invitation to assess the subsidy control competition
    Then the application is assigned to a assessor

Assessor can view the correct T&Cs have been accepted by the lead and partner applicants
    [Documentation]  IFS-9200
    Given the user navigates to the page                                                  ${server}/assessment/assessor/dashboard/competition/${subsidyControlCompetitionId}
    When the user clicks the button/link                                                  link = ${leadSubsidyControlApplication}
    Then the user can see the terms and conditions for the lead and partner applicant

Internal user marks subsidy control application to successful
    [Documentation]  IFS-9200
    Given log in as a different user                                        &{internal_finance_credentials}
    When the user navigates to the page                                     ${server}/management/competition/${subsidyControlCompetitionId}
    And the user clicks the button/link                                     jQuery = button:contains("Close assessment")
    Then making the application a successful project from correct state     ${subsidyControlCompetitionId}  ${leadSubsidyControlApplication}

MO can see T&Cs for the subsidy control application in project setup for both the applicants
    [Documentation]  IFS-9200
    [Setup]  Requesting Application ID of this application
    Given internal user assigns MO to application                                        ${subsidycontrolApplicationID}  ${leadSubsidyControlApplication}  Orvill  Orville Gibbs
    When log in as a different user                                                      &{monitoring_officer_one_credentials}
    And the user navigates to the page                                                   ${server}/application/${subsidycontrolApplicationID}/summary
    And the user should see the element                                                  jQuery = h1:contains("Application overview")
    Then the user can see the terms and conditions for the lead and partner applicant

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser    &{ifs_admin_user_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in initial details
    [Arguments]   ${compName}
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     ${compName}  ${month}  ${nextyear}  ${compType_Programme}  SUBSIDY_CONTROL  KTP

the user completes subsidy basis declaration
    the user selects the checkbox       agreement
    the user clicks the button/link     id = mark-questionnaire-complete
    the user should see the element     jQuery = li:contains("Subsidy basis") > .task-status-complete
    the user clicks the button/link     link = Subsidy basis

the user selects the subsidy basis option
    [Arguments]   ${answer}
    the user clicks the button twice      jQuery = label:contains("${answer}")
    the user clicks the button/link       jQuery = button:contains("Next")

the user starts the subsidy section again
    the user clicks the button/link     id = edit-application-details-button
    the user clicks the button/link     link = Start again
    the user clicks the button/link     jQuery = button:contains("Next")

the user accepts terms and conditions
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Terms and conditions accepted")

the user marks the subsidy contol finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user fills in the project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills the organisation details with Project growth table     ${Application}  ${LARGE_ORGANISATION_SIZE}
    the user clicks the button/link                                       link = Your funding
    the user selects the radio button                                     requestingFunding   true
    the user enters text to a text field                                  css = [name^="grantClaimPercentage"]  10
    the user selects the radio button                                     otherFunding   false
    the user clicks the button/link                                       jQuery = button:contains("Mark as complete")
    the user should see all finance subsections complete
    the user clicks the button/link                                       link = Back to application overview
    the user should see the element                                       jQuery = li:contains("Your project finances") > .task-status-complete

Requesting Application ID of this application
    ${subsidycontrolApplicationID} =  get application id by name  ${leadSubsidyControlApplication}
    Set suite variable    ${subsidycontrolApplicationID}

the user invites assessors to assess the subsidy control competition
    the user selects the checkbox       assessor-row-1
    the user selects the checkbox       assessor-row-2
    the user clicks the button/link     jQuery = button:contains("Add selected to invite list")
    the user should see the element     jQuery = td:contains("${assessor1_to_add}")
    the user should see the element     jQuery = td:contains("${assessor2_to_add}")
    the user clicks the button/link     jQuery = a:contains("Review and send invites")
    the user clicks the button/link     jQuery = .govuk-button:contains("Send invitation")

the assessors accept the invitation to assess the subsidy control competition
    log in as a different user                            ${assessor1_email}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = Subsidy control competition
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")
    log in as a different user                            ${assessor2_email}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = Subsidy control competition
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

the application is assigned to a assessor
    log in as a different user            &{Comp_admin1_credentials}
    the user navigates to the page        ${server}/management/assessment/competition/${subsidyControlCompetitionId}/application/${subsidycontrolApplicationID}/assessors
    the user selects the checkbox         assessor-row-1
    the user clicks the button/link       jQuery = button:contains("Add to application")
    the user navigates to the page        ${server}/management/competition/${subsidyControlCompetitionId}
    the user clicks the button/link       jQuery = button:contains("Notify assessors")
    log in as a different user            ${assessor1_email}   ${short_password}
    the user navigates to the page        ${server}/assessment/assessor/dashboard/competition/${subsidyControlCompetitionId}
    the user clicks the button/link       link = ${leadSubsidyControlApplication}
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = button:contains("Confirm")

the user can see the terms and conditions for the lead and partner applicant
    the user should see the element      jQuery = td:contains("Big Riffs And Insane Solos Ltd")+ td:contains("Subsidy control")
    the user should see the element      link = Innovate UK - Subsidy control
    the user should see the element      jQuery = td:contains("Ludlow")+ td:contains("State aid")
    the user should see the element      link = Innovate UK