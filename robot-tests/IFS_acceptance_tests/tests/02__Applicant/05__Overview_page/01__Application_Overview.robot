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
...
...               -INFUND-1162: As an applicant I want the ability to have a printable version of my application for review, so I can print and download it for offline use.
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Default Tags      Applicant    Overview
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Test Cases ***
Applicant can access the Overview page
    [Tags]    Applicant    Overview    HappyPath
    When the user clicks the button/link    link=A novel solution to an old problem
    Then the user should be redirected to the correct page    ${APPLICATION_OVERVIEW_URL}

Verify the "Review and submit" button (overview page)
    [Documentation]    -INFUND-195
    ...    -INFUND-214
    [Tags]    Applicant    Submit    Review and Submit    Overview    HappyPath
    When the user clicks the button/link    link=Review & submit
    Then the user should be redirected to the correct page    ${SUMMARY_URL}

Applicant can see the List with the sections
    [Tags]    Applicant    Overview
    When the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    Then the applicant should see three sections

The user should not see the file uploads (overview page)
    [Documentation]    INFUND-428
    [Tags]    Applicant    Overview
    Then the user should not see the element    css=#question-14 > div > input[type="file"]
    And the user should not see the element    css=#question-17 > div > input[type="file"]
    And the user should not see the element    css=#question-18 > div > input[type="file"]

The days left to submit should be visible
    [Documentation]    -INFUND-37
    [Tags]    Applicant    Overview    HappyPath
    Then the user should see the element    css=.progress-indicator

The Progress bar should be visible in the overview page
    [Documentation]    INFUND-32
    [Tags]    Applicant    Overview    HappyPath
    Then the user should see the element    css=.progress-indicator

User can print the application
    [Documentation]    INFUND-1162
    [Tags]      Applicant   Overview
    Given the user navigates to the page without the usual headers    ${SERVER}/application/1/print?noprint    #This URL its only for testing purposes
    Then the user should see the element    jQuery=.button:contains("Print your application")
    When the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    When the user clicks the button/link    link= Print your application
    Then the user should get a new print window

*** Keywords ***
the applicant should see three sections
    Element Should Be Visible    css=#section-1 .bold-medium
    Element Should Be Visible    css=#section-2 .heading-medium
    Element Should Be Visible    css=#section-6 .heading-medium

the user should get a new print window
    Select Window    Title=Print Application - Innovation Funding Service
