*** Settings ***
Documentation   IFS-2945 Withdraw a project from Project Setup
...
...             IFS-3473 Previous Applications Filter
...
...             IFS-3654 Filter out withdrawn projects from internal Project Setup dashboard
...
...             IFS-3565 Filter on Project Setup view of Competiton
...
...             IFS-5966 Migrate withdrawn projects back to project setup dashboard
...
...             IFS-5958 Read Only view of withdrawn and offline projects in project setup dashboards
...
...             IFS-5939 New manage project status page
...
...             IFS-5958 Read Only view of withdrawn and offline projects in project setup dashboards
...
Force Tags      Administrator  HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Variables ***
${externalProjectWithdrawnMessage}    This project has been withdrawn
${unsuccessfulState}                  Unsuccessful
${withdrawnState}                     Withdrawn
${successfulState}                    Successful
${ineligibleState}                    Ineligible
${successfulState}                    Successful

*** Test Cases ***
The IFS Admin searches for a project and clears all filters after searching for a project
    [Documentation]  IFS-3565
    [Setup]  The user logs-in in new browser              &{ifs_admin_user_credentials}
    Given the user navigates to the page                                         ${server}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    When the user enters a project to search for and clicks the filter button    74  # This is a partial search, which will show the Low-friction wheel coatings project
    Then the user should see the Low-friction wheel coatings project
    [Teardown]  the user clicks the button/link                                  css = button[class="govuk-button"]  # Filter

Manage project status Validations
    [Documentation]  IFS-5939
    Given the user navigates to the Manage Project status page
    Then the user should be able to see all validations working correctly

IFS Admin is able to Withdraw a project
    [Documentation]  IFS-2945 IFS-3654 IFS-5939
    Given the user selects the checkbox     confirmationWithdrawn
    When the user clicks the button/link   jQuery = button:contains("Change project status")
    Then the project should be withdrawn

Withdrawn project should contain RO links only
    [Documentation]  IFS-5958
    Given the user navigates to the page  ${server}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    Then all project sections should be read only

The IFS Admin filters the applications
    [Documentation]  IFS-3473
    [Setup]  the user navigates to the page                 ${server}/management/competition/${WITHDRAWN_PROJECT_COMPETITION}/applications/previous
    Given the user selects a filter for the applications    ${successfulState}  filter
    Then the user should be able to see previous applications by status

The IFS Admin clears any filters applied and can see all of the applications
    [Documentation]  IFS-3473
    Given the user clicks the button/link                         link = Clear all filters
    Then the user can see all of the previous applications when the All filter is applied

*** Keywords ***
All project sections should be read only
    the user clicks the button/link      css = #table-project-status td:nth-child(3).status.ok a
    the user should not see the element  link = Add team member
    the user clicks the button/link      link = Projects in setup
    the user clicks the button/link      css = #table-project-status td:nth-child(5).status.ok a
    the user should not see the element  link = Change Monitoring Officer
    the user clicks the button/link      link = Projects in setup
    finance checks are RO

finance checks are RO
    #Viability page is RO
    the user clicks the button/link      jQuery = #table-project-status td:nth-child(7).status.waiting a
    the user clicks the button/link      jQuery = tr:contains(Tanzone) a.govuk-link.viability-0
    the user should not see the element  jQuery = input[id="costs-reviewed"]
    the user should not see the element  jQuery = input[id="project-viable"]
    the user clicks the button/link      link = Finance checks
    #Eligibility page is RO
    the user clicks the button/link      jQuery = tr:contains(Tanzone) a.govuk-link.eligibility-0
    the user should not see the element  jQuery = input[id="project-eligible"]
    the user clicks the button/link      link = Finance checks
    #Queries page is RO
    the user clicks the button/link      jQuery = table.table-progress tr:nth-child(1) td:nth-child(6) a:contains("View")
    the user should not see the element  jQuery = button:contains("Post a new query")
    the user clicks the button/link      link = Finance checks
    #Notes page is RO
    the user clicks the button/link      jQuery = table.table-progress tr:nth-child(1) td:nth-child(7) a:contains("View")
    the user should not see the element  jQuery = button:contains("Create a new note")


The user should be able to see previous applications by status
    the user can see the previous application          ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${successfulState}
    the user selects a filter for the applications     ${unsuccessfulState}  filter
    the user can see the previous application          ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}  ${unsuccessfulState}
    the user selects a filter for the applications     ${ineligibleState}  filter
    the user can see the previous application          ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}  ${ineligibleState}

The project should be withdrawn
     the user should see the element      jQuery = p:contains("This project has been withdrawn.")
     the user clicks the button/link      link = Return to project details
     the user should see the element      jQuery = h1:contains("Project details")
     the user clicks the button/link      link = Projects in setup
     the user should see the element      jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") strong:contains("Withdrawn")

The user navigates to the Manage Project status page
    the user clicks the button/link   jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")
    the user clicks the button/link   link = Manage project status

The user should be able to see all validations working correctly
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     ${empty_field_warning_message}
    the user should see Manage offline validations
    the user should see Withdraw validations

The user should see Manage offline validations
    the user selects the radio button   state  HANDLED_OFFLINE
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     ${empty_field_warning_message}

The user should see Withdraw validations
    the user selects the radio button   state  WITHDRAWN
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     ${empty_field_warning_message}

The user cancels then withdraws the project
    the user clicks the button/link            link = Withdraw project
    the user clicks the button/link            jQuery = button:contains("Withdraw project") ~ button:contains("Cancel")  #Cancel the modal
    the user clicks the button/link            link = Withdraw project
    the user clicks the button/link            css = button[type="submit"]  # Withdraw the project on the modal

The user can see the previous application
    [Arguments]  ${filteredApplication}  ${applicationStatusInTable}
    the user should see the element            jQuery = td:contains("${filteredApplication}") ~ td:contains("${applicationStatusInTable}")

The user selects a filter for the applications
    [Arguments]  ${applicationStatusInDropDown}  ${filterID}
    Given the user selects the option from the drop-down menu    ${applicationStatusInDropDown}  id=${filterID}
    When the user clicks the button/link                         css = button[class = "govuk-button"]  # Filter

The user can see all of the previous applications when the All filter is applied
    the user can see the previous application                ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${successfulState}
    the user can see the previous application                ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}      ${unsuccessfulState}
    the user can see the previous application                ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}        ${ineligibleState}

The user enters a project to search for and clicks the Filter button
    [Arguments]  ${projectID}
    the user enters text to a text field    id = applicationSearchString  ${projectID}
    the user clicks the button/link         css = button[class="govuk-button"]  # Filter

The user should see the Low-friction wheel coatings project
    the user should see the element        jQuery = th:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}")

