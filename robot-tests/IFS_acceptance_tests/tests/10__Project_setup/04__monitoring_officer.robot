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
...
...               IFS-5428 Search by email for Monitoring Officer - Existing MOs
...
...               IFS-5088 Use auto-complete search to improve the assign Assessors/ Monitoring Officers/ Stakeholders journey
...
...               IFS-5104 Create a new Monitoring Officer from existing user in another role
...
...               IFS-5070 Add Monitoring Officer to multiple-role dashboard
...
...               IFS-4208 Create pending registration for new Monitoring Officer account
...
...               IFS-5032 MO assigned to project - Email notification
...
...               IFS-5418 Assign MO: Internal navigation
...
...               IFS-5686 MO - external user view
...
...               IFS-5859 MO View: Show Spend Profile navigation of all partners
...
...               IFS-8753 515 - 73652 - Monitoring Officer unable to view application feedback
...
...               IFS-8958  SBRI Milestones - Application overview / summary
...
...               IFS-9576 MO documents: 'Project setup' list - task management and filtering
...
...               IFS-9578 MO documents: design changes for other roles (not MO or Project manager)
...
...               IFS-9774 Investigate if its possible to fix AT's failure due to IDP upgrade
...
...               IFS-10047 MO documents: Monitor project page - View status of partners
...
...               IFS-9925 MO view of SBRI milestones in project setup
...
...               IFS-9673 MO improvements: visibility of project eligible costs
...
...               IFS-10022 MO improvements: visibility of finance status for individual partners
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${Successful_Monitoring_Officer_Page}     ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/monitoring-officer
${Assign_Project}                         Climate control solution
${Assign_Project_ID}                      ${application_ids["${Assign_Project}"]}
${Assign_Project2}                        High Performance Gasoline Stratified
${sbri_applicaton_name}                   SBRI application
${sbri_application_id}                    ${application_ids["${sbri_applicaton_name}"]}
${sbri_project_id}                        ${project_ids["${sbri_applicaton_name}"]}
${Assign_Project2_ID}                     ${application_ids["${Assign_Project2}"]}
${New_Mo}                                 tom@poly.io
${PSCapplicationTitle}                    PSC application 15
${PSCapplicationID}                       ${application_ids["${PSCapplicationTitle}"]}
${PSC_Competition_Name}                   Project Setup Comp 15
${PSC_Competition_Id}                     ${competition_ids["${PSC_Competition_Name}"]}
${financeApplicationTitle}                PSC application 18
${financeApplicationID}                   ${application_ids["${financeapplicationTitle}"]}
${financeProjectID}                       ${project_ids["${financeApplicationTitle}"]}
${financeCompetitionName}                 Project Setup Comp 18
${financeCompetitionId}                   ${competition_ids["${financeCompetitionName}"]}
${organisationID}                         ${organisation_ids["${organisationWardName}"]}
${organisationRedPlanetID}                ${organisation_ids["${organisationRedName}"]}
${organisationSmithZoneID}                ${organisation_ids["${organisationSmithName}"]} 

*** Test Cases ***
Before Monitoring Officer is assigned
    [Documentation]    INFUND-2634, INFUND-2621, INFUND-6706
    [Tags]  HappyPath
    [Setup]    The user logs-in in new browser          &{lead_applicant_credentials_bd}
    Given the user navigates to the page                ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user should see the element                 jQuery = h2:contains("Monitoring Officer") ~ p:contains("We will assign the project a Monitoring Officer.")
    And the user should not see the element             css = ul li.complete:nth-child(4)
    And the user should see the element                 css = ul li.waiting:nth-child(4)
    When the user clicks the button/link                link = Monitoring Officer
    Then the user should see the element                jQuery = p:contains("Your project has not yet been assigned a monitoring officer.")
    And the user should not see the element             jQuery = .success-alert:contains("We have assigned a monitoring officer to your project.")
    When the user navigates to the page                 ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    And the user should see the element                 css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(4)
    
Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5507,INFUND-5543
    [Setup]    log in as a different user       &{Comp_admin1_credentials}
    When the user navigates to the page         ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element        css = #table-project-status tr:nth-of-type(4) td:nth-of-type(1)                               # Project details
    And the user should see the element         css = #table-project-status tr:nth-of-type(4) td:nth-of-type(2)                               # Project team
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(3)                       # Documents
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(4)                       # Monitoring Officer
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(5)                       # Bank details
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td.govuk-table__cell.status.action    # Finance checks
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(7)                       # Spend Profile
    And the user should see the element         css = #table-project-status > tbody > tr:nth-child(4) > td:nth-child(8)                       # GOL

