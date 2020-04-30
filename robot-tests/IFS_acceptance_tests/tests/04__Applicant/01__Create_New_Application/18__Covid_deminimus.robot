*** Settings ***
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
${COVIDdeminimuscompetitionTitle}   599 Covid de minimis round 2
${COVIDdeminimuscompetitionId}      ${competition_ids['${COVIDdeminimuscompetitionTitle}']}
${COVIDdeminimusapplicationTitle1}  Covid deminimus Application
${exfinanceemail}                   exfinance1@example.com

*** Test Cases ***
IFS admin is able to invite an external PF
    [Documentation]  IFS-7357
    [Setup]  Log in as a different user         &{ifs_admin_user_credentials}
    Given the user navigates to the page        ${server}/management/competition/setup/${COVIDdeminimuscompetitionId}
    When the user clicks the button/link        link = External finance reviewers
    And the user clicks the button/link         jQuery = span:contains("Invite a new external finance reviewer")
    Then complete external project finance details
    [Teardown]    logout as user

New external project finance can create account
    [Documentation]  IFS-7357
    Given the user reads his email and clicks the link   ${exfinanceemail}  Invitation to review an Innovation Funding Service competition  You have been invited  1
    When external project finance creates account
    Then The user should not see the element                 link = ${COVIDdeminimuscompetitionTitle}

Create application to covid deminimus comp
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page   ${server}/competition/${COVIDdeminimuscompetitionId}/overview
    Then the user completes the covid deminimus application

Funding sought validation
    Given the user clicks the button/link               link = Your funding
    When the user enters text to a text field           id = amount   210000
    And the user selects the radio button               otherFunding  false
    And the user clicks the button/link                 id = mark-all-as-complete
    Then the user should see a field and summary error  Funding sought cannot be higher than your project costs.

Applicant can add funding sought
    Given the user enters text to a text field           id = amount   21000
    When the user clicks the button/link                 id = mark-all-as-complete
    Then the user should see all finance subsections complete
    [Teardown]  the user clicks the button/link   link = Back to application overview

Submit application
    Given the user clicks the button/link      id = application-overview-submit-cta
    And the user should not see the element    jQuery = .message-alert:contains("You will not be able to make changes")
    When the user clicks the button/link       id = submit-application-button
    Then the user should see the element       link = Reopen application

Non lead cannot reopen competition
    Given log in as a different user           collaborator1@example.com  ${correct_password}
    When the user should see the element       link = ${COVIDdeminimusapplicationTitle1}
    Then the user should not see the element   jQuery = li:contains("${COVIDdeminimusapplicationTitle1}") a:contains("Reopen")

Lead can reopen application
    [Setup]  log in as a different user   &{lead_applicant_credentials}
    Given the user clicks the button/link  link = Dashboard
    When the user clicks the button/link   jQuery = li:contains("${COVIDdeminimusapplicationTitle1}") a:contains("Reopen")
    And the user clicks the button/link    css = input[type="submit"]
    Then the user should see the element   jQuery = .message-alert:contains("Now your application is complete")

Lead can make changes and resubmit
    Given the user clicks the button/link     id = application-overview-submit-cta
    When the user should not see the element  jQuery = .message-alert:contains("You will not be able to make changes")
    Then the user clicks the button/link      id = submit-application-button

Internal user can invite to assesment
    [Setup]  get application id by name and set as suite variable   ${COVIDdeminimusapplicationTitle1}
    Given Log in as a different user       &{Comp_admin1_credentials}
    When the user clicks the button/link   link = ${COVIDdeminimuscompetitionTitle}
    Then the user completes assessment and moves to PS

External project finance can approve finances
    [Documentation]  IFS-7357
    [Setup]  the project finance approves all steps before finance
    Given Log in as a different user      ${exfinanceemail}  ${short_password}
    When the user navigates to the page   ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    And the user clicks the button/link   jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    Then confirm viability                0
    And confirm eligibility               0

External project finance can generate spend profile and complete PS
    [Documentation]  IFS-7357
    Given the user clicks the button/link    css = .generate-spend-profile-main-button
    When the user clicks the button/link    css = #generate-spend-profile-modal-button
    Then the internal user can complete PS

