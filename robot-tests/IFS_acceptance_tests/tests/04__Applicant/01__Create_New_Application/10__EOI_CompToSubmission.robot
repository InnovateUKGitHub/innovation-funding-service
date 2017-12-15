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

# This suite covers creation of EOI type competition and apply to it
*** Variables ***
${comp_name}         EOI comp
${EOI_application}   EOI Application

*** Test Cases ***
Comp Admin Creates EOI type competition
    [Documentation]  IFS-2192
    [Tags]  CompAdmin  HappyPath
    Given Logging in and Error Checking                     &{Comp_admin1_credentials}
    Then The competition admin creates a EOI Comp     ${business_type_id}  ${comp_name}  EOI

Applicant applies to newly created EOI competition
    [Documentation]  IFS-2192  IFS-2196
    [Tags]  HappyPath  MySQL
    When the competition is open                                 ${comp_name}
    Then Lead Applicant applies to the new created competition   ${comp_name}

Applicant submits his application
    [Documentation]  IFS-2196
    [Tags]  HappyPath
    Given the user clicks the button/link               link=Application details
    When the user fills in the Application details      ${EOI_application}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    and the lead applicant fills all the questions and marks as complete(EOI comp type)
    and the user should not see the element             jQuery=h2:contains("Finances")
    Then the applicant submits the application

Invite a registered assessor
    log in as a different user       &{Comp_admin1_credentials}
    the user clicks the button/link      link=EOI comp
    the user clicks the button/link      link=Invite assessors to assess the competition
    the user selects the option from the drop-down menu     Smart infrastructure  id=filterInnovationArea
    the user clicks the button/link              jQuery=.button:contains("Filter")
    the user clicks the button/link      jQuery=tr:contains("Paul Plum") label[for^="assessor-row"
    the user clicks the button/link      jQuery=.button:contains("Add selected to invite list)
    the user clicks the button/link      link=Invite
    the user clicks the button/link      link=Review and send invites  # a:contains("Review and send invites")
    And the user enters text to a text field  id=message    This is custom text
    And the user clicks the button/link       jQuery=.button:contains("Send invite")
    Then the user reads his email and clicks the link   paul.plum@gmail.com@  Invitation to assess 'EOI comp'  This is custom text  1

connectToDb to update milestones
    log in as a different user       &{Comp_admin1_credentials}
    The user clicks the button/link  link=Dashboard
    The user clicks the button/link       link=EOI comp
     And The user clicks the button/link         jQuery=a:contains("Manage assessments")
      the user clicks the button/link         jQuery=a:contains("Manage applications")
     And the user clicks the button/link         jQuery=tr:nth-child(1) a:contains("View progress")
     When the user clicks the button/link        jQuery=tr:contains("Paul Plum") button:contains("Assign")
    And the user clicks the button/link         jQuery=a:contains("Manage applications")
    And the user clicks the button/link         jQuery=tr:nth-child(1) a:contains("View progress")
     And the user clicks the button/link         jQuery=a:contains("Allocate applications")
    And the user clicks the button/link         jQuery=a:contains("Manage assessments")
    And the user clicks the button/link         jQuery=a:contains("Competition")
    And the user clicks the button/link         jQuery=button:contains("Notify assessors")
    And the element should be disabled          jQuery=button:contains("Notify assessors")
    Log in as a different user          paul.plum@gmail.com
     When The user clicks the button/link                    Link=EOI comp
    And the user selects the radio button                   assessmentAccept  true
    And The user clicks the button/link                     jQuery=button:contains("Confirm")
    Then the user should be redirected to the correct page  ${Assessor_application_dashboard}
      And the user should see that the element is disabled    id=submit-assessment-button
#     assesor accepted the invite

the assessor submits the assessment
     When The user clicks the button/link    link=EOI Comp
#    And the user should see that the element is disabled    id=submit-assessment-button
     And the user clicks the button/link    jQuery=.button:contains("Review and complete your assessment")
    Then the user should see the element    jQuery=h2:contains("Review assessment")

the user adds score and feedback for every question
    The user clicks the button/link    link=Scope
    The user selects the index from the drop-down menu    1    css=.research-category
    The user clicks the button/link    jQuery=label:contains("Yes")
    The user enters text to a text field    css=.editor    Testing scope feedback text
    mouse out  css=.editor
    Wait Until Page Contains Without Screenshots    Saved!
     :FOR  ${ELEMENT}    IN    @{EOI_questions}
         \     The user clicks the button/link    link= Back to your assessment overview
         \     the user clicks the button/link    jQuery=h3:contains(${ELEMENT})
         \     The user selects the option from the drop-down menu    10    css=.assessor-question-score
         \     The user enters text to a text field    css=.editor    Testing Business opportunity feedback text
         \     mouse out  css=.editor
    The user clicks the button/link    jquery=button:contains("Save and return to assessment overview")

the comp admin closes the asssessment

the comp admin release feedback

the comp appears in Previous tab but not in Project setup


*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser

The competition admin creates a EOI Comp
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}
    the user navigates to the page   ${CA_UpcomingComp}
    the user clicks the button/link  jQuery=.button:contains("Create competition")
    the user fills in the CS Initial details  ${competition}  ${month}  ${nextyear}  ${compType_EOI}
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility  ${orgType}
    the user fills in the CS Milestones   ${month}  ${nextyear}
    the user marks the Application as done  no  ${compType_EOI}
    the user fills in the CS Assessors
    the user clicks the button/link  link=Public content
    the user fills in the Public content and publishes  ${extraKeyword}
    the user clicks the button/link   link=Return to setup overview
    the user clicks the button/link  jQuery=a:contains("Complete")
    the user clicks the button/link  jQuery=a:contains("Done")
    the user navigates to the page   ${CA_UpcomingComp}
    the user should see the element  jQuery=h2:contains("Ready to open") ~ ul a:contains("${competition}")

the lead applicant fills all the questions and marks as complete(EOI comp type)
    the lead applicant marks every question as complete   Project summary
    the lead applicant marks every question as complete   Scope
    :FOR  ${ELEMENT}    IN    @{EOI_questions}
     \     the lead applicant marks every question as complete     ${ELEMENT}

