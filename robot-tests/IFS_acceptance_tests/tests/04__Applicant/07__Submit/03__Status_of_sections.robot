*** Settings ***
Documentation     INFUND-544: As an applicant I want the ‘Application review and submit’ page to show me complete and incomplete sections, so that I can easy judge how much of the application is left to do
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot

*** Variables ***

*** Test Cases ***
Status is updated after marking as complete
    [Documentation]    INFUND-544
    [Tags]
    Given the user navigates to the review and submit page of the Robot test application
    And the user navigates to the overview page of the Robot test application
    When the user clicks the button/link    link = 4. Economic benefit
    And the applicant adds some content and marks this section as complete
    And the user navigates to the review and submit page of the Robot test application
    Then the user should see the element    jQuery = .section-complete + button:contains("Economic benefit")

Status is updated after editing a section
    [Documentation]    INFUND-544
    [Tags]
    Given the user navigates to the review and submit page of the Robot test application
    And the user navigates to the overview page of the Robot test application
    When the user clicks the button/link        link = 4. Economic benefit
    And the applicant edits the "economic benefit" question
    And the user navigates to the review and submit page of the Robot test application
    Then the user should not see the element    jQuery = .section-complete + button:contains("Economic benefit")

*** Keywords ***
Custom suite setup
    log in and create new application if there is not one already  Robot test application
    Connect to database  @{database}

Custom suite teardown
    Disconnect from database
    The user closes the browser