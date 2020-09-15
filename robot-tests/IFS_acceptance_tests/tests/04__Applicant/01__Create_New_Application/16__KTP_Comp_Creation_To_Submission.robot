*** Settings ***
Documentation  IFS-7146  KTP - New funding type
...
...            IFS-7147  KTP - New set of Terms & Conditions
...
...            IFS-7148  Replace maximum funding level drop down menu with free type field in comp setup
...
...            IFS-7812  KTP Finance Overview - Your Organisation Section
...
...            IFS-7869  KTP Comp setup: Project eligibility
...
...            IFS-7841  KTP: Knowledge base organisation type
...
...            IFS-7805  KTP Application: Users cannot see project start date
...
...            IFS-7790  KTP: Your finances - Edit
...
...            IFS-7807  KTP predefined KB list type-ahead & manual entry
...
...            IFS-7806  KTP Assigning KTA on application
...
...            IFS-8001  KTP KTA Accepting invite
...
...            IFS-8104  KTP application overview content review 
...
...            IFS-7960  KTA Deashboard
...
...            IFS-7983  KTP Your Project Finances - KTA view
...
...            IFS-7956 KTP Your Project Finances - Other Funding
...
...            IFS-8154 KTP Project Costs - consumables
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Assessor_Commons.robot

*** Variables ***
&{ktpLeadApplicantCredentials}        email=${lead_ktp_email}  password=${short_password}
&{ktpNewPartnerCredentials}           email=${new_partner_ktp_email}  password=${correct_password}
&{ktpExistingLeadCredentials}         email=${existing_lead_ktp_email}  password=${short_password}
&{ktpExistingPartnerCredentials}      email=${existing_partner_ktp_email}  password=${short_password}
&{ktpExistingAcademicCredentials}     email=${existing_academic_email}  password=${short_password}
${ktpApplicationTitle}                KTP New Application
${secondKTPApplicationTitle}          KTP Application with existing users
${ktpOrgName}                         Middlesex University Higher Education Corporation
${secondKTPOrgName}                   The University of Reading
${group_employees_header}             Number of full time employees in your corporate group (if applicable)
${group_employees}                    200
${costsValue}                         123
@{turnover}                           100000  98000   96000
@{preTaxProfit}                       98000   96000   94000
@{netCurrentAssets}                   100000  100000  100000
@{liabilities}                        20000   15000   10000
@{shareHolderFunds}                   20000   15000   10000
@{loans}                              35000   40000   45000
@{employees}                          2000    1500    1200
${associateSalaryTable}               associate-salary-costs-table
${associateDevelopmentTable}          associate-development-costs-table
${kbOrgNameTextBoxValidation}         Please enter a knowledge base organisation name.
${kbOrgTypeValidation}                Please select the type of knowledge base your organisation is.
${postcodeValidation}                 Search using a valid postcode or enter the address manually.
${selectOrgValidation}                Select your knowledge base organisation.
${noKTAInApplicationValidation}       You cannot mark as complete until a Knowledge Transfer Adviser has been added to the application.
${nonRegisteredUserValidation}        You cannot invite the Knowledge Transfer Adviser as their email address is not registered.
${acceptInvitationValidation}         You cannot mark as complete until the Knowledge Transfer Adviser has accepted the invitation.
${ktaEmail}                           simon.smith@ktn-uk.test
${nonKTAEmail}                        James.Smith@ktn-uk.test
${invitedEmailPattern}                You have been invited to be the Knowledge Transfer Adviser for the Innovation Funding Service application
${removedEmailPattern}                You have been removed as Knowledge Transfer Adviser for the Innovation Funding Service application
${invitationEmailSubject}             Invitation to be Knowledge Transfer Adviser
${applicationQuestion}                What is the business opportunity that your project addresses?
${questionTextGuidance}               Entering text to allow valid mark as complete
${removedEmailSubject}                Removed as Knowledge Transfer Adviser
${acceptInvitationTitle}              You have been invited to be a knowledge transfer adviser
${fname}                              Indi
${lname}                              Gardiner
${phone_number}                       01234567897

*** Test Cases ***
Comp Admin creates an KTP competition
    [Documentation]  IFS-7146  IFS-7147  IFS-7148 IFS-7869
    Given the user logs-in in new browser               &{Comp_admin1_credentials}
    Then the competition admin creates competition      ${KTP_TYPE_ID}  ${ktpCompetitionName}  KTP  ${compType_Programme}  2  KTP  PROJECT_SETUP  no  1  false  single-or-collaborative

