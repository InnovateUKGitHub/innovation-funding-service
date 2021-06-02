*** Settings ***
Documentation    IFS-8260  KTP Assigning assessors
...
...              IFS-7915  KTP Assessments - assessor response
...
...              IFS-8295  KTP Assessments - assessor summary
...
...              IFS-8453  Assessor view of detailed finances
...
...              IFS-8594  For KTP pre populate the assessor view finance config to 'All'
...
...              IFS-8617  Assessment overview - missing print link and spacing of score assessment
...
...              IFS-8779 Subsidy Control - Create a New Competition - Initial Details
...
...              IFS-8548  KTP project setup banners
...
...              IFS-8550 Release supporter feedback to KTA
...
...              IFS-9246 KTP fEC/Non-fEC: application changes for read-only viewers
...
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${ktpAssessmentCompetitionID}               ${competition_ids['${ktpAssessmentCompetitionName}']}
${ktpAssessmentApplicationName}             KTP assessment application
${ktpAssessmentApplicationID}               ${application_ids['${ktpAssessmentApplicationName}']}
${ktpDetailsFinanceCompetitionName}         KTP assessment Detailed Finances
${ktpDetailsFinanceCompetitionID}           ${competition_ids['${ktpDetailsFinanceCompetitionName}']}
${ktpDetailsFinanceApplicationName}    	    KTP assessment detailed finances application
${ktpDetailsFinanceApplicationID}           ${application_ids['${ktpDetailsFinanceApplicationName}']}
${ktpOverviewFinanceCompetitionName}        KTP assessment Overview Finances
${ktpOverviewFinanceCompetitionID}          ${competition_ids['${ktpOverviewFinanceCompetitionName}']}
${ktpOverviewFinanceApplicationName}        KTP assessment overview finances application
${ktpOverviewFinanceApplicationID}          ${application_ids['${ktpOverviewFinanceApplicationName}']}
${nonKTPOverviewFinanceCompetitionName}     Non KTP competition all finance overview
${nonKTPOverviewFinanceCompetitionID}       ${competition_ids['${nonKTPOverviewFinanceCompetitionName}']}
${nonKTPOverviewFinanceApplicationName}     Non KTP Application
${nonKTPOverviewFinanceApplicationID}       ${application_ids['${nonKTPOverviewFinanceApplicationName}']}
${ktaEmail}                                 Amy.Colin@ktn-uk.test
${existingKTAEmail}                         john.fenton@ktn-uk.test
${monitoringOfficerEmail}                   hermen.mermen@ktn-uk.test
${KTPapplication}  	                        KTP in panel application
${ktpProjectID}                             ${project_ids["${KTPapplication}"]}
${KTPapplicationId}                         ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                            KTP in panel
${ktpLead}                                  bob@knowledge.base
${ktpPartner}                               jessica.doe@ludlow.co.uk
${uploadedPdf}                              fec-file

*** Test Cases ***
Comp admin can find the registered KTA in system
    [Documentation]   IFS-8260
    Given ifs admin invites a KTA user to IFS                ${ktaEmail}
    And KTA user creates an account and signed in to IFS     ${ktaEmail}
    And log in as a different user                           &{ifs_admin_user_credentials}
    When the user filters the KTA user
    Then the user should see the element                     link = Amy Colin

Internal users not be allowed to invite a new assessor to the KTP Competitions
    [Documentation]   IFS-8260
    When the user clicks the button/link         link = Invite
    Then the user should not see the element     link = Add a non-registered assessor to your list

Comp admin can see the user who is both applicant and KTA role in the list of assessors
    [Documentation]   IFS-8260
    Given assign the KTA role to an existing user      ${existingKTAEmail}
    When the user navigates to the page                ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/find
    Then the user should see the element               link = John Fenton

