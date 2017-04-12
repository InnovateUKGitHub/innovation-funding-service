*** Settings ***
Documentation     INFUND-2630 As a Competitions team member I want to be able to enter the details of a Monitoring Officer to assign them to the project and share their contact details with the consortium
...
...               INFUND-2632 As a Competitions team member I want to send an email to a Monitoring Officer so they are aware I have assigned them to a project
...
...               INFUND-2633 As a Competitions team member I want to send an email to the project manager so that they have the contact details of the Monitoring Officer I have assigned to their project
...
...               INFUND-2634 As a partner I want to be able to view details of the assigned Monitoring Officer for my project so I can contact them
...
...               INFUND-2621 As a contributor I want to be able to review the current Project Setup status of all partners in my project so I can get an indication of the overall status of the consortium
...
...               INFUND-6706 Mismatch in MO status between dashboard and consortium table
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***
${Successful_Monitoring_Officer_Page}    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/monitoring-officer

*** Test Cases ***
Before Monitoring Officer is assigned
    [Documentation]    INFUND-2634, INFUND-2621, INFUND-6706
    [Tags]    HappyPath
    [Setup]    Log in as user               &{lead_applicant_credentials}
    Given the user navigates to the page    ${project_in_setup_page}
    And the user should see the text in the page    We will assign the project a Monitoring Officer.
    And the user should not see the element    jQuery=ul li.complete:nth-child(3)
    And the user should see the element    jQuery=ul li.waiting:nth-child(3)
    When the user clicks the button/link    link=Monitoring Officer
    Then the user should see the text in the page    Your project has not yet been assigned a Monitoring Officer.
    And the user should not see the text in the page    A Monitoring Officer has been assigned.
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(2)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5507,INFUND-5543
    [Tags]      HappyPath
    [Setup]    log in as a different user   &{Comp_admin1_credentials}
    When the user navigates to the page    ${internal_project_summary}
    Then the user should see the element   jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(1).status.ok       # Project details
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.action   # MO
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status  # Bank details are not yet provided by any partner yet
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(4).status.action   # Finance checks-always action flag for private beta
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(5).status          # Spend Profile
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(6).status.waiting  # Other Docs
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(7).status          # GOL

Comp admin can view the Supporting information details on MO page
    [Documentation]    INFUND-2630
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    When the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Supporting information
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}
    And the user should see the text in the page    Satellite applications
    And the user should see the text in the page    Empire Road
    And the user should see the text in the page    Sheffield
    And the user should see the text in the page    S1 2ED
    And Element Should Contain    jQuery=p:nth-child(11)    1 Jan ${nextyear}
    And the user should see the text in the page    Elmo Chenault
    And the user should see the text in the page    Empire Ltd
    And the user should see the text in the page    EGGS
    And the user should see the text in the page    Ludlow

Project finance user can view MO page, and go on to assign MO
    [Documentation]    INFUND-5666, INFUND-5507
    [Tags]    HappyPath
    Given log in as a different user       &{internal_finance_credentials}
    When the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    Then the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Supporting information
    And the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}
    And the user should see the text in the page    Satellite applications
    And the user should see the text in the page    Empire Road
    And the user should see the text in the page    Sheffield
    And the user should see the text in the page    S1 2ED
    And Element Should Contain    jQuery=p:nth-child(11)    1 Jan ${nextyear}
    And the user should see the text in the page    Elmo Chenault
    And the user should see the text in the page    Empire Ltd
    And the user should see the text in the page    EGGS
    And the user should see the text in the page    Ludlow
    [Teardown]  the user clicks the button/link     link=Projects in setup


MO server-side validation
    [Documentation]    INFUND-2630
    [Tags]    HappyPath
    Given the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    When the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    Then the user should see an error    Please enter a first name.
    And the user should see an error    Please enter a last name.
    And the user should see an error    Please enter an email address.
    And the user should see an error    Please enter a phone number.
    And the user should see an error    Please enter a valid phone number.

