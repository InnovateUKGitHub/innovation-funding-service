*** Settings ***
Documentation     INFUND-669 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
...
...               INFUND-1163 As an applicant I want to create a new application so that I can submit an entry into a relevant competition
...
...               INFUND-1904 As a user registering an account and submitting the data I expect to receive a verification email so I can be sure that the provided email address is correct
...
...               INFUND-1920 As an applicant once I am accessing my dashboard and clicking on the newly created application for the first time, it will allow me to invite contributors and partners
...
...               INFUND-9243 Add marketing email option tick box to 'Your details' page in the 'Create your account' journey
Suite Setup       Delete the emails from both test mailboxes
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Variables ***
${APPLICATION_DETAILS_APPLICATION8}    ${SERVER}/application/99/form/question/428

*** Test Cases ***
Non registered users CH route: lead org Business
    [Documentation]    INFUND-669  INFUND-1904  INFUND-1920  INFUND-1785  INFUND-9280
    [Tags]    HappyPath    SmokeTest
    [Setup]    The guest user opens the browser
    When the user follows the flow to register their organisation      radio-1  # business
    then the user verifies email                                       Phil   Smith    ${test_mailbox_one}+business@gmail.com
    and the user directed to correct dashboaard                        ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    and the user reads his email and clicks the link                   ${test_mailbox_one}+business@gmail.com    Innovate UK applicant questionnaire    diversity survey
    [Teardown]    the user closes the browser

Non registered users CH route: lead org RTO
    [Documentation]    INFUND-669  INFUND-1904  INFUND-1785
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    The guest user opens the browser
    When the user follows the flow to register their organisation     radio-3   # RTO
    then the user verifies email                                      Lee    Tess    ${test_mailbox_one}+rto@gmail.com
    and the user directed to correct dashboaard                       ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    and the user reads his email and clicks the link                  ${test_mailbox_one}+rto@gmail.com   Innovate UK applicant questionnaire    diversity survey

The email address does not stay in the cookie
    [Documentation]    INFUND_2510
    [Tags]
    Given the user follows the flow to register their organisation    radio-1
    Then the user should not see the text in the page                 ${test_mailbox_one}+rto@gmail.com
    [Teardown]    the user closes the browser

Non registered users non CH route
    [Documentation]    INFUND-669  INFUND-1904  INFUND-1920
    [Tags]    HappyPath
    [Setup]    the guest user opens the browser
    Given the user navigates to the page            ${COMPETITION_OVERVIEW_URL}
    When the user clicks the button/link            jQuery=a:contains("Start new application")
    And the user clicks the button/link             jQuery=.button:contains("Create account")
    And the user clicks the Not on company house link
    And the user selects the radio button           organisationTypeId    radio-1
    And the user clicks the button/link             jQuery=.button:contains("Save and continue")
    And the user clicks the button/link             jQuery=.button:contains("Save and continue")
    And the user verifies email                     Stuart   Downing  ${test_mailbox_one}+2@gmail.com
    And the user directed to correct dashboaard     ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    [Teardown]    the user closes the browser

Verify the name of the new application
    [Documentation]    INFUND-669  INFUND-1163
    [Tags]    HappyPath    Email    SmokeTest
    [Setup]    the guest user opens the browser
    When Log in as user                             ${test_mailbox_one}+business@gmail.com    ${correct_password}
    And the user edits the competition title
    Then the user should see the text in the page    ${test_title}
    And the progress indicator should show 0
    And the user clicks the button/link             link=view team members and add collaborators
    And the user should see the text in the page    Application team
    And the user should see the text in the page    View and manage your participants
    And the new application should be visible in the dashboard page
    And the user clicks the button/link             link=${test_title}
    And the user should see the text in the page    ${test_title}

Marketing emails information should have updated on the profile
    [Documentation]    INFUND-9243
    [Tags]    HappyPath
    When the user navigates to the page                         ${edit_profile_url}
    Then the user should see that the checkbox is selected      allowMarketingEmails
    [Teardown]    the user closes the browser

*** Keywords ***
the user directed to correct dashboaard
    [Arguments]      ${Application_name}
    the user should see the text in the page      Your dashboard
    the user clicks the button/link               link=${Application_name}
    the user clicks the button/link               jQuery=a:contains("Begin application")
    the user should see the text in the page      Application overview
    logout as user

the new application should be visible in the dashboard page
    the user clicks the button/link              link= My dashboard
    the user should see the text in the page     ${test_title}
    the user should see the text in the page     Application number:

the user clicks the Not on company house link
    the user clicks the button/link    jQuery=summary:contains("Enter details manually")
    the user clicks the button/link    name=manual-address
    The user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    street
    The user enters text to a text field    id=addressForm.selectedPostcode.town    town
    The user enters text to a text field    id=addressForm.selectedPostcode.county    country
    The user enters text to a text field    id=addressForm.selectedPostcode.postcode    post code
    The user enters text to a text field    name=organisationName    org2
    the user clicks the button/link    jQuery=.button:contains("Continue")

the user edits the competition title
    the user clicks the button/link    link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user should see the element    link=Application details
    the user clicks the button/link    link=Application details
    The user enters text to a text field    id=application_details-title    ${test_title}
    the user clicks the button/link    jQuery=button:contains("Save and return")

the progress indicator should show 0
    Element Should Contain    css=.progress-indicator    0
