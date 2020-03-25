*** Settings ***
Documentation     Suite description
...
...               IFS-2192 As a Portfolio manager I am able to create an EOI competition
...
...               IFS-2196 As an applicant I am able to apply for an EOI competition
...
...               IFS-2941 As an applicant I am only offered the Research category eligible for the competition
...
...               IFS-4046 Person to organisation acceptance test updates
...
...               IFS-4080 As an applicant I am able to confirm the Research category eligible for the competition
...
...               IFS-5920 Acceptance tests for T's and C's
...
...               IFS-6054 Display completed projects in the previous tab
Suite Setup       custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin  Applicant  Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot

# This suite covers End to End flow of EOI type competition i.e comp creation, applicaiotn submission , assessmnet submission, release feedback
*** Variables ***
${comp_name}         EOI comp
${EOI_application}   EOI Application
${EOI_comp_title}    Expression of Interest: Assistive technologies for caregivers
${EOI_comp_ID}       ${competition_ids['${EOI_comp_title}']}
${EOI_application1}      Expression of Interest: Assistive technologies for caregivers - Application 1
${EOI_application_id}    ${application_ids["${EOI_application1}"]}
&{EOI_assessor}          email=eoi-assessor-user1@example.com    password=${short_password}

*** Test Cases ***
Comp Admin Creates EOI type competition
    [Documentation]  IFS-2192
    Given Logging in and Error Checking               &{Comp_admin1_credentials}
    Then the competition admin creates competition    ${business_type_id}  ${comp_name}  EOI  ${compType_EOI}  2  GRANT  release-feedback-completion-stage  no  1  true  collaborative

Applicant applies to newly created EOI competition
    [Documentation]  IFS-2192  IFS-2196  IFS-4046 IFS-4080
    [Setup]  get competition id and set open date to yesterday  ${comp_name}
    Given Log in as a different user            &{assessor_bob_credentials}
    Then logged in user applies to competition  ${comp_name}  1

Applicant submits his application
    [Documentation]  IFS-2196  IFS-2941  IFS-4046  IFS-5920
    Given the user clicks the button/link               link = Application details
    When the user fills in the Application details      ${EOI_application}  ${tomorrowday}  ${month}  ${nextyear}
    And the lead applicant fills all the questions and marks as complete(EOI comp type)
    And the applicant checks for competition terms and conditions
    Then the user should not see the element            jQuery = h2:contains("Finances")
    And the applicant submits the application

Invite a registered assessor
    [Documentation]  IFS-2376
    Given log in as a different user                          &{Comp_admin1_credentials}
    When the user clicks the button/link                      link = ${comp_name}
    And the user clicks the button/link                       link = Invite assessors to assess the competition
    And the user enters text to a text field                  id = assessorNameFilter   Paul Plum
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Filter")
    Then the user clicks the button/link                      jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Add selected to invite list")
    And the user clicks the button/link                       link = Invite
    And the user clicks the button/link                       link = Review and send invites
    And the user enters text to a text field                  id = message    This is custom text
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Send invite")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-2376
    [Setup]  update milestone to yesterday                  ${competitionId}  SUBMISSION_DATE
    Given Log in as a different user                        &{assessor_credentials}
    When The user clicks the button/link                    Link = ${comp_name}
    And the user selects the radio button                   acceptInvitation  true
    And The user clicks the button/link                     jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-2376
    Given log in as a different user              &{Comp_admin1_credentials}
    When The user clicks the button/link          link = Dashboard
    And The user clicks the button/link           link = EOI comp
    And The user clicks the button/link           jQuery = a:contains("Manage assessments")
    And the user clicks the button/link           jQuery = a:contains("Allocate applications")
    Then the user clicks the button/link          jQuery = tr:contains("${EOI_application}") a:contains("Assign")
    And the user adds an assessor to application  jQuery = tr:contains("Paul Plum") :checkbox
    When the user navigates to the page           ${server}/management/competition/${competitionId}
    Then the user clicks the button/link          jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-2376
    Given Log in as a different user                       &{assessor_credentials}
    When The user clicks the button/link                   link = EOI comp
    And the user clicks the button/link                    jQuery = li:contains("${EOI_application}") a:contains("Accept or reject")
    And the user selects the radio button                  assessmentAccept  true
    Then the user clicks the button/link                   jQuery = .govuk-button:contains("Confirm")
    And the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard/competition/${competitionId}
    And the user clicks the button/link                    link = EOI Application
    And the assessor submits the assessment

the comp admin closes the assessment and releases feedback
    [Documentation]  IFS-2376
    Given log in as a different user                  &{Comp_admin1_credentials}
    When making the application a successful project  ${competitionId}  ${EOI_application}
    And moving competition to Project Setup           ${competitionId}
    Then the user should not see an error in the page

