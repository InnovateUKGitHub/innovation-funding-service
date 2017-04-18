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
...
...               INFUND-5827 As a lead partner I want my Project Setup dashboard to inform me when all the Project Details and Finance Contacts are provided so that I know if any tasks are outstanding
...
...               INFUND-5979 Consortium table - Project details - should update when partners submit their Finance Contacts
...
...               INFUND-5805 As a successful applicant I want to be able to view the grant terms and conditions from my dashboard so that I can confirm what I agreed to in the application
...
...               INFUND-6781 Spend Profile is accessible before preliminary sections are completed
...
...               INFUND-7174 Not eligible partner should not have access to his Bank details page
...
...               INFUND-6882 Email validation done when valid is input selected for PM selection in project details
...
...               INFUND-7432 Terms and Conditions of grant offer takes you to the IFS ts and cs, not the grant ones
...
...               INFUND-9062 Validation missing when inviting self as finance contact or PM
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***
${project_details_submitted_message}    The project details have been submitted to Innovate UK

*** Test Cases ***
Internal users can see Project Details not yet completed
    [Documentation]    INFUND-5856
    [Tags]    HappyPath
    [Setup]    log in as user                      &{Comp_admin1_credentials}
    Given the user navigates to the page           ${internal_project_summary}
    Then the user should not see the element       jQuery=#table-project-status tr:nth-child(1) td.status.ok a    #Check here that there is no Green-Check
    When the user clicks the button/link           jQuery=#table-project-status tr:nth-child(1) td:nth-child(2) a
    Then the user should see the text in the page  These project details were supplied by the lead partner on behalf of the project.
    And the user should see the text in the page   Each partner must provide a finance contact. We will contact them with any finance queries.
    When the user should see the element           jQuery=#project-details
    Then the user should see the element           jQuery=#project-address:contains("Not yet completed")
    And the user should see the element            jQuery=#no-project-manager:contains("Not yet completed")
    When the user should see the element           jQuery=#project-details-finance
    Then the user should see the element           jQuery=#project-details-finance tr:nth-child(1) td:nth-child(2):contains("Not yet completed")
    And the user should see the element            jQuery=#project-details-finance tr:nth-child(2) td:nth-child(2):contains("Not yet completed")
    And the user should see the element            jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Not yet completed")
    When Log in as a different user                &{internal_finance_credentials}
    Then the user navigates to the page            ${internal_project_summary}
    And the user clicks the button/link            jQuery=#table-project-status tr:nth-child(1) td:nth-child(2) a
    Then the user should see the element           jQuery=#no-project-manager:contains("Not yet completed")
    And the user should see the element            jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Not yet completed")

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5507,INFUND-5543
    [Tags]    HappyPath
    [Setup]  log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.waiting    #Project details
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status    #MO
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status    #Bank details
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action    #Finance checks
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status    #Spend Profile
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.waiting    #Other Docs
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(7).status    #GOL
    #Internal user can view project details via the clickable 'hour glass' for Project details
    When the user clicks the button/link    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.waiting a
    Then the user should see the element    jQuery=h1:contains("Project details")
    And the user clicks the button/link    link=Projects in setup
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.waiting

Non-lead partner can see the project setup page
    [Documentation]    INFUND-2612, INFUND-2621, INFUND-4428, INFUND-5827, INFUND-5805, INFUND-7432
    [Tags]    HappyPath
    [Setup]  log in as a different user     &{collaborator1_credentials}
    When The user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    xpath=//a[contains(@href, '/info/terms-and-conditions')]
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application ${PROJECT_SETUP_APPLICATION_1_TITLE} has been successful within the ${PROJECT_SETUP_COMPETITION_NAME} competition
    And the user should see the element    link=View application and feedback
    And the user clicks the button/link    link=View the grant terms and conditions
    And the user should see the text in the page     Terms and conditions of an Innovate UK grant award
    And the user goes back to the previous page
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Finance checks
    And the user should see the text in the page    Spend profile
    And the user should see the text in the page    Other documents
    And the user should see the element    jQuery=li.require-action:nth-of-type(2)    #Action required, seen by non-lead
    And the user should see the text in the page    Grant offer letter
    And the user should see the text in the page    status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user navigates to the page    ${project_in_setup_page}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(1)

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{collaborator1_credentials}
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
    And the user should see the text in the page    Set up your project
    [Teardown]    the user goes back to the previous page

