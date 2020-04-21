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
...
...               IFS-2642 Resend invites in Project Setup
...
...               IFS-2920 Project details: Project location per partner
...
...               IFS-5920 Acceptance tests for T's and C's
...
...               IFS-5758 Adding finance reviewer to project
...
...               IFS-6751  Remove ability to amend project start date externally in Project Setup
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Project Setup  Applicant
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${invitedFinanceContact}  ${test_mailbox_one}+invitedfinancecontact@gmail.com
${user_email}  phillip.ramos@katz.example.com
${pmEmailId}  ${user_ids['${user_email}']}
${projectSetupCompMgtDetailsPage}  ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/1/details

# This suite uses the Magic material project

*** Test Cases ***
Internal finance can see competition terms and conditions
    [Documentation]  IFS-5920
    [Tags]
    Given the internal user should see read only view of terms and conditions   ${Internal_Competition_Status}   ${PS_PD_Application_Id}  Terms and conditions of an Innovate UK grant award
    Then the user navigates to the page           ${Internal_Competition_Status}

Competition admin can see competition terms and conditions
    [Documentation]  IFS-5920
    [Tags]
    Given Log in as a different user            &{Comp_admin1_credentials}
    Then the internal user should see read only view of terms and conditions   ${Internal_Competition_Status}  ${PS_PD_Application_Id}  Terms and conditions of an Innovate UK grant award

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049 INFUND-5507 INFUND-5543
    [Tags]
    Given the user navigates to the page    ${Internal_Competition_Status}
    Then the competition admin should see the status of each project setup stage
    And Internal user can view project details via the clickable 'hour glass' for Project details

Non-lead partner can see the project setup page
    [Documentation]    INFUND-2612 INFUND-2621 INFUND-4428 INFUND-5827 INFUND-5805 INFUND-7432
    [Tags]  HappyPath
    [Setup]  log in as a different user             &{collaborator1_credentials}
    Given the user should see the grant award terms and conditions
    Then the user should see the project setup stages
    And the user checks for project detail status on team status page   Ludlow
    And Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    And the user can see the application overview

Lead partner can see the project setup page
    [Documentation]    INFUND-2612 INFUND-2621 INFUND-5827 INFUND-5805 INFUND-4428
    [Tags]
    [Setup]    log in as a different user           &{lead_applicant_credentials}
    Given the user navigates to the page             ${Project_In_Setup_Page}
    Then the user should see the project setup stages
    And the user checks for project detail status on team status page   ${EMPIRE_LTD_NAME}

Lead partner can click the Dashboard link
    [Documentation]    INFUND-4426
    [Tags]
    Given the user clicks the button/link    link = Dashboard
    Then the user should see the element    jQuery = h2:contains("Set up your project")
    And the user can see the application overview

Lead partner is able to see finances without an error
    [Documentation]  INFUND-7634
    [Tags]
    Given the user clicks the button/link    jQuery = button:contains("Finances summary")
    When the user clicks the button/link     link = View finances
    Then the user should see the element     jQuery = h2:contains("Finance summary")
    And the user clicks the button/link      link = Back to feedback overview

Lead partner can see the overview of the project details
    [Documentation]    INFUND-2613
    [Tags]
    Given the user navigates to the page   ${Project_In_Setup_Page}
    When the user clicks the button/link   link = Project details
    Then the user should see the project details

Lead partner can not change the Start Date
    [Documentation]    IFS-6751
    [Tags]  HappyPath
    Given the user logs in and navigates to project details     &{lead_applicant_credentials}
    When the user clicks the button/link                         link = Target start date
    Then the user should be able to see change start date instructions

Lead partner can change the project address
    [Documentation]    INFUND-3157 INFUND-2165
    [Tags]  HappyPath
    Given the user navigates to the page             ${Project_In_Setup_Details_Page}
    And the user clicks the button/link              link = Correspondence address
    Then the user updates the correspondence address

IFS Admin is able to edit the Start Date
    [Documentation]  IFS-6751
    [Setup]  log in as a different user    &{ifs_admin_user_credentials}
    Given the user navigates to the page   ${projectSetupCompMgtDetailsPage}
    When the user checks for target start date validation
    Then the user should see the element    css = .day [readonly]
    And the user save the target start date

    # Please note that the following Test Cases regarding story INFUND-7090, have to remain in Project Details suite
    # and not in Bank Details. Because for this scenario there are testing data for project 4.
