*** Settings ***
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${COVIDcompetitionTitle}   596 Covid grants framework group
${COVIDcompetitionId}      ${competition_ids['${COVIDcompetitionTitle}']}
${COVIDapplicationTitle1}  Covid Application
${COVIDapplicationTitle2}  Covid Application2

*** Test Cases ***
Create application to covid comp
    Given the user navigates to the page   ${server}/competition/${COVIDcompetitionId}/overview
    the user clicks the button/link                           jQuery = a:contains("Start new application")
    the user clicks the button/link                           jQuery = button:contains("Save and continue")
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${COVIDapplicationTitle1}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant adds contributor to Application Team
    And the user selects research category              Feasibility studies
    the lead applicant fills all the questions and marks as complete(programme)
    the user accept the competition terms and conditions     Return to application overview
    the user navigates to Your-finances page                 ${COVIDapplicationTitle1}
    the user enters the project location
    the user fills in the organisation information  ${COVIDapplicationTitle1}  ${SMALL_ORGANISATION_SIZE}


Applicant is asked for funding sought
    the user clicks the button/link   link = Your funding
    the user enters text to a text field  id = amount   57,803
    the user selects the radio button       otherFunding  false
    And the user clicks the button/link        id = mark-all-as-complete
    the user clicks the button/link  link = Your project costs
    the user fills in Other costs
    the user clicks the button/link  css = label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery = button:contains("Mark as complete")
    the user clicks the button/link  link = Back to application overview

Submit application
    then the user clicks the button/link  id = application-overview-submit-cta
    the user should not see the element  jQuery = .message-alert:contains("You will not be able to make changes")
    then the user clicks the button/link   id = submit-application-button
    then the user should see the element   link = Reopen application

Non lead cannot reopen competition
    Given log in as a different user      collaborator@example.com  ${correct_password}
    When The user should see the element     link = ${COVIDapplicationTitle1}
    Then the user should not see the element   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")

Lead can reopen application
   [Setup]  log in as a different user   &{lead_applicant_credentials}
   Given the user clicks the button/link  link = Dashboard
   When the user clicks the button/link   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")
   And the user clicks the button/link    css = input[type="submit"]
   Then the user should see the element   jQuery = .message-alert:contains("Now your application is complete")

Lead can make changes and resubmit
    then the user clicks the button/link  id = application-overview-submit-cta
    the user should not see the element  jQuery = .message-alert:contains("You will not be able to make changes")
    then the user clicks the button/link   id = submit-application-button

Internal user cannot invite to assesment
    Given Log in as a different user       &{Comp_admin1_credentials}
    When The user clicks the button/link   link = ${COVIDcompetitionTitle}
    Then The user should see the element   jQuery = .disabled:contains("Invite assessors to assess the competition")
    And The user should see the element    jQuery = .disabled:contains("Manage assessments")

Internal user can send funding notification
    [Setup]
    get application id by name and set as suite variable   ${COVIDapplicationTitle1}
    the user clicks the button/link    link = Input and review funding decision
    the user selects the checkbox      app-row-1
    the user clicks the button/link    jQuery = button:contains("Successful")
    the user clicks the button/link    link = Competition
        Given the user clicks the button/link  link = Manage funding notifications
        When the user selects the checkbox     app-row-${application_id}
        And the user clicks the button/link    id = write-and-send-email
            Given the user clicks the button/link        jQuery = button:contains("Send email")[data-js-modal = "send-to-all-applicants-modal"]
            When the user clicks the button/link         jQuery = .send-to-all-applicants-modal button:contains("Send email")

Applicant can no longer reopen the competition
    Given Log in as a different user           &{lead_applicant_credentials}
    When The user should see the element       link = ${COVIDapplicationTitle1}
    Then the user should not see the element   jQuery = li:contains("${COVIDapplicationTitle1}") a:contains("Reopen")

Competition is in Live and PS tabs
    [Setup]  log in as a different user     &{Comp_admin1_credentials}
    Given the user clicks the button/link   jQuery = a:contains("Live (")
    And the user should see the element     link = ${COVIDcompetitionTitle}
    when the user clicks the button/link    jQuery = a:contains("Project setup (")
    Then the user should see the element    link = ${COVIDcompetitionTitle}

