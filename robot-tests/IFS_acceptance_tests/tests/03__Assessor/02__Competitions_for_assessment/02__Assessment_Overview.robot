*** Settings ***
Documentation     INFUND-3303: As an Assessor I want the ability to reject the application after I have been given access to the full details so I can make Innovate UK aware.
...
...
...               INFUND-3720 As an Assessor I can see deadlines for the assessment of applications currently in assessment on my dashboard, so that I am reminded to deliver my work on time
...
...               INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-5379 The Applications for assessment dashboard shouldn't show the rejected applications
Suite Setup       guest user log-in    felix.wilson@gmail.com    Passw0rd
Suite Teardown    the user closes the browser
Force Tags        Assessor
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot

*** Test Cases ***
Assessment overview should show the expected questions
    [Documentation]    INFUND-3400
    ...
    ...    INFUND-1188
    [Tags]    HappyPath
    Given The user clicks the button/link    link=Juggling Craziness
    when the user clicks the button/link    link=Juggling is fun
    Then The user should be redirected to the correct page    /assessment/11
    And the user should see four sections

Number of days remaining until assessment submission
    [Documentation]    INFUND-3720
    [Tags]    HappyPath
    Then The user should see the text in the page    Days left to submit
    And the days remaining should be correct (Top of the page)    2016-12-31

Reject application (Unable to assess this application)
    [Documentation]    INFUND-3540
    ...
    ...    INFUND-5379
    [Tags]
    When the user clicks the button/link    jQuery=.summary:contains("Unable to assess this application")
    And the user clicks the button/link    link=Reject this application
    And the user fills in rejection details
    And the user clicks the button/link    jQuery=button:contains("X")
    And the user clicks the button/link    link=Reject this application
    And the user clicks the button/link    jquery=button:contains("Reject")
    Then The user should be redirected to the correct page    ${Assessor_application_dashboard}
    And The user should not see the element    link=Juggling is fun

Assessor should not be able to access the rejected application
    [Documentation]    INFUND-5188
    [Tags]
    When the user navigates to the assessor page    ${Assessment_overview_11}
    Then The user should see permissions error message

*** Keywords ***
the user should see four sections
    the user should see the element    css=#section-16 .bold-medium
    the user should see the element    css=#section-71 .heading-medium
    the user should see the element    css=#section-17 .heading-medium

the user fills in rejection details
    And the user should see the element    id=rejectReason
    the user selects the option from the drop-down menu    ${empty}    id=rejectReason    # Note that using this empty option will actually select the 'Select a reason' option at the top of the dropdown menu
    the user clicks the button/link    jquery=button:contains("Reject")
    The user should see an error    Please enter a reason
    Select From List By Index    id=rejectReason    1
    the user should not see an error in the page
    The user enters text to a text field    id=rejectComment    Have conflicts with the area of expertise.
