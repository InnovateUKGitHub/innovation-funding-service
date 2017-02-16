*** Settings ***
Documentation     INFUND-7734 Competition Management: Assign to application dashboard in Closed competition
...
...               INFUND-7729 Competition management: Allocate application dashboard on Closed competition
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin    Assessor
Resource          ../../resources/defaultResources.robot

*** Test Cases ***
Allocate Applications
    [Documentation]    INFUND-7042
    ...    INFUND-7729
    Given The user clicks the button/link    link=${CLOSED_COMPETITION_NAME}
    And the user clicks the button/Link    jQuery=a:contains("Assessor management - assignment to applications")
    When the user clicks the button/Link    jQuery=tr:contains(Neural Industries) .no-margin
    Then The user should see the text in the page    00000012: Machine learning for transport infrastructure
    [Teardown]    The user clicks the button/link    link=Allocate applications
