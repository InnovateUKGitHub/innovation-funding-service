*** Settings ***
Documentation     IFS-7197 As a non-UK based business I can apply for International Competition as a Lead Applicant..
...
...               IFS-7198 As a non-UK based business I can create a new account to apply to an International Competition..
...
...               IFS-7199 Read only page for organisation details should not have a banner mentioning only UK based organisations can apply for the International Competition..
...
...               IFS-7252 When an existing organisation is applying for an international competition, we need to return their organisations based on whether they are UK or International
Suite Setup       Custom Suite Setup
Suite Teardown    Custom Suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${internationalOrganisationFirstLineAddress}           7 Pinchington Lane
${internationalApplicationTitle}                       New Test Application for International Users
${internationalCompetitionTitle}                       International Competition
${internationalCompetitionId}                          ${competition_ids["${internationalCompetitionTitle}"]}
${addressLine1}                                        7 Fisher House, Sydney,
${newAddress}                                          7 Fisher House

*** Test Cases ***
Non registered UK based users apply for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
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
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
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
    Given the user sign in and apply for international comp                            ${lead_applicant}  ${short_password}
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
    When the user reads his email and clicks the link    ${lead_intl_email_one}  Invitation to collaborate in ${createApplicationOpenInternationalCompetition}  You will be joining as part of the organisation  2
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
    When the user reads his email and clicks the link     ${partner_international_email}  Invitation to collaborate in ${createApplicationOpenInternationalCompetition}  You will be joining as part of the organisation  2
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
    Then The user should not see an error in the page

Registered International lead user applying for an international competition see only International organisations
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user sign in and apply for international comp                                 ${lead_international_email}  ${short_password}
    When check if there is an existing application in progress for this competition
    Then the user should see organisations list according to organisation type selected     Apply with a different organisation  jQuery = dt:contains("Empire (french)")

Registered International lead user applies for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user clicks the button/link                         link = Apply with a different organisation
    When the user provides international organisation details     343434435  Sydney  Australia  Australia  New Empire 1  international-organisation-details-cta
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
    When the user clicks the button/link     link = Application overview
    Then the user enters application details

Lead applicant applies again to international competition using the same international organisation and verifies organisation address
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given the user select the competition and starts application                       ${createApplicationOpenInternationalCompetition}
    And check if there is an existing application in progress for this competition
    When user selects where is organisation based                                      isInternational
    Then the user clicks the button/link                                               jQuery = span:contains("New Empire 1")
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
    Given team member accepts the invite to join organisation       ${team_member}  ${createApplicationOpenInternationalCompetition}  MemberFName  MemberSName
    When the user clicks the button/link                            link = Sign in
    And Logging in and Error Checking                               ${team_member}  ${correct_password}
    And the user clicks the button/link                             link = ${internationalApplicationTitle}
    Then the member can edit leads organisation address details
    [Teardown]    Logout as user

Lead applicant adds a UK based partner organisation
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    [Setup]  Logging in and Error Checking     ${lead_international_email}  ${short_password}
    Given the user clicks the button/link      link = ${internationalApplicationTitle}
    And the user clicks the button/link        link = Application team
    When the user clicks the button/link       link = Add a partner organisation
    And the user adds a new partner            Partner Ltd  FName SName  ${partner_org}
    Then the user should see the element       jQuery = td:contains("FName SName (pending for 0 days)")
    [Teardown]    Logout as user

UK based partner organisation accepts the invite to collaborate and cannot edit lead org address details
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given partner organisation accepts the invite to collaborate      ${partner_org}  ${createApplicationOpenInternationalCompetition}  ${BUSINESS_TYPE_ID}
    When the user clicks the button/link                              link = Application team
    Then the user should not see the element                          link = Edit
    And the user should see the element                               jQuery = td:contains("${newAddress}")
    [Teardown]    Logout as user

Internal user can see International Organisation Address Details in Application Overview
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    [Setup]  requesting Application ID for this Application
    Given Logging in and Error Checking                         &{internal_finance_credentials}
    When the user selects the application in progress
    And the user clicks the button/link                         jQuery = button:contains("Open all")
    Then the user should see the element                        jQuery = td:contains("Address")
    And the user should see the element                         jQuery = td:contains("${addressLine1}")
    [Teardown]    Logout as user

Lead applicant is able to complete international application
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    [Setup]  Logging in and Error Checking         ${lead_international_email}  ${short_password}
    Given the user clicks the button/link          link = ${internationalApplicationTitle}
    When the user completes the application

UK based partner also completes funding info
    [Documentation]    IFS-7264
    [Tags]  HappyPath
    Given log in as a different user                ${partner_org}  ${correct_password}
    Given the user clicks the button/link           link = ${internationalApplicationTitle}
    When the user clicks the button/link            link = Your project finances
    And partner marks the finance as complete       ${internationalApplicationTitle}
    Then Logout as user

