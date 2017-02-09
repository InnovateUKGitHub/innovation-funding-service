*** Settings ***
Documentation     INFUND-6914 Create 'Public content' menu page for "Front Door" setup pages

...               INFUND-6916 As a Competitions team member I want to create a Public content summary page

Suite Setup       Custom suite setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Variables ***
${upcomming_competitions_dashboard}    ${server}/management/dashboard/upcoming
${public_content_competition_name}     Public content competition

*** Test Cases ***
User can view the public content
    [Documentation]    INFUND-6914
    [Tags]    HappyPath
    Given the user navigates to the page      ${upcomming_competitions_dashboard}
    And the user clicks the button/link      link=${public_content_competition_name}
    Given the user clicks the button/link    link=Public content
    Then the user should see the element     link=Competition information and search
    And the user should see the element      link=Summary
    And the user should see the element      link=Eligibility
    And the user should see the element      link=Scope
    And the user should see the element      link=Dates
    And the user should see the element      link=How to apply
    And the user should see the element      link=Supporting information

Summary: Contain the correct options
        [Documentation]    INFUND-6916
        [Tags]    HappyPath
        Given the user clicks the button/link    link=Summary
        And the user should see the text in the page    Text entered into this section will appear in the summary tab
        Then the user should see the element    css=.editor
        and the user should see the element    jQuery=label:contains("Grant")
        And the user should see the element    jQuery=label:contains("Procurement")
        And the user should see the text in the page    Project size
        And the user should see the element    id=project-size
        And the user should see the element     jQuery=.buttonlink:contains("+ add new section")

Summary: User enters valid values and saves
    [Documentation]    INFUND-6916
    [Tags]    HappyPath  Pending
    Given The user clicks the button/link           link=Summary
    When the user enters valid data in the summary details
    and  the user clicks the button/link            jQuery=.button:contains("Save and return")
    Then the user should see the text in the page   John Doe
    And the user should see the text in the page    Ian Cooper
    And the user should see the element             jQuery=.button:contains("Edit")

*** Keywords ***

Custom suite setup
    Guest user log-in    &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    User creates a new competition   ${public_content_competition_name}

User creates a new competition
    [Arguments]    ${competition_name}
    Given the user navigates to the page      ${upcomming_competitions_dashboard}
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    And The user clicks the button/link           link=Initial details
    Then the user enters text to a text field                css=#title  ${competition_name}
    And the user selects the option from the drop-down menu  Sector  id=competitionTypeId
    And the user selects the option from the drop-down menu   Emerging and enabling technologies  id=innovationSectorCategoryId
    And the user selects the option from the drop-down menu   Creative economy  id=innovationAreaCategoryId-0
    And the user enters text to a text field    id=openingDateDay    01
    And the user enters text to a text field    Id=openingDateMonth    12
    And the user enters text to a text field    id=openingDateYear  ${nextyear}
    And the user selects the option from the drop-down menu    Ian Cooper    id=leadTechnologistUserId
    And the user selects the option from the drop-down menu    John Doe   id=executiveUserId
    When the user clicks the button/link            jQuery=.button:contains("Done")

the user enters valid data in the summary details
    When the user selects the option from the drop-down menu  Sector  id=competitionTypeId
    And the user enters text to a text field    id=description    Summary Description
    And the user enters text to a text field    id=project-size   10

