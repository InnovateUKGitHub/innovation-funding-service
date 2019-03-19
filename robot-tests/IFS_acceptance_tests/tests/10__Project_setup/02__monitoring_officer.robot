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
...
...               IFS-3553 Email subject for Monitoring Officer to include competition name and application ID
...
...               IFS-4209 MO view of project
...
...               IFS-5031 Assign an MO to a project
...
...               IFS-5298 MO permissions to view an application
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Project Setup
Resource          PS_Common.robot

*** Variables ***
${Successful_Monitoring_Officer_Page}    ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/monitoring-officer
${Assign_Project}   Mobile Phone Data for Logistics Analytics
${Assign_Project2}  High Performance Gasoline Stratified
${Assign_Project2_ID}  ${application_ids["${Assign_Project2}"]}
${New_Mo}          tom@poly.io

*** Test Cases ***
Before Monitoring Officer is assigned
    [Documentation]    INFUND-2634, INFUND-2621, INFUND-6706
    [Tags]  HappyPath
    [Setup]    The user logs-in in new browser          &{lead_applicant_credentials_bd}
    Given the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user should see the element                 jQuery = h2:contains("Monitoring Officer") ~ p:contains("We will assign the project a Monitoring Officer.")
    And the user should not see the element             css = ul li.complete:nth-child(3)
    And the user should see the element                 css = ul li.waiting:nth-child(3)
    When the user clicks the button/link                link = Monitoring Officer
    Then the user should see the element                jQuery = p:contains("Your project has not yet been assigned a Monitoring Officer.")
    And the user should not see the element             jQuery = .success-alert:contains("We have assigned a Monitoring Officer to your project.")
    When the user navigates to the page                 ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    And the user should see the element                 css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5507,INFUND-5543
    [Setup]    log in as a different user   &{Comp_admin1_credentials}
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(1)                               # Project details
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(3)                       # Documents
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(4)                       # Monitoring Officer
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(5)                       # Bank details
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td.govuk-table__cell.status.action    # Finance checks
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(7)                       # Spend Profile
    And the user should see the element    css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(8)                       # GOL

Comp admin can view the Supporting information details on MO page
    [Documentation]    INFUND-2630
    [Tags]  HappyPath
    [Setup]    Log in as a different user              &{Comp_admin1_credentials}
    When the user navigates to the page                ${Successful_Monitoring_Officer_Page}
    Then the user should see the element               jQuery = h1:contains("Monitoring Officer")
    And the user should see the element                jQuery = h2:contains("Supporting information")
    And the user should see the element                jQUery = h3:contains("Project title") ~ p:contains("${Grade_Crossing_Applicaiton_Titile}")
    And the user should see the element                jQuery = h3:contains("Area") ~ p:contains("Digital manufacturing")
    And the user should see the correct address
    And the user should see the text in the element    jQuery = p:nth-child(11)    1 Mar ${nextyear}
    And the user should see the element                jQuery = h3:contains("Project Manager") ~ p:contains("Diane Scott")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${Vitruvius_Name}")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${A_B_Cad_Services_Name}")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${Armstrong_Butler_Name}")

Project finance user can view MO page, and go on to assign MO
    [Documentation]    INFUND-5666, INFUND-5507
    [Tags]  HappyPath
    Given log in as a different user                   &{internal_finance_credentials}
    When the user navigates to the page                ${Successful_Monitoring_Officer_Page}
    Then the user should see the element               jQuery = h1:contains("Monitoring Officer")
    And the user should see the element                jQuery = h2:contains("Supporting information")
    And the user should see the element                jQUery = h3:contains("Project title") ~ p:contains("${Grade_Crossing_Applicaiton_Titile}")
    And the user should see the element                jQuery = h3:contains("Area") ~ p:contains("Digital manufacturing")
    And the user should see the correct address
    And the user should see the text in the element    jQuery = p:nth-child(11)    1 Mar ${nextyear}
    And the user should see the element                jQuery = h3:contains("Project Manager") ~ p:contains("Diane Scott")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${Vitruvius_Name}")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${A_B_Cad_Services_Name}")
    And the user should see the element                jQuery = h3:contains("Project partners") ~ ul li:contains("${Armstrong_Butler_Name}")
    [Teardown]  the user clicks the button/link        link = Projects in setup

MO server-side validation
    [Documentation]    INFUND-2630
    Given the user navigates to the page                ${Successful_Monitoring_Officer_Page}
    When the user clicks the button/link                jQuery = .govuk-button:contains("Assign Monitoring Officer")
    And the user clicks the button/link                 jQuery = [role = "dialog"] .govuk-button:contains("Assign Monitoring Officer")
    Then the user should see a field and summary error  ${enter_a_first_name}
    And the user should see a field and summary error   ${enter_a_last_name}
    And the user should see a field and summary error   Please enter an email address.
    And the user should see a field and summary error   ${enter_a_phone_number}
    And the user should see a field and summary error   ${enter_a_phone_number_between_8_and_20_digits}

