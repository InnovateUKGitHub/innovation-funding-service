*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
log in and create new application if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user

log in and create new application for collaboration if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Invite robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new invite application with the same user

Login new application invite academic
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    [Tags]    Email
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Academic robot test application
    Run Keyword If    '${status}' == 'FAIL'    Run keywords    Create new academic application with the same user
    ...    AND    Delete the emails from both test mailboxes
    ...    AND    Invite and accept the invitation    ${recipient}    ${subject}    ${pattern}
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
    And the user selects the checkbox    address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Confirm")
    And the user enters text to a text field    name=email    ${test_mailbox_one}+submittest@gmail.com
    And the user fills the create account form    Temur    Ketsbaia
    When the user reads his email and clicks the link    ${test_mailbox_one}+submittest@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user clicks the button/link    jQuery=.button:contains("Sign in")

the user marks every section but one as complete
    Guest user log-in    ${submit_test_email}    ${correct_password}
    the user navigates to the page    ${server}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=Project summary
    the user marks the section as complete    1039
    the user marks the section as complete    1040
    the user marks the section as complete    1041
    the user marks the section as complete    1045
    the user marks the section as complete    1049
    the user marks the section as complete    1053
    the user marks the section as complete    1057
    the user marks the section as complete    1061
    the user marks the section as complete    1065
    the user marks the section as complete    1069
    the user marks the section as complete    1073
    the user marks the section as complete    1077
    the user marks the section as complete    1081

the user marks the section as complete
    [Arguments]    ${form-id}
    Wait Until Element Is Visible Without Screenshots    css=#form-input-${form-id} .editor
    Input Text    css=#form-input-${form-id} .editor    Entering text to allow valid mark as complete
    Mouse Out    css=#form-input-${form-id} .editor
    wait for autosave
    the user clicks the button/link    name=mark_as_complete
    the user clicks the button/link    css=.next

