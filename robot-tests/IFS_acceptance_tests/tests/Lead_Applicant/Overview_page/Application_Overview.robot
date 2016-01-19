*** Settings ***
Documentation     -INFUND-408: As an applicant, and I am on the application overview I do not need to see progress updates for certain questions such as appendix questions
...
...               -INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section.
...
...               -INFUND-37: As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take.
...
...               -INFUND-32: As an applicant and I am on the MyApplications page, I can view the status of all my current applications, so I know what actions I need to take
...
...               -INFUND-1072: As an Applicant I want to see the Application Overview page redesigned so that they meet the agreed style
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Default Tags      Applicant    Overview
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Test Cases ***
Applicant can access the Overview page
    [Tags]    Applicant    Overview
    Given the user is logged in as applicant
    When the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Then the Applicant should see the overview of the selected application

Verify the "Review and submit" button (overview page)
    [Documentation]    -INFUND-195
    ...    -INFUND-214
    [Tags]    Applicant    Submit    Review and Submit    Overview
    Given Applicant goes to the Overview page
    And the overview page should have the "Review & Submit" button
    When the applicant clicks the submit button
    Then the applicant redirects to the summary page

Applicant can see the List with the sections
    [Tags]    Applicant    Overview
    When Applicant goes to the Overview page
    Then the applicant should see three sections

Only the main questions should show and not the file uploads
    [Documentation]    INFUND-428
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    Then the uploads should not be visible

The days left to submit should be visible
    [Documentation]    -INFUND-37
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    Then the "Days left to submit" should be visible in the overview page

The Progress bar should be visible in the overview page
    [Documentation]    INFUND-32
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    The Progress bar should be visible in the overview page

*** Keywords ***
the user is logged in as Applicant
    Location Should Be    ${applicant_dashboard_url}

the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Click Link    link=A novel solution to an old problem

the Applicant should see the overview of the selected application
    Location Should Be    ${APPLICATION_OVERVIEW_URL}

the Applicant is in the application overview page
    Applicant goes to the Overview page

the applicant should see three sections
    Element Should Be Visible    css=#section-1 .heading-medium
    Element Should Be Visible    css=#section-2 .heading-medium
    Element Should Be Visible    css=#section-6 .heading-medium

the uploads should not be visible
    Element Should Not Be Visible    css=#question-14 > div > input[type="file"]
    Element Should Not Be Visible    css=#question-17 > div > input[type="file"]
    Element Should Not Be Visible    css=#question-18 > div > input[type="file"]

the "Days left to submit" should be visible in the overview page
    Element Should Be Visible    css=.sub-header .deadline .pie-overlay > div

The Progress bar should be visible in the overview page
    Element should be visible    css=.progress-indicator

the overview page should have the "Review & Submit" button
    Element Should Be Visible    link=Review & submit

the applicant clicks the submit button
    Click Link    link=Review & submit
    Wait Until Page Contains    Application Summary

the applicant redirects to the summary page
    Location Should Be    ${SUMMARY_URL}
