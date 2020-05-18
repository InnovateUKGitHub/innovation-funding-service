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
${companyRegistrationNumber}                           637388
${internationalOrganisationFirstLineAddress}           7 Pinchington Lane
${internationalOrganisationTownAddress}                Kansas
${internationalOrganisationCountryAddress}             United S
${internationalOrganisationCountryCompleteAddress}     United States
${organisation_name}                                   Org

*** Test Cases ***
Non registered UK based users apply for an international competition
    [Documentation]    IFS-7197 IFS-7198 IFS-7199
    [Tags]  HappyPath
    Given the user starts an application for International competition
    When non-registered user selects business options                 isNotInternational
    Then the user should see the element                              jQuery = p:contains("This is the organisation that will lead the application.")
    And the user is able to complete UK based organisation details
    And the user verifies their organisation details
    And the user enters the details and clicks the create account     Zoya  Akhtar  ${lead_applicant}  ${correct_password}

Non registered international users apply for an international competition
    [Documentation]    IFS-7197 IFS-7198 IFS-7199
    [Tags]  HappyPath
    Given the user starts an application for International competition
    When non-registered user selects business options                 isInternational
    Then the user should see the element                              jQuery = p:contains("This is the organisation that will lead the application.")
    And the user is able to complete international organisation details
    And the user verifies their organisation details
    And the user enters the details and clicks the create account     Alex  Bumble  ${lead_international_email}  ${correct_password}

Registered users with international organisation apply for an international competition
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user starts an application for International competition
    When registered user signs in and selects business options     isInternational  ${lead_international_email}  ${short_password}
    And the user is able to complete international organisation details
    And the user verifies their organisation details
    Then the user should see the element                           jQuery = h1:contains("Application overview")
    And Logout as user

Registered users with UK based organisation apply for an international competition
    [Documentation]    IFS-7252
    [Tags]  HappyPath
    Given the user starts an application for International competition
    When registered user signs in and selects business options     isNotInternational  ${lead_applicant}  ${short_password}
    And the user chooses organisation type and enters the data manually
    Then the user should see the element                           jQuery = h1:contains("Application overview")

*** Keywords ***
the user chooses organisation type and enters the data manually
    the user selects the radio button     organisationTypeId  radio-1
    the user clicks the button/link       name = select-company-type
    the user clicks the button/link       jQuery = span:contains("Enter details manually")
    input text                            css = [id = "organisationName"]  ${organisation_name}
    the user clicks the button/link       css = [id = "save-organisation-details-button"]
    the user clicks the button/link       name = save-organisation

the user starts an application for International competition
    the user navigates to the page                            ${frontDoor}
    the user clicks the button/link in the paginated list     link = ${createApplicationOpenInternationalCompetition}
    the user clicks the button/link                           link = Start new application

non-registered user selects business options
    [Arguments]  ${isBusinessInternational}
    the user clicks the button/link         link = Continue and create an account
    the user should not see the element     jQuery = span:contains("Create an account")
    the user selects the radio button       international  ${isBusinessInternational}
    the user clicks the button/link         name = select-company-type

registered user signs in and selects business options
    [Arguments]  ${isBusinessInternational}  ${username}  ${password}
    the user clicks the button/link         jQuery = .govuk-grid-column-one-half a:contains("Sign in")
    Logging in and Error Checking           ${username}  ${password}
    check if there is an existing application in progress for this competition
    the user selects the radio button       international  ${isBusinessInternational}
    the user clicks the button/link         name = select-company-type
    the user clicks the button/link         link = Apply with a different organisation

the user is able to complete international organisation details
    the user selects the radio button     organisationTypeId  radio-1
    the user clicks the button/link       name = select-company-type
    the user gets an error message on not filling mandatory fields
    input text                            css = [id = "companyRegistrationNumber"]  ${companyRegistrationNumber}
    input text                            css = [id = "addressLine1"]  ${internationalOrganisationFirstLineAddress}
    input text                            css = [id = "town"]  ${internationalOrganisationTownAddress}
    input text                            css = [id = "country"]  ${internationalOrganisationCountryAddress}
    the user clicks the button/link       jQuery = ul li:contains("${internationalOrganisationCountryCompleteAddress}")
    the user clicks the button/link       css = [id = "lead-organisation-type-cta"]

the user is able to complete UK based organisation details
    the user selects the radio button     organisationTypeId  radio-1
    the user clicks the button/link       name = select-company-type
    the user should see the element       jQuery = h2:contains("Search on Companies House")
    the user clicks the Not on companies house link

the user verifies their organisation details
    the user should see the element      jQuery = p:contains("This organisation will lead the application.")
    the user clicks the button/link      jQuery = button:contains("Save and continue")

the user gets an error message on not filling mandatory fields
    input text                           css = [id = "name"]  ${internationalOrganisationName}
    the user clicks the button/link      css = [id = "lead-organisation-type-cta"]
    the user should see the element      jQuery = h2:contains("We were unable to save your changes.")
    the user should see the element      link = The first line of the address cannot be blank.
    the user should see the element      link = The town cannot be blank.
    the user should see the element      link = The country cannot be blank.