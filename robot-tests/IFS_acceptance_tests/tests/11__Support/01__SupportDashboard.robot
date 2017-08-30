*** Settings ***
Documentation     IFS-188 Stakeholder views – Support team
Suite Setup       The user logs-in in new browser  &{support_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        Support  CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Test Cases ***
Support dashboard
    [Documentation]    IFS-188
    Given the user navigates to the page  ${CA_Live}
    Then the user should see all live competitions

Competition links go directly to all applications page
    [Documentation]    IFS-188
    When The user clicks the button/link    link=${openCompetitionRTO_name}
    Then the user should see the element    jQuery=span:contains("${competition_ids['${openCompetitionRTO_name}']}: ${openCompetitionRTO_name}")
    And the user should see the element     jQuery=h1:contains("All applications")
    And the user should see the element     css=#application-list

Back navigation is to dashboard
    [Documentation]    IFS-188
    Given the user clicks the button/link    jQuery=.link-back:contains("Dashboard")
    Then the user should see the element    jQuery=h1:contains("All competitions")