*** Settings ***
Documentation   IFS-11442 OFGEM: Create a "ThirdParty" generic template
...
...             IFS-11476 OFGEM: Remove Overhead Costs section and any reference to it
...
...             IFS-11475 OFGEM: Removal of capital usage option in "Your project cost"
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${thirdPartyOfgemCompetitionName}    Thirdparty Ofgem Competition
${thirdPartyOfgemApplicationName}    Thirdparty Ofgem Application

*** Test Cases ***
Comp admin can select the funding type as Thirdparty and Competition type as Ofgem
    [Documentation]  IFS-11442
    Given the user navigates to the page            ${CA_UpcomingComp}
    When the user clicks the button/link            jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details    ${thirdPartyOfgemCompetitionName}  ${month}  ${nextyear}  Ofgem  NOT_AID  THIRDPARTY
    And the user clicks the button/link             link = Initial details
    Then the user should see the element            jQuery = dt:contains("Funding type")+dd:contains("Thirdparty")
    And the user should see the element             jQuery = dt:contains("Competition type")+dd:contains("Ofgem")

Comp admin can configure third party procurement terms and conditions
    [Documentation]  IFS-11442
    Given the user clicks the button/link                                 link = Back to competition details
    And the user clicks the button/link                                   link = Terms and conditions
    And the user completes required fields in third party competition     Innovation Fund governance document  Summary of Innovation Fund governance document   https://www.google.com
    When the user clicks the button/link                                  jQuery = button:contains("Done")
    Then the user should see the element                                  link = https://www.google.com (opens in a new window)
    And the user should see the element                                   jQuery = p:contains("This is the project costs guidance link applicants will see in the project costs section.")
    And the user verifies valid terms and conditions text is displaying   Innovation Fund governance document
    And the user clicks the button/link                                   link = Back to competition details
    And the user should see the element                                   jQuery = li:contains("Terms and conditions") .task-status-complete

Comp admin selects third party funder in funding information and completes the competition
    [Documentation]   IFS-11442
    Given comp admin creates ofgem competition
    When the user fills in funding information for the third party comp
    Then the user navigates to the page                                     ${CA_UpcomingComp}
    And the user should see the element                                     jQuery = h3 a:contains("${thirdPartyOfgemCompetitionName}")

User applies to third party ofgem competition
    [Documentation]  IFS-10083  IFS-10157
    [Setup]  get competition id and set open date to yesterday                                  ${thirdPartyOfgemCompetitionName}
    Given log in as a different user                                                            &{lead_applicant_credentials}
    And logged in user applies to competition                                                   ${thirdPartyOfgemCompetitionName}  3
    When the user fills in third-party Application details                                      ${thirdPartyOfgemApplicationName}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team                                                COMPLETE  steve.smith@empire.com
    And the applicant marks EDI question as complete
    And the lead applicant fills all the questions and marks as complete(procurement)
    Then the lead completes the questions with multiple answer choice and multiple appendices

Applicant fills in project finances without any VAT validations
    [Documentation]   IFS-10134   IFS-10273
    Given the user navigates to Your-finances page                   ${thirdPartyOfgemApplicationName}
    And the user fills the third party project costs
    When the user clicks the button/link                             css = label[for="stateAidAgreed"]
    And the user clicks the button/link                              jQuery = button:contains("Mark as complete")
    Then the user enters the project location
    And the user fills in the organisation information               ${thirdPartyOfgemApplicationName}  ${SMALL_ORGANISATION_SIZE}
    And the user fills in the funding information                    ${thirdPartyOfgemApplicationName}   no

the user submits the third party ofgem application
    [Documentation]   IFS-10083
    [Setup]  Get competitions id and set it as suite variable   ${thirdPartyOfgemCompetitionName}
    When the user clicks the button/link                        id = application-overview-submit-cta
    And the user clicks the button/link                         id = submit-application-button
    Then the user should see the element                        jQuery = h2:contains("Application submitted")
    [Teardown]  update milestone to yesterday                   ${competitionId}  SUBMISSION_DATE
