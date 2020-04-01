*** Settings ***
Documentation    IFS-5110 Handle IFS applications offline CTA
...
...              IFS-5939 New manage project status page
...
...              IFS-5941 Respond to onhold status changes
...
...              IFS-6054 Display completed projects in the previous tab
Suite Setup       Custom suite setup
Suite Teardown    the user closes the browser
Force Tags        Administrator  HappyPath
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/PS_Common.robot

*** Test Cases ***
IFS Admin marks a project as offline
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

IFS Admin marks a project as completed offline
    [Documentation]  IFS-5110  IFS-6054
    Given Log in as a different user     &{ifs_admin_user_credentials}
    When the user marks the project as completed offline
    Then the user is able to see that the project is beeing completed offline
    And The user is able to see completed offline project in previous tab

On hold Validations
    [Documentation]  IFS-5941
    [Setup]  the user navigates to the page              ${server}/project-setup-management/competition/${OnHoldCompId}/project/${OnHoldProjectId}/details
    Given the user clicks the button/link                link = Manage project status
    When the user selects the radio button               state   ON_HOLD
    And the user clicks the button/link                  jQuery = button:contains("Change project status")
    Then the user should see on hold validation errors

IFS Admin is able to mark a project as on hold
    [Documentation]  IFS-5941
    Given the user marks the project as on hold
    Then the user is able to see that the project is on hold

Project status page validations
    [Documentation]  IFS-5941
    Given the user clicks the button/link                jQuery = span:contains("Add a comment")
    When the user clicks the button/link                 jQuery = button:contains("Save comment")
    Then the user should see a field and summary error   Enter the details.

IFS Admin is able to add a comment on the Project status page
    [Documentation]  IFS-5941
    Given the user enters text to a text field  id = details   Adding a comment
    When the user clicks the button/link        jQuery = button:contains("Save comment")
    Then the user should see the element        jQuery = p:contains("Adding a comment")

IFS Admin is able to remove on hold status
    [Documentation]  IFS-5941
    Given the user clicks the button/link    jQuery = button:contains("Remove on hold status")
    Then the user should see the element     jQuery = p:contains("This project is no longer on hold.")
    And the user should see the element      jQuery = h1:contains("Manage project status")

Finance contact is able to mark a project as on hold
    [Documentation]  IFS-5941
    [Setup]   Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page    ${server}/project-setup-management/competition/${OnHoldCompId}/project/${OnHoldProjectId}/details
    When the user clicks the button/link    link = Manage project status
    And the user marks the project as on hold
    Then the user is able to see that the project is on hold

Finance contact is able to add a comment on the Project status page
    [Documentation]  IFS-5941
    Given the user clicks the button/link       jQuery = span:contains("Add a comment")
    When the user enters text to a text field   id = details   Adding a comment
    And the user clicks the button/link         jQuery = button:contains("Save comment")
    Then the user should see the element        jQuery = p:contains("Adding a comment")

Finance contact is able to remove on hold status
    [Documentation]  IFS-5941
    Given the user clicks the button/link     jQuery = button:contains("Remove on hold status")
    When the user should see the element      jQuery = p:contains("This project is no longer on hold.")
    And the user should see the element      jQuery = h1:contains("Project details")
    Then after a page refresh on hold status is no longer displayed

*** Keywords ***
After a page refresh on hold status is no longer displayed
    the user navigates to the page        ${server}/project-setup-management/competition/${OnHoldCompId}/project/${OnHoldProjectId}/details
    the user should not see the element   jQuery = p:contains("This project is on hold.")

The user should see on hold validation errors
    the user should see a field and summary error   Enter the reason to mark project as on hold.
    the user should see a field and summary error   Enter the details.

The user is able to see that the project is on hold
    the user should see the element   jQuery = p:contains("This project is on hold.")
    the user clicks the button/link   link = View details, reply or remove on hold status
    the user should see the element   jQuery = p:contains("Details")

The user marks the project as on hold
    the user enters text to a text field   id = onHoldReason   Reason
    the user enters text to a text field   id = onHoldDetails  Details
    the user clicks the button/link        jQuery = button:contains("Change project status")

The user is able to see that the project is beeing completed offline
    the user should see the element   jQuery = p:contains("Project setup has been completed offline.")
    the user clicks the button/link   link = Return to project details
    the user should see the element   jQuery = p:contains("Project setup has been completed offline.")

The user is able to see completed offline project in previous tab
    the user navigates to the page     ${server}/management/competition/${MARKOFFLINE_COMPETITION}/previous
    the user expands the section       Projects
    the user should see the element    jQuery = th:contains("${MARKOFFLINE_APPLICATION_1_TITLE}")

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
    the user clicks the button/link    css = #table-project-status td:nth-child(2).status.ok a
    the user clicks the button/link    link = Manage project status
    the user selects the radio button  state  COMPLETED_OFFLINE
    the user selects the checkbox      confirmationCompleteOffline
    the user clicks the button/link    jQuery = button:contains("Change project status")