MO client-side validation
    [Documentation]    INFUND-2630
    [Tags]    HappyPath
    When the user enters text to a text field    id=firstName    Abbey
    Then the user should not see the validation error    Please enter a first name.
    When the user enters text to a text field    id=lastName    Abigail
    Then the user should not see the validation error    Please enter a last name.
    When standard verification for email address follows
    When the user enters text to a text field    id=emailAddress    ${test_mailbox_one}+monitoringofficer@gmail.com
    And the user should not see the validation error    Please enter a valid email address.
    And the user should not see the validation error    Please enter an email address.
    When the user enters text to a text field    id=phoneNumber    0123
    And the user should not see the validation error    Please enter a phone number.
    And the user should not see the validation error    Please enter a valid phone number.
    And the user should see an error    Input for your phone number has a minimum length of 8 characters.
    When the user enters text to a text field    id=phoneNumber    07438620303
    Then the user should not see the validation error    Input for your phone number has a minimum length of 8 characters.

MO details can be added
    [Documentation]    INFUND-2630, INFUND-6706
    ...
    ...    INFUND-2632
    [Tags]    HappyPath
    And the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    And the user clicks the button/link    jQuery=.modal-assign-mo button:contains("Cancel")
    Then the user should not see the text in the page    A Monitoring Officer has been assigned.
    And the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    And the user clicks the button/link    jQuery=.modal-assign-mo button:contains("Assign Monitoring Officer")
    Then The user should see the element    css=.success-alert
    And the user should see the text in the page    A Monitoring Officer has been assigned.
    Then Log in as a different user       &{lead_applicant_credentials}
    And the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(3)
    And Element Should Contain    jQuery=ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Abbey Abigail.
    And the user clicks the button/link    link=status of my partners
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(2)

MO details(email step)
    [Documentation]    INFUND-2630
    ...
    ...    INFUND-2632
    ...
    ...    INFUND-2633
    [Tags]    Email    HappyPath
    # Note that assigning a monitoring officer will send emails out to both the new MO and the PM - this test checks for both emails
    When the user reads his email    ${test_mailbox_one}+monitoringofficer@gmail.com    New Monitoring Officer assignment    has been assigned to you
    And the user reads his email from the default mailbox    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    Your Monitoring Officer    has now been assigned a Monitoring Officer

MO details can be edited and viewed in the Project setup status page
    [Documentation]    INFUND-2630, INFUND-2621
    ...
    ...    INFUND-2634
    [Tags]    HappyPath
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page    ${Successful_Monitoring_Officer_Page}
    When the user clicks the button/link    link=Change Monitoring Officer
    And the user edits the MO details
    And the user can see the changed MO details
    When Log in as a different user        &{lead_applicant_credentials}
    Then the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(3)
    And Element Should Contain    jQuery=ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link    link=Monitoring Officer
    Then the user should see the text in the page    We have assigned a Monitoring Officer to your project.
    And the user should see the text in the page    Grace Harper
    And the user should see the text in the page    ${test_mailbox_two}+monitoringofficer@gmail.com
    And the user should see the text in the page    08549731414
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(2)

MO details edit(email step)
    [Documentation]    INFUND-2630
    ...
    ...    INFUND-2634
    [Tags]    Email
    # Note that assigning a monitoring officer will send emails out to both the new MO and the PM - this test checks for both emails
    When the user reads his email from the second mailbox    ${test_mailbox_two}+monitoringofficer@gmail.com    New Monitoring Officer assignment    has been assigned to you
    And the user reads his email from the default mailbox    ${PROJECT_SETUP_APPLICATION_1_PM_EMAIL}    Your Monitoring Officer    has now been assigned a Monitoring Officer

MO details accessible/seen by all partners
    [Documentation]    INFUND-2634, INFUND-2621
    [Tags]    HappyPath
    Given Log in as a different user       &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=ul li.complete:nth-child(3)
    And Element Should Contain    jQuery=ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link    link=Monitoring Officer
    Then the user should see the text in the page    We have assigned a Monitoring Officer to your project.
    And the user should see the text in the page    Grace Harper
    And the user should see the text in the page    ${test_mailbox_two}+monitoringofficer@gmail.com
    And the user should see the text in the page    08549731414
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(2)
    When Log in as a different user       &{lead_applicant_credentials}
    And the user navigates to the page    ${project_in_setup_page}
    Then the user should see the element    jQuery=ul li.complete:nth-child(3)
    And Element Should Contain    jQuery=ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link    link=Monitoring Officer
    Then the user should see the text in the page    We have assigned a Monitoring Officer to your project.
    And the user should see the text in the page    Grace Harper
    And the user should see the text in the page    ${test_mailbox_two}+monitoringofficer@gmail.com
    And the user should see the text in the page    08549731414
    When the user navigates to the page    ${project_in_setup_page}
    And the user clicks the button/link    link=status of my partners
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(2)


Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Tags]      HappyPath
    [Setup]    Log in as a different user  &{collaborator1_credentials}
    When the user navigates to the page    ${project_in_setup_page}
    And the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    Then the user should see the element    link = Monitoring Officer
    And the user should see the element    link = Finance checks
    And the user should not see the element    link = Spend profile
    And the user should not see the element    link = Grant offer letter


