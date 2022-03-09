*** Settings ***
Documentation    IFS-10080 Third party procurement: Comp setup configuration
...
...              IFS-10081 Third party procurement: Guidance document configuration
...
...              IFS-10082 Third party procurement: guidance document link page
...
...              IFS-10083 Third party procurement: supporting documents in application
...
...              IFS-10134 Ofgem programme: no VAT
...
...              IFS-10169 Third party procurement: Read-only costs guidance link
...
...              IFS-10151 Include the link to the T&C on the T&C tickbox label
...
...              IFS-10273  Ofgem - Hide Cost Cateogory - Capital usage
...
Suite Setup       Custom suite setup
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${thirdPartyProcurementCompetitionName}    Third party procurement competition
${thirdPartyProcurementApplicationName}    Third party procurement application

*** Test Cases ***
Third party procurement terms and conditions validations
    [Documentation]  IFS-10081  IFS-10082
    Given the user navigates to the page                    ${CA_UpcomingComp}
    And the user clicks the button/link                     jQuery = .govuk-button:contains("Create competition")
    And the user fills in the CS Initial details            ${thirdPartyProcurementCompetitionName}  ${month}  ${nextyear}  Programme  NOT_AID  PROCUREMENT
    When the user clicks the button/link                    link = Terms and conditions
    And the user clicks the button twice                    jQuery = label:contains("Procurement Third Party")
    And the user clicks the button/link                     jQuery = button:contains("Done")
    Then the user should see third party t&c validations

Comp admin can configure third party procurement terms and conditions
    [Documentation]  IFS-10081  IFS-10082  IFS-10169
    Given the user completes required fields in third party procurement competition     Innovation Fund governance document  Summary of Innovation Fund governance document   https://www.google.com
    When the user clicks the button/link                                                jQuery = button:contains("Done")
    Then the user should see the element                                                link = https://www.google.com (opens in a new window)
    And the user should see the element                                                 jQuery = p:contains("This is the project costs guidance link applicants will see in the project costs section.")
    And the user verifies valid terms and conditions text is displaying                 Innovation Fund governance document

Comp admin can edit the third party procurement terms and conditions
    [Documentation]  IFS-10081  IFS-10082  IFS-10169
    Given the user clicks the button/link                                             jQuery = button:contains("Edit")
    And the user completes required fields in third party procurement competition     Strategic Innovation Fund governance document  Summary of Strategic Innovation Fund governance document   https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/small-business-research-initiative-sbri-project-costs-guidance
    When the user clicks the button/link                                              jQuery = button:contains("Done")
    Then the user should see the element                                              link = https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/small-business-research-initiative-sbri-project-costs-guidance (opens in a new window)
    And the user verifies valid terms and conditions text is displaying               Strategic Innovation Fund governance document
    And the user clicks the button/link                                               link = Back to competition details
    And the user should see the element                                               jQuery = li:contains("Terms and conditions") .task-status-complete

Comp admin selects third party funder in funding information
    [Documentation]   IFS-10080
    Given comp admin creates procurement competition
    When the user fills in funding information for the third party comp
    Then the user navigates to the page                                                ${CA_UpcomingComp}
    And the user should see the element                                                jQuery = h3 a:contains("Third party procurement competition")

User applies to third party competition
    [Documentation]  IFS-10083  IFS-10157
    [Setup]  get competition id and set open date to yesterday                                  ${thirdPartyProcurementCompetitionName}
    Given log in as a different user                                                            &{lead_applicant_credentials}
    And logged in user applies to competition                                                   ${thirdPartyProcurementCompetitionName}  3
    When the user fills in third-party Application details                                      ${thirdPartyProcurementApplicationName}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team                                                COMPLETE  steve.smith@empire.com
    #And the applicant marks EDI question as complete
    And the lead applicant fills all the questions and marks as complete(procurement)
    Then the lead completes the questions with multiple answer choice and multiple appendices
    And the third party applicant can view the strategic innovation terms and conditions        Strategic Innovation Fund governance document

Applicant fills in project finances without any VAT validations
    [Documentation]   IFS-10134   IFS-10273
    Given the user navigates to Your-finances page                   ${thirdPartyProcurementApplicationName}
    And the user fills the third party procurement project costs
    When the user clicks the button/link                             css = label[for="stateAidAgreed"]
    And the user clicks the button/link                              jQuery = button:contains("Mark as complete")
    And the user should not see a field and summary error            Select if you are VAT registered
    Then the user enters the project location
    And the user fills in the organisation information               ${thirdPartyProcurementApplicationName}  ${SMALL_ORGANISATION_SIZE}
    And applicant fills in payment milestones

