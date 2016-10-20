*** Settings ***
Documentation     INFUND-539 - As an applicant I want the ‘Application details’ drop down on the ‘Application overview’ page to show a green tick when I’ve marked it as complete, so that I know what I’ve done
...
...               INFUND-1733 As an applicant I want to see if the 'Your Finance' section is marked as complete in the overview page
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Default Tags
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Green check shows after marking a question as complete
    [Documentation]    INFUND-539
    [Tags]    HappyPath
    [Setup]
    Given the user makes sure that the finances section is not marked as complete
    When the user navigates to the overview page of the Robot test application
    And none of the sections are marked as complete
    And the user clicks the button/link    link=4. Economic benefit
    And the applicant adds some content and marks this section as complete
    And The user navigates to the overview page of the Robot test application
    Then the applicant can see that the economics benefit section is marked as complete

Blue flag shows after marking a question as incomplete
    [Documentation]    INFUND-539
    [Tags]    HappyPath
    Given The user navigates to the overview page of the Robot test application
    And the applicant can see that the economics benefit section is marked as complete
    And the user clicks the button/link    link=4. Economic benefit
    And the applicant edits the "economic benefit" question
    And The user navigates to the overview page of the Robot test application
    Then none of the sections are marked as complete

Green check shows when finances are marked as complete
    [Documentation]    INFUND-1733
    [Tags]
    [Setup]    Run keywords    Make the finances ready for mark as complete
    When the user marks the finances as complete
    Then the user should see the element    jQuery=#section-6 .section:nth-child(1) img[src*="/images/field/field-done-right"]
    [Teardown]    Run keywords    Applicant navigates to the finances of the Robot application
    ...    AND    Run Keyword And Ignore Error    Focus    jQuery=button:contains("Edit")
    ...    AND    Run Keyword And Ignore Error    the user clicks the button/link    jQuery=button:contains("Edit")

*** Keywords ***
the user marks the finance section as not complete
    The user navigates to the overview page of the Robot test application
    the user clicks the button/link    link=Your finances
    the user clicks the button/link    jQuery=button:contains("Edit")

none of the sections are marked as complete
    the user should not see the element    css=.complete

the applicant can see that the economics benefit section is marked as complete
    the user should see the element    jQuery=#section-2 .section:nth-child(4) img[src*="/images/field/field-done-right"]

the user makes sure that the finances section is not marked as complete
    the user navigates to the overview page of the Robot test application
    the user clicks the button/link    link=Your finances
    run keyword and ignore error    the user clicks the button/link    jQuery=button:contains("Edit")
