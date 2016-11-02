*** Settings ***
Documentation     INFUND-2612 As a partner I want to have a overview of where I am in the process and what outstanding tasks I have to complete so that I can understand our project setup steps
...
...               INFUND-2613 As a lead partner I need to see an overview of project details for my project so that I can edit the project details in order for Innovate UK to be able to assign an appropriate Monitoring Officer
...
...               INFUND-2614 As a lead partner I need to provide a target start date for the project so that Innovate UK has correct details for my project setup
...
...               INFUND-2620 As a partner I want to provide my organisation's finance contact details so that the correct person is assigned to the role
...
...               INFUND-3382 As a partner I want to be able to view our project details after they have been submitted so that I can use them for reference
...
...               INFUND-2621 As a contributor I want to be able to review the current Project Setup status of all partners in my project so I can get an indication of the overall status of the consortium
...
...               INFUND-4583 As a partner I want to be able to continue with Project Setup once I have supplied my Project Details so that I don't have to wait until all partner details are submitted before providing further information
...
...               INFUND-4428 As a Partner, I should have access to the various Project Setup sections when they become available, so that I can access them when it is valid to
...
...               INFUND-5610 As a user I want to check the selected Project Manager value persists
...
...               INFUND-5368 Once finance contact is submitted, do not allow it to be changed again
...
...               INFUND-3483 As a lead partner I want to invite a new contributor to our organisation so that they can be assigned as our project manager
...
...               INFUND-3550 As a potential Project Manager, I can receive an email with a Join link, so that I can start the registration process and collaborate with the project
...
...               INFUND-3530 As a potential Finance Contact, I can click on a link to register and to become a Finance Contact for a Partner Organisation, so that I can start collaborating on the Project
...
...               INFUND-3554 As a potential Project Manager, I can click on a link to register and to become a Project Manager for the Project, so that I can start collaborating on the Project
...
...               INFUND-5898 As a partner I want to be able to change my Finance Contact in Project Setup so that I can submit updates to our partner details as appropriate
...
...               INFUND-5856 As an internal user I want to see a view of each project's submitted Project Details and the Finance contacts so I can use these for reference throughout Project Setup
Suite Setup       Run Keywords    delete the emails from both test mailboxes
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot

*** Variables ***
${project_details_submitted_message}    The project details have been submitted to Innovate UK

*** Test Cases ***
Internal users can see Project Details not yet completed
    [Documentation]  INFUND-5856
    [Tags]
    [Setup]  log in as user                         john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page            ${internal_project_summary}
    Then the user should not see the element        jQuery=#table-project-status tr:nth-child(1) td.status.ok a   #Check here that there is no Green-Check
    When the user clicks the button/link            jQuery=#table-project-status tr:nth-child(1) td:nth-child(2) a
    Then the user should see the text in the page   These project details were supplied by the lead partner on behalf of the project.
    And the user should see the text in the page    Each of the partners is responsible for submitting their own finance contact.
    When the user should see the element            jQuery=#project-details
    Then the user should see the element            jQuery=#project-address:contains("Not yet completed")
    And the user should see the element             jQuery=#no-project-manager:contains("Not yet completed")
    When the user should see the element            jQuery=#project-details-finance
    Then the user should see the element            jQuery=#project-details-finance tr:nth-child(1) td:nth-child(2):contains("Not yet completed")
    And the user should see the element             jQuery=#project-details-finance tr:nth-child(2) td:nth-child(2):contains("Not yet completed")
    And the user should see the element             jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Not yet completed")
    And Logout as user
    When log in as user                     project.finance1@innovateuk.test    Passw0rd
    Then the user navigates to the page     ${internal_project_summary}
    And the user clicks the button/link     jQuery=#table-project-status tr:nth-child(1) td:nth-child(2) a
    Then the user should see the element    jQuery=#no-project-manager:contains("Not yet completed")
    And the user should see the element     jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Not yet completed")
    [Teardown]  logout as user

