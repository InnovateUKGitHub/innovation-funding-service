*** Settings ***
Documentation     INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       Log in as user    email=paul.plum@gmail.com    password=Passw0rd
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
When the deadline has passed the assessment should not be visible
    [Documentation]    INFUND-1188
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    When The assessment deadline for the ${IN_ASSESSMENT_COMPETITION_NAME} changes to the past
    And the user reloads the page
    Then The user should not see the element    link=Park living
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2068-01-28 12:00:00' WHERE `competition_id`='${IN_ASSESSMENT_COMPETITION}' and type = 'ASSESSOR_DEADLINE';

*** Keywords ***