MO client-side validation
    [Documentation]    INFUND-2630
    [Tags]  HappyPath
    Given the user navigates to the page                 ${Successful_Monitoring_Officer_Page}
    When the user enters text to a text field            id = firstName    Abbey
    Then the user should not see the validation error    ${enter_a_first_name}
    When the user enters text to a text field            id = lastName    Abigail
    Then the user should not see the validation error    ${enter_a_last_name}
    When standard verification for email address follows
    When the user enters text to a text field            id = emailAddress    ${test_mailbox_one}+monitoringofficer@gmail.com
    And the user should not see the validation error     ${enter_a_valid_email}
    And the user should not see the validation error     Please enter an email address.
    When the user enters text to a text field            id = phoneNumber    0123
    And the user should not see the validation error     ${enter_a_phone_number}
    And the user should not see the validation error     ${enter_a_valid_phone_number}
    And the user should see a field error                ${enter_a_phone_number_between_8_and_20_digits}
    When the user enters text to a text field            id = phoneNumber    07438620303
    Then the user should not see the validation error    ${enter_a_phone_number_between_8_and_20_digits}

MO details can be added
    [Documentation]    INFUND-2630, INFUND-6706, INFUND-2632
    [Tags]  HappyPath
    When the user clicks the button/link                 jQuery = .govuk-button:contains("Assign Monitoring Officer")
    And the user clicks the button/link                  jQuery = .modal-assign-mo button:contains("Cancel")
    Then the user should not see the element             jQuery = .success-alert:contains("We have assigned a Monitoring Officer to your project.")
    And the user clicks the button/link                  jQuery = .govuk-button:contains("Assign Monitoring Officer")
    And the user clicks the button/link                  jQuery = .modal-assign-mo button:contains("Assign Monitoring Officer")
    Then The user should see the element                 css = .success-alert
    And the user should see the element                  jQuery = .success-alert:contains("A Monitoring Officer has been assigned.")
    Then Log in as a different user                      &{lead_applicant_credentials_bd}
    And the user navigates to the page                   ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user should see the element                  css = ul li.complete:nth-child(3)
    And the user should see the text in the element      css = ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Abbey Abigail.
    And the user clicks the button/link                  link = View the status of partners
    And the user should see the element                  css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

MO details(email step)
    [Documentation]    INFUND-2630, INFUND-2632, INFUND-2633, IFS-3553
    [Tags]  HappyPath
    # Note that assigning a monitoring officer will send emails out to both the new MO and the PM - this test checks for both emails
    When the user reads his email    ${test_mailbox_one}+monitoringofficer@gmail.com    New Monitoring Officer assignment    has been assigned to you
    And the user reads his email     ${Grade_Crossing_Lead_Partner_Email}    ${PS_Competition_Name}: Your Monitoring Officer for project ${Grade_Crossing_Applicaiton_No}    has now been assigned a Monitoring Officer

MO details can be edited and viewed in the Set up your project page
    [Documentation]    INFUND-2630, INFUND-2621, INFUND-2634
    [Tags]  HappyPath
    [Setup]    Log in as a different user              &{Comp_admin1_credentials}
    Given the user navigates to the page               ${Successful_Monitoring_Officer_Page}
    When the user clicks the button/link               link = Change Monitoring Officer
    And the user edits the MO details
    And the user can see the changed MO details
    When Log in as a different user                    &{lead_applicant_credentials_bd}
    Then the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user should see the element                css = ul li.complete:nth-child(3)
    And the user should see the text in the element    css = ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link                link = Monitoring Officer
    Then the user should see the element               jQuery = .success-alert:contains("We have assigned a Monitoring Officer to your project.")
    And the user should see the element                jQuery = .govuk-body:contains("Grace Harper")
    And the user should see the element                jQuery = .govuk-body:contains("${test_mailbox_two}+monitoringofficer@gmail.com")
    And the user should see the element                jQuery = .govuk-body:contains("08549731414")
    When the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    Then the user should see the element               css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

MO details edit(email step)
    [Documentation]    INFUND-2630, INFUND-2634, IFS-3553
    # Note that assigning a monitoring officer will send emails out to both the new MO and the PM - this test checks for both emails
    When the user reads his email    ${test_mailbox_two}+monitoringofficer@gmail.com    New Monitoring Officer assignment    has been assigned to you
    And the user reads his email     ${Grade_Crossing_Lead_Partner_Email}    ${PS_Competition_Name}: Your Monitoring Officer for project ${Grade_Crossing_Applicaiton_No}    has now been assigned a Monitoring Officer

