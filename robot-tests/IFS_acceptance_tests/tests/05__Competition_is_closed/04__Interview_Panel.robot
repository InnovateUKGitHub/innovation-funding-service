*** Settings ***
Documentation     IFS-2637 Manage interview panel link on competition dashboard - Internal
...
...               IFS-2633 Manage interview panel dashboard - Internal
...
...               IFS-2778 Invite Assessor to Interview Panel: Find and Invite Tabs
...
...               IFS-2779 Invite Assessor to Interview Panel: Review and Send Invite
...
...               IFS-2727 Interview Panels - Assign applications 'Find' tab
...
...               IFS-3054 Assessor dashboard - Invitation to interview panel box
...
...               IFS-3055 Assessor dashboard - Attend interview panel box
...
...               IFS-3143 Interview panels - Include URL in assessor invite
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    The user closes the browser
Force Tags        CompAdmin  Assessor
Resource          ../../resources/defaultResources.robot
Resource          ../07__Assessor/Assessor_Commons.robot

*** Test Cases ***
User navigates to the Manage interview panel
    [Documentation]  IFS-2633 IFS-2637
    [Tags]
    Given the Interview Panel is activated in the db
    When the user clicks the button/link   link=${CLOSED_COMPETITION_NAME}
    Then the user clicks the button/link   link=Manage interview panel
    And the user sees the Interview panel page and the Interview links

CompAdmin can add an assessors to the invite list
    [Documentation]  IFS-2778
    Given the user clicks the button/link   link=Invite assessors
    When the user clicks the button/link    link=Find
    Then the competition admin invites assessors to the competition

Cancel sending invite returns to the invite tab
    [Documentation]  IFS-2779
    [Tags]
    [Setup]  the user clicks the button/link  link=Invite
    Given the user clicks the button/link     link=Review and send invites
    And the user should see the element       jQuery=h2:contains("Recipients") ~ p:contains("${assessor_ben}")
    When the user clicks the button/link      link=Cancel
    Then the user should see the element      jQuery=td:contains("${assessor_ben}")

Assessors receives the invite to the interview panel
    [Documentation]  IFS-2779
    [Tags]
    Given the user clicks the button/link      link=Invite
    When the user clicks the button/link       link=Review and send invites
    Then the user should see the element       jQuery=h2:contains("Recipients") ~ p:contains("${assessor_ben}")
    And the user should see the element        jQuery=label:contains("Subject") ~ input[value="Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'"]
    And the user enters text to a text field   css=.editor   Additional message
    When the user clicks the button/link       css=button[type="submit"]   #Send invite
    Then the user should see the element       link=Find
    And the user reads his email               ${assessor_ben_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel
    And the user reads his email               ${assessor_joel_email}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel

CompAdmin can add the applications to the invite list
#to assign applications to interview panel
    [Documentation]  IFS-2727
    [Setup]  the user clicks the button/link    link=Manage interview panel
    Given the user clicks the button/link       link=Competition
    ${status}   ${value}=  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery=h1:contains("Closed")
    Run Keyword If  '${status}' == 'PASS'  the user move the closed competition to in panel
    And the user clicks the button/link         link=Manage interview panel
    When the user clicks the button/link        link=Assign applications
    Then the competition admin selects the applications and add to invite list

Assessors accept the invitation to the interview panel
    [Documentation]  IFS-3054  IFS-3055
    [Tags]
    Given log in as a different user         ${assessor_joel_email}   ${short_password}
    And the user clicks the button/link      jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    When the user selects the radio button   acceptInvitation  true
    And the user clicks the button/link      css=.button[type="submit"]   #Confirm
    Then the user navigates to the page      ${server}/assessment/assessor/dashboard
    And the user should not see the element  jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
    #TODO Assesor is able to reject the invitation from email need to add once IFS-3143 done
    And the user should see the element      jQuery=h2:contains("Interviews you have agreed to attend") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

Assessor accepts the invitation to the interview panel from email link
    [Documentation]  IFS-3143
    [Tags]
    Given log in as a different user         ${assessor_madeleine_email}   ${short_password}
    Then the user reads his email and clicks the link   ${assessor_madeleine}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.  1
    When the user selects the radio button   acceptInvitation  true
    And the user confirms and checks comp is not in invitation panel
    And the user should see the element      jQuery=h2:contains("Interviews you have agreed to attend") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")

Assessor declines the invitation to the interview panel from email link
    [Documentation]  IFS-3143
    [Tags]
    Given log in as a different user         ${assessor_riley_email}   ${short_password}
    Then the user reads his email and clicks the link   ${assessor_riley}   Invitation to Innovate UK interview panel for '${CLOSED_COMPETITION_NAME}'   We are inviting you to the interview panel for the competition '${CLOSED_COMPETITION_NAME}'.  1
    When the user selects the radio button   acceptInvitation  false
    And the user confirms and checks comp is not in invitation panel
    And the user should see the element      jQuery=h1:contains("Thank you")
    And the user should see the element      jQuery=p:contains("Thank you for letting us know you are unable to assess applications for this interview.")

*** Keywords ***
the Interview Panel is activated in the db
    Connect to Database    @{database}
    Execute sql string     UPDATE `${database_name}`.`competition` SET `has_interview_stage`=1 WHERE `id`='${CLOSED_COMPETITION}';

the user sees the Interview panel page and the Interview links
    And the user should see the element    jQuery=h1:contains("Manage interview panel")
    And the user should see the element    jQuery=a:contains("Allocate applications to assessors")[aria-disabled="true"]
    #TODO The above keyword will need to be removed/updated once the Interview links are active IFS-2783

the competition admin selects the applications and add to invite list
#compadmin selecting the applications checkbox
    the user clicks the button/link    jQuery=tr:contains("${Neural_network_application}") label
    the user clicks the button/link    jQuery=tr:contains("${computer_vision_application}") label
    the user clicks the button/link    jQuery=button:contains("Add selected to invite list")
    the user should see the element    link=Review and send invites
    the user should see the element    jQuery=td:contains("${Neural_network_application}") + td:contains("${CLOSED_COMPETITION_APPLICATION_TITLE}")
    the user should see the element    jQuery=td:contains("${computer_vision_application}") + td:contains("${computer_vision_application_name}")


the user confirms and checks comp is not in invitation panel
    And The user clicks the button/link      jQuery=button:contains("Confirm")
    And the user should not see the element  jQuery=h2:contains("Invitations to interview panel") ~ ul a:contains("${CLOSED_COMPETITION_NAME}")