Invite the KTA to assess the KTP competition
    [Documentation]   IFS-8260
    Given the user selects the checkbox       assessor-row-1
    And the user clicks the button/link       id = add-selected-assessors-to-invite-list-button
    When the user clicks the button/link      id = review-and-send-assessor-invites-button
    And the user clicks the button/link       jQuery = button:contains("Send invitation")
    Then the user should see the element      link = Amy Colin

Assessor accept the inviation to assess the KTP competition
    [Documentation]   IFS-8260
    Given KTA accepts the invitation to assess the application      ${ktpAssessmentCompetitionName}   ${ktaEmail}   ${short_password}
    When log in as a different user                                 &{ifs_admin_user_credentials}
    And the user navigates to the page                              ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/accepted
    Then the user should see the element                            link = Amy Colin

Allocated KTA to assess the KTP application
    [Documentation]   IFS-8260
    Given the user navigates to the page     ${server}/management/assessment/competition/${ktpAssessmentCompetitionID}/applications
    When the user clicks the button/link     link = View progress
    And the user selects the checkbox        assessor-row-1
    And the user clicks the button/link      jQuery = button:contains("Add to application")
    Then the user should see the element     jQuery = tr td:contains("Amy Colin")

Assessor accept the inviation to assess the KTP application
    [Documentation]   IFS-8260
    Given the user navigates to the page               ${server}/management/competition/${ktpAssessmentCompetitionID}
    And the user clicks the button/link                id = notify-assessors-changes-since-last-notify-button
    When KTA accepts to assess the KTP application     ${ktpAssessmentCompetitionName}   ${ktaEmail}  ${short_password}
    And the user clicks the button/link                link = KTP assessment application
    Then the user should see the element               jQuery = h1:contains("Assessment overview") span:contains("KTP assessment application")

Assessor can see lead organisation project finances when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Finances overview
    When the user clicks the button/link      jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    Then the user should see the element      link = Your project costs
    And the user should see the element       link = Your project location
    And the user should see the element       link = Your funding

Assessor can view the read-only view for 'Yes' selected fEC declaration
    [Documentation]  IFS-9246
    Given the user clicks the button/link                           link = Your fEC model
    Then the user should see read only view for FEC declaration

Assessor can see partner organisation project finances when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to your project finances
    And the user clicks the button/link       link = Back to finances overview
    When the user clicks the button/link      jQuery = div:contains("Ludlow") ~ a:contains("View finances")
    Then the user should see the element      link = Your organisation
    And the user should see the element       link = Your project location
    And the user should see the element       link = Other funding

Assessor can see project cost summary in finance overview when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to finances overview
    Then the user should see the element      jQuery = h2:contains("Project cost summary")
    And the user should see the element       jQuery = td:contains("Other costs") + td:contains("1,100")

Assessor should get a validation message if the score is not selected
    [Documentation]   IFS-7915
    Given The user clicks the button/link                  link = Back to your assessment overview
    And the user clicks the button/link                    link = Impact
    When the user clicks the button/link                   jQuery = button:contains("Save and return to assessment overview")
    Then the user should see a field and summary error     The assessor score must be a number.

Assessor can score impact category in the KTP application
    [Documentation]   IFS-7915
    Given The user clicks the button/link             link = Back to your assessment overview
    And the user clicks the button/link               link = Impact
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Impact   10   25%

Assessor can score innovation category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Innovation
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   20   50%

Assessor can score challenge category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Challenge
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   30   75%

Assessor can score cohesiveness category in the KTP application
    [Documentation]   IFS-7915
    Given the user clicks the button/link             link = Cohesiveness
    When Assessor completes the KTP category          Testing feedback text
    Then Assessor should see the category details     Innovation   40   100%

Assessor can see the Print button and the score Total
    [Documentation]   IFS-8617
    When the user should see the element      jQuery = a:contains("Print or download the application")
    And the user should see the element       jQuery = p:contains("Total score:")

