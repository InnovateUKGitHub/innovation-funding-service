*** Settings ***
Documentation     IFS-747 As a comp exec I am able to select a Competition type of Generic in Competition setup
Suite Setup       The user logs-in in new browser  &{lead_applicant_credentials}
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
User applies to generic competition
    [Documentation]    IFS-747
    [Tags]  HappyPath
    [Setup]    the user navigates to the page    ${frontDoor}
    Given navigate to next page if not found
    And the user clicks the button/link    link=Generic innovation
    When the user clicks the button/link    link=Start new application
    And the user clicks the button/link  jQuery=a:contains("Begin application")

User can edit six assesed questions
    [Documentation]    IFS-747
    [Tags]  HappyPath
    Given the user should not see the element  a:contains("7.")  # This comp has only 6 questions
    When the user clicks the button/link  link=6. Innovation
    Then the user should see the element  jQuery=button:contains("Mark as complete")

*** Keywords ***
navigate to next page if not found
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible  link=Generic innovation
    Run Keyword If    '${status}' == 'FAIL'    the user clicks the button/link  jQuery=a:contains("Next")