Lead applicant submits the application
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    [Setup]  Logging in and Error Checking         ${lead_international_email}  ${short_password}
    Given the user clicks the button/link          link = ${internationalApplicationTitle}
    When the applicant submits the application
    Then the user should not see an error in the page
    [Teardown]    Logout as user

Moving International Competition to Project Setup
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    [Setup]  Get competitions id and set it as suite variable     ${internationalCompetitionTitle}
    Given Logging in and Error Checking                           &{internal_finance_credentials}
    When moving competition to Closed                             ${internationalCompetitionId}
    And making the application a successful project               ${internationalCompetitionId}  ${internationalApplicationTitle}
    Then moving competition to Project Setup                      ${internationalCompetitionId}

Internal user is able to add a new international partner organisation
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    [Setup]  logging in and error checking                 &{internal_finance_credentials}
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${InternationalCompetitionId}/status/all
    When the user clicks the button/link                   jQuery = tr:contains("${internationalApplicationTitle}") .waiting:nth-child(3)
    And the user clicks the button/link                    link = Add a partner organisation
    And the user adds a new partner organisation           INTERNATIONAL PARTNER ORGANISATION  FName Surname  ${international_invite_email}
    Then organisation is able to accept project invite     FName  Surname  ${international_invite_email}  ${ApplicationID}  ${internationalApplicationTitle}

Partner organisation provide organisation detail and create an account
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    Given partner user provide organisation detail and create account     ${international_invite_email}
    When the user clicks the button/link                                  link = ${internationalApplicationTitle}
    Then partner completes all sections to join the project

Partner organisation submits project details and is able to see organisation address details in project team
    [Documentation]  IFS-7264
    [Tags]  HappyPath
    Given the user completes project details
    When the user clicks the button/link                                                     link = Return to setup your project
    And the user completes project team and can see international organisation addresses
    Then the user clicks the button/link                                                     link = Return to setup your project
    And the user should see the element                                                      jQuery = p:contains("You must complete your project and bank details within 30 days of our notification to you.")
    [Teardown]  logout as user

Lead organisation can see international organisation address details in project team and cannot edit it
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    [Setup]  logging in and error checking                                                    ${lead_international_email}  ${short_password}
    Given the user clicks the button/link                                                     link = ${internationalApplicationTitle}
    When the user completes project team and can see international organisation addresses

Lead organisation completes the project team and project details
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    Given the user chooses the project manager
    When the user clicks the button/link                                 link = Return to setup your project
    Then the user clicks the button/link                                 link = Project details
    And the user completes correspondence address in project details
    [Teardown]  logout as user

Partner organisation can see international organisation address details in project team and cannot edit it
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    [Setup]  logging in and error checking                                                    ${partner_org}  ${correct_password}
    Given the user clicks the button/link                                                     link = ${internationalApplicationTitle}
    When the user completes project team and can see international organisation addresses
    Then the user clicks the button/link                                                      link = Return to setup your project
    [Teardown]  logout as user

Internal user can view address details
    [Documentation]  IFS-7200
    [Tags]  HappyPath
    [Setup]  logging in and error checking       &{internal_finance_credentials}
    Given the user navigates to the page         ${server}/project-setup-management/competition/${InternationalCompetitionId}/status/all
    When the user clicks the button/link         jQuery = tr:contains("${internationalApplicationTitle}") .waiting:nth-child(3)
    Then the user should see the element         jQuery = td:contains("${addressLine1}")

*** Keywords ***
UK-based user sees these page elements
    the user should see the element         jQuery = p:contains("This is the organisation that will lead the application.")
    the user should see the element         jQuery = span:contains("Higher education and organisations registered with Je-S.")
    the user should not see the element     jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")

international user sees these page elements
    the user should see the element         jQuery = p:contains("This is the organisation that will lead the application.")
    the user should not see the element     jQuery = span:contains("Higher education and organisations registered with Je-S.")
    the user should not see the element     jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")

the user verifies their organisation details
    the user should see the element         jQuery = p:contains("This organisation will lead the application.")
    the user should not see the element     jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")
    the user should see the element         jQuery = dt:contains("Is your organisation based in the UK?")

the user sign in and apply for international comp
    [Arguments]  ${user}  ${password}
    the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
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

invite partner organisation
    [Arguments]  ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          link = Add a partner organisation
    the user adds a partner organisation     ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          jQuery = button:contains("Invite partner organisation")

