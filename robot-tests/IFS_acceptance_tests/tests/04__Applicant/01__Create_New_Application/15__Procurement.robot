*** Settings ***
Suite Setup     Custom suite setup
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource        ../../07__Assessor/Assessor_Commons.robot

*** Variables ***
${comp_name}         Procurement competition
${appl_name}         Procurement app

*** Test Cases ***
Comp Admin creates procurement competition
    [Documentation]
    [Tags]
    Given Logging in and Error Checking                          &{Comp_admin1_credentials}
    Then the competition admin creates competition               ${rto_type_id}  ${comp_name}  procurement  Programme  2  PROCUREMENT  release-feedback-completion-stage  no  2  true  single-or-collaborative

Applicant applies to newly created procurement competition
    [Documentation]  IFS-2688
    [Tags]    MySQL
    When the competition is open                  ${comp_name}
    And Log in as a different user                &{RTO_lead_applicant_credentials}
    Then logged in user applies to competition    ${comp_name}  3

Applicant submits his application
    [Documentation]  IFS-2688 IFS-3287
    [Tags]
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${appl_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    Then the lead applicant fills all the questions and marks as complete(Programme)
    When the user navigates to Your-finances page       ${appl_name}
    And the user marks the procurement finances as complete         ${appl_name}   Calculate  52,214  yes
    And the user selects research category              Feasibility studies
    And the applicant submits the procurement application
    [Teardown]  Competition is closed

Invite a registered assessor
    [Documentation]  IFS-2376
    [Tags]
    Given log in as a different user                          &{Comp_admin1_credentials}
    When the user clicks the button/link                      link = ${comp_name}
    And the user clicks the button/link                       link = Invite assessors to assess the competition
    And the user selects the option from the drop-down menu   Smart infrastructure  id = filterInnovationArea
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Filter")
    Then the user clicks the button/link                      jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Add selected to invite list")
    And the user clicks the button/link                       link = Invite
    And the user clicks the button/link                       link = Review and send invites
    And the user enters text to a text field                  id = message    This is custom text
    And the user clicks the button/link                       jQuery = .govuk-button:contains("Send invite")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-2376
    [Tags]
    Given Log in as a different user                        &{assessor_credentials}
    When The user clicks the button/link                    Link = ${comp_name}
    And the user selects the radio button                   acceptInvitation  true
    And The user clicks the button/link                     jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-2376
    [Tags]
    Given log in as a different user        &{Comp_admin1_credentials}
    When The user clicks the button/link    link = Dashboard
    And The user clicks the button/link     link = ${comp_name}
    And The user clicks the button/link     jQuery = a:contains("Manage assessments")
    And the user clicks the button/link     jQuery = a:contains("Allocate applications")
    Then the user clicks the button/link    jQuery = tr:contains("${appl_name}") a:contains("Assign")
    And the user clicks the button/link     jQuery = tr:contains("Paul Plum") button:contains("Assign")
    When the user navigates to the page     ${server}/management/competition/${competitionId}
    Then the user clicks the button/link    jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-2376
    [Tags]
    Given Log in as a different user                       &{assessor_credentials}
    When The user clicks the button/link                   link = ${comp_name}
    And the user clicks the button/link                    jQuery = li:contains("${appl_name}") a:contains("Accept or reject")
    And the user selects the radio button                  assessmentAccept  true
    Then the user clicks the button/link                   jQuery = .govuk-button:contains("Confirm")
    And the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard/competition/${competitionId}
    And the user clicks the button/link                    link = ${appl_name}
    And the assessor submits the assessment

the comp admin closes the assessment and releases feedback
    [Documentation]  IFS-2376
    [Tags]
    Given log in as a different user                  &{Comp_admin1_credentials}
    When making the application a successful project  ${competitionId}    ${appl_name}
    And moving competition to Project Setup           ${competitionId}
    Then the user should not see an error in the page

the procurement comp moves to Previous tab
    [Documentation]  IFS-2376
    [Tags]
    Given the user clicks the button/link  link = Dashboard
    When the user clicks the button/link   jQuery = a:contains("Previous")
    Then the user clicks the button/link   link = ${comp_name}
    And the user should see the element    JQuery = h1:contains("${comp_name}")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

the user marks the procurement finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user fills in the project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user should not see the element             css = table
    the user should see all procurement finance subsections complete
    the user clicks the button/link  link = Application overview
    the user should see the element  jQuery = li:contains("Your finances") > .task-status-complete
    the user should not see the element   link = Finances overview

the user should see all procurement finance subsections complete
    the user should see the element  css = li:nth-of-type(1) .task-status-complete
    the user should see the element  css = li:nth-of-type(2) .task-status-complete
    the user should see the element  css = li:nth-of-type(3) .task-status-complete

the applicant submits the procurement application
    the user clicks the button/link                    link = Review and submit
    the user should not see the element                jQuery = .task-status-incomplete
    the user should see that the element is disabled   jQuery = .govuk-button:contains("Submit application")
    the user selects the checkbox                      agreeTerms
    the user clicks the button/link                    jQuery = .govuk-button:contains("Submit application")
    the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, I want to submit my application")
    the user should be redirected to the correct page  track

Competition is closed
    Get competitions id and set it as suite variable    ${comp_name}
    the submission date changes in the db in the past   ${competitionId}

the assessor submits the assessment
    the assessor adds score and feedback for every question    11   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link               link = Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id = feedback    Procurement application assessed
    the user clicks the button/link               jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link               jQuery = li:contains("${appl_name}") label[for^="assessmentIds"]
    the user clicks the button/link               jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link               jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element               jQuery = li:contains("${appl_name}") strong:contains("Recommended")   #
