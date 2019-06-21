*** Settings ***
Documentation    IFS-5110 Handle IFS applications offline CTA
...
...              IFS-5939 New manage project status page
...
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  HappyPath
Resource          ../../resources/defaultResources.robot
Resource          ../10__Project_setup/PS_Common.robot

*** Test Cases ***
Ifs Admin marks a project as offline
    [Documentation]  IFS-5110
    [Setup]  the user navigates to the project to be Manage offline
    Given the user marks the project as managed offline
    Then the user is able to see that the project is being Managed offline

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
    Then the user is able to see that the project is beeing completed offline

*** Keywords ***
The user is able to see that the project is beeing completed offline
    the user should see the element   jQuery = p:contains("Project setup has been completed offline.")
    the user clicks the button/link   link = Return to project details
    the user should see the element   jQuery = p:contains("Project setup has been completed offline.")

The user is able to see that the project is being Managed offline
    the user should see the element   jQuery = p:contains("This project is being managed offline.")
    the user clicks the button/link   link = Return to project details
    the user should see the element   jQuery = p:contains("This project is being managed offline.")

The user navigates to the project to be Manage offline
    the user navigates to the page    ${server}/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link   jQuery = #table-project-status td:contains("Complete") a
    the user clicks the button/link   link = Manage project status

Custom suite setup
    the user logs-in in new browser              &{ifs_admin_user_credentials}

The user navigates to the Manage Project status page
    the user clicks the button/link   jQuery = tr:contains("${WITHDRAWN_PROJECT_COMPETITION_NAME_1}") a:contains("Incomplete")
    the user clicks the button/link   link = Manage project status

the user marks the project as managed offline
    the user selects the radio button    state   HANDLED_OFFLINE
    the user selects the checkbox        confirmationOffline
    the user clicks the button/link      jQuery = button:contains("Change project status")

the user marks the project as completed offline
    the user navigates to the page     ${server}/project-setup-management/competition/${MARKOFFLINE_COMPETITION}/status/all
    the user clicks the button/link    jQuery = #table-project-status td:contains("View") a
    the user clicks the button/link    link = Manage project status
    the user selects the radio button  state  COMPLETED_OFFLINE
    the user selects the checkbox      confirmationCompleteOffline
    the user clicks the button/link    jQuery = button:contains("Change project status")


