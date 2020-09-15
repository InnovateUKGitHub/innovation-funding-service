*** Settings ***
Documentation     IFS-188 Stakeholder views – Support team
...
...               IFS-1986 External users: search
...
...               IFS-1841 Basic view of all 'external' IFS users
...
...               IFS-2904 CSS Search by application number
...
...               IFS-3072 Search by either application number or competition name across each Competition management tab
...
...               IFS-7429 Support user has access to Project Details after Finance reviewer is assigned
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Support  CompAdmin  HappyPath
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${invitedCollaborator}  stuart@empire.com

*** Test Cases ***
Support dashboard
    [Documentation]    IFS-188
    Given the user navigates to the page  ${CA_Live}
    Then the user should see all live competitions

Competition links go directly to all applications page
    [Documentation]    IFS-188
    When The user clicks the button/link    link = ${openCompetitionRTO_name}
    Then the user should see the element    jQuery = span:contains("${openCompetitionRTO}: ${openCompetitionRTO_name}")
    And the user should see the element     jQuery = h1:contains("All applications")
    And the user should see the element     css = #application-list

Back navigation is to dashboard
    [Documentation]    IFS-188
    Given the user clicks the button/link  jQuery = .govuk-back-link:contains("Dashboard")
    Then the user should see the element   jQuery = h1:contains("All competitions")
    And the user should see the element    jQuery = a:contains("Live")
    And the user should see the element    jQuery = a:contains("Project setup")
    And the user should see the element    jQuery = a:contains("Previous")

Support user is able to search for competition
    [Documentation]  IFS-3072
    When the user navigates to the page       ${server}/management/dashboard/project-setup
    Then the user enters the competition name into the search field

Support user is able to search for an Application
    [Documentation]  IFS-2904
    When the user navigates to the page       ${server}/management/dashboard/live
    Then the user enters the application id into the search field

Support user is able to access Project details once a finance contact is assigned
    [Documentation]  IFS-7429
    Given finance reviewer is added to the project    ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/details
    When log in as a different user                   &{support_user_credentials}
    Then the user navigates to the page               ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/details
    [Teardown]  The user clicks the button/link       link = Dashboard

Support user is able to search active external users
    [Documentation]  IFS-1986 IFS-1841
    Given the user navigates to the page           ${manageExternalUsers}
    When the user is searching for external users  dustin  Email
    Then the user should see the element           jQuery = td:contains("Kazio") ~ td:contains("worth.email.test+dustin@gmail.com") + td:contains("Verified")
    And the user clicks the button/link            link = Clear
    When the user is searching for external users  Empire  Organisation name
    Then the user should see the element           jQuery = td:contains("${EMPIRE_LTD_NAME}") + td:contains("Business") + td:contains("${EMPIRE_LTD_ID}") + td:contains("${lead_applicant_credentials["email"]}")
    And the user clicks the button/link            link = Clear

Support user is able to search pending external users
    [Documentation]  IFS-1986 IFS-1841
    When a collaborator has been invited but he has not yet approved the invitation
    Then the support user should be able to see him as    Sent  Pending accounts
    When the invitee has accepted the invitation but has not yet verified his account
    Then the support user should be able to see him as    Not Verified  Active accounts
    When the invitee verifies his account
    Then the support user should be able to see him as    Verified  Active accounts

*** Keywords ***
the user is searching for external users
    [Arguments]  ${string}  ${category}
    the user enters text to a text field                   id = searchString  ${string}
    the user selects the option from the drop-down menu    ${category}  id = searchCategory
    the user clicks the button/link                        css = button[type = "submit"]  #Search

a collaborator has been invited but he has not yet approved the invitation
    log in as a different user            &{lead_applicant_credentials}
    the user navigates to the page        ${server}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}
    the user clicks the button/link       link = Application team
    the user clicks the button/link       jQuery = button:contains("Add person to Empire Ltd")
    the user enters text to a text field  css = [name=name]  Stuart
    the user enters text to a text field  css = [name=email]  ${invitedCollaborator}
    the user clicks the button/link       jQuery = button:contains("Invite to application")
    logout as user

the support user should be able to see him as
    [Arguments]  ${status}  ${tab}
    the user navigates to the page              ${LOGIN_URL}
    logging in and error checking               &{support_user_credentials}
    the user navigates to the page              ${manageExternalUsers}
    the user is searching for external users    ${invitedCollaborator}  Email
    the user clicks the button/link             jQuery = button:contains("${tab}")
    #The tab appears after enabling the search functionality
    the user should see the element             jQuery = td:contains("${invitedCollaborator}") ~ td:contains("${status}")
    the user logs out if they are logged in

the invitee has accepted the invitation but has not yet verified his account
    the user reads his email and clicks the link    ${invitedCollaborator}  Invitation to contribute in ${openCompetitionRTO_name}  to participate in an application  2
    the user clicks the button/link                 link = Yes, accept invitation
    the user clicks the button/link                 link = Confirm and continue
    the invited user fills the create account form  Stuart  Minions

the invitee verifies his account
    the user reads his email and clicks the link       ${invitedCollaborator}  Please verify your email address  recently set up an account  1
    the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

the user enters the application id into the search field
    the user enters text to a text field              id = searchQuery  ${createApplicationOpenCompetitionApplication1Number}
    the user clicks the button/link                   id = searchsubmit
    the user should see the element                   jQuery = td:contains("${createApplicationOpenCompetitionApplication1Number}")

the user enters the competition name into the search field
    the user enters text to a text field              id = searchQuery  ${openCompetitionResearch_name}
    the user clicks the button/link                   id = searchsubmit
    the user should see the element                   jQuery = a:contains("${openCompetitionResearch_name}")

Custom suite teardown
    Disconnect from database
    The user closes the browser

Custom suite setup
    Connect to database  @{database}
    The user logs-in in new browser  &{support_user_credentials}

