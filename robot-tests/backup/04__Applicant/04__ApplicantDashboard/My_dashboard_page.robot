*** Settings ***
Documentation     INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
...
...               INFUND-8614 Application dashboard - hours left
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***
Milestone date for application in progress is visible
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]
    When The user navigates to the page  ${DASHBOARD_URL}
    Then the user should see the date for submission of application

Number of days remaining until submission should be correct
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]
    The days remaining should be correct (Applicant's dashboard)  ${openCompetitionBusinessRTOCloseDate}  Robot test application

Hours remaining should show the last 24hours
    [Documentation]    INFUND-8614
    [Tags]    MySQL
    [Setup]    Custom setup
    When the user reloads the page
    Then the user should see the text in the page    hours left
    [Teardown]    Run Keywords    Connect to Database    @{database}
    ...    AND    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${openCompetitionRTOCloseDate} 00:00:00' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

*** Keywords ***
Custom setup
    ${TIME}=    Get Current Date    UTC    + 3 hours    exclude_millis=true    # This line gets the current date/time and adds 3 hours
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${TIME}' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

the user should see the date for submission of application
    the user should see the element  jQuery=li:contains("Robot test application") .status:contains("days left"):contains("% complete")