Comp Admin is able to see KTP funding type has been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user clicks the button/link        link = ${ktpCompetitionName}
    Given the user clicks the button/link           link = View and update competition details
    When the user clicks the button/link            link = Initial details
    Then the user should see the element            jQuery = dt:contains("Funding type") ~ dd:contains("Knowledge Transfer Partnership (KTP)")
    [Teardown]  the user clicks the button/link     link = Competition details

Comp Admin is able to see KTP T&C's have been selected
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link     link = Terms and conditions
    Then the user should see the element      link = Knowledge Transfer Partnership (KTP)

Existing lead applicant can not apply to KTP compettition if organisation type is not knowledge base
    [Documentation]  IFS-7841  IFS-7146  IFS-7147  IFS-7148
    [Setup]  get competition id and set open date to yesterday      ${ktpCompetitionName}
    Given Log in as a different user                                &{ktpExistingLeadCredentials}
    When the user select the competition and starts application     ${ktpCompetitionName}
    And the user clicks the button/link                             id=save-organisation-button
    Then the user should see the element                            jQuery = h1:contains("${invalidOrganisationValidationMessage}")

Existing lead applicant can apply to KTP competition with knowledge base organisation
    [Documentation]  IFS-7841
    Given the user navigates to the page                    ${server}/organisation/select
    And the user apply with knowledge base organisation     Reading   ${secondKTPOrgName}
    When the user clicks the button/link                    link = Application team
    Then the user should see the element                    jQuery = h2:contains("${secondKTPOrgName}")

KTP application shows the correct guidance text
    [Documentation]  IFS-8104
    Given the user clicks the button/link                      link = Application overview
    When the user should see the element                       jQuery = h1:contains("Application overview")
    Then the user should see the element                       jQuery = p:contains("This section contains the background information we need for your project.")
    And the user should not see the element                    jQuery = p:contains("These are the questions which will be marked by the assessors.")
    
Existing/new partner can only see business Or non profit organisation types
    [Documentation]  IFS-7841    
    Given the lead invites already registered user             ${existing_partner_ktp_email}  ${ktpCompetitionName}
    When logging in and error checking                         &{ktpExistingPartnerCredentials}
    Then the user clicks the button/link                       link = Join with a different organisation
    And the user should only see KB partner organisations

Existing/new partner can apply to KTP competition with business organisation
    [Documentation]  IFS-7812  IFS-7841
    Given the user clicks the button/link     link = Back to your organisation
    When the user clicks the button/link      id = save-organisation-button
    And the user clicks the button/link       link = Application team
    Then the user should see the element      jQuery = h2:contains("${secondKTPOrgName}")
    And the user should see the element       jQuery = h2:contains("${organisationSmithName}")

Existing/new partner can not apply to KTP competition with academic/research organisations
    [Documentation]  IFS-7812  IFS-7841
    Given log in as a different user                   &{ktpExistingLeadCredentials}
    When the user clicks the button/link               link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    And the lead invites already registered user       ${existing_academic_email}   ${ktpCompetitionName}
    When logging in and error checking                 &{ktpExistingAcademicCredentials}
    And the user clicks the button/link                id=save-organisation-button
    Then the user should see the element               jQuery = h1:contains("${invalidOrganisationValidationMessage}")

Existing/new partner can apply to KTP competition with non profit organisations
    [Documentation]  IFS-7841
    Given the user navigates to the page                       ${server}/organisation/select
    And the user clicks the button/link                        link = Join with a different organisation
    When the user slectes non profitable organisation type
    And the user clicks the button/link                        link = Application team
    Then the user should see the element                       jQuery = h2:contains("${existingAcademicPartnerOrgName}")

Enter knowledge base organisation details manually field validations
    [Documentation]  IFS-7807
    Given the user creates a new application with a different organisation
    When the user clicks the button/link                                       link = enter its details manually
    And the user clicks the button/link                                        jQuery = button:contains("Save and continue")
    Then the user is able to see validation messages

Existing lead applicant can enter catapult knowledge base organisation details manually
    [Documentation]  IFS-7807
    Given the user enters kb organisation details manually           KB Catapult Org   CATAPULT   RGCATAPULT123   catapultNumber
    When the user clicks the button/link                             jQuery = button:contains("Save and continue")
    Then the user should see knowledge base organisation details     Knowledge base   Catapult   KB Catapult Org   RGCATAPULT123   Montrose House 1   Registration number

Existing lead applicant can enter RTO knowledge base organisation details manually
    [Documentation]  IFS-7807
    Given the user clicks the button/link                            link = Back to enter details manually
    When the user enters kb organisation details manually            KB RTO Org   RTO   RGRTO123   rtoNumber
    And the user clicks the button/link                              jQuery = button:contains("Save and continue")
    Then the user should see knowledge base organisation details     Knowledge base   Research and technology organisation (RTO)   KB RTO Org   RGRTO123   Montrose House 1   Registration number

