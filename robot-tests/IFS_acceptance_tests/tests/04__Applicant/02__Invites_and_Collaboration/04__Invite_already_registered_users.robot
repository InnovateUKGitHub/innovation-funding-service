*** Settings ***
Documentation     INFUND-1458 As a existing user with an invitation to collaborate on an application and I am already registered with IFS I want to be able to use my existing credentials and confirm my details so that I don't have to follow the registration process again.
...
...               INFUND-2716: Error in where the name of an invited partner doesn't update in 'view and manage contributors and collaborators'
...
...               INFUND-3759: Existing Applicant should be able to accept invitations for other applications in the same organisation
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
The invited user should not follow the registration flow again
    [Documentation]    INFUND-1458
    [Tags]  HappyPath
    Given we create a new user                          ${openCompetitionBusinessRTO}  Stuart  Anderson  ${test_mailbox_one}+invitedregistered@gmail.com  ${RTO_TYPE_ID}
    And logout as user
    Given invite a registered user                      ${test_mailbox_one}+invite2@gmail.com    ${test_mailbox_one}+invitedregistered@gmail.com
    When the user reads his email and clicks the link   ${test_mailbox_one}+invitedregistered@gmail.com    Invitation to collaborate in ${openCompetitionBusinessRTO_name}    You will be joining as part of the organisation    2
    Then the user should see the element                jQuery = h3:contains("We have found an account with the invited email address")

The user clicks the login link
    [Documentation]    INFUND-1458
    [Tags]  HappyPath
    When the user clicks the button/link                link = Continue
    And The guest user inserts user email and password  ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    And the guest user clicks the log-in button
    Then the user should see the element                jQuery = dt:contains("INNOVATE LTD")
    When the user clicks the button/link                css = .govuk-button[type="submit"]    #Save and continue
    Then the user should see the element                jQuery = h1:contains("Application overview")

The user edits the name this should be changed in the View team page
    [Documentation]    INFUND-2716
    [Tags]  HappyPath
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    When the user clicks the button/link  link = Profile
    And the user clicks the button/link   link = Edit your details
    And the user enters profile details
    Then the user should see the change in the view team members page

Invite a user with the same organisation under the same organisation
    [Documentation]    INFUND-3759
    [Setup]    Log in as a different user                               ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    When Existing user creates a new application and invites a user from the same organisation
    Then the invited user should get a message to contact the helpdesk  ${test_mailbox_one}+invite2@gmail.com  Invitation to contribute in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation

*** Keywords ***
the user enters profile details
    The user enters text to a text field  id = firstName    Dennis
    The user enters text to a text field  id = lastName    Bergkamp
    Set Focus To Element                  css = [name="create-account"]
    The user clicks the button/link       css = [name="create-account"]

the user should see the change in the view team members page
    The user clicks the button/link  link = Dashboard
    The user clicks the button/link  css = #main-content section:nth-of-type(1) li:nth-child(2) h3 a
    The user clicks the button/link  link = Application team
    The user should see the element  jQuery = td:contains("Dennis Bergkamp")

Existing user creates a new application and invites a user from the same organisation
    the user navigates to the page        ${openCompetitionBusinessRTO_overview}
    the user clicks the button/link       jQuery = a:contains("Start new application")
    the user clicks the button/link       jQuery = .govuk-button:contains("Continue")
    the user should see a field and summary error   Please select an option to continue.
    the user selects the radio button     createNewApplication  true      #Yes, I want to create a new application.
    the user clicks the button/link       jQuery = .govuk-button:contains("Continue")
    the user clicks the button/link       css = .govuk-button[type="submit"]    #Save and continue
    the user clicks the button/link       link = Application team
    the user clicks the button/link       jQuery = button:contains("Add person to INNOVATE LTD")
    The user enters text to a text field  css = [name=name]    Olivier Giroud
    The user enters text to a text field  css = [name=email]     ${test_mailbox_one}+invite2@gmail.com
    the user clicks the button/link       jQuery = button:contains("Invite to application")
    the user should see the element       jQuery = td:contains("${test_mailbox_one}+invite2@gmail.com")
    the user clicks the button/link       link = Application overview
    the user clicks the button/link       link = Application details
    the user enters text to a text field  css = [id="name"]    Invite a user with the same org
    the user clicks the button/link       jQuery = .govuk-button:contains("Save and return")

The invited user should get a message to contact the helpdesk
    [Arguments]    ${recipient}  ${subject}  ${pattern}
    Logout as user
    When the user reads his email and clicks the link   ${recipient}    ${subject}    ${pattern}   2
    When the user clicks the button/link                link = Continue
    And The guest user inserts user email and password  ${recipient}  ${correct_password}
    And the guest user clicks the log-in button
    Then the user should see the element                jQuery = h1:contains("Confirm your organisation")
