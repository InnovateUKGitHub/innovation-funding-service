*** Settings ***
Documentation     INFUND-37 As an applicant and I am on the application overview, I can view the status of this application, so I know what actions I need to take
...
...               INFUND-8614 Application dashboard - hours left
Suite Setup       log in and create new application if there is not one already  Robot test application
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Milestone date for application in progress is visible
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]  HappyPath
    When The user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    Then the user should see the date for submission of application

Number of days remaining until submission should be correct
    [Documentation]  INFUND-37 INFUND-5485
    [Tags]  HappyPath
    The days remaining should be correct (Applicant's dashboard)  ${openCompetitionBusinessRTOCloseDate}  Robot test application

Hours remaining should show the last 24hours
    [Documentation]    INFUND-8614
    [Tags]  HappyPath
    [Setup]    Custom setup
    When the user reloads the page
    Then the user should see the element    jQuery = .status-msg:contains("hours left")
    [Teardown]     execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${openCompetitionRTOCloseDate} 00:00:00' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

Collaborator is able to remove an application
    [Documentation]  IFS-7088
    [Setup]  Log in as a different user       &{collaborator1_credentials}
    Given the user clicks the button/link     jQuery = button[name="hide-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
    When the user clicks the button/link      jQuery = li:contains("innovative") button:contains("Remove application")
    Then the user should not see the element  jQuery = li:contains("innovative")

Lead still sees application
    [Documentation]  IFS-7088
    Given Log in as a different user       &{lead_applicant_credentials}
    Then the user should see the element   jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")

Lead is able to delete an application
    [Documentation]  IFS-7088
    Given the user clicks the button/link     jQuery = button[name="delete-application-${CLOSED_COMPETITION_APPLICATION_NAME_NUMBER}"]
    When the user clicks the button/link      jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}") button:contains("Delete application")
    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")

Application is no longer visible to anyone
    [Documentation]  IFS-7088
    Given log in as a different user          &{collaborator2_credentials}
    Then the user should not see the element  jQuery = li:contains("${CLOSED_COMPETITION_APPLICATION_NAME}")
    And the user reads his email              ${collaborator2_credentials["email"]}  Successful deletion of application  All the application information has been deleted

*** Keywords ***
Custom setup
    ${TIME}=    Get Current Date    UTC    + 3 hours    exclude_millis=true    # This line gets the current date/time and adds 3 hours
    Connect to Database    @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${TIME}' WHERE `competition_id`='${openCompetitionRTO}' and type IN ('SUBMISSION_DATE');

the user should see the date for submission of application
    the user should see the element  jQuery=li:contains("Robot test application") .status:contains("days left"):contains("% complete")

Custom suite teardown
    The user closes the browser
    Disconnect from database