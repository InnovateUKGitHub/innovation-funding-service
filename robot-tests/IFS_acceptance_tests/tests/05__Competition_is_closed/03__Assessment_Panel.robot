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

Suite Setup       Custom Suite Setup
Suite Teardown    The user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${assessment_panel}  ${server}/management/assessment/panel/competition/${CLOSED_COMPETITION}
${assessor_ben}              Benjamin Nixon
${assessor_joel}             Joel George
${assessor_madeleine}        Madeleine Martin
${assessor_riley}            Riley Butler
${panel_assessor_ben}        benjamin.nixon@gmail.com
${panel_assessor_joel}       joel.george@gmail.com
${panel_assessor_madeleine}  madeleine.martin@gmail.com
${panel_assessor_riley}      riley.butler@gmail.com

*** Test Cases ***
Assement panel link is deactivated if the assessment panel is not set
    [Documentation]  IFS-786
    [Tags]  HappyPath
    Given The user clicks the button/link  link=${CLOSED_COMPETITION_NAME}
    Then the user should see the element   jQuery=.disabled:contains("Manage assessment panel")

Assessment panel links are active if the assessment panel has been set
    [Documentation]  IFS-786
    [Tags]  HappyPath
    [Setup]  enable assessment panel for the competition
    Given the user clicks the button/link  link=Manage assessment panel
    When the user clicks the button/link   link=Invite assessors to attend
    Then the user should see the element   jQuery=h1:contains("Invite assessors to panel")

There are no Assessors in Invite and Pending and rejected tab before sending invite
    [Documentation]  IFS-1561
    [Tags]
    Given the user clicks the button/link  link=Invite
    And the user should see the element    jQuery=tr:contains("There are no assessors to be invited to this panel.")
    Then the user clicks the button/link   link=Pending and rejected
    And the user should see the element    jQuery=tr:contains("There are no assessors invited to this assessment panel.")

CompAdmin can add an assessor to invite list
    [Documentation]  IFS-31
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link  link=Find
    Given the user clicks the button/link    jQuery=tr:contains("${assessor_ben}") label
    And the user clicks the button/link      jQuery=tr:contains("${assessor_joel}") label
    And the user clicks the button/link      jquery=tr:contains("${assessor_madeleine}") label
    And the user clicks the button/link      jquery=tr:contains("${assessor_riley}") label
    When the user clicks the button/link     jQuery=button:contains("Add selected to invite list")
    Then the user should see the element     jQuery=td:contains("${assessor_ben}") + td:contains("${panel_assessor_ben}")
    And the user should see the element      jQuery=td:contains("${assessor_joel}") + td:contains("${panel_assessor_joel}")
    And the user should see the element      jQuery=td:contains("${assessor_madeleine}") + td:contains("${panel_assessor_madeleine}")
    And the user should see the element      jQuery=td:contains("${assessor_riley}") + td:contains("${panel_assessor_riley}")
    When the user clicks the button/link      link=Find
    Then the user should not see the element  jQuery=td:contains("${assessor_ben}")
    And the user should not see the element   jQuery=td:contains("${assessor_joel}")
    And the user should not see the element   jquery=tr:contains("${assessor_madeleine}")

CompAdmin can remove assessor from invite list
    [Documentation]  IFS-1565
    [Tags]   HappyPath
    Given the user clicks the button/link    link=Invite
    When the user clicks the button/link     jQuery=td:contains("${assessor_madeleine}") ~ td:contains("Remove")
    And the user clicks the button/link      link=Find
    Then the user should see the element     jQuery=tr:contains("${assessor_madeleine}")

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
    And the user should see the element       jQuery=.column-quarter:contains("3") small:contains("Assessors on invite list")
    And the user reads his email              ${panel_assessor_ben}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel
    And the user reads his email              ${panel_assessor_joel}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel

Bulk add assessor to invite list
    [Documentation]  IFS-31
    [Tags]
    [Setup]  the user clicks the button/link  link=Find
    Given the user selects the checkbox       select-all-check
    And the user clicks the button/link       jQuery=button:contains("Add selected to invite list")
    And the user should see the element       jQuery=td:contains("${assessor_madeleine}") + td:contains("${panel_assessor_madeleine}")
    When the user clicks the button/link      link=Find
    Then the user should see the element      jQuery=td:contains("No available assessors found")