Registered UK based lead user goes to the application team
    Logging in and Error Checking       ${lead_applicant}  ${short_password}
    the user clicks the button/link     jQuery = li:contains("${createApplicationOpenInternationalCompetition}") a:contains("Untitled")
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
    the user should see the element              jQuery = span:contains("Is your organisation based in the UK?")
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
    lead marks the finance as complete                                             ${internationalApplicationTitle}   Calculate  52,214
    the user accept the competition terms and conditions                           Return to application overview

lead marks the finance as complete
    [Arguments]  ${application}  ${overheadsCost}  ${totalCosts}
    the user fills in the project costs                      ${overheadsCost}  ${totalCosts}
    the user enters the project location                     town   London
    the user fills in the organisation information           ${application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                     ${application}
    the user should see all finance subsections complete
    the user clicks the button/link                          link = Back to application overview
    the user should see the element                          jQuery = li:contains("Your project finances") > .task-status-complete

partner marks the finance as complete
    [Arguments]  ${application}
    the user clicks the button/link                          link = Your project costs
    the user fills in Labour
    the user clicks the button/link                          css = label[for="stateAidAgreed"]
    the user clicks the button/link                          jQuery = button:contains("Mark as complete")
    the user enters the project location                     postcode  AB12 3CD
    the user fills in the organisation information           ${application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                     ${application}
    the user should see all finance subsections complete
    the user clicks the button/link                          link = Back to application overview
    the user accept the competition terms and conditions     Return to application overview

the user enters the project location
    [Arguments]  ${locator}  ${location}
    the user clicks the button/link         link = Your project location
    the user enters text to a text field    ${locator}  ${location}
    the user clicks the button/link         jQuery = button:contains("Mark as complete")

requesting project ID for this project
    ${ProjectID} =  get project id by name    ${internationalApplicationTitle}
    Set suite variable    ${ProjectID}

requesting Application ID for this Application
    ${ApplicationID} =  get application id by name    ${internationalApplicationTitle}
    Set suite variable    ${ApplicationID}

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
    the user clicks the button/link     link = ${createApplicationOpenInternationalCompetition}
    the user clicks the button/link     link = Applications: All, submitted, ineligible
    the user clicks the button/link     link = All applications
    the user clicks the button/link     link = ${ApplicationID}

partner completes all sections to join the project
    the user updates organisation size details      SMALL  800000  200
    the user updates funding                        request-funding-yes  40  other-funding-no
    the user accepts terms and conditions
    And the user clicks the button/link             id = submit-join-project-button

the user updates organisation size details
    [Arguments]  ${Size}  ${Turnover}  ${Employees}
    the user clicks the button/link          link = Your organisation
    the user selects the radio button        organisationSize  ${Size}
    the user enters text to a text field     id = turnover   ${Turnover}
    the user enters text to a text field     id = headCount   ${Employees}
    the user clicks the button/link          id = mark_as_complete

the user updates funding
    [Arguments]  ${require_funding}  ${percentage}  ${other_funding}
    the user clicks the button/link          link = Your funding
    the user selects the radio button        requestingFunding  request-funding-yes
    the user enters text to a text field     id = grantClaimPercentage  ${percentage}
    the user selects the radio button        otherFunding  ${other_funding}
    the user clicks the button/link          id = mark-all-as-complete

the user accepts terms and conditions
    the user clicks the button/link     link = Award terms and conditions
    the user selects the checkbox       agreed
    the user clicks the button/link     id = agree-terms-button

the user completes project details
    the user clicks the button/link          link = Project details
    the user clicks the button/link          link = Edit
    the user enters text to a text field     id = town  Sydney
    the user clicks the button/link          jQuery = button:contains("Save project location")

the user completes project team and can see international organisation addresses
    the user clicks the button/link         link = Project team
    the user should not see the element     link = Edit
    the user should see the element         jQuery = h2:contains("New Empire 1")
    the user should see the element         jQuery = td:contains("${addressLine1}")
    the user clicks the button/link         link = Your finance contact
    the user selects the radio button       financeContact  financeContact1
    the user clicks the button/link         id = save-finance-contact-button

the user chooses the project manager
    the user clicks the button/link      link = Project manager
    the user selects the radio button    projectManager  projectManager1
    the user clicks the button/link      id = save-project-manager-button

the user completes correspondence address in project details
    the user clicks the button/link          link = Correspondence address
    the user enters text to a text field     id = addressLine1  Lane 1
    the user enters text to a text field     id = addressLine2  Tester Street
    the user enters text to a text field     id = town  Sydney
    input text                               id = country  Australia
    the user clicks the button/link          jQuery = ul li:contains("Australia")
    the user enters text to a text field     id = zipCode   ZippyCode123
    the user clicks the button/link          id = save-project-address-button
    the user clicks the button/link          link = Return to set up your project

the user cannot edit international organisation

Custom Suite Setup
    The guest user opens the browser
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database
