*** Settings ***
Documentation     -INFUND-408: As an applicant, and I am on the application overview I do not need to see progress updates for certain questions such as appendix questions
...
...               -INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section.
...
...               -INFUND-37: As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take.
...
...               -INFUND-32: As an applicant and I am on the MyApplications page, I can view the status of all my current applications, so I know what actions I need to take
Suite Setup       Login as User    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/variables/User_credentials.robot

*** Test Cases ***
Verify that the Applicant can access the Overview page
    [Tags]    Applicant    Overview
    Given the user is logged in as applicant
    When the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Then the Applicant should see the overview of the selected application

Verify that the Applicant can see the List with the sections
    [Tags]    Applicant    Overview
    When Applicant goes to the Overview page
    Then the applicant should see three sections

Verify that only the main questions show and not the file uploads
    [Documentation]    INFUND-428
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    Then the uploads should not be visible

Verify the applicant can see the days left to submit
    [Documentation]    -INFUND-37
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    The "Days left to submit" should be visible in the overview page
    The "days left to submit" should be correct in the overview page

Verify the "Progress bar" is 0% when the application is empty
    [Documentation]    INFUND-32
    [Tags]    Applicant    Overview
    When the Applicant is in the application overview page
    The Progress bar should be 0% in the overview page
    and the progress bar on the My applications page should be 0%

Verify the "Progress bar" is 14% when the applicant marks as complete one question from each section
    [Documentation]    INFUND-32
    [Tags]    Applicant    Overview
    When the applicant completes one question from every section
    Then the Progress bar should be 14% in the overview page
    and the progress bar on the my applications page should be 14%

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

The "Days left to submit" should be visible in the overview page
    Element Should Be Visible    css=.sub-header .deadline .pie-overlay > div

The Progress bar should be 0% in the overview page
    Element Text Should Be    css=#content > div.grid-row > div.column-half.competition-details > div > div > div.progress    0%

the applicant completes one question from every section
    go to    ${APPLICATION_URL}
    Input Text    id=11    test
    Click Element    css=#question-11 > div > div.textarea-wrapped.word-count > div.textarea-footer > button
    click link    link=Scope
    Input Text    id=13    test
    Click Element    css=#question-13 > div > div.textarea-wrapped.word-count > div.textarea-footer > button
    Click Link    link=Your business proposition
    Input Text    id=1    test
    Click Element    css=#question-1 > div > div > div.textarea-footer > button
    Click Link    link=Your approach to the project
    input text    id=5    test
    Click Element    css=#question-5 > div > div.textarea-wrapped.word-count > div.textarea-footer > button
    Click Link    link=Funding
    Input Text    id=15    test
    Click Element    css=#question-15 > div > div.textarea-wrapped.word-count > div.textarea-footer > button
    click link    link=Your finances
    Click Element    css=#question-20 > div.collapsible > h2:nth-child(1) > button
    Click Element    css=#collapsible-1 > div:nth-child(2) > div > button

the Progress bar should be 14% in the overview page
    go to    ${APPLICATION_OVERVIEW_URL}
    Element Text Should Be    css=#content > div.grid-row > div.column-half.competition-details > div > div > div.progress    14%

the progress bar on the My applications page should be 0%
    go to    ${DASHBOARD_URL}
    Element Text Should Be    css=#content > div > section.in-progress > ul > li:nth-child(1) > div > div:nth-child(1) > div > div > div.progress > div    0%

the progress bar on the my applications page should be 14%
    go to    ${DASHBOARD_URL}
    Element Text Should Be    css=#content > div > section.in-progress > ul > li:nth-child(1) > div > div:nth-child(1) > div > div > div.progress > div    14%

The "days left to submit" should be correct in the overview page
    Element Text Should Be    css=.sub-header .deadline .pie-overlay > div    8
