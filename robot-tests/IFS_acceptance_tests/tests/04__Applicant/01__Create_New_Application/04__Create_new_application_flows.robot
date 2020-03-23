*** Settings ***
Documentation     INFUND-669 As an applicant I want to create a new application so...
...
...               INFUND-1163 As an applicant I want to create a new application so..
...
...               INFUND-1904 As a user registering an account and submitting the data I expect to receive a verification email so...
...
...               INFUND-1920 As an applicant once I am accessing my dashboard and clicking on the newly created application for the first time, it will allow me to invite contributors and partners
...
...               INFUND-9243 Add marketing email option tick box to 'Your details' page in the 'Create your account' journey
...
...               INFUND-1040: As an applicant I want to be able to create more than one application so..
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../10__Project_setup/PS_Common.robot
Resource          ../Applicant_Commons.robot

*** Test Cases ***
Non registered users non companies house route
    [Documentation]    INFUND-669 INFUND-1904 INFUND-1920
    [Tags]  HappyPath
    Given the user navigates to the page           ${frontDoor}
    And the user clicks the button/link in the paginated list    link = ${createApplicationOpenCompetition}
    And the user clicks the button/link            jQuery = a:contains("Start new application")
    And the user clicks the button/link            link = Continue and create an account
    And the user selects the radio button          organisationTypeId    radio-1
    And the user clicks the button/link            jQuery = .govuk-button:contains("Save and continue")
    When the user clicks the Not on companies house link
    Then the user clicks the button/link           jQuery = .govuk-button:contains("Save and continue")
    And The user should see the element            jQuery = h1:contains("Your details")

The email address does not stay in the cookie
    [Documentation]    INFUND_2510
    Given Applicant goes to the registration form
    Then the user should not see the element        jQuery = strong:contains("${test_mailbox_one}+rto@gmail.com")

Non registered users sign-up companies house route
    [Documentation]    INFUND-669 INFUND-1904 INFUND-1920 INFUND-1785 INFUND-9280
    [Tags]  HappyPath
    Given Applicant goes to the registration form
    When the user verifies email                      Phil    Smith    ${test_mailbox_one}+business@gmail.com
    Then the user directed to correct dashboard       ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    And the user reads his email and clicks the link  ${test_mailbox_one}+business@gmail.com    Innovate UK applicant questionnaire    diversity survey

Verify the name of the new application
    [Documentation]    INFUND-669 INFUND-1163
    [Tags]  HappyPath
    [Setup]    the user navigates to the page                     ${SERVER}
    When Logging in and Error Checking                            ${test_mailbox_one}+business@gmail.com    ${correct_password}
    And the user edits the application title
    Then the user should see the element                          jQuery = h1 span:contains("${test_title}")
    And the progress indicator should show 0
    And the user clicks the button/link                           link = Application team
    And the user should see the element                           jQuery = h1:contains("Application team")
    And the user can see this new application on their dashboard  ${test_title}

Marketing emails information should have updated on the profile
    [Documentation]    INFUND-9243
    [Tags]  HappyPath
    When the user navigates to the page    ${edit_profile_url}
    Then Checkbox Should Be Selected       allowMarketingEmails

*** Keywords ***
the new application should be visible in the dashboard page
    the user clicks the button/link      link = Dashboard
    the user should see the element      jQuery = h1 span:contains("${test_title}")
    the user should see the element      jQuery = dt:contains("Application number:")

the user edits the application title
    the user clicks the button/link         link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         link = Application details
    The project start date is blank
    The user enters text to a text field    css = [id = "name"]    ${test_title}
    the user clicks the button/link         jQuery = .govuk-button:contains("Save and return")

the progress indicator should show 0
    Element Should Contain  css = .progress-indicator    0

The project start date is blank
    the user should see the element  xpath = //*[@id="startDate" and @placeholder="DD"]
    the user should see the element  xpath = //*[@id="application_details-startdate_month" and @placeholder="MM"]
    the user should see the element  xpath = //*[@id="application_details-startdate_year" and @placeholder="YYYY"]

The user can see this new application on their dashboard
    [Arguments]     ${application_name}
    the user navigates to the page            ${APPLICANT_DASHBOARD_URL}
    the user should see the element           link = ${application_name}

Applicant goes to the registration form
    the user navigates to the page   ${frontDoor}
    the user clicks the button/link in the paginated list   link = ${createApplicationOpenCompetition}
    And the user follows the flow to register their organisation   ${BUSINESS_TYPE_ID}

the user directed to correct dashboard
    [Arguments]    ${Application_name}
    the user should see the element      jQuery = h1:contains("Applications")
    the user clicks the button/link      link = ${Application_name}
    the user is redirected to overview page if he has been there already

the user is redirected to overview page if he has been there already
    log in as a different user           ${test_mailbox_one}+business@gmail.com    ${correct_password}
    the user clicks the button/link      link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user should see the element      jQuery = h1:contains("Application overview")
    the user clicks the button/link      link = Application team
    logout as user