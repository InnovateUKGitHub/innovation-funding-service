*** Settings ***
Documentation     IFS-7197 As a non-UK based business I can apply for International Competition as a Lead Applicant..
...
...               IFS-7198 As a non-UK based business I can create a new account to apply to an International Competition..
...
...               IFS-7199 Read only page for organisation details should not have a banner mentioning only UK based organisations can apply for the International Competition..
Suite Setup       Custom Suite Setup
Suite Teardown    Custom Suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${internationalOrganisationFirstLineAddress}           7 Pinchington Lane
${InternationalApplicationTitle}                       New Test Application for International Users
${InternationalCompetitionTitle}                       International Competition

*** Test Cases ***
Non registered UK based users apply for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
    When non-registered user selects business options                 isNotInternational
    Then the user should see the element                              jQuery = p:contains("This is the organisation that will lead the application.")
    And the user should see the element                               jQuery = span:contains("Higher education and organisations registered with Je-S.")
    And the user should not see the element                           jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")

Non registered UK based users confirm their organisation details and create an account
    [Documentation]    IFS-7199
    [Tags]  HappyPath
    Given the user selects the radio button                           organisationTypeId  radio-1
    And the user clicks the button/link                               name = select-company-type
    When the user enters text to a text field                         name = organisationSearchName  Nomensa
    And the user clicks the button/link                               name = search-organisation
    And the user clicks the button/link                               link = NOMENSA LTD
    And the user verifies their organisation details
    And the user clicks the button/link                               name = save-organisation
    And the user enters the details and clicks the create account     Tony  Blair  ${uk_based_applicant_1}  ${short_password}
    Then the user should not see an error in the page

Non registered international users apply for an international competition
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
    When non-registered user selects business options                 isInternational
    Then the user should see the element                              jQuery = p:contains("This is the organisation that will lead the application.")
    And the user should not see the element                           jQuery = span:contains("Higher education and organisations registered with Je-S.")
    And the user should not see the element                           jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")

Non registered international users can create an account and provide international organisation details
    [Documentation]    IFS-7198  IFS-7199
    [Tags]  HappyPath
    Given the user provides international organisation details        564789  London  cana  Canada  International_Ltd.  international-organisation-details-cta
    When the user verifies their organisation details
    And the user clicks the button/link                               id = international-confirm-organisation-cta
    And the user enters the details and clicks the create account     Roselin  Messy  ${lead_international_email_1}  ${short_password}
    Then the user should not see an error in the page

Registered users applying for an international competition see no international organisation if there is none
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
    And the user clicks the button/link                               jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    And Logging in and Error Checking                                 ${lead_applicant}  ${short_password}
    When check if there is an existing application in progress for this competition
    When user selects where is organisation based                     isInternational
    Then the user should not see the element                          link = Apply with a different organisation

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
    Given the user clicks the button/link       link = Apply with a different organisation
    When the user selects the radio button      organisationTypeId  radio-1
    And the user clicks the button/link         name = select-company-type
    And the user clicks the Not on companies house link
    Then the user verifies their organisation details
    And the user clicks the button/link         name = save-organisation

Registered UK based lead user invites partner organisation(with registered email/user)
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    Given the user clicks the button/link         link = Application team
    When invite partner organisation              Test Empire  Daniel Tan  ${lead_international_email1}
    Then the user should see the element          jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element           jQuery = td:contains("Daniel Tan (pending for")

Partner organisation(with registered email/user) accepts the invite
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  Logout as user
    When the user reads his email and clicks the link    ${lead_international_email1}  Invitation to collaborate in ${createApplicationOpenInternationalCompetition}  You will be joining as part of the organisation  2
    Then the user clicks the button/link                 link = Continue

Registered user(Partner organisation) logs in and select where their organisation is based
    [Documentation]    IFS-7197 IFS-7252
    [Tags]  HappyPath
    Given logging in and error checking               ${lead_international_email1}  ${short_password}
    When user selects where is organisation based     isInternational
    Then the user should not see the element          link = Join with a different organisation.
    When the user clicks the button/link              link = Back to tell us where your organisation is based
    And user selects where is organisation based      isNotInternational
    Then the user should see the element              jQuery = dt:contains("Golden Valley Research Ltd")

