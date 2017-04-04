*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list    #Invite flow without email. This test is using the old application
Suite Setup       Custom Suite Setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${INVITE_LINK}    ${SERVER}/accept-invite/78aa4567-0b70-41da-8310-a0940644d0ba
${SELECT_ORGANISATION}    ${SERVER}/organisation/create/type/new-account-organisation-type
# This file uses the Application: Climate science the history of Greenland's ice  (Lead applcant: Steve.Smith)

*** Test Cases ***
Lead applicant details should show in the invite page
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Yes, accept invitation")
    And the user should see the text in the page    Worth Internet Systems

User cannot continue if an organisation type is not selected
    [Documentation]    INFUND-1005, INFUND-1780, INFUND-1166
    [Tags]
    Given browser validations have been disabled
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Please select an organisation type.
    Given the user selects the radio button    organisationType    3
    And the user clicks the button/link    jQuery=.button:contains("Continue")

User is able to select only one type
    [Documentation]    INFUND-1005
    Given the user navigates to the page    ${SELECT_ORGANISATION}
    When the user selects the radio button    organisationType    2
    And the user selects the radio button    organisationType    1
    Then the radio button should have the new selection    1

The type of organisation navigates to the correct page
    [Documentation]    INFUND-1780, INFUND-1231, INFUND 8531
    [Tags]
    When the user should see the element    jQuery=.form-hint:contains("UK based business.")
    And the user selects the radio button    organisationType    1
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Enter your organisation name or registration number.
    When the user goes back to the previous page
    And the user should see the element    jQuery=.form-hint:contains("Universities, colleges, organisations registered on Je-S.")
    Given the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    This is the organisation that you work for, this will search all organisations available on Je-S.
    And the user enters text to a text field     id=organisationSearchName   zoo
    And the user clicks the button/link        jQuery=button:contains("Search")
    Then the user should see the element     jQuery=p:contains("Choose your organisation:")
    When the user clicks the button/link        jQuery=a:contains("Zoological Soc London Inst of Zoology")
    Then the user should see the text in the page    This is the address that your organisation works from
    When the user goes back to the previous page
    Then the user should see the text in the page    This is the organisation that you work for, this will search all organisations available on Je-S.
    Given the user clicks the button/link    jQuery=a:contains("Back to choose your organisation type")
    Then the user should see the element    jQuery=.form-hint:contains("Organisations which solely promote and conduct collaborative research and innovation.")
    Given the user selects the radio button    organisationType    3
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Research and technology organisations (RTOs)
    And the user should see the text in the page    Enter your organisation name or registration number.
    When the user goes back to the previous page
    And the user should see the element    jQuery=.form-hint:contains("A not-for-profit public sector body or charity working on innovation.")
    Given the user selects the radio button    organisationType    4
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Public sector organisation or charity
    And the user should see the text in the page    Enter your organisation name or registration number.
    And the user goes back to the previous page

Catapult search (empty, invalid & valid inputs)
    [Documentation]    INFUND-1230
    [Tags]    HappyPath
    Given the user navigates to the page    ${INVITE_LINK}
    When the user clicks the button/link    jQuery=.button:contains("Yes, accept invitation")
    And the user selects the radio button    organisationType    3
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    Please enter an organisation name to search.
    When the user clicks the button/link    jQuery=summary:contains("Enter details manually")
    And the user enters text to a text field    name=organisationName    Digital Catapult
    When the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user should see the text in the page    Please enter a UK postcode
    When the user enters text to a text field    name=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user clicks the button/link    jQuery=.button:contains("Use selected address")
    Then the address fields should be filled

Catapult search (accept invitation flow)
    [Documentation]    INFUND-1230
    [Tags]    HappyPath
    When the user clicks the button/link    jQuery=.button:contains("Continue")
    Then the user should see the text in the page    Digital Catapult
    And the user should see the text in the page    Operating Address
    And the user clicks the button/link    jQuery=.button:contains("Confirm")
    When the user navigates to the page  ${server}/registration/register
    Then the user fills the create account form    Thierry    Henry

Catapult search (accept invitation flow with email step)
    [Documentation]    INFUND-1230
    [Tags]    Email    HappyPath
    Given the user reads his email from the default mailbox and clicks the link  ${test_mailbox_one}+invite1@gmail.com  Please verify your email address    Once verified you can sign into your account
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in                   ${test_mailbox_one}+invite1@gmail.com  ${correct_password}
    Then the user should be redirected to the correct page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Climate science the history of Greenland's ice
    And the user clicks the button/link    link=Your finances
    And the user should see the element    jQuery=h1:contains("Your finances")

*** Keywords ***
Custom Suite Setup
    delete the emails from both test mailboxes
    The guest user opens the browser

the radio button should have the new selection
    [Arguments]    ${ORG_TYPE}
    Radio Button Should Be Set To    organisationType    ${ORG_TYPE}
