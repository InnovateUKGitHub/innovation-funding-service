*** Settings ***
Documentation     IFS-786 Assessment panels - Manage assessment panel link on competition dashboard
...
...               IFS-31 Assessment panels - Invite assessors to panel- Find and Invite Tabs
...
...               IFS-1560 Assessment panels - Invite assessors to panel - Invite assessors
...
...               IFS-1564 Assessment panels - Invite assessors to panel - Key statistics
...
...               IFS-1561 INFUND-6453 Filter and pagination on 'Overview' tab of Invite assessors dashboard
...
...               INFUND-1985 Rename 'Overview' tab on Invite assessors dashboard to 'Pending and rejected'
...
...               IFS-1135 Assessment panels - Assessor dashboard 'Invitations to attend panel' box
...
...               IFS-37 Assessment panels - Accept/Reject Panel Invite
...
...               IFS-1563 Assessment panels - Invite assessors to panel - Accepted tab
...
...               IFS-1565 Assessment panels - Invite assessors to panel - Remove assessors from Invite list
...
...               IFS-2114 Assessment panels - Invitation expiry
...
...               IFS-25 Assessment panels - Applications list
...
...               IFS-2039 Assessment panels - Assign and remove applications to panel
...
...               IFS-2049 Assessment panels - Filter on applications list
...
...               IFS-1125 Assessment panels - Send panel applications to assessors for review
...
...               IFS-1566 Assessment panels - Assessor dashboard 'Attend panel' box
...
...               IFS-1138 Assessment panels - Competition for panel dashboard
...
...               IFS-388 Assessment panels - Accept/Reject Panel applications for review
...
...               IFS-29 Assessment panels - Assessor Review applications
...
...               IFS-2375 Assessment Panels - Assessor review application with own feedback and scores
...
...               IFS-2549 Assign assessment panel applications to assessors upon Invite acceptance
...
...               INF-2637 Manage interview panel link on competition dashboard - Internal
Suite Setup       Custom Suite Setup
Suite Teardown    Custom Tear Down
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Variables ***
${assessment_panel}  ${server}/management/assessment/panel/competition/${CLOSED_COMPETITION}

*** Test Cases ***
Assement panel link is deactivated if the assessment panel is not set
    [Documentation]  IFS-786 INF-2637
    [Tags]  HappyPath
    Given The user clicks the button/link  link=${CLOSED_COMPETITION_NAME}
    Then the user should see the element   jQuery=.disabled:contains("Manage assessment panel")
    And the user should see the element    jQuery=.disabled:contains("Manage interview panel")

Confirm changes button unavailable before sending invite
    [Documentation]  IFS-1125
    [Tags]  HappyPath
    [Setup]  enable assessment panel for the competition
    Given the user clicks the button/link  link=Manage assessment panel
    When the user should see the element   jQuery=span:contains("0") ~ small:contains("Assessors accepted")
    And the user should see the element    jQuery=span:contains("0") ~ small:contains("Applications assigned to panel")
    Then the element should be disabled    jQuery=button:contains("Confirm actions")

Assessment panel links are active if the assessment panel has been set
    [Documentation]  IFS-786
    [Tags]  HappyPath
    When the user clicks the button/link   link=Invite assessors to attend
    Then the user should see the element   jQuery=h1:contains("Invite assessors to panel")

There are no Assessors in Invite and Pending and declined tab before sending invite
    [Documentation]  IFS-1561
    [Tags]
    Given the user clicks the button/link  link=Invite
    And the user should see the element    jQuery=tr:contains("There are no assessors to be invited to this panel.")
    Then the user clicks the button/link   link=Pending and declined
    And the user should see the element    jQuery=tr:contains("There are no assessors invited to this assessment panel.")

CompAdmin can add an assessors to the invite list
    [Documentation]  IFS-31
    [Tags]  HappyPath
    When the user clicks the button/link     link=Find
    Then the competition admin invites assessors to the competition

CompAdmin can remove assessor from invite list
    [Documentation]  IFS-1565
    [Tags]   HappyPath
    When the user clicks the button/link    link=Invite
    Then the compadmin can remove an assessor or application from the invite list    ${assessor_madeleine}

Cancel sending invite returns to the invite tab
    [Documentation]  IFS-1560
    [Tags]
    [Setup]  the user clicks the button/link  link=Invite
    Given the user clicks the button/link     link=Review and send invites
    And the user should see the element       jQuery=h2:contains("Recipients") ~ p:contains("${assessor_ben}")
    When the user clicks the button/link      link=Cancel
    Then the user should see the element      jQuery=td:contains("${assessor_ben}")