Assessor is presented with an error message when saving an assessment without guidance for funding sutability decision
    [Documentation]   IFS-8295
    Given the user clicks the button/link                  link = Review and complete your assessment
    When the user clicks the button/link                   jQuery = button:contains("Save assessment")
    Then the user should see a field and summary error     You must select an option.
    And the user should see the element                    jQuery = h1:contains("Assessment summary")
    And the user should see the element                    jQuery = h2:contains("Review assessment")

Assessor should see the scope section as incomplete if try to review the assessment without completing scope section
    [Documentation]   IFS-8295
    When the user clicks the button/link                                    id = accordion-questions-heading-1
    Then Assessor should review the incomplete scope category details       Incomplete    accordion-questions-content-1   ${EMPTY}

Assessor can review feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                           id = accordion-questions-heading-2
    Then Assessor should review the assessment category details     Complete    10/10   accordion-questions-content-2   Testing feedback text

Assessor can review feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-3
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   Testing feedback text

Assessor can review feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-4
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   Testing feedback text

Assessor can review feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-5
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   Testing feedback text

Assessor can amend the feedback they added to the scope assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the scope section
    When Assessor completes the scope section of an application
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the scope category details           Complete    Yes   accordion-questions-content-1   Testing feedback text

Assessor can amend the feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the impact section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-2   NEW testing feedback text

Assessor can amend the feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the innovation section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   NEW testing feedback text

Assessor can amend the feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the challenge section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   NEW testing feedback text

Assessor can amend the feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-8295
    Given the user clicks the button/link                            link = Edit the cohesiveness section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   NEW testing feedback text

Assessor can save the KTP application assessment
    [Documentation]   IFS-8295
    Given the user should see the element           jQuery = .govuk-body:contains("You must explain your decision")
    And the user selects the radio button           fundingConfirmation   true
    And the user enters text to a text field        id = feedback    Testing feedback text
    And the user clicks the button/link             jQuery = button:contains("Save assessment")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Assessed")

Assessor can submit the KTP application assessment
    [Documentation]   IFS-8295
    Given the user selects the checkbox             assessmentIds1
    When the user clicks the button/link            id = submit-assessment-button
    And the user clicks the button/link             jQuery = button:contains("Yes I want to submit the assessments")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Recommended")

Deafult value of assessor view finance config set to all for ktp competitions
    [Documentation]   IFS-8594  IFS-8779
    Given Log in as a different user                 &{Comp_admin1_credentials}
    When the user navigates to the page              ${CA_UpcomingComp}
    And the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details     competition config  ${month}  ${nextyear}  ${compType_Programme}  SUBSIDY_CONTROL  KTP
    And the user clicks the button/link              link = Assessors
    Then radio button should be set to               assessorFinanceView   ALL

Assessor can see lead organisation detailed finances when detailed option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given Invite KTA to assess the competition     ${ktpDetailsFinanceCompetitionID}   ${ktpDetailsFinanceApplicationName}   ${ktpDetailsFinanceCompetitionName}   ${ktaEmail}  ${short_password}
    And the user clicks the button/link            link = Finances overview
    When the user clicks the button/link           jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    Then the user should not see the element       link = Your project costs
    And the user should not see the element        link = Your project location
    And the user should not see the element        link = Your funding
    And the user should see the element            jQuery = h2:contains("Detailed finances")

Assessor can see partner organisation detailed finances when detailed option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link         link = Back to funding
    When the user clicks the button/link          jQuery = div:contains("Ludlow") ~ a:contains("View finances")
    Then the user should not see the element      link = Your organisation
    And the user should not see the element       link = Your project location
    And the user should not see the element       link = Other funding
    And the user should see the element           jQuery = h2:contains("Detailed finances")

Assessor can see project cost summary in detailed finance overview when detailed option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to funding
    Then the user should see the element      jQuery = h2:contains("Project cost summary")
    And the user should see the element       jQuery = td:contains("Other costs") + td:contains("1,100")

