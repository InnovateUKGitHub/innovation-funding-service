*** Settings ***
Documentation       IFS-7195  KTP - Organisational eligibility category in Competition setup

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${OrganisationEligibilityCompetitionName}                           Oraganisation Eligibility Competition
${ProjectEligibilityLink}                                           Project eligibility
${OrganisationalEligibilityTitle}                                   Organisational eligibility
${OrganisationalEligibilitySubTitle}                                Can international organisations apply?
${OrganisationalEligibilityInfo}                                    Is this competition open to organisations based outside the UK?
${OrganisationalEligibilityValidationErrorMessage}                  You must choose if organisations based outside the UK can apply for this competition.


*** Test Cases ***
Comp admin can view organisational eligibility category in competition setup
    [Documentation]  IFS-7195
    Given the user navigates to the page                            ${CA_UpcomingComp}
    When the user clicks the button/link                            jQuery = .govuk-button:contains("Create competition")
    Then the user should see the element                            jQuery = h3:contains("${OrganisationalEligibilityTitle}")

Eligibility is changed to project eligibility in competition setup
    [Documentation]  IFS-7195
     Given the user navigates to the page                           ${CA_UpcomingComp}
     When the user clicks the button/link                           jQuery = .govuk-button:contains("Create competition")
     Then the user should see the text in the element               jQuery = h3:contains("${ProjectEligibilityLink}")        ${ProjectEligibilityLink}

Comp admin can only access organisational eligibility category after intial details entered
    [Documentation]  IFS-7195
     When the user fills in the CS Initial details                  ${OrganisationEligibilityCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     Then the user should see the enabled element                   link = ${OrganisationalEligibilityTitle}
     And the user should not see the element                        jQuery = li:contains("${OrganisationalEligibilityTitle}") .task-status-complete

Eligibility is changed to project eligibility in project eligibility category
    [Documentation]  IFS-7195
     When the user clicks the button/link                           link = ${ProjectEligibilityLink}
     Then the user should see the text in the element               jQuery = h1:contains("${ProjectEligibilityLink}")        ${ProjectEligibilityLink}
     And the user should see the element                            jQuery = span:contains("${OrganisationalEligibilityTitle}")

Eligibility is changed to project eligibility in pagination
     Given the user clicks the button/link                          css = a[rel="Prev"]
     Then the user should see the text in the element               jQuery = span:contains("${ProjectEligibilityLink}")     ${ProjectEligibilityLink}
     When the user clicks the button/link                           jQuery = span:contains("${ProjectEligibilityLink}")
     And the user clicks the button/link                            jQuery = span:contains("${OrganisationalEligibilityTitle}")
     Then the user should see the text in the element               jQuery = span:contains("${ProjectEligibilityLink}")     ${ProjectEligibilityLink}

Comp admin can not complete the competition setup without organisational eligibility category completetion
    [Documentation]  IFS-7195
    Given the user clicks the button/link                           link = Return to setup overview
    When the user completes all categories except organisational eligibility category   KTP  ${business_type_id}  KTP  ${compType_Programme}  project-setup-completion-stage  yes  1  true  single
    Then the user check the element is disabled

Comp admin can access the Organisational eligibility category and check for all required fields
     [Documentation]    IFS-7195
     When the user clicks the button/link                           link = ${OrganisationalEligibilityTitle}
     Then the user checks for organisational eligibility fields

Organisational eligibility validations
    [Documentation]    IFS-7195
     When the user clicks the button/link                           jQuery = button:contains("Done")
     Then The user should see a field and summary error             ${OrganisationalEligibilityValidationErrorMessage}

Comp admin sets organisational eligibility to No
    [Documentation]    IFS-7195
     When the user selects the radio button                         internationalOrganisationsApplicable     false
     And The user should not see a field and summary error          ${OrganisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                            jQuery = button:contains("Done")
     Then the user should see the element                           jQuery = dd:contains("No")
     And the user should see the element                            jQuery = button:contains("Edit")

Comp admin sets organisational eligibility to Yes
    [Documentation]    IFS-7195
     When the user clicks the button/link                           jQuery = button:contains("Edit")
     And the user selects the radio button                          internationalOrganisationsApplicable     true
     And The user should not see a field and summary error          ${OrganisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                            jQuery = button:contains("Done")
     Then the user should see the element                           jQuery = dd:contains("Yes")
     And the user should see the element                            jQuery = button:contains("Edit")

Comp admin creates international organisation eligibility competition
    [Documentation]  IFS-7195
    When the user clicks the button/link                            link = Return to setup overview
    And the user clicks the button/link                             jQuery = a:contains("Complete")
    And the user clicks the button/link                             jQuery = button:contains('Done')
    And the user navigates to the page                              ${CA_UpcomingComp}
    Then the user should see the element                            jQuery = h2:contains("Ready to open") ~ ul a:contains("${OrganisationEligibilityCompetitionName}")

Comp admin sets the international organisation eligibility competition to live
    [Documentation]  IFS-7195
    [Setup]  Get competition id and set open date to yesterday      ${OrganisationEligibilityCompetitionName}
    And the user navigates to the page                              ${CA_Live}
    Then the user should see the element                            jQuery = h2:contains('Open') ~ ul a:contains('${OrganisationEligibilityCompetitionName}')

Comp admin can not update the organisation eligibility category when competition is in open status
    [Documentation]  IFS-7195
    When the user clicks the button/link                            link = ${OrganisationEligibilityCompetitionName}
    And the user clicks the button/link                             link = View and update competition setup
    And the user clicks the button/link                             link = ${OrganisationalEligibilityTitle}
    Then the user should not see the element                        jQuery = button:contains("Edit")



*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser                                 &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database

the user checks for organisational eligibility fields
    the user should see the element                                 jQuery = h1:contains("${OrganisationalEligibilityTitle}")
    the user should see the element                                 id = internationalOrganisationsApplicable
    the user should see the element                                 jQuery = span:contains("Is this competition open to organisations based outside the UK?")
    the user should see the element                                 css = [for="comp-internationalOrganisationsApplicable-yes"]
    the user should see the element                                 css = [for="comp-internationalOrganisationsApplicable-no"]
    the user should see the element                                 jQuery = button:contains("Done")
    the user should see the element                                 jQuery = span:contains("${ProjectEligibilityLink}")
    the user should see the element                                 link = Competition setup
    the user should see the element                                 link = Return to setup overview

the user completes all categories except organisational eligibility category
    [Arguments]  ${fundingType}  ${orgType}  ${extraKeyword}  ${compType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user selects procurement Terms and Conditions
    ...  ELSE  the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Eligibility                            ${orgType}    ${researchParticipation}    ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS Milestones                             ${completionStage}     ${month}    ${nextyear}
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user marks the procurement application as done      ${projectGrowth}  ${compType}
    ...  ELSE  the user marks the application as done               ${projectGrowth}  ${compType}
    the user fills in the CS Assessors
    Run Keyword If  '${fundingType}' == 'PROCUREMENT'  the user select no documents
    ...  ELSE  the user fills in the CS Documents in other projects
    the user clicks the button/link                                 link = Public content
    the user fills in the Public content and publishes              ${extraKeyword}
    the user clicks the button/link                                 link = Return to setup overview

the user check the element is disabled
    the user should not see an error in the page
    Wait Until Element Is Visible Without Screenshots               id=compCTA
    The user should see the element                                 css = [disabled="disabled"]






