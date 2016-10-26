*** Settings ***
Documentation     INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-3723 As an Assessor looking at my competition assessment dashboard I can see details for the competition, so that I am able to reference key information as I want.
Suite Setup       Log in as user    email=paul.plum@gmail.com    password=Passw0rd
Suite Teardown    TestTeardown User closes the browser
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot

*** Variables ***
@{database}       pymysql    ${database_name}    ${database_user}    ${database_password}    ${database_host}    ${database_port}

*** Test Cases ***
Competition link should navigate to the applications
    [Documentation]    INFUND-3716
    [Tags]    HappyPath
    [Setup]
    When The user clicks the button/link    link=Juggling Craziness
    Then The user should see the text in the page    Applications for Assessment

Calculation of the applications for assessment should be correct
    Then the total calculation in dashboard should be correct    Applications for Assessment    //div/form/ul/li

Details of the competition are visible
    [Documentation]    INFUND-3723
    Then the user should see the text in the page    Competition
    And the user should see the text in the page    Accept applications deadline
    And the user should see the text in the page    Submit applications deadline
    And the user should see the text in the page    Tuesday 12 January 2016
    And the user should see the text in the page    Saturday 28 January 2017

When the deadline has passed the assessment should not be visible
    [Documentation]    INFUND-1188
    [Tags]    MySQL    Pending
    #TODO We need to adjust this when the \ INFUND-1186 is ready
    When The assessment deadline for the Juggling Craziness changes to the past
    Then The user should not see the element    link=Juggling is fun
    [Teardown]    execute sql string    UPDATE `ifs`.`milestone` SET `DATE`='2016-12-31 00:00:00' WHERE `id`='21';

*** Keywords ***