Create new application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=a:contains("Begin application")
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
    And the user clicks the button/link    jQuery=Label:contains("I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=a:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    ${application_name}
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new invite application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=a:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invite robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new academic application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=Label:contains("I want to create a new application")
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=a:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Academic robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Invite and accept the invitation
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user should see the text in the page    view team members and add collaborators
    When the user clicks the button/link    link=view team members and add collaborators
    And the user clicks the button/link    jQuery=a:contains("Add partner organisation")
    And the user enters text to a text field    name=organisationName    Academic Test
    And the user enters text to a text field    name=applicants[0].name     Arsene Wenger
    And the user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+academictest@gmail.com
    And the user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    And the user closes the browser
    And the guest user opens the browser
    When the user reads his email and clicks the link    ${recipient}    ${subject}    ${pattern}   3
    And the user clicks the button/link    jQuery=.button:contains("Yes, accept invitation")
    When the user selects the radio button    organisationType    2
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
    And the user clicks the button/link    jQuery=.button:contains("Confirm")
    And the user fills the create account form    Arsene    Wenger
    And the user reads his email and clicks the link  ${test_mailbox_one}+academictest@gmail.com  Please verify your email address  We now need you to verify your email address
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in    ${test_mailbox_one}+academictest@gmail.com  ${correct_password}

The user redirects to the page
    [Arguments]    ${TEXT1}    ${TEXT2}
    Wait Until Keyword Succeeds Without Screenshots    10    500ms    Page Should Contain    ${TEXT1}
    Page Should Contain    ${TEXT2}
    Page Should Not Contain    error
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible Without Screenshots    id=global-header
    Page Should Contain    BETA

The user navigates to the summary page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Review and submit

The user navigates to the overview page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application

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
    wait for autosave

invite a registered user
    [Arguments]    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    the guest user opens the browser
    the user navigates to the page    ${COMPETITION_DETAILS_URL}
    the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    the user clicks the button/link    jQuery=.button:contains("Create account")
    the user clicks the button/link    jQuery=.button:contains("Create")
    the user enters text to a text field    id=organisationSearchName    Innovate
    the user clicks the button/link    id=org-search
    the user clicks the button/link    LINK=INNOVATE LTD
    the user selects the checkbox    address-same
    the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    the user clicks the button/link    jQuery=.button:contains("Confirm")
    the user enters the details and clicks the create account    ${EMAIL_LEAD}
    the user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    the user reads his email and clicks the link    ${EMAIL_LEAD}    Please verify your email address    Once verified you can sign into your account
    the user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    the user clicks the button/link    jQuery=.button:contains("Sign in")
    the guest user inserts user email & password    ${EMAIL_LEAD}  ${correct_password}
    the guest user clicks the log-in button
    the user clicks the button/link    link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link    jQuery=a:contains("Add partner organisation")
    the user enters text to a text field    name=organisationName    innovate
    the user enters text to a text field    name=applicants[0].name    Partner name
    the user enters text to a text field    name=applicants[0].email    ${EMAIL_INVITED}
    the user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    the user clicks the button/link    jQuery=a:contains("Begin application")
    the user should see the text in the page    Application overview
    the user closes the browser
    the guest user opens the browser

we create a new user
    [Arguments]    ${EMAIL_INVITED}
    The user navigates to the page    ${COMPETITION_DETAILS_URL}
    The user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    The user clicks the button/link    jQuery=.button:contains("Create account")
    The user clicks the button/link    jQuery=.button:contains("Create")
    The user enters text to a text field    id=organisationSearchName    Innovate
    The user clicks the button/link    id=org-search
    The user clicks the button/link    LINK=INNOVATE LTD
    The user selects the checkbox    address-same
    The user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    The user clicks the button/link    jQuery=.button:contains("Confirm")
    The user enters the details and clicks the create account    ${EMAIL_INVITED}
    The user should be redirected to the correct page    ${REGISTRATION_SUCCESS}
    the user reads his email and clicks the link    ${EMAIL_INVITED}    Please verify your email address    Once verified you can sign into your account
    The user should be redirected to the correct page    ${REGISTRATION_VERIFIED}
    The user clicks the button/link    jQuery=.button:contains("Sign in")
    The guest user inserts user email & password    ${EMAIL_INVITED}    Passw0rd123
    The guest user clicks the log-in button
    the user closes the browser

the user follows the flow to register their organisation
    Given the user navigates to the page    ${COMPETITION_DETAILS_URL}
    When the user clicks the button/link    jQuery=.column-third .button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQuery=.button:contains("Create")
    And the user enters text to a text field    id=organisationSearchName    Innovate
    And the user clicks the button/link    id=org-search
    And the user clicks the button/link    link=INNOVATE LTD
    And the user selects the checkbox    address-same
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Confirm")

the user enters the details and clicks the create account
    [Arguments]    ${REG_EMAIL}
    Wait Until Page Contains Element Without Screenshots    link=terms and conditions
    Page Should Contain Element    xpath=//a[contains(@href, '/info/terms-and-conditions')]
    Input Text    id=firstName    Stuart
    Input Text    id=lastName    ANDERSON
    Input Text    id=phoneNumber    23232323
    Input Text    id=email    ${REG_EMAIL}
    Input Password    id=password    Passw0rd123
    Input Password    id=retypedPassword    Passw0rd123
    the user selects the checkbox  termsAndConditions
    Submit Form

the user fills the create account form
    [Arguments]    ${NAME}    ${LAST_NAME}
    Input Text    id=firstName    ${NAME}
    Input Text    id=lastName    ${LAST_NAME}
    Input Text        id=phoneNumber    0612121212
    Input Password    id=password    Passw0rd123
    Input Password    id=retypedPassword    Passw0rd123
    the user selects the checkbox  termsAndConditions
    the user clicks the button/link  jQuery=.button:contains("Create account")
