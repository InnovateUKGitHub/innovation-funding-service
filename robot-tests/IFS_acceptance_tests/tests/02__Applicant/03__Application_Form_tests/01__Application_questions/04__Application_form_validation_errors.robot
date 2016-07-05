*** Settings ***
Documentation     INFUND-43 As an applicant and I am on the application form on an open application, I will receive feedback if I my input is invalid, so I know how I should enter the question
Suite Setup       Run keywords    log in and create new application if there is not one already
...               AND    Applicant goes to the application details page of the Robot application
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot
Resource          ../../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot

*** Test Cases ***
Application-details server-side validations
    [Documentation]    INFUND-2843
    Given the user should see the text in the page    Application details
    And the user enters text to a text field          id=application_details-title    ${EMPTY}
    And the user clicks the button/link               jQuery=button:contains("Mark as complete")
    And the user should see the text in the page      Please enter the full title of the project
    And the user should see the text in the page      Please enter a future date

Application-details client-side validations
    [Documentation]    INFUND-43, INFUND-2843
    Given the user should see the text in the page              Application details
    Then the user enters text to a text field                   id=application_details-title    ${EMPTY}
    When the applicant clears the application title field
    Then The applicant should get a validation error message    Please enter the full title of the project
    And the user enters text to a text field                    id=application_details-title    Robot test application

Invalid date (day)
    [Documentation]    -INFUND-43
    [Tags]
    And the user enters text to a text field                    id=application_details-startdate_day    32
    And the applicant should get a validation error message     Please enter a valid date
    And the user enters text to a text field                    id=application_details-startdate_day    0
    And the applicant should get a validation error message     Please enter a valid date
    And the user enters text to a text field                    id=application_details-startdate_day    -1
    Then the applicant should get a validation error message    Please enter a valid date
    And the field is empty                                      id=application_details-startdate_day
    Then the applicant should get a validation error message    Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_day    15

Invalid date (month)
    [Documentation]    -INFUND-43
    [Tags]
    And the user enters text to a text field                    id=application_details-startdate_month    0
    And the applicant should get a validation error message     Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_month    13
    And the applicant should get a validation error message     Please enter a valid date
    And the user enters text to a text field                    id=application_details-startdate_month    -1
    Then the applicant should get a validation error message    Please enter a valid date
    And the field is empty                                      id=application_details-startdate_month

Invalid date (Year)
    [Documentation]    -INFUND-43
    [Tags]    HappyPath
    And the applicant inserts an invalid date
    Then the applicant should get a validation error message    Please enter a future date
    When the field is empty                                     id=application_details-startdate_year
    Then the applicant should get a validation error message    Please enter a future date
    When the applicant inserts valid year
    Then the applicant should get a validation error message    Please enter a valid date

Invalid duration field
    [Documentation]    -INFUND-43
    [Tags]    Pending  #This is pending till the code is adapted. Since the error message doesn't go away after the instertion of a valid value.
    And the user enters text to a text field                    id=application_details-duration    0
    And the applicant should get a validation error message     Your project should last between 1 and 36 months
    When the user enters text to a text field                   id=application_details-duration    -1
    And the applicant should get a validation error message     Your project should last between 1 and 36 months
    And the field is empty                                      id=application_details-duration
    Then the applicant should get a validation error message    Please enter a valid value
    And the user enters text to a text field                    id=application_details-duration    15
    And the applicant should not see the validation error any more


*** Keywords ***
#the applicant inserts an input
#    [Arguments]    ${FIELD}    ${INPUT}
#    Clear Element Text    ${FIELD}
#    Input Text    ${FIELD}    ${INPUT}
#    focus    jQuery=button:contains("Save and")

the field is empty
    [Arguments]    ${EMPTY_FIELD}
    Clear Element Text    ${EMPTY_FIELD}

the applicant should not see the validation error any more
    Focus    css=.app-submit-btn
    the user should not see the element   css=.error-message

the applicant inserts an invalid date
    Clear Element Text    id=application_details-startdate_day
    Input Text    id=application_details-startdate_day    18
    Clear Element Text    id=application_details-startdate_year
    Input Text    id=application_details-startdate_year    2015
    Clear Element Text    id=application_details-startdate_month
    Input Text    id=application_details-startdate_month    11

the applicant inserts valid year
    Clear Element Text    id=application_details-startdate_year
    Input Text    id=application_details-startdate_year    2018

#the applicant clears the text area of the "Project Summary"
#    Clear Element Text    css=#form-input-11 .editor
#    Press Key    css=#form-input-11 .editor    \\8
#    Focus    css=.app-submit-btn
#    Comment    Click Element    css=.fa-bold
#    Sleep    300ms

the applicant clears the application title field
    Clear Element Text    id=application_details-title

The applicant should get a validation error message
    [Arguments]    ${validation error}
    focus    jQuery=button:contains("Save and")
    sleep    300ms
    Run Keyword and ignore error    Wait Until Page Contains    ${validation_error}

Applicant goes to the application details page of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Application details
