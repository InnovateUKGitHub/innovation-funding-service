*** Settings ***
Documentation   IFS-2945 Withdraw a project from Project Setup
Force Tags      Administrator
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Variables ***
${externalProjectWithdrawnMessage}    This project has been withdrawn

*** Test Cases ***
The IFS Admin withdraws a project from Project Setup
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser       &{ifs_admin_user_credentials}
    Given the user navigates to the page           ${server}/project-setup-management/competition/${NOT_EDITABLE_COMPETITION}/status/all
    And the user clicks the button/link            jQuery=tr:contains("${INFORM_COMPETITION_NAME_2}") a:contains("Incomplete")
    When the user cancels then withdraws the project
    Then the user should see the element           jQuery=a:contains("${INFORM_COMPETITION_NAME_2_NUMBER}")
    And the user should see the element            jQuery=a:contains("Previous competitions")
    #TODO IFS-3473 This needs to be expanded upon when filtering is added.

The external user can see their project is withdrawn
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    [Setup]  log in as a different user            &{successful_released_credentials}
    Given the user should see the element          jQuery=p:contains("Project withdrawn")
    When the user clicks the button/link           jQuery=a:contains("${INFORM_COMPETITION_NAME_2}")
    Then the user should see the element           jQuery=.warning-alert:contains("${externalProjectWithdrawnMessage}")

*** Keywords ***
The user cancels then withdraws the project
    the user clicks the button/link            link=Withdraw project
    the user clicks the button/link            jQuery=button:contains("Withdraw project") ~ button:contains("Cancel")    #Cancel the modal
    the user clicks the button/link            link=Withdraw project
    the user clicks the button/link            css=button[type="submit"]  #Withdraw the project on the modal