Search for an MO
    [Documentation]  IFS-5428  IFS-5418  IFS-5686
    [Setup]  log in as a different user             &{internal_finance_credentials}
    Given the user navigates to the page            ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    When the user clicks the button/link            css = #table-project-status tr:nth-child(4) > td:nth-child(5) a
    Then search for MO                              Orvill  Orville Gibbs
    And the user should see the element             jQuery = span:contains("Assign projects to Monitoring Officer")
    And the internal user assign project to MO      ${Grade_Crossing_Applicaiton_No}  ${Grade_Crossing_Application_Title}

MO details can be added
    [Documentation]    INFUND-2630, INFUND-6706, INFUND-2632
    [Tags]  HappyPath
    Given Log in as a different user                     &{lead_applicant_credentials_bd}
    And the user navigates to the page                   ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    And the user should see the element                  css = ul li.complete:nth-child(4)
    And the user should see the text in the element      css = ul li.complete:nth-child(4) p    Your Monitoring Officer for this project is Orville Gibbs.
    And the user clicks the button/link                  link = View the status of partners
    And the user should see the element                  css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

MO details can be edited and viewed in the Set up your project page
    [Documentation]    INFUND-2630, INFUND-2621, INFUND-2634
    [Tags]  HappyPath
    [Setup]    Log in as a different user              &{Comp_admin1_credentials}
    Given the user navigates to the page               ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    And the user clicks the button/link                css = #table-project-status tr:nth-child(4) > td:nth-child(5) a
    When the user clicks the button/link               link = Change monitoring officer
    And the user edits the MO details
    When Log in as a different user                    &{lead_applicant_credentials_bd}
    Then the user should see assigned MO details

MO details accessible/seen by all partners
    [Documentation]    INFUND-2634, INFUND-2621
    [Tags]  HappyPath
    Given Log in as a different user                   &{collaborator1_credentials_bd}
    When the user should see assigned MO details
    Then Log in as a different user                    &{collaborator2_credentials_bd}
    And the user should see assigned MO details

Links to other sections in Project setup dependent on project details (applicable for Lead/ partner)
    [Documentation]    INFUND-4428
    [Setup]    Log in as a different user      &{collaborator1_credentials_bd}
    When the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    Then the user should see the element       link = Monitoring Officer
    And the user should not see the element    link = Spend profile
    And the user should not see the element    link = Grant offer letter

Existing Monitoring Officer can sign in and see projects that they are assigned to
    [Documentation]    IFS-3977  IFS-3978  IFS-9576
    [Tags]  HappyPath
    Given log in as a different user                            &{monitoring_officer_one_credentials}
    And the user clicks the project setup tile if displayed
    When the user selects the checkbox                          previousProject
    And the user clicks the button/link                         id = update-documents-results-button
    Then the user should see the project status                 ${PS_LP_Application_Title}

Monitoring officer see the project setup veiw for assigned project
    [Documentation]  IFS-4209  IFS-5859
    Given the user clicks the button/link    link = ${PS_LP_Application_Title}
    When the user should see the project set view
    Then the MO user is able to access all of the links