Non-lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    [Setup]    the user navigates to the page    ${project_in_setup_page}
    And the user should see the text in the page    Other documents
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the text in the page    Congratulations, your application has been successful
    # And the user should see the text in the page    Application details
    # Pending due to INFUND-7861
    And the user should not see an error in the page

Lead partner can see the project setup page
    [Documentation]    INFUND-2612, INFUND-2621, INFUND-5827, INFUND-5805
    [Tags]    HappyPath
    [Setup]    log in as a different user    &{lead_applicant_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    xpath=//a[contains(@href, '/info/terms-and-conditions')]
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application ${PROJECT_SETUP_APPLICATION_1_TITLE} has been successful within the ${PROJECT_SETUP_COMPETITION_NAME} competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View the grant terms and conditions
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Other documents
    And the user should see the element    jQuery=li.require-action:nth-of-type(2)    #Action required, seen by lead
    And the user should see the text in the page    Grant offer letter
    And the user should see the text in the page    status of my partners
    When the user clicks the button/link    link=status of my partners
    Then the user navigates to the page    ${project_in_setup_page}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(1)

Lead partner can click the Dashboard link
    [Documentation]    INFUND-4426
    [Tags]
    [Setup]    the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Dashboard
    Then the user should not see an error in the page
    And the user should see the text in the page    Set up your project
    [Teardown]    the user goes back to the previous page

Lead partner can see the application overview
    [Documentation]    INFUND-2612
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=View application and feedback
    Then the user should see the element    jQuery=.success-alert h2:contains("Congratulations, your application has been successful")
    # And the user should see the element     jQuery=h2:contains("Application details")
    # Pending due to INFUND-7861
    And the user should not see an error in the page

Lead partner is able to see finances without an error
    [Documentation]  INFUND-7634
    [Tags]
    Given the user clicks the button/link  jQuery=button:contains("Finances summaries")
    When the user clicks the button/link   link=Detailed Organisation Finances
    Then the user should not see an error in the page
    And the user should see the element    jQuery=h2:contains("Finance summary")
    Then the user clicks the button/link   link=Application summary

Lead partner can see the overview of the project details
    [Documentation]    INFUND-2613
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Please supply the following details for your project and the team
    And the user should see the element    link=Target start date
    And the user should see the element    link=Project address
    And the user should see the element    link=Project Manager
    And the user should see the text in the page    Finance contacts

Submit button is disabled if the details are not fully filled out
    [Documentation]    INFUND-3381
    [Tags]
    When the user should see the element    xpath=//span[contains(text(), 'No')]
    Then the submit button should be disabled

Lead partner can change the Start Date
    [Documentation]    INFUND-2614
    [Tags]    HappyPath
    Given the user clicks the button/link    link=Target start date
    And the duration should be visible
    When the user enters text to a text field    id=projectStartDate_year    2013
    Then the user should see a validation error    Please enter a future date.
    And the user shouldn't be able to edit the day field as all projects start on the first of the month
    When the user enters text to a text field    id=projectStartDate_month    1
    And the user enters text to a text field     id=projectStartDate_year    ${nextyear}
    And Mouse Out    id=projectStartDate_year
    And wait for autosave
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then The user redirects to the page    You are providing these details as the lead on behalf of the overall project    Project details
    And the user should see the text in the page    1 Jan ${nextyear}
    Then the matching status checkbox is updated    project-details    1    yes
    [Teardown]    the user changes the start date back again

Option to invite a project manager
    [Documentation]    INFUND-3483
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Project Manager
    And the user should see the element    jQuery=p:contains("Who will be the Project Manager for your project?")
    When the user selects the radio button    projectManager    new
    Then the user should see the element    id=invite-project-manager
    When the user selects the radio button    projectManager    projectManager1
    Then the user should not see the element    id=project-manager    # testing that the element disappears when the option is deselected
    [Teardown]    the user selects the radio button    projectManager    new

Inviting project manager server side validations
    [Documentation]    INFUND-3483, INFUND-9062
    [Tags]
    When the user clicks the button/link    id=invite-project-manager
    Then the user should see the text in the page    Please enter a valid name.
    And the user should see the text in the page    Please enter an email address.
    When the user enters text to a text field    id=name-project-manager    Steve Smith
    And the user enters text to a text field     id=email-project-manager    steve.smith@empire.com
    And the user clicks the button/link    id=invite-project-manager
    Then the user should see the text in the page    You cannot invite yourself to the project.

Inviting project manager client side validations
    [Documentation]    INFUND-3483, INFUND-6882
    [Tags]
    When the user enters text to a text field    id=name-project-manager    John Smith
    And the user moves focus to the element    jQuery=.button:contains("Save")
    Then the user should not see the text in the page    Please enter a valid name.
    When the user enters text to a text field    id=email-project-manager    test
    And the user moves focus to the element    jQuery=.button:contains("Save")
    Then the user should not see the text in the page    Please enter a valid name.
    And the user should see the text in the page    Please enter a valid email address.
    When the user selects the radio button    projectManager    projectManager1
    Then the user should not see the text in the page    Please enter an email address.
    And the user should not see the text in the page    Please enter a valid name.
    When the user selects the radio button    projectManager    new
    And the user enters text to a text field    id=email-project-manager    test@example.com
    And the user moves focus to the element    jQuery=.button:contains("Save")
    Then the user should not see the text in the page    Please enter an email address.
    And the user should not see the text in the page    Please enter a valid name.
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
    Then the user should see the text in the page    Empire Ltd

Invited project manager registration flow
    [Documentation]    INFUND-3554
    [Tags]    HappyPath    Email
    Given the user should see the text in the page    You have been invited to join a project
    And the user should see the text in the page    Empire Ltd
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user creates the account    Bob    Jones
    And the user reads his email and clicks the link    ${TEST_MAILBOX_ONE}+invitedprojectmanager@gmail.com    Please verify your email address    Dear Bob Jones
    Then the user should see the text in the page    Account verified
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedprojectmanager@gmail.com  ${correct_password}
    And the guest user clicks the log-in button
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}
    Then the user should not see the element    jQuery=.my-applications .in-progress  #applications in progress section
    And the user should not see the element  jQuery=h2:contains("Application in progress")

