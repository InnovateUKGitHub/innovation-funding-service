*** Settings ***
Documentation   IFS-5709 - Create new project details page in project setup (External user jouney ONLY)
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          PS_Common.robot

*** Variables ***
${newProjecDetailsPage}       ${server}/project-setup/project/${PS_PD_Application_Title}/new-details

*** Test Cases ***
The lead partner is able to access project details page
    [Documentation]  IFS-5709
    Given the user logs-in in new browser    &{lead_applicant_credentials}
    When the user navigates to the page      ${newProjecDetailsPage}
    Then the user should see the element     jQuery = h1:contains("Project details")

*** Keywords ***
Custom suite setup
    The guest user opens the browser

Custom suite teardown
   The user closes the browser