#
#Invite a registered assessor
#    [Documentation]  IFS-10084
#    Given log in as a different user             &{Comp_admin1_credentials}
#    When the user clicks the button/link         link = ${thirdPartyOfgemCompetitionName}
#    And the user clicks the button/link          link = Invite assessors to assess the competition
#    And the user enters text to a text field     id = assessorNameFilter   Paul Plum
#    And the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
#    Then the user clicks the button/link         jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
#    And the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
#    And the user clicks the button/link          link = Invite
#    And the user clicks the button/link          link = Review and send invites
#    And the user enters text to a text field     id = message    This is custom text
#    And the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")
#
#Allocated assessor accepts invite to assess the competition
#    [Documentation]  IFS-10084
#    Given Log in as a different user                           &{assessor_credentials}
#    When The user clicks the button/link                       Link = ${thirdPartyOfgemCompetitionName}
#    And the user selects the radio button                      acceptInvitation  true
#    And The user clicks the button/link                        jQuery = button:contains("Confirm")
#    Then the user should be redirected to the correct page     ${server}/assessment/assessor/dashboard
#
#Comp Admin allocates assessor to application
#    [Documentation]  IFS-10084
#    Given log in as a different user                 &{Comp_admin1_credentials}
#    When The user clicks the button/link             link = Dashboard
#    And The user clicks the button/link              link = ${thirdPartyOfgemCompetitionName}
#    And The user clicks the button/link              jQuery = a:contains("Manage assessments")
#    And the user clicks the button/link              jQuery = a:contains("Allocate applications")
#    Then the user clicks the button/link             jQuery = tr:contains("${thirdPartyOfgemApplicationName}") a:contains("Assign")
#    And the user adds an assessor to application     jQuery = tr:contains("Paul Plum") :checkbox
#    And the user navigates to the page               ${server}/management/competition/${competitionId}
#    And the user clicks the button/link              jQuery = button:contains("Notify assessors")
#
#Allocated assessor assess the application
#    [Documentation]  IFS-10084
#    Given Log in as a different user                                        &{assessor_credentials}
#    And the user clicks the button/link                                     link = ${thirdPartyOfgemCompetitionName}
#    And the user clicks the button/link                                     jQuery = li:contains("${thirdPartyOfgemApplicationName}") a:contains("Accept or reject")
#    And the user selects the radio button                                   assessmentAccept  true
#    And the user clicks the button/link                                     jQuery = .govuk-button:contains("Confirm")
#    And the user should be redirected to the correct page                   ${server}/assessment/assessor/dashboard/competition/${competitionId}
#    When the user clicks the button/link                                    link = ${thirdPartyOfgemApplicationName}
#    Then the assessor submits the thirdparty assessment
#
#Comp admin closes the assessment and releases feedback
#    [Documentation]  IFS-10084
#    Given log in as a different user                        &{Comp_admin1_credentials}
#    When making the application a successful project        ${competitionId}    ${thirdPartyOfgemApplicationName}
#    And the user navigates to the page                      ${server}/management/competition/${competitionId}
#    And the user clicks the button/link                     css = button[type="submit"][formaction$="release-feedback"]
#    And log in as a different user                          &{lead_applicant_credentials}
#    And the user clicks the application tile if displayed
#    Then the user should see the element                    jQuery = li:contains("${thirdPartyOfgemApplicationName}") .status-msg:contains("Successful")
#
#Internal user can view third-party terms and conditions
#    [Documentation]  IFS-10083  IFS-10084
#    Given Requesting competition ID of this Project
#    And requesting application ID of this application
#    And Log in as a different user                          &{ifs_admin_user_credentials}
#    And The user navigates to the page                      ${server}/management/competition/${ThirdPartyCompId}/application/${ThirdPartyApplicationId}
#    When The user should see the element                    jQuery = button:contains("Strategic Innovation Fund governance document")
#    And the user clicks the button twice                    jQuery = button:contains("Strategic Innovation Fund governance document")
#    Then the user should see the element                    jQuery = th:contains("Partner") + th:contains("Strategic Innovation Fund governance document")
#    And The user should see the element                     jQuery = td:contains("Empire Ltd") ~ td:contains("Procurement Third Party")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

Requesting competition ID of this Project
    ${ThirdPartyCompId} =  get comp id from comp title    ${thirdPartyOfgemCompetitionName}
    Set suite variable   ${ThirdPartyCompId}

requesting application ID of this application
    ${ThirdPartyApplicationId} =  get application id by name   ${thirdPartyOfgemApplicationName}
    Set suite variable    ${ThirdPartyApplicationId}

comp admin creates ofgem competition
    the user fills in the CS Project eligibility                            ${BUSINESS_TYPE_ID}    2   false   single-or-collaborative
    the user fills in the CS funding eligibility                            false   Ofgem   NOT_AID
    the user selects the organisational eligibility to no                   false
    the user fills in the CS Milestones                                     PROJECT_SETUP   ${month}   ${nextyear}  No
    the user marks the Application as done                                  no   Ofgem   ${thirdPartyOfgemCompetitionName}
    the user clicks the button/link                                         link = Public content
    the user fills in the Public content and publishes                      Thirdparty Ofgem
    the user clicks the button/link                                         link = Return to setup overview

the user completes required fields in third party competition
    [Arguments]  ${title}  ${summary}  ${url}
    the user enters text to a text field        id = thirdPartyTermsAndConditionsLabel   ${title}
    the user enters text to a text field        css = .editor   ${summary}
    the user should see the element             jQuery = span:contains("Insert a link including the full URL http:// or https://")
    the user enters text to a text field        id = projectCostGuidanceLink   ${url}
    the user uploads the file                   css = .inputfile  ${valid_pdf}

the user verifies valid terms and conditions text is displaying
    [Arguments]  ${title}
    the user clicks the button/link                     jQuery = a:contains("Procurement Third Party (opens in a new window)")
    select window                                       title = ${title} - Innovation Funding Service
    the user should see the element                     jQuery = h1:contains("${title}")
    the user should see the element                     jQuery = a:contains("View ${title} (opens in a new window)")
    the user should see the element                     jQuery = p:contains("Summary of ${title}")
    [Teardown]   the user closes the last opened tab

the user fills the third party project costs
    the user clicks the button/link             link = Your project costs
    the user fills in Labour
    the user fills in Material
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs

the assessor submits the thirdparty assessment
    the user clicks the button/link                             link = Finances overview
    the user should see the element                             jQuery = h2:contains("Project cost breakdown") ~ div:contains("Total VAT")
    the user clicks the button/link                             link = Back to your assessment overview
    the assessor adds score and feedback for every question     11   # value 5: is the number of questions to loop through to submit feedback
    the user clicks the button/link                             link = Review and complete your assessment
    the user selects the radio button                           fundingConfirmation  true
    the user enters text to a text field                        id = feedback    Procurement application assessed
    the user clicks the button/link                             jQuery = .govuk-button:contains("Save assessment")
    the user clicks the button/link                             jQuery = li:contains("${thirdPartyProcurementApplicationName}") label[for^="assessmentIds"]
    the user clicks the button/link                             jQuery = .govuk-button:contains("Submit assessments")
    the user clicks the button/link                             jQuery = button:contains("Yes I want to submit the assessments")
    the user should see the element                             jQuery = li:contains("${thirdPartyProcurementApplicationName}") strong:contains("Recommended")