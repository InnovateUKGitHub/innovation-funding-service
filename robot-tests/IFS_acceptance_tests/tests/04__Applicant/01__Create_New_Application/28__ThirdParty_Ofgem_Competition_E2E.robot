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
${thirdPartyOfgemCompetitionName}    Thirdparty Competition - Ofgem
${thirdPartyOfgemApplicationName}    Thirdparty Application - Ofgem

*** Test Cases ***
Comp admin can select the funding type as Thirdparty and Competition type as Ofgem
    [Documentation]  IFS-11442
    Given the user navigates to the page            ${CA_UpcomingComp}
    When the user clicks the button/link            jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details    ${thirdPartyOfgemCompetitionName}  ${month}  ${nextyear}  Ofgem  STATE_AID  THIRDPARTY
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
    [Documentation]  IFS-11575  IFS-11476
    [Setup]  get competition id and set open date to yesterday                          ${thirdPartyOfgemCompetitionName}
    Given log in as a different user                                                    &{lead_applicant_credentials}
    And logged in user applies to competition                                           ${thirdPartyOfgemCompetitionName}  3
    And the user clicks the button/link                                                 link = Application details
    When the user fills in the Application details                                      ${thirdPartyOfgemApplicationName}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team                                        COMPLETE  steve.smith@empire.com
    Then the lead applicant fills all the questions and marks as complete(thirdparty)

Applicant should not view overhead and capital usage costs in project costs
    [Documentation]   IFS-11475  IFS-11476
    Given the user navigates to Your-finances page   ${thirdPartyOfgemApplicationName}
    When the user clicks the button/link             link = Your project costs
    Then the user should not see the element         jQuery = button:contains("Overhead costs")
    And the user should not see the element          jQuery = button:contains("Capital usage")

the user submits the third party ofgem application
    [Documentation]   IFS-11475  IFS-11476  IFS-11480
    [Setup]  Get competitions id and set it as suite variable   ${thirdPartyOfgemCompetitionName}
    Given the user completes thirdparty ofgem project finances
    And the user clicks the button/link                         link = Back to application overview
    And the user accept the thirdpary terms and conditions      Back to application overview
    When the user clicks the button/link                        id = application-overview-submit-cta
    And the user clicks the button/link                         id = accordion-questions-heading-3-1
    And the user should see the element                         jQuery = th:contains("Other funding (£)")
    And the user clicks the button/link                         id = submit-application-button
    Then the user should see the element                        jQuery = h2:contains("Application submitted")
    [Teardown]  update milestone to yesterday                   ${competitionId}  SUBMISSION_DATE

the applicant should not view overhead and capital usage costs in application summary
     [Documentation]   IFS-11475  IFS-11476
     Given the user clicks the button/link  link = View application
     When the user clicks the button/link   jQuery = button:contains("Finances summary")
     Then the user should not see the element   jQuery = th:contains("Overheads (£)")
     And the user should not see the element   jQuery = th:contains("Capital usage (£)")

Internal user should not view overhead and capital usage costs in application summary
    [Documentation]  IFS-11475  IFS-11476
    [Setup]  Requesting competition and application ID of this Project
    Given log in as a different user            &{Comp_admin1_credentials}
    When the user navigates to the page         ${server}/management/competition/${ThirdPartyCompId}/application/${ThirdPartyApplicationId}
    Then the user should not see the element    jQuery = th:contains("Overheads (£)")
    And the user should not see the element     jQuery = th:contains("Capital usage (£)")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

Requesting competition and application ID of this Project
    ${ThirdPartyCompId} =  get comp id from comp title    ${thirdPartyOfgemCompetitionName}
    Set suite variable   ${ThirdPartyCompId}
    ${ThirdPartyApplicationId} =  get application id by name   ${thirdPartyOfgemApplicationName}
    Set suite variable    ${ThirdPartyApplicationId}

comp admin creates ofgem competition
    the user fills in the CS Project eligibility            ${BUSINESS_TYPE_ID}    2   false   single-or-collaborative
    the user fills in the CS funding eligibility            false   Ofgem   STATE_AID
    the user selects the organisational eligibility to no   false
    the user fills in the CS Milestones                     PROJECT_SETUP   ${month}   ${nextyear}  No
    the user marks the Application as done                  no   Ofgem   ${thirdPartyOfgemCompetitionName}
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      Thirdparty Ofgem
    the user clicks the button/link                         link = Return to setup overview

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

the user completes thirdparty ofgem project finances
    the user fills the third party project costs
    the user clicks the button/link                    css = label[for="stateAidAgreed"]
    the user clicks the button/link                    jQuery = button:contains("Mark as complete")
    the user enters the project location
    the user fills in the organisation information     ${thirdPartyOfgemApplicationName}  ${SMALL_ORGANISATION_SIZE}
    the user fills thirdparty funding information      ${thirdPartyOfgemApplicationName}

the user fills thirdparty funding information
    [Arguments]  ${Application}
    the user navigates to Your-finances page                ${Application}
    the user selects funding section in project finances
    the user should see the element                         jQuery = span:contains("Have you received any aligned or third party funding for this project?")
    the user selects the radio button                       requestingFunding   true
    the user enters text to a text field                    css = [name^="grantClaimPercentage"]  10
    the user selects the radio button                       otherFunding   true
    the user enters text to a text field                    css = [name*=source]  Lottery funding
    the user enters text to a text field                    css = [name*=date]  12-${nextyear}
    the user enters text to a text field                    css = [name*=fundingAmount]  20000
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")

the user accept the thirdpary terms and conditions
    [Arguments]  ${returnLink}
    the user clicks the button/link    link = Innovation Fund governance document
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Innovation Fund governance document accepted")
    the user clicks the button/link    link = ${returnLink}