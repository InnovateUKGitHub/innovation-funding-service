*** Settings ***
Documentation     INFUND-2607 As an applicant I want to have a link to the feedback for my application from the Application Overview page when it becomes available so I can review the assessor feedback for my application
Suite Teardown    the user closes the browser
Force Tags        Upload    CompAdmin
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${successful_application_overview}    ${server}/application/${FUNDERS_PANEL_APPLICATION_1}
${unsuccessful_application_overview}    ${server}/application/${FUNDERS_PANEL_APPLICATION_2}
${successful_application_comp_admin_view}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_1}
${unsuccessful_application_comp_admin_view}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION}/application/${FUNDERS_PANEL_APPLICATION_2}

*** Test Cases ***
Comp admin can view uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    HappyPath
    [Setup]
    Given guest user log-in    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${successful_application_comp_admin_view}
    And the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]    The user goes back to the previous page

Comp admin can view unsuccessful uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    Given the user navigates to the page    ${unsuccessful_application_comp_admin_view}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    And the user navigates to the page    ${unsuccessful_application_comp_admin_view}

Unsuccessful applicant can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    [Setup]    Log in as a different user    worth.email.test.two+fundfailure@gmail.com    Passw0rd
    Given the user navigates to the page    ${unsuccessful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]    the user navigates to the page    ${unsuccessful_application_comp_admin_view}

Unsuccessful applicant cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove

Partner can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    HappyPath
    Given Log in as a different user    worth.email.test+fundsuccess@gmail.com    Passw0rd
    And the user navigates to the page    ${successful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should not see an error in the page
    [Teardown]    the user navigates to the page    ${successful_application_overview}

Partner cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove
