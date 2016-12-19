*** settings ***
Documentation     INFUND-6604 As a member of the competitions team I can view the Invite assessors dashboard so that I can find and invite assessors to the competition
...
...               INFUND-6602 As a member of the competitions team I can navigate to the dashboard of an 'In assessment' competition so that I can see information and further actions for the competition
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The User navigates to the Invite Dashboard
    [Documentation]    INFUND-6602 INFUND-6604
    [Tags]    Pending
    #TODO Pending Due to Infund 6985
    Given The user clicks the button/link    link=${IN_ASSESSMENT_COMPETITION_NAME}
    When The user clicks the button/link    jQuery=.button:contains("Invite assessors")
    Then The user should see the element    link=Find
    And The user should see the element    link=Invite
    And The user should see the element    link=Overview