Invited project manager shows on the project manager selection screen
    [Documentation]    INFUND-3554
    [Tags]    Email
    When the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Project Manager
    Then the user should see the text in the page    Bob Jones

Lead partner selects a project manager
    [Documentation]    INFUND-2616
    ...
    ...    INFUND-2996
    ...    INFUND-5610
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_details_page}
    And the user clicks the button/link    link=Project Manager
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see a validation error    You need to select a Project Manager before you can continue.
    When the user selects the radio button    projectManager    projectManager1
    And the user should not see the text in the page    You need to select a Project Manager before you can continue.
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    Steve Smith
    And the user clicks the button/link    link=Project Manager
    And the user sees that the radio button is selected    projectManager    ${STEVE_SMITH_ID}
    And the user selects the radio button    projectManager    projectManager2
    And the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the user should see the text in the page    Elmo Chenault
    And the matching status checkbox is updated    project-details    3    yes

Lead partner can change the project address
    [Documentation]    INFUND-3157
    ...
    ...    INFUND-2165
    [Tags]    HappyPath
    Given the user navigates to the page    ${project_in_setup_details_page}
    And the user clicks the button/link    link=Project address
    When the user clicks the button/link    jQuery=.button:contains("Save")
    Then the user should see the text in the page    You need to select a project address before you can continue.
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled
    And the user clicks the button/link    jQuery=.button:contains("Save project address")
    And the user should see the address data
    When the user clicks the button/link    link=Project address
    And the user selects the radio button    addressType    REGISTERED
    And the user clicks the button/link    jQuery=.button:contains("Save project address")
    Then the user should see the text in the page    1, Sheffield, S1 2ED

Project details can be submitted with PM, project address and start date
    [Documentation]    INFUND-4583
    [Tags]    HappyPath
    Given the user should see the element    css=#start-date-status.yes
    And the user should see the element    css=#project-address-status.yes
    And the user should see the element    css=#project-manager-status.yes
    Mark as complete button should be enabled

