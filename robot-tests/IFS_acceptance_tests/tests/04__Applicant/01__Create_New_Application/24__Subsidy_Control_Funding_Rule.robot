*** Settings ***
Documentation     IFS-8994  Two new sets of terms & conditions required
...
...               IFS-9137  Update Subsidy control T&Cs for Innovate UK & ATI
...
...               IFS-9124 Add dual T&Cs to Subsidy Control Competitions
...
...               IFS-9116 Applicant Subsidy Basis Questionnaire and Declaration Confirmation (Application)
...
...               IFS-9233 Applicant can view and accept the correct T&Cs based on their determined Funding Rules
...
...               IFS-9120 View Subsidy Basis answers and funding rules for each Partner Organisation in Application & Assessor Overviews
...
...               IFS-9200 View Correct T&Cs for each Organisation (Internal and External Users)
...
...               IFS-9127 View and Change an Organisations NI Declaration determined Funding Rules after Application Submittal
...
...               IFS-10213 Replace T&C -‘Innovate UK - Subsidy control’ Aug 2021
...
...               IFS-12116 Subsidy control - Content update for Subsidy basis questions
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
${subsidyControlCompetitionId}       ${competition_ids["${subsidyControlFundingComp}"]}
${leadSubsidyControlApplication}     Subsidy control application
${leadStateAidApplication}           State aid application
&{scLeadApplicantCredentials}        email=janet.howard@example.com     password=${short_password}
${subsidyControlCompetitionId}       ${competition_ids["${subsidyControlFundingComp}"]}
${partnerOrganisationName}           Ludlow
${subsidyOrgName}                    Big Riffs And Insane Solos Ltd
${assessor1_email}                   addison.shannon@gmail.com
${assessor1_to_add}                  Addison Shannon
${assessor2_to_add}                  Alexis Colon
${assessor2_email}                   alexis.colon@gmail.com

*** Test Cases ***
Creating a new comp to confirm ATI subsidy control T&C's
    [Documentation]  IFS-8994  IFS-9137  IFS-9124
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user clicks the button twice        jQuery = label:contains("Aerospace Technology Institute (ATI) - Subsidy control")
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
    And the user should see the element                  jQuery = li:contains("Subsidy Control/State aid obligations")
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
    And the user clicks the button twice        jQuery = label:contains("Innovate UK - Subsidy control")
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user selects the radio button       termsAndConditionsId    termsAndConditionsId13
    Then the user clicks the button/link        jQuery = button:contains("Done")
    And the user should see the element         jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Innovate UK - Subsidy control")
    And the user should see the element         jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Innovate UK")