MO sees the application feedback
    [Documentation]  IFS-5298  IFS-8066
    Given the user clicks the button/link       link = view application feedback
    Then the user should see the element        jQuery = h1:contains("Application overview")

Monitoring Officer cannot see projects if they are not assigned to them
    [Documentation]    IFS-3978
    Given log in as a different user            &{monitoring_officer_two_credentials}
    Then the user should not see the element    .projects-in-setup

# Please note that the below test cases refer to the new Monitoring Officer role functionality so the test cases above may become deprecated
# When adding new test cases here please make sure that anything unneccessary is removed from above.

Add MO client validations
    [Documentation]  IFS-5428
    [Setup]  log in as a different user        &{Comp_admin1_credentials}
    Given the user navigates to the page       ${server}/project-setup-management/monitoring-officer/view-all
    When The user clicks the button/link       link = Add a monitoring officer
    And the user enters text to a text field   id = emailAddress  ${EMPTY}
    Then the user should see a field error     Please enter an email address.

Add MO server validations
    [Documentation]  IFS-5428
    Given the user enters text to a text field          id = emailAddress  ${invalid_email_plain}
    When the user clicks the button/link                jQuery = button[type="submit"]
    Then the user should see a field and summary error  ${enter_a_valid_email}

Add MO - existing MO
    [Documentation]  IFS-5428
    Given the user enters text to a text field  id = emailAddress  ${monitoring_officer_one_credentials["email"]}
    And the user cannot see a validation error in the page
    When the user clicks the button/link        jQuery = button[type="submit"]
    Then the user should see the element        jQuery = span:contains("Assign projects to Monitoring Officer")

Add New MO details - client and server side validations
    [Documentation]  IFS-4208
    [Setup]  the user adds MO email address
    Given the user checks for validations
    Then the user should see client side validations
    And the user should see server side validations   Add monitoring officer

Comp admin adds new MO
    [Documentation]  IFS-4208
    Given the user enters the details
    Then the user clicks the button/link         jQUery = button:contains("Add monitoring officer")

Comp admin assign project to new MO
    [Documentation]  IFS-5031  IFS-5088  IFS-4208
    #Given Remove project from assigned MO
    And search for MO                                 Tom  Tom Poly
    When the internal user assign project to MO       ${Assign_Project2_ID}  ${Assign_Project2}
    Then the user should see the element              jQuery = td:contains("${Assign_Project2_ID}") ~ td:contains("Remove")

Link to Application
    [Documentation]  IFS-5031
    Given the user clicks the button/link   link = ${Assign_Project2_ID}
    Then the user should see the element    jQuery = h1:contains("Application overview")
    And the user should see the element     jQuery = dd:contains("${Assign_Project2}")
    [Teardown]  logout as user

MO create account: validations
    [Documentation]  IFS-5031  IFS-5032
    Given the user reads his email and clicks the link   tom@poly.io   ${INFORM_COMPETITION_NAME}   Welcome to the monitoring team  1
    When the user checks for details and password validations
    Then the user should see client side validations triggered correctly
    And the user should see server side validations triggered correctly

Create account flow: MO
    [Documentation]  IFS-5031
    Given MO enter details and create account
    When the user clicks the button/link      link = Sign into your account
    Then Logging in and Error Checking        tom@poly.io   ${short_password}
    And the user should see the element       jQuery = h1:contains("Project setup")

New MO see the project setup view for assigned project
    [Documentation]  IFS-5031
    Given the user clicks the button/link  link = ${Assign_Project2}
    Then the user should see the project set view

Mo is able to view application feedback on a competition which as been through assessment and interview panels
    [Documentation]  IFS-7230  IFS-8066
    [Setup]  release feedback on inform comp
    Given the user clicks the button/link   link = view application feedback
    Then the user should see the element    jQuery = h1:contains("Application overview")