Non lead partner nominates finance contact
    [Documentation]    INFUND-2620, INFUND-5368, INFUND-5827, INFUND-5979, INFUND-4428
    [Tags]    HappyPath
    When Log in as a different user        &{collaborator1_credentials}
    Then the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should not see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(1)
    And the user clicks the button/link    link=Project setup status
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Ludlow
    And the user selects the radio button    financeContact    financeContact1
    And the user clicks the button/link    jQuery=.button:contains("Save finance contact")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated differently    project-details-finance    3    Yes
    And the user should see the element    link=Ludlow
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element   jQuery=li.complete:nth-of-type(2)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(1)

    # Please note that the following Test Cases regarding story INFUND-7090, have to remain in Project Details suite
    # and not in Bank Details. Because for this scenario there are testing data for project 4.
Non lead partner not eligible for funding
    [Documentation]    INFUND-7090, INFUND-7174
    [Tags]    HappyPath
    Given the user navigates to the page    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}
    And the user should see the element    jQuery=ul li.complete:nth-child(2)
    Then the user should not see the element    jQuery=ul li.require-action:nth-child(4)
    When The user navigates to the page and gets a custom error message    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/bank-details    You do not have the necessary permissions for your request
    When the user navigates to the page    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}
    And the user clicks the button/link    link=status of my partners
    Then the user navigates to the page    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/team-status
    And the user should see the element    jQuery=#table-project-status tr:nth-child(3) td.status.na:nth-child(4)

Other partners can see who needs to provide Bank Details
    [Documentation]    INFUND-7090
    [Tags]
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${server}/project-setup/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/team-status
    Then the user should see the element    jQuery=#table-project-status tr:nth-child(3) td.status.na:nth-child(4)
    And the user should see the element     jQuery=#table-project-status tr:nth-child(2) td:nth-child(4):contains("")

Option to invite a finance contact
    [Documentation]    INFUND-3579
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    And the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Empire Ltd
    When the user selects the radio button    financeContact    new
    Then the user should see the element    id=invite-finance-contact
    When the user selects the radio button    financeContact    financeContact1
    Then the user should not see the element    id=invite-finance-contact    # testing that the element disappears when the option is deselected
    [Teardown]    the user selects the radio button    financeContact    new

Inviting finance contact server side validations
    [Documentation]    INFUND-3483, INFUND-9062
    [Tags]
    When the user clicks the button/link    id=invite-finance-contact
    Then the user should see the text in the page    Please enter a valid name.
    And the user should see the text in the page    Please enter an email address.
    When the user enters text to a text field    id=name-finance-contact    Steve Smith
    And the user enters text to a text field     id=email-finance-contact    steve.smith@empire.com
    And the user clicks the button/link    id=invite-finance-contact
    Then the user should see the text in the page    You cannot invite yourself to the project.

Inviting finance contact client side validations
    [Documentation]    INFUND-3483
    [Tags]
    When the user enters text to a text field    id=name-finance-contact    John Smith
    And the user moves focus to the element    jQuery=.button:contains("Save finance contact")
    Then the user should not see the text in the page    Please enter a valid name.
    When the user enters text to a text field    id=email-finance-contact    test
    And the user moves focus to the element    jQuery=.button:contains("Save finance contact")
    Then the user should not see the text in the page    Please enter a valid name.
    And the user should see the text in the page    Please enter a valid email address
    When the user enters text to a text field    id=email-finance-contact    test@example.com
    And the user moves focus to the element    jQuery=.button:contains("Save finance contact")
    Then the user should not see the text in the page    Please enter an email address.
    And the user should not see the text in the page    Please enter a valid name.
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
    Then the user should see the text in the page    Empire Ltd

Invited finance contact registration flow
    [Documentation]    INFUND-3530
    [Tags]    HappyPath    Email
    Given the user should see the text in the page    You have been invited to join a project
    And the user should see the text in the page    Empire Ltd
    When the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user creates the account    John    Smith
    And the user reads his email and clicks the link    ${test_mailbox_one}+invitedfinancecontact@gmail.com    Please verify your email address    Verify
    Then the user should see the text in the page    Account verified
    When the user clicks the button/link    jQuery=.button:contains("Sign in")
    And the guest user inserts user email & password    ${test_mailbox_one}+invitedfinancecontact@gmail.com    Passw0rd123
    And the guest user clicks the log-in button
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}

