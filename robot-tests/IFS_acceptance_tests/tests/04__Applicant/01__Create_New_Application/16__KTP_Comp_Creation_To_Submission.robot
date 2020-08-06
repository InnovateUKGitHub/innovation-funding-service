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

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
&{ktpLeadApplicantCredentials}        email=${lead_ktp_email}  password=${short_password}
&{ktpNewPartnerCredentials}           email=${new_partner_ktp_email}  password=${correct_password}
&{ktpExistingLeadCredentials}         email=${existing_lead_ktp_email}  password=${short_password}
&{ktpExistingPartnerCredentials}      email=${existing_partner_ktp_email}  password=${short_password}
&{ktpExistingAcademicCredentials}     email=${existing_academic_email}  password=${short_password}
${ktpApplicationTitle}                KTP Application
${secondKTPApplicationTitle}          KTP Application with existing users
${ktpOrgName}                         A Knowledge Base
${secondKTPOrgName}                   D Knowledge Base
${group_employees_header}             Number of full time employees in your corporate group (if applicable)
${group_employees}                    200
@{turnover}                           100000  98000   96000
@{preTaxProfit}                       98000   96000   94000
@{netCurrentAssets}                   100000  100000  100000
@{liabilities}                        20000   15000   10000
@{shareHolderFunds}                   20000   15000   10000
@{loans}                              35000   40000   45000
@{employees}                          2000    1500    1200

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
    And the user apply with knowledge base organisation     D knowledge   ${secondKTPOrgName}
    When the user clicks the button/link                    link = Application team
    Then the user should see the element                    jQuery = h2:contains("${secondKTPOrgName}")

Existing/new partner can only see business Or non profit organisation types
    [Documentation]  IFS-7841
    Given the user clicks the button/link                      link = Application overview
    And the lead invites already registered user               ${existing_partner_ktp_email}  ${ktpCompetitionName}
    When logging in and error checking                         &{ktpExistingPartnerCredentials}
    And the user clicks the button/link                        link = Join with a different organisation
    Then the user should only see KB partner organisations

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

New lead applicant starts KTP competition
    [Documentation]  IFS-7841
    Given get competition id and set open date to yesterday         ${ktpCompetitionName}
    When log in as a different user                                 &{ktpLeadApplicantCredentials}
    Then the user select the competition and starts application     ${ktpCompetitionName}

Select a knowledge base organisation validations and fields
    [Documentation]  IFS-7841
    Given The user clicks the button/link                                     link = Continue and create an account
    When the user clicks the button/link                                      jQuery = button:contains("Save and continue")
    Then the user should see a field and summary error                        Please select an organisation.
    And the user should see knowledge based organisation fields

New Lead applicant selects a knowledge based organisation
    [Documentation]  IFS-7812  IFS-7814
    When the user selects a knowledge based organisation     A Knowledge   ${ktpOrgName}
    Given the user clicks the button/link                    jQuery = button:contains("Save and continue")
    Then the user should see the element                     jQuery = dt:contains("Organisation type") ~ dd:contains("Knowledge base")
    And the user should see the element                      jQuery = dt:contains("Organisation name") ~ dd:contains("${ktpOrgName}")

New lead applicant creates an account and completes the KTP application
    [Documentation]  IFS-7146  IFS-7147  IFS-7148  IFS-7812  IFS-7814
    Given the user clicks the button/link                  jQuery = button:contains("Save and continue")
    And the user creates an account and verifies email     Indi  Gardiner  ${lead_ktp_email}  ${short_password}
    When Logging in and Error Checking                     &{ktpLeadApplicantCredentials}
    And the user clicks the button/link                    jQuery = a:contains("${UNTITLED_APPLICATION_DASHBOARD_LINK}")
    Then the user completes the KTP application

New lead applicant invites a new partner organisation user and fills in project finances
    [Documentation]  IFS-7812  IFS-7814
    Given the lead invites a non-registered user         ${new_partner_ktp_email}  ${ktpCompetitionName}  ${ktpApplicationTitle}  yes  Emma  Grant
    When the user clicks the button/link                 link = Sign in
    And Logging in and Error Checking                    &{ktpNewPartnerCredentials}
    And the user clicks the button/link                  link = ${ktpApplicationTitle}
    Then the user completes partner project finances     ${ktpApplicationTitle}  yes

New lead applicant submits the application
   [Documentation]  IFS-7812  IFS-7814
   Given Log in as a different user                 &{ktpLeadApplicantCredentials}
   When the user clicks the button/link             link = ${ktpApplicationTitle}
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
    Given the user navigates to the page         ${server}/project-setup-management/competition/${competitionId}/status/all
    When the user clicks the button/link         link = ${ApplicationID}
    Then the user should not see the element     jQuery = dt:contains("When do you wish to start your project?")
    And the user should see the element          jQuery = dt:contains("Duration in months")

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
    And internal user generates the GOL                NO  ${ProjectID}

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
the user marks the KTP finances as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}
    the user fills in ktp project costs
    the user enters the project location
    the user fills in the KTP organisation information       ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                     ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link                          link = Back to application overview
    the user should see the element                          jQuery = li:contains("Your project finances") > .task-status-complete

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

The user completes the KTP application
    the user clicks the button/link                                                 link = Application details
    the user fills in the KTP Application details                                   ${KTPapplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the applicant marks EDI question as complete
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                                        ${ktpApplicationTitle}
    the user marks the KTP finances as complete                                     ${ktpApplicationTitle}   Calculate  52,214
    the user accept the competition terms and conditions                            Return to application overview

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
    ${ApplicationID} =  get application id by name    ${ktpApplicationTitle}
    Set suite variable    ${ApplicationID}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in ktp project costs
    the user clicks the button/link  link = Your project costs
    the user fills in Associate employment
    the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   css = textarea[id$="associateSalary.description"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Additional company cost estimation")
    the user fills additional company costs    description  100
    the user clicks the button/link  css = label[for="stateAidAgreed"]
    the user clicks the button/link  jQuery = button:contains("Mark as complete")

the user fills in Associate employment
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="associate-salary-costs-table"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate employment")
    the user enters text to a text field    jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="duration"]  123
    the user enters text to a text field    jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  123

the user fills in Associate development
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element   jQuery = table[id="associate-development-costs-table"]
    Run Keyword If  '${status}' == 'PASS'    the user clicks the button/link         jQuery = button:contains("Associate development")
    the user enters text to a text field    jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  123

Requesting KTP Organisation ID
    ${ktpOrganisationID} =  get organisation id by name     ${ktpOrganisationName}
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
    the user clicks the button/link                     jQuery = button:contains("Save and continue")
    the user clicks the button/link                     name = save-organisation

the user should only see KB partner organisations
    the user should see the element         jQuery = span:contains("${businessOrganisationName}") + span:contains("${bussinessOrgInfoText}")
    the user should see the element         jQuery = span:contains("${nonProfitOrganisationName}") + span:contains("${nonJe-s/Public/CharityOrgInfoText}")
    the user should not see the element     jQuery = span:contains("${researchOrganisationName}") + span:contains("${researchOrgInfoText}")
    the user should not see the element     jQuery = span:contains("${rtoOrganisationName}") + span:contains("${rtoOrgInfoText}")

the user slectes non profitable organisation type
    the user selects the radio button                           organisationTypeId   4
    the user clicks the button/link                             jQuery = button:contains("Save and continue")
    the user search for organisation name on Companies house    worth   ${existingAcademicPartnerOrgName}
