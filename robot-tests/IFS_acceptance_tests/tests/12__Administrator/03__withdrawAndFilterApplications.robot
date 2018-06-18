*** Settings ***
Documentation   IFS-2945 Withdraw a project from Project Setup
...
...             IFS-3473 Previous Applications Filter
...
...             IFS-3654 Filter out withdrawn projects from internal Project Setup dashboard
...
...             IFS-3565 Filter on Project Setup view of Competiton
Force Tags      Administrator
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Variables ***
${externalProjectWithdrawnMessage}    This project has been withdrawn
${unsuccessfulState}                  Unsuccessful
${withdrawnState}                     Withdrawn
${ineligibleState}                    Ineligible

*** Test Cases ***
The IFS Admin searches for a project
    [Documentation]  IFS-3565
    [Tags]
    Given The user logs-in in new browser              &{ifs_admin_user_credentials}
    And the user navigates to the page                 ${server}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    When the user enters a project to search for and clicks the filter button    74  # This is a partial search, which will show the Low-friction wheel coatings project
    Then the user should see the Low-friction wheel coatings project

The IFS Admin clears all filters after searching for a project
    [Documentation]  IFS-3565
    [Tags]
    When the user clicks the button/link    css = button[class="button"]  #Filter

The IFS Admin withdraws a project from Project Setup
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    Given the user clicks the button/link                  jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")
    When the user cancels then withdraws the project
    Then the user can see the previous application         ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}

The IFS Admin can no longer see the withdrawn project in the project setup table
    [Documentation]  IFS-3654
    [Tags]
    When the user navigates to the page         ${server}/project-setup-management/competition/${WITHDRAWN_PROJECT_COMPETITION}/status/all
    Then the user should not see the element    jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")

The IFS Admin filters the applications
    [Documentation]  IFS-3473
    [Tags]  HappyPath
    [Setup]  the user navigates to the page                 ${server}/management/competition/${WITHDRAWN_PROJECT_COMPETITION}/applications/previous
    Given the user selects a filter for the applications    ${withdrawnState}  filter
    Then the user can see the previous application          ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}
    When the user selects a filter for the applications     ${unsuccessfulState}  filter
    Then the user can see the previous application          ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}  ${unsuccessfulState}
    When the user selects a filter for the applications     ${ineligibleState}  filter
    Then the user can see the previous application          ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}  ${ineligibleState}

The IFS Admin clears any filters applied and can see all of the applications
    [Documentation]  IFS-3473
    [Tags]  HappyPath
    [Setup]
    When the user clicks the button/link                         link=Clear all filters
    Then the user can see all of the previous applications when the All filter is applied

*** Keywords ***
The user cancels then withdraws the project
    the user clicks the button/link            link = Withdraw project
    the user clicks the button/link            jQuery = button:contains("Withdraw project") ~ button:contains("Cancel")  #Cancel the modal
    the user clicks the button/link            link = Withdraw project
    the user clicks the button/link            css = button[type="submit"]  #Withdraw the project on the modal

The user can see the previous application
    [Arguments]  ${filteredApplication}  ${applicationStatusInTable}
    the user should see the element            jQuery = td:contains("${filteredApplication}") ~ td:contains("${applicationStatusInTable}")

The user selects a filter for the applications
    [Arguments]  ${applicationStatusInDropDown}  ${filterID}
    Given the user selects the option from the drop-down menu    ${applicationStatusInDropDown}  id=${filterID}
    When the user clicks the button/link                         css = button[class = "button"]  #Filter

The user can see all of the previous applications when the All filter is applied
    the user can see the previous application                ${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}  ${withdrawnState}
    the user can see the previous application                ${UNSUCCESSFUL_PROJECT_COMPETITION_NAME_3}      ${unsuccessfulState}
    the user can see the previous application                ${INELIGIBLE_PROJECT_COMPETITION_NAME_2}        ${ineligibleState}

The user enters a project to search for and clicks the Filter button
    [Arguments]  ${projectID}
    the user enters text to a text field    id = applicationSearchString  ${projectID}
    the user clicks the button/link         css = button[class="button"]  #Filter

the user should see the Low-friction wheel coatings project
    the user should see the element        jQuery = th:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1_NUMBER}")