Non lead partner not eligible for funding
    [Documentation]    INFUND-7090, INFUND-7174
    [Tags]
    Given log in as a different user            &{collaborator1_credentials}
    When the user navigates to the page         ${Project_In_Setup_Page}
    And the user should see the element         jQuery = li:contains("Project details") .status-complete
    Then the user should not see the element    jQuery = li:contains("Documents") .status-action-required
    When The user navigates to the page and gets a custom error message     ${Project_In_Setup_Page}/bank-details    ${403_error_message}
    When the user navigates to the page         ${Project_In_Setup_Page}
    And the user clicks the button/link         link = View the status of partners
    Then the user should be redirected to the correct page    ${Project_In_Setup_Team_Status_Page}
    And the user should see the element         jQuery = th:contains("Ludlow") + .ok + .action +.na

Other partners can see who needs to provide Bank Details
    [Documentation]    INFUND-7090
    [Tags]  HappyPath
    [Setup]    log in as a different user   &{lead_applicant_credentials}
    Given the user navigates to the page    ${Project_In_Setup_Team_Status_Page}
    Then the user should see the element    jQuery = th:contains("Ludlow") + .ok + .action +.na
    And the user should see the element     jQuery = th:contains("EGGS") + .ok + .action +.na

Non-lead partner cannot change start date or project address
    [Documentation]    INFUND-3157
    [Setup]  log in as a different user        &{collaborator1_credentials}
    Given the user navigates to the page       ${Project_In_Setup_Page}
    Then the user should not see the element   link = Target start date
    And the user should not see the element    link = Correspondence address

Validation for project location
    [Documentation]   IFS-2920
    [Setup]  the user logs in and navigates to project details     &{lead_applicant_credentials}
    Given the user clicks the button/link               link = Edit
    And the user enters text to a text field            css = #postcode  ${empty}
    And Set Focus To Element                            link = Contact us
    And the user should see a field error               ${empty_field_warning_message}
    When the user clicks the button/link                css = button[type = "submit"]
    Then the user should see a field and summary error  ${empty_field_warning_message}

Project details submission flow
    [Documentation]    INFUND-3381, INFUND-2621, INFUND-5827
    [Tags]  HappyPath
    [Setup]  the user logs in and navigates to project details     &{lead_applicant_credentials}
    Given the user updates the project location in project setup   ${Project_In_Setup_Details_Page}
    And the user clicks the button/link   link = Project details
    When all the fields are completed
    Then the user navigates to the page    ${Project_In_Setup_Page}
    And the user should see the element  jQuery = li:contains("Project details") .status-complete

Lead partner can see the status update when all Project details are submitted
    [Documentation]    INFUND-5827
    [Tags]
    Given the user navigates to the page    ${Project_In_Setup_Page}
    And the user should see the element     jQuery = li:contains("Project details") .status-complete
    When the user clicks the button/link    link = View the status of partners
    Then the user should see the element    id = table-project-status
    And the user should see the element     jQuery = th:contains("Empire") + .ok

Project details links are still enabled after submission
    [Documentation]    INFUND-3381
    [Tags]
    Given the user navigates to the page    ${Project_In_Setup_Details_Page}
    When all the fields are completed
    Then The user should see the element    link = Target start date
    And the user should see the element     link = Correspondence address

All partners can view submitted project details
    [Documentation]    INFUND-3382, INFUND-2621
    [Tags]
    Given the non-lead partner see the completed project details
    And the lead partner see the completed project details

Non-lead partner cannot change any project details
    [Documentation]    INFUND-2619
    [Setup]    log in as a different user           &{collaborator1_credentials}
    Given the user navigates to the page            ${Project_In_Setup_Page}
    Then the non-lead partner cannot changes any project details

User is able to accept new site terms and conditions
    [Documentation]  IFS-3093
    [Tags]
    [Setup]  Delete user from terms and conditions database   ${pmEmailId}
    Log in as a different user             ${user_email}   ${short_password}
    When the user selects the checkbox     agree
    And the user clicks the button/link    css = button[type = "submit"]
    Then the user should see the element   jQuery = h1:contains(${APPLICANT_DASHBOARD_TITLE})

Add Finance reviewer validations
    [Documentation]  IFS-5758
    [Setup]  log in as a different user                 &{ifs_admin_user_credentials}
    Given the user navigates to the page                ${projectSetupCompMgtDetailsPage}
    When the user clicks the button/link                jQuery = a:contains("Edit")
    And the user clicks the button/link                 jQuery = button:contains("Update finance reviewer")
    Then the user should see a field and summary error  Enter the name of the finance reviewer.

