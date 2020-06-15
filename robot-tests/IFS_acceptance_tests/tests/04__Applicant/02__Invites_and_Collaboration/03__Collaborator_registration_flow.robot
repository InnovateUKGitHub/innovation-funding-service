*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list    #Invite flow without email. This test is using the old application
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${INVITE_LINK}    ${SERVER}/accept-invite/78aa4567-0b70-41da-8310-a0940644d0ba
${SELECT_ORGANISATION}    ${SERVER}/organisation/create/organisation-type
${terms_and_conditions_user_id}  ${user_ids['${terms_and_conditions_login_credentials["email"]}']}
# This file uses the Application: Climate science the history of Greenland's ice    (Lead applcant: Steve.Smith)

*** Test Cases ***
Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given the user navigates to the page          ${INVITE_LINK}
    And the user should see the element           jQuery = p strong:contains("Worth Internet Systems")
    When the user clicks the button/link          jQuery = .govuk-button:contains("Yes, accept invitation")

User cannot continue if an organisation type is not selected
    [Documentation]    INFUND-1005, INFUND-1780, INFUND-1166
    [Tags]
    Given browser validations have been disabled
    When the user clicks the button/link           jQuery = .govuk-button:contains("Save and continue")
    Then the user should see a field error         Please select an organisation type.
    Given the user selects the radio button        organisationTypeId    3
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")

User is able to select only one type
    [Documentation]    INFUND-1005
    Given the user navigates to the page                 ${SELECT_ORGANISATION}
    When the user selects the radio button               organisationTypeId    2
    And the user selects the radio button                organisationTypeId    1
    Then the radio button should have the new selection  1

The type of organisation navigates to the correct page
    [Documentation]    INFUND-1780, INFUND-1231, INFUND 8531
    [Tags]
    When the user selects the radio button          organisationTypeId    1
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    Then the user should see the element           jQuery = div label:contains("Enter your organisation name or registration number.")
    When the user clicks the button/link           link = Back to choose your organisation type
    And the user should see the element            jQuery = .govuk-hint:contains("Higher education and organisations registered with Je-S.")
    Given the user selects the radio button        organisationTypeId    2
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    Then the user should see the element           jQuery = span:contains("This is the organisation that you work for, this will search all organisations available on Je-S.")
    And the user enters text to a text field       id = organisationSearchName    zoo
    And the user clicks the button/link            jQuery = button:contains("Search")
    Then the user should see the element           jQuery = p:contains("Choose your organisation:")
    When the user clicks the button/link           jQuery = a:contains("Zoological Soc London Inst of Zoology")
    When the user clicks the button/link           link = Back to select your organisation
    Then the user should see the element           jQuery = span:contains("This is the organisation that you work for, this will search all organisations available on Je-S.")
    Given the user clicks the button/link          jQuery = a:contains("Back to choose your organisation type")
    Then the user should see the element           jQuery = .govuk-hint:contains("Organisations which solely promote and conduct collaborative research and innovation.")
    Given the user selects the radio button        organisationTypeId    3
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    And the user should see the element            jQuery = div label:contains("Enter your organisation name or registration number.")
    When the user clicks the button/link           link = Back to choose your organisation type
    And the user should see the element            jQuery = .govuk-hint:contains("A not-for-profit organisation focusing on innovation.")
    Given the user selects the radio button        organisationTypeId    4
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    And the user should see the element            jQuery = div label:contains("Enter your organisation name or registration number.")
    And the user goes back to the previous page

Research and technology organisations (RTO) search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    Given the user navigates to the page           ${INVITE_LINK}
    When the user clicks the button/link           jQuery = .govuk-button:contains("Yes, accept invitation")
    And the user selects the radio button          organisationTypeId    3
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    When the user clicks the button/link           jQuery = .govuk-button:contains("Search")
    Then the user should see a field error         Please enter an organisation name to search.
    When the user clicks the button/link           jQuery = summary:contains("Enter details manually")
    Then the user enters organisation details      Digital Catapult

Research and technology organisations (RTO) search (accept invitation flow)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    When the user navigates to the page            ${server}/registration/register
    Then the invited user fills the create account form    Thierry    Henry

Research and technology organisations (RTO) search (accept invitation flow with email step)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    Given the user reads his email and clicks the link  ${test_mailbox_one}+invite1@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page                        ${REGISTRATION_VERIFIED}
    When the user clicks the button/link                                         jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    And And Logging in and Error Checking                                        ${test_mailbox_one}+invite1@gmail.com    ${correct_password}
    Then the user should be redirected to the correct page                       ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                                          link = Climate science the history of Greenland's ice
    And the user clicks the button/link                                          link = Your project finances
    And the user should see the element                                          jQuery = h1:contains("Your project finances")

Validation on terms and condition page
    [Documentation]  IFS-3093
    [Tags]  HappyPath
    [Setup]  Delete user from terms and conditions database   ${terms_and_conditions_user_id}
    Given Log in as a different user                   &{terms_and_conditions_login_credentials}
    When The user clicks the button/link                css = button[type="submit"]
    Then the user should see a field and summary error  In order to continue you must agree to the terms and conditions.

User is able to accept new terms and conditions
    [Documentation]  IFS-3093
    [Tags]  HappyPath
    Given the user selects the checkbox   agree
    And the user cannot see a validation error in the page
    When the user clicks the button/link  css = .govuk-button[type="submit"]
    Then the user should see the element  jQuery = h1:contains(${APPLICANT_DASHBOARD_TITLE})

*** Keywords ***
Custom Suite Setup
    The guest user opens the browser
    Connect to database  @{database}

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationTypeId    ${ORG_TYPE}

the user enters organisation details
    [Arguments]    ${orgName}
    the user enters text to a text field       id = organisationSearchName    ${orgName}
    the user clicks the button/link            id = org-search
    the user clicks the button/link            link = INNOVATE LTD
    the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")

Custom suite teardown
    The user closes the browser
    Disconnect from database