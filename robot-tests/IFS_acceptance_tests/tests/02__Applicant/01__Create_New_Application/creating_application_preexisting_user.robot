*** Settings ***
Documentation     INNFUND-1040: As an applicant I want to be able to create more than one application so that i can enter the same competition more than once
Suite Setup       Delete the emails from the test mailbox
Resource          ../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../resources/variables/User_credentials.robot
Resource          ../../../resources/keywords/Login_actions.robot
Resource          ../../../resources/keywords/User_actions.robot
Force Tags        Create application

*** Test Cases ***

Logged in user can create a new application
    [Documentation]     INFUND-1040
    ...
    ...                 INFUND-1223
    [Tags]  Create application      Pending
    # Pending until INFUND-1223 is finished and on dev
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page  ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page      ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    Then the user should be redirected to the correct page       ${speed_bump_url}
    And the user selects the option     true
    And the user should see the text in the page        Inviting Contributors and Partners
    And the user clicks the button/link     jQuery=.button:contains("Begin application")
    And the user should see the text in the page      Application overview
    And the user can see this new application on their dashboard
    And the project start date is blank
    And the user can save the page with the blank date


Logged in user can choose to continue with an existing application
    [Documentation]     INFUND-1040
    [Tags]  Create application
    Given Guest user log-in    &{lead_applicant_credentials}
    When the user navigates to the page  ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page      ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    Then the user should be redirected to the correct page       ${speed_bump_url}
    And the user selects the option     false
    And the user should be redirected to the correct page   ${dashboard_url}



Non-logged in user has the option to log into an existing account
    [Documentation]     IFUND-1040
    [Tags]      Create application
    Given the user can log out
    When the user navigates to the page      ${competition_details_url}
    And the user clicks the button/link     jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page   ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link     jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link     jQUery=.button:contains("Login")
    And the guest user inserts user email & password       jessica.doe@ludlow.co.uk     Passw0rd
    And the guest user clicks the log-in button
    Then the user should be redirected to the correct page      ${speed_bump_url}
    And the user selects the option     true
    And the user should see the text in the page        Inviting Contributors and Partners
    And the user clicks the button/link         jQuery=.button:contains("Begin application")
    And the user should see the text in the page        Application overview
    And the user can see this new application on their dashboard


Non-logged in user can log in and continue with an existing application
    Given the user can log out
    When the user navigates to the page     ${competition_details_url}
    And the user clicks the button/link     jQuery=.button:contains("Apply now")
    And the user should be redirected to the correct page   ${ELIGIBILITY_INFO_URL}
    And the user clicks the button/link     jQuery=.button:contains("Sign in to apply")
    And the user clicks the button/link     jQUery=.button:contains("Login")
    And the guest user inserts user email & password       jessica.doe@ludlow.co.uk    Passw0rd
    And the guest user clicks the log-in button
    Then the user should be redirected to the correct page      ${speed_bump_url}
    And the user selects the option     false
    And the user should be redirected to the correct page       ${dashboard_url}
    [Teardown]  Logout as user


*** Keywords ***

The user selects the option
    [Arguments]         ${option}
    Select Radio Button    create-application      ${option}
    the user clicks the button/link     jQuery=.button:contains("Continue")


The user can see this new application on their dashboard
    The user navigates to the page      ${applicant_dashboard_url}
    The user should see the text in the page        Technology Inspired

The project start date is blank
    The user clicks the button/link          link=Technology Inspired
    The user clicks the button/link          link=Application details
    Element Should Be Visible               xpath=//*[@id="application_details-startdate_day" and @placeholder="DD"]
    Element Should Be Visible               xpath=//*[@id="application_details-startdate_month" and @placeholder="MM"]
    Element Should Be Visible               xpath=//*[@id="application_details-startdate_year" and @placeholder="YYYY"]


The user can save the page with the blank date
    the user clicks the button/link     jQuery=.button:contains("Save and return to application overview")
    the user should be redirected to the correct page       ${dashboard_url}





