*** Settings ***
Documentation     INFUND-1188 As an assessor I want to be able to review my assessments from one place so that I can work in my favoured style when reviewing
...
...               INFUND-3723 As an Assessor looking at my competition assessment dashboard I can see details for the competition, so that I am able to reference key information as I want.
...
...               INFUND-1180 As an Assessor I want to accept or decline an assignment of an application to assess so that the competitions team can manage the assessment process.
...
...               INFUND-4128 As an assessor I want the status of pending assignments to assess to update when I accept them so that I can see what I have committed to
...
...               INFUND-3726 As an Assessor I can select one or more assessments to submit so that I can work in my preferred way
...
...               INFUND-6040 As an assessor I want to see applications sorted by status in my competition dashboard so that I can clearly see applications that are pending, open and assessed
...
...               INFUND-3724 As an Assessor and I am looking at my competition assessment dashboard, I can review the status of applications that I am allocated so that I can track my work
...
...               INFUND-3725 As an Assessor I want to see the scores that I have given for applications I have completed assessing so that I can compare all the applications I am assessing.
...
...               INFUND-4797 Handle scenario where invitation to assess an application has been removed from this user before they have responded
...
...               INFUND-5494 An assessor CAN follow a link to the competition brief from the competition dashboard
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Assessor
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot

*** Test Cases ***
User cannot accept/reject an invite to an application that has been withdrawn
    [Documentation]    INFUND-4797
    Given the user navigates to the page              ${server}/assessment/${WITHDRAWN_ASSESSMENT}/assignment
    Then the user should see the element             jQuery = h1:contains("Invitation withdrawn")
    [Teardown]    the user clicks the button/link    jQuery = #navigation a:contains(Dashboard)

Competition link should navigate to the applications
    [Documentation]    INFUND-3716 INFUND-6040 INFUND-3724 INFUND-3725 INFUND-3723
    Given The user clicks the button/link   link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then the user should see competition details
    And The order of the applications should be correct according to the status
    And the total calculation in dashboard should be correct    Applications for assessment    //div/form/div/ul/li

User can view the competition brief
    [Documentation]    INFUND-5494
    Given the user clicks the button/link           link = View competition brief (opens in a new window)
    Then the user should get a competition brief window
    [Teardown]    the user closes the competition brief

Accept an application for assessment
    [Documentation]    INFUND-1180  INFUND-4128
    Given the user should see the element                     jQuery = .in-progress li:nth-child(1):contains("Intelligent water system"):contains("Pending")
    When the user accepts the invitation
    Then the user should be redirected to the correct page    ${Assessor_application_dashboard}
    And the user should see the element                       jQuery = .in-progress li:nth-child(6):contains("Intelligent water system"):contains("Accepted")

Reject an application for assessment
    [Documentation]    INFUND-1180  INFUND-4128  INFUND-6358  INFUND-3726
    [Setup]    Log in as a different user                &{assessor_credentials}
    Given The user clicks the button/link                link = ${IN_ASSESSMENT_COMPETITION_NAME}
    And the user should see the element                  jQuery = .in-progress li:nth-child(1):contains("Park living"):contains("Pending")
    When the user rejects the invitation
    Then the application for assessment should be removed

Check the comp admin see the assessor has rejected the application
    [Documentation]  IFS-396
    [Setup]    Log in as a different user  &{Comp_admin1_credentials}
    Given the user clicks the button/link  link = ${IN_ASSESSMENT_COMPETITION_NAME}
    Then comp admin checks the assessor rejected the application for assessment

Comp admin can see the application is rejected on manage assessor page
    [Documentation]  IFS-396
    [Setup]  the user navigates to the page                     ${server}/management/assessment/competition/${IN_ASSESSMENT_COMPETITION}
    Given the user clicks the button/link                       link = Manage assessors
    When the user clicks the button/link in the paginated list  jQuery = td:contains("Paul Plum") ~ td a:contains("View progress")
    Then the user should see the element                        jQuery = td:contains("Not my area of expertise")
    And the user should see the element                         jQuery = td:contains("Unable to assess the application as i'm on holiday.")

