*** Settings ***
Resource          ../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../resources/variables/User_credentials.robot
Resource          ../../resources/keywords/Login_actions.robot
Resource          ../../resources/keywords/User_actions.robot

*** Keywords ***
log in and create new application if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new application with the same user

log in and create new application for collaboration if there is not one already
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Invite robot test application
    Run Keyword If    '${status}' == 'FAIL'    Create new invite application with the same user

Log in create a new invite application invite academic collaborators and accept the invite
    [Tags]    Email
    Given Guest user log-in    &{lead_applicant_credentials}
    ${STATUS}    ${VALUE}=    Run Keyword And Ignore Error    Page Should Contain    Academic robot test application
    Run Keyword If    '${status}' == 'FAIL'    Run keywords    Create new academic application with the same user
    ...    AND    Delete the emails from both test mailboxes
    ...    AND    Invite and accept the invitation
    ...    AND    the user closes the browser

Create new application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new invite application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Invite robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Create new academic application with the same user
    When the user navigates to the page    ${COMPETITION_DETAILS_URL}
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user clicks the button/link    jQuery=.button:contains("Apply now")
    And the user selects the radio button    create-application    true
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    And the user clicks the button/link    jQuery=.button:contains("Begin application")
    And the user clicks the button/link    link=Application details
    And the user enters text to a text field    id=application_details-title    Academic robot test application
    And the user clicks the button/link    jQuery=button:contains("Save and return")

Invite and accept the invitation
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Academic robot test application
    And the user should see the text in the page    View team members and add collaborators
    When the user clicks the button/link    link=View team members and add collaborators
    And the user clicks the button/link    jQuery=.button:contains("Invite new contributors")
    And the user clicks the button/link    jquery=li:nth-last-child(1) button:contains('Add additional partner organisation')
    Input Text    name=organisations[1].organisationName    Academic Test
    Input Text    name=organisations[1].invites[0].personName    Arsene Wenger
    Input Text    name=organisations[1].invites[0].email    worth.email.test+academictest@gmail.com
    focus    jquery=button:contains("Save Changes")
    And the user clicks the button/link    jquery=button:contains("Save Changes")
    And the user closes the browser
    And the guest user opens the browser
    When the user opens the mailbox and accepts the invitation to collaborate
    And the user clicks the button/link    jQuery=.button:contains("Create")
    When the user selects the radio button    organisationType    2
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user selects the radio button    organisationType    5
    And the user clicks the button/link    jQuery=.button:contains("Continue")
    When the user enters text to a text field    id=organisationSearchName    Liv
    And the user clicks the button/link    jQuery=.button:contains("Search")
    When the user clicks the button/link    link= University of Liverpool
    When the user clicks the button/link    jQuery=button:contains("Enter address manually")
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine1    The East Wing
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine2    Popple Manor
    And the user enters text to a text field    id=addressForm.selectedPostcode.addressLine3    1, Popple Boulevard
    And the user enters text to a text field    id=addressForm.selectedPostcode.town    Poppleton
    And the user enters text to a text field    id=addressForm.selectedPostcode.county    Poppleshire
    And the user enters text to a text field    id=addressForm.selectedPostcode.postcode    POPPS123
    And the user clicks the button/link    jQuery=.button:contains("Save organisation and continue")
    And the user clicks the button/link    jQuery=.button:contains("Save")
    And the user fills the create account form    Arsene    Wenger
    And the user opens the mailbox and verifies the email from
    And the user clicks the button/link    jQuery=.button:contains("Sign in")
    And guest user log-in    worth.email.test+academictest@gmail.com    Passw0rd123

Applicant navigates to the finances of the Robot application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Your finances

The user redirects to the page
    [Arguments]    ${TEXT1}    ${TEXT2}
    Wait Until Keyword Succeeds    10    500ms    Page Should Contain    ${TEXT1}
    Page Should Contain    ${TEXT2}
    Page Should Not Contain    error
    Page Should Not Contain    Page or resource not found
    Page Should Not Contain    You do not have the necessary permissions for your request
    # Header checking (INFUND-1892)
    Wait Until Element Is Visible    id=global-header
    Page Should Contain    BETA

The user navigates to the summary page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    And the user clicks the button/link    link=Review & submit

The user navigates to the overview page of the Robot test application
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application

the user marks the finances as complete
    Focus    jQuery=button:contains("Mark all as complete")
    the user clicks the button/link    jQuery=button:contains("Mark all as complete")
    Sleep    1s

Make the finances ready for mark as complete
    Applicant navigates to the finances of the Robot application
    The user clicks the button/link    jQuery=label:contains(Medium - claim up to 60%) input
    Input Text    id=cost-financegrantclaim    20
    The user clicks the button/link    jQuery=#otherFundingShowHideToggle label:contains(No) input