Existing lead applicant can enter university knowledge base organisation details manually
    [Documentation]  IFS-7807
    Given the user clicks the button/link                            link = Back to enter details manually
    When the user enters kb organisation details manually            KB University Org   UNIVERSITY   UKPRN123   universityNumber
    And the user clicks the button/link                              jQuery = button:contains("Save and continue")
    Then the user should see knowledge base organisation details     Knowledge base   University   KB University Org   UKPRN123   Montrose House 1   UKPRN number

Existing lead applicant verifies the organisation details entered manually
    [Documentation]  IFS-7807
    When the user clicks the button/link     id = knowledge-base-confirm-organisation-cta
    And the user clicks the button/link      link = Application team
    Then the user should see the element     jQuery = h2:contains("KB University Org")

New lead applicant starts KTP competition
    [Documentation]  IFS-7841
    Given get competition id and set open date to yesterday         ${ktpCompetitionName}
    When log in as a different user                                 &{ktpLeadApplicantCredentials}
    Then the user select the competition and starts application     ${ktpCompetitionName}

Select a knowledge base organisation validations and fields
    [Documentation]  IFS-7841
    Given The user clicks the button/link                           link = Continue and create an account
    When the user clicks the button/link                            jQuery = button:contains("Confirm")
    Then the user should see a field and summary error              ${selectOrgValidation}
    And the user should see knowledge based organisation fields

New Lead applicant selects a catapult knowledge based organisation
    [Documentation]  IFS-7812  IFS-7814  IFS-7807
    When the user selects a knowledge based organisation             Digital   Digital Catapult
    And the user clicks the button/link                              jQuery = button:contains("Confirm")
    Then the user should see knowledge base organisation details     Knowledge base   Catapult   Digital Catapult   7964699   101 Euston Road   Registration number

New Lead applicant selects a RTO knowledge based organisation
    [Documentation]  IFS-7812  IFS-7814  IFS-7807
    Given the user clicks the button/link                            link = Back to select a knowledge base organisation
    When the user selects a knowledge based organisation             Earlham   Earlham Institute
    And the user clicks the button/link                              jQuery = button:contains("Confirm")
    Then the user should see knowledge base organisation details     Knowledge base   Research and technology organisation (RTO)   Earlham Institute   6855533   Norwich Research Park Innovation Centre   Registration number

New Lead applicant selects a university knowledge based organisation
    [Documentation]  IFS-7812  IFS-7814  IFS-7807
    Given the user clicks the button/link                            link = Back to select a knowledge base organisation
    When the user selects a knowledge based organisation             Middlesex University   ${ktpOrgName}
    And the user clicks the button/link                              jQuery = button:contains("Confirm")
    Then the user should see knowledge base organisation details     Knowledge base   University   ${ktpOrgName}   10004351   The Burroughs   UKPRN number

New lead applicant confirms the knowledge based organisation details and creates an account
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7812  IFS-7814  IFS-7807
    When the user clicks the button/link                    id = knowledge-base-confirm-organisation-cta
    Then the user creates an account and verifies email     Indi  Gardiner  ${lead_ktp_email}  ${short_password}

New lead applicant completes the KTP application
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7812  IFS-7814  IFS-8154
    When Logging in and Error Checking                                      &{ktpLeadApplicantCredentials}
    And the user clicks the button/link                                     jQuery = a:contains("${UNTITLED_APPLICATION_DASHBOARD_LINK}")
    Then the user completes the KTP application except application team and your funding

New lead applicant can declare any other government funding received
    [Documentation]  IFS-7956
    When the user fills in the funding information                           ${KTPapplicationTitle}   yes
    And the user clicks the button/link                                      link = Your funding
    Then the user should see the element                                     jQuery = dt:contains("Funding level")+dd:contains("10.00%")
    And the user should see the readonly view of other funding received
    And the user should see KTP finance sections are complete

New lead applicant invites a new partner organisation user and fills in project finances
    [Documentation]  IFS-7812  IFS-7814
    Given the user clicks the button/link                link = Back to application overview
    And the lead invites a non-registered user           ${new_partner_ktp_email}  ${ktpCompetitionName}  ${ktpApplicationTitle}  yes  Emma  Grant
    When the user clicks the button/link                 link = Sign in
    And Logging in and Error Checking                    &{ktpNewPartnerCredentials}
    And the user clicks the button/link                  link = ${ktpApplicationTitle}
    Then the user completes partner project finances     ${ktpApplicationTitle}  yes