the EOI comp moves to Previous tab
    [Documentation]  IFS-2376  IFS-6054
    Given the user clicks the button/link  link = Dashboard
    When the user clicks the button/link   jQuery = a:contains("Previous")
    Then the user should see the competition details and sucessful application

The comp admin assign application to panel and interview panel
    [Documentation]  IFS-6416
    [Setup]  the user navigates to the page   ${server}/management/competition/${EOI_comp_ID}
    Given the user clicks the button/link     jQuery = button:contains("Close assessment")
    Then the comp admin assign application to panel
    And the comp admin assign application to applicant
    And the comp admin assign application to interview panel

An assessor accept the application for panel
    [Documentation]  IFS-6416
    [Setup]  log in as a different user          &{EOI_assessor}
    Given the assessor accept the application    ${EOI_comp_title}  ${EOI_application1}
    When the user clicks the button/link         link = ${EOI_application1}
    And the user expands the section             Innovation
    Then the user should see the element         jQuery = p:contains("This is the innovation feedback")
    [Teardown]  the user clicks the button/link  link = Back to panel overview

An assessor view application assigned for interview panel
    [Documentation]  IFS-6416
    Given the user navigates to the page    ${server}/assessment/assessor/dashboard/competition/${EOI_comp_ID}/interview
    When the user clicks the button/link    link = ${EOI_application1}
    Then the user navigates to the page     ${server}/application/${EOI_application_id}/feedback
    And the user should see the element     jQuery = h1:contains("Feedback overview")

An applicant see the application in interview panel
    [Documentation]  IFS-6416
    Given log in as a different user       &{lead_applicant_credentials}
    When the user clicks the button/link   jQuery = ul li a:contains("${EOI_application1}")
    Then the user navigates to the page    ${server}/application/${EOI_application_id}/feedback
    And the user should see the element    jQuery = h1:contains("Feedback overview")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

the lead applicant fills all the questions and marks as complete(EOI comp type)
    the lead applicant marks every question as complete   Project summary
    the lead applicant marks every question as complete   Scope
    the applicant completes application team
    the user selects Research category   Feasibility studies
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

the assessor submits the assessment
    the assessor adds score and feedback for every question    5   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link               link = Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id = feedback    EOI application assessed
    the user clicks the button/link               jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link               jQuery = li:contains("${EOI_application}") label[for^="assessmentIds"]
    the user clicks the button/link               jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link               jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element               jQuery = li:contains("EOI Application") strong:contains("Recommended")   #

logged in user applies to competition
    [Arguments]  ${competition}  ${applicationType}
    the user select the competition and starts application   ${competition}
    the user selects the radio button    organisationTypeId  ${applicationType}
    the user clicks the button/link      jQuery = button:contains("Save and continue")
    the user clicks the Not on companies house link
    the user clicks the button/link      jQuery = button:contains("Save and continue")
    the user selects the checkbox        agree
    the user clicks the button/link      css = .govuk-button[type="submit"]    #Continue

the applicant checks for competition terms and conditions
    the user should see the element       jQuery = h2:contains("Terms and conditions") ~ p:contains("You are agreeing to these by submitting your application.")
    the user clicks the button/link       link = Award terms and conditions
    the user should see the element       jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    the user should not see the element   jQuery = button:contains("Agree and continue")
    the user clicks the button/link       link = Back to application overview

the user should see the competition details and sucessful application
    the user clicks the button/link    link = ${comp_name}
    the user should see the element    jQuery = dt:contains("Competition type:") ~ dd:contains("Expression of interest")
    the user should see the element    jQuery = button:contains("Projects (0)")
    the user expands the section       Applications
    the user should see the element    jQuery = h1:contains("${comp_name}")

the comp admin assign application to panel
    the user navigates to the page     ${server}/management/assessment/panel/competition/${EOI_comp_ID}/manage-applications
    the user clicks the button/link    jQuery = td:contains("${EOI_application_id}") ~ td a:contains("Assign")
    the user clicks the button/link    link = Manage assessment panel
    the user clicks the button/link    jQuery = button:contains("Confirm actions")

the comp admin assign application to applicant
    the user navigates to the page    ${server}/management/assessment/interview/competition/${EOI_comp_ID}/applications/find
    the user selects the checkbox     selectedIds
    the user clicks the button/link   jQuery = button:contains("Add selected to invite list")
    the user clicks the button/link   link = Review and send invites
    the user clicks the button/link   jQuery = button:contains("Send invite")

the comp admin assign application to interview panel
    the user navigates to the page      ${server}/management/assessment/interview/competition/${EOI_comp_ID}/assessors/allocate-assessors
    the user clicks the button/link     jQuery = td:contains("Klaus Baudelaire") ~ td a:contains("Allocate")
    the user selects the checkbox       selectedIds
    the user clicks the button/link     jQuery = button:contains("Allocate")
    the user clicks the button/link     jQuery = input[type='submit']     #Notify

Custom suite teardown
    Close browser and delete emails
    Disconnect from database