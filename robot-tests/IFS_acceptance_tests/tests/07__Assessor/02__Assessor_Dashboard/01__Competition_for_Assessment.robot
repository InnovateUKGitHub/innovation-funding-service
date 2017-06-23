*** Settings ***
Documentation     INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup       Guest user log-in  &{assessor_credentials}
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Assessment should not be visible when the deadline has passed
    [Documentation]    INFUND-1188
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    When The assessment deadline for the ${IN_ASSESSMENT_COMPETITION_NAME} changes to the past
    And the user reloads the page
    Then The user should not see the element    link=Park living
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2068-01-28 12:00:00' WHERE `competition_id`='${IN_ASSESSMENT_COMPETITION}' and type = 'ASSESSOR_DEADLINE';