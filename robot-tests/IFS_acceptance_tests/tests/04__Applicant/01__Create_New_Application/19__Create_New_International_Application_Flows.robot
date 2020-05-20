*** Settings ***
Documentation     IFS-7197 As a non-UK based business I can apply for International Competition as a Lead Applicant..
...
...               IFS-7198 As a non-UK based business I can create a new account to apply to an International Competition..
...
...               IFS-7199 Read only page for organisation details should not have a banner mentioning only UK based organisations can apply for the International Competition..
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Applicant_Commons.robot

*** Variables ***
${internationalOrganisationFirstLineAddress}           7 Pinchington Lane

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
    Given the user selects the radio button                                                     organisationTypeId  radio-1
    And the user clicks the button/link                                                         name = select-company-type
    When the user enters text to a text field                                                   name = organisationSearchName  Nomensa
    And the user clicks the button/link                                                         name = search-organisation
    And the user clicks the button/link                                                         link = NOMENSA LTD
    And the user confirms organisation details and create an account for non-registered user    Tony  Blair  ${uk_based_applicant_1}  ${short_password}
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
    Given the user provides international organisation details                                    564789  London  cana  Canada  International_Ltd.
    When the user confirms organisation details and create an account for non-registered user     Roselin  Messy  ${lead_international_email_1}  ${short_password}
    Then the user should not see an error in the page

Registered users applying for an international competition see no international organisation if there is none
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user select the competition and starts application      ${createApplicationOpenInternationalCompetition}
    And the user clicks the button/link                               jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    And Logging in and Error Checking                                 ${lead_applicant}  ${short_password}
    When check if there is an existing application in progress for this competition
    And user selects organisation type                                isInternational
    Then the user should not see the element                          link = Apply with a different organisation

Registered users applying for an international competition see only UK based organisations if they are UK based
    [Documentation]    IFS-7252
    [Tags]  HappyPath
     Given the user clicks the button/link     link = Back
     When user selects organisation type       isNotInternational
     Then the user should see the element      jQuery = dt:contains("Empire Ltd")
     And the user should see the element       link = Apply with a different organisation

Registered users applying for an international competition see only International organisations if they are non-UK based
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given log in as a different user                                 ${lead_international_email}  ${short_password}
    And the user select the competition and starts application       ${createApplicationOpenInternationalCompetition}
    When check if there is an existing application in progress for this competition
    And user selects organisation type                               isInternational
    Then the user should see the element                             jQuery = dt:contains("Empire (french)")
    And the user clicks the button/link                              link = Back
    When user selects organisation type                              isNotInternational
    Then the user should not see the element                         link = Apply with a different organisation

*** Keywords ***
the user confirms organisation details and create an account for non-registered user
    [Arguments]  ${firstname}  ${lastname}  ${email}  ${password}
    the user should see the element                             jQuery = p:contains("This organisation will lead the application.")
    the user should not see the element                         jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")
#    the user should see the element                             jQuery = h2:contains("Is your organisation based in the UK?")
    the user clicks the button/link                             name = save-organisation
    the user enters the details and clicks the create account   ${firstname}  ${lastname}  ${email}  ${password}

user selects organisation type
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       name = select-company-type

apply for comp with a different organisation
    [Arguments]  ${check_international}
    check if there is an existing application in progress for this competition
    user selects organisation type      ${check_international}
    the user clicks the button/link     link = Apply with a different organisation

non-registered user selects business options
    [Arguments]  ${isBusinessInternational}
    the user clicks the button/link         link = Continue and create an account
    the user should see the element         jQuery = p:contains("Is your organisation based in the UK?")
    the user should not see the element     jQuery = span:contains("Create an account")
    user selects organisation type          ${isBusinessInternational}

the user provides international organisation details
    [Arguments]  ${company_reg_no}  ${international_org_town}  ${international_org_country}  ${international_org_country_complete}  ${international_org_name}
    the user selects the radio button        organisationTypeId  radio-1
    the user clicks the button/link          name = select-company-type
    The user enters text to a text field     css = [id = "name"]  ${international_org_name}
    the user gets an error message on not filling mandatory fields
    The user enters text to a text field     css = [id = "companyRegistrationNumber"]  ${company_reg_no}
    The user enters text to a text field     css = [id = "addressLine1"]  ${internationalOrganisationFirstLineAddress}
    The user enters text to a text field     css = [id = "town"]  ${international_org_town}
    input text                               css = [id = "country"]  ${international_org_country}
    the user clicks the button/link          jQuery = ul li:contains("${international_org_country_complete}")
    the user clicks the button/link          css = [id = "lead-organisation-type-cta"]

the user verifies their organisation details
    the user should see the element         jQuery = p:contains("This organisation will lead the application.")
    the user should not see the element     jQuery = p:contains("Your organisation must be UK based to receive funding from Innovate UK.")
    the user clicks the button/link         jQuery = button:contains("Save and continue")

the user gets an error message on not filling mandatory fields
    the user clicks the button/link     css = [id = "lead-organisation-type-cta"]
    the user should see the element     jQuery = h2:contains("We were unable to save your changes.")
    the user should see the element     link = The first line of the address cannot be blank.
    the user should see the element     link = The town cannot be blank.
    the user should see the element     link = The country cannot be blank.