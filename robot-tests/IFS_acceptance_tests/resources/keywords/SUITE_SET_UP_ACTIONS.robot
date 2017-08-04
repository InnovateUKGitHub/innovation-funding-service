*** Settings ***
Resource          ../defaultResources.robot

*** Keywords ***
log in and create new application if there is not one already
    Given the user logs-in in new browser  &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user  Robot test application

log in and create new application for collaboration if there is not one already
    Given the user logs-in in new browser  &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Invite robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user  Invite robot test application

Login new application invite academic
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    [Tags]    Email
    Given Logging in and Error Checking  &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Page Should Contain    Academic robot test application
    Run Keyword If    '${status}' == 'FAIL'    Run keywords    Create new application with the same user  Academic robot test application
    ...    AND    Invite and accept the invitation    ${recipient}    ${subject}    ${pattern}

new account complete all but one
    Run keyword if    ${smoke_test}!=1    create new account for submitting
    Run keyword if    ${smoke_test}!=1    create new submit application
    the user marks every section but one as complete

create new account for submitting
    Given the guest user opens the browser
    the user navigates to the page              ${COMPETITION_OVERVIEW_URL_2}  # This comp forces RTO lead!
    the user clicks the button/link             jQuery=a:contains("Start new application")
    the user clicks the button/link             jQuery=a:contains("Create account")
    And the user enters text to a text field    id=organisationSearchName    Hive IT
    And the user clicks the button/link         jQuery=.button:contains("Search")
    And the user clicks the button/link         link=${PROJECT_SETUP_APPLICATION_1_ADDITIONAL_PARTNER_NAME}
    And the user selects the checkbox           address-same
    And the user clicks the button/link         jQuery=.button:contains("Continue")
    And the user clicks the button/link         jQuery=.button:contains("Save and continue")
    And the user selects the radio button       organisationTypeId  radio-1
    And the user clicks the button/link         jQuery=.button:contains("Save and continue")
    And the user clicks the button/link         jQuery=.button:contains("Save and continue")
    And the user enters text to a text field    name=email    ${test_mailbox_one}+submittest@gmail.com
    And the user fills the create account form    Temur    Ketsbaia
    When the user reads his email and clicks the link    ${test_mailbox_one}+submittest@gmail.com    Please verify your email address    Once verified you can sign into your account
    And the user clicks the button/link        jQuery=.button:contains("Sign in")

the user marks every section but one as complete
    the user navigates to the page    ${server}
    the user clicks the button/link    link=${application_name}
    the user clicks the button/link    link=Project summary
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=Public description
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=Scope
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=1. Business opportunity
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=2. Potential market
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=3. Project exploitation
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=4. Economic benefit
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=5. Technical approach
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=6. Innovation
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=7. Risks
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=8. Project team
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=9. Funding
    the user marks the section as complete
    the user clicks the button/link    link=Application overview
    the user clicks the button/link    link=10. Adding value
    the user marks the section as complete

the user marks the section as complete
    Wait Until Element Is Visible Without Screenshots    css=.textarea-wrapped .editor
    Input Text    css=.textarea-wrapped .editor    Entering text to allow valid mark as complete
    Mouse Out    css=.textarea-wrapped .editor
    wait for autosave
    the user clicks the button/link    name=mark_as_complete
    #the user clicks the button/link    css=.next

Create new application with the same user
    [Arguments]  ${Application_title}
    When the user navigates to the page         ${COMPETITION_OVERVIEW_URL}
    the user clicks the button/link             jQuery=a:contains("Start new application")
    And the user clicks the button/link         jQuery=Label:contains("Yes, I want to create a new application.")
    And the user clicks the button/link         jQuery=.button:contains("Continue")
    And the user clicks the button/link         jQuery=a:contains("Begin application")
    And the user clicks the button/link         link=Application details
    And the user enters text to a text field    id=application_details-title    ${Application_title}
    And the user clicks the button/link         jQuery=button:contains("Save and return")

create new submit application
    When the user navigates to the page                 ${COMPETITION_OVERVIEW_URL}
    the user clicks the button/link                     jQuery=a:contains("Start new application")
    And the user clicks the button/link                 jQuery=p ~ a:contains("Sign in")
    And The guest user inserts user email and password  ${test_mailbox_one}+submittest@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    And the user clicks the button/link                 jQuery=a:contains("Begin application")
    And the user clicks the button/link                 link=Application details
    And the user enters text to a text field            id=application_details-title    ${application_name}
    And the user clicks the button/link                 jQuery=button:contains("Save and return")

