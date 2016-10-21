*** Settings ***
Documentation     INFUND-544: As an applicant I want the ‘Application summary’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do
Suite Setup       log in and create new application if there is not one already
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***

*** Test Cases ***
Status is updated after marking as complete
    [Documentation]    INFUND-544
    [Tags]    HappyPath
    Given the user navigates to the summary page of the Robot test application
    And the user should not see the text in the page    Complete
    And the user navigates to the overview page of the Robot test application
    When the user clicks the button/link    link=4. Economic benefit
    And the applicant adds some content and marks this section as complete
    And the user navigates to the summary page of the Robot test application
    Then the user should see the text in the page    Complete

Status is updated after editing a section
    [Documentation]    INFUND-544
    [Tags]
    Given the user navigates to the summary page of the Robot test application
    And the user should see the text in the page    Complete
    And the user navigates to the overview page of the Robot test application
    When the user clicks the button/link    link=4. Economic benefit
    And the applicant edits the "economic benefit" question
    And the user navigates to the summary page of the Robot test application
    Then the user should not see the text in the page    Complete

*** Keywords ***