Non-lead partner can see the project setup page
    [Documentation]    INFUND-2612, INFUND-2621, INFUND-4428
    [Tags]    HappyPath
    [Setup]    log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    When The user clicks the button/link    link=00000026: best riffs
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application best riffs has been successful within the Killer Riffs competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Finance checks
    And the user should see the text in the page    Spend profile
    And the user should see the text in the page    Other documents
    And the user should see the text in the page    Grant offer letter
    And the user should see the text in the page    What's the status of each of my partners?
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user navigates to the page    ${project_in_setup_page}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(1)
    # This test case can be part of above one. (If included then ensure a successful HappyPath run)
    # This test case covers non lead partner.

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]
    [Setup]    log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    Then the user should not see the element    link = Monitoring Officer
    And the user should not see the element    link = Bank details
    And the user should not see the element    link = Finance checks
    And the user should not see the element    link= Spend profile
    And the user should not see the element    link = Grant offer letter

Non-lead partner can click the Dashboard link
    [Documentation]    INFUND-4426
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Dashboard
    Then the user should not see an error in the page
    And the user should see the text in the page    Projects in setup
    [Teardown]    the user goes back to the previous page

Non-lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${project_in_setup_page}
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the text in the page    Congratulations, your application has been successful
    And the user should see the text in the page    Application questions
    And the user should not see an error in the page
    [Teardown]    logout as user

