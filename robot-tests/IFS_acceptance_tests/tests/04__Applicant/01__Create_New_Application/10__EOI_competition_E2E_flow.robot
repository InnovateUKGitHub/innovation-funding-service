*** Settings ***
Documentation   Suite description
...
...             IFS-2192 As a Portfolio manager I am able to create an EOI competition
...
...             IFS-2196 As an applicant I am able to apply for an EOI competition
Suite Setup     custom suite setup
Suite Teardown  Close browser and delete emails
Force Tags      compAdmin  Applicant  Assessor
Resource        ../../../resources/defaultResources.robot
Resource        ../Applicant_Commons.robot
Resource        ../../02__Competition_Setup/CompAdmin_Commons.robot
Resource        ../../07__Assessor/Assessor_Commons.robot

# This suite covers End to End flow of EOI type competition i.e comp creation, applicaiotn submission , assessmnet submission, release feedback
*** Variables ***
${comp_name}         EOI comp
${EOI_application}   EOI Application

*** Test Cases ***
Comp Admin Creates EOI type competition
    [Documentation]  IFS-2192
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking               &{Comp_admin1_credentials}
    Then The competition admin creates a EOI Comp     ${business_type_id}  ${comp_name}  EOI

Applicant applies to newly created EOI competition
    [Documentation]  IFS-2192  IFS-2196
    [Tags]  HappyPath  MySQL
    When the competition is open                                 ${comp_name}
    Then Lead Applicant applies to the new created competition   ${comp_name}  &{lead_applicant_credentials}

Applicant submits his application
    [Documentation]  IFS-2196
    [Tags]  HappyPath
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details new     ${EOI_application}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    And the lead applicant fills all the questions and marks as complete(EOI comp type)
    Then the user should not see the element            jQuery=h2:contains("Finances")
    And the applicant submits the application

Invite a registered assessor
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    Given log in as a different user                          &{Comp_admin1_credentials}
    When the user clicks the button/link                      link=${comp_name}
    And the user clicks the button/link                       link=Invite assessors to assess the competition
    And the user selects the option from the drop-down menu   Smart infrastructure  id=filterInnovationArea
    And the user clicks the button/link                       jQuery=.button:contains("Filter")
    Then the user clicks the button/link                      jQuery=tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link                       jQuery=.button:contains("Add selected to invite list")
    And the user clicks the button/link                       link=Invite
    And the user clicks the button/link                       link=Review and send invites  # a:contains("Review and send invites")
    And the user enters text to a text field                  id=message    This is custom text
    And the user clicks the button/link                       jQuery=.button:contains("Send invite")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    [Setup]  Milestones are updated in database to move competition to assessment state
    Given Log in as a different user                        &{assessor_credentials}
    When The user clicks the button/link                    Link=${comp_name}
    And the user selects the radio button                   acceptInvitation  true
    And The user clicks the button/link                     jQuery=button:contains("Confirm")
    Then the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    Given log in as a different user        &{Comp_admin1_credentials}
    When The user clicks the button/link    link=Dashboard
    And The user clicks the button/link     link=EOI comp
    And The user clicks the button/link     jQuery=a:contains("Manage assessments")
    And the user clicks the button/link     jQuery=a:contains("Allocate applications")
    Then the user clicks the button/link    jQuery=tr:contains("${EOI_application}") a:contains("Assign")
    And the user clicks the button/link     jQuery=tr:contains("Paul Plum") button:contains("Assign")
    When the user navigates to the page     ${server}/management/competition/${competitionId}
    Then the user clicks the button/link    jQuery=button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    Given Log in as a different user                       &{assessor_credentials}
    When The user clicks the button/link                   link=EOI comp
    And the user clicks the button/link                    jQuery=li:contains("${EOI_application}") a:contains("Accept or reject")
    And the user selects the radio button                  assessmentAccept  true
    Then the user clicks the button/link                   jQuery=.button:contains("Confirm")
    And the user should be redirected to the correct page  ${server}/assessment/assessor/dashboard/competition/${competitionId}
    And the user clicks the button/link                    link=EOI Application
    And the assessor submits the assessment

the comp admin closes the assessment and releases feedback
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    Given log in as a different user                  &{Comp_admin1_credentials}
    When making the application a successful project  ${competitionId}  ${EOI_application}
    And moving competition to Project Setup           ${competitionId}
    Then the user should not see an error in the page

the EOI comp moves to Previous tab
    [Documentation]  IFS-2376
    [Tags]  HappyPath
    Given the user clicks the button/link  link=Dashboard
    When the user clicks the button/link   jQuery=a:contains("Previous")
    Then the user clicks the button/link   link=${comp_name}
    And the user should see the element    JQuery=h1:contains("${comp_name}")
#    TODO IFS-2471 Once implemented please update test to see the application appear in relevant section in Previous tab.

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

The competition admin creates a EOI Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}  1  # 1 means 30%
    the user fills in the CS Milestones   ${month}  ${nextyear}
    the user marks the Application as done  no  ${compType_EOI}
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  css=button[type="submit"]
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(EOI comp type)
    the lead applicant marks every question as complete   Project summary
    the lead applicant marks every question as complete   Scope
    the applicant completes application team
    the user selects Research category new  Feasibility studies
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

Milestones are updated in database to move competition to assessment state
    ${competitionId} =  get comp id from comp title  ${comp_name}
    Set suite variable  ${competitionId}
    the submission date changes in the db in the past   ${competitionId}

the assessor submits the assessment
    the assessor adds score and feedback for every question    5   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link               link=Review and complete your assessment
    the user selects the radio button             fundingConfirmation  true
    the user enters text to a text field          id=feedback    EOI application assessed
    the user clicks the button/link               jQuery=.button:contains("Save assessment")
    the user clicks the button/link               jQuery=li:contains("${EOI_application}") label[for^="assessmentIds"]
    the user clicks the button/link               jQuery=.button:contains("Submit assessments")
    the user clicks the button/link               jQuery=button:contains("Yes I want to submit the assessments")
    the user should see the element               jQuery=li:contains("EOI Application") strong:contains("Recommended")   #