Competition goes into previous
    the user clicks the button/link  link = Dashboard
    Given the user clicks the button/link    jQuery = a:contains("Project setup (")
    And The user should not see the element  link = ${COVIDdeminimuscompetitionTitle}
    when the user clicks the button/link     jQuery = a:contains("Previous (")
    Then the user should see the element     link = ${COVIDdeminimuscompetitionTitle}

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser   &{lead_applicant_credentials}
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the applicant adds contributor to Application Team
    the user clicks the button/link                        link = Application team
    then the user clicks the button/link                   jQuery = button:contains("Add person to Empire Ltd")
    the user invites a person to the same organisation     Collaborator  collaborator1@example.com
    logout as user
    When the user reads his email and clicks the link      collaborator1@example.com    Invitation to contribute in ${COVIDdeminimuscompetitionTitle}     You will be joining as part of the organisation    2
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, accept invitation")
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm and continue")
    And the invited user fills the create account form     Collaborator  Axe
    And the user reads his email and clicks the link       collaborator1@example.com    Please verify your email address    Once verified you can sign into your account
    the user clicks the button/link                        jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    Logging in and Error Checking                          &{lead_applicant_credentials}
    then the user clicks the button/link                   link = ${COVIDdeminimusapplicationTitle1}
    the applicant completes Application Team

the user fills in bank details
    the user clicks the button/link                      link = Set up your project
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

the project finance approves all steps before finance
    Log in as a different user               &{lead_applicant_credentials}
    the user clicks the button/link          link = ${COVIDdeminimusapplicationTitle1}
    the user clicks the button/link          link = Project details
    the user clicks the button/link          link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link          id = return-to-set-up-your-project-button
    the user clicks the button/link          link = Project team
    the user clicks the button/link          link = Project manager
    the user selects the radio button        projectManager    projectManager1
    the user clicks the button/link          id = save-project-manager-button
    The user selects their finance contact   financeContact1
    the user clicks the button/link          link = Set up your project
    the user clicks the button/link          link = Documents
    the user clicks the button/link          link = Exploitation plan
    the user uploads the file                css = .inputfile  ${valid_pdf}
    the user should see the element          jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
    the user clicks the button/link          id = submit-document-button
    the user clicks the button/link          id = submitDocumentButtonConfirm
    the user clicks the button/link          link = Back to document overview
    the user fills in bank details
    log in as a different user               &{ifs_admin_user_credentials}
    the user navigates to the page           ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user clicks the button/link          jQuery = td.action:nth-of-type(3) a
    the user clicks the button/link          link = Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page           ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user clicks the button/link          jQuery = td.action:nth-of-type(4)
    search for MO                            Orvill  Orville Gibbs
    the internal user assign project to MO   ${application_id}  ${COVIDdeminimusapplicationTitle1}
    the user navigates to the page           ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user clicks the button/link          jQuery = td.action:nth-of-type(5)
    approve bank account details

confirm viability
    [Arguments]  ${viability}
    the user clicks the button/link                      css = .viability-${viability}
    the user selects the checkbox                        project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      id = confirm-button      #Page confirmation button
    the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

confirm eligibility
    [Arguments]  ${eligibility}
    the user clicks the button/link                      css = .eligibility-${eligibility}
    the user expands the section                         Overhead costs
    #the user should see the element                      jQuery = .table-overflow:contains("20%")
    #the user should not see the element                  jQuery = .table-overflow:contains("20%") ~ div a:contains("Edit")
    the user selects the checkbox                        project-eligible
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      css = #confirm-button        #Page confirmation button
    the user clicks the button/link                      name = confirm-eligibility   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

approve bank account details
    the user clicks the button/link    jQuery = button:contains("Approve bank account details")
    the user clicks the button/link    jQuery = button:contains("Approve account")
    the user should see the element    jQuery = h2:contains("The bank details provided have been approved.")
    the user clicks the button/link    link = Back to project setup