CompAdmin resend invites to multiple assessors
    [Documentation]  IFS-1561
    [Tags]  HappyPath
    [Setup]  the user clicks the button/link    link=Pending and rejected
    Given the user clicks the button/link     jQuery=tr:contains("${assessor_ben}") label
    And the user clicks the button/link       jQuery=tr:contains("${assessor_joel}") label
    And the user clicks the button/link       jQuery=button:contains("Resend invites")
    And the user should see the element       jQuery=h2:contains("Recipients") ~ p:contains("${assessor_ben}")
    When the user clicks the button/link      jQuery=button:contains("Send invite")
    Then the user should see the element      jQuery=td:contains("${assessor_ben}") ~ td:contains("Invite sent: ${today}")
    And the user should see the element       jQuery=td:contains("${assessor_joel}") ~ td:contains("Invite sent: ${today}")

Assesor is able to accept the invitation from dashboard
    [Documentation]  IFS-37  IFS-1135
    [Tags]  HappyPath
    [Setup]  Log in as a different user       ${panel_assessor_ben}  ${short_password}
    Given the user clicks the button/link     jQuery=h2:contains("Invitations to attend panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    When the user selects the radio button    acceptInvitation  true
    And The user clicks the button/link       jQuery=button:contains("Confirm")
    Then the user should not see the element  jQuery=h2:contains("Invitations to attend panel")

Assesor is able to reject the invitation from email
    [Documentation]  IFS-37
    [Tags]
    [Setup]  Logout as user
    Given the user reads his email and clicks the link  ${panel_assessor_joel}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel  1
    When the user selects the radio button              acceptInvitation  false
    And The user clicks the button/link                 jQuery=button:contains("Confirm")
    And the user clicks the button/link                 link=Sign in
    And Logging in and Error Checking                   ${panel_assessor_joel}  ${short_password}
    Then the user should not see the element            jQuery=h2:contains("Invitations to attend panel")

Comp Admin can see the rejected and accepted invitation
    [Documentation]  IFS-37 IFS-1563
    [Tags]
    [Setup]  Log in as a different user        &{Comp_admin1_credentials}
    Given the user navigates to the page       ${SERVER}/management/assessment/panel/competition/${CLOSED_COMPETITION}/assessors/overview
    And the user should see the element        jQuery=td:contains("${assessor_joel}") ~ td:contains("Invite declined")
    And the user should see the element        jQuery=.column-quarter:contains(1) small:contains("Declined")
    When the user clicks the button/link       link=Accepted
    Then the user should see the element       jQuery=td:contains("${assessor_ben}") ~ td:contains("Materials, process and manufacturing design technologies")
    And the user should see the element        jQuery=.column-quarter:contains(1) small:contains("Accepted")
    And the user should see the element        jQuery=.column-quarter:contains(1) small:contains("Assessors on invite list")
    When the user clicks the button/link       link=Pending and rejected
    Then the user should not see the element   jQuery=td:contains("${assessor_ben}")

Assessor tries to accept expired invitation
    [Documentation]  IFS-2114
    [Tags]
    [Setup]   the assessment panel period changes in the db   2017-02-24 00:00:00
    When the user reads his email and clicks the link   ${panel_assessor_riley}  Invitation to assessment panel for '${CLOSED_COMPETITION_NAME}'  We are inviting you to the assessment panel  1
    Then the user should see the text in the page       This invitation is now closed
    [Teardown]  the assessment panel period changes in the db   2018-02-24 00:00:00

*** Keywords ***

Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${today} =  get today short month
    set suite variable  ${today}

the assessment panel period changes in the db
    [Arguments]  ${Date}
    Connect to Database    @{database}
    Execute sql string    UPDATE `${database_name}`.`milestone` SET `DATE`='${Date}' WHERE type='ASSESSMENT_PANEL' AND competition_id=${competition_ids["${CLOSED_COMPETITION_NAME}"]};

enable assessment panel for the competition
    the user clicks the button/link    link=View and update competition setup
    the user clicks the button/link    link=Assessors
    the user clicks the button/link    jQuery=button:contains("Edit")
    the user selects the radio button  hasAssessmentPanel  hasAssessmentPanel-0
    the user clicks the button/link    jQuery=button:contains("Done")
    the user clicks the button/link    link=Competition setup
    the user clicks the button/link    link=All competitions
    the user clicks the button/link    link=${CLOSED_COMPETITION_NAME}