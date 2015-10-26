*** Settings ***
Documentation     -INFUND-408: As an applicant, and I am on the application overview I do not need to see progress updates for certain questions such as appendix questions
...
...               -INFUND-39: As an applicant and I am on the application overview, I can select a section of the application, so I can see the status of each subsection in this section.
...
...
Test Setup       Login as User    &{lead_applicant_credentials}
Test Teardown    TestTeardown User closes the browser
Resource          ../GLOBAL_VARIABLES.robot
Resource          ../GLOBAL_LIBRARIES.robot
Resource          ../Login_actions.robot
Resource          ../USER_CREDENTIALS.robot
Resource          ../Applicant_actions.robot

*** Test Cases ***
Verify that the Applicant can access the Overview page
    [Tags]    Applicant
    Given the user is logged in as applicant
    When the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Then the Applicant should see the overview of the selected application

Verify that the Applicant can see the List with the sections
    [Tags]    Applicant
    Given the Applicant is in the application overview page
    Then the applicant should see six sections

Verify that when Applicant clicks the "Scope" this section is expanded
    [Tags]    Applicant
    Given the Applicant is in the application overview page
    Then the Applicant clicks the "Scope" section
    Then the First section should not be expanded
    And the first section should be hidden
    And the second button should be expanded
    And the second section should be visible
    And the "Scope" sub-section should be visible

Verify that only the main questions show and not the file uploads
    [Documentation]    INFUND-428
    Given the Applicant is in the application overview page
    When the Applicant clicks the "Yous approach" section
    Then the uploads should not be visible

*** Keywords ***
the user is logged in as Applicant
    Location Should Be    ${applicant_dashboard_url}

the Applicant clicks the "Rovel Additive Manufacturing Process" application
    Click Link    link=A novel solution to an old problem

the Applicant should see the overview of the selected application
    Location Should Be    ${APPLICATION_OVERVIEW_URL}


the Applicant is in the application overview page
    Applicant goes to the Overview page

the applicant should see six sections
    Page Should Contain Element    css=.section-overview > div:nth-of-type(1)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(2)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(3)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(4)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(5)
    Page Should Contain Element    css=.section-overview > div:nth-of-type(6)


the Applicant clicks the "Scope" section
    [Documentation]    1. click second section
    Click Element    css=.section-overview > h2:nth-of-type(2) button

The First section should not be expanded
    [Documentation]    Assumption: page is loaded with first section option because of /section/1
    ...
    ...
    ...    2. check if the first button is now not expanded anymore (aria-expanded=false)
    ...
    ...    4. check if the second button is now expanded (aria-expanded=true)
    ...
    ...    5. check if the second section is now visible \ (aria-hidden=false)
    Page Should Contain Element    css=.section-overview > h2:nth-of-type(1) button[aria-expanded="false"]

the first section should be hidden
    Page Should Contain Element    css=.section-overview > div:nth-of-type(1)[aria-hidden="true"]

the second button should be expanded
    Page Should Contain Element    css=.section-overview > h2:nth-of-type(2) button[aria-expanded="true"]

the second section should be visible
    Page Should Contain Element    css=.section-overview > div:nth-of-type(2)[aria-hidden="false"]

the "Scope" sub-section should be visible
    Element Should Be Visible    link=How does your project align with the scope of this competition?


the Applicant clicks the "Yous approach" section
    Click Element    css=#content > form > div > h2:nth-child(7) > button

the uploads should not be visible
    Element Should Not Be Visible    css=#question-14 > div > input[type="file"]
    Element Should Not Be Visible    css=#question-17 > div > input[type="file"]
    Element Should Not Be Visible    css=#question-18 > div > input[type="file"]
