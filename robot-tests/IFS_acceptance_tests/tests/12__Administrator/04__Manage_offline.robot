*** Settings ***
Documentation    Suite description
Force Tags      Administrator  HappyPath
Resource        ../../resources/defaultResources.robot
Resource        ../10__Project_setup/PS_Common.robot

*** Test Cases ***
Ifs Admin marks a project as offline
    [Tags]
    Given the user logs-in in new browser              &{ifs_admin_user_credentials}
    When the user marks the project as managed offline
    Then the user should see the element   jQuery = .warning-alert:contains("Project setup is being processed offline")

Project setup options are disabled
    Given the user clicks the button/link  link = Projects in setup
    Then the user should see the element   jQuery = a:contains("${MARKOFFLINE_APPLICATION_1_NUMBER}") ~ p:contains("Set up is being handled offline")
    And the user should see the element    jQuery = th:contains("${MARKOFFLINE_APPLICATION_1_TITLE}") ~ td:contains("View") + .na + .na + .na + .na + .na + .na

Applicant sees the project is being managed offline
    Given Log in as a different user       &{RTO_lead_applicant_credentials}
    When the user clicks the button/link   link = ${MARKOFFLINE_APPLICATION_1_TITLE}
    Then the user should see the element   jQuery = .warning-alert:contains("Project setup will be processed offline. For support get in touch with 0300 321 4357")
  #  And the user should see the element    jQuery = .progress-list .read-only + .read-only + .complete +.read-only + .read-only +.read-only + .read-only

Ifs Admin marks a project as completed offline
    Given Log in as a different user     &{ifs_admin_user_credentials}
    When the user marks the project as completed offline
    #Then the user should see the element  .warning-alert:contains("Project setup is being processed offline")

Project setup shows project is completed offline
    Given the user clicks the button/link  link = Projects in setup
    Then the user should see the element   jQuery = a:contains("${MARKOFFLINE_APPLICATION_1_NUMBER}") ~ p:contains("Set up is being handled offline")

*** Keywords ***
the user marks the project as managed offline
    the user navigates to the page    https://ifs.local-dev/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link   jQuery = #table-project-status td:contains("Incomplete") a
    the user clicks the button/link   link = Manage project set up offline
    the user clicks the button/link   css = .modal-handle-project-offline button[type="submit"]

the user marks the project as completed offline
    the user navigates to the page    https://ifs.local-dev/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link   jQuery = #table-project-status td:contains("View") a
    the user clicks the button/link   link = Mark as completed offline
    the user clicks the button/link   css = .modal-complete-project-offline button[type="submit"]



