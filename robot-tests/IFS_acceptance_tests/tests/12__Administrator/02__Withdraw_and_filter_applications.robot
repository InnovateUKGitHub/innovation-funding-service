*** Settings ***
Documentation   IFS-2945 Withdraw a project from Project Setup
...
...             IFS-3473 Previous Applications Filter
...
...             IFS-3654 Filter out withdrawn projects from internal Project Setup dashboard
...
...             IFS-3565 Filter on Project Setup view of Competiton
...
...             IFS-5958 Read Only view of withdrawn and offline projects in project setup dashboards
...
...             IFS-5939 New manage project status page
...
Force Tags      Administrator  HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Variables ***
${externalProjectWithdrawnMessage}    This project has been withdrawn
${unsuccessfulState}                  Unsuccessful
${withdrawnState}                     Withdrawn
${ineligibleState}                    Ineligible

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
    [Documentation]  IFS-5939
    Given the user selects the checkbox     confirmationWithdrawn
    When the user clicks the button/link   jQuery = button:contains("Change project status")
    Then the user should see the element   p:contains("This project has been withdrawn.")
    [Teardown]  


#The IFS Admin withdraws a project from Project Setup
#    [Documentation]  IFS-2945
#    [Tags]
#    Given the user clicks the button/link                  jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")
#    When the user cancels then withdraws the project
#    Then the user can see the previous application         ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}

#The IFS Admin can no longer see the withdrawn project in the project setup table
#    [Documentation]  IFS-3654
#    [Tags]
#    When the user navigates to the page         ${server}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
#    Then the user should not see the element    jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")

#The IFS Admin filters the applications
#    [Documentation]  IFS-3473
#    [Tags]
#    [Setup]  the user navigates to the page                 ${server}/management/competition/${WITHDRAWN_PROJECT_COMPETITION}/applications/previous
#    Given the user selects a filter for the applications    ${withdrawnState}  filter
#    Then the user can see the previous application          ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}
#    When the user selects a filter for the applications     ${unsuccessfulState}  filter
#    Then the user can see the previous application          ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}  ${unsuccessfulState}
#    When the user selects a filter for the applications     ${ineligibleState}  filter
#    Then the user can see the previous application          ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}  ${ineligibleState}

#The IFS Admin clears any filters applied and can see all of the applications
#    [Documentation]  IFS-3473
#    [Tags]
#    When the user clicks the button/link                         link = Clear all filters
#    Then the user can see all of the previous applications when the All filter is applied

*** Keywords ***
The user navigates to the Manage Project status page
    the user clicks the button/link   jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")
    the user clicks the button/link   link = Manage project status

The user should be able to see all validations working correctly
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     This field cannot be left blank.
    the user should see Manage offline validations
    the user should see Withdraw validations

The user should see Manage offline validations
    the user selects the radio button   state  HANDLED_OFFLINE
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     This field cannot be left blank.

The user should see Withdraw validations
    the user selects the radio button   state  WITHDRAWN
    the user clicks the button/link    jQuery = button:contains("Change project status")
    the user should see a field and summary error     This field cannot be left blank.

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
    the user can see the previous application                ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}
    the user can see the previous application                ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}      ${unsuccessfulState}
    the user can see the previous application                ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}        ${ineligibleState}

The user enters a project to search for and clicks the Filter button
    [Arguments]  ${projectID}
    the user enters text to a text field    id = applicationSearchString  ${projectID}
    the user clicks the button/link         css = button[class="govuk-button"]  # Filter

the user should see the Low-friction wheel coatings project
    the user should see the element        jQuery = th:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}")

