*** Settings ***
Documentation     INFUND-43 As an applicant and I am on the application form on an open application, I will receive feedback if I my input is invalid, so I know how I should enter the question \ INFUND-4694 As an applicant I want to be able to provide details of my previous submission if I am allowed to resubmit my project in the current competition so that I comply with Innovate UK competition eligibility criteria
Suite Setup       Run keywords    log in and create new application if there is not one already
...               AND    Applicant goes to the application details page of the Robot application
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Test Cases ***
Title field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Tags]    HappyPath
    Given the user should see the text in the page    Application details
    When the user enters text to a text field    id=application_details-title    ${EMPTY}
    And the user should see an error    Please enter the full title of the project
    And the user enters text to a text field    id=application_details-title    Robot test application
    And the applicant should not see the validation error any more

Day field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]    HappyPath
    [Setup]    The applicant inserts a valid date
    When the user enters text to a text field    id=application_details-startdate_day    32
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_day    0
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_day    -1
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_day    ${EMPTY}
    Then the user should see an error    Please enter a valid date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more
    #TODO this test case fails when running the HappyPath.

Month field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]
    [Setup]    The applicant inserts a valid date
    When the user enters text to a text field    id=application_details-startdate_month    0
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_month    13
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_month    -1
    Then the user should see an error    Please enter a valid date
    When the user enters text to a text field    id=application_details-startdate_month    ${EMPTY}
    Then the user should see an error    Please enter a valid date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Year field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]    HappyPath
    [Setup]    Run keywords    the user enters text to a text field    id=application_details-title    Robot test application
    ...    AND    the user enters text to a text field    id=application_details-duration    15
    ...    AND    Run Keyword And Ignore Error    Focus    jQuery=Button:contains("Mark as complete")
    When the applicant inserts an invalid date
    Then the user should see an error    Please enter a future date
    When the user enters text to a text field    id=application_details-startdate_year    ${EMPTY}
    Then the user should see an error    Please enter a future date
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Duration field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]
    [Setup]    Run keywords    the user enters text to a text field    id=application_details-title    Robot test application
    ...    AND    the applicant inserts a valid date
    When the user enters text to a text field    id=application_details-duration    0
    Then the user should see an error    Your project should last between 1 and 36 months
    When the user enters text to a text field    id=application_details-duration    -1
    Then the user should see an error    Your project should last between 1 and 36 months
    When the user enters text to a text field    id=application_details-duration    ${EMPTY}
    Then the user should see an error    Please enter a valid value
    And the user enters text to a text field    id=application_details-duration    15
    And the applicant should not see the validation error of the duration any more

Application details server side
    [Documentation]    INFUND-2843
    ...
    ...    INFUND-4694
    [Tags]    Pending    HappyPath
    # TODO pending INFUND-3999
    Given the user should see the text in the page    Application details
    When the user clicks the button/link    jQuery=label:contains(Yes) input
    And the user enters text to a text field    id=application_details-title    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_day    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_month    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_year    ${EMPTY}
    And the user enters text to a text field    id=application_details-duration    ${EMPTY}
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then The user should see an error    Please enter the full title of the project
    And the user should see an error    Please enter a future date
    And the user should see an error    Your project should last between 1 and 36 months
    And the user should see an error    Please enter the previous application number
    And the user should see an error    Please enter the previous application title
    And the user should see the element    css=.error-summary-list
    [Teardown]    the user enters text to a text field    id=application_details-title    Robot test application

Empty text area
    [Documentation]    INFUND-43
    [Tags]
    Given the user clicks the button/link    css=.pagination-part-title
    When the applicant clears the text area of the "Project Summary"
    When the user clicks the button/link    jQuery=Button:contains("Mark as complete")
    Then the user should see an error    Please enter some text
    When The user enters text to a text field    css=#form-input-11 .editor    Test 123
    Then the applicant should not see the validation error any more

*** Keywords ***
the applicant should not see the validation error any more
    Run Keyword And Ignore Error    Mouse Out    css=input
    Run Keyword And Ignore Error    Focus    jQuery=Button:contains("Mark as complete")
    sleep    300ms
    the user should not see the element    css=.error-message

the applicant inserts a valid date
    Clear Element Text    id=application_details-startdate_day
    The user enters text to a text field    id=application_details-startdate_day    12
    Clear Element Text    id=application_details-startdate_month
    The user enters text to a text field    id=application_details-startdate_month    11
    Clear Element Text    id=application_details-startdate_year
    The user enters text to a text field    id=application_details-startdate_year    2020

the applicant inserts an invalid date
    Clear Element Text    id=application_details-startdate_day
    The user enters text to a text field    id=application_details-startdate_day    18
    Clear Element Text    id=application_details-startdate_year
    The user enters text to a text field    id=application_details-startdate_year    2015
    Clear Element Text    id=application_details-startdate_month
    The user enters text to a text field    id=application_details-startdate_month    11

the applicant clears the text area of the "Project Summary"
    Clear Element Text    css=#form-input-11 .editor
    Press Key    css=#form-input-11 .editor    \\8
    Focus    css=.app-submit-btn
    Sleep    300ms

Applicant goes to the application details page of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Application details

the applicant should not see the validation error of the duration any more
    Focus    css=.app-submit-btn
    run keyword and ignore error    mouse out    css=input
    Run Keyword And Ignore Error    mouse out    css=.editor
    Focus    css=.app-submit-btn
    sleep    300ms
    The user should not see the text in the page    Please enter a valid value
