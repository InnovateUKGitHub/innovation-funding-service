*** Settings ***
Documentation     IFS-1324 IFS-1325
Suite Setup       The guest user opens the browser
Suite Teardown    Close browser and delete emails
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot

*** Test Cases ***
Two lead applicants users signing up as a Business organisation with the same details can succesfully invite eachother
    Given we create a new user  ${openCompetitionBusinessRTO}  Business  collab  businesscollab@gmail.com  ${BUSINESS_TYPE_ID}
    And the user logs out if they are logged in
    And we create a new user  ${openCompetitionBusinessRTO}  Business  lead  businesslead@gmail.com  ${BUSINESS_TYPE_ID}

    When the user invites collaborator by email address  businesscollab@gmail.com
    And the user changes the application name  businesscollab application
    And the user logs out if they are logged in
    And the user reads his email and clicks the link  businesscollab@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You are invited by  2

    Then the user is able to confirm the invite
    And sees the application he was invited for on his dashboard


#Two lead applicants users signing up as a Research organisation with the same details can successfully invite eachother
#    Given we create a new user  ${openCompetitionRTO}  Business lead  researchlead@gmail.com  ${ACADEMIC_TYPE_ID}
#    When the user reads his email and clicks the link  academiclead@gmail.com  Please verify your email address  Once verified you can sign into your account  1
#    Given we create a new user  ${openCompetitionRTO}  Business collab  researchcollab@gmail.com  ${ACADEMIC_TYPE_ID}
#    When the user reads his email and clicks the link  academiclead@gmail.com  Please verify your email address  Once verified you can sign into your account  1

#    When Log in as a different user

*** Keywords ***
the user invites collaborator by email address
    [Arguments]    ${COLLAB_USER_EMAIL}
    the user clicks the button/link         link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         jQuery=a:contains("Update and add contributors from INNOVATE LTD")
    the user clicks the button/link         jQuery=button:contains("Add another contributor")
    The user enters text to a text field    name=stagedInvite.name    research collab
    The user enters text to a text field    name=stagedInvite.email    ${COLLAB_USER_EMAIL}
    the user clicks the button/link         jQuery=button:contains("Invite")

the user is able to confirm the invite
    the user clicks the button/link                    jQuery=.button:contains("Continue or sign in")
    The guest user inserts user email and password     businesscollab@gmail.com  Passw0rd123
    The guest user clicks the log-in button
    the user should see the text in the page           Confirm your organisation
    the user should see the element                    link=email the lead applicant
    the user clicks the button/link                    jQuery=.button:contains("Confirm and accept invitation")

the user changes the application name
    [Arguments]    ${application_name}
    the user navigates to the page          ${DASHBOARD_URL}
    the user clicks the button/link         link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link         jQuery=a:contains('Begin application')
    the user clicks the button/link         jQuery=a:contains('Application details')
    the user enters text to a text field    id=application_details-title  ${application_name}
    the user clicks the button/link         jQuery=button:contains('Save and return to application overview')

sees the application he was invited for on his dashboard
    the user should see the text in the page  businesscollab application

