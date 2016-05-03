*** Settings ***
Documentation     INFUND-2443 Acceptance test: Check that the comp manager cannot edit an application's finances
...
...               INFUND-2304 Read only view mode of applications from the application list page
Suite Setup       Run Keywords    Log in as user    &{Comp_admin1_credentials}
...               AND    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
Suite Teardown    User closes the browser
Force Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${valid_pdf}      testing.pdf

*** Test Cases ***
Comp admin can open the view mode of the application
    [Documentation]    INFUND-2300
    ...
    ...    INFUND-2304
    ...
    ...    INFUND-2435
    [Tags]    Competition management
    [Setup]    Run keywords    Log in as user    &{lead_applicant_credentials}
    ...    AND    the user can see the option to upload a file on the page    ${project_team_url}
    ...    AND    the user uploads the file to the 'project team' question    ${valid_pdf}
    Given log in as user    &{Comp_admin1_credentials}
    And the user navigates to the page    ${COMP_MANAGEMENT_APPLICATIONS_LIST}
    When the user clicks the button/link    link=00000001
    Then the user should be redirected to the correct page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    And the user should see the element    Link=Print application
    And the user should see the text in the page    A novel solution to an old problem
    And the user can see the upload for the 'Technical approach' question
    And the user can view this file without any errors

Comp admin should not be able to edit the finances
    [Documentation]    INFUND-2443
    Given the user navigates to the page    ${COMP_MANAGEMENT_APPLICATION_1_OVERVIEW}
    When the user clicks the button/link    jQuery=button:contains("Finances Summary")
    Then the user should not see the element    link=your finances

*** Keywords ***
the user uploads the file to the 'project team' question
    [Arguments]    ${file_name}
    Choose File    name=formInput[18]    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms

the user can see the option to upload a file on the page
    [Arguments]    ${url}
    The user navigates to the page    ${url}
    Page Should Contain    Upload

the user can see the upload for the 'Technical approach' question
    the user clicks the button/link    css=[aria-controls="collapsible-8"]
    the user should see the text in the page    ${valid_pdf}

the user can view this file without any errors
    the user clicks the button/link    xpath=//a[contains(@href, 'download')]
    the user should not see an error in the page