MO is able to download the appendix file
    [Documentation]  IFS-7230  IFS-9774
    Given log in as a different user                            &{monitoring_officer_one_credentials}
    And the user clicks the project setup tile if displayed
    And the user selects the checkbox                           previousProject
    And the user clicks the button/link                         id = update-documents-results-button
    And the user clicks the button/link                         link = ${PS_LP_Application_Title}
    When the user clicks the button/link                        link = view application feedback
    And the user clicks the button/link                         jQuery = button:contains("Technical approach")
    And the user clicks the button/link                         link = super-effy---super-efficient-forecasting-of-freight-yields-technical-approach.pdf (opens in a new window)
    And Select Window                                           NEW
    Then the user should not see internal server and forbidden errors
    And the user closes the last opened tab

Assign MO role to existing IFS user
    [Documentation]  IFS-5104
    [Setup]  log in as a different user         &{Comp_admin1_credentials}
    Given the user navigates to the page        ${server}/project-setup-management/monitoring-officer/search-by-email
    And the user enters text to a text field    id = emailAddress   ${assessor2_credentials["email"]}
    When the user clicks the button/link        jQuery = button:contains("Continue")
    Then the user should see exisitng IFS user details and add phone number
    And the user should see the element         jQuery = h1:contains("Felix Wilson") span:contains("Assign projects to Monitoring Officer")

Comp admin assign project existing IFS user MO
    [Documentation]  IFS-5104  IFS-5070  IFS-9576
    Given the internal user assign project to MO   ${Elbow_Grease_Application_No}  ${Elbow_Grease_Title}
    And logout as user
    Then the user logs in and checks for assigned projects

Internal user assigns a MO to a new project and removes a partner organisation
    [Documentation]    IFS-8753
    When Log in as a different user                      &{internal_finance_credentials}
    Then internal user assigns MO to application         ${PSCapplicationID}  ${PSCapplicationTitle}  Orvill  Orville Gibbs
    And Internal user removes a partner organisation

MO can now check the application feedback
    [Documentation]    IFS-8753
    Given Log in as a different user                           &{monitoring_officer_one_credentials}
    And the user clicks the project setup tile if displayed
    When The user clicks the button/link                       link = ${PSCapplicationTitle}
    and the user clicks the button/link                        link = view application feedback
    Then The user should see the element                       jQuery = h1:contains("Application overview")

MO can now view payment milestones in SBRI application
    [Documentation]   IFS-8958
    Given Requesting IDs of this application
    #When the SBRI MO assignee has been changed
    When Log in as a different user                                         &{monitoring_officer_one_credentials}
    And the user navigates to the page                                      ${server}/project-setup/project/${sbri_projectID}
    And the user clicks the button/link                                     link = view application feedback
    Then the payment milestone table is visible in application overview

Change MO for the project
    [Documentation]   IFS-9578
    Given Log in as a different user               &{Comp_admin1_credentials}
    When the user navigates to the page            ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/monitoring-officer
    And the user clicks the button/link            jQuery = a:contains("Change monitoring officer")
    And search for MO                              Nilesh  Nilesh Patti
    And the user clicks the button/link            jQuery = a:contains("Remove")
    And the user clicks the button/link            jQuery = a:contains("Back to assign monitoring officers")
    Then search for MO                             Orvill  Orville Gibbs
    And the internal user assign project to MO     ${Grade_Crossing_Applicaiton_No}   ${Grade_Crossing_Application_Title}

MO can see the link to the partners for collaborating applications only
    [Documentation]   IFS-10047
    Given Log in as a different user            &{monitoring_officer_one_credentials}
    And the user clicks the project setup tile if displayed
    And the user clicks the button/link         jQuery = a:contains('${Grade_Crossing_Application_Title}')
    When the user clicks the button/link        jQuery = a:contains('View the status of partners')
    Then the user should see the element        jQuery = h1:contains('Project team status')
    And the user clicks the button/link         id = dashboard-navigation-link
    And the user clicks the project setup tile if displayed
    And the user clicks the button/link         jQuery = a:contains('${sbri_applicaton_name}')
    And the user should not see the element     jQuery = a:contains('View the status of partners')