Invited finance contact shows on the finance contact selection screen
    [Documentation]    INFUND-3530
    [Tags]    Email
    When the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_HEADER}
    And the user clicks the button/link    link=Project details
    And the user clicks the button/link    link=Empire Ltd
    Then the user should see the text in the page    John Smith

Lead partner selects a finance contact
    [Documentation]    INFUND-2620, INFUND-5571, INFUND-5898
    [Tags]    HappyPath
    Then the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Finance contacts
    And the user should see the text in the page    Partner
    And the user clicks the button/link    link=Empire Ltd
    And the user should not see duplicated select options
    And the user should not see the text in the page    Pending
    And the user selects the radio button    financeContact    financeContact2
    And the user clicks the button/link    jQuery=.button:contains("Save finance contact")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated differently   project-details-finance    1    Yes
    And the user should see the text in the page    Elmo Chenault
    And the user should see the element    link=Empire Ltd

Non-lead partner cannot change start date, project manager or project address
    [Documentation]    INFUND-3157
    [Tags]
    [Setup]
    Given log in as a different user    &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should not see the element    link=Target start date
    And the user should not see the element    link=Project Manager
    And the user should not see the element    link=Project address

Internal user should see project details are incomplete
    [Documentation]    INFUND-6781
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page     ${internal_project_summary}
    When the user clicks the button/link     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.waiting
    Then the user should see the text in the page  	Not yet completed

Academic Partner nominates Finance contact
    [Documentation]    INFUND-2620, INFUND-5368, INFUND-5827, INFUND-5979, INFUND-6781
    [Tags]    HappyPath
    [Setup]    Log in as a different user   &{collaborator2_credentials}
    Then the user navigates to the page     ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should not see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(1)
    When the user clicks the button/link    link=Project setup status
    Then the user should not see the element    jQuery=li.require-action:nth-child(4)
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page  Finance contacts
    And the user should see the text in the page   Partner
    And the user clicks the button/link            link=EGGS
    And the user selects the radio button          financeContact    financeContact1
    And the user clicks the button/link            jQuery=.button:contains("Save finance contact")
    Then the user should be redirected to the correct page    ${project_in_setup_page}
    And the matching status checkbox is updated differently    project-details-finance    2    Yes
    And the user should see the element     link=EGGS
    When the user navigates to the page     ${project_in_setup_page}
    Then the user should see the element    jQuery=li.complete:nth-of-type(2)
    And the user should see the element    jQuery=li.require-action:nth-child(4)
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td.status.ok:nth-of-type(1)

Project details submission flow
    [Documentation]    INFUND-3381, INFUND-2621, INFUND-5827
    [Tags]    HappyPath
    [Setup]    log in as a different user    &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_details_page}
    When all the fields are completed
    And the applicant clicks the submit button and then clicks cancel in the submit modal
    And the user should not see the text in the page    The project details have been submitted to Innovate UK
    Then the applicant clicks the submit button in the modal
    And the user should see the text in the page    The project details have been submitted to Innovate UK
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=li.complete:nth-of-type(2)

Lead partner can see the status update when all Project details are submitted
    [Documentation]    INFUND-5827
    [Tags]    HappyPath
    [Setup]    Log in as a different user  &{lead_applicant_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element   jQuery=ul li.complete:nth-child(2)
    And the user should see the element    jQuery=ul li.require-action:nth-child(4)
    And the user should see the element    jQuery=ul li.waiting:nth-child(7)
    When the user clicks the button/link   link=status of my partners
    Then the user should see the element   id=table-project-status
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)

Project details read only after submission
    [Documentation]    INFUND-3381
    [Tags]
    Given the user navigates to the page    ${project_in_setup_details_page}
    Then all the fields are completed
    And The user should not see the element    link=Target start date
    And The user should not see the element    link=Project address
    And The user should not see the element    link=Project Manager

