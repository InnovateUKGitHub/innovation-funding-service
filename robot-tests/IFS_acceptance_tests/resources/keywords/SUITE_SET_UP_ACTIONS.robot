*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
log in and create new application if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user

log in and create new application for collaboration if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Invite robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new invite application with the same user

Login new application invite academic
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    [Tags]    Email
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Academic robot test application
    Run Keyword If    '${status}' == 'FAIL'    Run keywords    Create new academic application with the same user
    ...    AND    Delete the emails from both test mailboxes
    ...    AND    Invite and accept the invitation        ${recipient}    ${subject}    ${pattern}
    ...    AND    the user closes the browser

new account complete all but one
    Run keyword if    ${smoke_test}!=1    create new account for submitting
    Run keyword if    ${smoke_test}!=1    create new submit application
    the user marks every section but one as complete

create new account for submitting
    Given the guest user opens the browser
    And the user navigates to the page    ${competition_details_url}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Hive IT
    And the user clicks the button/link    jQuery=.button:contains("Search")
    And the user clicks the button/link    link=HIVE IT LIMITED
    And the user selects the checkbox    id=address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user enters text to a text field    name=email    ${test_mailbox_one}+submittest@gmail.com
    And the user fills the create account form    Temur    Ketsbaia
    When the user opens the mailbox and reads his own email    ${test_mailbox_one}+submittest@gmail.com    Please verify your email address    If you did not request an account with us
    And the user clicks the button/link    jQuery=.button:contains("Sign in")

the user marks every section but one as complete
    Guest user log-in    ${submit_test_email}    Passw0rd123
    the user navigates to the page    ${server}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=Project summary
    the user marks the section as complete    11
    the user marks the section as complete    12
    the user marks the section as complete    13
    the user marks the section as complete    1
    the user marks the section as complete    2
    the user marks the section as complete    3
    the user marks the section as complete    4
    the user marks the section as complete    5
    the user marks the section as complete    6
    the user marks the section as complete    7
    the user marks the section as complete    8
    the user marks the section as complete    15
    the user marks the section as complete    16
    the user marks finances as complete

the user marks the section as complete
    [Arguments]    ${form-id}
    Wait Until Element Is Visible    css=#form-input-${form-id} .editor
    Input Text    css=#form-input-${form-id} .editor    Entering text to allow valid mark as complete
    Mouse Out    css=#form-input-${form-id} .editor
    sleep    200ms
    the user clicks the button/link    name=mark_as_complete
    the user clicks the button/link    css=.next

Create new application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("Yes I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

create new submit application
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+submittest@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    And the user clicks the button/link    jQuery=Label:contains("Yes I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    ${application_name}
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new invite application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("Yes I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invite robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new academic application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("Yes I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Academic robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Invite and accept the invitation
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user should see the text in the page    view team members and add collaborators
    When the user clicks the button/link    link=view team members and add collaborators
    And the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    And the user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    Input Text    name=organisations[1].organisationName    Academic Test
    Input Text    name=organisations[1].invites[0].personName    Arsene Wenger
    Input Text    name=organisations[1].invites[0].email    ${test_mailbox_one}+academictest@gmail.com
    focus    jquery=button:contains("Save Changes")
    And the user clicks the button/link    jquery=button:contains("Save Changes")
    And the user closes the browser
    And the guest user opens the browser
    When the user opens the mailbox and reads his own email    ${recipient}    ${subject}    ${pattern}
    And the user clicks the button/link    jQuery=.button:contains("Create")
    When the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user enters text to a text field    id=organisationSearchName    Liv
    And the user clicks the button/link    jQuery=.button:contains("Search")
    When the user clicks the button/link    link= University of Liverpool
    When the user clicks the button/link    jQuery=button:contains("Enter address manually")
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    The East Wing
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine2    Popple Manor
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine3    1, Popple Boulevard
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    Poppleton
    And the user enters text to a text field    id=addressForm.selectedPostcode.county    Poppleshire
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    POPPS123
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Arsene    Wenger
    And the user opens the mailbox and reads his own email    ${test_mailbox_one}+academictest@gmail.com    Please verify your email address    If you did not request an account with us
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123

Applicant navigates to the finances of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Your finances

The user redirects to the page
    [Arguments]    ${TEXT1}    ${TEXT2}
    Wait Until Keyword Succeeds    10    500ms    Page Should Contain    ${TEXT1}
    Page Should Contain    ${TEXT2}
    Page Should Not Contain    error
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible    id=global-header
    Page Should Contain    BETA

The user navigates to the summary page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Review & submit

The user navigates to the overview page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application

the user marks finances as complete
    the user clicks the button/link    jQuery=#otherFundingShowHideToggle label:contains(No) input
    the user selects the radio button    financePosition-organisationSize    LARGE
    the user enters text to a text field    id=cost-financegrantclaim    20
    the user selects the checkbox    id=agree-terms-page
    the user selects the checkbox    id=agree-state-aid-page
    the user moves focus to the element    jQuery=button:contains("Mark all as complete")
    the user clicks the button/link    jQuery=button:contains("Mark all as complete")
    Sleep    1s

the user marks the finances as complete
    the user selects the checkbox    id=agree-terms-page
    the user selects the checkbox    id=agree-state-aid-page
    the user moves focus to the element    jQuery=button:contains("Mark all as complete")
    the user clicks the button/link    jQuery=button:contains("Mark all as complete")
    Sleep    1s

Make the finances ready for mark as complete
    Applicant navigates to the finances of the Robot application
    the user selects the radio button    financePosition-organisationSize    SMALL
    The user clicks the button/link    jQuery=#otherFundingShowHideToggle label:contains(No) input

The user navigates to the academic application finances
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Your finances

The user navigates to the finance overview of the academic
    When the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user clicks the button/link    link=Finances overview

The user marks the academic application finances as incomplete
    When The user navigates to the academic application finances
    Focus    jQuery=button:contains("Edit")
    the user clicks the button/link    jQuery=button:contains("Edit")
    Sleep    1s
