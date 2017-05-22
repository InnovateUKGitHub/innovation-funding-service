*** Settings ***
Documentation     IFS-188 Stakeholder views – Support team
Suite Setup       Log in as user    &{support_user_credentials}
Suite Teardown    the user closes the browser
Force Tags        support
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Support dashboard
    [Documentation]    IFS-188
    [Tags]  support
    Given the user should see the text in the page    All competitions
    Then the user should see the text in the page    Open
    And the user should see the text in the page    Closed
    And the user should see the text in the page    Panel
    And the user should see the text in the page    Inform

Competition links go directly to all applications page
    [Documentation]    IFS-188
    [Tags]  support
    When The user clicks the button/link    link=${OPEN_COMPETITION_NAME}
    Then the user should see the element    jQuery=span:contains("15: Predicting market trends programme")
    And the user should see the element    jQuery=h1:contains("All applications")
    And the user should see the text in the page    All applications
    And the user should see the text in the page    Application number
    And the user should see the text in the page    Project title
    And the user should see the text in the page    Innovation area
    And the user should see the text in the page    Lead
    And the user should see the text in the page    Status
    And the user should see the text in the page    Percentage complete

Back navigation is to dashboard
    [Documentation]    IFS-188
    [Tags]  support
    Given the user clicks the button/link    jQuery=.link-back:contains("Dashboard")
    Then the user should see the element    jQuery=h1:contains("All competitions")