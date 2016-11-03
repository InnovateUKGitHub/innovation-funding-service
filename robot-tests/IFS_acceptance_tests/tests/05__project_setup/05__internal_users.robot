*** Settings ***
Documentation     INFUND-4821: As a project finance team member I want to have a summary overview of project details for this competition so I can refer to this in a consistent way throughout the finance checks section
...
...               INFUND-4903: As a Project Finance team member I want to view a list of the status of all partners' bank details checks so that I can navigate from the internal dashboard
...
...               INFUND-4049: As an internal user I want to have an overview of where a project is in the Project Setup process so that I can view and manage outstanding tasks
...
...               INFUND-5516:  As a internal user, I want to view the Project Setup status link
...
...               INFUND-5300: As a Project Finance team member I want to have an equivalent dashboard to the Competitions team for Project Setup so that I can view the appropriate partners'
...                            statuses and access pages appropriate to my role
Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***

# Project Finance can see Bank Details - testcase moved to 04__experian_feedback.robot
Other internal users cannot see Bank details or Finance checks
    [Documentation]    INFUND-4903, INFUND-5720
    [Tags]    Experian    HappyPath
    [Setup]    Log in as user    john.doe@innovateuk.test    Passw0rd
    # This is added to HappyPath because CompAdmin should NOT have access to Bank details
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link=Killer Riffs
    Then the user should see the element          jQuery=h2:contains("Projects in setup")
    And the user should not see the element       jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3) a
    And the user should not see the element       jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(4) a
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/1/review-all-bank-details    You do not have the necessary permissions for your request
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/1/finance-check    You do not have the necessary permissions for your request


Project Finance user can see the internal project summary page
    [Documentation]    INFUND-4049, INFUND-5144
    [Tags]
    [Setup]    log in as a different user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the text in the page    best riffs
    And the user clicks the button/link    xpath=//a[contains(@href, 'project-setup-management/project/1/monitoring-officer')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    xpath=//a[contains(@href, 'project-setup-management/project/1/review-all-bank-details')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user should not see the element    xpath=//a[contains(@href, '/project-setup-management/project/4/spend-profile/approval')]    # since the spend profile hasn't been generated yet - see INFUND-5144


Comp Admin user cannot see the finance check summary page(duplicate)
    [Documentation]    INFUND-4821
    [Tags]
    [Setup]    Log in as a different user    john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/1/finance-check    You do not have the necessary permissions for your request

Comp Admin user can see the internal project summary page
    [Documentation]    INFUND-4049
    [Tags]
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the text in the page    best riffs
    And the user clicks the button/link    xpath=//a[contains(@href, '/project-setup-management/project/1/monitoring-officer')]
    And the user should not see an error in the page


Project Finance has a dashboard and can see projects in PS
    [Documentation]    INFUND-5300
    [Tags]
    [Setup]  Log in as a different user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page  ${COMP_MANAGEMENT_PROJECT_SETUP}
    Then the user should see the element    link=Killer Riffs
    When the user clicks the button/link    link=Killer Riffs
    Then the user should see the element    jQuery=.column-third.alignright.extra-margin h2:contains("Projects in setup")
    And the user should see the element     jQuery=tr:nth-child(1) th:contains("best riffs")
    And the user should see the element     jQuery=tr:nth-child(1) th a:contains("00000026")
    And the user should see the element     jQuery=tr:nth-child(1) th:contains("3 partners")
    And the user should see the element     jQuery=tr:nth-child(1) th:contains("Lead: Vitruvius Stonework Limited")
    And the user should see the element     jQuery=tr:nth-child(2) th:contains("better riffs")
    And the user should see the element     jQuery=tr:nth-child(3) th:contains("awesome riffs")
    When the user clicks the button/link    link=00000026
    Then the user navigates to the page     ${server}/management/competition/6/application/26
    And the user should not see an error in the page

Project Finance can see the status of projects in PS
    [Documentation]  INFUND-5300
    [Tags]
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.action
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.action

Project Finance can see the progress of partners bank details
    [Documentation]  INFUND-4903
    [Tags]    HappyPath
    Given the user navigates to the page            ${internal_project_summary}
    And the user clicks the button/link             jQuery=#table-project-status tr:nth-child(1) td:nth-child(4) a
    Then the user navigates to the page             ${server}/project-setup-management/project/1/review-all-bank-details
    And the user should see the text in the page    This overview shows whether each partner has submitted their bank details
    Then the user should see the element            jQuery=tr:nth-child(1) td:nth-child(2):contains("Complete")
    # And the user should see the element           jQuery=tr:nth-child(2) td:nth-child(2):contains("Complete")  TODO INFUND-5966
    # And the user should see the element           jQuery=tr:nth-child(3) td:nth-child(2):contains("Complete")  TODO Upcoming functionality covering Academic user
    When the user clicks the button/link            link=Vitruvius Stonework Limited
    Then the user should see the text in the page   Vitruvius Stonework Limited - Account details
    And the user should see the text in the page    Bob Jones
    And the user should see the element             jQuery=a:contains("${test_mailbox_one}+invitedprojectmanager@gmail.com")
    And the user should see the text in the page    0987654321
    #TODO for Jessica and Pete