IFS Admin is able to add a Finance reviewer
    [Documentation]  IFS-5758
    Given the user selects finance reviewer   Arden Pimenta
    When the user clicks the button/link      jQuery = button:contains("Update finance reviewer")
    Then the user should see the element      jQuery = p:contains("Innovate UK project finance reviewer has been updated.")
    And the user should see the element       jQuery = tr:contains("Arden Pimenta")

IFS Admin is able to edit Finance reviewer
    [Documentation]  IFS-5758
    Given the user clicks the button/link          jQuery = a:contains("Edit")
    When the user selects finance reviewer         Rianne Almeida
    And the user clicks the button/link            jQuery = button:contains("Update finance reviewer")
    Then the user should see the element           jQuery = tr:contains("Rianne Almeida")

*** Keywords ***
The user should be able to see change start date instructions
    the user should not see the element         css = input.govuk-input[name="projectStartDate.monthValue"]
    the user should not see the element         css = input.govuk-input[name="projectStartDate.year"]
    the user should see the element             jQuery = p:contains("To request a change to the project start date please contact Innovate UK.")

The user selects finance reviewer
    [Arguments]   ${FlName}
    input text                          id = userId    ${FlName}
    the user clicks the button/link     jQuery = ul li:contains("${FlName}")

All the fields are completed
    the user should see the element   jQuery = td:contains("Correspondence address")~ td strong:contains("Complete")

Custom suite setup
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    Connect to database  @{database}
    the user logs-in in new browser          &{internal_finance_credentials}

the user accepts invitation
    [Arguments]  ${email}  ${title}  ${pattern}
    the user reads his email and clicks the link  ${email}  ${title}  ${pattern}
    the user should see the element               jQuery = h1:contains("Join a project")
    the user clicks the button/link               link = Create account

the invited user signs in
    [Arguments]  ${email}  ${name}  ${famName}
    the user reads his email and clicks the link    ${email}  Please verify your email address  Dear ${name} ${famName}
    the user should see the element                 jQuery = h1:contains("Account verified")
    the user clicks the button/link                 jQuery = p:contains("Sign in to your Innovation Funding Service account.")~ a:contains("Sign in")
    Logging in and Error Checking                   ${email}  ${correct_password}

The user resends and clicks the button
    [Arguments]  ${Resend_OR_Cancel}
    The user clicks the button/link    jQuery = label:contains("John Smith") ~ a:contains("Resend invite")
    The user should see the element    jQuery = h2:contains("Resend invite to team member")
    The user clicks the button/link    jQuery = button:contains("${Resend_OR_Cancel}")

the user should see the project setup stages
    the user should see the element    link = Project details
    the user should see the element    jQuery = h2:contains("Monitoring Officer")
    the user should see the element    jQuery = h2:contains("Bank details")
    the user should see the element    jQuery = h2:contains("Finance checks")
    the user should see the element    jQuery = h2:contains("Spend profile")
    the user should see the element    link = Documents
    the user should see the element    jQuery = h2:contains("Grant offer letter")

the competition admin should see the status of each project setup stage
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td.waiting:nth-child(2)                                  # Project details
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td.waiting:nth-child(3)                                  # Project Team
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td.waiting:nth-child(4)                                  # Documents
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td:nth-child(5):contains("Stage is not yet available")   # MO
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td:nth-child(6):contains("Stage is not yet available")   # Bank details
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td.action:nth-child(7)                                   # Finance checks
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td:nth-child(8):contains("Stage is not yet available")                         # Spend Profile
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td:nth-child(9):contains("Stage is not yet available")                         # GOL

Internal user can view project details via the clickable 'hour glass' for Project details
    the user clicks the button/link    jQuery = tr:contains("${PS_PD_Application_Title}") td.waiting:nth-child(2) a
    the user should see the element    jQuery = h1:contains("Project details")
    the user clicks the button/link    link = Back to project setup
    the user should see the element    jQuery = tr:contains("${PS_PD_Application_Title}") td.waiting:nth-child(2)  # Project details

the user should see the grant award terms and conditions
    the user clicks the button/link        link = ${PS_PD_Application_Title}
    the user clicks the button/link        link = view the award terms and conditions
    the user should see the element        jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    the user goes back to the previous page