*** Keywords ***
Custom Suite Setup
   The user logs-in in new browser  &{assessor2_credentials}
   ${status}   ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = h1:contains("Select a dashboard")
   Run Keyword If  '${status}' == 'PASS'  Run keywords   the user selects the checkbox   selectedRole1
   ...                              AND    the user clicks the button/link   css = .button[type = "submit"]   #Continue

the assessor fills all fields with valid inputs
    Select From List By Index                             id = rejectReasonValid    2
    The user should not see the element                   jQuery = .govuk-error-message:contains("Please enter a reason")
    the user enters multiple strings into a text field    id = rejectComment  a${SPACE}  102
    the user should see a field and summary error         Maximum word count exceeded. Please reduce your word count to 100.
    The user enters text to a text field                  id = rejectComment    Unable to assess the application as i'm on holiday.

the application for assessment should be removed
    the user should not see the element    link = Park living
    the user should not see the element    css = .assessment-submit-checkbox

The order of the applications should be correct according to the status
    element should contain    css = li:nth-child(1) .msg-deadline-waiting    Pending
    element should contain    css = li:nth-child(2) .msg-deadline-waiting    Pending
    element should contain    css = .progress-list li:nth-child(5) .msg-progress    Accepted
    element should contain    css = .progress-list li:nth-child(6) .msg-progress    Accepted

The user should get a competition brief window
    Select Window       title = Competition overview - ${IN_ASSESSMENT_COMPETITION_NAME} - Innovation Funding Service
    the user should not see an error in the page
    the user should see the element                  jQuery = h1:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    the user should see the element                  jQuery = .govuk-list li:contains("Competition opens")
    the user should see the element                  jQuery = .govuk-list li:contains("Competition closes")
    the user should see the element                  jQuery = .govuk-button:contains("Start new application")

The user closes the competition brief
    Close Window
    Select Window

Custom suite teardown
    The user closes the browser

the user accepts the invitation
    the user clicks the button/link       jQuery = .in-progress li:nth-child(1) a:contains("Accept or reject")
    the user should see the element       jQuery = h1:contains("Accept application")
    the user selects the radio button     assessmentAccept  true
    the user clicks the button/link       jQuery = button:contains("Confirm")

the user rejects the invitation
    the user clicks the button/link                  jQuery = .in-progress li:nth-child(1) a:contains("Accept or reject")
    the user should see the element                  jQuery = h1:contains("Accept application")
    the user should not see the element              id = rejectComment
    the user selects the radio button                assessmentAccept  false
    the user clicks the button/link                  jQuery = button:contains("Confirm")
    the user should see a field and summary error    Please enter a reason.
    the assessor fills all fields with valid inputs
    the user clicks the button/link                  jQuery = .govuk-button:contains("Confirm")

comp admin checks the assessor rejected the application for assessment
    the user clicks the button/link    jQuery = a:contains("Manage assessments")
    the user clicks the button/link    jQuery = a:contains("Manage applications")
    the user clicks the button/link    jQuery = tr:nth-child(1) a:contains("View progress")
    the user should see the element    jQuery = h2:contains("Rejected (1)")
    the user should see the element    jQuery = .assessors-rejected td:contains("Not my area of expertise")
    the user should see the element    jQuery = .assessors-rejected td:contains("Unable to assess the application as i'm on holiday.")

the user should see competition details
    the user should see the element  jQuery = dt:contains("Competition") + dd:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    the user should see the element    jQuery = dt:contains("Innovation Lead") + dd:contains("Ian Cooper")
    the user should see the element    jQuery = dt:contains("Accept applications deadline") + dd:contains("${IN_ASSESSMENT_COMPETITION_ASSESSOR_ACCEPTS_TIME_DATE_LONG}")
    the user should see the element    jQuery = dt:contains("Submit applications deadline:") + dd:contains("${IN_ASSESSMENT_COMPETITION_ASSESSOR_DEADLINE_DATE_LONG}")
    the user should see the element    jQuery = h2:contains("Applications for assessment")