All partners can view submitted project details
    [Documentation]    INFUND-3382, INFUND-2621
    [Tags]    HappyPath
    When log in as a different user       &{collaborator1_credentials}
    And the user navigates to the page    ${project_in_setup_details_page}
    Then the user should see the text in the page    Ludlow
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}
    Then the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)
    When log in as a different user         &{lead_applicant_credentials}
    And the user navigates to the page    ${project_in_setup_details_page}
    Then the user should see the text in the page    Empire Ltd
    And all the fields are completed
    And the user should see the text in the page    ${project_details_submitted_message}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(1)

Non-lead partner cannot change any project details
    [Documentation]    INFUND-2619
    [Setup]    log in as a different user   &{collaborator1_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Target start date
    And the user should see the text in the page     1 Jan ${nextyear}
    And the user should not see the element    link=Target start date
    And the user should see the text in the page    Project Manager
    And the user should see the text in the page    Elmo Chenault
    And the user should not see the element    link=Project Manager
    And the user should see the text in the page    Project address
    And the user should see the text in the page    1, Sheffield, S1 2ED
    And the user should not see the element    link=Project address
    And the user navigates to the page    ${project_start_date_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_manager_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}
    And the user navigates to the page    ${project_address_page}
    And the user should be redirected to the correct page    ${project_in_setup_page}

Internal user can see the Project details as submitted
    [Documentation]    INFUND-5856
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page     ${internal_project_summary}
    When the user clicks the button/link     jQuery=#table-project-status tr:nth-child(2) td.status.ok a
    Then the user should see the element     jQuery=#project-details
    And the user can see all project details completed
    When the user should see the element     jQuery=#project-details-finance
    And the user can see all finance contacts completed

*** Keywords ***
the user should see a validation error
    [Arguments]    ${ERROR1}
    Focus    jQuery=button:contains("Save")
    wait for autosave
    Then the user should see an error    ${ERROR1}

the matching status checkbox is updated
    [Arguments]    ${table_id}    ${ROW}    ${STATUS}
    the user should see the element    ${table_id}
    the user should see the element    jQuery=#${table_id} tr:nth-of-type(${ROW}) .${STATUS}

the matching status checkbox is updated differently
    [Arguments]    ${table_id}    ${ROW}    ${STATUS}
    the user should see the element    ${table_id}
    the user should see the element    jQuery=#${table_id} tr:nth-of-type(${ROW}):contains("${STATUS}")

the duration should be visible
    Element Should Contain    xpath=//*[@id="content"]/form/p/strong    36 months

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
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jquery=button:contains("Cancel")

the applicant clicks the submit button in the modal
    Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=button:contains("Submit")

all the fields are completed
    the matching status checkbox is updated    project-details    1    yes
    the matching status checkbox is updated    project-details    2    yes
    the matching status checkbox is updated    project-details    3    yes
    the matching status checkbox is updated differently    project-details-finance    1    Yes
    the matching status checkbox is updated differently  project-details-finance    2    Yes
    the matching status checkbox is updated differently   project-details-finance    3    Yes

the user changes the start date back again
    the user clicks the button/link    link=Target start date
    the user enters text to a text field    id=projectStartDate_year    ${nextyear}
    the user clicks the button/link    jQuery=.button:contains("Save")

Mark as complete button should be enabled
    Then Wait Until Element Is Enabled Without Screenshots    jQuery=.button:contains("Mark as complete")

the user should not see duplicated select options
    ${NO_OPTIONs}=    Get Matching Xpath Count    //div/fieldset/label
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
    the user should see the element  jQuery=#start-date:contains("1 Jan ${nextyear}")
    the user should see the element  jQuery=#project-address:contains("1, Sheffield, S1 2ED")
    the user should see the element  jQuery=#project-manager:contains("Elmo Chenault")

the user can see all finance contacts completed
    the user should see the element  jQuery=#project-details-finance tr:nth-child(1) td:nth-child(2):contains("Elmo Chenault")
    the user should see the element  jQuery=#project-details-finance tr:nth-child(2) td:nth-child(2):contains("Pete Tom")
    the user should see the element  jQuery=#project-details-finance tr:nth-child(3) td:nth-child(2):contains("Jessica Doe")

Custom suite setup
    delete the emails from both test mailboxes
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
