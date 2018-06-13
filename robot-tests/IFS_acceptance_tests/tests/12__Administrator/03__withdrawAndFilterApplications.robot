*** Settings ***
Documentation   IFS-2945 Withdraw a project from Project Setup
...
...             IFS-3473 Previous Applications Filter
...
...             IFS-3654 Filter out withdrawn projects from internal Project Setup dashboard
Force Tags      Administrator
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Variables ***
${externalProjectWithdrawnMessage}    This project has been withdrawn
${proj_electric_drive}                ${application_ids['Electric Drive']}

*** Test Cases ***
# This is placed before withdrawing the project in the next test case as we want to verify that it what we don't search for isn't showing.
The IFS Admin searches for a project
    [Documentation]  IFS-3565
    [Tags]
    Given The user logs-in in new browser              &{ifs_admin_user_credentials}
    And the user navigates to the page                 ${server}/project-setup-management/competition/${NOT_EDITABLE_COMPETITION}/status/all
    #When the user enters a project to search for and clicks the filter button    ${INFORM_COMPETITION_NAME_1_NUMBER}  # Climate control solution
    When the user enters a project to search for and clicks the filter button    9  # Climate control solution
    Then the user should only see the Climate control solution project

The IFS Admin clears all filters after searching for a project
    [Documentation]  IFS-356
    [Tags]
    When the user clicks the button/link    css = button[class="button"]  #Filter
    Then the user should see both projects

The IFS Admin withdraws a project from Project Setup
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    Given the user navigates to the page                   ${server}/project-setup-management/competition/${NOT_EDITABLE_COMPETITION}/status/all
    And the user clicks the button/link                    jQuery = tr:contains("${INFORM_COMPETITION_NAME_2}") a:contains("Incomplete")
    When the user cancels then withdraws the project
    Then the user can see the previous application         ${INFORM_COMPETITION_NAME_2_NUMBER}  Withdrawn

The IFS Admin can no longer see the withdrawn project in the project setup table
    [Documentation]  IFS-3654
    [Tags]
    When the user navigates to the page         ${server}/project-setup-management/competition/${NOT_EDITABLE_COMPETITION}/status/all
    Then the user should not see the element    jQuery = tr:contains("${INFORM_COMPETITION_NAME_2}") a:contains("Incomplete")

The IFS Admin filters the applications
    [Documentation]  IFS-3473
    [Tags]  HappyPath
    [Setup]  the user navigates to the page                 ${server}/management/competition/${NOT_EDITABLE_COMPETITION}/applications/previous
    Given the user selects a filter for the applications    Withdrawn  filter
    Then the user can see the previous application          ${INFORM_COMPETITION_NAME_2_NUMBER}  Withdrawn
    When the user selects a filter for the applications     Unsuccessful  filter
    Then the user can see the previous application          ${proj_electric_drive}  Unsuccessful

The IFS Admin clears any filters applied
    [Documentation]  IFS-3473
    [Tags]  HappyPath
    [Setup]
    When the user clicks the button/link                         link=Clear all filters
    Then the user can see the previous application               ${INFORM_COMPETITION_NAME_2_NUMBER}  Withdrawn
    And the user can see the previous application                ${proj_electric_drive}  Unsuccessful

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
    the user selects the option from the drop-down menu    ${applicationStatusInDropDown}  id=${filterID}
    the user clicks the button/link                         css = button[class="button"]  #Filter

The user enters a project to search for and clicks the Filter button
    [Arguments]  ${projectID}
    the user enters text to a text field    id = applicationSearchString  ${projectID}
    the user clicks the button/link         css = button[class="button"]  #Filter

the user should only see the Climate control solution project
    the user should not see the element    jQuery = th:contains("${INFORM_COMPETITION_NAME_2}") a:contains("${INFORM_COMPETITION_NAME_2_NUMBER}")
    the user should see the element        jQuery = th:contains("${INFORM_COMPETITION_NAME_1}") a:contains("${INFORM_COMPETITION_NAME_1_NUMBER}")

the user should see both projects
    the user should see the element        jQuery = th:contains("${INFORM_COMPETITION_NAME_2}") a:contains("${INFORM_COMPETITION_NAME_2_NUMBER}")
    the user should see the element        jQuery = th:contains("${INFORM_COMPETITION_NAME_1}") a:contains("${INFORM_COMPETITION_NAME_1_NUMBER}")