*** Keywords ***
standard verification for email address follows
    the user enters text to a text field    id=emailAddress    ${invalid_email_plain}
    the user should see an error    Please enter a valid email address.
    the user enters text to a text field    id=emailAddress    ${invalid_email_symbols}
    the user should see an error    Please enter a valid email address.
    the user enters text to a text field    id=emailAddress    ${invalid_email_no_username}
    the user should see an error    Please enter a valid email address.
    the user enters text to a text field    id=emailAddress    ${invalid_email_format}
    the user should see an error    Please enter a valid email address.
    the user enters text to a text field    id=emailAddress    ${invalid_email_no_at}
    the user should see an error    Please enter a valid email address.

the user should not see the validation error
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Focus    jQuery=.button:contains("Assign Monitoring Officer")
    Wait for autosave
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Wait Until Element Does Not Contain Without Screenshots    css=.error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}

the user edits the MO details
    The user enters text to a text field    id=firstName    Grace
    The user enters text to a text field    id=lastName    Harper
    The user enters text to a text field    id=emailAddress    ${test_mailbox_two}+monitoringofficer@gmail.com
    The user enters text to a text field    id=phoneNumber    08549731414
    the user clicks the button/link    jQuery=.button:contains("Assign Monitoring Officer")
    the user clicks the button/link    jQuery=.modal-assign-mo button:contains("Assign Monitoring Officer")

the user can see the changed MO details
    The user should see the element    css=.success-alert
    the user should see the text in the page    A Monitoring Officer has been assigned.
    Textfield Should Contain    id=firstName    Grace
    Textfield Should Contain    id=lastName    Harper

Custom suite setup
    delete the emails from both test mailboxes
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    the lead partner fills in project details if they are not already filled in



the lead partner fills in project details if they are not already filled in
    log in as user    &{lead_applicant_credentials}
    the user navigates to the page    ${project_in_setup_page}
    the user clicks the button/link    link=Project details
    ${project_details_not_completed}    ${value}=    run keyword and ignore error without screenshots    the user should not see the element    css=#project-address-status.yes
    run keyword if    '${project_details_not_completed}' == 'PASS'    the users fill in project details


the users fill in project details
    the lead partner fills in project details
    internal user can see that MO can be assigned
    the academic partner fills in their finance contact
    the industrial partner fills in their finance contact


the lead partner fills in project details
    the user clicks the button/link    link=Target start date
    the user enters text to a text field    id=projectStartDate_month    1
    the user enters text to a text field     id=projectStartDate_year    ${nextyear}
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    link=Project address
    the user selects the radio button    addressType    REGISTERED
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    link=Project Manager
    the user selects the radio button    projectManager    60
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Submit")
    the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}
    the user selects the radio button    financeContact    55
    the user clicks the button/link    jQuery=.button:contains("Save")

internal user can see that MO can be assigned
    log in as a different user   &{internal_finance_credentials}
    the user navigates to the page   ${Successful_Monitoring_Officer_Page}
    the user should not see an error in the page


the academic partner fills in their finance contact
    log in as a different user    &{collaborator2_credentials}
    the user navigates to the page    ${project_in_setup_page}
    the user clicks the button/link   link=Project details
    the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}
    the user selects the radio button    financeContact    57
    the user clicks the button/link    jQuery=.button:contains("Save")


the industrial partner fills in their finance contact
    log in as a different user    &{collaborator1_credentials}
    the user navigates to the page    ${project_in_setup_page}
    the user clicks the button/link   link=Project details
    the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_PARTNER_NAME}
    the user selects the radio button    financeContact    56
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user closes the browser