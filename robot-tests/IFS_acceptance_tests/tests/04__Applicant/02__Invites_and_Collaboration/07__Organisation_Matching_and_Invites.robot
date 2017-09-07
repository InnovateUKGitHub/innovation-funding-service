*** Settings ***
Documentation     IFS-1324 IFS-1325
Suite Setup       The guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant  Email
Resource          ../../../resources/defaultResources.robot

# In those test cases we add Operating address as well. So we don't click the checkbox = use same address

*** Test Cases ***
Two lead applicants users signing up as a Business organisation with the same details can succesfully invite eachother
    [Documentation]  IFS-1324
    Given we create a new user  ${openCompetitionBusinessRTO}  Business  collab  businesscollab@gmail.com  ${BUSINESS_TYPE_ID}
    And the user logs out if they are logged in
    And we create a new user  ${openCompetitionBusinessRTO}  Business  lead  businesslead@gmail.com  ${BUSINESS_TYPE_ID}

    When the user invites collaborator by email address  businesscollab@gmail.com
    And the user changes the application name  businesscollab application
    And the user logs out if they are logged in
    And the user reads his email and clicks the link  businesscollab@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You are invited by  2

    Then the user is able to confirm the invite  businesscollab@gmail.com
    And sees the application he was invited for on his dashboard
    [Teardown]  logout as user

Researcher invites other researcher from same organisation in his application and the latter is able to accept
    [Documentation]  IFS-1325
    [Tags]
    Given New Research user applies to Competition and starts application
    And Another Research user applies to Competition and starts application
    Then the latter researcher is able to invite the first one to his application

#CV4 7AL

*** Keywords ***
the user invites collaborator by email address
    [Arguments]    ${COLLAB_USER_EMAIL}
    the user clicks the button/link       link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link       jQuery=a:contains("Update and add contributors from INNOVATE LTD")
    the user clicks the button/link       jQuery=button:contains("Add another contributor")
    The user enters text to a text field  name=stagedInvite.name  research collab
    The user enters text to a text field  name=stagedInvite.email  ${COLLAB_USER_EMAIL}
    the user clicks the button/link       jQuery=button:contains("Invite")

the user is able to confirm the invite
    [Arguments]  ${email}
    the user clicks the button/link                 jQuery=.button:contains("Continue or sign in")
    The guest user inserts user email and password  ${email}  ${correct_password}
    The guest user clicks the log-in button
    the user should see the text in the page        Confirm your organisation
    the user should see the element                 link=email the lead applicant
    the user clicks the button/link                 jQuery=.button:contains("Confirm and accept invitation")

the user changes the application name
    [Arguments]    ${application_name}
    the user navigates to the page          ${DASHBOARD_URL}
    the user clicks the button/link         link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         jQuery=a:contains("Begin application")
    the user clicks the button/link         jQuery=a:contains("Application details")
    the user enters text to a text field    id=application_details-title  ${application_name}
    the user clicks the button/link         jQuery=button:contains("Save and return to application overview")

sees the application he was invited for on his dashboard
    the user should see the element  jQuery=h1:contains("businesscollab application")

the user creates new account and organisation
    [Arguments]  ${organisationType}  ${compId}
    the user navigates to the page     ${server}/competition/${compId}/overview
    the user clicks the button/link    link=Start new application
    the user clicks the button/link    link=Create account
    the user selects the radio button  organisationTypeId  ${organisationType}
    the user clicks the button/link    css=button[type="submit"]

the user inserts the address of his research organisation
    [Arguments]  ${streetOne}  ${city}  ${postcode}
    the user enters text to a text field  organisationSearchName  Warwick
    the user clicks the button/link       css=[id="org-search"]
    the user clicks the button/link       link=University of Warwick
    the user clicks the button/link       button:contains("Enter address manually")
    the user enters text to a text field  addressForm.selectedPostcode.addressLine1  ${streetOne}
    the user enters text to a text field  addressForm.selectedPostcode.town  ${city}
    the user enters text to a text field  addressForm.selectedPostcode.postcode  ${postcode}
    the user clicks the button/link       jQuery=button:contains("Save organisation and continue")
    the user clicks the button/link       jQuery=button:contains("Save and continue")

the user verifies account and starts his application
    [Arguments]  ${email}
    the user reads his email and clicks the link  ${email}  Please verify your email address  you can sign into your account.
    the user clicks the button/link               jQuery=.button:contains("Sign in")
    logging in and error checking                 ${email}  ${correct_password}
    the user clicks the button/link               link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link               link=Begin application
    the user clicks the button/link               link=Application details
    the user enters text to a text field          application_details-title  ${email}'s Application
    the user clicks the button/link               jQuery=button:contains("Save and return to application overview")

the user navigates to the Application Team Page
    [Arguments]  ${email}
    the user navigates to the page   ${dashboard_url}
    the user clicks the button/link  ${email}'s Application
    the user clicks the button/link  link=view and manage contributors and collaborators

the user updates his organisation inviting the user
    [Arguments]  ${name}  ${email}
    the user clicks the button/link       link=Update and add contributors from University of Warwick
    the user clicks the button/link       link=Add another contributor
    the user enters text to a text field  stagedInvite.name  ${name}
    the user enters text to a text field  stagedInvite.email  ${email}
    the user clicks the button/link       css=button[name="executeStagedInvite"]

# I am on purpose not making the following lines one keyword.
# That is because i want to insert too many custom inputs, that would lead to too many arguments
New Research user applies to Competition and starts application
    the user creates new account and organisation  radio-2  ${openCompetitionRTO}
    the user inserts the address of his research organisation  p.o. box 42  coventry  cv4 7al
    the user enters text to a text field    email  bob@minions.com
    the user fills the create account form  Bob  Minion
    the user verifies account and starts his application  bob@minions.com
    logout as user

Another Research user applies to Competition and starts application
    the user creates new account and organisation  radio-2  ${openCompetitionRTO}
    the user inserts the address of his research organisation  P.O. BOX 42  Coventry  CV4 7AL
    the user enters text to a text field    email  stuart@minions.com
    the user fills the create account form  Stuart  Minion
    the user verifies account and starts his application  stuart@minions.com

The latter researcher is able to invite the first one to his application
    the user navigates to the Application Team Page  stuart@minions.com
    the user updates his organisation inviting the user  Bob  bob@minions.com
    log in as a different user
    the user reads his email and clicks the link  bob@minions.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation  2
    the user is able to confirm the invite  bob@minions.com