Partner applicant can declare any other government funding received
    [Documentation]  IFS-7956
    When the user fills in the funding information                           ${KTPapplicationTitle}   yes
    And the user clicks the button/link                                      link = Other funding
    Then the user should see the readonly view of other funding received
    And the user should see KTP finance sections are complete

System should display a validation if no email address entered while inviting the KTA
    [Documentation]  IFS-7806
    Given Log in as a different user                       &{ktpLeadApplicantCredentials}
    When the user clicks the button/link                   link = ${ktpApplicationTitle}
    And the user clicks the button/link                    link = Application team
    And the user clicks the button/link                    name = invite-kta
    Then the user should see a field and summary error     ${nonRegisteredUserValidation}

The applicant should not be able to mark the application team section as complete until lead applicant adds a KTA to the application
    [Documentation]  IFS-7806
    When the user clicks the button/link                   id = application-question-complete
    Then the user should see a field and summary error     ${noKTAInApplicationValidation}

System should not allow a KTA to be invited if they do not have a KTA account in IFS
    [Documentation]  IFS-7806
    Given the user enters text to a text field             id = ktaEmail   ${nonKTAEmail}
    When the user clicks the button/link                   name = invite-kta
    Then the user should see a field and summary error     ${nonRegisteredUserValidation}

The applicant invites a KTA user to the application
    [Documentation]  IFS-7806
    [Setup]  Assign the KTA role to the user
    Given Log in as a different user               &{ktpLeadApplicantCredentials}
    When the user invites a KTA to application     ${ktpApplicationTitle}   ${ktaEmail}
    Then The user reads his email                  ${ktaEmail}   ${invitationEmailSubject}   ${invitedEmailPattern}
    And the user should see the element            jQuery = td:contains("pending for 0 days")
    And the user should see the element            Jquery = td:contains("${ktaEmail}")

The applicant should not be able to mark the application team section as complete until the KTA has accepted the invitation to join the application
    [Documentation]  IFS-7806
    When the user clicks the button/link                   id = application-question-complete
    Then the user should see a field and summary error     ${acceptInvitationValidation}

The applicant can resend the invite to the existing KTA
    [Documentation]  IFS-7806
    When the user clicks the button/link     name = resend-kta
    Then The user reads his email            ${ktaEmail}   ${invitationEmailSubject}   ${invitedEmailPattern}

The applicant can remove pending KTA from the application and send a notification to the KTA
    [Documentation]  IFS-7806
    When the user clicks the button/link         name = remove-kta
    Then the user should not see the element     name = remove-kta
    And The user reads his email                 ${ktaEmail}   ${removedEmailSubject}   ${removedEmailPattern}

The applicant invites the KTA again
    [Documentation]  IFS-7806  IFS-8001
    Given the user enters text to a text field      id = ktaEmail   ${ktaEmail}
    When the user clicks the button/link            name = invite-kta
    Then the user should see the element            jQuery = td:contains("pending for 0 days")
    [Teardown]  Logout as user

The KTA can see application name, organisation and lead applicant details in the invite
    [Documentation]  IFS-7806  IFS-8001
    When the user reads his email and clicks the link                                 ${ktaEmail}   ${invitationEmailSubject}   ${invitedEmailPattern}
    Then KTA should see application name, organisation and lead applicant details

The KTA can see the dashboard with assesments and applications tiles after accepting the invite and logging in
    [Documentation]  IFS-7960
    Given the user clicks the button/link     jQuery = a:contains("Continue")
    When logging in and error checking        ${ktaEmail}   ${short_password}
    Then the user should see the element      jQuery = h2:contains("Assessments")
    And the user should see the element       jQuery = h2:contains("Applications")

The KTA can see the read only view of the application/s
    [Documentation]  IFS-7983
    Given the user clicks the button/link     jQuery = h2:contains("Applications")
    When the user clicks the button/link      link = ${ktpApplicationTitle}
    Then the user should see the element      jQuery = h1:contains("Application overview")
    And the user should see the element       jQuery = h2:contains("Project details")

The KTA cannot edit any application section
    [Documentation]  IFS-7983
    When the user clicks the button/link        id = accordion-questions-heading-2-1
    Then the user should see the element        jQuery = span:contains("${applicationQuestion}")
    And the user should not see the element     jQuery = p:contains("${questionTextGuidance}")

The KTA is able to see lead applicant's project finances
    #Then should be changed once IFS-7958 is merged so that we can verify funding breakdown in KTA view. Remove this comment after that.
    [Documentation]  IFS-7983
    Given the user clicks the button/link     id = accordion-questions-heading-3-1
    When the user clicks the button/link      jQuery = div:contains("${ktpOrgName}") ~ a:contains("View finances")
    Then the user should see the element      jQuery = h2:contains("Finance summary")

