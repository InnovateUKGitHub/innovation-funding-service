*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
Suite Setup       The user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot

*** Test Cases ***
User can edit six assesed questions
    [Documentation]    IFS-747
    [Tags]  HappyPath
    [Setup]  logged in user applies to competition  ${openGenericCompetition}
    Given the user should not see the element  a:contains("7.")  # This comp has only 1 question
#    When the user clicks the button/link  link=6. Innovation
#    Then the user should see the element  jQuery=button:contains("Mark as complete")
# TODO IFS-2303
