*** Settings ***
Documentation   IFS-5720 - Add team members (internal)
...
...             IFS-5721 - Resend invitation to add new members (internal)
...
...             IFS-5721 - Remove a pending invitation (internal)
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot
*** Variables ***
${internalViewTeamPage}  ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/project/${PS_PD_Project_Id}/team
${internalInviteeEmail}  internal@invitee.com

*** Test Cases ***
Add new team member Validations
  [Documentation]
  Given the user navigates to the page   ${internalViewTeamPage}
  Then the user should see the element   jQuery = h1:contains("Project team")

*** Keywords ***
Custom suite setup
    the user logs-in in new browser    &{Comp_admin1_credentials}

Custom suite teardown
    The user closes the browser