*** Settings ***
Documentation     INFUND-6459 As a member of the competitions team I can select 'Close assessment' in an In assessment competition so that the competition is moved to state 'Out of assessment'
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition so that I can see information and further actions for the competition
...
...               IFS-1654 Reinstate application button should not be shown for ineligible applications which have reached 'previous' tab
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    Custom Suite Teardown
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
The Comp admin closes the competition In Assessment
    [Documentation]    INFUND-6459
    ...
    ...    INFUND-6602
    When The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link    jQuery=.button:contains("Close assessment")
    Then The user should see the text in the page    Panel
    And The user clicks the button/link    link=All competitions
    And The user should see the text in the element    css=section:nth-child(6)    ${IN_ASSESSMENT_COMPETITION_NAME}

Comp admin is not allowed to reinstate an application once assessment is closed
    [Documentation]    IFS-1654
    When the user navigates to the page  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/ineligible
    And the user clicks the button/link  link=${application_ids['Application with ineligible']}
    Then the user should not see the element  jQuery=a:contains("Reinstate application")

Assessors shouldn't see the closed competition
    [Documentation]    INFUND-6459
    [Setup]    Log in as a different user    &{assessor2_credentials}
    Then The user should not see the element    link=${IN_ASSESSMENT_COMPETITION_NAME}

*** Keywords ***
Custom Suite Teardown
    Connect to Database  @{database}
    execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`=NULL WHERE type='ASSESSMENT_CLOSED' AND competition_id=${competition_ids['${IN_ASSESSMENT_COMPETITION_NAME}']};
    #Changed the status of the competition to "In Assessment" for the rest of the tests
    the user closes the browser