Lead partner can see the project setup page
    [Documentation]    INFUND-2612, INFUND-2621
    [Tags]    HappyPath
    [Setup]    log in as user    &{lead_applicant_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application best riffs has been successful within the Killer Riffs competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Other documents
    And the user should see the text in the page    Grant offer letter
    And the user should see the text in the page    What's the status of each of my partners?
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user navigates to the page    ${project_in_setup_page}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(1)

Lead partner can click the Dashboard link
    [Documentation]    INFUND-4426
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Dashboard
    Then the user should not see an error in the page
    And the user should see the text in the page    Projects in setup
    [Teardown]    the user goes back to the previous page

Lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the text in the page    Congratulations, your application has been successful
    And the user should see the text in the page    Application questions
    And the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

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
    [Documentation]    INFUND-3381
    [Tags]
    When the user should see the element    xpath=//span[contains(text(), 'No')]
    Then the submit button should be disabled

Lead partner can change the Start Date
    [Documentation]    INFUND-2614
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Start date
    And the duration should be visible
    When the user enters text to a text field    id=projectStartDate_year    2013
    Then the user should see a validation error    Please enter a future date
    And the user shouldn't be able to edit the day field as all projects start on the first of the month
    When the user enters text to a text field    id=projectStartDate_month    1
    And the user enters text to a text field    id=projectStartDate_year    2018
    And Mouse Out    id=projectStartDate_year
    And wait for autosave
    When the user clicks the button/link    jQuery=.button:contains("Save")
    #Run Keyword And Ignore Error    When the user clicks the button/link    jQuery=.button:contains("Save")    # Click the button for second time because the focus is still in the date field
    Then The user redirects to the page    You are providing these details as the lead applicant on behalf of the overall project    Project details
    And the user should see the text in the page    1 Jan 2018
    Then the matching status checkbox is updated    project-details    1    yes
    [Teardown]    the user changes the start date back again

Option to invite a project manager
    [Documentation]    INFUND-3483
    [Tags]    HappyPath
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Project manager
    And the user should see the element    jQuery=p:contains("Who will be the Project Manager for your project?")
    When the user selects the radio button    projectManager    new
    Then the user should see the element    id=invite-project-manager
    When the user selects the radio button    projectManager    projectManager1
    Then the user should not see the element    id=project-manager   # testing that the element disappears when the option is deselected
    [Teardown]    the user selects the radio button    projectManager    new

Inviting project manager server side validations
    [Documentation]    INFUND-3483
    [Tags]    Pending
    # TODO Pending due to INFUND-5704
    When the user clicks the button/link    id=invite-project-manager
    Then the user should see the text in the page    Please enter a contact name
    And the user should see the text in the page    Please enter an email address

Inviting project manager client side validations
    [Documentation]    INFUND-3483
    [Tags]    Pending
    # TODO Pending due to INFUND-5704
    When the user enters text to a text field    id=name-project-manager1    John Smith
    Then the user should not see the text in the page    Please enter a contact name
    When the user enters text to a text field    id=email-project-manager1    test
    Then the user should not see the text in the page    Please enter an email address
    And the user should see the text in the page    Please enter a valid email address
    When the user enters text to a text field    id=email-project-manager1    test@example.com
    Then the user should not see the text in the page    Please enter a valid email address
    And the user should not see an error in the page

Partner invites a project manager
    [Documentation]    INFUND-3483
    [Tags]    HappyPath    Email
    When the user enters text to a text field    id=name-project-manager    John Smith
    And the user enters text to a text field    id=email-project-manager    ${test_mailbox_one}+invitedprojectmanager@gmail.com
    And the user clicks the button/link    id=invite-project-manager
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    [Teardown]    logout as user


Invited project manager receives an email
    [Documentation]    INFUND-3550
    [Tags]    HappyPath    Email
    When the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+invitedprojectmanager@gmail.com    Project Manager invitation    You will be managing the project
    Then the user should see the text in the page    Vitruvius Stonework Limited


Invited project manager registration flow
    [Documentation]    INFUND-3554
    [Tags]    HappyPath    Email
    Given the user should see the text in the page    You have been invited to join a collaborative project
    And the user should see the text in the page    Vitruvius Stonework Limited
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user creates the account    Bob    Jones
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+invitedprojectmanager@gmail.com    Please verify your email address    Dear Bob Jones
    Then the user should see the text in the page    Account verified
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedprojectmanager@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    best riffs

Invited project manager shows on the project manager selection screen
    [Documentation]    INFUND-3554
    [Tags]    Email
    When the user clicks the button/link    link=00000026: best riffs
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Project manager
    Then the user should see the text in the page    Bob Jones

Lead partner selects a project manager
    [Documentation]    INFUND-2616
    ...
    ...    INFUND-2996
    ...    INFUND-5610
    [Tags]    HappyPath
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    And the user clicks the button/link    link=Project manager
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see a validation error    You need to select a Project Manager before you can continue
    When the user selects the radio button    projectManager    1
    And the user should not see the text in the page    You need to select a Project Manager before you can continue
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    Steve Smith
    And the user clicks the button/link    link=Project manager
    And the user sees that the radio button is selected    projectManager    1
    And the user selects the radio button    projectManager    projectManager1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the text in the page    test twenty
    And the matching status checkbox is updated    project-details    3    yes

Lead partner can change the project address
    [Documentation]    INFUND-3157
    ...
    ...    INFUND-2165
    [Tags]    HappyPath
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    And the user clicks the button/link    link=Project address
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
    Then the user should see the text in the page    1, Bath, BA1 5LR

Project details can be submitted with PM, project address and start date
    [Documentation]    INFUND-4583
    [Tags]    HappyPath
    Given the user should see the element    css=#start-date-status.yes
    And the user should see the element    css=#project-address-status.yes
    And the user should see the element    css=#project-manager-status.yes
    Mark as complete button should be enabled

Partners nominate finance contacts
    [Documentation]    INFUND-2620, INFUND-5368
    [Tags]    HappyPath
    [Setup]    Logout as user
    When Log in as user    jessica.doe@ludlow.co.uk    Passw0rd
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    1    yes
    And the user should see the element    link=Ludlow
    Then Logout as user
    When Log in as user    pete.tom@egg.com    Passw0rd
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=EGGS
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    2    yes
    And the user should see the element    link=EGGS
    [Teardown]    logout as user

Option to invite a finance contact
    [Documentation]    INFUND-3579
    [Tags]    HappyPath
    [Setup]    Log in as user    steve.smith@empire.com    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    And the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Vitruvius Stonework Limited
    When the user selects the radio button    financeContact    new
    Then the user should see the element    id=invite-finance-contact
    When the user selects the radio button    financeContact    financeContact1
    Then the user should not see the element    id=invite-finance-contact    # testing that the element disappears when the option is deselected
    [Teardown]    the user selects the radio button    financeContact    new

Inviting finance contact server side validations
    [Documentation]    INFUND-3579
    [Tags]    Pending
    # TODO Pending due to INFUND-5704
    When the user clicks the button/link    id=invite-finance-contact
    Then the user should see the text in the page    Please enter a contact name
    And the user should see the text in the page    Please enter an email address

Inviting finance contact client side validations
    [Documentation]    INFUND-3579
    [Tags]    Pending
    # TODO Pending due to INFUND-5704
    When the user enters text to a text field    id=name-finance-contact1    John Smith
    Then the user should not see the text in the page    Please enter a contact name
    When the user enters text to a text field    id=email-finance-contact1    test
    Then the user should not see the text in the page    Please enter an email address
    And the user should see the text in the page    Please enter a valid email address
    When the user enters text to a text field    id=email-finance-contact1    test@example.com
    Then the user should not see the text in the page    Please enter a valid email address
    And the user should not see an error in the page

Partner invites a finance contact
    [Documentation]    INFUND-3579
    [Tags]    HappyPath    Email
    When the user enters text to a text field    id=name-finance-contact    John Smith
    And the user enters text to a text field    id=email-finance-contact    ${test_mailbox_one}+invitedfinancecontact@gmail.com
    And the user clicks the button/link    id=invite-finance-contact
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    [Teardown]    logout as user

Invited finance contact receives an email
    [Documentation]    INFUND-3524
    [Tags]    HappyPath    Email
    When the user reads his email and clicks the link    ${test_mailbox_one}+invitedfinancecontact@gmail.com    Finance contact invitation    Dear John Smith
    Then the user should see the text in the page    Vitruvius Stonework Limited


Invited finance contact registration flow
    [Documentation]    INFUND-3530
    [Tags]    HappyPath    Email
    Given the user should see the text in the page    You have been invited to join a collaborative project
    And the user should see the text in the page    Vitruvius Stonework Limited
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user creates the account    John    Smith
    And the user reads his email and clicks the link    ${test_mailbox_one}+invitedfinancecontact@gmail.com    Please verify your email address    Verify account
    Then the user should see the text in the page    Account verified
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedfinancecontact@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    best riffs

Invited finance contact shows on the finance contact selection screen
    [Documentation]    INFUND-3530
    [Tags]    Email
    When the user clicks the button/link    link=00000026: best riffs
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Vitruvius Stonework Limited
    Then the user should see the text in the page    John Smith

Lead partner selects a finance contact
    [Documentation]    INFUND-2620, INFUND-5571, INFUND-5898
    [Tags]    HappyPath
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Vitruvius Stonework Limited
    And the user should not see duplicated select options
    And the user should not see the text in the page    Pending
    And the user selects the radio button    financeContact    financeContact3
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated    project-details-finance    1    yes
    And the user should see the text in the page    test twenty
    And the user should see the element    link=Vitruvius Stonework Limited


Non-lead partner cannot change start date, project manager or project address
    [Documentation]    INFUND-3157
    [Tags]
    [Setup]    Logout as user
    Given guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should not see the element    link=Start date
    And the user should not see the element    link=Project manager
    And the user should not see the element    link=Project address
    [Teardown]    Logout as user

Project details submission flow
    [Documentation]    INFUND-3381, INFUND-2621
    [Tags]    HappyPath
    [Setup]    guest user log-in    steve.smith@empire.com    Passw0rd
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    When all the fields are completed
    And the applicant clicks the submit button and then clicks cancel in the submit modal
    And the user should not see the text in the page    The project details have been submitted to Innovate UK
    Then the applicant clicks the submit button in the modal
    And the user should see the text in the page    The project details have been submitted to Innovate UK
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(2)
    And the user should see the element    jQuery=ul li.require-action:nth-child(4)
    And the user should see the element    jQuery=ul li.require-action:nth-child(7)
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    id=table-project-status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)

