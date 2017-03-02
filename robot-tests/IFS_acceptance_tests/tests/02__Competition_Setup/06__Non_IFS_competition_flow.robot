*** Settings ***
Documentation     INFUND-7963: Create Non-IFS tab in 'Competition dashboard' for adding non-IFS competitions to front door search
...
...               INFUND-7964: Create Non-IFS 'Competition details page' for adding non-IFS competitions to front door
...
...               INFUND-7965: Update 'Public content' for adding non-IFS competitions to front door
Suite Setup       Guest user log-in    &{Comp_admin1_credentials}
Suite Teardown    the user closes the browser
Force Tags        CompAdmin    Failing
Resource          ../../resources/defaultResources.robot
Resource          CompAdmin_Commons.robot

*** Test Cases ***
Create new non-IFS competition
    [Documentation]    INFUND-7963 INFUND-7964
    When the user clicks the button/link    jQuery=a:contains(Non-IFS)    # We have used the JQuery selector for the link because the title will change according to the competitions number
    And the user clicks the button/link     link=Create non-IFS competition
    Then the user should see the text in the page       Non-IFS competition details
    When the user clicks the button/link    link=Back to all competitions
    And the user clicks the button/link    jQuery=a:contains(Non-IFS)
    Then the user should see the element  link=No competition title defined

Validation errors on non-IFS competition details
    [Documentation]    INFUND-7964
    When the user clicks the button/link    link=No competition title defined
    Then the user should see the dropdown option selected  12:00 pm  id=closeDate-time
    When the user clicks the button/link     jQuery=button:contains(Save and continue)
    Then the user should see a field and summary error      Please enter a title.
    And the user should see a field and summary error      Please enter a competition URL.
    When the user fills out the competition title and url
    And the user clicks the button/link     jQuery=button:contains(Save and continue)
    Then the user should see a summary error      The date you entered is invalid.

Submit non-IFS competition details
    [Documentation]    INFUND-7964
    When the user fills out the non-IFS details
    And the user clicks the button/link     jQuery=button:contains(Save and continue)
    Then The user should see the text in the page   Public content

Non-IFS public content
    [Documentation]    INFUND-7965
    Then The user should see the text in the page   Public content


*** Keywords ***
the user fills out the competition title and url
    When the user enters text to a text field   id=title    Test non-IFS competition
    And the user enters text to a text field   id=url    http://www.google.co.uk

the user fills out the non-IFS details
    And the user selects the option from the drop-down menu  Materials and manufacturing  id=innovationSector
    And the user selects the option from the drop-down menu  Manufacturing Readiness  id=innovationArea
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


