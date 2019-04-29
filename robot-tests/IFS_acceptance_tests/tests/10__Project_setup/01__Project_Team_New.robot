*** Settings ***
Documentation   IFS-5700 - Create new project team page to manage roles in project setup
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot

*** Variables ***
${newProjecTeamPage}   ${server}/project-setup/project/4/team

*** Test Cases ***
The user is able to access project details page
    Given the user logs-in in new browser    &{collaborator1_credentials}
    When the user navigates to the page      ${newProjecTeamPage}
    Then the user should see the element     jQuery = h1:contains("Project team")

*** Keywords ***
 Custom suite setup
    The guest user opens the browser

Custom suite teardown
    The user closes the browser