MO details accessible/seen by all partners
    [Documentation]    INFUND-2634, INFUND-2621
    [Tags]  HappyPath
    Given Log in as a different user                   &{collaborator1_credentials_bd}
    When the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element               css = ul li.complete:nth-child(3)
    And the user should see the text in the element    css = ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link                link = Monitoring Officer
    Then the user should see the element               jQuery = .success-alert:contains("We have assigned a Monitoring Officer to your project.")
    And the user should see the element                jQuery = .govuk-body:contains("Grace Harper")
    And the user should see the element                jQuery = .govuk-body:contains("${test_mailbox_two}+monitoringofficer@gmail.com")
    And the user should see the element                jQuery = .govuk-body:contains("08549731414")
    When the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    Then the user should see the element               css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)
    When Log in as a different user                    &{lead_applicant_credentials_bd}
    And the user navigates to the page                 ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element               css = ul li.complete:nth-child(3)
    And the user should see the text in the element    css = ul li.complete:nth-child(3) p    Your Monitoring Officer for this project is Grace Harper.
    And the user clicks the button/link                link = Monitoring Officer
    Then the user should see the element               jQuery = .success-alert:contains("We have assigned a Monitoring Officer to your project.")
    And the user should see the element                jQuery = .govuk-body:contains("Grace Harper")
    And the user should see the element                jQuery = .govuk-body:contains("${test_mailbox_two}+monitoringofficer@gmail.com")
    And the user should see the element                jQuery = .govuk-body:contains("08549731414")
    When the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    Then the user should see the element               css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Setup]    Log in as a different user      &{collaborator1_credentials_bd}
    When the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element       link = Monitoring Officer
    And the user should not see the element    link = Spend profile
    And the user should not see the element    link = Grant offer letter

Existing Monitoring Officer can sign in and see projects that they are assigned to
    [Documentation]    IFS-3977  IFS-3978
    [Tags]  HappyPath
    Given log in as a different user          &{monitoring_officer_one_credentials}
    Then the user should see the element      jQuery = .projects-in-setup h2:contains("Projects in setup") ~ ul li a:contains("Magic material")

Monitoring officer see the project setup veiw for assigned project
    [Documentation]  IFS-4209
    Given the user clicks the button/link    link = Magic material
    Then the user should see the project set view

MO sees the application feedback
    [Documentation]  IFS-5298
    Given the user clicks the button/link  link = view application feedback
    Then the user should see the element   jQuery = h1:contains("Feedback overview")

Monitoring Officer cannot see projects if they are not assigned to them
    [Documentation]    IFS-3978
    Given log in as a different user            &{monitoring_officer_two_credentials}
    Then the user should not see the element    .projects-in-setup
    [Teardown]  logout as user

# Please note that the below test cases refer to the new Monitoring Officer role functionality so the test cases above may become deprecated
# When adding new test cases here please make sure that anything unneccessary is removed from above.

MO create account: validations
    [Documentation]  IFS-5031
    Given the user navigates to the page     ${server}/management/monitoring-officer/hash123/register
    When the user checks for validations
    Then the user should see client side validations triggered correctly
    And the user should see server side validations triggered correctly

Create account flow: MO
    [Documentation]  IFS-5031
    Given MO enter details and create account
    When the user clicks the button/link      link = Sign into your account
    And Logging in and Error Checking         tom@poly.io  ${short_password}
    Then the user should see the element      jQuery = h1:contains("Applications")
    [Teardown]  Get user id and set as suite variable  ${New_Mo}

Comp admin assign project to new MO
    [Documentation]  IFS-5031
    [Setup]  log in as a different user                        &{Comp_admin1_credentials}
    Given the user navigates to the page                       ${server}/project-setup-management/monitoring-officer/${userId}/projects
    When comp admin assign and remove project to MO
    And the user selects the option from the drop-down menu    ${Assign_Project2_ID} - ${Assign_Project2}  id = projectId
    And the user clicks the button/link                        jQuery = button:contains("Assign")
    Then the user should see the element                       jQuery = td:contains("${Assign_Project2_ID}") ~ td:contains("Remove")

Link to Application
    [Documentation]  IFS-5031
    Given the user clicks the button/link  link = ${Assign_Project2_ID}
    Then the user should see the element    jQuery = h1:contains("Application overview") ~ form section dd:contains("${Assign_Project2}")

New MO see the project setup view for assigned project
    [Documentation]  IFS-5031
    [Setup]  log in as a different user    tom@poly.io  ${short_password}
    Given the user clicks the button/link  link = ${Assign_Project2}
    Then the user should see the project set view