Assessor recieves the invite to panel
    [Documentation]  IFS-1560  IFS-1564
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link  link=Invite
    Given the user clicks the button/link     link=Review and send invites
    When the user clicks the button/link      jQuery=button:contains("Send invite")
    Then the user should see the element      jQuery=.column-quarter:contains("3") small:contains("Invited")
    And the user reads his email              ${assessor_ben_email}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel
    And the user reads his email              ${assessor_joel_email}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel

Bulk add assessor to invite list
    [Documentation]  IFS-31
    [Tags]
    [Setup]  the user clicks the button/link  link=Find
    Given the user selects the checkbox       select-all-check
    And the user clicks the button/link       jQuery=button:contains("Add selected to invite list")
    And the user should see the element       jQuery=td:contains("${assessor_madeleine}") + td:contains("${assessor_madeleine_email}")
    When the user clicks the button/link      link=Find
    Then the user should see the element      jQuery=td:contains("No available assessors found")

CompAdmin resend invites to multiple assessors
    [Documentation]  IFS-1561
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link    link=Pending and declined
    Given the user clicks the button/link       jQuery=tr:contains("${assessor_ben}") label
    When the user clicks the button/link        jQuery=tr:contains("${assessor_joel}") label
    Then the compAdmin resends the invites for interview panel     ${assessor_ben}   ${assessor_joel}
    And the user should see the element         jQuery=td:contains("${assessor_ben}") ~ td:contains("Invite sent: ${today}")
    And the user should see the element         jQuery=td:contains("${assessor_joel}") ~ td:contains("Invite sent: ${today}")