the user checks for project detail status on team status page
    [Arguments]  ${partner}
    the user clicks the button/link            link = View the status of partners
    the user should be redirected to the correct page    ${Project_In_Setup_Page}/team-status
    the user should see the element            jQuery = h1:contains("Project team status")
    the user should see the element            jQuery = tr:contains("${partner}") td.action:nth-of-type(2)

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    the user should not see the element    link = Monitoring Officer
    the user should not see the element    link = Bank details
    the user should not see the element    link = Finance checks
    the user should not see the element    link = Spend profile
    the user should not see the element    link = Grant offer letter

the user can see the application overview
    the user navigates to the page       ${Project_In_Setup_Page}
    the user clicks the button/link      link = view application feedback
    the user should see the element      jQuery = .success-alert:contains("Congratulations, your application has been successful") ~ h2:contains("Application details")

the user should see the project details
    the user should see the element    jQuery = p:contains("As the lead you must complete these details on behalf of the overall project.")
    the user should see the element    link = Target start date
    the user should see the element    link = Correspondence address
    the user should see the element    jQuery = h2:contains("Project location")

the user logs in and navigates to project details
    [Arguments]  &{user_id}
    Log in as a different user       &{user_id}
    the user navigates to the page   ${Project_In_Setup_Details_Page}

the user checks for target start date validation
    the user clicks the button/link                 link = Target start date
    the user should see the element                 jQuery = h2:contains("Project duration") ~ p:contains("10 months")
    the user enters text to a text field            id = projectStartDate-date_year    2019
    the user clicks the button/link                 jQuery = .govuk-button:contains("Save")
    the user should see a field and summary error   Please enter a future date.

the user save the target start date
    the user enters text to a text field       id = projectStartDate-date_month    1
    the user enters text to a text field       id = projectStartDate-date_year    ${nextyear}
    the user clicks the button/link            jQuery = .govuk-button:contains("Save")
    the user should see the element            jQuery = td:contains("1 Jan ${nextyear}")

the user select exisitng user as project manager
    the user should see the element                jQuery = .govuk-hint:contains("Who will be the Project Manager for your project?")
    the user selects the radio button             projectManager    new
    the user should see the element               id = invite-project-manager
    the user selects the radio button             projectManager    projectManager1
    the user should not see the element           id = project-manager    # testing that the element disappears when the option is deselected

the user should see client side validations triggered correctly
    [Arguments]  ${name_id}  ${email_id}  ${option}
    the user enters text to a text field        id = ${name_id}   ${empty}
    the user enters text to a text field        id = ${email_id}   ${empty}
    Set Focus To Element                        id = ${option}
    the user should see a field error           ${enter_a_valid_name}
    the user should see a field error           Please enter an email address.

the user should see server side validations triggered correctly
    [Arguments]  ${option}
    the user clicks the button/link             id = ${option}
    the user should see a field error           ${enter_a_valid_name}
    the user should see a field error           Please enter an email address.

the user should see validations triggered correctly
    the user should see a field and summary error   ${enter_a_first_name}
    the user should see a field and summary error   ${enter_a_last_name}
    the user should see a field and summary error   To create a new account you must agree to the website terms and conditions.
    the user should see a field and summary error   Please enter your password.

the non-lead partner see the completed project details
    the user logs in and navigates to project details   &{collaborator1_credentials}
    the user should see the element                     jQuery = td:contains("${organisationLudlowName}")
    all the fields are completed
    the user navigates to the page                      ${Project_In_Setup_Page}
    the user clicks the button/link                     link = View the status of partners
    the user should see the element                     jQuery = tr:contains("Empire") td.ok:nth-of-type(1)

the lead partner see the completed project details
    the user logs in and navigates to project details      &{lead_applicant_credentials}
    the user should see the element             jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}")
    all the fields are completed
    the user navigates to the page               ${Project_In_Setup_Page}
    the user clicks the button/link              link = View the status of partners
    the user should see the element              jQuery = tr:contains("Empire") td.ok:nth-of-type(1)

the non-lead partner cannot changes any project details
    the user clicks the button/link             link = Project details
    the user should see the element             jQuery = td:contains("Target start date") ~ td:contains("1 Jan ${nextyear}")
    the user should not see the element         link = Target start date
    the user should see the element             jQuery = td:contains("Correspondence address") ~ td:contains("Montrose House 1, Neston, CH64 3RU")
    the user should not see the element         link = Correspondence address
    the user navigates to the page              ${Project_Start_Date_Page}
    the user should be able to see change start date instructions
    the user navigates to the page and gets a custom error message    ${Project_Address_Page}    ${403_error_message}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database