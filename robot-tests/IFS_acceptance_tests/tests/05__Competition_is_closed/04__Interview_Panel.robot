*** Settings ***
Documentation     IFS-2637 Manage interview panel link on competition dashboard - Internal
...
...               IFS-2633 Manage interview panel dashboard - Internal
...
...               IFS-2727 Interview Panels - Assign applications 'Find' tab
...
...               IFS-2778 Invite Assessor to Interview Panel: Find and Invite Tabs
...
...               IFS-2779 Invite Assessor to Interview Panel: Review and Send Invite
...
...               IFS-2780 Invite Assessor to Interview Panel: Pending and Declined Tab
...
...               IFS-3054 Assessor dashboard - Invitation to interview panel box
...
...               IFS-3055 Assessor dashboard - Attend interview panel box
...
...               IFS-3143 Interview panels - Include URL in assessor invite
...
...               IFS-2782 Assign Applications to Interview Panel: Send Invites
...
...               IFS-3155 Assign applications to interview panel - View status tab
...
...               IFS-3156 Assign applications to interview panel - Remove application(s) from invite tab
...
...               IFS-3154 Invite Assessor to Interview Panel: Resend invite
...
...               IFS-3201 Invite Assessor to Interview Panel: Accepted tab
...
...               IFS-2635 Assign applications to interview panel dashboard - Key statistics
...
...               IFS-3251 Applicant dashboard - Assigned to interview panel box
...
...               IFS-3252 Invite Assessor to Interview Panel: Key statistics
...
...               IFS-2783 Assign Applications to Interview Panel: Add Feedback
...
...               IFS-3385 Assign applications to interview panel - Remove feedback
...
...               IFS-3291 Applicant dashboard - View application and assessment feedback
...
...               IFS-3253 Assign applications to interview panel - Applicant respond to feedback
...
...               IFS-3378 Applicant dashboard - Dynamic info banner and view of additional feedback
...
...               IFS-3435 Allocate applications to assessors - View
...
...               IFS-3436 Allocate applications to assessors - Assessor profile view
...
...               IFS-3450 Allocate applications to assessors - Applications tab
...
...               IFS-3451 Allocate applications to assessors - Notify assessors
...
...               IFS-3485 Remove applications before allocating (notifying) to assessor
...
...               IFS-3452 Allocate applications to assessors - Allocated tab
...
...               IFS-3535 Assign applications to interview panel - View of previously sent invite
...
...               IFS-3534 Assessor dashboard - List of applications
...
...               IFS-3524 Manage Interview panel - Key statistics
...
...               IFS-3542 Interview panels - View of application and feedback when competition feedback released
Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot


*** Test Cases ***
CompAdmin can add an assessors to the invite list
    [Documentation]  IFS-2778
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link   link=${CLOSED_COMPETITION_NAME}
    Given the user clicks the button/link      link=Manage interview panel
    When the user clicks the button/link       link=Invite assessors
    And the user clicks the button/link        link=Find
    Then the competition admin invites assessors to the competition

Cancel sending invite returns to the invite tab
    [Documentation]  IFS-2779
    [Tags]
    Given the compAdmin navigates to the send invite email page
    When the user clicks the button/link      link=Cancel
    Then the user should see the element      jQuery=td:contains("${assessor_ben}")

