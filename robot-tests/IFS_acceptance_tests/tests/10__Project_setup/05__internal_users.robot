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
Suite Setup       the project is completed if it is not already complete
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot


*** Test Cases ***
Project Finance user can see the internal project summary page
    [Documentation]    INFUND-4049, INFUND-5144
    [Tags]
    Given the user navigates to the page    ${internal_competition_status}
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}
    And the user clicks the button/link    css=#table-project-status tr:nth-child(2) td:nth-child(3) a   #Monitoring officer page link
    And the user goes back to the previous page
    And the user should not see the element   css=#table-project-status tr:nth-child(2) td:nth-child(6) a  #SP element is not seen

Project Finance has a dashboard and can see projects in PS
    [Documentation]    INFUND-5300, IFS-1881
    [Tags]
    [Setup]  Log in as a different user    &{internal_finance_credentials}
    Given the user navigates to the page  ${COMP_MANAGEMENT_PROJECT_SETUP}
    When the user clicks the button/link    link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should see the element    jQuery=tr:nth-child(2) th:contains("${PROJECT_SETUP_APPLICATION_1_TITLE}")
    And the user should see the element     jQuery=tr:nth-child(2) th a:contains("${PROJECT_SETUP_APPLICATION_1_NUMBER}")
    And the user should see the element     jQuery=tr:nth-child(2) th:contains("3 partners")
    And the user should see the element     jQuery=tr:nth-child(2) th:contains("Lead: ${FUNDERS_PANEL_APPLICATION_1_LEAD_ORGANISATION_NAME}")
    And the user should see the element     jQuery=tr:nth-child(3) th:contains("Office Chair for Life")
    And the user should see the element     jQuery=tr:nth-child(1) th:contains("Elbow grease")
    When the user clicks the button/link    link=${PROJECT_SETUP_APPLICATION_1_NUMBER}
    Then the user should be redirected to the correct page     ${server}/management/competition/${PROJECT_SETUP_COMPETITION}/application/${PROJECT_SETUP_APPLICATION_1}
    And the user should not see an error in the page

Pr Finance can visit an application and navigate back
    [Documentation]  IFS-544
    [Tags]  HappyPath
    Given the user navigates to the page  ${internal_competition_status}
    When the user clicks the button/link  link=${PROJECT_SETUP_APPLICATION_1}
    Then the user should see the element  jQuery=h1:contains("Application overview")
    When the user clicks the button/link  link=Back
    Then the user should be redirected to the correct page  ${internal_competition_status}

Project Finance can see the status of projects in PS
    [Documentation]  INFUND-5300, INFUND-7109
    [Tags]
    Given the user navigates to the page    ${internal_competition_status}
    Then the user should see the element    css=#table-project-status tr:nth-of-type(2) td:nth-of-type(1).status.ok
    And the user should see the element     css=#table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.ok
    And the user should not see the element  css=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status.waiting
    And the user should see the element     css=#table-project-status tr:nth-of-type(2) td:nth-of-type(4).status.action

# Project Finance can see Bank Details - testcase moved to 04__experian_feedback.robot
Other internal users cannot see Bank details or Finance checks
    [Documentation]    INFUND-4903, INFUND-5720, IFS-1881
    [Tags]    Experian    HappyPath
    [Setup]    Log in as a different user    &{Comp_admin1_credentials}
    # This is added to HappyPath because CompAdmin should NOT have access to Bank details
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link=${PROJECT_SETUP_COMPETITION_NAME}
    Then the user should not see the element      css=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3) a
    And the user should not see the element       css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(4) a
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/review-all-bank-details    ${403_error_message}
    And the user navigates to the page and gets a custom error message    ${server}/project-setup-management/project/${PROJECT_SETUP_APPLICATION_1_PROJECT}/finance-check    ${403_error_message}

Comp Admin user can see the internal project summary page
    [Documentation]    INFUND-4049, INFUND-5899
    [Tags]
    Given the user navigates to the page    ${internal_competition_status}
    Then the user should see the text in the page    ${PROJECT_SETUP_APPLICATION_1_TITLE}
    And the user clicks the button/link    css=#table-project-status tr:nth-child(2) td:nth-child(3) a   #Monitoring officer page link
    And the user should not see an error in the page
    And the user goes back to the previous page
    When the user clicks the button/link    link=Competition dashboard
    Then the user should see the text in the page    All competitions


*** Keywords ***
the project is completed if it is not already complete
    The user logs-in in new browser  &{lead_applicant_credentials}
    the user navigates to the page    ${project_in_setup_page}/details
    ${project_manager_not_set}    ${value}=    run keyword and ignore error without screenshots    The user should not see the element    css=#project-manager-status.yes
    run keyword if  '${project_manager_not_set}' == 'PASS'  all previous sections of the project are completed
    run keyword if  '${project_manager_not_set}' == 'FAIL'  login as a different user  &{internal_finance_credentials}

all previous sections of the project are completed
    project lead submits project details        ${PROJECT_SETUP_APPLICATION_1_PROJECT}
    partners submit finance contacts
    all partners submit their bank details
    project finance approves bank details
    project finance submits monitoring officer  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  Grace  Harper  ${test_mailbox_two}+monitoringofficer@gmail.com  08549731414

partners submit finance contacts
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_ID}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{lead_applicant_credentials}
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_PARTNER_ID}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{collaborator1_credentials}
    the partner submits their finance contact  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_ID}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  &{collaborator2_credentials}

all partners submit their bank details
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_LEAD_PARTNER_EMAIL}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}
    partner submits his bank details  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_EMAIL}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}  ${account_one}  ${sortCode_one}

project finance approves bank details
    log in as a different user                          &{internal_finance_credentials}
    the project finance user approves bank details for  ${PROJECT_SETUP_APPLICATION_1_LEAD_ORGANISATION_NAME}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}
    the project finance user approves bank details for  ${PROJECT_SETUP_APPLICATION_1_ACADEMIC_PARTNER_NAME}  ${PROJECT_SETUP_APPLICATION_1_PROJECT}