MO can view payment milestones
    [Documentation]   IFS-9925
    Given log in as a different user                               &{monitoring_officer_one_credentials}
    When the user clicks the project setup tile if displayed
    And monitoring officer clicks on payment milestones link
    Then monitoring officer views detailed payment milestones

MO can view project finance changes
    [Documentation]   IFS-9673
    Given log in as a different user                               &{monitoring_officer_one_credentials}
    When the user clicks the project setup tile if displayed
    Then Monitoring officer checks changes to finances

MO can view summary of the project finances
    [Documentation]   IFS-9673
    Given The user navigates to the page      ${server}/project-setup/project/${sbri_project_id}/finance-check/read-only
    And the user clicks the button/link       link = see an overview
    Then The user should see the element      jQuery = h1:contains("Finance overview")
    And The user should see the element       jQuery = h3:contains("Overview") ~ h3:contains("Project cost breakdown")

MO can see status of the finance as a awaiting review for individual partners
    [Documentation]   IFS-10022
    Given log in as a different user                                        &{internal_finance_credentials}
    When Assign monitoring officer to project                               ${financeApplicationID}  ${financeApplicationTitle}
    And log in as a different user                                          &{monitoring_officer_one_credentials}
    And the user navigates to the page                                      ${server}/project-setup/project/${financeProjectID}/finance-check/read-only
    Then MO can view awaiting review as a finance status for all partners

MO can see status of the finance as a completed for individual partners
    [Documentation]   IFS-10022
    Given log in as a different user                                        &{internal_finance_credentials}
    When The user navigates to the page                                     ${server}/project-setup-management/competition/${financeCompetitionId}/status/all
    And The user clicks the button/link                                     link = Review
    And project finance approves Viability for                              ${organisationID}  ${financeProjectID}
    And project finance approves Viability for                              ${organisationSmithZoneID}  ${financeProjectID}
    And project finance approves Eligibility                                ${organisationID}  ${organisationRedPlanetID}  ${organisationSmithZoneID}  ${financeProjectID}
    And log in as a different user                                          &{monitoring_officer_one_credentials}
    And the user navigates to the page                                      ${server}/project-setup/project/46/finance-check/read-only
    Then MO can view completed as a finance status for individual partners     

*** Keywords ***
The MO user is able to access all of the links
    the user is able to see Project details section
    the user is able to see Documents section
    the user is able to see Monitoring officer section
    the user is able to see Spend profile section

The user is able to see Project details section
    the user clicks the button/link   link = Project details
    the user should see the element   jQuery = h1:contains("Project details")
    the user clicks the button/link   jQuery = a:contains("Set up your project")

The user is able to see Documents section
    the user clicks the button/link   link = Documents
    the user should see the element   jQuery = h1:contains("Documents")
    the user clicks the button/link   jQuery = a:contains("Back to monitor project")

The user is able to see Monitoring officer section
    the user clicks the button/link   jQuery = a:contains("Monitoring Officer")
    the user should see the element   jQuery = h1:contains("Monitoring Officer")
    the user clicks the button/link   jQuery = a:contains("Set up your project")

The user is able to see Spend profile section
    the user clicks the button/link   link = Spend profile
    the user clicks the button/link   jQuery = a:contains("Set up your project")

Requesting IDs of this application
    ${sbri_projectID} =  get project id by name     ${sbri_applicaton_name}
    Set suite variable    ${sbri_projectID}

The SBRI MO assignee has been changed
    log in as a different user                  &{Comp_admin1_credentials}
    #the user navigates to the page              ${server}/project-setup-management/project/${sbri_projectID}/monitoring-officer
    #the user clicks the button/link             link = Change monitoring officer
    internal user assigns mo to application     ${sbri_application_id}      ${sbri_applicaton_name}     Orvill    Orville Gibbs

