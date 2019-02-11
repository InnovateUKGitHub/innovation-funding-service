*** Settings ***
Documentation    INFUND-5432 As an assessor I want to receive an alert to complete my profile when I log into my dashboard so that I can ensure that it is complete.
Suite Setup      Custom suite setup
Suite Teardown   Custom suite teardown
Force Tags       Assessor
Resource         ../../../resources/defaultResources.robot
Resource         ../../07__Assessor/Assessor_Commons.robot

*** Test Cases ***
Assessment should not be visible when the deadline has passed
    [Documentation]  INFUND-1188
    [Tags]  MySQL
    Given update milestone to yesterday         ${IN_ASSESSMENT_COMPETITION}  ASSESSOR_DEADLINE
    Then The user should not see the element    link=Park living
    [Teardown]  Reset competition's milestone   ${assessorDeadline}

*** Keywords ***
Reset competition's milestone
    [Arguments]  ${date}
    Execute sql string  UPDATE `${database_name}`.`milestone` SET `DATE`='${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_DB}' WHERE `competition_id`='${IN_ASSESSMENT_COMPETITION}' and `type`='ASSESSOR_DEADLINE';

Custom suite setup
    The user logs-in in new browser  &{assessor_credentials}
    Connect To Database   @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database