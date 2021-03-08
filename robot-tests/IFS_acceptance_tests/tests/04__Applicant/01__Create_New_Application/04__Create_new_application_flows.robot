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
...
...               IFS-8646 Comp 730 - App 97191 - Unable to access application
...
...               IFS-8826 Applicants with assessor roles cannot add a funding level in an application
...
...               IFS-7723 Improvement to company search results
...
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Applicant_Commons.robot

*** Test Cases ***
Non registered users non companies house route
    [Documentation]    INFUND-669 INFUND-1904 INFUND-1920  IFS-7723
    [Tags]  HappyPath
    Given the user navigates to the page                         ${frontDoor}
    And the user clicks the button/link in the paginated list    link = ${createApplicationOpenCompetition}
    And the user clicks the button/link                          jQuery = a:contains("Start new application")
    And the user clicks the button/link                          link = Continue and create an account
    And the user selects the radio button                        organisationTypeId    radio-1
    And the user clicks the button/link                          jQuery = .govuk-button:contains("Save and continue")

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

Applicant can still access the application from dashboard on saving the apllication with an empty application name
    [Documentation]  IFS-8646
    Given the user navigates to the page         ${SERVER}
    When Logging in and Error Checking           ${test_mailbox_one}+business@gmail.com    ${correct_password}
    And the user edits the application title     ${empty}
    And the user clicks the button/link          link = Dashboard
    Then the user should see the element         link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}

Verify the name of the new application
    [Documentation]    INFUND-669 INFUND-1163
    [Tags]  HappyPath
    When the user edits the application title                        ${test_title}
    Then the user should see the element                             jQuery = h1 span:contains("${test_title}")
    And the progress indicator should show 0
    And the user clicks the button/link                              link = Application team
    And the user should see the element                              jQuery = h1:contains("Application team")
    And the user can see this new application on their dashboard     ${test_title}

Marketing emails information should have updated on the profile
    [Documentation]    INFUND-9243
    [Tags]  HappyPath
    When the user navigates to the page    ${edit_profile_url}
    Then Checkbox Should Be Selected       allowMarketingEmails

The user with assessor and applicant roles can add a funding level in the application
    [Documentation]   IFS-8826
    Given log in as a different user                               jo.peters@ntag.example.com   ${short_password}
    And the user select the competition and starts application     KTP new competition
    And the user apply with knowledge base organisation            Reading    The University of Reading
    When the user completes funding level in application
    Then the user should see the element                           jQuery = dt:contains("Funding level")+dd:contains("100.00%")
    And the user should see the element                            jQuery = p:contains("No other funding")


*** Keywords ***
the new application should be visible in the dashboard page
    the user clicks the button/link      link = Dashboard
    the user should see the element      jQuery = h1 span:contains("${test_title}")
    the user should see the element      jQuery = dt:contains("Application number:")

the user edits the application title
    [Arguments]   ${applicationName}
    the user clicks the button/link         link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         link = Application details
    The project start date is blank
    The user enters text to a text field    css = [id = "name"]    ${applicationName}
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

