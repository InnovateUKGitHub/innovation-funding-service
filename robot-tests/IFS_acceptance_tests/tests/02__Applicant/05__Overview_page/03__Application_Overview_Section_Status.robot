*** Settings ***
Documentation     INFUND-539 - As an applicant I want the ‘Application details’ drop down on the ‘Application overview’ page to show a green tick when I’ve marked it as complete, so that I know what I’ve done
Suite Setup       log in and create new application if there is not one already
Suite Teardown    User closes the browser
Force Tags        Applicant
Default Tags
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/Application_question_edit_actions.robot
Resource          ../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
Status is updated after marking as complete
    [Documentation]    INFUND-539
    [Tags]    HappyPath
    When The user navigates to the overview page of the Robot test application
    And none of the sections are marked as complete
    And the user clicks the button/link    link=4. Economic benefit
    And the applicant adds some content and marks this section as complete
    And The user navigates to the overview page of the Robot test application
    Then the applicant can see that the 'economics benefit' section is marked as complete

Status is updated after marking as incomplete
    [Documentation]    INFUND-539
    [Tags]    HappyPath
    Given The user navigates to the overview page of the Robot test application
    And the applicant can see that the 'economics benefit' section is marked as complete
    And the user clicks the button/link    link=4. Economic benefit
    And the applicant edits the "economic benefit" question
    And The user navigates to the overview page of the Robot test application
    Then none of the sections are marked as complete

*** Keywords ***
none of the sections are marked as complete
    Element Should Not Be Visible    css=.complete

the applicant can see that the 'economics benefit' section is marked as complete
    Element Should Be Visible    jQuery=#section-2 .section:nth-child(4) img[src*="/images/field/field-done-right"]