Standard verification for email address follows
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

The user should not see the validation error
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css = input
    Set Focus To Element      jQuery = .govuk-button:contains("Assign Monitoring Officer")
    Wait for autosave
    ${STATUS}    ${VALUE} =    Run Keyword And Ignore Error Without Screenshots    Wait Until Element Does Not Contain Without Screenshots    css = .govuk-error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}

The user edits the MO details
    search for MO   Orvill  Orville Gibbs
    the user clicks the button/link    jQuery = td:contains("${Grade_Crossing_Applicaiton_No}") ~ td a:contains("Remove")
    the user clicks the button/link    link = Back to assign monitoring officers
    search for MO  Nilesh  Nilesh Patti
    the internal user assign project to MO   ${Grade_Crossing_Applicaiton_No}  ${Grade_Crossing_Application_Title}

Custom suite setup
    Connect to database  @{database}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}

The user should see the correct address
    the user should see the element       jQuery = p:contains("3722 Corben Point")
    the user should see the element       jQuery = p:contains("London")
    the user should see the element       jQuery = p:contains("London")
    the user should see the element       jQuery = p:contains("E17 5LR")

The user should see the project set view
    the user should see the element    jQuery = h1:contains("Monitor project")
    the user should see the element    jQuery = a:contains("Project details")
    the user should see the element    jQuery = a:contains("Documents")
    the user should see the element    jQuery = .progress-list .read-only h2:contains("Bank details")
    the user should see the element    jQuery = a:contains("Finance checks")
    the user should see the element    jQuery = .progress-list h2:contains("Spend profile")
    the user should see the element    jQuery = .progress-list h2:contains("Grant offer letter")

The user enters the details
    the user enters text to a text field    id = firstName  Tom
    the user enters text to a text field    id = lastName   Poly
    the user enters text to a text field    id = phoneNumber  123456789

The payment milestone table is visible in application overview
    the user expands the section                Funding breakdown
    the user should see the element             jQuery = h1:contains("Application overview")
    the user should see the element             jQuery = h3:contains("Payment milestones") + * tfoot:contains("£265,084") th:contains("100%")
    the user should see the element             jQuery = h3:contains("Project cost breakdown") + * td:contains("£265,084")

MO enter details and create account
    the user enters the details
    the user enters text to a text field    id = password  ${short_password}
    the user should not see an error in the page
    the user clicks the button/link         jQuery = button:contains("Create account")

The user checks for validations
    the user enters text to a text field    id = firstName  ${empty}
    the user enters text to a text field    id = lastName   ${empty}
    the user enters text to a text field    id = phoneNumber  ${empty}

The user checks for details and password validations
    the user checks for validations
    the user enters text to a text field    id = password  ${empty}

The user should see client side validations
    the user should see a field error    Please enter a first name.
    the user should see a field error    Please enter a last name.
    the user should see a field error    Please enter a phone number.

The user should see client side validations triggered correctly
    the user should see client side validations
    the user should see a field error    Password must contain at least one lower case letter.

The user should see server side validations
    [Arguments]  ${button}
    the user clicks the button/link                  jQuery = button:contains("${button}")
    the user should see a field and summary error    Please enter a first name.
    the user should see a field and summary error    Your first name should have at least 2 characters.
    the user should see a field and summary error    Please enter a last name.
    the user should see a field and summary error    Your last name should have at least 2 characters.
    the user should see a field and summary error    Please enter a phone number.
    the user should see a field and summary error    Please enter a valid phone number between 8 and 20 digits.

The user should see server side validations triggered correctly
    the user should see server side validations      Create account
    the user should see a field and summary error    Password must be at least 12 characters.
    the user should see a field and summary error    Please enter your password.