Assessor can see lead and partner organisation finance overview when overview option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given Invite KTA to assess the competition     ${ktpOverviewFinanceCompetitionID}   ${ktpOverviewFinanceApplicationName}   ${ktpOverviewFinanceCompetitionName}   ${ktaEmail}  ${short_password}
    When the user clicks the button/link           link = Finances overview
    Then the user should not see the element       jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    And the user should not see the element        jQuery = div:contains("Ludlow") ~ a:contains("View finances")
    And the user should see the element            jQuery = h2:contains("Project cost summary")
    And the user should see the element            jQuery = td:contains("Other costs") + td:contains("1,100")

Assessor can see lead organisation finances for non ktp compettition when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given Invite KTA to assess the competition     ${nonKTPOverviewFinanceCompetitionID}    ${nonKTPOverviewFinanceApplicationName}    ${nonKTPOverviewFinanceCompetitionName}     addison.shannon@gmail.com   ${short_password}
    And the user clicks the button/link            link = Finances overview
    When the user clicks the button/link           jQuery = div:contains("Mo Juggling Mo Problems Ltd") ~ a:contains("View finances")
    Then the user should see the element           link = Your project costs
    And the user should see the element            link = Your project location
    And the user should see the element            link = Your organisation
    And the user should see the element            link = Your funding

Assessor can see partner organisation finances for non ktp compettition when all option selected in assessor view of fiannces in competition setup
    [Documentation]  IFS-8453
    Given the user clicks the button/link     link = Back to funding
    When the user clicks the button/link      jQuery = div:contains("University of Bath") ~ a:contains("View finances")
    Then the user should see the element      link = Your organisation
    And the user should see the element       link = Your project costs
    And the user should see the element       link = Your project location
    And the user should see the element       link = Your funding

KTA can see application successfull banner and feedback information with date on making the application successful, before the feedback is released
    [Documentation]  IFS-8548
    Given IFS Admin makes the application decision           ${ktpAssessmentCompetitionName}  Successful
    And IFS Admin notifies all applicants
    When MO navigates to application overview page           ${ktpAssessmentApplicationName}  This application was successful.
    Then the user should see the element                     jQuery = h2:contains("This application was successful.")
    And the user should see the element                      jQuery = p:contains("All application feedback will be available here from ${ktpAssessmentCompetitionReleaseFeedbackDayMonthYear}.")

KTA can see application successful banner and feedback information after the feedback is released
    [Documentation]  IFS-8548
    Given IFS admin releases feedback to the applicant    ${ktpAssessmentCompetitionName}
    When MO navigates to application overview page        ${ktpAssessmentApplicationName}  This application was successful.
    Then the user should see the element                  jQuery = h2:contains("This application was successful.")
    And the user should see the element                   jQuery = p:contains("You can view all scores and application feedback in the relevant sections.")

KTA can see application unsuccessful banner and feedback information with date on making the application unsuccessful, before the feedback is released
    [Documentation]  IFS-8548
    Given IFS Admin makes the application decision        ${ktpDetailsFinanceCompetitionName}  Unsuccessful
    And IFS Admin notifies all applicants
    When MO navigates to application overview page        ${ktpDetailsFinanceApplicationName}  This application was unsuccessful.
    Then the user should see the element                  jQuery = h2:contains("This application was unsuccessful.")
    And the user should see the element                   jQuery = p:contains("All application feedback will be available here from ${ktpDetailsFinanceCompetitionReleaseFeedbackDayMonthYear}.")

KTA can see application unsuccessful banner and feedback information after the feedback is released
    [Documentation]  IFS-8548
    Given IFS admin releases feedback to the applicant    ${ktpDetailsFinanceCompetitionName}
    When MO navigates to application overview page        ${ktpDetailsFinanceApplicationName}  This application was unsuccessful.
    Then the user should see the element                  jQuery = h2:contains("This application was unsuccessful.")
    And the user should see the element                   jQuery = p:contains("You can view all scores and application feedback in the relevant sections.")

