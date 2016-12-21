*** settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard...
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition...
...
...               INFUND-6392 As a member of the competitions team, I can add/remove an assessor...
...
...               INFUND-6412 As a member of the competitions team, I can view the invite list before sending invites...
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The User can Add and Remove Assessors
    [Documentation]    INFUND-6602 INFUND-6604 INFUND-6392 INFUND-6412
    [Tags]
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    And The user clicks the button/link    jQuery=.button:contains("Invite assessors")
    And The user should see the element    link=Overview
    When The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Add)
    And The user clicks the button/link    link=Invite
    Then The user should see the text in the page    will.smith@gmail.com
    And The user should see the text in the page    Will Smith
    And The user should see the element    jQuery=tr:nth-child(1) .no
    When The user clicks the button/link    link=Find
    And The user clicks the button/link    jQuery=tr:nth-child(1) .button:contains(Remove)
    And The user clicks the button/link    link=Invite
    Then The user should not see the text in the page    will.smith@gmail.com