Comp admin remove project assigned to MO
    [Arguments]  ${project_name}
    the user should not see assigned project in Select a project to assign search field
    the user should see the element     jQuery = span:contains("1") ~ span:contains("assigned projects")
    the user clicks the button/link     jQuery = td:contains("${project_name}") ~ td a:contains("Remove")

The user should not see assigned project in Select a project to assign search field
    input text                             id = projectId    ${Assign_Project_ID}
    the user should not see the element    jQuery = ul li:contains("${Assign_Project_ID} - ${Assign_Project}")

Comp admin assign and remove project to MO
    the internal user assign project to MO        ${Assign_Project_ID}  ${Assign_Project}
    comp admin remove project assigned to MO      ${Assign_Project}

The user should see exisitng IFS user details and add phone number
    the user should see the element          jQuery = .message-alert:contains("We have found a user with this email address.")
    the user should see the element          jQuery = dt:contains("Email address") ~ dd:contains("${assessor2_credentials["email"]}")
    the user should see the element          jQuery = dt:contains("First name") ~ dd:contains("Felix")
    the user should see the element          jQuery = dt:contains("Last name") ~ dd:contains("Wilson")
    the user should see the element          jQuery = dt:contains("Phone number") ~ dd:contains("094073497201")
    the user clicks the button/link          jQuery = button:contains("Add monitoring officer")

The user logs in and checks for assigned projects
    the user reads his email and clicks the link    ${assessor2_credentials["email"]}   ${PROJECT_SETUP_COMPETITION_NAME}   The project Elbow grease has been assigned to you as the Monitoring Officer  1
    logging in and error checking                   &{assessor2_credentials}
    the user clicks the button/link                 id = dashboard-link-MONITORING_OFFICER
    the user selects the checkbox                   previousProject
    the user clicks the button/link                 id = update-documents-results-button
    the user should see the project status          ${Elbow_Grease_Title}
    #the user should see the element                 jQuery = .task:contains("${Elbow_Grease_Title}") + .status:contains("Live project")

The user navigate to assign MO page
    the user navigates to the page         ${server}/management/dashboard/project-setup
    the user clicks the button/link        link = Assign monitoring officers

The user adds MO email address
    log in as a different user              &{Comp_admin1_credentials}
    the user navigate to assign MO page
    the user clicks the button/link         link = Add a monitoring officer
    the user enters text to a text field    id = emailAddress  tom@poly.io
    the user clicks the button/link         jQuery = button[type="submit"]

The user should see assigned MO details
    the user navigates to the page                 ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element                css = ul li.complete:nth-child(4)
    the user should see the text in the element    css = ul li.complete:nth-child(4) p    Your Monitoring Officer for this project is Nilesh Patti.
    the user clicks the button/link                link = Monitoring Officer
    the user should see the element                jQuery = .success-alert:contains("We have assigned a monitoring officer to your project.")
    the user should see the element                jQuery = .govuk-body:contains("Nilesh Patti")
    the user should see the element                jQuery = .govuk-body:contains("nilesh.patti@gmail.com")
    the user should see the element                jQuery = .govuk-body:contains("449890325459")
    the user navigates to the page                 ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element                css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

Custom suite teardown
    the user closes the browser
    Disconnect from database

release feedback on inform comp
    log in as a different user    &{Comp_admin1_credentials}
    ${status}   ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should not see the element  link = ${INFORM_COMPETITION_NAME}
    Run Keyword If  '${status}' == 'FAIL'  Run keywords   the user clicks the button/link  link = ${INFORM_COMPETITION_NAME}
    ...                              AND    the user clicks the button/link  jQuery = button:contains("Release feedback")
    log in as a different user     tom@poly.io   ${short_password}
    the user clicks the button/link  link = ${Assign_Project2}

