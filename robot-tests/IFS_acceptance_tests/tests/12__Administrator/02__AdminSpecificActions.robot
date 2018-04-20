*** Settings ***
Documentation  IFS-50 Change an existing unsuccessful application into a successful project in setup
Suite Setup    The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown  the user closes the browser
Force Tags      Administrator
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot


*** Test Cases ***
Administrator is able to mark as successful an unsuccessful application
    [Documentation]  IFS-50
    [Tags]  HappyPath
    Given the user navigates to the page  ${server}/management/competition/${PROJECT_SETUP_COMPETITION}/applications/unsuccessful
    Then the user should be allowed to only reinstate Unsuccessful applications
    When the user clicks the button/link  jQuery=td:contains("Cleaning Product packaging") ~ td a:contains("Mark as successful")
    And the user clicks the button/link   css=.button[name="mark-as-successful"]  # I'm sure button
    Then the user should no longer see the application in the unsuccessful list but in Project Setup

The IFS Admin withdraws a project from Project Setup
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    [Setup]  The user logs-in in new browser       &{ifs_admin_user_credentials}
    Given the user navigates to the page           ${server}/project-setup-management/competition/${NOT_EDITABLE_COMPETITION}/status/all
    And the user clicks the button/link            jQuery=tr:contains("${INFORM_COMPETITION_NAME_2}") a:contains("Incomplete")
    When the user cancels then withdraws the project
    Then the user should see the element           jQuery=a:contains("Previous applications")
    #TODO IFS-3035 This may need amending with 3035 as the redirect/page will change.

The internal user can see their project is withdrawn
    [Documentation]  IFS-2945
    [Tags]  HappyPath
    [Setup]  log in as a different user            &{successful_released_credentials}
    Given the user should see the element          jQuery=p:contains("Project withdrawn")
    When the user clicks the button/link           jQuery=a:contains("${INFORM_COMPETITION_NAME_2}")
    Then the user should see the element           jQuery=.warning-alert:contains("${externalProjectWithdrawnMessage}")

*** Keywords ***
The user should be allowed to only reinstate Unsuccessful applications
    the user should see the element  jQuery=td:contains("Unsuccessful") ~ td a:contains("Mark as success")

the user should no longer see the application in the unsuccessful list but in Project Setup
    the user should not see the element  jQuery=td:contains("Cleaning Product packaging")
    the user navigates to the page       ${server}/project-setup-management/competition/${PROJECT_SETUP_COMPETITION}/status/all
    the user should see the element      jQuery=th:contains("Cleaning Product packaging")

The user cancels then withdraws the project
    the user clicks the button/link            link=Withdraw project
    the user clicks the button/link            jQuery=button:contains("Withdraw project") ~ button:contains("Cancel")    #Cancel the modal
    the user clicks the button/link            link=Withdraw project
    the user clicks the button/link            css=button[type="submit"]  #Withdraw the project on the modal