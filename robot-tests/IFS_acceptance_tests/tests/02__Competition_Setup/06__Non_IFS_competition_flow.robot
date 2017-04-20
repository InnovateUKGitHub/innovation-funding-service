*** Settings ***
Documentation     INFUND-7963: Create Non-IFS tab in 'Competition dashboard' for adding non-IFS competitions to front door search
...
...               INFUND-7964: Create Non-IFS 'Competition details page' for adding non-IFS competitions to front door
...
...               INFUND-7965: Update 'Public content' for adding non-IFS competitions to front door
...
...               INFUND-8554 As a member of the Competition team I want to be able to publish a "Registration Closes" date in non-IFS public content...
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot

*** Test Cases ***
Create new non-IFS competition by proj Finance
    [Documentation]    INFUND-7963 INFUND-7964
    [Tags]  HappyPath
    Given Guest user log-in                 &{internal_finance_credentials}
    When the user navigates to the Non IFS competitions tab
    And the user clicks the button/link     link=Create non-IFS competition
    Then the user should see the element  jQuery=h1:contains("Non-IFS competition details")
    When the user clicks the button/link    link=Back to all competitions
    And the user navigates to the Non IFS competitions tab
    Then the user should see the element  link=No competition title defined

Validation errors on non-IFS competition details
    [Documentation]    INFUND-7964
    [Tags]
    Given log in as a different user        &{Comp_admin1_credentials}
    And the user navigates to the Non IFS competitions tab
    When the user clicks the button/link    link=No competition title defined
    Then the user should see the dropdown option selected  12:00 pm  id=closeDate-time
    When the user clicks the button/link     jQuery=button:contains("Save and continue")
    Then the user should see a field and summary error  Please enter a title.
    And the user should see a field and summary error   Please enter a competition URL.
    And the user should see a field and summary error   Please enter a valid date.
    When the user fills out the competition title and url
    Then the user should not see the element  jQuery=.error-message:contains("Please enter a competition URL.")
    And the user should not see the element   jQuery=.error-message:contains("Please enter a title.")
    When the user fills out the non-IFS details
    Then the user clicks the button/link       jQuery=button:contains("Save and continue")

Submit non-IFS competition details
    [Documentation]    INFUND-7964
    [Tags]
    [Setup]  log in as a different user  &{Comp_admin1_credentials}
    Given the user navigates to the Non IFS competitions tab
    And the user clicks the button/link  link=Test non-IFS competition
    When the user fills out the competition title and url
    Then the user fills out the non-IFS details
    When the user clicks the button/link  jQuery=button:contains("Save and continue")
    Then the user should see the element  jQuery=h1:contains("Public content")

Non-IFS public content
    [Documentation]    INFUND-7965
    [Tags]
    Given the user should see the element  jQuery=h1:contains("Public content")
    When the user fills in the Public content and publishes
    Then the user clicks the button/link  link=Return to non-IFS competition details
    And the user clicks the button/link   jQuery=button:contains("Save and continue")

Internal user can see the Non-IFS comp and its brief information
    [Documentation]  INFUND-7963 INFUND-7964
    [Tags]
    Given the user navigates to the Non IFS competitions tab
    Then the user should see the element  jQuery=div:contains("Test non-IFS competition") ~ *:contains("Assembly / disassembly / joining")
    And the user should see the element   jQuery=div:contains("Test non-IFS competition") ~ *:contains("Last published")
    [Teardown]  the user can log out

Guest user can apply to a Non-IFS competition at the FrontDoor
    [Documentation]    INFUND-7965
    [Tags]    MySQL
    Given the user navigates to the page    ${frontDoor}
    When the user enters text to a text field    id=keywords    search
    And the user clicks the button/link    jQuery=button:contains("Update results")
    Given the competition is open    Test non-IFS competition
    When the user clicks the button/link    link=Test non-IFS competition
    Then The user should see the element    link=Register and apply online

Guest can see the Dates tab
    [Documentation]    INFUND-8554
    Then the user clicks the button/link    link=Dates
    And The user should see the text in the page    Registration closes

*** Keywords ***
the user fills out the competition title and url
    When the user enters text to a text field   id=title    Test non-IFS competition
    And the user enters text to a text field   id=url    http://www.google.co.uk

the user fills out the non-IFS details
    And the user selects the option from the drop-down menu  Materials and manufacturing  id=innovationSector
    And the user selects the option from the drop-down menu  Assembly / disassembly / joining  id=innovationArea
    When the user enters text to a text field   id=openDate-day    1
    And the user enters text to a text field   id=openDate-month    1
    And the user enters text to a text field   id=openDate-year    2020
    And the user enters text to a text field   id=closeDate-day    1
    And the user enters text to a text field   id=closeDate-month    3
    And the user enters text to a text field   id=closeDate-year    2020
    And the user selects the option from the drop-down menu  4:00 pm  id=closeDate-time
    And the user enters text to a text field   id=applicantNotifiedDate-day    1
    And the user enters text to a text field   id=applicantNotifiedDate-month    5
    And the user enters text to a text field   id=applicantNotifiedDate-year    2020

the user navigates to the Non IFS competitions tab
    the user navigates to the page   ${CA_Live}
    the user clicks the button/link  jQuery=a:contains(Non-IFS)
    # We have used the JQuery selector for the link because the title will change according to the competitions number

the competition is open
    [Arguments]  ${compTitle}
    Connect to Database  @{database}
    change the open date of the competition in the database to one day before  ${compTitle}