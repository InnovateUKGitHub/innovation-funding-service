*** Settings ***
Documentation     IFS-7195  Organisational eligibility category in Competition setup
...
...               IFS-7246  Comp setup allowing international organisations to lead the competition
...
...               IFS-7197 As a non-UK based business I can apply for International Competition as a Lead Applicant..
...
...               IFS-7198 As a non-UK based business I can create a new account to apply to an International Competition..
...
...               IFS-7199 Read only page for organisation details should not have a banner mentioning only UK based organisations can apply for the International Competition..
...
...               IFS-7252 When an existing organisation is applying for an international competition, we need to return their organisations based on whether they are UK or International

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
&{partnerApplicantCredentialsNonUKBased}               email=${international_invite_email}   password=${short_password}
&{partnerApplicantCredentialsZeroFunding}              email=${zero_funding_partner_email}   password=${short_password}
&{partnerApplicantCredentialsUKBased}                  email=${partner_org}                  password=${correct_password}
&{leadApplicantCredentials}                            email=${lead_international_email}     password=${short_password}
&{ukLeadOrganisationCredentials}                       email=${lead_applicant}               password=${short_password}
&{internationalPartnerOrganisationCredentials}         email=${partner_international_email}  password=${short_password}
${organisationalEligibilityInfo}                       Is this competition open to organisations based outside the UK?
${ukBasedOrganisationFundingInfo}                      Your organisation must be UK based to receive funding from Innovate UK.
${projectLocationInfo}                                 Please enter the town or city where most of the project work will take place
${researchOrganisationInfoText}                        Higher education and organisations registered with Je-S.
${organisationalEligibilitySubTitle}                   Can international organisations apply?
${leadOrganisationsTitle}                              Lead organisations
${leadOrganisationsSubTitle}                           Can international organisations lead the competition?
${correspondenceAddressTitle}                          Correspondence address
${organisationBasedInUkTitle}                          Is your organisation based in the UK?
${internationalApplicationTitle}                       New Test Application for International Users
${ukLeadInternationalApplicationTitle}                 New Test Application for UK Lead International Users
${subTitleForCorrespondenceAddress}                    This is the postal address for your organisation.
${organisationalEligibilityValidationErrorMessage}     You must choose if organisations based outside the UK can apply for this competition.
${leadOrganisationsValidationErrorMessage}             You must choose if international organisations can lead the competition.
${countryValidationMessage}                            You must select the country where your organisation is based.
${townOrCityValidationMessage}                         You must enter your organisation's town or city.
${streetValidationMessage}                             You must enter your organisation's street address.
${chooseYourOragnisationTypeInfoText}                  This is the organisation that will lead the application.
${projectLocationValidationErrorMessage}               This field cannot be left blank.
${internationalOrganisationFirstLineAddress}           7 Pinchington Lane
${addressLine1}                                        7 Fisher House, Sydney,
${newAddress}                                          7 Fisher House
${partnerOrganisationNameNonUKBased}                   Test Empire
${zeroFundingPartnerOrgnaisationName}                  UNIVERSITY OF LIVERPOOL
${partnerOrganisationNameUKBased}                      INNOVATE LTD
${leadApplicantOrganisationName}                       New Empire 1
${ukLeadOrganisationName}                              org2
${internationalPartnerOrganisation}                    New Empire


*** Test Cases ***
Comp admin can only access organisational eligibility category after intial details entered
     [Documentation]  IFS-7195
     Given the user navigates to the page              ${CA_UpcomingComp}
     And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
     When the user fills in the CS Initial details     ${internationalLeadInternationalCompetition}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     Then the user should see the enabled element      link = ${organisationalEligibilityTitle}
     And the user should not see the element           jQuery = li:contains("${organisationalEligibilityTitle}") .task-status-complete

Eligibility is changed to project eligibility in project eligibility category
     [Documentation]  IFS-7195
     When the user clicks the button/link                 link = ${projectEligibilityLink}
     Then the user should see the text in the element     jQuery = h1:contains("${projectEligibilityLink}")        ${ProjectEligibilityLink}
     And the user should see the element                  jQuery = span:contains("${organisationalEligibilityTitle}")

Eligibility is changed to project eligibility in pagination
     [Documentation]  IFS-7195
     Given the user clicks the button/link                css = a[rel="Prev"]
     When the user should see the text in the element     jQuery = span:contains("${projectEligibilityLink}")     ${ProjectEligibilityLink}
     And the user clicks the button/link                  jQuery = span:contains("${projectEligibilityLink}")
     And the user clicks the button/link                  jQuery = span:contains("${organisationalEligibilityTitle}")
     Then the user should see the text in the element     jQuery = span:contains("${projectEligibilityLink}")     ${ProjectEligibilityLink}

Comp admin can not complete the competition setup without organisational eligibility category completetion
     [Documentation]  IFS-7195
     Given the user clicks the button/link                                                 link = Return to setup overview
     When the user completes all categories except organisational eligibility category     ${business_type_id}  KTP  ${compType_Programme}  project-setup-completion-stage  yes  1  true  collaborative
     Then The user should see the element                                                  css = #compCTA[disabled]

Comp admin can access the Organisational eligibility category and check for all required fields
     [Documentation]    IFS-7195
     When the user clicks the button/link                           link = ${organisationalEligibilityTitle}
     Then the user checks for organisational eligibility fields

Organisational eligibility validations
     [Documentation]    IFS-7195
     When the user clicks the button/link                   jQuery = button:contains("Save and continue")
     Then The user should see a field and summary error     ${organisationalEligibilityValidationErrorMessage}

