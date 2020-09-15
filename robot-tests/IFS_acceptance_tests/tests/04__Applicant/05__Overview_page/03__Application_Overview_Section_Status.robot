*** Settings ***
Documentation     INFUND-539 - As an applicant I want the ‘Application details’ drop down on the ‘Application overview’ page to show a green tick when I’ve marked it as complete, so that I know what I’ve done
...
...               INFUND-1733 As an applicant I want to see if the 'Your Finance' section is marked as complete in the overview page
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Default Tags
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot

#This test suite is using the Application:  Robot test application

*** Test Cases ***
Green check shows after marking a question as complete
    [Documentation]    INFUND-539
    [Tags]
    Given the user navigates to the overview page of the Robot test application
    When the user clicks the button/link        link = 4. Economic benefit
    And the applicant adds some content and marks this section as complete
    And The user navigates to the overview page of the Robot test application
    Then the user should not see the element    jQuery = button:contains("Economic benefit") *:contains("Complete")

Blue flag shows after marking a question as incomplete
    [Documentation]    INFUND-539
    [Tags]
    Given The user navigates to the overview page of the Robot test application
    And the user should not see the element  jQuery = button:contains("Economic benefit") *:contains("Complete")
    And the user clicks the button/link      link = 4. Economic benefit
    And the applicant edits the "economic benefit" question

Green check shows when finances are marked as complete
    [Documentation]    INFUND-1733
    [Tags]
    [Setup]  The user navigates to the overview page of the Robot test application
    Given the Application details are completed
    And the user navigates to Your-finances page  Robot test application
    Then the user marks the finances as complete  Robot test application  labour costs  n/a  no


*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    Connect to database  @{database}
    log in and create new application if there is not one already  Robot test application

Custom suite teardown
    Disconnect from database
    The user closes the browser