send and approve gol
    log in as a different user         &{ifs_admin_user_credentials}
    the user navigates to the page     ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user clicks the button/link    jQuery = td.action:nth-of-type(7) a
    the user selects the checkbox      approvedByLeadTechnologist
    the user clicks the button/link    jQuery = button:contains("Approved")
    the user clicks the button/link    jQuery = .modal-accept-profile button:contains("Approve")
    the user clicks the button/link    jQuery = td.action:nth-of-type(8) a
    the user uploads the file          grantOfferLetter  ${valid_pdf}
    the user selects the checkbox      confirmation
    the user clicks the button/link    id = send-gol
    the user clicks the button/link    jQuery = .modal-accept-send-gol .govuk-button:contains("Publish to project team")
    Log in as a different user         &{lead_applicant_credentials}
    the user clicks the button/link    link = ${COVIDdeminimusapplicationTitle1}
    the user clicks the button/link    link = Grant offer letter
    the user uploads the file          signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link    css = .govuk-button[data-js-modal = "modal-confirm-grant-offer-letter"]
    the user clicks the button/link    id = submit-gol-for-review
    log in as a different user         &{ifs_admin_user_credentials}
    the user navigates to the page     ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user clicks the button/link    jQuery = td.action:nth-of-type(8) a
    the user selects the radio button  approvalType  acceptGOL
    the user clicks the button/link    jQuery = button:contains("Submit")
    the user clicks the button/link    jQuery = button[type = "submit"]:contains("Accept signed grant offer letter")

the external finance cannot access spend profile
    log in as a different user            ${exfinanceemail}  ${short_password}
    the user navigates to the page        ${server}/project-setup-management/competition/${COVIDdeminimuscompetitionId}/status/all
    the user should see the element       jQuery = td.action:nth-of-type(7)
    the user should not see the element   jQuery = td.action:nth-of-type(7) a

the external finance cannot access GOL section
    Log in as a different user              ${exfinanceemail}  ${short_password}
    the user clicks the button/link         link = ${COVIDdeminimuscompetitionId}
    the user should not see the element     jQuery = td.action:nth-of-type(8) a
    the user should not see the element     jQuery = td.ok:nth-of-type(7) a

Complete external project finance details
    the user enters text to a text field    id = firstName     External
    the user enters text to a text field    id = lastName      Finance
    the user enters text to a text field    id = emailAddress  ${exfinanceemail}
    the user clicks the button/link         css = button[name = "inviteFinanceUser"]

External project finance creates account
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user enters text to a text field     id = firstName  External
    the user enters text to a text field     id = lastName  Finance
    the user enters text to a text field     id = password  ${short_password}
    the user clicks the button/link          jQuery = .govuk-button:contains("Create account")
    the user clicks the button/link          link = Sign into your account
    Logging in and Error Checking            ${exfinanceemail}   ${short_password}

the user completes the covid deminimus application
    the user clicks the button/link                          jQuery = a:contains("Start new application")
    the user clicks the button/link                          jQuery = button:contains("Save and continue")
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${COVIDdeminimusapplicationTitle1}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant adds contributor to Application Team
    the user selects research category                       Feasibility studies
    the lead applicant fills all the questions and marks as complete(programme)
    the user accept the competition terms and conditions     Return to application overview
    the user navigates to Your-finances page                 ${COVIDdeminimusapplicationTitle1}
    the user fills in project costs no overheads
    the user enters the project location
    the user fills in the organisation information           ${COVIDdeminimusapplicationTitle1}  ${SMALL_ORGANISATION_SIZE}