The KTA is able to see non-lead applicant's project finances
     #Then should be changed once IFS-7958 is merged so that we can verify funding breakdown in KTA view. Remove this comment after that.
     [Documentation]  IFS-7983
     Given the user clicks the button/link     link = Back to application overview
     When the user clicks the button/link      jQuery = div:contains("${newPartnerOrgName}") ~ a:contains("View finances")
     Then the user should see the element      jQuery = h2:contains("Finance summary")

Lead applicant verifies the inviation is accepted.
    [Documentation]  IFS-7806  IFS-8001
    When log in as a different user              &{ktpLeadApplicantCredentials}
    And the user navigates to the page           ${server}/application/${ApplicationID}/form/question/1994/team
    Then the user should not see the element     name = resend-kta

New lead applicant submits the application
   [Documentation]  IFS-7812  IFS-7814
   When the user clicks the button/link             link = Application overview
   And the applicant completes Application Team
   Then the applicant submits the application

Moving KTP Competition to Project Setup
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given Log in as a different user                   &{internal_finance_credentials}
    Then moving competition to Closed                  ${competitionId}
    And making the application a successful project    ${competitionId}  ${ktpApplicationTitle}
    And moving competition to Project Setup            ${competitionId}
    [Teardown]  Requesting IDs of this Project

the project finance user cannot see the project start date
    [Documentation]  IFS-7805
    Given Log in as a different user                   &{internal_finance_credentials}
    When the user navigates to the page                ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user clicks the button/link               link = ${ApplicationID}
    And the user should not see the element            jQuery = dt:contains("When do you wish to start your project?")
    And the user should see the element                jQuery = dt:contains("Duration in months")

The lead is able to complete the Project details section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user                           &{ktpLeadApplicantCredentials}
    Given the user navigates to the page                          ${server}/project-setup/project/${ProjectID}
    When the user is able to complete project details section
    Then the user should see the element                          jQuery = .progress-list li:nth-child(1):contains("Completed")

The lead cannot see the project start date
    [Documentation]  IFS-7805
    When the user clicks the button/link            link = Project details
    Then the user sees the text in the element      id = start-date    ${empty}
    [Teardown]  the user clicks the button/link     id = return-to-set-up-your-project-button

The lead is able to complete Project team section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link                link = Project team
    When the user completes the project team section
    Then the user should see the element                 jQuery = .progress-list li:nth-child(2):contains("To be completed")

The lead is able to complete the Documents section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user clicks the button/link                link = Documents
    When the user uploads the exploitation plan
    And the user uploads the Test document type
    And the user uploads the Collaboration agreement
    And the user clicks the button/link                  link = Set up your project
    Then the user should see the element                 jQuery = .progress-list li:nth-child(3):contains("Awaiting review")

The lead is able to complete the Bank details section
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the user enters bank details
    When the user clicks the button/link     link = Set up your project
    Then the user should see the element     jQuery = .progress-list li:nth-child(5):contains("Awaiting review")

The partner is able to complete Project team Section
    [Documentation]  IFS-7812
    [Setup]  log in as a different user             &{ktpNewPartnerCredentials}
    Given the user clicks the button/link           link = ${ktpApplicationTitle}
    And the user clicks the button/link             link = Project team
    When The user selects their finance contact     financeContact1
    And the user clicks the button/link             link = Set up your project
    Then the user should see the element            jQuery = .progress-list li:nth-child(2):contains("Completed")

Internal user is able to approve documents
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user                  &{Comp_admin1_credentials}
    Given Internal user is able to approve documents
    When the user navigates to the page                  ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element                 css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

Internal user cannot see the project start date
    [Documentation]  IFS-7805
    When the user clicks the button/link            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)
    Then the user sees the text in the element      id = start-date    ${empty}
    [Teardown]  the user clicks the button/link     link = Back to project setup

Internal user is able to assign an MO
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  the user navigates to the page         ${server}/project-setup-management/project/${ProjectID}/monitoring-officer
    Given Search for MO                             Orvill  Orville Gibbs
    When The internal user assign project to MO     ${ApplicationID}  ${ktpApplicationTitle}
    And the user navigates to the page              ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

Finance user approves bank details
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    [Setup]  log in as a different user                         &{internal_finance_credentials}
    When the project finance user approves bank details for     ${ktpOrgName}  ${ProjectID}
    Then the user navigates to the page                         ${server}/project-setup-management/competition/${competitionId}/status/all
    And the user should see the element                         css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(5)