Partner user provides non-UK based organisation details and verifies them
    [Documentation]    IFS-7198 IFS-7199
    [Tags]  HappyPath
    Given the user clicks the button/link                         link = Join with a different organisation.
    When the user provides uk based organisation details          Nomensa  NOMENSA LTD
    And the user should see the element                           jQuery = p:contains("This is the organisation that you will join the application with.")
    And the user clicks the button/link                           name = save-organisation
    Then the user should see the element                          jQuery = h2:contains("Application progress")

Registered UK based lead user invites partner organisation(with non-registered email/user)
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  logout as user
    Given Registered UK based lead user goes to the application team
    When invite partner organisation              New Empire  Tim Simpson  ${partner_international_email}
    Then the user should see the element          jQuery = td:contains("Steve") ~ td:contains("Lead")
    And the user should see the element           jQuery = td:contains("Tim Simpson (pending for")

Partner organisation(with non-registered email/user) accepts the invite
    [Documentation]    IFS-7197
    [Tags]  HappyPath
    [Setup]  Logout as user
    When the user reads his email and clicks the link    ${partner_international_email}  Invitation to collaborate in ${createApplicationOpenInternationalCompetition}  You will be joining as part of the organisation  2
    Then the user clicks the button/link                 jQuery = a:contains("Yes, accept invitation")

Non-Registered user(Partner organisation) provide organisation details and verifies them
    [Documentation]    IFS-7199
    [Tags]  HappyPath
    Given user selects where is organisation based                isInternational
    When the user provides international organisation details     435353543  Helsinki  Finland  Finland  New Empire  international-organisation-details-cta
    And the user verifies their organisation details
    And the user clicks the button/link                           id = international-confirm-organisation-cta
    Then The user should not see an error in the page

Non-Registered user(Partner organisation) create an account
    [Documentation]    IFS-7198 IFS-7199
    [Tags]  HappyPath
    When Partner user enters the details and clicks the create account     Tim  Simpson  ${short_password}
    Then The user should not see an error in the page

Registered International lead user applying for an international competition see only International organisations
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user select the competition and starts application                            ${createApplicationOpenInternationalCompetition}
    And the user clicks the button/link                                                     jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    And logging in and error checking                                                       ${lead_international_email}  ${short_password}
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
    [Setup]  the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    Given the user clicks the button/link       link = Untitled application (start here)
    When the user completes the application
    Then the applicant submits the application

Moving International Competition to Project Setup
    [Documentation]  IFS-7197
    [Setup]  Get competitions id and set it as suite variable     ${InternationalCompetitionTitle}
    Given Log in as a different user                              &{internal_finance_credentials}
    Then moving competition to Closed                             ${InternationalCompetitionId}
    And making the application a successful project               ${InternationalCompetitionId}  ${InternationalApplicationTitle}
    And moving competition to Project Setup                       ${InternationalCompetitionId}
    [Teardown]  Requesting IDs of this Project

*** Keywords ***
Partner user enters the details and clicks the create account
    [Arguments]   ${first_name}  ${last_name}  ${password}
    Wait Until Page Contains Element Without Screenshots    jQuery = a:contains("Terms and conditions")
    the user enters text to a text field                    id = firstName  ${first_name}
    the user enters text to a text field                    id = lastName  ${last_name}
    the user enters text to a text field                    id = phoneNumber  234324234
    Input Password                   id = password  ${password}
    the user selects the checkbox    termsAndConditions
    the user selects the checkbox    allowMarketingEmails
    the user clicks the button/link  name = create-account

invite partner organisation
    [Arguments]  ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          link = Add a partner organisation
    the user adds a partner organisation     ${org_name}  ${user_name}  ${email}
    the user clicks the button/link          jQuery = button:contains("Invite partner organisation")