Innovateuk subsidy control t&c's are correct
    [Documentation]  IFS-8994  IFS-9124  IFS-10213
    When the user clicks the button/link                 jQuery = button:contains("Edit")
    And the user clicks the button/link                  link = ${innovateUKSubsidyControl}
    And select window                                    title = Terms and conditions of an Innovate UK Grant Award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should see the element                  jQuery = li:contains("Subsidy Control/State aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk State aid t&c's are correct
    [Documentation]  IFS-8994
    When the user clicks the button/link                 jQuery = button:contains("Done")
    And the user clicks the button/link                  link = ${innovateUKStateAid}
    And select window                                    title = Terms and conditions of an Innovate UK Grant Award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should see the element                  jQuery = li:contains("UK Subsidy obligations")
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

Lead applicant declares subsidy basis as Northern Ireland Protocol when all questions answered as yes
    [Documentation]  IFS-9116  IFS-12116
    Given the user determines the subsidy basis                 Yes  Yes  Yes  Yes  Yes  Yes  n/a
    When the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see provided answers for subsidy basis  Yes  Yes  Yes  Yes  Yes  Yes  n/a

Lead applicant declares subsidy basis as Northern Ireland Protocol when trading goods through Northern Ireland
    [Documentation]  IFS-9116  IFS-12116
    Given the user starts the subsidy section again
    When the user determines the subsidy basis                  No  Yes  No  No  Yes  Yes  Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see provided answers for subsidy basis  No  Yes  No  No  Yes  Yes  Yes

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

Partner applicant declares subsidy basis as Subsidy control Act
    [Documentation]  IFS-9116  IFS-12116
    Given the user clicks the button/link                       link = subsidy basis
    And the user clicks the button/link                         jQuery = button:contains("Next")
    When the user determines the subsidy basis                  No  n/a  n/a  n/a  n/a  n/a  No
    And the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Subsidy control Act")
    And the user should see provided answers for subsidy basis  No  n/a  n/a  n/a  n/a  n/a  No

Lead applicant completes state aid subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user                                                    &{scLeadApplicantCredentials}
    And the user clicks the button/link                                                 link = ${leadStateAidApplication}
    When the applicant completes Application Team                                       COMPLETE  janet.howard@example.com
    And the lead applicant fills all the questions and marks as complete(programme)
    And the user navigates to Your-finances page                                        ${leadStateAidApplication}
    Then the user marks the finances as complete                                        ${leadStateAidApplication}  labour costs  54,000  yes

Lead applicant accepts state aid terms and conditions based on NI declaration
    [Documentation]  IFS-9233
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should not see the element       jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Partner completes project finances and terms and conditions of state aid application
    [Documentation]  IFS-9116
    Given log in as a different user                                &{collaborator1_credentials}
    When the user navigates to Your-finances page                   ${leadStateAidApplication}
    Then the user marks the subsidy contol finances as complete     ${leadStateAidApplication}  labour costs  54,000  yes

Partner applicant can accept subsidy control terms and conditions based on NI declaration
    [Documentation]  IFS-9233  IFS-10213
    When the user clicks the button/link         link = Award terms and conditions
    Then the user should see the element         jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should see the element          jQuery = li:contains("Subsidy Control/State aid obligations")
    And the user accepts terms and conditions

Lead applicant submits state aid subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user             &{scLeadApplicantCredentials}
    When the user clicks the button/link         link = ${leadStateAidApplication}
    And the user clicks the button/link          link = Review and submit
    Then the user should not see the element     jQuery = .task-status-incomplete
    And the user clicks the button/link          jQuery = .govuk-button:contains("Submit application")

Lead applicant creates subsidy control subsidy basis application and declares subsidy basis as Subsidy control Act
    [Documentation]  IFS-9116  IFS-12116
    Given existing user creates a new application               ${subsidyControlFundingComp}
    And the user clicks the button/link                         link = Application details
    And the user fills in the Application details               ${leadSubsidyControlApplication}  ${tomorrowday}  ${month}  ${nextyear}
    And the user clicks the button/link                         link = Subsidy basis
    And the user clicks the button/link                         jQuery = button:contains("Next")
    When the user determines the subsidy basis                  Yes  No  No  No  No  No  n/a
    And the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Subsidy control Act")
    And the user should see provided answers for subsidy basis  Yes  No  No  No  No  No  n/a

Partner applicant declares subsidy basis as Northern Ireland Protocol when activites have a direct link to Northern Ireland
    [Documentation]  IFS-9116  IFS-12116
    Given the user clicks the button/link                       link = Back to application overview
    And the lead invites already registered user                ${collaborator1_credentials["email"]}  ${subsidyControlFundingComp}
    When logging in and error checking                          jessica.doe@ludlow.co.uk  ${short_password}
    And the user clicks the button/link                         id = save-organisation-button
    And the user clicks the button/link                         link = Subsidy basis
    And the user clicks the button/link                         jQuery = button:contains("Next")
    When the user determines the subsidy basis                  Yes  Yes  Yes  Yes  No  Yes  Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see provided answers for subsidy basis  Yes  Yes  Yes  Yes  No  Yes  Yes

Partner applicant declares subsidy basis as Northern Ireland Protocol when trading goods through Northern Ireland
    [Documentation]  IFS-9116  IFS-12116
    Given the user starts the subsidy section again
    When the user determines the subsidy basis                  No  Yes  Yes  Yes  No  Yes  Yes
    And the user completes subsidy basis declaration
    Then the user should see the element                        jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    And the user should see provided answers for subsidy basis  No  Yes  Yes  Yes  No  Yes  Yes

Lead applicant completes subsidy control subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user                                                    &{scLeadApplicantCredentials}
    And the user clicks the button/link                                                 link = ${leadSubsidyControlApplication}
    When the applicant completes Application Team                                       COMPLETE  janet.howard@example.com
    And the lead applicant fills all the questions and marks as complete(programme)
    And the user completes the application research category                            Feasibility studies
    And the user navigates to Your-finances page                                        ${leadSubsidyControlApplication}
    Then the user marks the subsidy contol finances as complete                         ${leadSubsidyControlApplication}  labour costs  54,000  yes

Lead applicant can accept subsidy control terms and conditions based on NI declaration
    [Documentation]  IFS-9223  IFS-10213
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should see the element           jQuery = li:contains("Subsidy Control/State aid obligations")
    And the user accepts terms and conditions

Partner completes project finances and terms and conditions of subsidy control application
    [Documentation]  IFS-9116
    Given log in as a different user                  &{collaborator1_credentials}
    When the user navigates to Your-finances page     ${leadSubsidyControlApplication}
    Then the user marks the finances as complete      ${leadSubsidyControlApplication}  labour costs  54,000  yes

Partner applicant can accept state aid terms and conditions based on NI declaration
    [Documentation]  IFS-9223
    When the user clicks the button/link          link = Award terms and conditions
    Then the user should see the element          jQuery = h1:contains("Terms and conditions of an Innovate UK Grant Award")
    And the user should not see the element       jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Lead applicant gets validation message when submiting the application without all partners subsidy basis not determined
    [Documentation]  IFS-9120
    Given partner edits the subsidy basis section
    When lead review and submits the application
    And the user clicks the button/link               id = accordion-questions-heading-1-1
    Then the user should see the element              jQuery = td:contains("${partnerOrganisationName}") + td:contains("Not determined")
    And the user should see the element               jQuery = div:contains("Incomplete") button:contains("Subsidy basis")

Lead applicant submits subsidy control subsidy basis application
    [Documentation]  IFS-9116
    Given log in as a different user                      &{collaborator1_credentials}
    And the user clicks the button/link                   link = ${leadSubsidyControlApplication}
    And the user clicks the button/link                   link = Subsidy basis
    When the user completes subsidy basis declaration
    And lead review and submits the application
    Then the user clicks the button/link                  jQuery = .govuk-button:contains("Submit application")

Lead applicant can see organisations subsidy basis funding rules in application overview
    [Documentation]  IFS-9120
    When the user clicks the button/link                  link = View application
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

Partner applicant can see organisations subsidy basis funding rules in application overview
    [Documentation]  IFS-9120
    Given log in as a different user                       &{collaborator1_credentials}
    And the user clicks the button/link                    link = ${leadSubsidyControlApplication}
    When the user clicks the button/link                   link = View application
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

Comp admin assigns assessors to the competition and assigns the application to an assessor
    [Documentation]  IFS-9200
    [Setup]  update milestone to yesterday                                              ${subsidyControlCompetitionId}  SUBMISSION_DATE
    Given requesting application ID of this application
    And log in as a different user                                                      &{ifs_admin_user_credentials}
    And the user navigates to the page                                                  ${server}/management/competition/${subsidyControlCompetitionId}/assessors/find
    When the user invites assessors to assess the subsidy control competition
    And the assessors accept the invitation to assess the subsidy control competition
    Then the application is assigned to be an assessor

Assessor can view the correct T&Cs have been accepted by the lead and partner applicants
    [Documentation]  IFS-9200
    Given the user navigates to the page                                                  ${server}/assessment/assessor/dashboard/competition/${subsidyControlCompetitionId}
    When the user clicks the button/link                                                  link = ${leadSubsidyControlApplication}
    Then the user can see the terms and conditions for the lead and partner applicant

Assessor can see organisations subsidy basis funding rules in assessment overview
    [Documentation]  IFS-9200
    When the user clicks the button/link                    link = Subsidy basis
    And the user should see the element                     jQuery = td:contains("${subsidyOrgName}") + td:contains("Subsidy control")+ td:contains("View answers")
    Then assessor should see valid subsidy basis answers

Internal user can see organisations subsidy basis funding rules in application overview
    [Documentation]  IFS-9120
    Given Log in as a different user                        &{internal_finance_credentials}
    And making the application a successful project         ${subsidyControlCompetitionId}   ${leadSubsidyControlApplication}
    When the user navigates to the page                     ${server}/management/competition/${subsidyControlCompetitionId}/application/${leadSubsidyControlApplicationID}
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

Monitoring officer can see organisations subsidy basis funding rules in application feedback
    [Documentation]  IFS-9120
    Given Internal user assigns MO to application               ${leadSubsidyControlApplicationID}   ${leadSubsidyControlApplication}  Orvill  Orville Gibbs
    And log in as a different user                              &{monitoring_officer_one_credentials}
    And the user clicks the project setup tile if displayed
    When the user clicks the button/link                        link = ${leadSubsidyControlApplication}
    And the user clicks the button/link                         link = view application feedback
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

MO can see T&Cs for the subsidy control application in project setup for both the applicants
    [Documentation]  IFS-9200
    Given the user navigates to the page                                                 ${server}/application/${leadSubsidyControlApplicationID}/summary
    When the user clicks the button/link                                                 id = accordion-questions-heading-4-1
    Then the user should see the element                                                 jQuery = h1:contains("Application overview")
    And the user can see the terms and conditions for the lead and partner applicant

Lead applicant can see organisations subsidy basis funding rules in application feedback
    [Documentation]  IFS-9120
    Given log in as a different user                        &{scLeadApplicantCredentials}
    When the user clicks the button/link                    link = ${leadSubsidyControlApplication}
    And the user clicks the button/link                     link = view application feedback
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

Partner can see organisations subsidy basis funding rules in application feedback
    [Documentation]  IFS-9120
    Given log in as a different user                        &{collaborator1_credentials}
    When the user clicks the button/link                    link = ${leadSubsidyControlApplication}
    And the user clicks the button/link                     link = view application feedback
    Then the user can see valid subsidy control answers
    And the user can see valid state aid answers

IFS admin can view the terms and conditions accepted by both the applicants
    [Documentation]  IFS-9200
    Given log in as a different user                                                      &{ifs_admin_user_credentials}
    When the user navigates to the page                                                   ${server}/management/competition/${competitionId}/application/${leadSubsidyControlApplicationID}
    Then the user can see the terms and conditions for the lead and partner applicant

Innovation lead can view the terms and conditions accepted by the applicants
    [Documentation]  IFS-9200
    Given log in as a different user                                                      &{innovation_lead_one}
    When the user navigates to the page                                                   ${server}/management/competition/${competitionId}/application/${leadSubsidyControlApplicationID}
    Then the user can see the terms and conditions for the lead and partner applicant

Internal user can view and change the project's funding rules
    [Documentation]  IFS-9127
    [Setup]  Requesting Organisation Id of this application
    Given Requesting Project ID of this Project
    And Log in as a different user                      &{ifs_admin_user_credentials}
    When the user navigates to the page                 ${server}/project-setup-management/project/${subsidyProjectId}/finance-check/organisation/${subsidyOrgID}/funding-rules/edit
    And the user selects the checkbox                   id = override-funding-rules
    And the user clicks the button/link                 jQuery = button:contains("Save and return")
    And the user clicks the button/link                 jQuery = a:contains("Return to finance checks")
    And the user navigates to the page                  ${server}/project-setup-management/project/${subsidyProjectId}/finance-check/organisation/${subsidyOrgID}/eligibility/changes
    Then the user should see the element                jQuery = th:contains("Funding rules") ~ td:contains("Subsidy control") ~ td:contains("State aid")

Internal user can approve lead applicant funding rules
    [Documentation]  IFS-9127
    Given the user navigates to the page     ${server}/project-setup-management/project/${subsidyProjectId}/finance-check
    When the user approves funding rules     table.table-progress tr:nth-child(1) td:nth-child(2) a:contains("Review")
    Then the user should see the element     jQuery = th:contains("${subsidyOrgName}") + td:contains("Approved") + td:contains("State aid")

Finance manager can approve partner applicant funding rules
    [Documentation]  IFS-9127
    Given Log in as a different user         &{internal_finance_credentials}
    And the user navigates to the page       ${server}/project-setup-management/project/${subsidyProjectId}/finance-check
    When the user approves funding rules     table.table-progress tr:nth-child(2) td:nth-child(2) a:contains("Review")
    Then the user should see the element     jQuery = th:contains("${partnerOrganisationName}") + td:contains("Approved") + td:contains("State aid")

Internal user can approve viability and eligibility of lead organisation
    [Documentation]  IFS-9127
    Given the user navigates to the page     ${server}/project-setup-management/project/${subsidyProjectId}/finance-check
    When confirm viability                   0
    And confirm eligibility                  0
    Then the user should see the element     jQuery = table.table-progress tr:nth-child(1) td:nth-child(4) a:contains("Approved")
    And the user should see the element      jQuery = table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("Approved")

Internal user can approve viability and eligibility of partner organisation
    [Documentation]  IFS-9127
    Given the user navigates to the page     ${server}/project-setup-management/project/${subsidyProjectId}/finance-check
    When confirm viability                   1
    And confirm eligibility                  1
    Then the user should see the element     jQuery = table.table-progress tr:nth-child(2) td:nth-child(4) a:contains("Approved")
    And the user should see the element      jQuery = table.table-progress tr:nth-child(2) td:nth-child(6) a:contains("Approved")

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

the user determines the subsidy basis
    [Arguments]   ${answer1}  ${answer2}  ${answer3}  ${answer4}  ${answer5}  ${answer6}  ${answer7}
    the user selects the subsidy basis question option                             ${answer1}
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = td:contains("Will the Innovate UK funding directly or indirectly have an effect upon either:")+td:contains("Yes")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user should see the element                         jQuery = p:contains("The funding you are applying for is potentially")
    ...                             AND                 the user selects the subsidy basis question option      ${answer2}
    ...                             AND                 the user selects the subsidy basis question option      ${answer3}
    ...                             AND                 the user selects the subsidy basis question option      ${answer4}
    ...                             AND                 the user selects the subsidy basis question option      ${answer5}
    Run Keyword If  '${status}' == 'FAIL'    the user selects the subsidy basis question option      ${answer7}
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+td:contains("Yes")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user should see the element                         jQuery = p:contains("The funding you are applying for is potentially")
    ...                             AND                 the user selects the subsidy basis question option      ${answer2}
    ...                             AND                 the user selects the subsidy basis question option      ${answer3}
    ...                             AND                 the user selects the subsidy basis question option      ${answer4}
    ...                             AND                 the user selects the subsidy basis question option      ${answer5}
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    jQuery = td:contains("Is your enterprise engaged in the production, processing or marketing of agricultural products")+td:contains("Yes")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user should see the element                         jQuery = h4:contains("Can you confirm that the Innovate UK funding will be directed towards activities other than the production")
    ...                             AND                 the user selects the subsidy basis question option      ${answer6}

the user selects the subsidy basis question option
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

partner edits the subsidy basis section
    log in as a different user          &{collaborator1_credentials}
    the user clicks the button/link     link = ${leadSubsidyControlApplication}
    the user clicks the button/link     link = Subsidy basis
    the user clicks the button/link     id = edit-application-details-button

lead review and submits the application
    log in as a different user          &{scLeadApplicantCredentials}
    the user clicks the button/link     link = ${leadSubsidyControlApplication}
    the user clicks the button/link     link = Review and submit

the user can see valid subsidy control answers
    the user should see the element                             jQuery = td:contains("${subsidyOrgName}") + td:contains("Subsidy control")+ td:contains("View answers")
    the user clicks the button/link                             jQuery = tr:nth-child(1) a:contains("View answers")
    the user should see the element                             jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Subsidy control Act")
    the user should see provided answers for subsidy basis      Yes  No  No  No  No  No  No
    the user clicks the button/link                             link = Back to application overview

the user can see valid state aid answers
    the user should see the element                             jQuery = td:contains("${partnerOrganisationName}") + td:contains("State aid")+ td:contains("View answers")
    the user clicks the button/link                             jQuery = tr:nth-child(2) a:contains("View answers")
    the user should see the element                             jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    the user should see provided answers for subsidy basis      No  Yes  Yes  Yes  No  n/a  Yes

requesting application ID of this application
    ${leadSubsidyControlApplicationID} =  get application id by name   ${leadSubsidyControlApplication}
    Set suite variable    ${leadSubsidyControlApplicationID}

Requesting Project ID of this Project
    ${subsidyProjectId} =  get project id by name    ${leadSubsidyControlApplication}
    Set suite variable    ${subsidyProjectId}

Requesting Organisation Id of this application
    ${subsidyOrgId}=    get organisation id by name     ${subsidyOrgName}
    Set suite variable      ${subsidyOrgId}

the user invites assessors to assess the subsidy control competition
    the user selects the checkbox       jQuery = tr:contains("Addison Shannon") :checkbox
    the user selects the checkbox       jQuery = tr:contains("Alexis Colon") :checkbox
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

the application is assigned to be an assessor
    log in as a different user                             &{Comp_admin1_credentials}
    get assessment period id and set as suite variable     ${subsidyControlCompetitionId}
    the user navigates to the page                         ${server}/management/assessment/competition/${subsidyControlCompetitionId}/application/${leadSubsidyControlApplicationID}/period/${assessmentPeriodID}/assessors
    the user selects the checkbox                          assessor-row-1
    the user clicks the button/link                        jQuery = button:contains("Add to application")
    the user navigates to the page                         ${server}/management/competition/${subsidyControlCompetitionId}
    the user clicks the button/link                        jQuery = button:contains("Notify assessors")
    log in as a different user                             ${assessor1_email}   ${short_password}
    the user navigates to the page                         ${server}/assessment/assessor/dashboard/competition/${subsidyControlCompetitionId}
    the user clicks the button/link                        link = ${leadSubsidyControlApplication}
    the user selects the radio button                      assessmentAccept  true
    the user clicks the button/link                        jQuery = button:contains("Confirm")

assessor should see valid subsidy basis answers
    the user clicks the button/link                          jQuery = tr:nth-child(1) a:contains("View answers")
    the user should see the element                          jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Subsidy control Act")
    the user should see provided answers for subsidy basis   Yes  No  No  No  No  No  No
    the user clicks the button/link                          link = Back to subsidy basis
    the user should see the element                          jQuery = td:contains("${partnerOrganisationName}") + td:contains("State aid")+ td:contains("View answers")
    the user clicks the button/link                          jQuery = tr:nth-child(2) a:contains("View answers")
    the user should see the element                          jQuery = p:contains("Based on the answers, the subsidy basis has been determined as falling under the") span:contains("Northern Ireland Protocol")
    the user should see provided answers for subsidy basis   No  Yes  Yes  Yes  No  n/a  Yes

the user can see the terms and conditions for the lead and partner applicant
    the user should see the element      jQuery = td:contains("${subsidyOrgName}")+ td:contains("Subsidy control")
    the user should see the element      link = Innovate UK - Subsidy control
    the user should see the element      jQuery = td:contains("${partnerOrganisationName}")+ td:contains("State aid")
    the user should see the element      link = Innovate UK

get assessment period id and set as suite variable
    [Arguments]  ${competitionID}
    ${assessmentPeriodID} =   get assessment period using competition id   ${competitionID}
    Set suite variable  ${assessmentPeriodID}


the user should see provided answers for subsidy basis
    [Arguments]     ${selectedAnswer1}  ${selectedAnswer2}  ${selectedAnswer3}  ${selectedAnswer4}  ${selectedAnswer5}  ${selectedAnswer6}  ${selectedAnswer7}
    the user should see the element        jQuery = td:contains("Will the Innovate UK funding directly or indirectly have an effect upon either:")+td:contains("${selectedAnswer1}")
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = td:contains("Will the Innovate UK funding directly or indirectly have an effect upon either:")+td:contains("Yes")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user should see the element     jQuery = td:contains("Is your enterprise based in or active in Northern Ireland?")+td:contains("${selectedAnswer2}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Does your enterprise trade directly with customers in Northern Ireland?")+td:contains("${selectedAnswer3}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Does your enterprise make goods or provide services to third parties with a view to")+td:contains("${selectedAnswer4}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Is your enterprise engaged in the production, processing or marketing of agricultural products")+td:contains("${selectedAnswer5}")
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")
    Run Keyword if  '${status}' == 'PASS'  the user should see the element        jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+td:contains("${selectedAnswer7}")
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  jQuery = td:contains("Are you intending to trade any goods arising from the activities funded by Innovate UK with the European Union through Northern Ireland?")+td:contains("Yes")
    Run Keyword if  '${status}' == 'PASS'  runkeywords  the user should see the element     jQuery = td:contains("Is your enterprise based in or active in Northern Ireland?")+td:contains("${selectedAnswer2}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Does your enterprise trade directly with customers in Northern Ireland?")+td:contains("${selectedAnswer3}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Does your enterprise make goods or provide services to third parties with a view to")+td:contains("${selectedAnswer4}")
    ...                             AND                 the user should see the element     jQuery = td:contains("Is your enterprise engaged in the production, processing or marketing of agricultural products")+td:contains("${selectedAnswer5}")
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    jQuery = td:contains("Can you confirm that the Innovate UK funding will be directed towards activities other than the production")
    Run Keyword if  '${status}' == 'PASS'   the user should see the element  jQuery = td:contains("Can you confirm that the Innovate UK funding will be directed towards activities other than the production")+td:contains("${selectedAnswer6}")