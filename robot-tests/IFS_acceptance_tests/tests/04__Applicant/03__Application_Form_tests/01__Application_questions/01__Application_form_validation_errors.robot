*** Settings ***
Documentation     INFUND-43 As an applicant and I am on the application form on an open application, I will receive feedback if I my input is invalid, so I know how I should enter the question
...
...               INFUND-4694 As an applicant I want to be able to provide details of my previous submission if I am allowed to resubmit my project in the current competition so that I comply with Innovate UK competition eligibility criteria
...
...               INFUND-6823 As an Applicant I want to be invited to select the primary 'Research area' for my project
...
...               IFS-2776 As an Portfolio manager I am able to set the min/max project duration for a competition
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot

*** Variables ***
${applicationTitle}  Robot test application

*** Test Cases ***
Title field client side
    [Documentation]    INFUND-43, INFUND-2843
    [Tags]    HappyPath
    Given the user should see the element      jQuery=h1:contains("Application details")
    When the user enters text to a text field  id=application.name  ${EMPTY}
    Then the user should see a field error     Please enter the full title of the project
    When the user enters text to a text field  id=application.name  ${applicationTitle}
    Then the applicant should not see the validation error any more

Day field client side
    [Documentation]  INFUND-43 INFUND-2843
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
    [Documentation]  INFUND-43 INFUND-2843
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
    [Documentation]  INFUND-43 INFUND-2843
    [Tags]  HappyPath
    Given the user enters text to a text field    id=application.durationInMonths  15
    When the applicant inserts an invalid date
    Then the user should see an error    Please enter a future date.
    When the user enters text to a text field    id=application_details-startdate_year    ${EMPTY}
    Then the user should see an error    Please enter a future date.
    When the applicant inserts a valid date
    Then the applicant should not see the validation error any more

Duration field client side
    [Documentation]  INFUND-43 INFUND-2843 IFS-2776
    [Tags]
    When the user enters text to a text field  id=application.durationInMonths  0
    And the user clicks the button/link        id=application-question-complete
    Then the user should see a field and summary error  Your project should last between 1 and 36 months.

    When the user enters text to a text field  id=application.durationInMonths  -1
    And the user clicks the button/link        id=application-question-complete
    Then the user should see a field and summary error  Your project should last between 1 and 36 months.

    When the user enters text to a text field  id=application.durationInMonths  ${EMPTY}
    Then the user should see a field error     This field cannot be left blank.

    And the user enters text to a text field   id=application.durationInMonths  25
    And the applicant should not see the validation error of the duration any more

Application details server side
    [Documentation]  INFUND-2843 INFUND-4694 INFUND-6823 IFS-2776
    [Tags]
    Given the user should see the element     jQuery=h1:contains("Application details")
    When the user clicks the button/link      jQuery=label:contains(Yes)
    And the user enters text to a text field  id=application.name  ${EMPTY}
    And the user enters text to a text field  id=application_details-startdate_day  ${EMPTY}
    And the user enters text to a text field  id=application_details-startdate_month  ${EMPTY}
    And the user enters text to a text field  id=application_details-startdate_year    ${EMPTY}
    And the user enters text to a text field  id=application.durationInMonths    ${EMPTY}
    And the user unchecks the resubmission radio button
    And the user clicks the button/link       id=application-question-complete
#    Then the user should see a field and summary error  Please tell us if this application is a resubmission or not.
# TODO IFS-3188
    And The user should see a field and summary error   Please enter the full title of the project.
    And the user should see a field and summary error   Please enter a future date.
    And the user should see a field and summary error   This field cannot be left blank.
    And the user should see a field and summary error   Please enter the full title of the project.
    And the user should see a field and summary error   Please select a research category.
    [Teardown]  the user enters text to a text field    id=application.name  ${applicationTitle}


Empty text area
    [Documentation]    INFUND-43
    [Tags]
    [Setup]    The user clicks the button/link    link=Application overview
    Given the user clicks the button/link    link=Project summary
    When the applicant clears the text area of the "Project Summary"
    When the user clicks the button/link    id=application-question-complete
    Then the user should see an error    Please enter some text.
    When The user enters text to a text field    css=.textarea-wrapped .editor    Test 123
    Then the applicant should not see the validation error any more

*** Keywords ***
Custom Suite Setup
    log in and create new application if there is not one already  ${applicationTitle}
    Applicant goes to the application details page of the Robot application

the applicant should not see the validation error any more
    Run Keyword And Ignore Error Without Screenshots    Mouse Out    css=input
    Run Keyword And Ignore Error Without Screenshots    Focus  id=application-question-complete
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
    Clear Element Text    css=.textarea-wrapped .editor
    Press Key    css=.textarea-wrapped .editor    \\8
    Focus    css=.app-submit-btn
    wait for autosave

Applicant goes to the application details page of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    When the user clicks the button/link    link=${applicationTitle}
    And the user clicks the button/link     link=Application details

the applicant should not see the validation error of the duration any more
    Focus    css=.app-submit-btn
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=input
    Run Keyword And Ignore Error Without Screenshots    mouse out    css=.editor
    Focus    css=.app-submit-btn
    wait for autosave
    The user should not see the text in the page    Your project should last between 1 and 36 months

the user unchecks the resubmission radio button
    Run Keyword And Ignore Error Without Screenshots  Execute Javascript  jQuery('#application\\.resubmission-yes').get(0).removeAttribute('checked');
    Run Keyword And Ignore Error Without Screenshots  Execute Javascript  jQuery('#application\\.resubmission-no').get(0).removeAttribute('checked');