KTA receives a notification email that assessor and supporter feedback is available on release feedback
    [Documentation]  IFS-8550
    Given log in as a different user                                     &{ifs_admin_user_credentials}
    And the user clicks the button/link                                  link = ${KTPcompetiton}
    When IFS admin releases feedback on making application sucessful
    Then the user reads his email and clicks the link                    ${monitoringOfficerEmail}   ${KTPcompetiton}: Feedback for application ${KTPapplicationId} is now available.   You can now view the feedback for this application  1

KTA can view written feedback from assessors and supporters on release feedback
    [Documentation]  IFS-8550
    Given log in as a different user                        ${monitoringOfficerEmail}   ${short_password}
    When the user navigates to application overview         ${KTPapplication}
    Then KTA should see assessors and supporters feedback

Project lead should not see assessor or supporter feedback
    [Documentation]  IFS-8550
    Given log in as a different user                                               ${ktpLead}  ${short_password}
    When the user navigates to the page                                            ${server}/project-setup/project/${ktpProjectID}
    And the user clicks the button/link                                            link = view application overview
    Then the project team member should not see assessor or supporter feedback

Project partner should not see assessor or supporter feedback
    [Documentation]  IFS-8550
    Given log in as a different user                                               ${ktpPartner}  ${short_password}
    When the user navigates to the page                                            ${server}/project-setup/project/${ktpProjectID}
    And the user clicks the button/link                                            link = view application overview
    Then the project team member should not see assessor or supporter feedback

*** Keywords ***
Custom suite setup
    Set predefined date variables
    The user logs-in in new browser     &{ifs_admin_user_credentials}

the user filters the KTA user
    the user navigates to the page           ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/find
    the user enters text to a text field     id = assessorNameFilter   Amy
    the user clicks the button/link          id = assessor-filter-button

KTA accepts the invitation to assess the application
    [Arguments]    ${compettitionName}                    ${ktaEmail}   ${short_password}
    log in as a different user                            ${ktaEmail}   ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user selects the radio button                     acceptInvitation   true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

KTA accepts to assess the KTP application
    [Arguments]   ${compettitionName}                    ${ktaEmail}  ${short_password}
    log in as a different user                           ${ktaEmail}  ${short_password}
    the user clicks the assessment tile if displayed
    the user clicks the button/link                       link = ${compettitionName}
    the user clicks the button/link                       link = Accept or reject
    the user selects the radio button                     assessmentAccept  true
    the user clicks the button/link                       jQuery = button:contains("Confirm")

Assessor completes the KTP category
    [Arguments]   ${feedbackText}
    The user selects the option from the drop-down menu     10    css = .assessor-question-score
    The user enters text to a text field                    css = .editor    ${feedbackText}
    Wait for autosave
    mouse out  css = .editor
    the user should see the element                                    jQuery = span:contains("Saved!")
    The user clicks the button/link                                    jQuery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

Assessor should see the category details
    [Arguments]   ${category}   ${score}   ${percentage}
    the user should see the element     jQuery = li:contains("${category}") .task-status-complete:contains("Complete")
    the user should see the element     jQuery = li:contains("${category}") .notification:contains("Score 10 / 10")
    the user should see the element     jQuery = p:contains("${score}")
    the user should see the element     jQuery = p:contains("${percentage}")

Assessor completes the scope section of an application
    the user selects the radio button                       govuk-radios__item     in-scope-true
    The user selects the option from the drop-down menu     Industrial research    css = .research-category
    The user enters text to a text field                    css = .editor    Testing feedback text
    Wait for autosave
    mouse out  css = .editor
    the user should see the element                                    jQuery = span:contains("Saved!")
    the user clicks the button/link                                    jQuery = button:contains("Save and return to assessment overview")
    ${error} =   Run Keyword and return status without screenshots     page should contain     An unexpected error occurred.
    Run Keyword If    '${error}' == 'True'                             the user clicks the button/link   jQuery = button:contains("Save and return to assessment overview")