Comp admin sets organisational eligibility to No
     [Documentation]    IFS-7195 IFS-7246
     When the user selects the radio button                                             internationalOrganisationsApplicable     false
     And The user should not see a field and summary error                              ${organisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                                                jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility response question and answer

Comp admin sets organisational eligibility to Yes and check for lead organisations fields
     [Documentation]    IFS-7195 IFS-7246
     Given the user clicks the button/link                     jQuery = button:contains("Edit")
     When the user selects the radio button                    internationalOrganisationsApplicable     true
     And The user should not see a field and summary error     ${organisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                       jQuery = button:contains("Save and continue")
     Then the user checks for lead organisations fields

Lead organisations validations
     [Documentation]    IFS-7246
     When the user clicks the button/link                   jQuery = button:contains("Save and continue")
     Then The user should see a field and summary error     ${leadOrganisationsValidationErrorMessage}

Comp admin sets international organisations can not lead the competition
     [Documentation]   IFS-7246
     When the user selects the radio button                                                                    leadInternationalOrganisationsApplicable  false
     And The user should not see a field and summary error                                                     ${leadOrganisationsValidationErrorMessage}
     And the user clicks the button/link                                                                       jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility and lead organisation response question and answers     No

Comp admin sets international organisations can lead the competition
     [Documentation]   IFS-7246
     Given the user clicks the button/link                                                                     jQuery = button:contains("Edit")
     And the user clicks the button/link                                                                       jQuery = button:contains("Save and continue")
     When the user selects the radio button                                                                    leadInternationalOrganisationsApplicable  true
     And the user clicks the button/link                                                                       jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility and lead organisation response question and answers     Yes

Comp admin creates international organisation eligibility competition
     [Documentation]  IFS-7195
     Given the user clicks the button/link     link = Return to setup overview
     When the user clicks the button/link      jQuery = a:contains("Complete")
     And the user clicks the button/link       jQuery = button:contains('Done')
     And the user navigates to the page        ${CA_UpcomingComp}
     Then the user should see the element      jQuery = h2:contains("Ready to open") ~ ul a:contains("${internationalLeadInternationalCompetition}")

Comp admin sets lead organisations can lead international competitions and sets competition to live
     [Documentation]  IFS-7195
     Given Get competition id and set open date to yesterday     ${internationalLeadInternationalCompetition}
     When the user navigates to the page                         ${CA_Live}
     Then the user should see the element                        jQuery = h2:contains('Open') ~ ul a:contains('${internationalLeadInternationalCompetition}')

Comp admin sets lead organisations can not lead international competitions and sets competition to live
     [Documentation]  IFS-7246
     Given the user navigates to the page                                                  ${CA_UpcomingComp}
     When comp admin sets lead organisation can not lead the international competition
     And Get competition id and set open date to yesterday                                 ${ukLeadInternationalCompetition}
     Then the user navigates to the page                                                   ${CA_Live}
     And the user should see the element                                                   jQuery = h2:contains('Open') ~ ul a:contains('${ukLeadInternationalCompetition}')

Non registered UK based users apply for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given Logout as user
    And the user select the competition and starts application      ${ukLeadInternationalCompetition}
    When non-registered user selects business options                 isNotInternational
    Then UK-based user sees these page elements

Non registered UK based users confirm their organisation details and create an account
    [Documentation]    IFS-7199
    [Tags]  HappyPath
    Given the user provides uk based organisation details             Nomensa  NOMENSA LTD
    And the user verifies their organisation details
    When the user clicks the button/link                              name = save-organisation
    And the user enters the details and clicks the create account     Tony  Blair  ${uk_based_applicant_new}  ${short_password}
    Then the user should not see an error in the page

Non registered international users apply for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${internationalLeadInternationalCompetition}
    When non-registered user selects business options                 isInternational
    Then international user sees these page elements

Non registered international users can create an account and provide international organisation details
    [Documentation]    IFS-7198  IFS-7199
    [Tags]  HappyPath
    Given the user provides international organisation details         564789  London  cana  Canada  International_Ltd.  international-organisation-details-cta
    When the user verifies their organisation details
    And the user clicks the button/link                                id = international-confirm-organisation-cta
    Then the user enters the details and clicks the create account     Roselin  Messy  ${lead_intl_email_two}  ${short_password}
    And the user should not see an error in the page

Registered users applying for an international competition see no international organisation if there is none
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user sign in and apply for international comp                            ${lead_applicant}  ${short_password}  ${ukleadinternationalcompetition}
    And check if there is an existing application in progress for this competition
    When user selects where is organisation based                                      isInternational
    Then the user should not see the element                                           link = Apply with a different organisation

Registered users applying for an international competition see only UK based organisations if they are UK based
    [Documentation]    IFS-7252
    [Tags]  HappyPath
     Given the user clicks the button/link              link = Back to tell us where your organisation is based
     When user selects where is organisation based      isNotInternational
     Then the user should see the element               jQuery = dt:contains("Empire Ltd")
     And the user should see the element                link = Apply with a different organisation

Registered UK based user applies for International Competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user clicks the button/link                   link = Apply with a different organisation
    When the user selects organisation type as business
    And the user clicks the Not on companies house link
    Then the user verifies their organisation details
    And the user clicks the button/link                     name = save-organisation

Registered UK based lead user invites partner organisation(with registered email/user)
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user clicks the button/link     link = Application team
    When invite partner organisation          Test Empire  Daniel Tan  ${lead_intl_email_one}
    Then the user should see the element      jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element       jQuery = td:contains("Daniel Tan (pending for")

Partner organisation(with registered email/user) accepts the invite
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  Logout as user
    When the user reads his email and clicks the link    ${lead_intl_email_one}  Invitation to collaborate in ${ukLeadInternationalCompetition}  You will be joining as part of the organisation  2
    Then the user clicks the button/link                 link = Continue

Registered user(Partner organisation) logs in and select where their organisation is based
    [Documentation]    IFS-7197 IFS-7252
    [Tags]  HappyPath
    Given logging in and error checking               ${lead_intl_email_one}  ${short_password}
    When user selects where is organisation based     isInternational
    Then the user should not see the element          link = Join with a different organisation.
    And the user clicks the button/link               link = Back to tell us where your organisation is based
    When user selects where is organisation based     isNotInternational
    Then the user should see the element              jQuery = dt:contains("Golden Valley Research Ltd")

Partner user provides UK based organisation details and verifies them
    [Documentation]    IFS-7198 IFS-7199
    [Tags]  HappyPath
    Given the user clicks the button/link                    link = Join with a different organisation
    When the user provides uk based organisation details     Nomensa  NOMENSA LTD
    Then the user should see the element                     jQuery = p:contains("This is the organisation that you will join the application with.")
    And the user clicks the button/link                      name = save-organisation
    And the user should see the element                      jQuery = h2:contains("Application progress")

Registered UK based lead user invites partner organisation(with non-registered email/user)
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  logout as user
    Given Registered UK based lead user goes to the application team
    When invite partner organisation                                     New Empire  Tim Simpson  ${partner_international_email}
    Then the user should see the element                                 jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element                                  jQuery = td:contains("Tim Simpson (pending for")

Partner organisation(with non-registered email/user) accepts the invite
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  Logout as user
    When the user reads his email and clicks the link     ${partner_international_email}  Invitation to collaborate in ${ukLeadInternationalCompetition}  You will be joining as part of the organisation  2
    Then the user clicks the button/link                  jQuery = a:contains("Yes, accept invitation")

Non-Registered user(Partner organisation) provide international organisation details and verifies them
    [Documentation]    IFS-7199
    [Tags]  HappyPath
    Given user selects where is organisation based                isInternational
    When the user provides international organisation details     435353543  Helsinki  Finland  Finland  New Empire  international-organisation-details-cta
    Then the user should see the element                          jQuery = p:contains("This is the organisation that you will join the application with.")
    And the user clicks the button/link                           id = international-confirm-organisation-cta
    And The user should not see an error in the page

Non-Registered user(International partner organisation) create an account
    [Documentation]    IFS-7198 IFS-7199
    [Tags]  HappyPath
    When Partner user enters the details and clicks the create account     Tim  Simpson  ${short_password}
    And Verify international partner email address                         ${partner_international_email}
    Then The user should not see an error in the page
    [Teardown]  Logout as user

Registered International lead user applying for an international competition see only International organisations
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user sign in and apply for international comp                                 ${lead_international_email}  ${short_password}  ${internationalLeadInternationalCompetition}
    When check if there is an existing application in progress for this competition
    Then the user should see organisations list according to organisation type selected     Apply with a different organisation  jQuery = dt:contains("Empire (french)")

Registered International lead user applies for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user clicks the button/link                         link = Apply with a different organisation
    When the user provides international organisation details     343434435  Sydney  Australia  Australia  ${leadApplicantOrganisationName}  international-organisation-details-cta
    Then the user verifies their organisation details
    And the user clicks the button/link                           id = international-confirm-organisation-cta

Lead applicant can see organisation address details on the application team page
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    [Setup]  the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link       link = Untitled application (start here)
    And the user clicks the button/link         link = Application team
    Then the user should see the element        jQuery = td:contains("7 Pinchington Lane, Sydney, ")

Lead applicant can edit organisation address details on the application team page
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    When the user clicks the button/link                                     link = Edit
    Then the user should be able to edit the address and see the changes

Lead applicant completes application details
    When the user clicks the button/link         link = Application overview
    Then the user enters application details

Lead applicant applies again to international competition using the same international organisation and verifies organisation address
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given the user select the competition and starts application                       ${internationalLeadInternationalCompetition}
    And check if there is an existing application in progress for this competition
    When user selects where is organisation based                                      isInternational
    Then the user clicks the button/link                                               jQuery = span:contains("${leadApplicantOrganisationName}")
    And the user should be able to see the same address details

Lead applicant adds a team member
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    [Setup]  the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link       link = ${internationalApplicationTitle}
    And the user clicks the button/link         link = Application team
    When the user clicks the button/link        jQuery = button:contains("Add person to New Empire 1")
    And the user adds a new team member         MemberFName MemberSName  ${team_member}
    Then the user should see the element        jQuery = td:contains("MemberFName MemberSName (pending for 0 days)")
    [Teardown]  logout as user

Team member accepts the invite and can change lead organisation address details
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given team member accepts the invite to join organisation     ${team_member}  ${internationalLeadInternationalCompetition}  MemberFName  MemberSName
    When the user clicks the button/link                          link = Sign in
    And Logging in and Error Checking                             ${team_member}  ${correct_password}
    And the user clicks the button/link                           link = ${internationalApplicationTitle}
    Then the member can edit leads organisation address details

Lead applicant adds a UK based partner organisation
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given log in as a different user           ${lead_international_email}  ${short_password}
    Given the user clicks the button/link      link = ${internationalApplicationTitle}
    And the user clicks the button/link        link = Application team
    When the user clicks the button/link       link = Add a partner organisation
    And the user adds a new partner            Partner Ltd  FName SName  ${partner_org}
    Then the user should see the element       jQuery = td:contains("FName SName (pending for 0 days)")
    [Teardown]    Logout as user

UK based partner organisation accepts the invite to collaborate and cannot edit lead org address details
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given partner organisation accepts the invite to collaborate      ${partner_org}  ${internationalLeadInternationalCompetition}  ${BUSINESS_TYPE_ID}
    When the user clicks the button/link                              link = Application team
    Then the user should not see the element                          link = Edit
    And the user should see the element                               jQuery = td:contains("${newAddress}")

Internal user can see International Organisation Address Details in Application Overview
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    [Setup]  requesting Application ID for this Application
    Given log in as a different user                            &{internal_finance_credentials}
    When the user selects the application in progress
    And the user clicks the button/link                         jQuery = button:contains("Open all")
    Then the user should see the element                        jQuery = td:contains("Address")
    And the user should see the element                         jQuery = td:contains("7 Fisher House, Sydney, ")
    [Teardown]    Logout as user

Non-uk based lead project location validations in project finances
    [Documentation]    IFS - 7240
    [Setup]  Requesting new empire one organisation ID of this Project
    Given Logging in and Error Checking                                       ${lead_international_email}  ${short_password}
    And the user navigates to the page                                        ${server}/application/${ApplicationID}/form/your-project-location/organisation/${organisationTestEmpireOneID}/section/723
    When the user should see project location details in project finances
    And the user clicks the button/link                                       jQuery = button:contains("Mark as complete")
    Then the user should see a field and summary error                        ${projectLocationValidationErrorMessage}

Lead applicant is able to complete international application
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    Given the user navigates to the page         ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link          link = ${internationalApplicationTitle}
    When the user completes the application

UK based partner also completes funding info
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given log in as a different user               ${partner_org}  ${correct_password}
    And the user clicks the button/link            link = ${internationalApplicationTitle}
    When the user clicks the button/link           link = Your project finances
    Then partner marks the finance as complete     ${internationalApplicationTitle}   id = postcode   BS1 4NT

Lead applicant submits the application
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    Given log in as a different user                      ${lead_international_email}  ${short_password}
    And the user clicks the button/link                 link = ${internationalApplicationTitle}
    When the applicant submits the application
    Then the user should not see an error in the page

Moving International Competition to Project Setup
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    [Setup]  Requesting competition ID of this Project
    Given log in as a different user                       &{internal_finance_credentials}
    When moving competition to Closed                      ${internationalCompetitionId}
    And making the application a successful project        ${internationalCompetitionId}  ${internationalApplicationTitle}
    Then moving competition to Project Setup               ${internationalCompetitionId}

Internal user is able to add a new international partner organisation
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${InternationalCompetitionId}/status/all
    When the user clicks the button/link                   jQuery = tr:contains("${internationalApplicationTitle}") .waiting:nth-child(3)
    And the user clicks the button/link                    link = Add a partner organisation
    And the user adds a new partner organisation           INTERNATIONAL PARTNER ORGANISATION  FName Surname  ${international_invite_email}
    Then organisation is able to accept project invite     FName  Surname  ${international_invite_email}  ${ApplicationID}  ${internationalApplicationTitle}

Partner organisation provide organisation detail and create an account
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    When partner user provide organisation detail and create account     ${international_invite_email}
    And the user clicks the button/link                                  link = ${internationalApplicationTitle}
    Then partner completes all sections to join the project

Partner organisation is able to see organisation address details in project team
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    Given the user clicks the button/link                                                    link = Project team
    And the user completes project team and can see international organisation addresses
    Then the user clicks the button/link                                                     link = Return to setup your project
    And the user should see the element                                                      jQuery = p:contains("You must complete your project and bank details within 30 days of our notification to you.")

Lead organisation can see international organisation address details in project team and cannot edit it
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    Given log in as a different user                                                          ${lead_international_email}  ${short_password}
    And the user clicks the button/link                                                       link = ${internationalApplicationTitle}
    When the user clicks the button/link                                                      link = Project team
    Then the user completes project team and can see international organisation addresses

Partner organisation can see international organisation address details in project team and cannot edit it
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    Given log in as a different user                                                          ${partner_org}  ${correct_password}
    And the user clicks the button/link                                                       link = ${internationalApplicationTitle}
    And the user clicks the button/link                                                       link = Project team
    When the user completes project team and can see international organisation addresses
    Then the user clicks the button/link                                                      link = Return to setup your project
    [Teardown]  logout as user

Internal user can view address details
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    [Setup]  Requesting project ID of this Project
    Given logging in and error checking                &{internal_finance_credentials}
    When the user navigates to the page                ${server}/project-setup-management/competition/${internationalCompetitionId}/project/${ProjectID}/team
    Then the user should see the element               jQuery = td:contains("${addressLine1}")

External dashboard - hide the bank details if partner is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given the user logs-in in new browser        &{partnerApplicantCredentialsNonUKBased}
    When the user navigates to the page          ${server}/project-setup/project/${ProjectID}
    Then the user should not see the element     jQuery = h2:contains("Bank details")

External dashboard - non-uk based partner applicant can complete the project location details
    [Documentation]    IFS - 7240
    [Tags]
    [Setup]  Requesting test empire organisation ID of this Project
    Given the user navigates to the page                                             ${server}/project-setup/project/${ProjectID}/organisation/${organistaionTestEmpireID}/partner-project-location
    When the user should see project location details in project details section
    And the user enters text to a text field                                         id = town     delhi
    And the user clicks the button/link                                              jQuery = button:contains("Save project location")
    Then the user should see the element                                             jQuery = td:contains("Delhi")

External dashboard - hide the bank details if lead organisation is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given Log in as a different user             &{leadApplicantCredentials}
    When the user navigates to the page          ${server}/project-setup/project/${ProjectID}
    Then the user should not see the element     jQuery = h2:contains("Bank details")

Correspondence address field validations
    [Documentation]     IFS - 7241
    [Tags]
    Given the user navigates to the page                                   ${server}/project-setup/project/${ProjectID}/details
    when the user clicks the button/link                                   link = Correspondence address
    And the user check for correspondence address titles and info text
    And the user clicks the button/link                                    id = save-project-address-button
    Then the user should see field and summary validation messages

United kingdom should displaying in country list of correspondence address for non-uk based organisations
    [Documentation]     IFS - 7241
    [Tags]
    When enter the country in the autocomplete field     United King        United Kingdom
    Then the user sees the text in the text field        id = country       United Kingdom

non-uk based lead applicant can complete the correspondence address
    [Documentation]     IFS - 7241
    [Tags]
    When the user fills correspondence address data                                         Calle 11   No 1111    San Sebastian   Argentina      X5187XAB
    And the user clicks the button/link                                                     id = save-project-address-button
    Then the user should see read only view of completed correspondence address details

Non-uk based project location validations in project setup
    [Documentation]    IFS - 7240
    Given the user navigates to the page                                             ${server}/project-setup/project/${ProjectID}/organisation/${organisationTestEmpireOneID}/partner-project-location
    When the user should see project location details in project details section
    And The user clears text in the text field                                       id = town
    And the user clicks the button/link                                              jQuery = button:contains("Save project location")
    Then the user should see a field and summary error                               ${projectLocationValidationErrorMessage}

External dashboard - non-uk based lead applicant can complete the project location details
    [Documentation]    IFS - 7240
    [Tags]
    When the user enters text to a text field     id = town     mamungkukumpurangkuntjunya Hill
    And the user clicks the button/link           jQuery = button:contains("Save project location")
    Then the user should see the element          jQuery = td:contains("Mamungkukumpurangkuntjunya Hill")

non-uk based partner applicant can see the read only view of the corresponding address
    [Documentation]     IFS - 7241
    Given Log in as a different user                                                       &{partnerApplicantCredentialsNonUKBased}
    When the user navigates to the page                                                    ${server}/project-setup/project/${ProjectID}/details
    Then the user should see read only view of completed correspondence address details

External dashboard - lead applicant - view status of partners - bank details not required message should display for international lead applicant organisation
    [Documentation]    IFS - 7163
    [Tags]
    Given lead applicant invites new partner and accepts invitation
    And partner organisation sets funding level to zero
    When the user clicks the button/link                                                 link = View the status of partners
    Then bank details not required message should display for international lead applicant organisation

External dashboard - partner organisation - view status of partners - bank details not required message should display for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given Log in as a different user                                                                            &{partnerApplicantCredentialsNonUKBased}
    And The user navigates to the page                                                                          ${server}/project-setup/project/${ProjectID}
    When the user clicks the button/link                                                                        link = View the status of partners
    Then bank details not required message should display for non uk based and zero funding partner organisations

Project setup dashboard - Bank details - No action required should display for non uk based and zero funding organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given lead and partner applicants completes the project and bank details
    When Log in as a different user                                                            &{ifs_admin_user_credentials}
    And the user navigates to the page                                                         ${server}/project-setup-management/competition/${internationalCompetitionId}/status/all
    And the user clicks the button/link                                                        jQuery = td:nth-child(6) a:contains("Review")
    Then No action required should display for non uk based and zero funding organisations

Project setup dashboard - will not prevent the consortium's bank details from approval
    [Documentation]    IFS - 7163
    [Tags]
    When Comp admin approves bank details of partner organisation     ${partnerOrganisationNameUKBased}
    Then the user should see the element                              jQuery = li:nth-child(4) span:nth-child(1)

Non-uk based organisations project location details updated in ifs admin project setup view
    [Documentation]     IFS-7240
    Given the user navigates to the page     ${server}/project-setup-management/competition/${internationalCompetitionId}/status/all
    When the user clicks the button/link     jQuery = td:nth-child(2) a:contains("Complete")
    Then the user should see the element     jQuery = td:contains("${leadApplicantOrganisationName}") ~ td:contains("Mamungkukumpurangkuntjunya Hill")
    And the user should see the element      jQuery = td:contains("${partnerOrganisationNameNonUKBased}") ~ td:contains("Delhi")

comp admin can see the correspondence address entered by non uk based lead applicant in project setup dashboard
    [Documentation]     IFS - 7241
    When the user navigates to the page      ${server}/project-setup-management/competition/${internationalCompetitionId}/project/${ProjectID}/details
    Then the user should see the element     jQuery = td:contains("Calle 11, San Sebastian,")
    And the user should see the element      jQuery = td:contains("Argentina, X5187XAB")

Monitoring officer assign link should be displayed on completing correspondence address and project location
    When the user navigates to the page     ${server}/project-setup-management/competition/${competitionID}/status/all
    Then the user should see the element    link = Assign

Monitoring office can see the correspondence address entered by non uk based lead applicant in project setup dashboard
    [Documentation]     IFS - 7241
    Given ifs admin assigns MO to the competition in project setup      ${ApplicationID}   ${internationalApplicationTitle}
    When the user navigates to the page                                 ${server}/project-setup-management/project/${ProjectID}/monitoring-officer
    Then the user should see the element                                jQuery = p:contains("Argentina")

Uk based lead applicant moves application to project setup and generates GOL
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    When uk lead applicant completes application form
    And international partner submits finance details
    Then Uk lead submits international competition application to assesment
    And Uk lead completes project setup details and generated GOL

GOL template to be updated with country for correspondents address
    [Documentation]     IFS - 7241
    Given the user complete all sections of the project setup and generates GOL
    When the user navigates to the page                                            ${server}/project-setup-management/project/${ProjectID}/grant-offer-letter/template
    Then element should contain                                                    xpath = //p[1]     Argentina

*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser      &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

the user checks for organisational eligibility fields
    the user should see the element           jQuery = h1:contains("${organisationalEligibilityTitle}")
    the user should see the element           id = internationalOrganisationsApplicable
    the user should see the element           jQuery = span:contains("${organisationalEligibilityInfo}")
    the user should see the element           css = [for="comp-internationalOrganisationsApplicable-yes"]
    the user should see the element           css = [for="comp-internationalOrganisationsApplicable-no"]
    the user should see the element           jQuery = button:contains("Save and continue")
    the user should see the element           jQuery = span:contains("${projectEligibilityLink}")
    the user should see the element           link = Competition setup
    the user should see the element           link = Return to setup overview

the user checks for lead organisations fields
    the user should see the element           jQuery = h1:contains("${leadOrganisationsTitle}")
    the user should see the element           id = leadInternationalOrganisationsApplicable
    the user should see the element           css = [for="comp-leadInternationalOrganisationsApplicable-yes"]
    the user should see the element           css = [for="comp-leadInternationalOrganisationsApplicable-no"]
    the user should see the element           jQuery = button:contains("Save and continue")
    the user should see the element           jQuery = span:contains("${organisationalEligibilityTitle}")
    the user should see the element           link = Back to organisational eligibility
    the user should see the element           link = Return to setup overview

the user completes all categories except organisational eligibility category
    [Arguments]    ${orgType}  ${extraKeyword}  ${compType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}             ${researchParticipation}    ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS Milestones                     ${completionStage}     ${month}                    ${nextyear}
    the user marks the Application as done                  ${projectGrowth}       ${compType}
    the user fills in the CS Assessors
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview

comp admin can view organisation eligibility and lead organisation response question and answers
    [Arguments]     ${option}
    the user should see the element       jQuery = dt:contains("${organisationalEligibilitySubTitle}") ~ dd:contains("Yes")
    the user should see the element       jQuery = dt:contains("${leadOrganisationsSubTitle}") ~ dd:contains("${option}")
    the user should see the element       jQuery = button:contains("Edit")

comp admin can view organisation eligibility response question and answer
    the user should see the element         jQuery = dd:contains("No")
    the user should see the element         jQuery = button:contains("Edit")
    the user should not see the element     jQuery = h1:contains("${leadOrganisationsTitle}")

comp admin sets lead organisation can not lead the international competition
     the user clicks the button/link                                                   jQuery = .govuk-button:contains("Create competition")
     the user fills in the CS Initial details                                          ${ukLeadInternationalCompetition}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     the user selects the organisational eligibility                                   true    false
     the user completes all categories except organisational eligibility category      ${business_type_id}  KTP  ${compType_Programme}  project-setup-completion-stage  yes  1  true  collaborative
     the user clicks the button/link                                                   jQuery = a:contains("Complete")
     the user clicks the button/link                                                   jQuery = button:contains('Done')

UK-based user sees these page elements
    the user should see the element         jQuery = p:contains("${chooseYourOragnisationTypeInfoText}")
    the user should see the element         jQuery = span:contains("${researchOrganisationInfoText}")
    the user should not see the element     jQuery = p:contains("${ukBasedOrganisationFundingInfo}")

international user sees these page elements
    the user should see the element         jQuery = p:contains("${chooseYourOragnisationTypeInfoText}")
    the user should not see the element     jQuery = span:contains("${researchOrganisationInfoText}")
    the user should not see the element     jQuery = p:contains("${ukBasedOrganisationFundingInfo}")

the user verifies their organisation details
    the user should see the element         jQuery = p:contains("This organisation will lead the application.")
    the user should not see the element     jQuery = p:contains("${ukBasedOrganisationFundingInfo}")
    the user should see the element         jQuery = dt:contains("${organisationBasedInUkTitle}")

the user sign in and apply for international comp
    [Arguments]  ${user}  ${password}   ${competitionName}
    the user select the competition and starts application      ${competitionName}
    the user clicks the button/link                             jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    Logging in and Error Checking                               ${user}  ${password}

the user selects organisation type as business
    the user selects the radio button     organisationTypeId  radio-1
    the user clicks the button/link       name = select-company-type

organisation is able to accept project invite
    [Arguments]  ${fname}  ${sname}  ${email}  ${applicationID}  ${appTitle}
    logout as user
    the user reads his email and clicks the link     ${email}  Invitation to join project ${applicationID}: ${appTitle}  You have been invited to join the project ${appTitle}

partner user provide organisation detail and create account
    [Arguments]  ${email}
    the user clicks the button/link                                   jQuery = .govuk-button:contains("Yes, create an account")
    user selects where is organisation based                          isInternational
    the user provides international organisation details              435445543  Sydney  Australia  Australia  Test Empire  international-organisation-details-cta
    the user should see the element                                   jQuery = p:contains("This is the organisation that you will join the project with.")
    the user clicks the button/link                                   id = international-confirm-organisation-cta
    partner user enters the details and clicks the create account     Tester  Simpson  ${short_password}
    the user should see the element                                   jQuery = h1:contains("Please verify your email address")
    the user reads his email and clicks the link                      ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page                 ${REGISTRATION_VERIFIED}
    the user clicks the button/link                                   link = Sign in
    Logging in and Error Checking                                     ${email}  ${short_password}

Partner user enters the details and clicks the create account
    [Arguments]   ${first_name}  ${last_name}  ${password}
    Wait Until Page Contains Element Without Screenshots    jQuery = a:contains("Terms and conditions")
    the user enters text to a text field                    id = firstName  ${first_name}
    the user enters text to a text field                    id = lastName  ${last_name}
    the user enters text to a text field                    id = phoneNumber  234324234
    Input Password                                          id = password  ${password}
    the user selects the checkbox                           termsAndConditions
    the user selects the checkbox                           allowMarketingEmails
    the user clicks the button/link                         name = create-account

Verify international partner email address
    [Arguments]   ${email}
    the user should see the element                       jQuery = h1:contains("Please verify your email address")
    the user reads his email and clicks the link          ${email}  Please verify your email address  Once verified you can sign into your account.
    the user should be redirected to the correct page     ${REGISTRATION_VERIFIED}
    the user clicks the button/link                       link = Sign in
    Logging in and Error Checking                         ${email}  ${short_password}

invite partner organisation
    [Arguments]  ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          link = Add a partner organisation
    the user adds a partner organisation     ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          jQuery = button:contains("Invite partner organisation")

Registered UK based lead user goes to the application team
    Logging in and Error Checking       ${lead_applicant}  ${short_password}
    the user clicks the button/link     jQuery = li:contains("${ukLeadInternationalCompetition}") a:contains("Untitled")
    the user clicks the button/link     link = Application team

the user should see organisations list according to organisation type selected
    [Arguments]  ${arg}  ${locator}
    user selects where is organisation based     isNotInternational
    the user should not see the element          link = ${arg}
    the user clicks the button/link              link = Back to tell us where your organisation is based
    user selects where is organisation based     isInternational
    the user should see the element              ${locator}

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta

apply for comp with a different organisation
    [Arguments]  ${check_international}
    check if there is an existing application in progress for this competition
    user selects where is organisation based                                       ${check_international}
    the user clicks the button/link                                                link = Apply with a different organisation

non-registered user selects business options
    [Arguments]  ${isBusinessInternational}
    the user clicks the button/link              link = Continue and create an account
    the user should see the element              jQuery = span:contains("${organisationBasedInUkTitle}")
    the user should not see the element          jQuery = span:contains("Create an account")
    user selects where is organisation based     ${isBusinessInternational}

the user provides international organisation details
    [Arguments]  ${company_reg_no}  ${international_org_town}  ${international_org_country}  ${international_org_country_complete}  ${international_org_name}  ${button_id}
    the user selects organisation type as business
    the user enters text to a text field                               id = name  ${international_org_name}
    the user gets an error message on not filling mandatory fields     ${button_id}
    the user enters text to a text field                               id = companyRegistrationNumber  ${company_reg_no}
    the user enters text to a text field                               id = addressLine1  ${internationalOrganisationFirstLineAddress}
    the user enters text to a text field                               id = town  ${international_org_town}
    input text                                                         id = country  ${international_org_country}
    the user clicks the button/link                                    jQuery = ul li:contains("${international_org_country_complete}")
    the user clicks the button/link                                    id = ${button_id}

the user provides uk based organisation details
    [Arguments]  ${org_search_name}  ${org}
    the user selects organisation type as business
    the user enters text to a text field               name = organisationSearchName  ${org_search_name}
    the user clicks the button/link                    name = search-organisation
    the user clicks the button/link                    link = ${org}

the user gets an error message on not filling mandatory fields
    [Arguments]  ${button_id}
    the user clicks the button/link     id = ${button_id}
    the user should see the element     jQuery = h2:contains("There is a problem")
    the user should see the element     link = You must enter your organisation's street address.
    the user should see the element     link = You must enter your organisation's town or city.
    the user should see the element     link = You must select the country where your organisation is based.

The user completes the application
    the applicant completes Application Team
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                                       ${internationalApplicationTitle}
    lead marks the finance as complete                                             ${internationalApplicationTitle}   Calculate  52,214    id = town       Sydney
    the user accept the competition terms and conditions                           Return to application overview

lead marks the finance as complete
    [Arguments]  ${application}  ${overheadsCost}  ${totalCosts}   ${townOrPostcodeLocator}    ${townOrPostcodeValue}
    the user fills in the project costs                                   ${overheadsCost}  ${totalCosts}
    the user enters the project location                                  ${townOrPostcodeLocator}    ${townOrPostcodeValue}
    the user fills the organisation details with Project growth table     ${application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                                  ${application}
    the user should see all finance subsections complete
    the user clicks the button/link                                       link = Back to application overview
    the user should see the element                                       jQuery = li:contains("Your project finances") > .task-status-complete

partner marks the finance as complete
    [Arguments]  ${application}      ${townOrPostcodeLocator}    ${townOrPostcodeValue}
    the user clicks the button/link                                       link = Your project costs
    the user fills in Labour
    the user clicks the button/link                                       css = label[for="stateAidAgreed"]
    the user clicks the button/link                                       jQuery = button:contains("Mark as complete")
    the user enters the project location                                  ${townOrPostcodeLocator}    ${townOrPostcodeValue}
    the user fills the organisation details with Project growth table     ${application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                                  ${application}
    the user should see all finance subsections complete
    the user clicks the button/link                                       link = Back to application overview
    the user accept the competition terms and conditions                  Return to application overview

the user enters the project location
    [Arguments]  ${locator}  ${location}
    the user clicks the button/link         link = Your project location
    the user enters text to a text field    ${locator}  ${location}
    the user clicks the button/link         jQuery = button:contains("Mark as complete")

the user should be able to edit the address and see the changes
    the user enters text to a text field     id = addressLine1  77 First Line
    the user enters text to a text field     id = zipCode  RG14 7AU
    the user clicks the button/link          id = update-organisation-address
    the user should see the element          jQuery = td:contains("77 First Line")

the user should be able to see the same address details
    the user clicks the button/link     id = save-organisation-button
    the user clicks the button/link     link = Application team
    the user should see the element     jQuery = td:contains("77 First Line")

the user enters application details
    the user clicks the button/link               link = Application details
    the user fills in the Application details     ${internationalApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}

the user adds a new partner
    [Arguments]   ${partnerOrgName}  ${persFullName}  ${email}
    the user enters text to a text field     id = organisationName  ${partnerOrgName}
    the user enters text to a text field     id = name  ${persFullName}
    the user enters text to a text field     id = email  ${email}
    the user clicks the button/link          jQuery = .govuk-button:contains("Invite partner organisation")
    the user should see the element          jQuery = h2:contains(${partnerOrgName})

the member can edit leads organisation address details
    the user clicks the button/link          link = Application team
    the user clicks the button/link          link = Edit
    the user enters text to a text field     id = addressLine1  ${newAddress}
    the user clicks the button/link          id = update-organisation-address
    the user should see the element          jQuery = td:contains("${newAddress}")

partner organisation accepts the invite to collaborate
    [Arguments]  ${email}  ${compName}  ${businessTypeId}
    the user reads his email and clicks the link     ${email}  Invitation to collaborate in ${compName}  You are invited by  2
    The user clicks the button/link                  jQuery = a:contains("Yes, accept invitation")
    user selects where is organisation based         isNotInternational
    The user should see the element                  jQuery = h1:contains("Choose your organisation type")
    The user completes the new account creation      ${email}  ${businessTypeId}
    The user clicks the button/link                  link = ${internationalApplicationTitle}
    The user should not see an error in the page

the user selects the application in progress
    the user clicks the button/link     link = ${internationalLeadInternationalCompetition}
    the user clicks the button/link     link = Applications: All, submitted, ineligible
    the user clicks the button/link     link = All applications
    the user clicks the button/link     link = ${ApplicationID}

requesting Application ID for this Application
    ${ApplicationID} =  get application id by name     ${internationalApplicationTitle}
    Set suite variable      ${ApplicationID}

Requesting competition ID of this Project
    ${internationalCompetitionId} =  get comp id from comp title    ${internationalLeadInternationalCompetition}
    Set suite variable      ${internationalCompetitionId}

Requesting project ID of this Project
    ${ProjectID} =  get project id by name     ${internationalApplicationTitle}
    Set suite variable     ${ProjectID}
Requesting innovate uk organisation ID of this Project
    ${organistaionInnovateID} =  get organisation id by name     ${partnerOrganisationNameUKBased}
    Set suite variable     ${organistaionInnovateID}

Requesting test empire organisation ID of this Project
    ${organistaionTestEmpireID} =  get organisation id by name     ${partnerOrganisationNameNonUKBased}
    Set suite variable     ${organistaionTestEmpireID}

Requesting new empire one organisation ID of this Project
    ${organisationTestEmpireOneID} =  get organisation id by name     ${leadApplicantOrganisationName}
    Set suite variable     ${organisationTestEmpireOneID}

Lead applicant submits project team details
    [Arguments]     ${email}  ${password}  ${ProjectID}
    Log in as a different user                  ${email}  ${password}
    the user navigates to the page              ${server}/project-setup/project/${ProjectID}/team/project-manager
    the user selects the radio button           projectManager  projectManager1
    the user clicks the button/link             id = save-project-manager-button
    the user navigates to the page              ${server}/project-setup/project/${ProjectID}/team
    The user selects their finance contact      financeContact1
    the user clicks the button/link             link = Set up your project

zero funding parter submits the project and team details
    Log in as a different user                &{partnerApplicantCredentialsZeroFunding}
    the user navigates to the page            ${server}/project-setup/project/${ProjectID}/details
    the user clicks the button/link           link = Edit
    the user enters text to a text field      id = postcode      P05T C0D3
    the user clicks the button/link           jQuery = button:contains("Save project location")
    the user clicks the button/link           id = return-to-set-up-your-project-button
    the user clicks the button/link           link = Project team
    the user clicks the button/link           link = Your finance contact
    the user selects the radio button         financeContact  financeContact1
    the user clicks the button/link           id = save-finance-contact-button

partner submits the project and team details
    [Arguments]     ${email}  ${password}  ${ProjectID}
    Log in as a different user              ${email}  ${password}
    the user navigates to the page          ${server}/project-setup/project/${ProjectID}
    the user clicks the button/link         link = Project team
    the user clicks the button/link         link = Your finance contact
    the user selects the radio button       financeContact  financeContact1
    the user clicks the button/link         id = save-finance-contact-button

lead applicant invites new partner and accepts invitation
    Log in as a different user                                              &{ifs_admin_user_credentials}
    the user navigates to the page                                          ${server}/project-setup-management/competition/${internationalCompetitionId}/project/${ProjectID}/team/partner
    the user adds a new partner organisation                                university    jsonsmith    ${zero_funding_partner_email}
    a new organisation is able to accept project invite in project setup    json  smith   ${zero_funding_partner_email}  UNIVERSITY   ${zeroFundingPartnerOrgnaisationName}    ${ApplicationID}    ${internationalApplicationTitle}   isNotInternational

partner organisation sets funding level to zero
    The new partner can complete Your organisation
    the user clicks the button/link                          link = Your funding
    the user selects the radio button                        requestingFunding   false
    the user selects the radio button                        otherFunding  false
    the user clicks the button/link                          jQuery = button:contains("Mark as complete")
    the user accept the competition terms and conditions     Return to join project
    the user clicks the button/link                          id = submit-join-project-button

partner completes all sections to join the project
    The new partner can complete Your organisation
    the user clicks the button/link                          link = Your funding
    the user selects the radio button                        requestingFunding   true
    the user enters text to a text field                     css = [name^="grantClaimPercentage"]  35
    the user selects the radio button                        otherFunding  false
    the user clicks the button/link                          jQuery = button:contains("Mark as complete")
    the user accept the competition terms and conditions     Return to join project
    the user clicks the button/link                          id = submit-join-project-button

Comp admin approves bank details of partner organisation
    [Arguments]     ${organisationName}
    the user clicks the button/link     link = ${organisationName}
    the user clicks the button/link     jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link     jQuery = .govuk-button:contains("Approve account")
    the user clicks the button/link     link = Bank details

the user should see project location details in project details section
    the user should see the element     css = [for ="town"]
    the user should see the element     jQuery = span:contains("${projectLocationInfo}")
    the user should see the element     id = town
    the user should see the element     jQuery = button:contains("Save project location")

the user should see project location details in project finances
    the user should see the element     css = [for ="town"]
    the user should see the element     jQuery = span:contains("${projectLocationInfo}")
    the user should see the element     id = town
    the user should see the element     jQuery = button:contains("Save and return to project finances")

No action required should display for non uk based and zero funding organisations
    the user should see the element     css = li.read-only:nth-child(1) div.task-status > span:nth-child(1)
    the user should see the element     css = li:nth-child(2) strong
    the user should see the element     css = li.read-only:nth-child(3) div.task-status > span:nth-child(1)
    the user should see the element     css = li.read-only:nth-child(4) div.task-status > span:nth-child(1)

lead and partner applicants completes the project and bank details
    Lead applicant submits project team details                 ${lead_international_email}   ${short_password}     ${ProjectID}
    zero funding parter submits the project and team details
    partner submits the project and team details                ${international_invite_email}  ${short_password}  ${ProjectID}
    partner submits the project and team details                ${partner_org}   ${correct_password}   ${ProjectID}
    the user clicks the button/link                             link = Return to setup your project
    the user enters bank details

bank details not required message should display for non uk based and zero funding partner organisations
    the user should see the element         jQuery = th:contains("${zeroFundingPartnerOrgnaisationName}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element         jQuery = th:contains("${partnerOrganisationNameNonUKBased}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element         jQuery = th:contains("${leadApplicantOrganisationName}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should not see the element     jQuery = th:contains("${partnerOrganisationNameUKBased}") ~ td:nth-child(6) span:contains("Not required for this partner")

bank details not required message should display for international lead applicant organisation
    the user should see the element         jQuery = th:contains("${zeroFundingPartnerOrgnaisationName}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element         jQuery = th:contains("${partnerOrganisationNameNonUKBased}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element         jQuery = th:contains("${leadApplicantOrganisationName}") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should not see the element     jQuery = th:contains("${partnerOrganisationNameUKBased}") ~ td:nth-child(6) span:contains("Not required for this partner")

the user check for correspondence address titles and info text
    the user should see the element     jQuery = h1:contains("${correspondenceAddressTitle}")
    the user should see the element     jQuery = p:contains("${subTitleForCorrespondenceAddress}")
    the user should see the element     link = Project details
    the user should see the element     jQuery = span:contains("Start typing your country's name and select it from the list.")

the user fills correspondence address data
    [Arguments]     ${addresLine1}  ${addresLine2}  ${town}  ${country}  ${zipCode}
    the user enters text to a text field            id = addressLine1       ${addresLine1}
    the user enters text to a text field            id = addressLine2       ${addresLine2}
    the user enters text to a text field            id = town               ${town}
    enter the country in the autocomplete field     Argent                  ${country}
    the user enters text to a text field            id = zipCode            ${zipCode}

the user should see read only view of completed correspondence address details
    the user should see the element     jQuery = td:contains("Calle 11, San Sebastian,")
    the user should see the element     jQuery = td:contains("Argentina, X5187XAB")
    the user should see the element     id = project-address-status

the user should see field and summary validation messages
    the user should see a field and summary error   ${countryValidationMessage}
    the user should see a field and summary error   ${townOrCityValidationMessage}
    the user should see a field and summary error   ${streetValidationMessage}

ifs admin assigns MO to the competition in project setup
    [Arguments]     ${ApplicationID}   ${applicationTitle}
    the user navigates to the page             ${server}/management/dashboard/project-setup
    the user clicks the button/link            link = Assign monitoring officers
    Search for MO                              Orvill  Orville Gibbs
    The internal user assign project to MO     ${ApplicationID}   ${applicationTitle}

project finance approves Eligibility for Innovate uk organisation
    [Arguments]         ${organisationID}
    the user navigates to the page           ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${organisationID}/eligibility
    the user approves project costs

partner submits the spend profile
    [Arguments]     ${ProjectID}   ${organistaionInnovateID}
    log in as a different user          ${partner_org}   ${correct_password}
    the user navigates to the page      ${server}/project-setup/project/${ProjectID}/partner-organisation/${organistaionInnovateID}/spend-profile
    the user clicks the button/link     link = Submit to lead partner
    the user clicks the button/link     jQuery = button.govuk-button:contains("Submit")

external partner organisation submit the spend profile
    [Arguments]     ${ProjectID}   ${organistaionTestEmpireID}   ${organisationUiveristyOfLiverPoolId}
    Login and submit partners spend profile     ${international_invite_email}      ${organistaionTestEmpireID}            ${ProjectID}
    Login and submit partners spend profile     ${zero_funding_partner_email}      ${organisationUiveristyOfLiverPoolId}  ${ProjectID}

lead organisations submit the spend profile
    [Arguments]     ${ProjectID}  ${organisationID}  ${email}  ${password}
    log in as a different user          ${email}   ${password}
    the user navigates to the page      ${server}/project-setup/project/${ProjectID}/partner-organisation/${organisationID}/spend-profile/review
    the user clicks the button/link     name = mark-as-complete
    the user clicks the button/link     link = Review and submit project spend profile
    the user clicks the button/link     link = Submit project spend profile
    the user clicks the button/link     id = submit-send-all-spend-profiles

project manager submits documents
    [Arguments]     ${email}  ${password}   ${ProjectID}
    log in as a different user          ${email}  ${password}
    the user navigates to the page      ${server}/project-setup/project/${ProjectID}/document/all
    the user clicks the button/link     link = Collaboration agreement
    the user uploads the file           css = .inputfile    ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm
    the user clicks the button/link     link = Return to documents
    the user clicks the button/link     link = Exploitation plan
    the user uploads the file           css = .inputfile    ${valid_pdf}
    the user clicks the button/link     id = submit-document-button
    the user clicks the button/link     id = submitDocumentButtonConfirm

the user complete all sections of the project setup and generates GOL
    ${organisationUiveristyOfLiverPoolId} =  get organisation id by name     ${zeroFundingPartnerOrgnaisationName}
    Requesting test empire organisation ID of this Project
    Requesting innovate uk organisation ID of this Project
    project manager submits documents                                   ${lead_international_email}   ${short_password}   ${ProjectID}
    project finance approves both documents                             ${ProjectID}
    project finance approves Viability for                              ${organisationTestEmpireOneID}        ${ProjectID}
    project finance approves Viability for                              ${organisationUiveristyOfLiverPoolId}    ${ProjectID}
    project finance approves Viability for                              ${organistaionTestEmpireID}       ${ProjectID}
    project finance approves Viability for                              ${organistaionInnovateID}       ${ProjectID}
    project finance approves Eligibility                                ${organisationTestEmpireOneID}        ${organistaionTestEmpireID}    ${organistaionInnovateID}   ${ProjectID}
    project finance approves Eligibility for Innovate uk organisation   ${organisationUiveristyOfLiverPoolId}
    the user clicks the button/link                                     link = Return to finance checks
    the user clicks the button/link                                     css = .generate-spend-profile-main-button
    the user clicks the button/link                                     css = #generate-spend-profile-modal-button
    partner submits the spend profile                                   ${ProjectID}   ${organistaionInnovateID}
    external partner organisation submit the spend profile              ${ProjectID}   ${organistaionTestEmpireID}  ${organisationUiveristyOfLiverPoolId}
    lead organisations submit the spend profile                         ${ProjectID}   ${organisationTestEmpireOneID}   ${lead_international_email}     ${short_password}
    proj finance approves the spend profiles                            ${ProjectID}

a new organisation is able to accept project invite in project setup
    [Arguments]  ${fname}  ${sname}  ${email}  ${orgId}  ${orgName}  ${applicationID}  ${appTitle}  ${organisationBase}
    logout as user
    the user reads his email and clicks the link                                                  ${email}  Invitation to join project ${applicationID}: ${appTitle}  You have been invited to join the project ${appTitle}
    The user accepts invitation and selects organisation type and where is organisation based     ${orgId}  ${orgName}  ${organisationBase}
    the user fills in account details                                                             ${fname}  ${sname}
    the user clicks the button/link                                                               jQuery = button:contains("Create account")
    the user verifies their account                                                               ${email}
    a new organisation logs in and sees the project                                               ${email}
    the user should see the element                                                               jQuery = ul:contains("${appTitle}") .status:contains("Ready to join project")
    the user clicks the button/link                                                               link = ${appTitle}
    the user should see the element                                                               jQuery = h1:contains("Join project")

The user accepts invitation and selects organisation type and where is organisation based
    [Arguments]   ${orgId}  ${orgName}  ${organisationBase}
    the user clicks the button/link                       jQuery = .govuk-button:contains("Yes, create an account")
    user selects where is organisation based              ${organisationBase}
    the user selects the radio button                     organisationTypeId    1
    the user clicks the button/link                       jQuery = .govuk-button:contains("Save and continue")
    the user selects his organisation in Companies House  ${orgId}  ${orgName}

Requesting uk lead international project ID
    ${ukLeadApplicationProjectID} =   get project id by name     ${ukLeadInternationalApplicationTitle}
    Set suite variable      ${ukLeadApplicationProjectID}

Requesting uk lead international application ID
    ${ukLeadApplicationID} =  get application id by name     ${ukLeadInternationalApplicationTitle}
    Set suite variable      ${ukLeadApplicationID}

Requesting uk lead international competition ID
    ${ukLeadinternationalCompetitionId} =  get comp id from comp title    ${ukLeadInternationalCompetition}
    Set suite variable      ${ukLeadinternationalCompetitionId}

Requesting uk lead international organisation IDs
    ${organistaionOrg2} =    get organisation id by name           ${ukLeadOrganisationName}
    Set suite variable      ${organistaionOrg2}
    ${organistaionNewEmpireID} =  get organisation id by name           ${internationalPartnerOrganisation}
    Set suite variable      ${organistaionNewEmpireID}

uk lead applicant completes application form
    log in as a different user                               &{ukLeadOrganisationCredentials}
    the user navigates to the page                           ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link                          link = Untitled application (start here)
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${ukLeadInternationalApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the user clicks the button/link                          link = Application team
    the user clicks the button/link                          id = remove-organisation-129
    the user clicks the button/link                          name = remove-team-member
    the user clicks the button/link                          id = application-question-complete
    the user clicks the button/link                          link = Return to application overview
    the user should see the element                          jQuery = li:contains("Application team") > .task-status-complete
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                 ${ukLeadInternationalApplicationTitle}
    lead marks the finance as complete                       ${ukLeadInternationalApplicationTitle}   Calculate  52,214  id = postcode   BS1 4NT
    the user accept the competition terms and conditions     Return to application overview

international partner submits finance details
    log in as a different user                               &{internationalPartnerOrganisationCredentials}
    the user clicks the button/link                          link = ${ukLeadInternationalApplicationTitle}
    the user clicks the button/link                          link = Your project finances
    partner marks the finance as complete                    ${ukLeadInternationalApplicationTitle}  id = town   Chennai

Uk lead submits international competition application to assesment
    Requesting uk lead international competition ID
    Log in as a different user                            &{ukLeadOrganisationCredentials}
    the user clicks the button/link                       link = ${ukLeadInternationalApplicationTitle}
    the applicant submits the application
    Log in as a different user                            &{internal_finance_credentials}
    moving competition to Closed                          ${ukLeadinternationalCompetitionId}
    making the application a successful project           ${ukLeadinternationalCompetitionId}  ${ukLeadInternationalApplicationTitle}
    moving competition to Project Setup                   ${ukLeadinternationalCompetitionId}

project finance approves Eligibility for uk based lead and international partner
    [Arguments]  ${lead}  ${partner}  ${project}
    the user navigates to the page      ${server}/project-setup-management/project/${project}/finance-check/organisation/${lead}/eligibility
    the user approves project costs
    the user navigates to the page      ${server}/project-setup-management/project/${project}/finance-check/organisation/${partner}/eligibility
    the user approves project costs

ifs finance user approves bank details of uk based partner
    the user navigates to the page                              ${server}/project-setup-management/competition/${ukLeadinternationalCompetitionId}/status/all
    the user clicks the button/link                             jQuery = td:nth-child(6) a:contains("Review")
    Comp admin approves bank details of partner organisation    ${ukLeadOrganisationName}

UK lead applicant enters correspondence address
    the user navigates to the page                ${server}/project-setup/project/${ukLeadApplicationProjectID}/details
    the user clicks the button/link               link = Correspondence address
    the user enter the Correspondence address

Uk lead completes project setup details and generated GOL
    Requesting uk lead international project ID
    Requesting uk lead international application ID
    Requesting uk lead international organisation IDs
    lead applicant submits project team details                                         ${lead_applicant}    ${short_password}  ${ukLeadApplicationProjectID}
    the user enters bank details
    UK lead applicant enters correspondence address
    partner submits the project and team details                                        ${partner_international_email}   ${short_password}  ${ukLeadApplicationProjectID}
    project manager submits documents                                                   ${lead_applicant}    ${short_password}  ${ukLeadApplicationProjectID}
    project finance approves both documents                                             ${ukLeadApplicationProjectID}
    ifs finance user approves bank details of uk based partner
    ifs admin assigns MO to the competition in project setup                            ${ukLeadApplicationID}   ${ukLeadInternationalApplicationTitle}
    project finance approves Viability for                                              ${organistaionOrg2}        ${ukLeadApplicationProjectID}
    project finance approves Viability for                                              ${organistaionNewEmpireID}    ${ukLeadApplicationProjectID}
    project finance approves Eligibility for uk based lead and international partner    ${organistaionOrg2}   ${organistaionNewEmpireID}   ${ukLeadApplicationProjectID}
    the user clicks the button/link                                                     link = Return to finance checks
    the user clicks the button/link                                                     css = .generate-spend-profile-main-button
    the user clicks the button/link                                                     css = #generate-spend-profile-modal-button
    Login and submit partners spend profile                                             ${partner_international_email}         ${organistaionNewEmpireID}     ${ukLeadApplicationProjectID}
    lead organisations submit the spend profile                                         ${ukLeadApplicationProjectID}   ${organistaionOrg2}   ${lead_applicant}      ${short_password}
    proj finance approves the spend profiles                                            ${ukLeadApplicationProjectID}

the user completes project team and can see international organisation addresses
    the user should not see the element     link = Edit
    the user should see the element         jQuery = h2:contains("${leadApplicantOrganisationName}")
    the user should see the element         jQuery = td:contains("${addressLine1}")