Internal user removes a partner organisation
    the user navigates to the page          ${server}/project-setup-management/competition/${PSC_Competition_Id}/status/all
    the user clicks the button/link         jQuery = tbody tr:nth-of-type(1) td:nth-of-type(2)
    the user clicks the button/link         jQuery = h2:contains("SmithZone")~ button:contains("Remove organisation"):first
    the user clicks the button/link         jQuery = .warning-modal[aria-hidden=false] button:contains("Remove organisation")
    the user should not see the element     jQuery = h2:contains("SmithZone")

Monitoring officer clicks on payment milestones link
    the user clicks the button/link     jQuery = a:contains('${sbri_applicaton_name}')
    the user clicks the button/link     jQuery = a:contains("Finance checks")
    the user clicks the button/link     jQuery = a:contains("Payment milestones")

Monitoring officer views detailed payment milestones
    the user should see the element     jQuery = h1:contains("Payment milestones")
    the user should see the element     css = [aria-controls="accordion-finances-content-1"]
    #addiing check to run the test locally and in cloud
    #cloud check
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element   jQuery = h3:contains("Total payment requested") + h3:contains("100%")+h3:contains("£243,484")
    #local check
    run keyword if  '${status}'=='FAIL'  run keyword  the user should see the element     jQuery = h3:contains("Total payment requested") + h3:contains("100%")+h3:contains("£265,084")

Monitoring officer checks changes to finances
    the user clicks the button/link     jQuery = a:contains('${sbri_applicaton_name}')
    the user clicks the button/link     jQuery = a:contains("Finance checks")
    #addiing check to run the test locally and in cloud
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots   the user should see the element     jQuery = a:contains("Changes to finances")
    #cloud check
    run keyword if  '${status}'=='PASS'   run keyword   Monitoring officer views updated values in changes to finances
    #local check
    ...                           ELSE    run keyword   the user should not see the element     jQuery = a:contains("Changes to finances")

Monitoring officer views updated values in changes to finances
    the user clicks the button/link     jQuery = a:contains("Changes to finances")
    the user should see the element     jQuery = th:contains("Subcontracting") ~ td:contains("80,000")
    the user should see the element     jQuery = th:contains("Other costs") ~ td:contains("11,100")
    the user should see the element     jQuery = th:contains("Overhead costs") ~ td:contains("2,000")
    the user should see the element     jQuery = th:contains("Total project costs inclusive of VAT") ~ td:contains("£243,484")

Assign monitoring officer to project
    [Arguments]  ${applicationNumber}   ${applicationTitle}
    the user navigates to the page            ${server}/project-setup-management/monitoring-officer/view-all?ktp=false
    search for MO                             Orvill  Orville Gibbs
    the user should see the element           jQuery = span:contains("Assign projects to Monitoring Officer")
    the internal user assign project to MO    ${applicationNumber}  ${applicationTitle}

MO can view awaiting review as a finance status for all partners
    the user should see the element    jQuery = td:contains("${organisationWardName}")~td:contains("Awaiting review")
    the user should see the element    jQuery = td:contains("${organisationSmithName}")~td:contains("Awaiting review")
    the user should see the element    jQuery = td:contains("${organisationRedName}")~td:contains("Awaiting review")

MO can view completed as a finance status for individual partners
    the user should see the element    jQuery = td:contains("${organisationWardName}")~td:contains("Complete")
    the user should see the element    jQuery = td:contains("${organisationRedName}")~td:contains("Complete")

Remove project from exisitng MO
    search for MO                       Orville  Orville Gibbs
    the user clicks the button/link     jQuery = td:contains("${elbow_grease_title}") ~ td:contains("Remove")
    the user clicks the button/link     link = Back to assign monitoring officers

#adding a if condition to run the test locally and in the cloud
the user should see the project status
    [Arguments]  ${applicationTitle}
    #Cloud check
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = .task:contains("${applicationTitle}") + .status:contains("Live project")
    #Local check
    run keyword if  '${status}'=='FAIL'  run keyword  the user should see the element   jQuery = .task:contains("${applicationTitle}") + .status:contains("Monitor project")
