*** Settings ***
Documentation     INFUND-4821: As a project finance team member I want to have a summary overview of project details for this competition so I can refer to this in a consistent way throughout the finance checks section
...
...               INFUND-4903: As a Project Finance team member I want to view a list of the status of all partners' bank details checks so that I can navigate from the internal dashboard
...
...               INFUND-4049: As an internal user I want to have an overview of where a project is in the Project Setup process so that I can view and manage outstanding tasks
...
...               INFUND-5516:  As an internal user, I want to view the Project Setup status link
...
...               INFUND-5300: As a Project Finance team member I want to have an equivalent dashboard to the Competitions team for Project Setup so that I can view the appropriate partners'
...                            statuses and access pages appropriate to my role
...
...               INFUND-7109 Bank Details Status - Internal user
...
...               INFUND-5899 As an internal user I want to be able to use the breadcrumb navigation consistently throughout Project Setup so I can return to the previous page as appropriate
...
...               IFS-1881 Project Setup internal project dashboard navigation
Suite Setup       the user logs-in in new browser    &{internal_finance_credentials}
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot


*** Test Cases ***
Project Finance has a dashboard and can see projects in PS
    [Documentation]    INFUND-5300, IFS-1881
    [Tags]  HappyPath
    Given the user navigates to the page    ${COMP_MANAGEMENT_PROJECT_SETUP}
    When the user clicks the button/link    link = ${PS_Competition_Name}
    Then the user should see the element    link = All projects
    And the user should see the element     jQuery = tr:nth-child(3) th:contains("${PS_IU_Application_Title}")
    And the user should see the element     jQuery = tr:nth-child(3) th a:contains("${PS_IU_Application_No}")
    And the user should see the element     jQuery = tr:nth-child(3) th:contains("3 partners")
    And the user should see the element     jQuery = tr:nth-child(3) th:contains("Lead: ${Ntag_Name}")
    And the user should see the element     jQuery = tr:nth-child(4) th:contains("${Grade_Crossing_Applicaiton_Titile}")
    And the user should see the element     jQuery = tr:nth-child(5) th:contains("${PS_SP_APPLICATION_TITLE}")
    When the user clicks the button/link    link = ${PS_IU_Application_No}
    Then the user should be redirected to the correct page     ${server}/management/competition/${PS_Competition_Id}/application/${PS_IU_Application_No}
    And the user should not see an error in the page

Pr Finance can visit an application and navigate back
    [Documentation]  IFS-544
    [Tags]  HappyPath
    Given the user navigates to the page  ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    When the user clicks the button/link  link = ${PS_IU_Application_No}
    Then the user should see the element  jQuery = h1:contains("Application overview")
    When the user clicks the button/link  link = Back
    Then the user should be redirected to the correct page  ${server}/project-setup-management/competition/${PS_Competition_Id}/status

Project Finance can see the status of projects in PS
    [Documentation]  INFUND-5300, INFUND-7109
    [Tags]
    Given the user navigates to the page     ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element     css = #table-project-status tr:nth-of-type(3) td:nth-of-type(1).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(3) td:nth-of-type(2).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(3) td:nth-of-type(3).status.ok
    And the user should see the element      css = #table-project-status tr:nth-of-type(3) td:nth-of-type(5).status.action

# Project Finance can see Bank Details - testcase moved to 04__experian_feedback.robot
Other internal users cannot see Bank details or Finance checks
    [Documentation]    INFUND-4903, INFUND-5720, IFS-1881
    [Tags]    Experian  HappyPath
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    # This is added to HappyPath because CompAdmin should NOT have access to Bank details
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link = ${PS_Competition_Name}
    Then the user should see the element          link = All projects
    And the user should not see the element       css = #table-project-status tr:nth-of-type(3) td.status.ok:nth-of-type(4) a
    And the user should not see the element       css = #table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5) a
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${PS_IU_Application_Project}/review-all-bank-details    ${403_error_message}
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${PS_IU_Application_Project}/finance-check    ${403_error_message}

Comp Admin user can see the internal project summary page
    [Documentation]    INFUND-4049, INFUND-5899
    [Tags]
    Given the user navigates to the page             ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should see the element             jQuery = th div:contains("${PS_EF_APPLICATION_TITLE}")
    And the user clicks the button/link              css = #table-project-status > tbody > tr:nth-child(3) > td:nth-child(4) > a   # Monitoring officer page link
    And the user should not see an error in the page





