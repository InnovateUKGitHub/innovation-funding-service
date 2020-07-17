*** Settings ***
Documentation     INFUND-228: As an Assessor I can see competitions that I have been invited to assess so that I can accept or reject them.
...
...               INFUND-4631: As an assessor I want to be able to reject the invitation for a competition, so that the competition team is aware that I am available for assessment
...
...               INFUND-4145: As an Assessor and I am accepting an invitation to assess within a competition and I don't have an account, I need to select that I create an account in order to be available to assess applications.
...
...               INFUND-1478 As an Assessor creating an account I need to supply my contact details so that Innovate UK can contact me to assess applications.
...
...               INFUND-4919 As an assessor and I have completed setting up my account I can see my dashboard so that I can see the competitions I have accepted to assess.
...
...               INFUND-5165 As an assessor attempting to accept/reject an invalid invitation to assess in a competition, I will receive a notification that I cannot reject the competition as soon as I attempt to reject it.
...
...               INFUND-4895 Securing of services related to Assessor Journey changes
...
...               INFUND-7603 Innovation area added to an Assessor's profile from invite
Suite Setup       The guest user opens the browser
Suite Teardown    The user closes the browser
Force Tags        Assessor  ATS2020
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Assessor_Commons.robot

*** Variables ***
${Invitation_nonregistered_assessor2}     ${server}/assessment/invite/competition/396d0782-01d9-48d0-97ce-ff729eb555b0 #invitation for assessor:${test_mailbox_one}+david.peters@gmail.com
${Invitation_nonregistered_assessor3}     ${server}/assessment/invite/competition/9c2cc102-b934-4f54-9be8-6b864cdfc6e2 #invitation for assessor:${test_mailbox_one}+thomas.fister@gmail.com
${openCompetitionAPC}                     Low-cost propulsion mechanisms for subsonic travel

*** Test Cases ***
Non-registered assessor: Accept invitation
    [Documentation]    INFUND-228  INFUND-4145
    Given the user navigates to the page       ${Invitation_nonregistered_assessor3}
    When the user selects the radio button     acceptInvitation  true
    And The user clicks the button/link        jQuery = button:contains("Confirm")
    Then the user should see the element       jQuery = .govuk-button:contains("Create account")

User can navigate back to Become an Assessor page
    [Documentation]    INFUND-4145
    Given the user clicks the button/link     jQuery = .govuk-button:contains("Create account")
    And the user should see the element       jQuery = .govuk-heading-s:contains("Email") ~ p:contains("worth.email.test+thomas.fister@gmail.com")
    When the user clicks the button/link      jQuery = .govuk-back-link:contains("Back")
    Then the user should see the element      jQuery = h1:contains("Become an assessor for Innovate UK")

Create assessor account: Postcode lookup and save
    [Documentation]    INFUND-1478
    Given the user creates an account with postcode lookup
    When the user clicks the button/link                                            jQuery = button:contains("Continue")
    Then the user should see the element                                           jQuery = h1:contains("Your account has been created")
    And the user clicks the button/link                                            jQuery = a:contains("Sign into your account")
    And the user should be redirected to the correct page                          ${LOGGED_OUT_URL_FRAGMENT}

Create assessor account: Accepted competitions should be displayed in dashboard
    [Documentation]    INFUND-4919
    Given logging in and error checking                  &{nonregistered_assessor3_credentials}
    And the user should see the element                  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    When the user clicks the button/link                 link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then the user should see the element                 jQuery = p:contains("There are currently no assessments for you to review.")
    And the user reads his email and clicks the link     ${test_mailbox_one}+thomas.fister@gmail.com    Innovate UK assessor questionnaire    diversity survey
    [Teardown]    the user navigates to the page         ${LOGIN_URL}

The internal user invites an applicant as an assessor
    Given the comp admin logs in and navigate to invite tab     ${openCompetitionRTO_name}
    When The internal user invites a user as an assessor        Dave Adams  ${RTO_lead_applicant_credentials["email"]}
    Then the internal user send invite
    [Teardown]    Logout as user

The invited applicant accepts the invitation
    Given the user reads his email and clicks the link     ${RTO_lead_applicant_credentials["email"]}  Invitation to assess '${openCompetitionRTO_name}'  We are inviting you to assess applications
    When the user selects the radio button                 acceptInvitation  true
    And the user clicks the button/link                    css = button[type = "Submit"]
    Then the user should see the element                   jQuery = p:contains("Your email address is linked to an existing account.")

*** Keywords ***
the user should not see the validation error in the create assessor form
    [Arguments]    ${ERROR_TEXT}
    Run Keyword And Ignore Error Without Screenshots    mouse out    css = input
    Set Focus To Element                                jQuery = button:contains("Continue")
    Wait for autosave
    ${STATUS}    ${VALUE} =     Run Keyword And Ignore Error Without Screenshots    Wait Until Element Does Not Contain Without Screenshots    css = .govuk-error-message    ${ERROR_TEXT}
    Run Keyword If    '${status}' == 'FAIL'    Page Should not Contain    ${ERROR_TEXT}

the user enters the postcode and password to create account
    The user enters text to a text field                   id = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                        id = postcode-lookup
    the user selects the index from the drop-down menu     1  id=addressForm.selectedPostcodeIndex
    the user enters text to a text field                   id = password    ${correct_password}

the user should not see the error messages after entering valid values
    the user enters text to a text field                                         id = firstName    Thomas
    the user should not see the validation error in the create assessor form     ${enter_a_first_name}
    The user enters text to a text field                                         id = lastName    Fister
    the user should not see the validation error in the create assessor form     ${enter_a_last_name}
    the user enters text to a text field                                         id = phoneNumber    123123123123
    the user should not see the validation error in the create assessor form     ${enter_a_phone_number}
    the user should not see the validation error in the create assessor form     ${enter_a_phone_number_between_8_and_20_digits}
    the user enters text to a text field                                         id = password    ${correct_password}
    the user should not see the validation error in the create assessor form     Please enter your password.
    the user should not see the validation error in the create assessor form     Password must be at least 8 characters.

the comp admin logs in and navigate to invite tab
    [Arguments]  ${competition_name}
    the user logs-in in new browser     &{Comp_admin1_credentials}
    the user clicks the button/link     link = ${competition_name}
    the user clicks the button/link     jQuery = a:contains("Invite assessors to assess the competition")
    the user clicks the button/link     jQuery = a:contains("Invite")

the internal user send invite
    the user cannot see a validation error in the page
    the user clicks the button/link     jQuery = a:contains("Review and send invites")
    the user clicks the button/link     jQuery = button:contains("Send invite")

the user creates an account with postcode lookup
    the user clicks the button/link                                            jQuery = .govuk-button:contains("Create account")
    the user clicks the button/link                                            jQuery = button:contains("Continue")
    the user should not see the error messages after entering valid values
    the user clicks the button/link                                            id = postcode-lookup
    the user enters the postcode and password to create account