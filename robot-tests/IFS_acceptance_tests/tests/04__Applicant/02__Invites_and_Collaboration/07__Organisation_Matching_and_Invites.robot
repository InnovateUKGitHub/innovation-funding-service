*** Settings ***
Documentation     IFS-1324 IFS-1325
Suite Setup       The guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant  Email
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot

*** Variables ***
# research partners
${bob}     bob@minions.com
${stuart}  stuart@minions.com
# business partners
${bCollaborator}  businesscollab@gmail.com
${bLead}          businesslead@gmail.com

*** Test Cases ***
Business invites other business from same organisation in his application and the latter is able to accept
    [Documentation]  IFS-1324
    [Tags]
    # TODO would be nice to try with custom address insertion instead of clicking checkbox same-address IFS-1550
    Given we create a new user  ${openCompetitionBusinessRTO}  Business  collab  ${bCollaborator}  ${BUSINESS_TYPE_ID}
    And the user logs out if they are logged in
    And we create a new user  ${openCompetitionBusinessRTO}  Business  lead  ${bLead}  ${BUSINESS_TYPE_ID}
    When the user invites collaborator by email address  ${bCollaborator}
    And the user changes the application name  ${bCollaborator}'s Application
    And the user logs out if they are logged in
    And the user reads his email and clicks the link  ${bCollaborator}  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You are invited by  2
    Then the user is able to confirm the invite  ${bCollaborator}  ${correct_password}
    And the user sees the application he was invited for on his dashboard  ${bCollaborator}'s Application
    [Teardown]  logout as user

# In those test cases we add Operating address as well. So we don't click the checkbox = use same address
Researcher invites other researcher from same organisation in his application and the latter is able to accept
    [Documentation]  IFS-1325
    [Tags]
    Given New Research user applies to Competition and starts application
    And Another Research user applies to Competition and starts application
    Then the latter researcher is able to invite the first one to his application

*** Keywords ***
the user invites collaborator by email address
    [Arguments]    ${COLLAB_USER_EMAIL}
    the user clicks the button/link       link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link       jQuery=a:contains("Update and add contributors from INNOVATE LTD")
    the user clicks the button/link       jQuery=button:contains("Add another contributor")
    The user enters text to a text field  name=stagedInvite.name  research collab
    The user enters text to a text field  name=stagedInvite.email  ${COLLAB_USER_EMAIL}
    the user clicks the button/link       jQuery=button:contains("Invite")

the user changes the application name
    [Arguments]    ${application_name}
    the user navigates to the page          ${DASHBOARD_URL}
    the user clicks the button/link         link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         jQuery=a:contains("Begin application")
    the user clicks the button/link         jQuery=a:contains("Application details")
    the user enters text to a text field    id=application_details-title  ${application_name}
    the user clicks the button/link         jQuery=button:contains("Save and return to application overview")

the user sees the application he was invited for on his dashboard
    [Arguments]  ${application}
    the user navigates to the page   ${dashboard_url}
    the user should see the element  jQuery=.in-progress li:contains("${application}")

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
    the user clicks the button/link       jQuery=button:contains("Enter address manually")
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
    the user clicks the button/link  link=${email}'s Application
    the user clicks the button/link  link=view and manage contributors and collaborators

the user updates his organisation inviting the user
    [Arguments]  ${name}  ${email}
    the user clicks the button/link       link=Update and add contributors from University of Warwick
    the user clicks the button/link       jQuery=.buttonlink:contains("Add another contributor")
    the user enters text to a text field  stagedInvite.name  ${name}
    the user enters text to a text field  stagedInvite.email  ${email}
    the user clicks the button/link       css=button[name="executeStagedInvite"]

# I am on purpose not making the following lines one keyword.
# That is because i want to insert too many custom inputs, that would lead to too many arguments
New Research user applies to Competition and starts application
    the user creates new account and organisation  radio-2  ${openCompetitionResearch}
    the user inserts the address of his research organisation  p.o. box 42  coventry  cv4 7al
    the user enters text to a text field    email  ${bob}
    the user fills the create account form  Bob  Minion
    the user verifies account and starts his application  ${bob}
    logout as user

Another Research user applies to Competition and starts application
    the user creates new account and organisation  radio-2  ${openCompetitionResearch}
    the user inserts the address of his research organisation  P.O. BOX 42  Coventry  CV4 7AL
    the user enters text to a text field    email  ${stuart}
    the user fills the create account form  Stuart  Minion
    the user verifies account and starts his application  ${stuart}

The latter researcher is able to invite the first one to his application
    the user navigates to the Application Team Page  ${stuart}
    the user updates his organisation inviting the user  Bob  ${bob}
    logout as user
    the user reads his email and clicks the link  ${bob}  Invitation to collaborate in ${openCompetitionResearch_name}  You will be joining as part of the organisation  2
    the user is able to confirm the invite  ${bob}  ${correct_password}
    the user sees the application he was invited for on his dashboard  ${bob}'s Application