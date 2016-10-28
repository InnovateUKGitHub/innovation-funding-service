*** Settings ***
Documentation     INNFUND-669 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
...
...               INFUND-1163 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
...
...               INFUND-1904 As a user registering an account and submitting the data I expect to receive a verification email so I can be sure that the provided email address is correct
...
...               INFUND-1920 As an applicant once I am accessing my dashboard and clicking on the newly created application for the first time, it will allow me to invite contributors and partners
Suite Setup       Delete the emails from both test mailboxes
Test Teardown     The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${APPLICATION_DETAILS_APPLICATION8}    ${SERVER}/application/8/form/question/9

*** Test Cases ***
Non registered users CH route
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1904
    ...
    ...    INFUND-1920
    ...
    ...    INFUND-1785
    [Tags]    HappyPath    SmokeTest
    [Setup]    The guest user opens the browser
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    LINK=INNOVATE LTD
    And the user selects the checkbox    id=address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details and clicks the create account    ${test_mailbox_one}+${unique_email_number}@gmail.com
    And the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}

Non registered users CH route (email step)
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1904
    ...
    ...    INFUND-1920
    ...
    ...    INFUND-1785
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    The guest user opens the browser
    Given the user reads his email and clicks the link    ${test_mailbox_one}+${unique_email_number}@gmail.com    Please verify your email address    If you did not request an account with us
    And the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+${unique_email_number}@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Your dashboard
    And the user clicks the button/link    link=${OPEN_COMPETITION_LINK}
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user should see the text in the page    Application overview

The email address does not stay in the cookie
    [Documentation]    INFUND_2510
    [Tags]
    [Setup]    The guest user opens the browser
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=INNOVATE LTD
    And the user selects the checkbox    id=address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should not see the text in the page    ${test_mailbox_one}+1@gmail.com

Non registered users non CH route
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1904
    ...
    ...    INFUND-1920
    [Tags]    HappyPath
    [Setup]    The guest user opens the browser
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user clicks the Not on company house link
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details and clicks the create account    ${test_mailbox_one}+2@gmail.com
    And the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}

Non registered users non CH route (email step)
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1904
    ...
    ...    INFUND-1920
    [Tags]    Email    HappyPath
    [Setup]    The guest user opens the browser
    Given the user reads his email and clicks the link    ${test_mailbox_one}+2@gmail.com    Please verify your email address    If you did not request an account with us
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+2@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    Your dashboard
    And the user clicks the button/link    link=${OPEN_COMPETITION_LINK}
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user should see the text in the page    Application overview

Verify the name of the new application
    [Documentation]    INFUND-669
    ...
    ...    INFUND-1163
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    The guest user opens the browser
    When guest user log-in    ${test_mailbox_one}+${unique_email_number}@gmail.com    Passw0rd123
    And the user edits the competition title
    Then the user should see the text in the page    ${test_title}
    And the progress indicator should show 0
    And the user clicks the button/link    link=view team members and add collaborators
    And the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your contributors and partners
    And the new application should be visible in the dashboard page
    And the user clicks the button/link    link=${test_title}
    And the user should see the text in the page    ${test_title}

Special Project Finance role
    [Documentation]    INFUND-2609
    [Tags]
    [Setup]    The guest user opens the browser
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user clicks the Not on company house link
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters the details and clicks the create account    worth.email.test+project.finance1@gmail.com
    And the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}

Special Project Finance role (email step)
    [Documentation]    INFUND-2609
    [Tags]    Email
    [Setup]    The guest user opens the browser
    Given the user reads his email from the default mailbox and clicks the link    worth.email.test+project.finance1@gmail.com    Please verify your email address    If you did not request an account with us
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    worth.email.test+project.finance1@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should be redirected to the correct page without error checking    ${COMP_ADMINISTRATOR_DASHBOARD}/live
    [Teardown]    Logout as user

*** Keywords ***
the new application should be visible in the dashboard page
    the user clicks the button/link    link= My dashboard
    sleep    500ms
    the user should see the text in the page    ${test_title}
    the user should see the text in the page    Application number: 0000

the user clicks the Not on company house link
    the user clicks the button/link    name=not-in-company-house
    the user clicks the button/link    name=manual-address
    The user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    street
    The user enters text to a text field    id=addressForm.selectedPostcode.town    town
    The user enters text to a text field    id=addressForm.selectedPostcode.county    country
    The user enters text to a text field    id=addressForm.selectedPostcode.postcode    post code
    The user enters text to a text field    name=organisationName    org2
    the user clicks the button/link    jQuery=.button:contains("Continue")

the user edits the competition title
    the user clicks the button/link    link=${OPEN_COMPETITION_LINK}
    the user should see the element    link=Application details
    the user clicks the button/link    link=Application details
    The user enters text to a text field    id=application_details-title    ${test_title}
    the user clicks the button/link    jQuery=button:contains("Save and return")

the progress indicator should show 0
    Element Should Contain    css=.progress-indicator    0
