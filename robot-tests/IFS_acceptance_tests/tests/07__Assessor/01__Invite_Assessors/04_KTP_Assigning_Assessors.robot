*** Settings ***
Documentation    IFS-8260  KTP Assigning assessors
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

Comp admin can see the user who is got both applicant and KTA role in the list of assessors
    [Documentation]   IFS-8260
    Given assign the KTA role to the user      ${existingKTAEmail}
    When the user navigates to the page        ${server}/management/competition/${ktpAssessmentCompetitionID}/assessors/find
    Then the user should see the element       link = John Fenton

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

allocated KTA to assess the KTP application
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