Internal user is able to approve Finance checks and generate spend profile
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7812
    [Setup]  the user navigates to the page     ${server}/project-setup-management/project/${ProjectID}/finance-check
    Given the user approves Eligibility         ${ProjectID}
    And the user approves Viability             ${ProjectID}
    And the user approves Spend Profile
    When the user navigates to the page         ${server}/project-setup-management/competition/${competitionId}/status/all
    Then the user should see the element        css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(6)
    And the user should see the element         css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(7)

The partner is able to submit the spend profile and should not see the project start date
    [Documentation]  IFS-7812  IFS-7805
    [Setup]  log in as a different user             &{ktpNewPartnerCredentials}
    Given The partner submits the spend profile     ${ProjectID}  ${partnerOrgId}
    And the user should not see the element         jQuery = dt:contains("Project start date")

The lead is able to submit the spend profile and should not see the project start date
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7805
    [Setup]  Requesting KTP Organisation ID
    Given log in as a different user            &{ktpLeadApplicantCredentials}
    When the user navigates to the page         ${server}/project-setup/project/${ProjectID}/partner-organisation/${ktpOrganisationID}/spend-profile/review
    And the user should not see the element     jQuery = dt:contains("Project start date")
    Then the user submits the spend profile
    And the user should see the element         jQUery = .progress-list li:nth-child(7):contains("Awaiting review")

Internal user is able to approve Spend profile and generates the GOL
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given proj finance approves the spend profiles     ${ProjectID}
    Then the user should see the element               css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(7)
    And internal user generates the GOL                ${ProjectID}

Applicant is able to upload the GOL
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given log in as a different user         &{ktpLeadApplicantCredentials}
    When Applicant uploads the GOL           ${ProjectID}
    Then the user should see the element     jQUery = .progress-list li:nth-child(8):contains("Awaiting review")

Internal user is able to approve the GOL and the project is now Live
    [Documentation]  IFS-7146  IFS-7147  IFS-7148
    Given the internal user approve the GOL                                    ${ProjectID}
    When log in as a different user                                            &{ktpLeadApplicantCredentials}
    And the user navigates to the page                                         ${server}/project-setup/project/${ProjectID}
    Then the user should see project is live with review its progress link

*** Keywords ***
the user marks the KTP project costs, location and organisation information as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}
    the user fills in ktp project costs
    the user enters the project location
    the user fills in the KTP organisation information       ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user clicks the button/link                          link = Back to application overview

the user fills in the KTP organisation information
    [Arguments]  ${Application}  ${org_size}
    the user navigates to Your-finances page                    ${Application}
    the user clicks the button/link                             link = Your organisation
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  page should contain element  jQuery = button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'      the user clicks the button/link  jQuery = button:contains("Edit")
    the user selects the radio button                           organisationSize  ${org_size}
    the user enters text to a text field                        name = financialYearEndMonthValue  04
    the user enters text to a text field                        name = financialYearEndYearValue   2020
    the user fills financial overview section
    the user clicks the button/link                             jQuery = button:contains("Mark as complete")
    the user checks the read only view for KTP Organisation

the user checks the read only view for KTP Organisation
    the user clicks the button/link     link = Your organisation
    the user should see the element     jQuery = dt:contains("${group_employees_header}") ~ dd:contains("${group_employees}")
    the user clicks the button/link     link = Your project finances

the user fills financial overview section
    ${i} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{turnover}
             \    the user enters text to a text field     id = years[${i}].turnover  ${ELEMENT}
             \    ${i} =   Evaluate   ${i} + 1

    ${j} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{preTaxProfit}
             \    the user enters text to a text field     id = years[${j}].preTaxProfit  ${ELEMENT}
             \    ${j} =   Evaluate   ${j} + 1

    ${k} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{netCurrentAssets}
             \    the user enters text to a text field     id = years[${k}].currentAssets  ${ELEMENT}
             \    ${k} =   Evaluate   ${k} + 1

    ${l} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{liabilities}
             \    the user enters text to a text field     id = years[${l}].liabilities  ${ELEMENT}
             \    ${l} =   Evaluate   ${l} + 1

    ${m} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{shareHolderFunds}
             \    the user enters text to a text field     id = years[${m}].shareholderValue  ${ELEMENT}
             \    ${m} =   Evaluate   ${m} + 1

    ${n} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{loans}
             \    the user enters text to a text field     id = years[${n}].loans  ${ELEMENT}
             \    ${n} =   Evaluate   ${n} + 1

    ${a} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{employees}
             \    the user enters text to a text field     id = years[${a}].employees  ${ELEMENT}
             \    ${a} =   Evaluate   ${a} + 1

    the user enters text to a text field     id = groupEmployees  ${group_employees}