the user submits the third party procurement application
    [Documentation]   IFS-10083
    [Setup]  Get competitions id and set it as suite variable   ${thirdPartyProcurementCompetitionName}
    Given the user clicks the button/link                       id = application-overview-submit-cta
    And the user should see the element                         jQuery = h2:contains("Strategic Innovation Fund governance document")
    When the user clicks the button/link                        id = accordion-questions-heading-4-1
    And the user should see the element                         jQuery = td:contains("Empire Ltd")+td:contains("Procurement Third Party")+td:contains("Accepted")
    And the user clicks the button/link                         id = submit-application-button
    Then the user should see the element                        jQuery = h2:contains("Application submitted")
    And the user should see procurement terms and conditions in application summary
    [Teardown]  update milestone to yesterday                   ${competitionId}  SUBMISSION_DATE

Invite a registered assessor
    [Documentation]  IFS-10084
    Given log in as a different user             &{Comp_admin1_credentials}
    When the user clicks the button/link         link = ${thirdPartyProcurementCompetitionName}
    And the user clicks the button/link          link = Invite assessors to assess the competition
    And the user enters text to a text field     id = assessorNameFilter   Paul Plum
    And the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    Then the user clicks the button/link         jQuery = tr:contains("Paul Plum") label[for^="assessor-row"]
    And the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    And the user clicks the button/link          link = Invite
    And the user clicks the button/link          link = Review and send invites
    And the user enters text to a text field     id = message    This is custom text
    And the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")

Allocated assessor accepts invite to assess the competition
    [Documentation]  IFS-10084
    Given Log in as a different user                           &{assessor_credentials}
    When The user clicks the button/link                       Link = ${thirdPartyProcurementCompetitionName}
    And the user selects the radio button                      acceptInvitation  true
    And The user clicks the button/link                        jQuery = button:contains("Confirm")
    Then the user should be redirected to the correct page     ${server}/assessment/assessor/dashboard

Comp Admin allocates assessor to application
    [Documentation]  IFS-10084
    Given log in as a different user                 &{Comp_admin1_credentials}
    When The user clicks the button/link             link = Dashboard
    And The user clicks the button/link              link = ${thirdPartyProcurementCompetitionName}
    And The user clicks the button/link              jQuery = a:contains("Manage assessments")
    And the user clicks the button/link              jQuery = a:contains("Allocate applications")
    Then the user clicks the button/link             jQuery = tr:contains("${thirdPartyProcurementApplicationName}") a:contains("Assign")
    And the user adds an assessor to application     jQuery = tr:contains("Paul Plum") :checkbox
    And the user navigates to the page               ${server}/management/competition/${competitionId}
    And the user clicks the button/link              jQuery = button:contains("Notify assessors")

Allocated assessor assess the application
    [Documentation]  IFS-10084
    Given Log in as a different user                                        &{assessor_credentials}
    And the user clicks the button/link                                     link = ${thirdPartyProcurementCompetitionName}
    And the user clicks the button/link                                     jQuery = li:contains("${thirdPartyProcurementApplicationName}") a:contains("Accept or reject")
    And the user selects the radio button                                   assessmentAccept  true
    And the user clicks the button/link                                     jQuery = .govuk-button:contains("Confirm")
    And the user should be redirected to the correct page                   ${server}/assessment/assessor/dashboard/competition/${competitionId}
    When the user clicks the button/link                                    link = ${thirdPartyProcurementApplicationName}
    Then assessor should see third party procurement terms and conditions   Strategic Innovation Fund governance document
    And the assessor submits the assessment

Comp admin closes the assessment and releases feedback
    [Documentation]  IFS-10084
    Given log in as a different user                        &{Comp_admin1_credentials}
    When making the application a successful project        ${competitionId}    ${thirdPartyProcurementApplicationName}
    And the user navigates to the page                      ${server}/management/competition/${competitionId}
    And the user clicks the button/link                     css = button[type="submit"][formaction$="release-feedback"]
    And log in as a different user                          &{lead_applicant_credentials}
    And the user clicks the application tile if displayed
    Then the user should see the element                    jQuery = li:contains("${thirdPartyProcurementApplicationName}") .status-msg:contains("Successful")