Invite and accept the invitation
    [Arguments]    ${recipient}    ${subject}    ${pattern}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user should see the text in the page    view team members and add collaborators
    When the user clicks the button/link    link=view team members and add collaborators
    And the user clicks the button/link    jQuery=a:contains("Add partner organisation")
    And the user enters text to a text field    name=organisationName    Academic Test
    And the user enters text to a text field    name=applicants[0].name    Arsene Wenger
    And the user enters text to a text field    name=applicants[0].email    ${test_mailbox_one}+academictest@gmail.com
    And the user clicks the button/link    jQuery=button:contains("Add organisation and invite applicants")
    And logout as user
    When the user reads his email and clicks the link    ${recipient}    ${subject}    ${pattern}    3
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
    And the user clicks the button/link    jQuery=.button:contains("Save and continue")
    And the user fills the create account form    Arsene    Wenger
    And the user reads his email and clicks the link    ${test_mailbox_one}+academictest@gmail.com    Please verify your email address    We now need you to verify your email address
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And Logging in and Error Checking      ${test_mailbox_one}+academictest@gmail.com    ${correct_password}

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
    the user navigates to the academic application finances
    the user clicks the button/link    link=Your project costs
    Focus    jQuery=button:contains("Edit")
    the user clicks the button/link    jQuery=button:contains("Edit")
    wait for autosave

invite a registered user
    [Arguments]    ${EMAIL_LEAD}    ${EMAIL_INVITED}
    the user navigates to the page                           ${COMPETITION_OVERVIEW_URL}
    the user follows the flow to register their organisation
    the user verifies email                                    Stuart   Anderson    ${EMAIL_LEAD}
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
    [Arguments]    ${COMPETITION_ID}  ${first_name}  ${last_name}  ${EMAIL_INVITED}
    the user navigates to the page                             ${SERVER}/competition/${COMPETITION_ID}/overview/
    the user follows the flow to register their organisation
    the user verifies email    ${first_name}   ${last_name}    ${EMAIL_INVITED}

the user verifies email
    [Arguments]    ${first_name}  ${last_name}  ${EMAIL_INVITED}
    The user enters the details and clicks the create account  ${first_name}  ${last_name}  ${EMAIL_INVITED}
    The user should be redirected to the correct page          ${REGISTRATION_SUCCESS}
    the user reads his email and clicks the link               ${EMAIL_INVITED}  Please verify your email address  Once verified you can sign into your account
    The user should be redirected to the correct page          ${REGISTRATION_VERIFIED}
    The user clicks the button/link                            jQuery=.button:contains("Sign in")
    The guest user inserts user email and password             ${EMAIL_INVITED}  ${correct_password}
    The guest user clicks the log-in button

the user follows the flow to register their organisation
    the user clicks the button/link             jQuery=a:contains("Start new application")
    the user clicks the button/link             jQuery=a:contains("Create account")
    the user enters text to a text field        id=organisationSearchName    Innovate
    the user clicks the button/link             id=org-search
    the user clicks the button/link             link=INNOVATE LTD
    the user selects the checkbox               address-same
    the user should not see the element         jQuery=h3:contains("Organisation type")
    the user clicks the button/link             jQuery=.button:contains("Continue")
#    This is added as the flow differs if comp is setup to have either Business or RTO as lead org type but not both
    run keyword and ignore error  the user selects the radio button    organisationTypeId   radio-1
    the user clicks the button/link             jQuery=.button:contains("Save and continue")
    run keyword and ignore error  the user clicks the button/link             jQuery=.button:contains("Save and continue")

the user enters the details and clicks the create account
    [Arguments]   ${first_name}  ${last_name}  ${REG_EMAIL}
    Wait Until Page Contains Element Without Screenshots    link=terms and conditions
    Page Should Contain Element    xpath=//a[contains(@href, '/info/terms-and-conditions')]
    Input Text    id=firstName      ${first_name}
    Input Text    id=lastName       ${last_name}
    Input Text    id=phoneNumber    23232323
    Input Text    id=email          ${REG_EMAIL}
    Input Password    id=password    ${correct_password}
    the user selects the checkbox    termsAndConditions
    the user selects the checkbox    allowMarketingEmails
    Submit Form

the user fills the create account form
    [Arguments]    ${NAME}    ${LAST_NAME}
    Input Text    id=firstName    ${NAME}
    Input Text    id=lastName    ${LAST_NAME}
    Input Text    id=phoneNumber    0612121212
    Input Password    id=password    ${correct_password}
    the user selects the checkbox    termsAndConditions
    the user clicks the button/link    jQuery=.button:contains("Create account")

the user clicks the forgot psw link
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    click element    link=forgot your password?
    Run Keyword If    '${status}' == 'FAIL'    click element    jQuery=summary:contains("Need help signing in or creating an account?")
    Run Keyword If    '${status}' == 'FAIL'    click element    link=Forgotten your password?

Close browser and delete emails
    Close any open browsers
    Delete the emails from both test mailboxes