Assessor should review the assessment category details
    [Arguments]   ${sectionStatus}   ${score}   ${idSelector}   ${feedbackText}
    the user should see the element     jQuery = h2:contains("${sectionStatus}")
    the user should see the element     jQuery = .section-score:contains("Score")
    the user should see the element     jQuery = .section-score:contains("${score}")
    the user should see the element     jQuery = \#${idSelector}:contains("${feedbackText}")
    the user should see the element     jQuery = .govuk-body:contains(40/40)
    the user should see the element     jQuery = .govuk-body:contains(100%)

Assessor should review the scope category details
    [Arguments]   ${sectionStatus}   ${scopeAnswer}   ${idSelector}   ${feedbackText}
    the user should see the element     jQuery = h2:contains("${sectionStatus}")
    the user should see the element     jQuery = .score:contains("In scope: ${scopeAnswer}")
    the user should see the element     jQuery = \#${idSelector}:contains("${feedbackText}")

Assessor should review the incomplete scope category details
    [Arguments]   ${sectionStatus}   ${idSelector}   ${feedbackText}
    the user should see the element         jQuery = h2:contains("${sectionStatus}")
    the user should not see the element     jQuery = .score:contains("In scope:")
    the user should see the element         jQuery = \#${idSelector}:contains("${feedbackText}")

Invite KTA to assess the competition
    [Arguments]   ${competitionID}   ${applicationTitle}   ${competitionName}   ${email}  ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/competition/${competitionID}/assessors/find
    ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots    the user should see the element    jQuery = span:contains("Non KTP competition all finance overview")
    Run Keyword If   '${status}' == 'PASS'    the user selects the checkbox     jQuery = tr:contains("Addison Shannon") :checkbox
    ...                              ELSE     the user selects the checkbox     assessor-row-1
    the user clicks the button/link                          id = add-selected-assessors-to-invite-list-button
    the user clicks the button/link                          id = review-and-send-assessor-invites-button
    the user clicks the button/link                          jQuery = button:contains("Send invitation")
    KTA accepts the invitation to assess the application     ${competitionName}  ${email}   ${short_password}
    Log in as a different user                               &{ifs_admin_user_credentials}
    the user navigates to the page                           ${server}/management/assessment/competition/${competitionID}/applications
    the user clicks the button/link                          link = View progress
    the user selects the checkbox                            assessor-row-1
    the user clicks the button/link                          jQuery = button:contains("Add to application")
    the user navigates to the page                           ${server}/management/competition/${competitionID}
    the user clicks the button/link                          id = notify-assessors-changes-since-last-notify-button
    KTA accepts to assess the KTP application                ${competitionName}    ${email}  ${short_password}
    the user clicks the button/link                          link = ${applicationTitle}

IFS Admin makes the application decision
    [Arguments]   ${competitionName}  ${decision}
    log in as a different user                            &{ifs_admin_user_credentials}
    the user clicks the button/link                       link = ${competitionName}
    the user clicks the button/link                       id = close-assessment-button
    IFS admin inputs the funding decision                 ${decision}

IFS admin inputs the funding decision
    [Arguments]   ${decision}
    the user clicks the button/link     link = Input and review funding decision
    the user clicks the button/link     id = select-all-1
    the user clicks the button/link     jQuery = button:contains("${decision}")
    the user clicks the button/link     link = Competition

IFS Admin notifies all applicants
    the user clicks the button/link                      link = Manage funding notifications
    the user clicks the button/link                      id = select-all-1
    the user clicks the button/link                      id = write-and-send-email
    the user clicks the button/link                      id = send-email-to-all-applicants
    the user clicks the button/link                      id = send-email-to-all-applicants-button
    the user refreshes until element appears on page     jQuery = td:contains("Sent")

