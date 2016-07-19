*** Settings ***
Documentation     INFUND-2607 As an applicant I want to have a link to the feedback for my application from the Application Overview page when it becomes available so I can review the assessor feedback for my application
...
...               INFUND-2612 As a partner I want to have a overview of where I am in the process and what outstanding tasks I have to complete so that I can understand our project setup steps
...
...
...               INFUND-2613 As a lead partner I need to see an overview of project details for my project so that I can edit the project details in order for Innovate UK to be able to assign an appropriate Monitoring Officer
...
...               INFUND-2614 As a lead partner I need to provide a target start date for the project so that Innovate UK has correct details for my project setup
...
...               INFUND-2620 As a partner I want to provide my organisation's finance contact details so that the correct person is assigned to the role
...
...               INFUND-2630 As a Competitions team member I want to be able to enter the details of a Monitoring Officer to assign them to the project and share their contact details with the consortium
...
...               INFUND-2632 As a Competitions team member I want to send an email to a Monitoring Officer so they are aware I have assigned them to a project
...
...               INFUND-3010 As a partner I want to be able to supply bank details for my business so that Innovate UK can verify its suitability for funding purposes
...
...               INFUND-3282 As a partner I want to be able to supply an existing or new address for my bank account to support the bank details verification process
Suite Setup       Run Keywords    delete the emails from both test mailboxes
Suite Teardown    the user closes the browser
Force Tags        Comp admin    Upload
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${successful_application_overview}    ${server}/application/16
${unsuccessful_application_overview}    ${server}/application/17
${successful_application_comp_admin_view}    ${server}/management/competition/3/application/16
${unsuccessful_application_comp_admin_view}    ${server}/management/competition/3/application/17
${Successful_Monitoring_Officer_Page}    ${server}/management/project/4/monitoring-officer

*** Test Cases ***



Comp admin can view uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    HappyPath
    [Setup]
    Given guest user log-in    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${successful_application_comp_admin_view}
    And the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]     The user goes back to the previous page


Comp admin can view unsuccessful uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    Given the user navigates to the page    ${unsuccessful_application_comp_admin_view}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    And the user navigates to the page    ${unsuccessful_application_comp_admin_view}
    [Teardown]    Logout as user

Unsuccessful applicant can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    [Setup]    guest user log-in    worth.email.test.two+fundfailure@gmail.com    Passw0rd
    Given the user navigates to the page    ${unsuccessful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]    the user navigates to the page    ${unsuccessful_application_comp_admin_view}

Unsuccessful applicant cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove


Unsuccessful applicant can download the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    Pending
    # TODO Pending until download functionality has been plugged in
    Given the user should see the text in the page    ${valid_pdf}
    When the user downloads the file from the link    ${valid_pdf}    ${download_link}
    Then the file should be downloaded    ${valid_pdf}
    [Teardown]    Remove File    ${valid_pdf}


Partner can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    HappyPath
    Given guest user log-in    worth.email.test+fundsuccess@gmail.com    Passw0rd
    And the user navigates to the page    ${successful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]    the user navigates to the page    ${successful_application_overview}

Partner cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove

Partner can download the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    Pending    HappyPath
    # TODO Pending until download functionality has been plugged in
    Given the user should see the text in the page    ${valid_pdf}
    When the user downloads the file from the link    ${valid_pdf}    ${download_link}
    Then the file should be downloaded    ${valid_pdf}
    [Teardown]    Remove File    ${valid_pdf}

Partner can see the project setup page
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    When the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE}
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application Cheese is good has been successful within the La Fromage competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Other documents

Lead partner can see the overview of the project details
    [Documentation]    INFUND-2613
    [Tags]    HappyPath
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Please supply the following details for your project and the team
    And the user should see the element    link=Start date
    And the user should see the element    link=Project address
    And the user should see the element    link=Project manager
    And the user should see the text in the page    Finance contacts

Submit button is disabled if the details are not fully filled out
    [Documentation]    INFUND-3467
    [Tags]    Pending
    # TODO
    When the user should see the element    xpath=//span[contains(text(), 'No')]
    Then the submit button should be disabled

