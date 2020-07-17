*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list    #Invite flow without email. This test is using the old application
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant  ATS2020
Resource          ../../resources/defaultResources.robot

*** Variables ***
${INVITE_LINK}                      ${SERVER}/accept-invite/78aa4567-0b70-41da-8310-a0940644d0ba
${SELECT_ORGANISATION}              ${SERVER}/organisation/create/organisation-type
${terms_and_conditions_user_id}     ${user_ids['${terms_and_conditions_login_credentials["email"]}']}
# This file uses the Application: Climate science the history of Greenland's ice    (Lead applcant: Steve.Smith)

*** Test Cases ***
Research and technology organisations (RTO) search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    Given the user navigates to the page                                    ${INVITE_LINK}
    When the user accepts the invitation and search for an organisation
    Then the user should see a field error                                  Please enter an organisation name to search.
    And the user clicks the button/link                                     jQuery = summary:contains("Enter details manually")
    And the user enters organisation details                                Digital Catapult

Research and technology organisations (RTO) search (accept invitation flow)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    When the user navigates to the page                    ${server}/registration/register
    Then the invited user fills the create account form    Thierry    Henry

Research and technology organisations (RTO) search (accept invitation flow with email step)
    [Documentation]    INFUND-1230
    [Tags]  HappyPath
    Given the user reads his email and clicks the link        ${test_mailbox_one}+invite1@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page     ${REGISTRATION_VERIFIED}
    When the user clicks the button/link                      jQuery = p:contains("Your account has been successfully verified.")~ a:contains("Sign in")
    And Logging in and Error Checking                         ${test_mailbox_one}+invite1@gmail.com    ${correct_password}
    Then the user should be redirected to the correct page    ${APPLICANT_DASHBOARD_URL}

*** Keywords ***
Custom Suite Setup
    The guest user opens the browser
    Connect to database  @{database}

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationTypeId    ${ORG_TYPE}

the user enters organisation details
    [Arguments]    ${orgName}
    the user enters text to a text field     id = organisationSearchName    ${orgName}
    the user clicks the button/link          id = org-search
    the user clicks the button/link          link = INNOVATE LTD
    the user clicks the button/link          jQuery = .govuk-button:contains("Save and continue")

Custom suite teardown
    The user closes the browser
    Disconnect from database

the user accepts the invitation and search for an organisation
    the user clicks the button/link       jQuery = .govuk-button:contains("Yes, accept invitation")
    the user selects the radio button     organisationTypeId    3
    the user clicks the button/link       jQuery = .govuk-button:contains("Save and continue")
    the user clicks the button/link       jQuery = .govuk-button:contains("Search")