Assesor is able to accept the invitation from dashboard
    [Documentation]  IFS-37  IFS-1135  IFS-1566  IFS-1138
    [Tags]  HappyPath
    [Setup]  Log in as a different user       ${assessor_ben_email}  ${short_password}
    Given the user clicks the button/link     jQuery=h2:contains("Invitations to attend panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    When the user selects the radio button    acceptInvitation  true
    And The user clicks the button/link       css=button[type="submit"]  # Confirm
    Then the user should not see the element  jQuery=h2:contains("Invitations to attend panel")
    When the user clicks the button/link      jQuery=h2:contains("Attend panel") + ul li h3:contains("${CLOSED_COMPETITION_NAME}")
    Then the user should see the element      jQuery=dt:contains("Competition:") ~ dd:contains("${CLOSED_COMPETITION_NAME}")
    And the user should see the element       jQuery=dt:contains("Innovation Lead:") ~ dd:contains("Ian Cooper")
    And the user should see the element       jQuery=h2:contains("Applications for panel") + ul li p:contains("No applications have been assigned to this panel.")

Assesor is able to reject the invitation from email
    [Documentation]  IFS-37
    [Tags]
    [Setup]  Logout as user
    Given the user reads his email and clicks the link  ${assessor_joel_email}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel  1
    When the user selects the radio button              acceptInvitation  false
    And The user clicks the button/link                 jQuery=button:contains("Confirm")
    And the user clicks the button/link                 link=Sign in
    And Logging in and Error Checking                   ${assessor_joel_email}  ${short_password}
    Then the user should not see the element            jQuery=h2:contains("Invitations to attend panel")

Comp Admin can see the rejected and accepted invitation
    [Documentation]  IFS-37 IFS-1563
    [Tags]
    [Setup]  Log in as a different user        &{Comp_admin1_credentials}
    Given the user navigates to the page       ${SERVER}/management/assessment/panel/competition/${CLOSED_COMPETITION}/assessors/pending-and-declined
    And the user should see the element        jQuery=td:contains("${assessor_joel}") ~ td:contains("Invite declined")
    And the user should see the element        jQuery=.column-quarter:contains(1) small:contains("Declined")
    When the user clicks the button/link       link=Accepted
    Then the user should see the element       jQuery=td:contains("${assessor_ben}") ~ td:contains("Materials, process and manufacturing design technologies")
    And the user should see the element        jQuery=.column-quarter:contains(1) small:contains("Accepted")
    When the user clicks the button/link       link=Pending and declined
    Then the user should not see the element   jQuery=td:contains("${assessor_ben}")

Assessor tries to accept expired invitation
    [Documentation]  IFS-2114
    [Tags]  MySQL
    [Setup]  get the initial milestone value
    Given we are moving the milestone to yesterday  ASSESSMENT_PANEL  ${CLOSED_COMPETITION}
    When the user reads his email and clicks the link  ${assessor_riley_email}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel  1
    Then the user should see the text in the page      This invitation is now closed
    [Teardown]  we are moving the milestone to tomorrow  ASSESSMENT_PANEL  ${CLOSED_COMPETITION}

Assign application link decativated if competition is in close state
    [Documentation]   IFS-25
    [Tags]
    [Setup]  Log in as a different user   &{Comp_admin1_credentials}
    Given the user navigates to the page  ${server}/management/assessment/panel/competition/${CLOSED_COMPETITION}
    Then the user should see the element  jQuery=.disabled:contains("Assign applications to panel")

Assign application link activate if competition is in panel state
    [Documentation]   IFS-25
    [Tags]
    [Setup]  the user clicks the button/link     link=Competition
    Given the user moves the closed competition to panel
    And the user should see the element    jQuery=h1:contains("Panel")
    When the user clicks the button/link   link=Manage assessment panel
    And the user clicks the button/link    link=Assign applications to panel
    Then the user should see the element   jQuery=h1:contains("Assign applications to panel")

Manage Assessment Panel Assign and remove button functionality
    [Documentation]   IFS-2039
    [Tags]
    When the user clicks the button/link    jQuery=td:contains("${Neural_network_application}") ~ td:contains("Assign")
    Then the user should see the element    jQuery=h2:contains("Assigned applications (1)")
    When the user clicks the button/link    jQuery=td:contains("${Neural_network_application}") ~ td:contains("Remove")
    Then the user should see the element    jQuery=h2:contains("Assigned applications (0)")
    And the user should see the element     jQuery=td:contains("${Neural_network_application}") ~ td:contains("Assign")

Filter by application number
    [Documentation]  IFS-2049
    [Tags]
    Given the user enters text to a text field     id=filterSearch   ${Neural_network_application}
    When the user clicks the button/link           jQuery=.button:contains("Filter")
    Then the user should see the element           jQuery=td:contains("${Neural_network_application}")
    And the user should not see the element        jQuery=td:contains("${computer_vision_application}")
    When the user clicks the button/link           jQuery=a:contains("Clear")
    Then the user should see the element           jQuery=td:contains("${computer_vision_application}")

Assign applications to panel
    [Documentation]  IFS-1125
    [Tags]
    When the user clicks the button/link    jQuery=td:contains("${Neural_network_application}") ~ td:contains("Assign")
    And the user clicks the button/link     jQuery=td:contains("${computer_vision_application_name}") ~ td:contains("Assign")
    Then the user should see the element    jQuery=h2:contains("Assigned applications (2)")
    When the user clicks the button/link    link=Manage assessment panel
    And the user clicks the button/link     jQuery=button:contains("Confirm actions")
    And the user reads his email            ${assessor_ben}  Applications ready for review  You have been allocated applications to review within the competition ${CLOSED_COMPETITION_NAME}.

Assign applications to assessor upon accepting invite in panel
    [Documentation]   IFS-2549
    [Tags]
    # When subsequently an assessor is invited, assign application without clicking on 'Confirm action'
    Given the user clicks the button/link     link=Invite assessors to attend
    And the user clicks the button/link       link=Invite
    When the user clicks the button/link      link=Review and send invites
    Then the user clicks the button/link      jQuery=button:contains("Send invite")
    When log in as a different user           ${assessor_madeleine_email}  ${short_password}
    Then the user clicks the button/link      jQuery=h2:contains("Invitations to attend panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    And the user selects the radio button     acceptInvitation  true
    And the user clicks the button/link       css=button[type="submit"]  # Confirm

Assessors view of competition dashboard and applications in panel status
    [Documentation]  IFS-1138  IFS-388
    [Tags]
    Given Log in as a different user            ${assessor_ben_email}  ${short_password}
    When the user clicks the button/link        jQuery=h2:contains("Attend panel") + ul li h3:contains("${CLOSED_COMPETITION_NAME}")
    Then the user should see the element        jQuery=h2:contains("Applications for panel") + ul li h3:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    When the user clicks the button/link        JQuery=.progress-list div:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") ~ div a:contains("Accept or reject")
    Then the user selects the radio button      reviewAccept  false
    And the user should see the text in the element     reject-application  Use this space to tell us why.
    When the user enters text to a text field   id=rejectComment   Conflict of interest
    And the user clicks the button/link         jQuery=button:contains("Confirm")
    Then the user should not see the element    jQuery=h2:contains("Applications for panel") + ul li h3:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    When the user clicks the button/link        jQuery=.progress-list div:contains("${computer_vision_application_name}") ~ div a:contains("Accept or reject")
    And the user selects the radio button       reviewAccept  true
    Then The user should see the text in the element    accept-application    You will still have the option to reject after accepting and viewing the full application.
    When the user clicks the button/link        jQuery=button:contains("Confirm")
    Then the user should see the element        jQuery=.progress-list div:contains("${computer_vision_application_name}") ~ div strong:contains("Accepted")

Assessor can attend Panel and see applications he has not assessed
    [Documentation]  IFS-29   IFS-2375   IFS-2549
    [Tags]
    # assessor view of application summary when he has not assessed application at first place.
    Given the user clicks the button/link       link=${computer_vision_application_name}
    When the user should see the element        jQuery=h1 span:contains("${computer_vision_application_name}")
    And the user should see the element         jQuery=h1:contains("Application summary")
    Then the user expands the section           Business opportunity
    And the user should not see the element     jQuery=span:contains("Question score")
    And the user should not see the element     jQuery=label:contains("Feedback")

Assessor can attend Panel and see applications that he has assessed
    [Documentation]  IFS-29   IFS-2375   IFS-2549
    [Tags]
    [Setup]  log in as a different user         ${assessor_madeleine_email}  ${short_password}
    Given the user clicks the button/link       jQuery=h2:contains("Attend panel") + ul li h3:contains("${CLOSED_COMPETITION_NAME}")
    Then the user clicks the button/link        JQuery=.progress-list div:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}") ~ div a:contains("Accept or reject")
    And the user selects the radio button       reviewAccept  true
    And the user clicks the button/link         css=button[type="submit"]  # Confirm
    When the user clicks the button/link        link=${CLOSED_COMPETITION_APPLICATION_TITLE}
    And the user clicks the button/link         jQuery=button:contains("Business opportunity")
    Then the user should see the element        jQuery=p:contains("This is the business opportunity feedback")
    And the user should see the element         jQuery=div:contains("Score") span:contains(8)

Assessor cannot see competition on dashboard after funders panel date expiry
    [Documentation]   IFS-1138
    [Tags]  MySQL
    ${fundersPanel} =  Get the proper milestone value from the db  FUNDERS_PANEL
    Given we are moving the milestone to yesterday  FUNDERS_PANEL  ${CLOSED_COMPETITION}
    When the user clicks the button/link      link=Dashboard
    Then the user should not see the element  jQuery=h2:contains("Attend panel") + ul li h3:contains("${CLOSED_COMPETITION_NAME}")
    [Teardown]  return back to original milestone  FUNDERS_PANEL  ${fundersPanel}  ${CLOSED_COMPETITION}

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${today} =  get today short month
    set suite variable  ${today}
    get the initial milestone value

Custom Tear Down
    return back to original milestone  FUNDERS_PANEL  ${assessmentPanelDate}  ${CLOSED_COMPETITION}
    the user closes the browser

Get the proper milestone value from the db
    [Arguments]  ${type}
    Connect to Database    @{database}
    ${result} =  Query  SELECT DATE_FORMAT(`date`, '%Y-%l-%d %H:%i:%s') FROM `${database_name}`.`milestone` WHERE `competition_id`='${CLOSED_COMPETITION}' AND type='${type}';
    ${result} =  get from list  ${result}  0
    ${milestone} =  get from list  ${result}  0
    [Return]  ${milestone}

return back to original milestone
    [Arguments]  ${type}  ${date}  ${competitionId}
    Connect to Database    @{database}
    Execute sql string     UPDATE `${database_name}`.`milestone` SET `DATE`='${date}' WHERE `type`='${type}' AND `competition_id`='${competitionId}'

we are moving the milestone to yesterday
    [Arguments]  ${type}  ${competitionId}
    ${date} =  get yesterday
    update the database  ${date}  ${type}  ${competitionId}

we are moving the milestone to tomorrow
    [Arguments]  ${type}  ${competitionId}
    ${date} =  get tomorrow
    update the database  ${date}  ${type}  ${competitionId}

update the database
    [Arguments]  ${date}  ${type}  ${competitionId}
    Connect to Database    @{database}
    Execute sql string     UPDATE `${database_name}`.`milestone` SET `DATE`='${date}' WHERE `type`='${type}' AND `competition_id`='${competitionId}'

get the initial milestone value
    ${assessmentPanelDate} =  Get the proper milestone value from the db  ASSESSMENT_PANEL
    set suite variable  ${assessmentPanelDate}

enable assessment panel for the competition
    the user clicks the button/link    link=View and update competition setup
    the user clicks the button/link    link=Assessors
    the user clicks the button/link    jQuery=button:contains("Edit")
    the user selects the radio button  hasAssessmentPanel  hasAssessmentPanel-0
    the user clicks the button/link    jQuery=button:contains("Done")
    the user clicks the button/link    link=Competition setup
    the user clicks the button/link    link=All competitions
    the user clicks the button/link    link=${CLOSED_COMPETITION_NAME}