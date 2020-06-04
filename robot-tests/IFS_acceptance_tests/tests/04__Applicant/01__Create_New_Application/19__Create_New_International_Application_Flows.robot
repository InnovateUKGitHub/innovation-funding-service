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
    Given the user sign in and apply for international comp     ${lead_applicant}  ${short_password}
    And check if there is an existing application in progress for this competition
    When user selects where is organisation based               isInternational
    Then the user should not see the element                    link = Apply with a different organisation

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

Partner user provides non-UK based organisation details and verifies them
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

Non-Registered user(Partner organisation) provide organisation details and verifies them
    [Documentation]    IFS-7199
    [Tags]  HappyPath
    Given user selects where is organisation based                isInternational
    When the user provides international organisation details     435353543  Helsinki  Finland  Finland  New Empire  international-organisation-details-cta
    Then the user should see the element                          jQuery = p:contains("This is the organisation that you will join the application with.")
    And the user clicks the button/link                           id = international-confirm-organisation-cta
    And The user should not see an error in the page

Non-Registered user(Partner organisation) create an account
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

Applicant is able to complete and submit international application
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    [Setup]  the user navigates to the page        ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link          link = Untitled application (start here)
    When the user completes the application
    Then the applicant submits the application

Moving International Competition to Project Setup
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    [Setup]  Get competitions id and set it as suite variable     ${internationalCompetitionTitle}
    Given Log in as a different user                              &{internal_finance_credentials}
    When moving competition to Closed                             ${internationalCompetitionId}
    And making the application a successful project               ${internationalCompetitionId}  ${internationalApplicationTitle}
    Then moving competition to Project Setup                      ${internationalCompetitionId}
    [Teardown]  Requesting IDs of this Project

Ifs Admin is able to add a new partner organisation
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${InternationalCompetitionId}/status/all
    When the user clicks the button/link                   jQuery = tr:contains("${internationalApplicationTitle}") .waiting:nth-child(3)
    And the user clicks the button/link                    link = Add a partner organisation
    And the user adds a new partner organisation           Testing International Partner Organisation  FName Surname  ${international_invite_email}
    Then organisation is able to accept project invite     FName  Surname  ${international_invite_email}  ${ApplicationID}  ${internationalApplicationTitle}

Partner organisation provide organisation detail and create an account
    [Documentation]  IFS-7197
    [Tags]  HappyPath
    When partner user provide organisation detail and create account
    Then the user should not see an error in the page

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
    the user clicks the button/link                                   jQuery = .govuk-button:contains("Yes, create an account")
    user selects where is organisation based                          isInternational
    the user provides international organisation details              435445543  Sydney  Australia  Australia  Test Empire  international-organisation-details-cta
    the user should see the element                                   jQuery = p:contains("This is the organisation that you will join the project with.")
    the user clicks the button/link                                   id = international-confirm-organisation-cta
    partner user enters the details and clicks the create account     Tester  Simpson  ${short_password}

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
    the user clicks the button/link     jQuery = li:contains("${internationalCompetitionTitle}") a:contains("Untitled")
    the user clicks the button/link     link = Application team

the user should see organisations list according to organisation type selected
    [Arguments]  ${arg}  ${locator}
    user selects where is organisation based     isNotInternational
    the user should not see the element          link = ${arg}
    the user clicks the button/link              link = Back to tell us where your organisation is based
    user selects where is organisation based     isInternational
    the user should see the element              ${locator}

the user confirms organisation details and create an account for non-registered user
    [Arguments]  ${firstname}  ${lastname}  ${email}  ${password}

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
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${internationalApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                 ${internationalApplicationTitle}
    the user marks the finance as complete                   ${internationalApplicationTitle}   Calculate  52,214
    the user accept the competition terms and conditions     Return to application overview

the user marks the finance as complete
    [Arguments]  ${application}  ${overheadsCost}  ${totalCosts}
    the user fills in the project costs                      ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills in the organisation information           ${application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section                     ${application}
    the user should see all finance subsections complete
    the user clicks the button/link                          link = Back to application overview
    the user should see the element                          jQuery = li:contains("Your project finances") > .task-status-complete

the user enters the project location
    the user clicks the button/link         link = Your project location
    the user enters text to a text field    town   BS1 4NT
    the user clicks the button/link         jQuery = button:contains("Mark as complete")

Requesting IDs of this Project
    ${ProjectID} =  get project id by name    ${internationalApplicationTitle}
    Set suite variable    ${ProjectID}
    ${ApplicationID} =  get application id by name    ${internationalApplicationTitle}
    Set suite variable    ${ApplicationID}

Custom Suite Setup
    The guest user opens the browser
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database