*** Settings ***
Documentation     INFUND-43 As an applicant and I am on the application form on an open application, I will receive feedback if I my input is invalid, so I know how I should enter the question
...
...               INFUND-4694 As an applicant I want to be able to provide details of my previous submission if I am allowed to resubmit my project in the current competition so that I comply with Innovate UK competition eligibility criteria
...
...               INFUND-6823 As an Applicant I want to be invited to select the primary 'Research area' for my project
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
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_day    0
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_day    -1
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_day    ${EMPTY}
    Then the user should see an error    Please enter a valid date.
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Month field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]
    [Setup]    The applicant inserts a valid date
    When the user enters text to a text field    id=application_details-startdate_month    0
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_month    13
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_month    -1
    Then the user should see an error    Please enter a valid date.
    When the user enters text to a text field    id=application_details-startdate_month    ${EMPTY}
    Then the user should see an error    Please enter a valid date.
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Year field client side
    [Documentation]    INFUND-43
    ...
    ...    INFUND-2843
    [Tags]    HappyPath
    [Setup]    Run keywords    the user enters text to a text field    id=application_details-title    Robot test application
    ...    AND    the user enters text to a text field    id=application_details-duration    15
    ...    AND    Run Keyword And Ignore Error Without Screenshots    Focus    jQuery=Button:contains("Mark as complete")
    When the applicant inserts an invalid date
    Then the user should see an error    Please enter a future date.
    When the user enters text to a text field    id=application_details-startdate_year    ${EMPTY}
    Then the user should see an error    Please enter a future date.
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
    Then the user should see an error    Your project should last between 1 and 36 months.
    When the user enters text to a text field    id=application_details-duration    -1
    Then the user should see an error    Your project should last between 1 and 36 months.
    When the user enters text to a text field    id=application_details-duration    ${EMPTY}
    Then the user should see an error    This field cannot be left blank.
    And the user enters text to a text field    id=application_details-duration    15
    And the applicant should not see the validation error of the duration any more

Application details server side
    [Documentation]    INFUND-2843
    ...
    ...    INFUND-4694
    ...
    ...    INFUND-6823
    [Tags]
    Given the user should see the text in the page    Application details
    When the user clicks the button/link    jQuery=label:contains(Yes)
    And the user enters text to a text field    id=application_details-title    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_day    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_month    ${EMPTY}
    And the user enters text to a text field    id=application_details-startdate_year    ${EMPTY}
    And the user enters text to a text field    id=application_details-duration    ${EMPTY}
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then The user should see an error    Please enter the full title of the project.
    And the user should see an error    Please enter a future date.
    And the user should see an error    This field cannot be left blank.
    And the user should see an error    Please enter the previous application number.
    And the user should see an error    Please enter the previous application title.
    And the user should see an error    Please enter the full title of the project.
    # TODO commented due to INFUND-9066
    # And the user should see an error    Please select a research category.
    # And The user should see an error    Please select an innnovation area.
    And the user should see the element    css=.error-summary-list
    [Teardown]    Run keywords    the user enters text to a text field    id=application_details-title    Robot test application
    ...    AND    Focus    jQuery=button:contains("Save and return to application overview")

Empty text area
    [Documentation]    INFUND-43
    [Tags]
    [Setup]    The user clicks the button/link    link=Application overview
    Given the user clicks the button/link    link=Project summary
    When the applicant clears the text area of the "Project Summary"
    When the user clicks the button/link    jQuery=Button:contains("Mark as complete")
    Then the user should see an error    Please enter some text.
    When The user enters text to a text field    css=#form-input-1039 .editor    Test 123
    Then the applicant should not see the validation error any more

*** Keywords ***
the applicant should not see the validation error any more
    Run Keyword And Ignore Error Without Screenshots    Mouse Out    css=input
    Run Keyword And Ignore Error Without Screenshots    Focus    jQuery=Button:contains("Mark as complete")
    wait for autosave
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
    Clear Element Text    css=#form-input-1039 .editor
    Press Key    css=#form-input-1039 .editor    \\8
    Focus    css=.app-submit-btn
    wait for autosave

Applicant goes to the application details page of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Application details

the applicant should not see the validation error of the duration any more
    Focus    css=.app-submit-btn
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=.editor
    Focus    css=.app-submit-btn
    wait for autosave
    The user should not see the text in the page    Your project should last between 1 and 36 months