Partner nominates a finance contact
    [Documentation]    INFUND-3162
    [Tags]    HappyPath
    [Setup]    Logout as user
    When Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${SUCCESSFUL_PROJECT_PAGE}
    And the matching status checkbox is updated    project-details-finance    1    yes
    Then Logout as user
    When Log in as user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${SUCCESSFUL_PROJECT_PAGE}
    And the matching status checkbox is updated    project-details-finance    2    yes
    Then Logout as user
    When Log in as user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    Then the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Cheeseco
    Then the user should see the text in the page    Finance contact
    And the user selects the radio button    financeContact    financeContact2
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${SUCCESSFUL_PROJECT_PAGE}
    And the matching status checkbox is updated    project-details-finance    3    yes

Lead partner can change the Start Date
    [Documentation]    INFUND-2614
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Start date
    And the duration should be visible
    When the user enters text to a text field    id=projectStartDate_year    2013
    And the user enters text to a text field    id=projectStartDate_month    1
    Then the user should see a validation error    Please enter a future date
    And the user shouldn't be able to edit the day field as all projects start on the first of the month
    When the user enters text to a text field    id=projectStartDate_month    1
    When the user enters text to a text field    id=projectStartDate_year    2018
    When the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see the text in the page    1 Jan 2018
    Then status of the start date should be Yes

Lead partner can change the project manager
    [Documentation]    INFUND-2616, INFUND-2996
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Project manager
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see a validation error    You need to select a Project Manager before you can continue
    When the user selects the radio button    projectManager    27
    And the user should not see the text in the page      You need to select a Project Manager before you can continue
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    test ten
    Then the user should be redirected to the correct page    ${SUCCESSFUL_PROJECT_PAGE}
    And the matching status checkbox is updated    project-details    3    yes

Lead partner can change the project address
    [Documentation]    INFUND-3157, INFUND-2165
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Project address
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    You need to select a project address before you can continue
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user should see the address data
    When the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    1 Cheese Road, Bath, BA1 5LR

