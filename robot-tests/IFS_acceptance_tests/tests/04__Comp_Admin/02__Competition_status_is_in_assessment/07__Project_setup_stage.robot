*** Settings ***
Documentation     INFUND-2607 As an applicant I want to have a link to the feedback for my application from the Application Overview page when it becomes available so I can review the assessor feedback for my application
...
...               INFUND-2612 As a partner I want to have a overview of where I am in the process and what outstanding tasks I have to complete so that I can understand our project setup steps
...
...
...               INFUND-2613 As a lead partner I need to see an overview of project details for my project so that I can edit the project details in order for Innovate UK to be able to assign an appropriate Monitoring Officer
...
...               INFUND-2614 As a lead partner I need to provide a target start date for the project so that Innovate UK has correct details for my project setup
Suite Teardown    the user closes the browser
Force Tags        Comp admin    Upload
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Variables ***
${successful_application_overview}    ${server}/application/16
${unsuccessful_application_overview}    ${server}/application/17
${SUCCESSFUL_PROJECT_PAGE}    ${server}/project/1
${successful_application_comp_admin_view}   ${server}/management/competition/3/application/16
${unsuccessful_application_comp_admin_view}     ${server}/management/competition/3/application/17

*** Test Cases ***
Partner can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    Given guest user log-in    worth.email.test+fundsuccess@gmail.com    Passw0rd
    And the user navigates to the page    ${successful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should see the text in the page    ${valid_pdf_excerpt}
    [Teardown]    the user navigates to the page    ${successful_application_overview}

Partner cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove

Partner can download the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    Pending
    # Pending until download functionality has been plugged in
    Given the user should see the text in the page    ${valid_pdf}
    When the user downloads the file from the link    ${valid_pdf}    ${download_link}
    Then the file should be downloaded    ${valid_pdf}
    [Teardown]    Remove File    ${valid_pdf}

Partner can see the project setup page
    [Documentation]    INFUND-2612
    When the user navigates to the page    ${SUCCESSFUL_PROJECT_PAGE}
    Then the user should see the element    jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page    Successful application
    And the user should see the text in the page    The application Cheese is good has been successful within the La Fromage competition
    And the user should see the element    link=View application and feedback
    And the user should see the element    link=View terms and conditions of grant offer
    And the user should see the text in the page    Project details
    And the user should see the text in the page    Monitoring Officer
    And the user should see the text in the page    Bank details
    And the user should see the text in the page    Other documents

Partner can see the overview of the project details
    [Documentation]    INFUND-2613
    When the user clicks the button/link    link=Project details
    Then the user should see the text in the page    Please supply the following details for your project and the team
    And the user should see the element    link=Start date
    And the user should see the element    link=Project address
    And the user should see the element    link=Project manager
    And the user should see the text in the page    Finance contacts

Partner can change the Start Date
    [Documentation]    INFUND-2614
    Given the user clicks the button/link    link=Start date
    And the duration should be visible
    When the user enters text to a text field    id=projectStartDate_year    2013
    And the user enters text to a text field    id=projectStartDate_month    1
    Then the user should see a validation error    Please enter a future date
    And the user shouldn't be able to edit the day field as all projects start on the first of the month
    When the user enters text to a text field    id=projectStartDate_month    1
    When the user enters text to a text field    id=projectStartDate_year    2018
    When the user clicks the button/link    jQuery=button:contains("Save")
    Then the user should see the text in the page    1 Jan 2018
    Then status of the start date should be Yes

Comp admin can view uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    [Setup]    Run Keywords    Logout as user
    Given guest user log-in    john.doe@innovateuk.test    Passw0rd
    When the user navigates to the page    ${successful_application_comp_admin_view}
    And the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should see the text in the page    ${valid_pdf_excerpt}

Comp admin can view unsuccessful uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    Given the user navigates to the page    ${unsuccessful_application_comp_admin_view}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should see the text in the page    ${valid_pdf_excerpt}
    And the user navigates to the page    ${unsuccessful_application_comp_admin_view}
    [Teardown]    Logout as user

Unsuccessful applicant can view the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    [Setup]    guest user log-in    worth.email.test.two+fundfailure@gmail.com    Passw0rd
    Given the user navigates to the page    ${unsuccessful_application_overview}
    When the user should see the text in the page    ${valid_pdf}
    And the user clicks the button/link    link=testing.pdf (7.94 KB)
    Then the user should see the text in the page    ${valid_pdf_excerpt}
    [Teardown]    the user navigates to the page    ${unsuccessful_application_comp_admin_view}

Unsuccessful applicant cannot remove the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]
    When the user should see the text in the page    ${valid_pdf}
    Then the user should not see the text in the page    Remove
    And the user should not see the element    link=Remove

Unsuccessful applicant can download the uploaded feedback
    [Documentation]    INFUND-2607
    [Tags]    Pending
    # Pending until download functionality has been plugged in
    Given the user should see the text in the page    ${valid_pdf}
    When the user downloads the file from the link    ${valid_pdf}    ${download_link}
    Then the file should be downloaded    ${valid_pdf}
    [Teardown]    Remove File    ${valid_pdf}

*** Keywords ***
the user should see a validation error
    [Arguments]    ${ERROR1}
    Mouse Out    id=projectStartDate_year
    Focus    jQuery=button:contains("Save")
    sleep    300ms
    Then the user should see an error    ${ERROR1}

status of the start date should be Yes
    Element Should Contain    id=start-date-status    Yes

the duration should be visible
    Element Should Contain    xpath=//*[@id="content"]/form/fieldset/div/p[5]/strong    3 months

the user shouldn't be able to edit the day field as all projects start on the first of the month
    the user should see the element      css=.day [readonly]