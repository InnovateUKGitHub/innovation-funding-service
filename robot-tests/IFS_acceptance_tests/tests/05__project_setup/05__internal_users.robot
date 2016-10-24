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
Other internal users cannot see Bank details
    [Documentation]    INFUND-4903
    [Tags]    Experian    HappyPath    Pending
    #TODO INFUND-5720
    [Setup]    Log in as user    john.doe@innovateuk.test    Passw0rd
    # This is added to HappyPath because CompAdmin should NOT have access to Bank details
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link=Killer Riffs
    Then the user should see the element          jQuery=h2:contains("Projects in setup")
    And the user should not see the element           jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/1/review-all-bank-details    You do not have the necessary permissions for your request

Project Finance user can view the Project setup status page
    [Documentation]    INFUND-5516
    [Tags]    Failing
    Given the user navigates to the page          ${server}/project-setup-management/project/1/partner/documents
    And the user clicks the button/link           link=Project setup status
    Then the user should not see an error in the page
    And the user should see the text in the page   Projects in setup
    [Teardown]  Logout as user

Project Finance user can see the internal project summary page
    [Documentation]    INFUND-4049
    [Tags]    Failing
    [Setup]    Log in as user    project.finance1@innovateuk.test    Passw0rd
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the text in the page    best riffs
    And the user clicks the button/link    xpath=//a[contains(@href, 'project-setup-management/project/1/monitoring-officer')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    xpath=//a[contains(@href, 'project-setup-management/project/1/review-all-bank-details')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    xpath=//a[contains(@href, 'project-setup-management/project/1/partner/documents')]
    And the user should not see an error in the page
    [Teardown]    logout as user

Comp Admin user cannot see the finance check summary page(duplicate)
    [Documentation]    INFUND-4821
    [Tags]    Failing
    [Setup]    Log in as user    john.doe@innovateuk.test    Passw0rd
    Given the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/1/finance-check    You do not have the necessary permissions for your request

Comp Admin user can see the internal project summary page
    [Documentation]    INFUND-4049
    [Tags]    Failing
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the text in the page    best riffs
    And the user clicks the button/link    xpath=//a[contains(@href, '/project-setup-management/project/1/monitoring-officer')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link   xpath=//a[contains(@href, '/project-setup-management/project/1/review-all-bank-details')]
    And the user should not see an error in the page
    And the user goes back to the previous page
    And the user clicks the button/link    xpath=//a[contains(@href, '/project-setup-management/project/1/partner/documents')]
    And the user should not see an error in the page
    [Teardown]    logout as user

Project Finance has a dashboard and can see projects in PS
    [Documentation]    INFUND-5300
    [Tags]
    [Setup]  Log in as user  project.finance1@innovateuk.test    Passw0rd
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
    [Tags]    Failing
    Given the user navigates to the page    ${internal_project_summary}
    Then the user should see the element    jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(1).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(2).status.ok
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(3).status.action
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(4).status.action
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(5).status.waiting
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td:nth-of-type(6).status.action


