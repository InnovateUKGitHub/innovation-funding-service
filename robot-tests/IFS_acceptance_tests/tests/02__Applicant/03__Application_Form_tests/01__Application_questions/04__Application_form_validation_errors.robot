*** Settings ***
Documentation     INFUND-43 As an applicant and I am on the application form on an open application, I will receive feedback if I my input is invalid, so I know how I should enter the question
Suite Setup       Guest user log-in    &{lead_applicant_credentials}
Suite Teardown    TestTeardown User closes the browser
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot

*** Test Cases ***
Empty project title field
    [Documentation]    -INFUND-43
    [Tags]    Applicant
    Given the user navigates to the page    ${APPLICATION_DETAILS_URL}
    When the applicant clears the application title field
    Then The applicant should get a validation error message    Please enter the full title of the project.

Invalid date (Year)
    [Documentation]    -INFUND-43
    [Tags]    Applicant    HappyPath
    Given the user navigates to the page    ${APPLICATION_DETAILS_URL}
    And the applicant inserts an invalid date "18-11-2015"
    Then the applicant should get a validation error message    Please enter a future date
    And the field is empty    id=application_details-startdate_year
    Then the applicant should get a validation error message    This field should be a number
    And the applicant inserts "2016" in the Year field(valid date)
    And the applicant should not see the validation error any more

Invalid date (day)
    [Documentation]    -INFUND-43
    [Tags]    Applicant
    Given the user navigates to the page    ${APPLICATION_DETAILS_URL}
    And the applicant inserts an input    id=application_details-startdate_day    32
    And the applicant should get a validation error message    Please enter a valid date
    And the applicant inserts an input    id=application_details-startdate_day    0
    And the applicant should get a validation error message    Please enter a valid date
    And the applicant inserts an input    id=application_details-startdate_day    -1
    Then the applicant should get a validation error message    Please enter a valid date
    And the field is empty    id=application_details-startdate_day
    Then the applicant should get a validation error message    This field should be a number
    And the applicant inserts an input    id=application_details-startdate_day    15
    And the applicant should not see the validation error any more

Invalid date (month)
    [Documentation]    -INFUND-43
    [Tags]    Applicant
    Given the user navigates to the page    ${APPLICATION_DETAILS_URL}
    And the applicant inserts an input    id=application_details-startdate_month    0
    And the applicant should get a validation error message    Please enter a valid date
    When the applicant inserts an input    id=application_details-startdate_month    13
    And the applicant should get a validation error message    Please enter a valid date
    And the applicant inserts an input    id=application_details-startdate_month    -1
    Then the applicant should get a validation error message    Please enter a valid date
    ANd the field is empty    id=application_details-startdate_month
    And the applicant should get a validation error message    This field should be a number
    And the applicant inserts an input    id=application_details-startdate_month    09
    And the applicant should not see the validation error any more

Invalid duration field
    [Documentation]    -INFUND-43
    [Tags]    Applicant
    Given the user navigates to the page    ${APPLICATION_DETAILS_URL}
    And the applicant inserts an input    id=application_details-duration    0
    And the applicant should get a validation error message    Please enter a valid duration
    When the applicant inserts an input    id=application_details-duration    -1
    And the applicant should get a validation error message    Please enter a valid duration
    And the field is empty    id=application_details-duration
    Then the applicant should get a validation error message    This field should be a number
    And the applicant inserts an input    id=application_details-duration    15
    And the applicant should not see the validation error any more

Empty text area
    [Documentation]    -INFUND-43
    [Tags]    Applicant
    Given the user navigates to the page    ${PROJECT_SUMMARY_URL}
    When the applicant clears the text area of the "Project Summary"
    Then the applicant should get a validation error message    Please enter some text

*** Keywords ***
the applicant inserts an input
    [Arguments]    ${FIELD}    ${INPUT}
    Clear Element Text    ${FIELD}
    Input Text    ${FIELD}    ${INPUT}
    focus    jQuery=button:contains("Save and")

the field is empty
    [Arguments]    ${EMPTY_FIELD}
    Clear Element Text    ${EMPTY_FIELD}

the applicant should not see the validation error any more
    Focus    css=.app-submit-btn
    sleep    1s
    Wait Until Element Is Not Visible    css=.error-message

the applicant inserts an invalid date "18-11-2015"
    Clear Element Text    id=application_details-startdate_day
    Input Text    id=application_details-startdate_day    18
    Clear Element Text    id=application_details-startdate_year
    Input Text    id=application_details-startdate_year    2015
    Clear Element Text    id=application_details-startdate_month
    Input Text    id=application_details-startdate_month    11

the applicant inserts "2016" in the Year field(valid date)
    Clear Element Text    id=application_details-startdate_year
    Input Text    id=application_details-startdate_year    2016

the applicant clears the text area of the "Project Summary"
    #Question should be editable    css=#form-input-11 .buttonlink[name="mark_as_incomplete"]
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Comment    Click Element    css=.fa-bold
    Sleep    2s

the applicant clears the application title field
    Clear Element Text    id=application_details-title

The applicant should get a validation error message
    [Arguments]    ${validation error}
    focus    jQuery=button:contains("Save and")
    sleep    5s
    Run Keyword and ignore error    Wait Until Page Contains    ${validation_error}
    #Element Should Be Visible    css=.error-message