the user approves Eligibility
    [Arguments]  ${project}
    Requesting Organisation IDs
    the user navigates to the page      ${server}/project-setup-management/project/${project}/finance-check/organisation/${leadOrgId}/eligibility
    the user approves project costs
    the user navigates to the page      ${server}/project-setup-management/project/${project}/finance-check/organisation/${partnerOrgId}/eligibility
    the user approves project costs

the user approves viability
    [Arguments]  ${project}
    project finance approves Viability for     ${leadOrgId}  ${project}
    project finance approves Viability for     ${partnerOrgId}  ${project}

the user approves spend profile
     the user clicks the button/link      link = Return to finance checks
     the user clicks the button/link      link = Generate spend profile
     the user clicks the button/link      css = #generate-spend-profile-modal-button
     the user should see the element      jQuery = .success-alert p:contains("The finance checks have been approved and profiles generated.")

Requesting Organisation IDs
    ${leadOrgId} =    get organisation id by name     ${ktpOrgName}
    Set suite variable      ${leadOrgId}
    ${partnerOrgId} =  get organisation id by name    ${newPartnerOrgName}
    Set suite variable      ${partnerOrgId}

Internal user is able to approve documents
    the user navigates to the page               ${server}/project-setup-management/project/${ProjectID}/document/all
    the user clicks the button/link              link = Exploitation plan
    internal user approve uploaded documents
    the user clicks the button/link              link = Return to documents
    the user clicks the button/link              link = Test document type
    internal user approve uploaded documents
    the user clicks the button/link              link = Return to documents
    the user clicks the button/link              link = Collaboration agreement
    internal user approve uploaded documents
    the user clicks the button/link              link = Return to documents

the user completes the KTP application except application team and your funding
    the user clicks the button/link                                                             link = Application details
    the user fills in the KTP Application details                                               ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant marks EDI question as complete
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                                                    ${ktpApplicationTitle}
    the user marks the KTP project costs, location and organisation information as complete     ${ktpApplicationTitle}   Calculate  52,214
    the user accept the competition terms and conditions                                        Return to application overview

the user fills in the KTP Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user should see the element                jQuery = h1:contains("Application details")
    the user should not see the element            id = startDate
    the user enters text to a text field           id = name  ${appTitle}
    the user enters text to a text field           id = durationInMonths  24
    the user clicks the button twice               css = label[for="resubmission-no"]
    the user can mark the question as complete
    the user should see the element                jQuery = li:contains("Application details") > .task-status-complete

The user completes the research category
    [Arguments]  ${res_category}
    the user clicks the button/link      link=Research category
    the user selects the checkbox        researchCategory
    the user clicks the button/link      jQuery=label:contains("${res_category}")
    the user clicks the button/link      id=application-question-complete
    the user should see the element      jQuery=li:contains("Research category") > .task-status-complete

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Requesting IDs of this Project
    ${ProjectID} =  get project id by name    ${ktpApplicationTitle}
    Set suite variable    ${ProjectID}

Requesting IDs of this application
    ${ApplicationID} =  get application id by name    ${ktpApplicationTitle}
    Set suite variable    ${ApplicationID}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in ktp project costs
    the user clicks the button/link             link = Your project costs
    the user fills in Associate employment
    the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   css = textarea[id$="associateSalary.description"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Additional company cost estimation")
    the user fills additional company costs     description  100
    the user clicks the button/link             css = label[for="stateAidAgreed"]
    the user clicks the button/link             jQuery = button:contains("Mark as complete")

the user fills in Associate employment
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="${associateSalaryTable}"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate employment")
    the user enters text to a text field    jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="duration"]  ${costsValue}  
    the user enters text to a text field    jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${costsValue}  

the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="${associateDevelopmentTable}"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate development")
    the user enters text to a text field    jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${costsValue}  

Requesting KTP Organisation ID
    ${ktpOrganisationID} =  get organisation id by name     ${ktpOrgName}
    Set suite variable      ${ktpOrganisationID}

the user should see knowledge based organisation fields
    the user should see the element     jQuery = p:contains("Only a knowledge base organisation can lead this application.")
    the user should see the element     jQuery = h1:contains("Select a knowledge base organisation")
    the user should see the element     jQuery = span:contains("Select your knowledge base organisation.")
    the user should see the element     jQuery = span:contains("Create an account")
    the user should see the element     jQuery = label:contains("Find your organisation")

the user selects a knowledge based organisation
    [Arguments]   ${knowledgeBase}  ${completeKBOrganisartionName}
    input text                          id = knowledgeBase        ${knowledgeBase}
    the user clicks the button/link     jQuery = ul li:contains("${completeKBOrganisartionName}")

