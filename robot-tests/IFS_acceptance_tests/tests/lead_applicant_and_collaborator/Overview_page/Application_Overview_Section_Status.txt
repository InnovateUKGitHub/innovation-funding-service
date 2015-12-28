*** Settings ***
Documentation     INFUND-539 - As an applicant I want the ‘Application details’ drop down on the ‘Application overview’ page to show a green tick when I’ve marked it as complete, so that I know what I’ve done
Suite Setup       Log in as user    &{lead_applicant_credentials}
Suite Teardown    User closes the browser
Default Tags      Applicant    Overview    Application
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/Applicant_actions.robot
Resource          ../../../resources/keywords/Application_question_edit_actions.robot

*** Test Cases ***
Section status is updated on the overview page after marking a section as complete
    [Documentation]    INFUND-539
    Given applicant goes to the 'application overview' page for application 3
    And none of the sections are marked as complete
    When applicant goes to the 'economic benefits' question for application 3
    And the applicant adds some content and marks this section as complete
    And applicant goes to the 'application overview' page for application 3
    Then the applicant can see that the 'economics benefit' section is marked as complete

Section status is updated on the overview page after editing a section so it is no longer complete
    [Documentation]    INFUND-539
    Given applicant goes to the 'application overview' page for application 3
    And the applicant can see that the 'economics benefit' section is marked as complete
    When applicant goes to the 'economic benefits' question for application 3
    And the applicant edits the "economic benefit" question
    And applicant goes to the 'application overview' page for application 3
    Then none of the sections are marked as complete

*** Keywords ***
none of the sections are marked as complete
    Element Should Not Be Visible    css=.complete

the applicant can see that the 'economics benefit' section is marked as complete
    Element Should Be Visible    css=.application-overview #section-2 #form-input-4 .complete