*** Keywords ***
standard verification for email address follows
    the user enters text to a text field    id = emailAddress    ${invalid_email_plain}
    the user should see a field error       ${enter_a_valid_email}
    the user enters text to a text field    id = emailAddress    ${invalid_email_symbols}
    the user should see a field error       ${enter_a_valid_email}
    the user enters text to a text field    id = emailAddress    ${invalid_email_no_username}
    the user should see a field error       ${enter_a_valid_email}
    the user enters text to a text field    id = emailAddress    ${invalid_email_format}
    the user should see a field error       ${enter_a_valid_email}
    the user enters text to a text field    id = emailAddress    ${invalid_email_no_at}
    the user should see a field error       ${enter_a_valid_email}

the user should not see the validation error
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css = input
    Set Focus To Element      jQuery = .govuk-button:contains("Assign Monitoring Officer")
    Wait for autosave
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Wait Until Element Does Not Contain Without Screenshots    css = .govuk-error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}

the user edits the MO details
    The user enters text to a text field    id = firstName    Grace
    The user enters text to a text field    id = lastName    Harper
    The user enters text to a text field    id = emailAddress    ${test_mailbox_two}+monitoringofficer@gmail.com
    The user enters text to a text field    id = phoneNumber    08549731414
    the user clicks the button/link         jQuery = .govuk-button[type = "submit"]:contains("Assign Monitoring Officer")
    the user clicks the button/link         jQuery = .modal-assign-mo button:contains("Assign Monitoring Officer")

the user can see the changed MO details
    The user should see the element             css = .success-alert
    the user should see the element             jQuery = .success-alert:contains("A Monitoring Officer has been assigned.")
    Textfield Should Contain                    id = firstName    Grace
    Textfield Should Contain                    id = lastName    Harper

Custom suite setup
    Connect to database  @{database}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}

the user should see the correct address
    the user should see the element       jQuery = p:contains("3722 Corben Point")
    the user should see the element       jQuery = p:contains("London")
    the user should see the element       jQuery = p:contains("London")
    the user should see the element       jQuery = p:contains("E17 5LR")

the user should see the project set view
    the user should see the element    jQuery = a:contains("Project details")
    the user should see the element    jQuery = a:contains("Documents")
    the user should see the element    jQuery = .progress-list .read-only h2:contains("Bank details")
    the user should see the element    jQuery = .progress-list .read-only h2:contains("Finance checks")
    the user should see the element    jQuery = .progress-list .read-only h2:contains("Spend profile")
    the user should see the element    jQuery = .progress-list .read-only h2:contains("Grant offer letter")

MO enter details and create account
    the user enters text to a text field    id = firstName  Tom
    the user enters text to a text field    id = lastName   Poly
    the user enters text to a text field    id = phoneNumber  123456789
    the user enters text to a text field    id = password  ${short_password}
    the user should not see an error in the page
    the user clicks the button/link         jQuery = button:contains("Create account")

the user checks for validations
    the user enters text to a text field    id = firstName  ${empty}
    the user enters text to a text field    id = lastName   ${empty}
    the user enters text to a text field    id = phoneNumber  ${empty}
    the user enters text to a text field    id = password  ${empty}

the user should see client side validations triggered correctly
    the user should see a field error    Please enter a first name.
    the user should see a field error    Please enter a last name.
    the user should see a field error    Please enter a phone number.
    the user should see a field error    Password must contain at least one lower case letter.

the user should see server side validations triggered correctly
    the user clicks the button/link                  jQuery = button:contains("Create account")
    the user should see a field and summary error    Please enter a first name.
    the user should see a field and summary error    Your first name should have at least 2 characters.
    the user should see a field and summary error    Please enter a last name.
    the user should see a field and summary error    Your last name should have at least 2 characters.
    the user should see a field and summary error    Please enter a phone number.
    the user should see a field and summary error    Please enter a valid phone number between 8 and 20 digits.
    the user should see a field and summary error    Password must be at least 8 characters.
    the user should see a field and summary error    Please enter your password.

comp admin assign and remove project to MO
    the user clicks the button/link     jQuery = button:contains("Assign")
    the user should not see assigned project in Select a project to assign drop down
    the user should see the element     jQuery = span:contains("1") ~ span:contains("assigned projects")
    the user clicks the button/link     jQuery = td:contains("${Assign_Project}") ~ td a:contains("Remove")

the user should not see assigned project in Select a project to assign drop down
    the user clicks the button/link        css = .govuk-select
    the user should not see the element    jQuery = .govuk-select option:contains("${Assign_Project}")

Custom suite teardown
    the user closes the browser
    Disconnect from database