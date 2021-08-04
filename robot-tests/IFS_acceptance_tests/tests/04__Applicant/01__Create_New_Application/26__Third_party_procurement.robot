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
    [Documentation]  IFS-10081  IFS-10082
    Given the user completes required fields in third party procurement competition     Innovation Fund governance document  Summary of Innovation Fund governance document   https://www.google.com
    When the user clicks the button/link                                                jQuery = button:contains("Done")
    Then the user should see the element                                                link = https://www.google.com (opens in a new window)
    And the user should see the element                                                 jQuery = p:contains("This is the project costs guidance link applicants will see in the project costs section.")
    And the user verifies valid terms and conditions text is displaying                 Innovation Fund governance document

Comp admin can edit the third party procurement terms and conditions
    [Documentation]  IFS-10081  IFS-10082
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
    When the user fills in funding information for the third party procurement comp
    Then the user navigates to the page                                                ${CA_UpcomingComp}
    And the user should see the element                                                jQuery = h3 a:contains("Third party procurement competition")

#User applies to third party competition
#    [Documentation]  IFS-10083
#    [Setup]  get competition id and set open date to yesterday      ${thirdPartyProcurementCompetitionName}
#    Given log in as a different user                                &{lead_applicant_credentials}
#    And logged in user applies to competition                       ${thirdPartyProcurementCompetitionName}  3
#    When the user fills in third-party Application details          ${thirdPartyProcurementApplicationName}  ${tomorrowday}  ${month}  ${nextyear}
#    And the applicant completes Application Team
#    And the applicant marks EDI question as complete
#    And the lead applicant fills all the questions and marks as complete(procurement)
#    Then the lead completes the questions with multiple answer choice and multiple appendices
#    And the third party applicant can view the strategic innovation terms and conditions

#Applicant fills in project finances without any VAT validations
#    [Documentation]   IFS-10134
#    Given the user navigates to Your-finances page            ${thirdPartyProcurementApplicationName}
#    And the user fills the procurement project costs          Calculate  52,214
#    When the user clicks the button/link                      css = label[for="stateAidAgreed"]
#    And the user clicks the button/link                       jQuery = button:contains("Mark as complete")
#    And the user should not see a field and summary error     Select if you are VAT registered
#    Then the user enters the project location
#    And the user fills in the organisation information        ${thirdPartyProcurementApplicationName}  ${SMALL_ORGANISATION_SIZE}
#    And Applicant fills in payment milestones

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

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
    the user enters text to a text field        id = projectCostGuidanceLink   ${url}
    the user uploads the file                   css = .inputfile  ${valid_pdf}

the user verifies valid terms and conditions text is displaying
    [Arguments]  ${title}
    the user clicks the button/link                     jQuery = a:contains("Procurement Third Party (opens in a new window)")
    select window                                       title = ${title} - Innovation Funding Service
    the user should see the element                     jQuery = h1:contains("${title}")
    the user should see the element                     jQuery = a:contains("View ${title}")
    the user should see the element                     jQuery = p:contains("Summary of ${title}")
    [Teardown]   the user closes the last opened tab

the third party applicant can view the strategic innovation terms and conditions
    the user clicks the button/link    link = Strategic Innovation Fund Governance Document
    the user should see the element    jQuery = h1:contains("Strategic Innovation Fund Governance Document")

Applicant fills in payment milestones
    the user clicks the button/link                           link = Your payment milestones
    the user clicks the button/link                           jQuery = button:contains("Open all")
    applicant fills in payment milestone                      accordion-finances-content  2  Milestone 1  £72,839  taskOrActivity 1  deliverable 1  successCriteria 1
    the user clicks the button/link                           id = mark-all-as-complete
    applicant views saved payment milestones                  2  £72,839  Milestone 1  100%  £72,839  100%
    applicant views readonly payment milestones subsections   taskOrActivity 1  deliverable 1  successCriteria 1
    the user should see the element                           jQuery = li:contains("Your payment milestones") > .task-status-complete
    the user clicks the button/link                           link = Back to application overview
    the user should see the element                           jQuery = li:contains("Your project finances") > .task-status-complete
