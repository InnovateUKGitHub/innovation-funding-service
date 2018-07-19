*** Settings ***
Documentation     IFS-188 Stakeholder views – Support team
...
...               IFS-1986 External users: search
...
...               IFS-1841 Basic view of all 'external' IFS users
...
...               IFS-2904 CSS Search by application number
Suite Setup       The user logs-in in new browser  &{support_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        Support  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${invitedCollaborator}  stuart@empire.com
${competitionName}      Networking home IOT devices

*** Test Cases ***
Support dashboard
    [Documentation]    IFS-188
    Given the user navigates to the page  ${CA_Live}
    Then the user should see all live competitions

Competition links go directly to all applications page
    [Documentation]    IFS-188
    When The user clicks the button/link    link=${openCompetitionRTO_name}
    Then the user should see the element    jQuery=span:contains("${competition_ids['${openCompetitionRTO_name}']}: ${openCompetitionRTO_name}")
    And the user should see the element     jQuery=h1:contains("All applications")
    And the user should see the element     css=#application-list

Back navigation is to dashboard
    [Documentation]    IFS-188
    Given the user clicks the button/link  jQuery=.govuk-back-link:contains("Dashboard")
    Then the user should see the element   jQuery=h1:contains("All competitions")
    And the user should see the element    jQuery=a:contains("Live")
    And the user should see the element    jQuery=a:contains("Project setup")
    And the user should see the element    jQuery=a:contains("Previous")

Support user is able to search for an Application
    [Documentation]  IFS-2904
    [Tags]  HappyPath
    When the user navigates to the page       ${server}/management/dashboard/live
    Then the user enters the application id into the search field

Support user is able to search active external users
    [Documentation]  IFS-1986 IFS-1841
    [Tags]  HappyPath
    Given the user navigates to the page           ${manageExternalUsers}
    When the user is searching for external users  becky  Email
    Then the user should see the element           jQuery=td:contains("Dreambit") ~ td:contains("becky.mason@gmail.com") + td:contains("Verified")
    And the user clicks the button/link            link=Clear
    When the user is searching for external users  Empire  ORGANISATION_NAME
    Then the user should see the element           jQuery=td:contains("${EMPIRE_LTD_NAME}") + td:contains("${EMPIRE_LTD_ID}") + td:contains("${lead_applicant_credentials["email"]}")
    And the user clicks the button/link            link=Clear

Support user is able to search pending external users
    [Documentation]  IFS-1986 IFS-1841
    [Tags]  HappyPath
    When a collaborator has been invited but he has not yet approved the invitation
    Then the support user should be able to see him as  Sent  Pending accounts
    When the invitee has accepted the invitation but has not yet verified his account
    Then the support user should be able to see him as  Not Verified  Active accounts
    When the invitee verifies his account
    Then the support user should be able to see him as  Verified  Active accounts

*** Keywords ***
the user is searching for external users
    [Arguments]  ${string}  ${category}
    the user enters text to a text field  id=searchString  ${string}
    the user selects the option from the drop-down menu  ${category}  id=searchCategory
    the user clicks the button/link  css=button[type="submit"]  #Search

a collaborator has been invited but he has not yet approved the invitation
    log in as a different user       &{lead_applicant_credentials}
    the user navigates to the page   ${server}/application/${OPEN_COMPETITION_APPLICATION_1_NUMBER}/team/update/existing/${EMPIRE_LTD_ID}
    the user clicks the button/link  jQuery=.button-clear:contains("Add another contributor")
    the user enters text to a text field  name=stagedInvite.name  Stuart
    the user enters text to a text field  name=stagedInvite.email  ${invitedCollaborator}
    the user clicks the button/link       jQuery=button:contains("Invite")
    logout as user

the support user should be able to see him as
    [Arguments]  ${status}  ${tab}
    the user navigates to the page   ${LOGIN_URL}
    logging in and error checking    &{support_user_credentials}
    the user navigates to the page   ${manageExternalUsers}
    the user is searching for external users  ${invitedCollaborator}  Email
    the user clicks the button/link  jQuery=.button-clear:contains("${tab}")
    #The tab appears after enabling the search functionality
    the user should see the element  jQuery=td:contains("${invitedCollaborator}") ~ td:contains("${status}")
    the user logs out if they are logged in

the invitee has accepted the invitation but has not yet verified his account
    the user reads his email and clicks the link  ${invitedCollaborator}  Invitation to collaborate in ${openCompetitionRTO_name}  to participate in an application  2
    the user clicks the button/link  link=Yes, accept invitation
    the user clicks the button/link  link=Confirm and continue
    the invited user fills the create account form  Stuart  Minions

the invitee verifies his account
    the user reads his email and clicks the link       ${invitedCollaborator}  Please verify your email address  recently set up an account  1
    the user should be redirected to the correct page  ${REGISTRATION_VERIFIED}

the user enters the application id into the search field
    ${applicationID} =  get application id by name  ${competitionName}
    the user enters text to a text field    id=searchQuery  ${applicationID}
    the user clicks the button/link          id=searchsubmit
    the user should see the element          jQuery=td:contains("${applicationID}")