Registered UK based lead user goes to the application team
    Logging in and Error Checking                               ${lead_applicant}  ${short_password}
    the user clicks the button/link                             link = Untitled application (start here)
    the user clicks the button/link                             link = Application team

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
    user selects where is organisation based     ${check_international}
    the user clicks the button/link              link = Apply with a different organisation

non-registered user selects business options
    [Arguments]  ${isBusinessInternational}
    the user clicks the button/link              link = Continue and create an account
    the user should see the element              jQuery = p:contains("Is your organisation based in the UK?")
    the user should not see the element          jQuery = span:contains("Create an account")
    user selects where is organisation based     ${isBusinessInternational}

the user provides international organisation details
    [Arguments]  ${company_reg_no}  ${international_org_town}  ${international_org_country}  ${international_org_country_complete}  ${international_org_name}  ${button_id}
    the user selects the radio button        organisationTypeId  radio-1
    the user clicks the button/link          name = select-company-type
    input text                               id = name  ${international_org_name}
    the user gets an error message on not filling mandatory fields     ${button_id}
    input text                               id = companyRegistrationNumber  ${company_reg_no}
    input text                               id = addressLine1  ${internationalOrganisationFirstLineAddress}
    input text                               id = town  ${international_org_town}
    input text                               id = country  ${international_org_country}
    the user clicks the button/link          jQuery = ul li:contains("${international_org_country_complete}")
    the user clicks the button/link          id = ${button_id}

the user provides uk based organisation details
    [Arguments]  ${org_search_name}  ${org}
    the user selects the radio button        organisationTypeId  radio-1
    the user clicks the button/link          name = select-company-type
    the user enters text to a text field     name = organisationSearchName  ${org_search_name}
    the user clicks the button/link          name = search-organisation
    the user clicks the button/link          link = ${org}

the user verifies their organisation details
    the user should see the element         jQuery = p:contains("This organisation will lead the application.")
    the user should not see the element     jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")
    the user should see the element         jQuery = h2:contains("Is your organisation based in the UK?")

the user gets an error message on not filling mandatory fields
    [Arguments]  ${button_id}
    the user clicks the button/link     id = ${button_id}
    the user should see the element     jQuery = h2:contains("We were unable to save your changes.")
    the user should see the element     link = The first line of the address cannot be blank.
    the user should see the element     link = The town cannot be blank.
    the user should see the element     link = The country cannot be blank.

The user completes the application
    the user clicks the button/link                          link = Application details
    the user fills in the Application details                ${InternationalApplicationTitle}  ${tomorrowday}  ${month}  ${nextyear}
    the applicant completes Application Team
    the lead applicant fills all the questions and marks as complete(programme)
    the user navigates to Your-finances page                 ${InternationalApplicationTitle}
    the user marks the finance as complete                  ${InternationalApplicationTitle}   Calculate  52,214  yes
    the user accept the competition terms and conditions     Return to application overview

the user marks the finance as complete
    [Arguments]  ${Application}  ${overheadsCost}  ${totalCosts}  ${Project_growth_table}
    the user fills in the project costs  ${overheadsCost}  ${totalCosts}
    the user enters the project location
    the user fills in the organisation information     ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user checks Your Funding section        ${Application}
    the user should see all finance subsections complete
    the user clicks the button/link  link = Back to application overview
    the user should see the element  jQuery = li:contains("Your project finances") > .task-status-complete

Requesting IDs of this Project
    ${ProjectID} =  get project id by name    ${InternationalApplicationTitle}
    Set suite variable    ${ProjectID}
    ${ApplicationID} =  get application id by name    ${InternationalApplicationTitle}
    Set suite variable    ${ApplicationID}

Get competitions id and set it as suite variable
    [Arguments]  ${competitionTitle}
    ${InternationalCompetitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${InternationalCompetitionId}

Custom Suite Setup
    The guest user opens the browser
    Set predefined date variables
    Connect to database  @{database}

Custom Suite teardown
    Close any open browsers
    Disconnect from database