Internal user can view third-party terms and conditions
    [Documentation]  IFS-10083  IFS-10084
    Given Requesting competition ID of this Project
    And requesting application ID of this application
    And Log in as a different user                          &{ifs_admin_user_credentials}
    And The user navigates to the page                      ${server}/management/competition/${ThirdPartyCompId}/application/${ThirdPartyApplicationId}
    When The user should see the element                    jQuery = button:contains("Strategic Innovation Fund governance document")
    And the user clicks the button twice                    jQuery = button:contains("Strategic Innovation Fund governance document")
    Then the user should see the element                    jQuery = th:contains("Partner") + th:contains("Strategic Innovation Fund governance document")
    And The user should see the element                     jQuery = td:contains("Empire Ltd") ~ td:contains("Procurement Third Party")

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

Requesting competition ID of this Project
    ${ThirdPartyCompId} =  get comp id from comp title    ${thirdPartyProcurementCompetitionName}
    Set suite variable   ${ThirdPartyCompId}

requesting application ID of this application
    ${ThirdPartyApplicationId} =  get application id by name   ${thirdPartyProcurementApplicationName}
    Set suite variable    ${ThirdPartyApplicationId}

comp admin creates procurement competition
    the user fills in the CS Project eligibility              ${BUSINESS_TYPE_ID}    2   false   single-or-collaborative
    the user fills in the CS funding eligibility              false   Programme   NOT_AID
    the user selects the organisational eligibility to no     false
    the user fills in the CS Milestones                       RELEASE_FEEDBACK   ${month}   ${nextyear}  No
    the user marks the procurement application as done        no   Programme
    the user clicks the button/link                           link = Public content
    the user fills in the Public content and publishes        procurement
    the user clicks the button/link                           link = Return to setup overview

the user should see third party t&c validations
    the user should see a field and summary error       Please enter a label to replace terms and conditions.
    the user should see a field and summary error       Please enter a description text for terms and conditions page.
    the user should see a field and summary error       Please enter a project costs guidance link.
    the user uploads the file                           css = .inputfile  ${ods_file}
    the user should see the element                     jQuery = :contains("${wrong_filetype_validation_error}")

the user completes required fields in third party procurement competition
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

the third party applicant can view the strategic innovation terms and conditions
    [Arguments]  ${title}
    the user clicks the button/link    link = ${title}
    the user should see the element    jQuery = h1:contains("${title}")
    the user should see the element    link = View ${title} (opens in a new window)
    the user should see the element    jQuery = p:contains("Summary of ${title}")
    the user should see the element    link = ${title} (opens in a new window)
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("${title} accepted")
    the user clicks the button/link    link = Return to application overview

applicant fills in payment milestones
    the user clicks the button/link                           link = Your payment milestones
    the user clicks the button/link                           jQuery = button:contains("Open all")
    applicant fills in payment milestone                      accordion-finances-content  2  Milestone 1  £55,224  taskOrActivity 1  deliverable 1  successCriteria 1
    the user clicks the button/link                           id = mark-all-as-complete
    applicant views saved payment milestones                  2  £55,224  Milestone 1  100%  £55,224  100%
    applicant views readonly payment milestones subsections   taskOrActivity 1  deliverable 1  successCriteria 1
    the user should see the element                           jQuery = li:contains("Your payment milestones") > .task-status-complete
    the user clicks the button/link                           link = Back to application overview
    the user should see the element                           jQuery = li:contains("Your project finances") > .task-status-complete

the user should see procurement terms and conditions in application summary
    the user clicks the button/link     link = View application
    the user should see the element     jQuery = td:contains("Empire Ltd")+td:contains("Procurement Third Party")

the assessor submits the assessment
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

assessor should see third party procurement terms and conditions
    [Arguments]  ${title}
    the user should see the element    jQuery = h2:contains("${title}")
    the user clicks the button/link    jQuery = a:contains("Procurement Third Party")
    the user should see the element    jQuery = h1:contains("${title}")
    the user should see the element    link = View ${title} (opens in a new window)
    the user should see the element    jQuery = p:contains("Summary of ${title}")
    the user clicks the button/link    link = Back to assessment overview

the user fills the third party procurement project costs
    the user clicks the button/link             link = Your project costs
    the user fills in Labour
    the user fills in Material
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other costs
