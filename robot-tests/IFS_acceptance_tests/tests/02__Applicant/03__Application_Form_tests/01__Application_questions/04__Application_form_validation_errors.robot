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
Title field client side
    [Documentation]    INFUND-43, INFUND-2843
    Given the user should see the text in the page              Application details
    When the user enters text to a text field                   id=application_details-title    ${EMPTY}
    Then the applicant should get a validation error message    Please enter the full title of the project
    And the user enters text to a text field                    id=application_details-title    Robot test application
    And the applicant should not see the validation error any more

Day field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Tags]
    When the user enters text to a text field                   id=application_details-startdate_day    32
    Then the applicant should get a validation error message    Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_day    0
    Then the applicant should get a validation error message    Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_day    -1
    Then the applicant should get a validation error message    Please enter a valid date
    When the field is empty                                     id=application_details-startdate_day
    Then the applicant should get a validation error message    Please enter a valid date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Month field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Tags]
    When the user enters text to a text field                   id=application_details-startdate_month    0
    Then the applicant should get a validation error message    Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_month    13
    Then the applicant should get a validation error message    Please enter a valid date
    When the user enters text to a text field                   id=application_details-startdate_month    -1
    Then the applicant should get a validation error message    Please enter a valid date
    When the field is empty                                     id=application_details-startdate_month
    Then the applicant should get a validation error message    Please enter a valid date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more


Year field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Tags]    HappyPath
    [Setup]  Run keywords  the user enters text to a text field    id=application_details-title    Robot test application
    ...      AND  the user enters text to a text field             id=application_details-duration    15
    When the applicant inserts an invalid date
    Then the applicant should get a validation error message    Please enter a future date
    When the field is empty                                     id=application_details-startdate_year
    Then the applicant should get a validation error message    Please enter a future date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more


Duration field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Setup]  Run keywords  the user enters text to a text field    id=application_details-title    Robot test application
    ...      AND  the applicant inserts a valid date
    When the user enters text to a text field                   id=application_details-duration    0
    Then the applicant should get a validation error message    Your project should last between 1 and 36 months
    When the user enters text to a text field                   id=application_details-duration    -1
    And the applicant should get a validation error message     Your project should last between 1 and 36 months
    And the field is empty                                      id=application_details-duration
    Then the applicant should get a validation error message    Please enter a valid value
    And the user enters text to a text field                    id=application_details-duration    15
    And the applicant should not see the validation error any more

Application details server side
    [Documentation]    INFUND-2843
    Given the user should see the text in the page    Application details
    When the user enters text to a text field         id=application_details-title    ${EMPTY}
    Then the user enters text to a text field         id=application_details-startdate_day    ${EMPTY}
    And the user enters text to a text field          id=application_details-startdate_month    ${EMPTY}
    And the user enters text to a text field          id=application_details-startdate_year    ${EMPTY}
    And the field is empty                            id=application_details-duration
    When the user clicks the button/link              jQuery=button:contains("Mark as complete")
    Then the user should see the text in the page     Please enter the full title of the project
    And the user should see the text in the page      Please enter a future date
    And the user should see the text in the page      Your project should last between 1 and 36 months
    And the user should see the element               css=.error-summary-list

Empty text area
    [Documentation]    -INFUND-43
    [Tags]
    Given the user should see the text in the page    Application details
    Then the user clicks the button/link               css=.pagination-part-title
    When the applicant clears the text area of the "Project Summary"
    Then the applicant should get a validation error message    Please enter some text


*** Keywords ***
the field is empty
    [Arguments]    ${EMPTY_FIELD}
    Clear Element Text    ${EMPTY_FIELD}

the applicant should not see the validation error any more
    Focus    css=.app-submit-btn
    run keyword and ignore error  mouse out  css=input
    wait until element is not visible    css=.error-message

the applicant inserts a valid date
    Clear Element Text    id=application_details-startdate_day
    Input Text            id=application_details-startdate_day    20
    Clear Element Text    id=application_details-startdate_month
    Input Text            id=application_details-startdate_month    11
    Clear Element Text    id=application_details-startdate_year
    Input Text            id=application_details-startdate_year    2020

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

the applicant clears the text area of the "Project Summary"
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Comment    Click Element    css=.fa-bold
    Sleep    300ms

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
