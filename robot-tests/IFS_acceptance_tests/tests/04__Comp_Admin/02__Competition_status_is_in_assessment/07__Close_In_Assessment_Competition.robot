*** settings ***
Documentation     INFUND-6459 As a member of the competitions team I can select 'Close assessment' in an In assessment competition so that the competition is moved to state 'Out of assessment'
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    Run Keywords    Connect to Database    @{database}
...               AND    execute sql string    UPDATE `ifs`.`milestone` SET `DATE`=NULL WHERE `id`='124';
...               AND    the user closes the browser    #Changed the status of the competition to "In Assessment" for the rest of the tests
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The Comp admin closes the competition In Assessment
    [Documentation]    INFUND-6459
    When The user clicks the button/link    link=Sustainable living models for the future
    And The user clicks the button/link    jQuery=.button:contains("Close assessment")
    Then The user should be redirected to the correct page    ${Comp_admin_all_competitions_page}
    And The user should see the text in the element    css=section:nth-child(6)    Sustainable living models for the future

Assessors shouldn't see the closed competition
    [Documentation]    INFUND-6459
    [Setup]    Log in as a different user    &{assessor2_credentials}
    Then The user should not see the element    link=Sustainable living models for the future