Project details read only after submission
    [Documentation]    INFUND-3381
    [Tags]
    Given the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then all the fields are completed
    And The user should not see the element    link=Start date
    And The user should not see the element    link=Project address
    And The user should not see the element    link=Project manager

All partners can view submitted project details
    [Documentation]    INFUND-3382, INFUND-2621
    [Setup]    the user logs out if they are logged in
    When guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    And the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then the user should see the text in the page    Ludlow
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}
    Then the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=What's the status of each of my partners?
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)
    Then the user logs out if they are logged in
    When guest user log-in    steve.smith@empire.com    Passw0rd
    And the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE_DETAILS}
    Then the user should see the text in the page    Vitruvius Stonework Limited
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user clicks the button/link    link=What's the status of each of my partners?
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)

Non-lead partner cannot change any project details
    [Documentation]    INFUND-2619
    [Setup]    Run Keywords    logout as user
    ...    AND    guest user log-in    jessica.doe@ludlow.co.uk    Passw0rd
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Start date
    And the user should see the text in the page    1 Jan 2017
    And the user should not see the element    link=Start date
    And the user should see the text in the page    Project manager
    And the user should see the text in the page    test twenty
    And the user should not see the element    link=Project manager
    And the user should see the text in the page    Project address
    And the user should see the text in the page    1, Bath, BA1 5LR
    And the user should not see the element    link=Project address
    And the user navigates to the page    ${project_start_date_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_manager_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_address_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    [Teardown]    Logout as user

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049
    [Tags]
    [Setup]    guest user log-in    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).waiting
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.action