Assessors receives the invite to the interview panel
    [Documentation]  IFS-2779  IFS-2780
    [Tags]  HappyPath
    Given the compAdmin navigates to the send invite email page
    And the user should see the element        jQuery=label:contains("Subject") ~ input[value="Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'"]
    And the user enters text to a text field   css=.editor   Additional message
    When the user clicks the button/link       css=button[type="submit"]   #Send invite
    Then the user navigates to the page        ${server}/management/assessment/interview/competition/18/assessors/pending-and-declined
    And the user should see the element        jQuery=td:contains("${assessor_ben}") ~ td:contains("Awaiting response")
    And the user should see the element        jQuery=td:contains("${assessor_joel}") ~ td:contains("Awaiting response")
    And the user reads his email               ${assessor_ben_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel
    And the user reads his email               ${assessor_joel_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel

CompAdmin can add or remove the applications from the invite list
#to assign applications to interview panel
    [Documentation]  IFS-2727   IFS-3156   IFS-2635
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link    link=Manage interview panel
    Given the user clicks the button/link       link=Competition
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=h1:contains("Closed")
    Run Keyword If  '${status}' == 'PASS'  the user moves the closed competition to panel
    And the user clicks the button/link         link=Manage interview panel
    When the user clicks the button/link        link=Assign applications
    Then the user checks for Key Statistics for submitted application
    And the competition admin selects the applications and adds them to the invite list
    And the compadmin can remove an assessor or application from the invite list   ${crowd_source_application_name}

Competition Admin can send or cancel sending the invitation to the applicants
#competition admin send the email to applicant with application details to attend interview panel
    [Documentation]  IFS-2782  IFS-3155   IFS-2635  IFS-3251  IFS-2783  IFS-3385
    [Tags]  HappyPath
    Given the user clicks the button/link      link=Invite
    When the user clicks the button/link       link=Review and send invites
    Then the user should see the element       jQuery=td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    And the user should see the element        jQuery=td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    When the user clicks the button/link       link=Cancel
    Then the user navigates to the page        ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/invite
    When the user clicks the button/link       link=Review and send invites
    Then the compAdmin uploads additional feedback for an application
    And the compAdmin removes uploaded feedback for an application
    And the user clicks the button/link        css=.button[type="submit"]     #Send invite
    Then the Competition Admin should see the assigned applications in the View status tab
    And the user checks for Key Statistics for assigned to interview panel
    Then the user reads his email              ${aaron_robertson_email}   Please attend an interview for an Innovate UK funding competition   Competition: Machine learning for transport infrastructure
    When log in as a different user            ${aaron_robertson_email}   ${short_password}
    Then the user should see the element       jQuery=.progress-list div:contains("Neural networks to optimise freight train routing") ~ div span:contains("Invited to interview")

CompAdmin view invite sent to the applicant
    [Documentation]  IFS-3535
    [Tags]
    [Setup]  log in as a different user     &{Comp_admin1_credentials}
    Given the user navigates to the page    ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status
    When the user clicks the button/link    jQuery=td:contains("${Neural_network_application}") ~ td a:contains("View invite")
    Then the user should see the element    jQuery=h1:contains("Review invite email")
    And the user should see the element     jQuery=td:contains("${Neural_network_application}") ~ td:contains("testing_5MB.pdf")

Assessors accept the invitation to the interview panel
    [Documentation]  IFS-3054  IFS-3055
    [Tags]  HappyPath
    Given log in as a different user         ${assessor_joel_email}   ${short_password}
    And the user clicks the button/link      jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    When the user selects the radio button   acceptInvitation  true
    And the user clicks the button/link      css=.button[type="submit"]   #Confirm
    Then the user navigates to the page      ${server}/assessment/assessor/dashboard
    And the user should not see the element  jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    And the user should see the element      jQuery=h2:contains("Interviews you have agreed to attend") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

Assessor can respond to email invite and decline
    [Documentation]  IFS-3143
    [Tags]
    Given log in as a different user         ${assessor_madeleine_email}   ${short_password}
    When the user reads his email and clicks the link   ${assessor_madeleine}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.  1
    Then The user should see the element     jQuery=h1:contains("Invitation to interview panel")
    And the assessor declines the interview invitation and no longer sees the competition in the dashboard

CompAdmin resends the interview panel invite
    [Documentation]  IFS-3154  IFS-3208
    [Tags]
    Given log in as a different user          &{Comp_admin1_credentials}
    When the user navigates to the page       ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/pending-and-declined
    And the user should see the element       jQuery=td:contains("${assessor_madeleine}") ~ td:contains("Invite declined: ${today}")
    And the user clicks the button/link       jQuery=tr:contains("${assessor_ben}") label
    And the user clicks the button/link       jquery=tr:contains("${assessor_madeleine}") label    #resending the interview panel invite to an assessor that has been rejected initially
    When the compAdmin resends the invites for interview panel     ${assessor_ben}   ${assessor_madeleine}
    Then the user should see the element      jQuery=td:contains("${assessor_ben}") ~ td:contains("Invite sent: ${today}")
    And the user reads his email              ${assessor_ben}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.
    When log in as a different user           ${assessor_madeleine_email}   ${short_password}
    Then the user clicks the button/link      jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

CompAdmin Views the assessors that have accepted the interview panel invite
    [Documentation]  IFS-3201 IFS-3252
    [Tags]
    Given log in as a different user         &{Comp_admin1_credentials}
    When the user navigates to the page      ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/accepted
    Then the user checks for the key statistics for invite assessors
    Then the user should see the element     jQuery=td:contains("${assessor_joel}") ~ td:contains("Digital manufacturing")

Applicant can see the feedback given
    [Documentation]  IFS-3291
    [Tags]  HappyPath
    Given log in as a different user          ${aaron_robertson_email}  ${short_password}
    When the user should see the element      jQuery=.progress-list div:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") + div:nth-child(2) span:contains("Invited to interview")
    Then The user clicks the button/link      link=${CLOSED_COMPETITION_APPLICATION_TITLE}
    And the user clicks the button/link       jQuery=a:contains("Business opportunity")
    Then the user should see the element      jQuery=p:contains("This is the business opportunity feedback")
    And the user should see the element       jQuery=h2:contains("Average score: 8/ 10")

Applicant can upload the reponse to interview panel
    [Documentation]  IFS-3253
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link    link=Feedback overview
    When the applicant upload the response to the interview panel
    Then the compAdmin checks the status for response uploaded applicantion
    And the user should see the element         jQuery=td:contains("${Neural_network_application}") ~ td:contains("Responded to feedback")

Applicant can remove the uploaded response
    [Documentation]  IFS-3253  IFS-3378
    [Setup]  log in as a different user      ${peter_styles_email}   ${short_password}
    Given the user clicks the button/link    link=${computer_vision_application_name}
    And the user should see the element      jQuery=.message-alert p:contains("As the lead applicant you can respond to feedback. This response will be noted by the interview panel.")  #checking banner message befor uploading file.
    When the applicant upload the response to the interview panel
    Then the user should see the element     jQuery=.message-alert p:contains("Your response has been uploaded. This response will be noted by the interview panel.")  #checking banner message after uploading file.
    When the user clicks the button/link     css=.button-secondary  #remove
    Then the user should see the element     jQuery=p:contains("No file currently uploaded") ~ label:contains("+ Upload")
    And the compAdmin checks the status for response uploaded applicantion
    And the user should see the element      jQuery=td:contains("${computer_vision_application}") ~ td:contains("Awaiting response")

CompAdmin checks for interview panel key statistics
    [Documentation]  IFS-3524
    When the user navigates to the page    ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}
    Then the user checks for Manage interview panel key statistics

CompAdmin can access the Allocate applications to assessors screen
    [Documentation]  IFS-3435  IFS-3436  IFS-3450
    [Tags]
    When the user navigates to the page      ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/allocate-assessors
    Then the user should see the element     jQuery=a:contains("${assessor_joel}")
    And the user should see the element      jQuery=h1:contains("${CLOSED_COMPETITION}: Machine learning for transport infrastructure")
    When the user clicks the button/link     link=Allocate
    Then the user should see the element     jQuery=h1:contains(" Allocate applications to ${assessor_joel}")
    ${applications_Assiged}=  Get text       css=div:nth-child(6) div span:nth-child(1)
    And the user should see the element      link=Applications (${applications_Assiged})
    And the user should see the element      jQuery=td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")

CompAdmin allocate applications to assessor
    [Documentation]  IFS-3451  IFS-3485  IFS-3451
    [Tags]
    Given the user clicks the button/link    jQuery=tr:contains("${Neural_network_application}") label
    And the user clicks the button/link      jQuery=tr:contains("${computer_vision_application}") label
    When the user clicks the button/link     css=.button[name="addSelected"]  #Allocate
    Then the user should see the element     jQuery=td:contains("${Neural_network_application}") ~ td:contains("Remove")
    And the compAdmin can cancel allocating applications to assessor
    And the compAdmin removes the application from notify list
    When the user clicks the button/link     css=input[type="submit"]   #Notify
    And the user should see the element      jQuery=a:contains("${CLOSED_COMPETITION_APPLICATION}")
    Then the user should see the element     jQuery=td:contains("${Neural_network_application}") ~ td:contains("Neural Industries") ~ td:contains("Remove")
    And the user reads his email             ${assessor_joel_email}   Applications for interview panel for '${CLOSED_COMPETITION_NAME}'   You have now been assigned applications.

Assessor can view the list of allocated applications
    [Documentation]  IFS-3534
    [Tags]
    Given log in as a different user         ${assessor_joel_email}   ${short_password}
    When the user navigates to the page      ${SERVER}/assessment/assessor/dashboard/competition/${CLOSED_COMPETITION}/interview
    Then the user should see the element     jQuery=h1:contains("${CLOSED_COMPETITION_NAME}")
    And the user should see the element      jQuery=a:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") ~ p:contains("Neural Industries")

Assessor marks appplications as successful and releases competition feedback
    [Documentation]  IFS-3542
    [Tags]
    Given log in as a different user          &{Comp_admin1_credentials}
    When the user navigates to the page       ${SERVER}/management/competition/18/funding
    Then the user marks applications as successful and send funding decision email
    And the user clicks the button/link       css=button.button.button-large  #Release feedback

Applicant can still see their feedback once the comp feedback has been released
    [Documentation]  IFS-3542
    [Tags]
    Given log in as a different user          ${aaron_robertson_email}   ${short_password}
    When the user clicks the button/link      jQuery=section:contains("Previous") h3:contains("Neural network")
    Then the user should see the element      link=testing.pdf (opens in a new window)

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    the Interview Panel is activated in the db
    ${today} =  get today short month
    set suite variable  ${today}

the Interview Panel is activated in the db
    Connect to Database    @{database}
    Execute sql string     UPDATE `${database_name}`.`competition` SET `has_interview_stage`=1 WHERE `id`='${CLOSED_COMPETITION}';

the competition admin selects the applications and adds them to the invite list
#compadmin selecting the applications checkbox
    the user clicks the button/link    jQuery=tr:contains("${Neural_network_application}") label
    the user clicks the button/link    jQuery=tr:contains("${computer_vision_application}") label
    the user clicks the button/link    jQuery=tr:contains("${crowd_source_application_name}") label
    the user clicks the button/link    jQuery=button:contains("Add selected to invite list")
    the user should see the element    link=Review and send invites
    the user should see the element    jQuery=td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    the user should see the element    jQuery=td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    the user should see the element    jQuery=td:contains("${crowd_source_application}") + td:contains("${crowd_source_application_name}")

the compAdmin navigates to the send invite email page
    the user clicks the button/link    link=Invite
    the user clicks the button/link    link=Review and send invites
    the user should see the element    jQuery=h2:contains("Recipients") ~ p:contains("${assessor_ben}")

the assessor declines the interview invitation and no longer sees the competition in the dashboard
    the user selects the radio button    acceptInvitation  false
    the user clicks the button/link      css=.button[type="submit"]   #Confirm
    the user should see the element      jQuery=p:contains("Thank you for letting us know you are unable to assess applications for this interview.")
    the user navigates to the page       ${server}/assessment/assessor/dashboard
    the user should not see the element  jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

the Competition Admin should see the assigned applications in the View status tab
    the user navigates to the page        ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status     #view status tab
    the user should see the element       jQuery=td:contains("${Neural_network_application}")
    the user should see the element       jQuery=td:contains("${computer_vision_application}")

the user checks for Key Statistics for submitted application
    ${Application_in_comp}=  Get Text   css=div:nth-child(1) > div > span   #Total number of submitted applications
    the user should see the element    jQuery=div span:contains("${Application_in_comp}") ~ small:contains("Applications in competition")
    the user should see the element    jQuery=div span:contains("0") ~ small:contains("Assigned to interview panel")
    Get the total number of submitted applications
    Should Be Equal As Integers    ${NUMBER_OF_APPLICATIONS}    ${Application_in_comp}

the user checks for Key Statistics for assigned to interview panel
    ${Assigned_applications}=  Get Text  css=div:nth-child(2) > div > span    #Assigned to interview panel
    ${Application_sent}=  Get Text  css=div:nth-child(7) > div>:nth-child(1)  #Application assigned
    Should Be Equal As Integers    ${Assigned_applications}   ${Application_sent}

the user checks for the key statistics for invite assessors
    ${Invited}=  Get Text  css=div:nth-child(1) > div > span
    ${Accepted}=  Get Text  css=div:nth-child(2) > div > span
    ${Declined}=  Get Text  css=div:nth-child(3) > div > span
    the user should see the element     jQuery=.column-quarter:contains("${invited}") small:contains("Invited")
    the user should see the element      jQuery=.column-quarter:contains("${accepted}") small:contains("Accepted")
    the user should see the element      jQuery=.column-quarter:contains("${Declined}") small:contains("Declined")

the compAdmin uploads additional feedback for an application
    the user uploads the file          id=attachment-${Neural_network_application}   ${too_large_pdf}  #checking for large file upload
    the user should see the element    jQuery=h1:contains("Attempt to upload a large file")
    the user goes back to the previous page
    the user uploads the file          id=attachment-${Neural_network_application}    ${text_file}    #checking validation for worng fomrate file upload
    the user should see a field and summary error      Your upload must be a PDF.
    the user uploads the file          id=attachment-${Neural_network_application}   ${5mb_pdf}
    the user should see the element    link=testing_5MB.pdf

the compAdmin removes uploaded feedback for an application
    the user uploads the file          id=attachment-${computer_vision_application}   ${5mb_pdf}
    the user should see the element    link=testing_5MB.pdf
    the user clicks the button/link    jQuery=td:contains("${computer_vision_application}") ~ td div:nth-child(2):contains("Remove")
    the user should see the element    jQuery=td:contains("${computer_vision_application}") ~ td label:contains("+ Upload")

the applicant upload the response to the interview panel
    the user uploads the file              css=.inputfile   ${valid_pdf}
    the user should see the element        link=testing.pdf (opens in a new window)

the compAdmin checks the status for response uploaded applicantion
    log in as a different user        &{Comp_admin1_credentials}
    the user navigates to the page    ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status

the compAdmin removes the application from notify list
    the user clicks the button/link   jQuery=td:contains("${computer_vision_application}") ~ td:contains("Remove")
    the user clicks the button/link   link=Applications allocated for interview
    the user should see the element   jQuery=td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    the user clicks the button/link   css=.button[name="addSelected"]  #Allocate

the compAdmin can cancel allocating applications to assessor
    the user clicks the button/link    link= Cancel
    the user navigates to the page     ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/unallocated-applications/${assessor_joel_id}
    the user clicks the button/link   css=.button[name="addSelected"]  #Allocate

the user checks for Manage interview panel key statistics
    ${applications_assigned}=  Get Text  css=ul li:nth-child(1) span
    ${assessor_accepted}=      Get Text  css=ul li:nth-child(3) span
    ${feedback_responded}=     Get Text  css=ul li:nth-child(2) span
    the user should see the element      jQUery=div span:contains("${feedback_responded}") ~ small:contains("Applicants responded to feedback")
    the user should see the element      jQuery=div span:contains("${feedback_responded}") ~ small:contains("Applications responded to feedback")
    the user navigates to the page       ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/find
    ${Assigned_applications}=  Get Text  css=div:nth-child(2) > div > span    #Assigned to interview panel
    Should Be Equal As Integers   ${Assigned_applications}  ${applications_assigned}
    the user navigates to the page       ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/find
    ${Accepted}=  Get Text  css=div:nth-child(2) > div > span
    Should Be Equal As Integers   ${Accepted}  ${assessor_accepted}

the user marks applications as successful and send funding decision email
    the user selects the checkbox         select-all-1
    the user clicks the button/link       jQuery=button:contains("Successful")
    the user clicks the button/link       link=Competition
    the user clicks the button/link       link=Manage funding notifications
    the user selects the checkbox         select-all-1
    the user clicks the button/link       css=button.button.button-notification.extra-margin-top   #Assessor clicks 'Write and send emails'
    the user clicks the button/link       css=.button[data-js-modal="send-to-all-applicants-modal"]
    the user clicks the button/link       css=button[name="send-emails"]
    the user clicks the button/link       link=Competition