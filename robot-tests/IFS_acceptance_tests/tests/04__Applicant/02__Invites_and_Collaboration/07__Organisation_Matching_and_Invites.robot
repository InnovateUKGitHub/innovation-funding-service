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

    When the user invites collaborator by email address  businesslead@gmail.com  businesscollab@gmail.com
    And the user logs out if they are logged in
    And the user reads his email and clicks the link   businesscollab@gmail.com    Invitation to assess 'Sustainable living models for the future'    We are inviting you to assess applications for the competition   2

    #Then the user is able to confirm the invite
    #And sees the invite application on his dashboard


#Two lead applicants users signing up as a Research organisation with the same details can successfully invite eachother
#    Given we create a new user  ${openCompetitionRTO}  Business lead  researchlead@gmail.com  ${ACADEMIC_TYPE_ID}
#    When the user reads his email and clicks the link  academiclead@gmail.com  Please verify your email address  Once verified you can sign into your account  1
#    Given we create a new user  ${openCompetitionRTO}  Business collab  researchcollab@gmail.com  ${ACADEMIC_TYPE_ID}
#    When the user reads his email and clicks the link  academiclead@gmail.com  Please verify your email address  Once verified you can sign into your account  1

#    When Log in as a different user

*** Keywords ***
the user invites collaborator by email address
    [Arguments]    ${EXISTING_USER_EMAIL}    ${COLLAB_USER_EMAIL}
    the user clicks the button/link    link=${UNTITLED_APPLICATION_DASHBOARD_LINK}
    the user clicks the button/link           link=view and manage contributors and collaborators
    the user clicks the button/link           jQuery=a:contains("Update and add contributors from ${UNTITLED_APPLICATION_NAME}")
    the user clicks the button/link            jQuery=button:contains("Add another contributor")
    The user enters text to a text field      name=stagedInvite.name    research collab
    The user enters text to a text field       name=stagedInvite.email    ${EXISTING_USER_EMAIL}
    the user clicks the button/link            jQuery=button:contains("Invite")