Complete PS
    Log in as a different user           &{lead_applicant_credentials}
    the user clicks the button/link    link = ${COVIDapplicationTitle1}
    the user clicks the button/link    link = Project details
    the user clicks the button/link    link = Correspondence address
    the user enter the Correspondence address
    the user clicks the button/link    id = return-to-set-up-your-project-button
    the user clicks the button/link    link = Project team
    the user clicks the button/link    link = Project manager
    the user selects the radio button  projectManager    projectManager1
    the user clicks the button/link    id = save-project-manager-button
    The user selects their finance contact  financeContact1
    the user clicks the button/link    link = Set up your project
    the user clicks the button/link      link = Documents
    the user clicks the button/link        link = Exploitation plan
    the user uploads the file              css = .inputfile  ${valid_pdf}
    the user should see the element        jQuery = .upload-section:contains("Exploitation plan") a:contains("${valid_pdf}")
        the user clicks the button/link     id = submit-document-button
        the user clicks the button/link     id = submitDocumentButtonConfirm
        the user clicks the button/link    link = Back to document overview
    the user fills in bank details
    the project finance approves all steps before finance

Competition goes into previous

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser   &{lead_applicant_credentials}
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the applicant adds contributor to Application Team
    the user clicks the button/link  link = Application team
    then the user clicks the button/link  jQuery = button:contains("Add person to Empire Ltd")
    the user invites a person to the same organisation   Collaborator  collaborator@example.com
    logout as user
    When the user reads his email and clicks the link      collaborator@example.com    Invitation to contribute in ${COVIDcompetitionTitle}     You will be joining as part of the organisation    2
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Yes, accept invitation")
    And the user clicks the button/link                    jQuery = .govuk-button:contains("Confirm and continue")
    And the invited user fills the create account form     Collaborator  Axe
    And the user reads his email and clicks the link       collaborator@example.com    Please verify your email address    Once verified you can sign into your account
    the user clicks the button/link       jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    Logging in and Error Checking           &{lead_applicant_credentials}
    then the user clicks the button/link    link = ${COVIDapplicationTitle1}
    the applicant completes Application Team

the user fills in bank details
    the user clicks the button/link                      link = Set up your project
    the user clicks the button/link                      link = Bank details
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

the project finance approves all steps before finance
    log in as a different user                   &{ifs_admin_user_credentials}
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(3) a
    the user clicks the button/link              link = Exploitation plan
    internal user approve uploaded documents
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(4)
    search for MO                                Orvill  Orville Gibbs
    And the internal user assign project to MO   ${application_id}  ${COVIDapplicationTitle1}
    the user navigates to the page               ${server}/project-setup-management/competition/${COVIDcompetitionId}/status/all
    the user clicks the button/link              jQuery = td.action:nth-of-type(5)
    approve bank account details
    the user clicks the button/link   jQuery = td.ok + td.ok + td.ok + td.ok + td.ok + td.action a
    confirm viability    0
        confirm eligibility  0
        all external users send spend profile
            log in as a different user         &{lead_applicant_credentials}
            the user clicks the button/link    link = ${COVIDapplicationTitle1}
            the user clicks the button/link    link = Spend profile
            the user clicks the button/link    link = Empire Ltd
            the user clicks the button/link    id = spend-profile-mark-as-complete-button
            the user clicks the button/link    jQuery = a:contains("Review and submit project spend profile")
            the user clicks the button/link    jQuery = a:contains("Submit project spend profile")
            the user clicks the button/link    id = submit-send-all-spend-profiles


confirm viability
    [Arguments]  ${viability}
    the user clicks the button/link   css = .viability-${viability}
    the user selects the checkbox                        project-viable
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link                      id = confirm-button      #Page confirmation button
    the user clicks the button/link                      name = confirm-viability   #Pop-up confirmation button
    the user clicks the button/link                      link = Return to finance checks

confirm eligibility
    [Arguments]  ${eligibility}
    the user clicks the button/link                     css = .eligibility-${eligibility}
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