Project details submission flow
    [Documentation]    INFUND-3467
    [Tags]    HappyPath
    When The user should not see the element    xpath=//span[contains(text(), 'No')]
    And the applicant clicks the submit button and the clicks cancel in the submit modal
    And the user should not see the text in the page      The project details have been submitted to Innovate UK
    Then the applicant clicks the submit button in the modal
    And the user should see the text in the page    The project details have been submitted to Innovate UK
    Then the user navigates to the page    ${successful_project_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(2)

Project details submitted is read only
    [Documentation]    INFUND-3467
    [Tags]
    When the user clicks the button/link    link=Project details
    Then The user should not see the element    xpath=//span[contains(text(), 'No')]
    And The user should not see the element    link=Start date
    And The user should not see the element    link=Project address
    And The user should not see the element    link=Project manager
    And The user should not see the element    link=Ludlow
    And The user should not see the element    link=EGGS
    And The user should not see the element    link=Cheeseco

Non-lead partner cannot change any project details
    [Documentation]    INFUND-2619
    [Setup]    Run Keywords    logout as user
    ...    AND    guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user navigates to the page    ${successful_project_page}
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Start date
    And the user should see the text in the page    1 Jan 2018
    And the user should not see the element    link=Start date
    And the user should see the text in the page    Project manager
    And the user should see the text in the page    Steve Smith
    And the user should not see the element    link=Project manager
    And the user should see the text in the page    Project address
    And the user should see the text in the page    1 Cheese Road, Bath, BA1 5LR
    And the user should not see the element    link=Project address
    And the user navigates to the page    ${project_start_date_page}
    And the user should be redirected to the correct page    ${successful_project_page}
    And the user navigates to the page    ${project_manager_page}
    And the user should be redirected to the correct page    ${successful_project_page}
    And the user navigates to the page    ${project_address_page}
    And the user should be redirected to the correct page    ${successful_project_page}


Bank details server side validations
    [Documentation]    INFUND-3010
    [Tags]
    [Setup]   logout as user
    Given guest user log-in       steve.smith@empire.com     Passw0rd
    And the user clicks the button/link      link=00000004: Cheese is good
    And the user clicks the button/link     link=Bank details
    When the user clicks the button/link     jQuery=.button:contains("Submit bank account details")
    Then the user should see an error       Please enter an account number
    And the user should see an error        Please enter a sort code
    And the user should see an error        You need to select a bank address before you can continue


Bank details client side validations
    [Documentation]     INFUND-3010
    [Tags]
    When the user enters text to a text field     name=accountNumber      1234567
    And the user moves focus away from the element     name=accountNumber
    Then the user should not see the text in the page        Please enter an account number
    And the user should see an error       Please enter a valid account number
    When the user enters text to a text field      name=accountNumber    12345678
    And the user moves focus away from the element     name=accountNumber
    Then the user should not see the text in the page    Please enter an account number
    And the user should not see the text in the page     Please enter a valid account number
    When the user enters text to a text field      name=sortCode     12345
    And the user moves focus away from the element    name=sortCode
    Then the user should see an error     Please enter a valid sort code
    When the user enters text to a text field     name=sortCode    123456
    And the user moves focus away from the element      name=sortCode
    Then the user should not see the text in the page      Please enter a sort code
    And the user should not see the text in the page      Please enter a valid sort code
    When the user selects the radio button      addressType    REGISTERED
    Then the user should not see the text in the page      You need to select a bank address before you can continue


Bank account postcode lookup
    [Documentation]    INFUND-3282
    [Tags]
    When the user selects the radio button     addressType   ADD_NEW
    When the user enters text to a text field    name=addressForm.postcodeInput    ${EMPTY}
    # the following two steps have been commented out as they are
    # Pending due to INFUND-4043
    # And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    # Then the user should see the element    css=.form-label .error-message
    When the user enters text to a text field    name=addressForm.postcodeInput    BS14NT/
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    name=addressForm.selectedPostcodeIndex
    When the user selects the radio button      addressType    ADD_NEW
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled


Bank details submission
    [Documentation]     INFUND-3010
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link    jquery=button:contains("Cancel")
    And the user should not see the text in the page      Your bank account details have been submitted to Innovate UK
    When the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link    jquery=button:contains("Submit")
    And the user should see the text in the page    Your bank account details have been submitted to Innovate UK
    And the user should see the element       css=.success-alert
    Then the user navigates to the page    ${successful_project_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(2)

Before Monitoring Officer is assigned
    [Documentation]    INFUND-3349
    [Tags]    Pending    HappyPath
    # TODO Pending due to INFUND-3963
    Given the user navigates to the page    ${successful_project_page}
    When the user clicks the button/link      link=Monitoring Officer
    Then the user should see the text in the page    Your project has not yet been assigned a Monitoring Officer.
    And the user should not see the element    [element to check the green tick mark is not there]

Comp admin can view the Supporting information details on MO page
    [Documentation]    INFUND-3330
    [Tags]    Pending    HappyPath
    [Setup]    Log in as user    &{Comp_admin1_credentials}
    # TODO Pending due to work in progress
    When the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    Cheese is good
    And the user should see the text in the page    Earth Observation
    And the user should see the text in the page    1 Cheese Road, Bath, BA1 5LR
    And the user should see the text in the page    1st Oct 2020
    And the user should see the text in the page    Steve Smith
    And the user should see the text in the page    Cheeseco
    And the user should see the text in the page    Ludlow
    And the user should see the text in the page    EGGS

MO client-side validation
    [Documentation]    INFUND-3330
    [Tags]    Pending    HappyPath
    # TODO Pending due to work in progress

MO server-side validation
    [Documentation]    INFUND-3330
    [Tags]    Pending    HappyPath
    # TODO Pending due to work in progress
    When the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")


MO details can be added and updated
    [Documentation]    INFUND-3330, INFUND-3334
    [Tags]
    Given guest user log-in    john.doe@innovateuk.test    Passw0rd
    And the user navigates to the page  ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    Monitoring Officer
    When the user enters text to a text field    id=firstName    Pradha
    And the user enters text to a text field    id=lastName    Muniraj
    And the user enters text to a text field    id=emailAddress    ${test_mailbox_one}+monitoringofficer@gmail.com
    And the user enters text to a text field    id=phoneNumber    07438620303
    Then the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    And the user clicks the button/link    jQuery=.modal-assign-mo button:contains("Assign Monitoring Officer")
    Then the user should see the text in the page    A Monitoring Officer has been assigned.
    And Open mailbox and confirm received email    ${test_mailbox_one}+monitoringofficer@gmail.com    testtest1    has been assigned to you

MO details can be edited and updated
    [Documentation]    INFUND-3330
    [Tags]    Pending
    # TODO Pending due to work in progress
    Given the user clicks the button/link    link=Change Monitoring Officer
    And the user enters text to a text field    id=firstName    Pradha
    And the user enters text to a text field    id=lastName    Jagankumar
    And the user enters text to a text field    id=emailAddress    pradha.raj@gmail.com
    And the user enters text to a text field    id=phoneNumber    08549731414
    And the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    And the user reloads the page
    Then the edited text should be visible
    # TODO [Add step to check the status change in PS page]

MO details can be viewed on the page after editting
    [Documentation]    INFUND-3330
    [Tags]    Pending
    # TODO Pending due to work in progress
    Given the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    Pradha
    And the user should see the text in the page    Jagankumar
    And the user should see the text in the page    pradha.raj@gmail.com
    And the user should see the text in the page    08549731414

MO details accessible/seen by all partners
    [Documentation]    INFUND-3349
    [Tags]    Pending    HappyPath
    # TODO Pending due to work in progress
    Given Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    [greeen tick mark element]
    And the user should see the text in the page    Your project has been assigned a Monitoring Officer
    And the user should see the text in the page    Pradha Jagankumar
    And the user should see the text in the page    pradha.raj@gmail.com
    And the user should see the text in the page    08549731414

*** Keywords ***
the user should see a validation error
    [Arguments]    ${ERROR1}
    Focus    jQuery=button:contains("Save")
    sleep    300ms
    Then the user should see an error    ${ERROR1}

the matching status checkbox is updated
    [Arguments]    ${id}    ${COLUMN}    ${STATUS}
    the user should see the element    ${id}
    the user should see the element    jQuery=#${id} tr:nth-of-type(${COLUMN}) .${STATUS}

status of the start date should be Yes
    Element Should Contain    id=start-date-status    Yes

the duration should be visible
    Element Should Contain    xpath=//*[@id="content"]/form/fieldset/div/p[5]/strong    3 months

the user shouldn't be able to edit the day field as all projects start on the first of the month
    the user should see the element    css=.day [readonly]

the user should see the address data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' != 'NO'    the user should see the valid data
    Run Keyword If    '${POSTCODE_LOOKUP_IMPLEMENTED}' == 'NO'    the user should see the dummy data

the user should see the valid data
    the user should see the text in the page    Am Reprographics, Bristol, BS1 4NT

the user should see the dummy data
    the user should see the text in the page    Montrose House 1, Neston, CH64 3RU

the submit button should be disabled
    Element Should Be Disabled    jQuery=.button:contains("Submit project details")

standard verification for email address follows
    When the user enters text to a text field    id=emailAddress    ${EMPTY}
    Then the user should see an error    Please enter your email
    And the user enters text to a text field    id=emailAddress    ${invalid_email_plain}
    Then the user should see an error    Please enter a valid email address
    And the user enters text to a text field    id=emailAddress    ${invalid_email_symbols}
    Then the user should see an error    Please enter a valid email address
    And the user enters text to a text field    id=emailAddress    ${invalid_email_no_username}
    Then the user should see an error    Please enter a valid email address
    And the user enters text to a text field    id=emailAddress    ${invalid_email_format}
    Then the user should see an error    Please enter a valid email address
    And the user enters text to a text field    id=emailAddress    ${invalid_email_no_at}
    Then the user should see an error    Please enter a valid email address
    And the user enters text to a text field    id=emailAddress    ${valid_email}
    Then the user should see an error    Email address is already in use

standard verification for Phone number follows
    When the user enters text to a text field    id=phoneNumber    ${EMPTY}
    Then the user should see an error    Please enter a phone number
    And the user enters text to a text field    id=phoneNumber    invalidphone
    Then the user should see an error    Please enter a valid phone number
    And the user enters text to a text field    id=phoneNumber    0123
    Then the user should see an error    Input for your phone number has a minimum length of 8 characters

the applicant clicks the submit button and the clicks cancel in the submit modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jquery=button:contains("Cancel")

the applicant clicks the submit button in the modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=.button:contains("Submit project details")
    the user clicks the button/link    jQuery=button:contains("Submit")



the user moves focus away from the element
    [Arguments]    ${element}
    mouse out       ${element}
    focus         jQuery=.button:contains("Submit bank account details")