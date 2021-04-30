*** Settings ***
Documentation     INFUND-7963: Create Non-IFS tab in 'Competition dashboard' for adding non-IFS competitions to front door search
...
...               INFUND-7964: Create Non-IFS 'Competition details page' for adding non-IFS competitions to front door
...
...               INFUND-7965: Update 'Public content' for adding non-IFS competitions to front door
...
...               INFUND-8554 As a member of the Competition team I want to be able to publish a "Registration Closes" date in non-IFS public content...
...
...               IFS-1117: As a comp exec I am able to set Application milestones in Non-IFS competition details (Initial view)
...
...               IFS-5945: Pagination in Project Setup
Suite Setup       Connect to Database  @{database}
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot

*** Test Cases ***
Create new non-IFS competition by proj Finance
    [Documentation]    INFUND-7963 INFUND-7964
    [Tags]
    Given The user logs-in in new browser   &{internal_finance_credentials}
    When the user navigates to the Non IFS competitions tab
    And the user clicks the button/link     link = Create non-IFS competition
    Then the user should see the element    jQuery = h1:contains("Non-IFS competition details")
    When the user clicks the button/link    link = Back to all competitions
    And the user navigates to the Non IFS competitions tab
    Then the user should see the element    link = No competition title defined

Validation errors on non-IFS competition details
    [Documentation]    INFUND-7964
    [Tags]
    Given log in as a different user                       &{Comp_admin1_credentials}
    And the user navigates to the Non IFS competitions tab
    When the user clicks the button/link                   link = No competition title defined
    Then the user should see the dropdown option selected  12:00 pm  id=closeDate-time
    When the user clicks the button/link                   jQuery = button:contains("Save and continue")
    Then the user should see a field and summary error     Please enter a title.
    And the user should see a field and summary error      Please enter a competition URL.
    And the user should see a field and summary error      Enter a valid funding type.
    And the user should see a field and summary error      ${enter_a_valid_date}
    And the user should see a summary error                ${enter_a_valid_date}
    When the user fills out the competition title and url
    And the user selects the radio button                  fundingType  GRANT
    Then the user should not see the element               jQuery = .govuk-error-message:contains("Please enter a competition URL.")
    And the user should not see the element                jQuery = .govuk-error-message:contains("Please enter a title.")
    And the user should not see the element                jQuery = .govuk-error-message:contains("Enter a valid funding type.")
    When the user fills out the non-IFS details
    Then the user should not see the element               jQuery = .govuk-error-message   #Valid dates in milestones
    And the user clicks the button/link                    jQuery = button:contains("Save and continue")

Submit non-IFS competition details
    [Documentation]    INFUND-7964
    [Setup]  log in as a different user     &{Comp_admin1_credentials}
    Given the user navigates to the Non IFS competitions tab
    And the user clicks the button/link     link = Test non-IFS competition
    When the user fills out the competition title and url
    And the user selects the radio button   fundingType  GRANT
    Then the user fills out the non-IFS details
    When the user clicks the button/link    jQuery = button:contains("Save and continue")
    Then the user should see the element    jQuery = h1:contains("Public content")

Non-IFS public content
    [Documentation]    INFUND-7965
    Given the user should see the element      jQuery = h1:contains("Public content")
    When the user fills in the Public content and publishes  Non-IFS
    Then the user should see the element       jQuery = small:contains("Last published")
    And the user should not see the element    jQuery = button:contains("Publish content")
    Then the user clicks the button/link       link = Return to non-IFS competition details
    And the user clicks the button/link        jQuery = button:contains("Save and continue")

Internal user can see the Non-IFS comp and its brief information
    [Documentation]  INFUND-7963 INFUND-7964
    Given the user navigates to the Non IFS competitions tab
    Then the user should see the element    jQuery = div:contains("Test non-IFS competition") ~ *:contains("Assembly / disassembly / joining")
    And the user should see the element     jQuery = div:contains("Test non-IFS competition") ~ *:contains("Last published")

Internal user is able to delete a Non-IFS comp
    [Documentation]  IFS-5945
    Given the internal user deletes a Non-IFS competition
    When the user navigates to the Non IFS competitions tab
    Then the user should not see the element   link = ${Non_Ifs_Comp}
    [Teardown]  Logout as user

Guest user can apply to a Non-IFS competition at the FrontDoor
    [Documentation]    INFUND-7965
    Given the user navigates to the page                   ${frontDoor}
    And the user enters text to a text field               id = keywords    search
    When the user clicks the button/link                   jQuery = button:contains("Update results")
    And get competition id and set open date to yesterday  Test non-IFS competition
    And the user clicks the button/link                    link = Test non-IFS competition
    Then The user should see the element                   link = Register and apply online

Guest can see the Dates tab
    [Documentation]  INFUND-8554  IFS-1117
    [Tags]
    When the user clicks the button/link  link = Dates
    Then the user should see the element  jQuery = #dates dd:contains("Competition opens")
    And the user should see the element   jQuery = #dates dd:contains("Registration closes")
    And the user should see the element   jQuery = #dates dd:contains("Competition closes")
    And the user should see the element   jQuery = #dates dd:contains("Applicants notified")

*** Keywords ***
the internal user deletes a Non-IFS competition
    the user clicks the button/link     link = ${Non_Ifs_Comp}
    the user clicks the button/link     link = Delete competition
    the user clicks the button/link     jQuery = button:contains("Delete")

the user fills out the competition title and url
    When the user enters text to a text field   id = title    Test non-IFS competition
    And the user enters text to a text field    id = url    http://www.google.co.uk

the user fills out the non-IFS details
    And the user selects the option from the drop-down menu    Materials and manufacturing  id = innovationSectorCategoryId
    And the user selects the option from the drop-down menu    Assembly / disassembly / joining  id = innovationAreaCategoryId
    When the user enters text to a text field    id = openDate-day    1
    And the user enters text to a text field     id = openDate-month    10
    And the user enters text to a text field     id = openDate-year    2017
    the user enters text to a text field         id = registrationCloseDate-day  15
    the user enters text to a text field         id = registrationCloseDate-month  2
    the user enters text to a text field         id = registrationCloseDate-year  2020
    the user selects the option from the drop-down menu    6:00 pm  id = registrationCloseDate-time
    And the user enters text to a text field     id = closeDate-day    1
    And the user enters text to a text field     id = closeDate-month    3
    And the user enters text to a text field     id = closeDate-year    2024
    And the user selects the option from the drop-down menu  4:00 pm  id = closeDate-time
    And the user should see the text in the element   css = #applicantNotifiedDate  Applicants notified (optional)
    And the user enters text to a text field     id = applicantNotifiedDate-day    1
    And the user enters text to a text field     id = applicantNotifiedDate-month    5
    And the user enters text to a text field     id = applicantNotifiedDate-year    2024

the user navigates to the Non IFS competitions tab
    the user navigates to the page     ${CA_Live}
    the user clicks the button/link    jQuery = a:contains(Non-IFS)
    # We have used the JQuery selector for the link because the title will change according to the competitions number

Custom suite teardown
    The user closes the browser
    Disconnect from database

