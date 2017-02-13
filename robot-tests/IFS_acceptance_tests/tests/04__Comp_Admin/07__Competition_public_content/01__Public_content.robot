*** Settings ***
Documentation     INFUND-6914 Create 'Public content' menu page for "Front Door" setup pages

...               INFUND-6916 As a Competitions team member I want to create a Public content summary page

Suite Setup       Custom suite setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../CompAdmin_Commons.robot

*** Variables ***
${upcoming_competitions_dashboard}    ${server}/management/dashboard/upcoming
${public_content_competition_name}     Public content competition

*** Test Cases ***
User can view the public content
    [Documentation]    INFUND-6914
    [Tags]    HappyPath
    Given the user navigates to the page     ${upcoming_competitions_dashboard}
    And the user clicks the button/link      link=${public_content_competition_name}
    Given the user clicks the button/link    link=Public content
    Then the user should see the element     link=Competition information and search
    And the user should see the element      link=Summary
    And the user should see the element      link=Eligibility
    And the user should see the element      link=Scope
    And the user should see the element      link=Dates
    And the user should see the element      link=How to apply
    And the user should see the element      link=Supporting information

Summary: Contains the correct options
    [Documentation]    INFUND-6916
    [Tags]    HappyPath
    Given the user clicks the button/link           link=Summary
    And the user should see the text in the page    Text entered into this section will appear in the summary tab
    Then the user should see the element            css=.editor
    and the user should see the element             jQuery=label:contains("Grant")
    And the user should see the element             jQuery=label:contains("Procurement")
    And the user should see the text in the page    Project size
    And the user should see the element             id=project-size
    And the user should see the element             jQuery=.buttonlink:contains("+ add new section")

Summary: User enters invalid values and saves
    [Documentation]    INFUND-6916
    [Tags]    HappyPath
    When the user clicks the button/link                jQuery=.button:contains("Save and return")
    Then the user should see a summary error            Please enter a funding type.
    And the user should see a summary error             Please enter a project size.
    And the user should see a summary error             Please enter a competition description.

Summary: User enters valid values and saves
    [Documentation]    INFUND-6916
    [Tags]    HappyPath
    When the user enters valid data in the summary details
    Then the user should be redirected to the correct page    ${public_content_overview}
    And the user should see the element      link=Summary
    And the user should see the element      link=Eligibility

Summary: Contains the correct values when viewed
    [Documentation]    INFUND-6916
    [Tags]    HappyPath
    [Setup]    the user navigates to the page           ${public_content_overview}
    When the user clicks the button/link                link=Summary
    Then the user should see the text in the page       Text entered into this section will appear in the summary tab
    And the user should see the text in the page        Grant
    And the user should see the text in the page        10
    And the user should see the element                 jQuery=.button:contains("Return to public content")
    And the user should see the element                 jQuery=.button-secondary:contains("Edit")

Summary: Add, remove sections and submit
    [Documentation]    INFUND-6916
    [Setup]    the user navigates to the page    ${public_content_overview}
    When the user clicks the button/link                         link=Summary
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         jQuery=li:nth-of-type(4) img.complete

Eligibility: Add, remove sections and submit
    [Documentation]    INFUND-6917 INFUND-7602
    [Setup]    the user navigates to the page    ${public_content_overview}
    When the user clicks the button/link                         link=Eligibility
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         jQuery=li:nth-of-type(3) img.complete

Scope: Add, remove sections and submit
    [Documentation]    INFUND-6918 INFUND-7602
    [Setup]    the user navigates to the page    ${public_content_overview}
    When the user clicks the button/link                         link=Scope
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         jQuery=li:nth-of-type(4) img.complete

How to apply: Add, remove sections and submit
    [Documentation]    INFUND-6920 INFUND-7602
    [Setup]    the user navigates to the page    ${public_content_overview}
    When the user clicks the button/link                         link=How to apply
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         jQuery=li:nth-of-type(6) img.complete

Supporting information: Add, remove sections and submit
    [Documentation]    INFUND-6921 INFUND-7602
    [Setup]    the user navigates to the page    ${public_content_overview}
    When the user clicks the button/link                         link=Supporting information
    Then the user can add and remove multiple content groups
    When the user clicks the button/link                        jQuery=button:contains("Save and return")
    And the user should see the element                         jQuery=li:nth-of-type(7) img.complete


*** Keywords ***

Custom suite setup
    Connect to Database  @{database}
    Guest user log-in    &{Comp_admin1_credentials}
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}
    User creates a new competition   ${public_content_competition_name}
    ${competitionId}=  get comp id from comp title  ${public_content_competition_name}
    ${public_content_overview}=    catenate    ${server}/management/competition/setup/public-content/    ${competitionId}
    Set suite variable  ${public_content_overview}

User creates a new competition
    [Arguments]    ${competition_name}
    Given the user navigates to the page      ${upcoming_competitions_dashboard}
    When the user clicks the button/link    jQuery=.button:contains("Create competition")
    When the user fills in the CS Initial details      ${competition_name}  01  02  ${nextyear}

the user enters valid data in the summary details
    The user enters text to a text field   css=.editor    Summary Description
    the user selects the radio button       fundingType    Grant
    the user enters text to a text field    id=project-size   10
    the user clicks the button/link         jQuery=.button:contains("Save and return")

the user can add and remove multiple content groups
    When the user clicks the button/link        jQuery=button:contains("Save and return")
    Then the user should see a summary error    Please enter a heading.
    And the user should see a summary error     Please enter content.
    When the user enters text to a text field   id=heading-0    Heading 1
    And the user enters text to a text field    jQuery=.editor:eq(0)     Content 1
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-1    Heading 2
    And the user enters text to a text field    jQuery=.editor:eq(1)     Content 2
    And the user clicks the button/link         jQuery=button:contains("+ add new section")
    And the user enters text to a text field    id=heading-2    Heading 3
    And the user enters text to a text field    jQuery=.editor:eq(2)     Content 3
    And the user clicks the button/link         jQuery=button:contains("Remove section"):eq(1)
    Then the user should not see the element    id=heading-2
    And the user should not see the element     jQuery=.editor:eq(2)