Internal user can see the Project details as sumbmitted
    [Documentation]  INFUND-5856
    [Tags]
    [Setup]  the user navigates to the page  ${internal_project_summary}
    When the user clicks the button/link     jQuery=#table-project-status tr:nth-child(1) td.status.ok a
    Then the user should see the element     jQuery=#project-details
    And the user can see all project details completed
    When the user should see the element     jQuery=#project-details-finance
    And the user can see all finance contacts completed
    [Teardown]  logout as user

*** Keywords ***
the user should see a validation error
    [Arguments]    ${ERROR1}
    Focus    jQuery=button:contains("Save")
    sleep    300ms
    Then the user should see an error    ${ERROR1}

the matching status checkbox is updated
    [Arguments]    ${table_id}    ${ROW}    ${STATUS}
    the user should see the element    ${table_id}
    the user should see the element    jQuery=#${table_id} tr:nth-of-type(${ROW}) .${STATUS}

the duration should be visible
    Element Should Contain    xpath=//*[@id="content"]/form/fieldset/div/p[5]/strong    36 months

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
    Element Should Be Disabled    jQuery=.button:contains("Mark as complete")

the applicant clicks the submit button and then clicks cancel in the submit modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jquery=button:contains("Cancel")

the applicant clicks the submit button in the modal
    Wait Until Element Is Enabled    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=button:contains("Submit")

all the fields are completed
    the matching status checkbox is updated    project-details    1    yes
    the matching status checkbox is updated    project-details    2    yes
    the matching status checkbox is updated    project-details    3    yes
    the matching status checkbox is updated    project-details-finance    1    yes
    the matching status checkbox is updated    project-details-finance    2    yes
    the matching status checkbox is updated    project-details-finance    3    yes

the user changes the start date back again
    the user clicks the button/link    link=Start date
    the user enters text to a text field    id=projectStartDate_year    2017
    the user clicks the button/link    jQuery=.button:contains("Save")

Mark as complete button should be enabled
    Then Wait Until Element Is Enabled    jQuery=.button:contains("Mark as complete")

the user should not see duplicated select options
    ${NO_OPTIONs}=    Get Matching Xpath Count    //div/div/label
    Should Be Equal As Integers    ${NO_OPTIONs}    5    # note that an extra option shows here due to the invited project manager appearing in the list for lead partner organisation members


the user creates the account
    [Arguments]    ${first_name}    ${last_name}
    the user enters text to a text field    id=firstName    ${first_name}
    the user enters text to a text field    id=lastName    ${last_name}
    the user enters text to a text field    id=phoneNumber    0987654321
    the user enters text to a text field    id=password    Passw0rd123
    the user enters text to a text field    id=retypedPassword    Passw0rd123
    the user selects the checkbox    termsAndConditions
    the user clicks the button/link    jQuery=.button:contains("Create account")

the user can see all project details completed
    the user should see the element  jQuery=#start-date:contains("1 Jan 2017")
    the user should see the element  jQuery=#project-address:contains("1, Bath, BA1 5LR")
    the user should see the element  jQuery=#project-manager:contains("test twenty")

the user can see all finance contacts completed
    the user should see the element  jQuery=#project-details-finance tr:nth-child(1) td:nth-child(2):contains("Jessica Doe")
    the user should see the element  jQuery=#project-details-finance tr:nth-child(2) td:nth-child(2):contains("Pete Tom")
    the user should see the element  jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Bob Jones")