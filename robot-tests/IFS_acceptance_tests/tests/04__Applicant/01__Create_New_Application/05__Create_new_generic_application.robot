*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
Suite Setup       The user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../10__Project_setup/PS_Common.robot

*** Test Cases ***
User applies to generic competition
    [Documentation]    IFS-747
    [Setup]    the user navigates to the page    ${frontDoor}
    Given the user clicks the button/link    link=Generic innovation
    When the user clicks the button/link    link=Start new application
    Then the user selects the radio button  create-application  true
    And the user clicks the button/link  jQuery=button:contains("Continue")
    And the user clicks the button/link  jQuery=a:contains("Begin application")

User can edit six assesed questions
    [Documentation]    IFS-747
    Given the user should not see the element  a:contains("7.")
    When the user clicks the button/link  link=6. Innovation
    Then the user should see the element  jQuery=button:contains("Mark as complete")

*** Keywords ***
