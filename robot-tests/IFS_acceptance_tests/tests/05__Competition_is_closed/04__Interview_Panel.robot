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
...
...               IFS-3566 Assessor dashboard - View of individual application
...
...               IFS-3541 Assign applications to interview panel - Edit and resend invite
...
...               IFS-3571 Interview panels - Internal user view of applications and associated feedback
...
...               IFS-5920 Acceptance tests for T's and C's
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot


*** Test Cases ***
CompAdmin can add an assessors to the invite list
    [Documentation]  IFS-2778
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link   link = ${CLOSED_COMPETITION_NAME}
    Given the user clicks the button/link      link = Manage interview panel
    When the user clicks the button/link       link = Invite assessors
    And the user clicks the button/link        link = Find
    Then the competition admin invites assessors to the competition

Cancel sending invite returns to the invite tab
    [Documentation]  IFS-2779
    [Tags]
    Given the compAdmin navigates to the send invite email page
    When the user clicks the button/link      link = Cancel
    Then the user should see the element      jQuery = td:contains("${assessor_ben}")

Assessors receives the invite to the interview panel
    [Documentation]  IFS-2779  IFS-2780
    [Tags]  HappyPath
    Given the compAdmin navigates to the send invite email page
    When the comp admin send invites to the assessors
    Then the user reads his email              ${assessor_ben_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel
    And the user reads his email               ${assessor_joel_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel

CompAdmin can add or remove the applications from the invite list
#to assign applications to interview panel
    [Documentation]  IFS-2727   IFS-3156   IFS-2635
    [Tags]  HappyPath
    [Setup]  the comp admin navigates to assign applications page
    Given the user checks for Key Statistics for submitted application
    When the competition admin selects the applications and adds them to the invite list
    Then the compadmin can remove an assessor or application from the invite list   ${crowd_source_application_name}

Competition Admin can send or cancel sending the invitation to the applicants
#competition admin send the email to applicant with application details to attend interview panel
    [Documentation]  IFS-2782  IFS-3155   IFS-2635  IFS-3251  IFS-2783  IFS-3385
    [Tags]  HappyPath
    Given the comp admin can cancel sending invites to the applicants
    When the comp admin send invites to the applicants
    Then an applicant checks for email and invite on his dashboard

CompAdmin view invite sent to the applicant and resend invite
    [Documentation]  IFS-3535  IFS-3541
    [Tags]
    [Setup]  the comp admin view invite send to an applicant
    Given the compAdmin can cancel resend inivte to an applicant
    Then the comp admin resend invite to an applicant

Assessors accept the invitation to the interview panel
    [Documentation]  IFS-3054  IFS-3055
    [Tags]  HappyPath
    Given an assessor logs in and accept the invite to interview panel
    When the user navigates to the page        ${server}/assessment/assessor/dashboard
    Then the user should not see the element   jQuery = h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    And the user should see the element        jQuery = h2:contains("Interviews you have agreed to attend") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

Assessor can respond to email invite and decline
    [Documentation]  IFS-3143
    [Tags]
    Given log in as a different user                    ${assessor_madeleine_email}   ${short_password}
    When the user reads his email and clicks the link   ${assessor_madeleine}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.  1
    Then The user should see the element                jQuery = h1:contains("Invitation to interview panel")
    And the assessor declines the interview invitation and no longer sees the competition in the dashboard

CompAdmin resends the interview panel invite
    [Documentation]  IFS-3154  IFS-3208
    [Tags]
    Given the comp admin resend invite the assessors who has been rejected initially
    Then an assessor checks for email and invite on his dashboard

CompAdmin Views the assessors that have accepted the interview panel invite
    [Documentation]  IFS-3201 IFS-3252
    [Tags]
    Given log in as a different user         &{Comp_admin1_credentials}
    When the user navigates to the page      ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/accepted
    Then the user checks for the key statistics for invite assessors
    And the user should see the element      jQuery = td:contains("${assessor_joel}") ~ td:contains("Digital manufacturing")

Applicant can see the feedback given
    [Documentation]  IFS-3291  IFS-3541
    [Tags]  HappyPath
    Given log in as a different user          ${aaron_robertson_email}  ${short_password}
    And the user should see the element      jQuery = .progress-list div:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") + div:nth-child(2) span:contains("Invited to interview")
    Then an applicant can see the feedback given by as assessor

Applicant can upload the reponse to interview panel
    [Documentation]  IFS-3253  IFS-3571
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link         link = Feedback overview
    Given the compAdmin/applicant upload feedback    css = .inputfile  ${5mb_pdf}  link = testing_5MB.pdf
    Then the compAdmin checks the status for response uploaded applicantion
    And the comp admin see the response uploaded by lead applicant

Applicant can remove the uploaded response
    [Documentation]  IFS-3253  IFS-3378
    [Tags]  HappyPath
    Given an applicant uploads response to an applicantion
    When the user clicks the button/link     css = .button-secondary  #remove
    Then the user should see the element     jQuery = p:contains("No file currently uploaded") ~ label:contains("+ Upload")
    And the compAdmin checks the status for response uploaded applicantion
    And the user should see the element      jQuery = td:contains("${computer_vision_application}") ~ td:contains("Awaiting response")

CompAdmin checks for interview panel key statistics
    [Documentation]  IFS-3524
    Given the user navigates to the page    ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}
    Then the user checks for Manage interview panel key statistics

CompAdmin can access the Allocate applications to assessors screen
    [Documentation]  IFS-3435  IFS-3436  IFS-3450
    [Tags]  HappyPath
    Given the comp admin navigates to allocate applications page
    ${applications_Assiged}=  Get text       css = .column-one span:nth-child(1)
    Then the user should see the element     link = Applications (${applications_Assiged})
    And the user should see the element      jQuery = td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")

CompAdmin allocate applications to assessor
    [Documentation]  IFS-3451  IFS-3485
    [Tags]  HappyPath
    Given the comp admin allocate applicantions to interview panel
    When the compAdmin can cancel allocating applications to assessor
    And the compAdmin removes the application from notify list
    Then the comp admin notify remaining applications to an assessor
    And the user reads his email             ${assessor_joel_email}   Applications for interview panel for '${CLOSED_COMPETITION_NAME}'   You have now been assigned applications.

Assessor can view the list of allocated applications
    [Documentation]  IFS-3534  IFS-3566
    [Tags]  HappyPath
    Given log in as a different user         ${assessor_joel_email}   ${short_password}
    When the user navigates to the page      ${SERVER}/assessment/assessor/dashboard/competition/${CLOSED_COMPETITION}/interview
    Then the user should see the element     jQuery = h1:contains("${CLOSED_COMPETITION_NAME}")
    And the user should see the element      jQuery = h3:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") ~ p:contains("Neural Industries")
    And an assessor can view feedback overview of an application    ${CLOSED_COMPETITION_APPLICATION_TITLE}  The lead applicant has responded to feedback. Download and review all attachments before the interview panel.

Assessor can view feedback with detailed finances
    [Documentation]  IFS-6561
    Given the comp admin assign an application for interview panel which was not assigned for assessment
    Then an assessor can view feedback overview of an application   ${computer_vision_application_name}   The lead applicant has not responded to feedback

CompAdmin marks appplications as successful and releases competition feedback
    [Documentation]  IFS-3542
    [Tags]  HappyPath
    Given log in as a different user          &{Comp_admin1_credentials}
    When the user navigates to the page       ${SERVER}/management/competition/${CLOSED_COMPETITION}/funding
    Then the user marks applications as successful and send funding decision email
    And the user clicks the button/link       css = button[type="submit"]  #Release feedback

Applicant can still see their feedback once the comp feedback has been released
    [Documentation]  IFS-3542
    Given log in as a different user          ${aaron_robertson_email}   ${short_password}
    When the user clicks the button/link      link = ${CLOSED_COMPETITION_APPLICATION_TITLE}
    And the user clicks the button/link       link = view application feedback
    Then the user should see the element      link = testing_5MB.pdf

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Connect to database  @{database}
    the Interview Panel is activated in the db
    execute sql string   UPDATE `${database_name}`.`competition` SET `assessor_finance_view` = 'DETAILED' WHERE `name` = '${CLOSED_COMPETITION_NAME}';
    ${today} =  get today short month
    set suite variable  ${today}

the Interview Panel is activated in the db
    Execute sql string     UPDATE `${database_name}`.`competition` SET `has_interview_stage`=1 WHERE `id`='${CLOSED_COMPETITION}';

the competition admin selects the applications and adds them to the invite list
#compadmin selecting the applications checkbox
    the user clicks the button/link    jQuery = tr:contains("${Neural_network_application}") label
    the user clicks the button/link    jQuery = tr:contains("${computer_vision_application}") label
    the user clicks the button/link    jQuery = tr:contains("${crowd_source_application_name}") label
    the user clicks the button/link    jQuery = button:contains("Add selected to invite list")
    the user should see the element    link = Review and send invites
    the user should see the element    jQuery = td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    the user should see the element    jQuery = td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    the user should see the element    jQuery = td:contains("${crowd_source_application}") + td:contains("${crowd_source_application_name}")

the compAdmin navigates to the send invite email page
    the user clicks the button/link    link = Invite
    the user clicks the button/link    link = Review and send invites
    the user should see the element    jQuery = h2:contains("Recipients") ~ p:contains("${assessor_ben}")

the assessor declines the interview invitation and no longer sees the competition in the dashboard
    the user selects the radio button    acceptInvitation  false
    the user clicks the button/link      css = .govuk-button[type = "submit"]   #Confirm
    the user should see the element      jQuery = p:contains("Thank you for letting us know you are unable to assess applications for this interview.")
    the user navigates to the page       ${server}/assessment/assessor/dashboard
    the user should not see the element  jQuery = h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

the Competition Admin should see the assigned applications in the View status tab
    the user navigates to the page        ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status     #view status tab
    the user should see the element       jQuery = td:contains("${Neural_network_application}")
    the user should see the element       jQuery = td:contains("${computer_vision_application}")

the user checks for Key Statistics for submitted application
    ${Application_in_comp}=  Get Text  css = div:nth-child(1) > div > span   #Total number of submitted applications
    the user should see the element    jQuery = div span:contains("${Application_in_comp}") ~ small:contains("Applications in competition")
    the user should see the element    jQuery = div span:contains("0") ~ small:contains("Assigned to interview panel")
    Get the total number of submitted applications
    Should Be Equal As Integers    ${NUMBER_OF_APPLICATIONS}    ${Application_in_comp}

the user checks for Key Statistics for assigned to interview panel
    ${Assigned_applications} =  Get Text  jQuery = .govuk-grid-column-one-quarter:contains("Assigned to interview panel") .govuk-heading-l   #Assigned to interview panel
    ${Application_sent} =  Get Text  jQuery = .govuk-grid-column-one-half span:nth-child(1)  #Application assigned
    Should Be Equal As Integers    ${Assigned_applications}   ${Application_sent}

the user checks for the key statistics for invite assessors
    ${Invited} =  Get Text  css = div:nth-child(1) > div > span
    ${Accepted} =  Get Text  css = div:nth-child(2) > div > span
    ${Declined} =  Get Text  css = div:nth-child(3) > div > span
    the user should see the element      jQuery = .govuk-grid-column-one-quarter:contains("${invited}") small:contains("Invited")
    the user should see the element      jQuery = .govuk-grid-column-one-quarter:contains("${accepted}") small:contains("Accepted")
    the user should see the element      jQuery = .govuk-grid-column-one-quarter:contains("${Declined}") small:contains("Declined")

the compAdmin uploads additional feedback for an application
    the user uploads the file          id = feedback[0]   ${too_large_pdf}  #checking for large file upload
    the user should get an error page  ${too_large_pdf_validation_error}
    the user goes back to the previous page
    the user uploads the file          id = feedback[0]   ${text_file}    #checking validation for worng fomrate file upload
    the user should see a field and summary error      ${wrong_filetype_validation_error}
    the compAdmin/applicant upload feedback     id = feedback[0]  ${5mb_pdf}  link = ${5mb_pdf}

the compAdmin/applicant upload feedback
    [Arguments]   ${uploadId}  ${FileToUpload}  ${uploadedFile}
    the user uploads the file          ${uploadId}  ${FileToUpload}
    the user should see the element    ${uploadedFile}

the compAdmin removes uploaded feedback for an application
    the user uploads the file          id = feedback[1]   ${5mb_pdf}
    the user should see the element    link = testing_5MB.pdf
    the user clicks the button/link    jQuery = td:contains("${computer_vision_application}") ~ td div:nth-child(2):contains("Remove")
    the user should see the element    jQuery = td:contains("${computer_vision_application}") ~ td label:contains("+ Upload")

the applicant upload the response to the interview panel
    the user uploads the file              css = .inputfile   ${5mb_pdf}
    the user should see the element        link = testing.pdf

the compAdmin checks the status for response uploaded applicantion
    log in as a different user        &{Comp_admin1_credentials}
    the user navigates to the page    ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status

the comp admin see the response uploaded by lead applicant
     the user should see the element      jQuery = td:contains("${Neural_network_application}") ~ td:contains("Responded to feedback")
     the user clicks the button/link      link = ${Neural_network_application}
     the user should see the element      jQuery = p:contains("The lead applicant has responded to feedback. Download and review all attachments before the interview panel.")

the compAdmin removes the application from notify list
    the user clicks the button/link   jQuery = td:contains("${computer_vision_application}") ~ td:contains("Remove")
    the user clicks the button/link   link = Applications allocated for interview
    the user should see the element   jQuery = td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    the user clicks the button/link   css = .govuk-button[name="addSelected"]  #Allocate

the compAdmin can cancel allocating applications to assessor
    the user clicks the button/link    link = Cancel
    the user navigates to the page     ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/unallocated-applications/${assessor_joel_id}
    the user clicks the button/link    css = .govuk-button[name="addSelected"]  #Allocate

the user checks for Manage interview panel key statistics
    ${applications_assigned} =  Get Text  css = ul li:nth-child(1) span
    ${assessor_accepted} =      Get Text  css = ul li:nth-child(3) span
    ${feedback_responded} =     Get Text  css = ul li:nth-child(2) span
    the user should see the element      jQuery = div span:contains("${feedback_responded}") ~ small:contains("Applicants responded to feedback")
    the user navigates to the page       ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/find
    ${Assigned_applications} =  Get Text  css = div:nth-child(2) > div > span    #Assigned to interview panel
    Should Be Equal As Integers   ${Assigned_applications}  ${applications_assigned}
    the user navigates to the page       ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/find
    ${Accepted} =  Get Text  css = div:nth-child(2) > div > span
    Should Be Equal As Integers   ${Accepted}  ${assessor_accepted}

the user marks applications as successful and send funding decision email
    the user selects the checkbox         select-all-1
    the user clicks the button/link       jQuery = button:contains("Successful")
    the user clicks the button/link       link = Competition
    the user clicks the button/link       link = Manage funding notifications
    the user selects the checkbox         select-all-1
    the user clicks the button/link       css = button.govuk-button.button-notification   #Assessor clicks 'Write and send emails'
    the user clicks the button/link       css = .govuk-button[data-js-modal="send-to-all-applicants-modal"]
    the user clicks the button/link       css = button[name="send-emails"]
    the user clicks the button/link       link = Competition

the compAdmin can cancel resend inivte to an applicant
    the user should see the element    jQuery = h1:contains("Resend invites to interview panel")
    the user clicks the button/link    link = Cancel
    the user navigates to the page     ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/invite/${Neural_network_application}/view
    the user clicks the button/link    link = Edit and resend invite

the comp admin send invites to the assessors
    the user should see the element         jQuery = label:contains("Subject") ~ input[value = "Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'"]
    the user enters text to a text field    css = .editor   Additional message
    the user clicks the button/link         css = button[type="submit"]   #Send invite
    the user navigates to the page          ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/pending-and-declined
    the user should see the element         jQuery = td:contains("${assessor_ben}") ~ td:contains("Awaiting response")
    the user should see the element         jQuery = td:contains("${assessor_joel}") ~ td:contains("Awaiting response")

the comp admin navigates to assign applications page
    the user clicks the button/link    link = Manage interview panel
    the user clicks the button/link       link = Competition
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=h1:contains("Closed")
    Run Keyword If  '${status}' == 'PASS'  the user moves the closed competition to panel
    the user clicks the button/link         link = Manage interview panel
    the user clicks the button/link        link = Assign applications

the comp admin can cancel sending invites to the applicants
    the user clicks the button/link     link = Invite
    the user clicks the button/link     link = Review and send invites
    the user should see the element     jQuery = td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    the user should see the element     jQuery = td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")
    the user clicks the button/link     link = Cancel

the comp admin send invites to the applicants
    the user navigates to the page        ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/invite
    the user clicks the button/link       link = Review and send invites
    the compAdmin uploads additional feedback for an application
    the compAdmin removes uploaded feedback for an application
    the user clicks the button/link       css = .govuk-button[type = "submit"]     #Send invite
    the Competition Admin should see the assigned applications in the View status tab
    the user checks for Key Statistics for assigned to interview panel

an applicant checks for email and invite on his dashboard
    the user reads his email              ${aaron_robertson_email}   Please attend an interview for an Innovate UK funding competition   Competition: Machine learning for transport infrastructure
    log in as a different user            ${aaron_robertson_email}   ${short_password}
    the user should see the element       jQuery = .progress-list div:contains("Neural networks to optimise freight train routing") ~ div span:contains("Invited to interview")

the comp admin view invite send to an applicant
    log in as a different user         &{Comp_admin1_credentials}
    the user navigates to the page     ${server}/management/assessment/interview/competition/${CLOSED_COMPETITION}/applications/view-status
    the user clicks the button/link    jQuery = td:contains("${Neural_network_application}") ~ td a:contains("View invite")
    the user should see the element    jQuery = h1:contains("Review invite email")
    the user should see the element    jQuery = td:contains("${Neural_network_application}") ~ td:contains("${5mb_pdf}")
    the user clicks the button/link    link = Edit and resend invite

the comp admin resend invite to an applicant
    the user clicks the button/link    jQuery = td:contains("${Neural_network_application}") ~ td div:nth-child(2):contains("Remove")
    the compAdmin/applicant upload feedback    css = .inputfile  ${5mb_pdf}  jQuery = div td:contains("${Neural_network_application}") ~ td:contains("testing_5MB.pdf")
    the user clicks the button/link     css = .govuk-button[type="submit"]  #Resend invite

an assessor logs in and accept the invite to interview panel
    log in as a different user           ${assessor_joel_email}   ${short_password}
    the user clicks the button/link      jQuery = h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    the user selects the radio button    acceptInvitation  true
    the user clicks the button/link      css = .govuk-button[type="submit"]   #Confirm

the comp admin resend invite the assessors who has been rejected initially
    log in as a different user          &{Comp_admin1_credentials}
    the user navigates to the page      ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/pending-and-declined
    the user should see the element     jQuery = td:contains("${assessor_madeleine}") ~ td:contains("Invite declined: ${today}")
    the user clicks the button/link     jQuery = tr:contains("${assessor_ben}") label
    the user clicks the button/link     jquery = tr:contains("${assessor_madeleine}") label    #resending the interview panel invite to an assessor that has been rejected initially
    the compAdmin resends the invites for interview panel     ${assessor_ben}   ${assessor_madeleine}
    the user should see the element     jQuery = td:contains("${assessor_ben}") ~ td:contains("Invite sent: ${today}")

an assessor checks for email and invite on his dashboard
    the user reads his email             ${assessor_ben}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.
    log in as a different user           ${assessor_madeleine_email}   ${short_password}
    the user clicks the button/link      jQuery = h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

an applicant can see the feedback given by as assessor
    the user clicks the button/link      link = ${CLOSED_COMPETITION_APPLICATION_TITLE}
    the user should see the element      jQuery = h3:contains("Additional Innovate UK feedback") ~ a:contains("testing_5MB.pdf")
    the user clicks the button/link      jQuery = a:contains("Business opportunity")
    the user should see the element      jQuery = p:contains("This is the business opportunity feedback")
    the user should see the element      jQuery = h2:contains("Average score: 8/ 10")

an applicant uploads response to an applicantion
    log in as a different user                 ${peter_styles_email}   ${short_password}
    the user clicks the button/link            link = ${computer_vision_application_name}
    the user should see the element            jQuery = .message-alert p:contains("If you are asked to respond to feedback you can upload your response below.")  #checking banner message befor uploading file.
    the compAdmin/applicant upload feedback    css = .inputfile  ${5mb_pdf}  link = testing_5MB.pdf
    the user should see the element            jQuery = .message-alert p:contains("Your response has been uploaded. This response will be noted by the interview panel.")  #checking banner message after uploading file.

the comp admin navigates to allocate applications page
    the user navigates to the page      ${SERVER}/management/assessment/interview/competition/${CLOSED_COMPETITION}/assessors/allocate-assessors
    the user should see the element     jQuery = a:contains("${assessor_joel}")
    the user should see the element     jQuery = h1:contains("${CLOSED_COMPETITION}: Machine learning for transport infrastructure")
    the user clicks the button/link     link = Allocate
    the user should see the element     jQuery = h1:contains(" Allocate applications to ${assessor_joel}")

the comp admin allocate applicantions to interview panel
    the user clicks the button/link    jQuery = tr:contains("${Neural_network_application}") label
    the user clicks the button/link      jQuery = tr:contains("${computer_vision_application}") label
    the user clicks the button/link     css = .govuk-button[name="addSelected"]  #Allocate
    the user should see the element     jQuery = td:contains("${Neural_network_application}") ~ td:contains("Remove")

the comp admin assign an application for interview panel which was not assigned for assessment
    Log in as a different user           &{Comp_admin1_credentials}
    the comp admin navigates to allocate applications page
    the user clicks the button/link      jQuery = tr:contains("${computer_vision_application}") label
    the user clicks the button/link      css = .govuk-button[name="addSelected"]
    the comp admin notify remaining applications to an assessor
    log in as a different user           ${assessor_joel_email}   ${short_password}
    the user clicks the button/link      link = ${CLOSED_COMPETITION_NAME}

the comp admin notify remaining applications to an assessor
    the user clicks the button/link     css = input[type="submit"]   #Notify
    the user should see the element     jQuery = a:contains("${CLOSED_COMPETITION_APPLICATION}")
    the user should see the element     jQuery = td:contains("${Neural_network_application}") ~ td:contains("Neural Industries") ~ td:contains("Remove")

an assessor can view feedback overview of an application
    [Arguments]   ${application}  ${message}
    the user clicks the button/link     link = ${application}
    the user should see the element     jQuery = h1:contains("Feedback overview")
    the user should see the element     jQuery = .message-alert p:contains("${message}")
    assessor should see the competition terms and conditions     Back to feedback overview

Custom suite teardown
    Disconnect from database
    The user closes the browser