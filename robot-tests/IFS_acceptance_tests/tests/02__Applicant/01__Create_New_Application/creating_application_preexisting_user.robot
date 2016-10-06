*** Settings ***
Documentation     INNFUND-1040: As an applicant I want to be able to create more than one application so that i can enter the same competition more than once
Suite Setup       Delete the emails from both test mailboxes
Force Tags        Applicant
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Resource          ../../../resources/keywords/EMAIL_KEYWORDS.robot

*** Test Cases ***
Logged in user can create a new application
    [Documentation]    INFUND-1040
    ...
    ...    INFUND-1223
    [Tags]    HappyPath
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page    ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    Then the user should be redirected to the correct page    ${speed_bump_url}
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user should see the text in the page    Inviting Contributors and Partners
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user should see the text in the page    Application overview
    And the user can see this new application on their dashboard
    And the project start date is blank
    And the user can save the page with the blank date

Logged in user can choose to continue with an existing application
    [Documentation]    INFUND-1040
    [Tags]    HappyPath
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page    ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    Then the user should be redirected to the correct page    ${speed_bump_url}
    And the user selects the radio button    create-application    false
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user should be redirected to the correct page    ${dashboard_url}

Non-logged in user has the option to log into an existing account
    [Documentation]    INFUND-1040
    [Tags]
    Given the user can log out
    When the user navigates to the page    ${competition_details_url}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page    ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQUery=.button:contains("Sign in")
    And the guest user inserts user email & password    jessica.doe@ludlow.co.uk    Passw0rd
    And the guest user clicks the log-in button
    Then the user should be redirected to the correct page    ${speed_bump_url}
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user should see the text in the page    Inviting Contributors and Partners
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user should see the text in the page    Application overview
    And the user can see this new application on their dashboard

Non-logged in user can log in and continue with an existing application
    [Documentation]    INFUND-1040
    [Tags]
    Given the user can log out
    When the user navigates to the page    ${competition_details_url}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page    ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Create account")
    And the user clicks the button/link    jQUery=.button:contains("Sign in")
    And the guest user inserts user email & password    jessica.doe@ludlow.co.uk    Passw0rd
    And the guest user clicks the log-in button
    Then the user should be redirected to the correct page    ${speed_bump_url}
    And the user selects the radio button    create-application    false
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user should be redirected to the correct page    ${dashboard_url}
    [Teardown]    Logout as user

*** Keywords ***
The user can see this new application on their dashboard
    the user navigates to the page    ${applicant_dashboard_url}
    the user should see the text in the page    ${OPEN_COMPETITION_LINK}

The project start date is blank
    the user clicks the button/link    link=${OPEN_COMPETITION_LINK}
    the user clicks the button/link    link=Application details
    the user should see the element    xpath=//*[@id="application_details-startdate_day" and @placeholder="DD"]
    the user should see the element    xpath=//*[@id="application_details-startdate_month" and @placeholder="MM"]
    the user should see the element    xpath=//*[@id="application_details-startdate_year" and @placeholder="YYYY"]

The user can save the page with the blank date
    Submit Form
    # note that the below validation is being used rather than a specific application number so that the test is
    # not broken by other tests that run before it and may change this application's number
    the user should see the text in the page    Application overview
    the user should see the text in the page    ${OPEN_COMPETITION_LINK}