the user apply with knowledge base organisation
    [Arguments]   ${knowledgeBase}  ${completeKBOrganisartionName}
    the user clicks the button/link                     link = Apply with a different organisation
    the user selects a knowledge based organisation     ${knowledgeBase}  ${completeKBOrganisartionName}
    the user clicks the button/link                     jQuery = button:contains("Confirm")
    the user clicks the button/link                     id = knowledge-base-confirm-organisation-cta

the user should only see KB partner organisations
    the user should see the element         jQuery = span:contains("${businessOrganisationName}") + span:contains("${bussinessOrgInfoText}")
    the user should see the element         jQuery = span:contains("${nonProfitOrganisationName}") + span:contains("${nonJe-s/Public/CharityOrgInfoText}")
    the user should not see the element     jQuery = span:contains("${researchOrganisationName}") + span:contains("${researchOrgInfoText}")
    the user should not see the element     jQuery = span:contains("${rtoOrganisationName}") + span:contains("${rtoOrgInfoText}")

the user slectes non profitable organisation type
    the user selects the radio button                           organisationTypeId   4
    the user clicks the button/link                             jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    worth   ${existingAcademicPartnerOrgName}

the user should see knowledge base organisation details
    [Arguments]   ${orgType}  ${kbType}  ${orgName}  ${orgNumber}  ${orgAddress}  ${regOrUKPRNNumber}
    the user should see the element     jQuery = dt:contains("Organisation type")+dd:contains("${orgType}")
    the user should see the element     jQuery = dt:contains("Knowledge base type")+dd:contains("${kbType}")
    the user should see the element     jQuery = dt:contains("Organisation name")+dd:contains("${orgName}")
    the user should see the element     jQuery = dt:contains("${regOrUKPRNNumber}")+dd:contains("${orgNumber}")
    the user should see the element     jQuery = dt:contains("Address")+dd:contains("${orgAddress}")

the user is able to see validation messages
    the user should see a field and summary error     ${kbOrgNameTextBoxValidation}
    the user should see a field and summary error     ${kbOrgTypeValidation}
    the user should see a field and summary error     ${postcodeValidation}

the user enters kb organisation details manually
    [Arguments]  ${orgName}  ${kbType}  ${orgNumber}  ${regOrUKPRNNumber}
    the user enters text to a text field     id = name   ${orgName}
    the user selects the radio button        type   ${kbType}
    the user enters text to a text field     id = ${regOrUKPRNNumber}   ${orgNumber}
    the user looks for address using postcode

the user creates a new application with a different organisation
    the user select the competition and starts application     ${ktpCompetitionName}
    the user selects the radio button                          createNewApplication   true
    the user clicks the button/link                            name = create-application-submit
    the user clicks the button/link                            link = Apply with a different organisation

KTA should see application name, organisation and lead applicant details
    Requesting IDs of this application
    the user should see the element     jQuery = h1:contains("${acceptInvitationTitle}")
    the user should see the element     jQuery = dt:contains("Lead organisation")+dd:contains("${ktpOrgName}")
    the user should see the element     jQuery = dt:contains("Lead applicant")+dd:contains("Indi Gardiner")
    the user should see the element     jQuery = dt:contains("Application")+dd:contains("${ApplicationID}: ${ktpApplicationTitle}")

Assign the KTA role to the user
    log in as a different user               &{ifs_admin_user_credentials}
    the user clicks the button/link          link = Manage users
    the user enters text to a text field     id = filter   ${ktaEmail}
    the user clicks the button/link          css = [class="btn"]
    the user clicks the button/link          jQuery = a:contains("Edit")
    the user clicks the button/link          link = Add a new external role profile
    the user clicks the button/link          jQuery = button:contains("Confirm role profile")
    the user clicks the button/link          jQuery = button:contains("Save and return")

the user invites a KTA to application
    [Arguments]  ${applicationName}   ${email}
    the user clicks the button/link          link = ${applicationName}
    the user clicks the button/link          link = Application team
    the user enters text to a text field     id = ktaEmail   ${email}
    the user clicks the button/link          name = invite-kta

the user should see the readonly view of other funding received
    the user should see the element     jQuery = th:contains("Lottery funding")
    the user should see the element     jQuery = td:contains("12-${nextyear}")
    the user should see the element     jQuery = th:contains("Total other funding") ~ td:contains("£20,000")
    the user should see the element     jQuery = th:contains("Lottery funding") ~ td:contains("£20,000")

the user should see KTP finance sections are complete
    the user clicks the button/link     link = Return to finances
    the user should see the element     css = li:nth-of-type(1) .task-status-complete
    the user should see the element     css = li:nth-of-type(2) .task-status-complete
    the user should see the element     css = li:nth-of-type(3) .task-status-complete
    the user should see the element     css = li:nth-of-type(4) .task-status-complete