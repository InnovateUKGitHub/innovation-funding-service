*** Settings ***
Documentation     INFUND-1231: As a collaborator registering my company as Academic, I want to be able to enter full or partial details of the Academic organisation's name so I can select my Academic organisation from a list
Suite Setup       The guest user opens the browser
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Academic organisations search
    [Documentation]    INFUND-1231
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    Delete the emails from both test mailboxes
    Given we create a new user    ${test_mailbox_one}+invitedacademics${unique_email_number}@gmail.com
    Given the lead applicant invites a registered user    ${test_mailbox_one}+academicinvite${unique_email_number}@gmail.com    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com
    When the user opens the mailbox and reads his own email    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com    Invitation to collaborate in Connected digital additive manufacturing    participate in their project
    And the user clicks the button/link    jQuery=.button:contains("Create")
    When the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see an error    Please enter an organisation name to search
    When the user enters text to a text field    id=organisationSearchName    abcd
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    No results found.
    When the user enters text to a text field    id=organisationSearchName    !!
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    No results found.

Accept invitation as academic
    [Documentation]    INFUND-1166
    ...
    ...    INFUND-917
    ...
    ...    INFUND-2450
    ...
    ...    INFUND-2256
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    Delete the emails from both test mailboxes
    When the user enters text to a text field    id=organisationSearchName    Liv
    And the user clicks the button/link    jQuery=.button:contains("Search")
    Then the user should see the text in the page    University of Liverpool
    When the user clicks the button/link    link= University of Liverpool
    And the user should see the text in the page    University (HEI)
    When the user clicks the button/link    jQuery=button:contains("Enter address manually")
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    The East Wing
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine2    Popple Manor
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine3    1, Popple Boulevard
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    Poppleton
    And the user enters text to a text field    id=addressForm.selectedPostcode.county    Poppleshire
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    POPPS123
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Steven    Gerrard
    And If the user goes to the previous page he should redirect to the login page
    And the user opens the mailbox and reads his own email    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com    Please verify your email address    You have recently set up an account
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in    ${test_mailbox_one}+inviteacademics${unique_email_number}@gmail.com    Passw0rd123
    When the user clicks the button/link    link=${OPEN_COMPETITION_LINK}
    And the user clicks the button/link    link=Your finances
    Then the user should see the text in the page    TSB reference
    And the user should not see the text in the page    Labour
    And the user should not see an error in the page

*** Keywords ***
If the user goes to the previous page he should redirect to the login page
    And the user goes back to the previous page
    Then the user should see the text in the page    New to this service?
