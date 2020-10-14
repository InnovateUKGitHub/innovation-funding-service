*** Settings ***
Documentation    IFS-8260  KTP Assigning assessors
...
...              IFS-7915  KTP Assessments - assessor response
...
...              IFS-8295  KTP Assessments - assessor summary
...
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${ktpAssessmentCompetitionName}     KTP assessment
${ktpAssessmentCompetitionID}       ${competition_ids['${ktpAssessmentCompetitionName}']}
${ktpAssessmentApplicationName}     KTP assessment application
${ktpAssessmentApplicationID}       ${application_ids['${ktpAssessmentApplicationName}']}
${ktaEmail}                         Amy.Colin@ktn-uk.test
${existingKTAEmail}                 john.fenton@ktn-uk.test

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

Invite the KTA to assess the KTP application
    [Documentation]   IFS-8260
    Given the user selects the checkbox       assessor-row-1
    And the user clicks the button/link       id = add-selected-assessors-to-invite-list-button
    When the user clicks the button/link      id = review-and-send-assessor-invites-button
    And the user clicks the button/link       jQuery = button:contains("Send invite")
    Then the user should see the element      link = Amy Colin

Assessor accept the inviation to assess the KTP competition
    [Documentation]   IFS-8260
    Given KTA accepts the invitation to assess the application
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
    When KTA accepts to assess the KTP application
    And the user clicks the button/link                link = KTP assessment application
    Then the user should see the element               jQuery = h1:contains("Assessment overview") span:contains("KTP assessment application")

Assessor should get a validation message if the score is not selected
    [Documentation]   IFS-7915
    Given the user clicks the button/link                  link = Impact
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

Assessor is presented an error when saving an incomplete assessment
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link             link = Review and complete your assessment
    When the user clicks the button/link              jQuery = button:contains("Save assessment")
    Then the user should see a summary error          You must select an option.

Assessor can review feedback they added to the scope assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user should see the element                                   jQuery = h1:contains("Assessment summary")
    And the user should see the element                                     jQuery = h2:contains("Review assessment")
    When the user clicks the button/link                                    id = accordion-questions-heading-1
    Then Assessor should review the incomplete scope category details       Incomplete    accordion-questions-content-1   ${EMPTY}

Assessor can review feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                           id = accordion-questions-heading-2
    Then Assessor should review the assessment category details     Complete    10/10   accordion-questions-content-2   Testing feedback text

Assessor can review feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-3
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   Testing feedback text

Assessor can review feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-4
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   Testing feedback text

Assessor can review feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            id = accordion-questions-heading-5
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   Testing feedback text

Assessor can amend the feedback they added to the scope assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            link = Edit the scope section
    When Assessor completes the scope section of an application
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the scope category details           Complete    Yes   accordion-questions-content-1   Testing feedback text

Assessor can amend the feedback they added to the impact assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            link = Edit the impact section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-2   NEW testing feedback text

Assessor can amend the feedback they added to the innovation assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            link = Edit the innovation section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-3   NEW testing feedback text

Assessor can amend the feedback they added to the challenge assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            link = Edit the challenge section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-4   NEW testing feedback text

Assessor can amend the feedback they added to the cohesiveness assessment category section in the KTP application
    [Documentation]   IFS-7915, IFS-8295
    Given the user clicks the button/link                            link = Edit the cohesiveness section
    When Assessor completes the KTP category                         NEW testing feedback text
    And the user clicks the button/link                              link = Review and complete your assessment
    Then Assessor should review the assessment category details      Complete    10/10   accordion-questions-content-5   NEW testing feedback text

Assessor can save the KTP application assessment
    [Documentation]   IFS-7915, IFS-8295
    Given the user should see the element           jQuery = .govuk-body:contains("You must explain your decision")
    And the user selects the radio button           fundingConfirmation   true
    And the user enters text to a text field        id = feedback    Testing feedback text
    And the user clicks the button/link             jQuery = button:contains("Save assessment")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Assessed")

Assessor can submit the KTP application assessment
    [Documentation]   IFS-7915, IFS-8295
    Given the user selects the checkbox             assessmentIds1
    When the user clicks the button/link            id = submit-assessment-button
    And the user clicks the button/link             jQuery = button:contains("Yes I want to submit the assessments")
    Then the user should see the element            jQuery = li:contains("KTP assessment application") .msg-progress:contains("Recommended")


*** Keywords ***
Custom suite setup
    The user logs-in in new browser     &{ifs_admin_user_credentials}

the user filters the KTA user
    the user navigates to the page           ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/find
    the user enters text to a text field     id = assessorNameFilter   Amy
    the user clicks the button/link          id = assessor-filter-button

KTA accepts the invitation to assess the application
    log in as a different user           ${ktaEmail}   ${short_password}
    the user clicks the button/link      link = ${ktpAssessmentCompetitionName}
    the user selects the radio button    acceptInvitation   true
    the user clicks the button/link      jQuery = button:contains("Confirm")

KTA accepts to assess the KTP application
    log in as a different user           ${ktaEmail}  ${short_password}
    the user clicks the button/link      link = ${ktpAssessmentCompetitionName}
    the user clicks the button/link      link = Accept or reject
    the user selects the radio button    assessmentAccept  true
    the user clicks the button/link      jQuery = button:contains("Confirm")

Assessor completes the KTP category
    [Arguments]   ${feedbackText}
    The user selects the option from the drop-down menu    10    css = .assessor-question-score
    The user enters text to a text field                   css = .editor    ${feedbackText}
    Wait for autosave
    mouse out  css = .editor
    Wait Until Page Contains Without Screenshots    Saved!
    The user clicks the button/link                         jQuery = button:contains("Save and return to assessment overview")

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
    Wait Until Page Contains Without Screenshots            Saved!
    the user clicks the button/link                         jQuery = button:contains("Save and return to assessment overview")

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
