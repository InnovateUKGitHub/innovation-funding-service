*** Settings ***
Suite Setup       Log in as user    email=paul.plum@gmail.com    password=Passw0rd
Force Tags
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
When the deadline has passed the assessment should not be visible
    [Documentation]    INFUND-1188
    [Tags]    MySQL
    [Setup]    Connect to Database    @{database}
    When The assessment deadline for the Juggling Craziness changes to the past
    And the user reloads the page
    Then The user should not see the element    link=Juggling is fun
    [Teardown]    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='2016-12-31 00:00:00' WHERE `id`='35';