IFS admin releases feedback to the applicant
    [Arguments]  ${competitionName}
    log in as a different user          &{ifs_admin_user_credentials}
    the user clicks the button/link     link = ${competitionName}
    the user clicks the button/link     id = release-feedback-button

MO navigates to application overview page
    [Arguments]  ${applicationName}  ${message}
    log in as a different user                            ${monitoringOfficerEmail}  ${short_password}
    the user navigates to application overview            ${applicationName}
    the user refreshes until element appears on page      jQuery = h2:contains("${message}")

the user navigates to application overview
    [Arguments]  ${applicationName}
    the user navigates to the page                       ${server}/project-setup/monitoring-officer/dashboard
    the user refreshes until element appears on page     link = ${applicationName}
    the user clicks the button/link                      link = ${applicationName}
    the user clicks the button/link                      link = view application overview

KTA should see assessors and supporters feedback
    the user clicks the button/link     jQuery = .govuk-heading-m:contains("Score assessment") + div button:contains("Open all")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") + div h3:contains("Assessor 1") + p:contains("This is the impact feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") + div h3:contains("Assessor 2") + p:contains("This is the impact feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Impact") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") + div h3:contains("Assessor 1") + p:contains("This is the innovation feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") + div h3:contains("Assessor 2") + p:contains("This is the innovation feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Innovation") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") + div h3:contains("Assessor 1") + p:contains("This is the challenge feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") + div h3:contains("Assessor 2") + p:contains("This is the challenge feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Challenge") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") + div h3:contains("Assessor 1") + p:contains("This is the cohesiveness feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") + div h3:contains("Assessor 2") + p:contains("This is the cohesiveness feedback")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Cohesiveness") span:contains("Average score 7.0 / 10")
    the user should see the element     jQuery = h3:contains("Application score: 70.0%")
    the user clicks the button/link     jQuery = .govuk-heading-m:contains("Application feedback") + div button:contains("Open all")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Assessor feedback") + div ul li:contains("Assessor 1") p:contains("Perfect application")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Assessor feedback") + div ul li:contains("Assessor 2") p:contains("Perfect application")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Anarchy inc.") p:contains("This application is extraordinary I'd love to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Money inc.") p:contains("This application is extraordinary I'd love to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Evil inc.") p:contains("This application is extraordinary I'd hate to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Guest inc.") p:contains("This application is extraordinary I'd hate to fund it")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Connolly inc.")
    the user should see the element     jQuery = .govuk-accordion__section-header:contains("Supporter feedback") + div ul li span:contains("Alston inc.")

the project team member should not see assessor or supporter feedback
    the user should not see the element    jQuery = .govuk-accordion__section-header:contains("Assessor feedback")
    the user should not see the element    jQuery = .govuk-accordion__section-header:contains("Supporter feedback")
    the user should not see the element    jQuery = h2:contains("Score assessment")

IFS admin releases feedback on making application sucessful
    IFS admin inputs the funding decision                Successful
    IFS Admin notifies all applicants
    the user clicks the button/link                      link = Competition
    the user refreshes until element appears on page     id = release-feedback-button
    the user clicks the button/link                      id = release-feedback-button

the user should see read only view for FEC declaration
    the user should not see the element                     jQuery = button:contains("Edit your fEC Model")
    the user checks the read-only page

the user checks the read-only page
    # Due to us testing webtest data here, the file does not exist so we check for only no internal server errors. Page not found is OK in this case.
    the user should see the element                                  jQuery = legend:contains("Will you be using the full economic costing (fEC) funding model?") > p:contains("Yes")
    the user clicks the button/link                                  jQuery = h3:contains("View fEC certificate") ~ div a:contains("${uploadedPdf}")
    Select Window                                                    NEW
    the user should not see internal server and forbidden errors
    the user closes the last opened tab
