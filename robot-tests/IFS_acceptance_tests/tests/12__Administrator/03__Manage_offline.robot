*** Settings ***
Documentation    IFS-5110 Handle IFS applications offline CTA
Force Tags      Administrator  HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Test Cases ***
Ifs Admin marks a project as offline
    [Documentation]  IFS-5110
    Given the user logs-in in new browser              &{ifs_admin_user_credentials}
    When the user marks the project as managed offline
    Then the user should see the element   jQuery = .warning-alert:contains("This project is being managed offline")

Project setup options are disabled
    [Documentation]  IFS-5110
    Given the user clicks the button/link  link = Projects in setup
    Then the user should see the element   jQuery = a:contains("${MARKOFFLINE_APPLICATION_1_NUMBER}") ~ p:contains("Setup is being managed offline")
    And the user should see the element    jQuery = th:contains("${MARKOFFLINE_APPLICATION_1_TITLE}") ~ td:contains("View") + .na + .na + .na + .na + .na + .na

Applicant sees the project is being managed offline
    [Documentation]  IFS-5110
    Given Log in as a different user       &{lead_applicant_credentials}
    When the user clicks the button/link   link = ${MARKOFFLINE_APPLICATION_1_TITLE}
    Then the user should see the element   jQuery = .message-alert:contains("Innovate UK is managing this project's setup offline. For help call 0300 321 4357.")
    And the user should see the element    jQuery = .progress-list .read-only + .read-only +.waiting +.read-only + .read-only + .read-only + .read-only

Ifs Admin marks a project as completed offline
    [Documentation]  IFS-5110
    Given Log in as a different user     &{ifs_admin_user_credentials}
    When the user marks the project as completed offline
    Then the user should see the element  jQuery = .warning-alert:contains("Project setup has been completed offline.")

Project setup shows project is completed offline
    [Documentation]  IFS-5110
    Given the user clicks the button/link  link = Projects in setup
    Then the user should see the element   jQuery = a:contains("${MARKOFFLINE_APPLICATION_1_NUMBER}") ~ p:contains("Setup has been completed offline")
    [Teardown]  The user closes the browser

*** Keywords ***
the user marks the project as managed offline
    the user navigates to the page    ${server}/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link   jQuery = #table-project-status td:contains("Complete") a
    the user clicks the button/link   link = Manage offline
    the user clicks the button/link   css = .modal-handle-project-offline button[type="submit"]

the user marks the project as completed offline
    the user navigates to the page    ${server}/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link   jQuery = #table-project-status td:contains("View") a
    the user clicks the button/link   link = Mark as completed offline
    the user clicks the button/link   css = .modal-complete-project-offline button[type="submit"]



