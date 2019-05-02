*** Settings ***
Documentation   IFS-5700 - Create new project team page to manage roles in project setup
...
...             IFS-5719 - Add team members in Project setup
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot

*** Variables ***
${newProjecTeamPage}   ${server}/project-setup/project/4/team

*** Test Cases ***
The lead partner is able to access project details page
    [Documentation]  IFS-5700
    Given the user logs-in in new browser    &{lead_applicant_credentials}
    When the user navigates to the page      ${newProjecTeamPage}
    Then the user should see the element     jQuery = h1:contains("Project team")

Verify add new team member field validation
    [Documentation]  IFS-5719
    Given the user clicks the button/link               jQuery = button:contains("Add team member")
    When the user clicks the button/link                jQuery = button:contains("Invite to project")
    Then the user should see a field and summary error  Please enter a name.
    #And the user should see a field and summary error   Enter an email address in the correct format, like name@example.com

The lead partner is able to add a new team member
    [Documentation]  IFS-5719
    Given the user adds a new team member



A new team member is able to accept the inviation and see projec set up
    [Documentation]  IFS-5719

The lead partner is able to assign a project manager
    [Documentation]  IFS-5719

The lead partner is able to change the project manager
    [Documentation]  IFS-5719

The lead partner is able to assign finance contact
    [Documentation]  IFS-5719

The lead partner is able to change the finance contact
    [Documentation]  IFS-5719

Non Lead partner is able to add a new team member
    [Documentation]  IFS-5719

Non Lead partner isnt able to assign a project manager
    [Documentation]  IFS-5719

Non Lead partner is able to assign finance contact
    [Documentation]  IFS-5719

Non Lead partner is able to change the finance contact
    [Documentation]  IFS-5719

*** Keywords ***
The user adds a new team member


Custom suite setup
    The guest user opens the browser

Custom suite teardown
    The user closes the browser