invite assessor the the assesment
    update milestone to yesterday                      ${COVIDdeminimuscompetitionId}  SUBMISSION_DATE
    the user clicks the button/link                    link = Dashboard
    the user clicks the button/link                    link = ${COVIDdeminimuscompetitionTitle}
    the user clicks the button/link                    link = Invite assessors to assess the competition
    the user enters text to a text field               id = assessorNameFilter   Paul Plum
    the user clicks the button/link                    jQuery = .govuk-button:contains("Filter")
    the user clicks the button/link                    jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    the user clicks the button/link                    jQuery = .govuk-button:contains("Add selected to invite list")
    the user clicks the button/link                    link = Invite
    the user clicks the button/link                    link = Review and send invites
    the user enters text to a text field               id = message    This is custom text
    the user clicks the button/link                    jQuery = .govuk-button:contains("Send invite")
    Log in as a different user                         &{assessor_credentials}
    the user clicks the button/link                    link = ${COVIDdeminimuscompetitionTitle}
    the user selects the radio button                  acceptInvitation  true
    the user clicks the button/link                    jQuery = button:contains("Confirm")
    the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard
    log in as a different user                         &{Comp_admin1_credentials}
    the user clicks the button/link                    link = Dashboard
    the user clicks the button/link                    link = ${COVIDdeminimuscompetitionTitle}
    the user clicks the button/link                    jQuery = a:contains("Manage assessments")
    the user clicks the button/link                    jQuery = a:contains("Allocate applications")
    the user clicks the button/link                    jQuery = tr:contains("${COVIDdeminimusapplicationTitle1}") a:contains("Assign")
    the user adds an assessor to application           jQuery = tr:contains("Paul Plum") :checkbox
    the user navigates to the page                     ${server}/management/competition/${COVIDdeminimuscompetitionId}
    the user clicks the button/link                    jQuery = button:contains("Notify assessors")
    Log in as a different user                         &{assessor_credentials}
    The user clicks the button/link                    link = ${COVIDdeminimuscompetitionTitle}
    the user clicks the button/link                    jQuery = li:contains("${COVIDdeminimusapplicationTitle1}") a:contains("Accept or reject")
    the user selects the radio button                  assessmentAccept  true
    the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm")
    the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard/competition/${COVIDdeminimuscompetitionId}
    the user clicks the button/link                    link = ${COVIDdeminimusapplicationTitle1}
    the assessor submits

the assessor submits
    the assessor adds score for every question    11
    the user clicks the button/link               link = Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id = feedback    Covid application assessed
    the user clicks the button/link               jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link               jQuery = li:contains("${COVIDdeminimusapplicationTitle1}") label[for^="assessmentIds"]
    the user clicks the button/link               jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link               jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element               jQuery = li:contains("${COVIDdeminimusapplicationTitle1}") strong:contains("Recommended")

the assessor adds score for every question
    [Arguments]   ${no_of_questions}
    The user clicks the button/link                       link = Scope
    The user selects the index from the drop-down menu    1    css = .research-category
    The user clicks the button/link                       jQuery = label:contains("Yes")
    :FOR  ${INDEX}  IN RANGE  1  ${no_of_questions}
      \    the user clicks the button/link    css = .next
      \    The user selects the option from the drop-down menu    23    css = .assessor-question-score
    The user clicks the button with resubmission              jquery = button:contains("Save and return to assessment overview")

the user completes assessment and moves to PS
    the user set assessor score notification to yes
    invite assessor the the assesment
    log in as a different user                     &{Comp_admin1_credentials}
    making the application a successful project    ${COVIDdeminimuscompetitionId}  ${COVIDdeminimusapplicationTitle1}
    the user reads his email                       steve.smith@empire.com   	${COVIDdeminimuscompetitionTitle}: Notification regarding your application ${application_id}: ${COVIDdeminimusapplicationTitle1}  Average assessor score
    moving competition to Project Setup            ${COVIDdeminimuscompetitionId}

the internal user can complete PS
    log in as a different user         &{lead_applicant_credentials}
    the user clicks the button/link    link = ${COVIDdeminimusapplicationTitle1}
    the user clicks the button/link    link = Spend profile
    the user clicks the button/link    link = Empire Ltd
    the user clicks the button/link    id = spend-profile-mark-as-complete-button
    the user clicks the button/link    jQuery = a:contains("Review and submit project spend profile")
    the user clicks the button/link    jQuery = a:contains("Submit project spend profile")
    the user clicks the button/link    id = submit-send-all-spend-profiles
    the external finance cannot access spend profile
    send and approve gol

