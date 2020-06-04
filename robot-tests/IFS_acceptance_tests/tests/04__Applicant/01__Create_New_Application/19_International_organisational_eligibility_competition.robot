*** Settings ***
Documentation     IFS-7195  Organisational eligibility category in Competition setup
...
...               IFS-7246  Comp setup allowing international organisations to lead the competition
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${leadOrganisationInternationalCompetition}            Lead International Organisation Competition
${organisationalEligibilitySubTitle}                   Can international organisations apply?
${organisationalEligibilityInfo}                       Is this competition open to organisations based outside the UK?
${organisationalEligibilityValidationErrorMessage}     You must choose if organisations based outside the UK can apply for this competition.
${leadOrganisationsTitle}                              Lead organisations
${leadOrganisationsSubTitle}                           Can international organisations lead the competition?
${leadOrganisationsValidationErrorMessage}             You must choose if international organisations can lead the competition.

*** Test Cases ***
Comp admin can only access organisational eligibility category after intial details entered
     [Documentation]  IFS-7195
     Given the user navigates to the page                   ${CA_UpcomingComp}
     And the user clicks the button/link                    jQuery = .govuk-button:contains("Create competition")
     When the user fills in the CS Initial details          ${organisationEligibilityCompetitionName}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     Then the user should see the enabled element           link = ${organisationalEligibilityTitle}
     And the user should not see the element                jQuery = li:contains("${organisationalEligibilityTitle}") .task-status-complete

Eligibility is changed to project eligibility in project eligibility category
     [Documentation]  IFS-7195
     When the user clicks the button/link                   link = ${projectEligibilityLink}
     Then the user should see the text in the element       jQuery = h1:contains("${projectEligibilityLink}")        ${ProjectEligibilityLink}
     And the user should see the element                    jQuery = span:contains("${organisationalEligibilityTitle}")

Eligibility is changed to project eligibility in pagination
     [Documentation]  IFS-7195
     Given the user clicks the button/link                  css = a[rel="Prev"]
     When the user should see the text in the element       jQuery = span:contains("${projectEligibilityLink}")     ${ProjectEligibilityLink}
     And the user clicks the button/link                    jQuery = span:contains("${projectEligibilityLink}")
     And the user clicks the button/link                    jQuery = span:contains("${organisationalEligibilityTitle}")
     Then the user should see the text in the element       jQuery = span:contains("${projectEligibilityLink}")     ${ProjectEligibilityLink}

Comp admin can not complete the competition setup without organisational eligibility category completetion
     [Documentation]  IFS-7195
     Given the user clicks the button/link                                                  link = Return to setup overview
     When the user completes all categories except organisational eligibility category      ${business_type_id}  KTP  ${compType_Programme}  project-setup-completion-stage  yes  1  true  single
     Then The user should see the element                                                   css = #compCTA[disabled]

Comp admin can access the Organisational eligibility category and check for all required fields
     [Documentation]    IFS-7195
     When the user clicks the button/link                   link = ${organisationalEligibilityTitle}
     Then the user checks for organisational eligibility fields

Organisational eligibility validations
     [Documentation]    IFS-7195
     When the user clicks the button/link                   jQuery = button:contains("Save and continue")
     Then The user should see a field and summary error     ${organisationalEligibilityValidationErrorMessage}

Comp admin sets organisational eligibility to No
     [Documentation]    IFS-7195 IFS-7246
     When the user selects the radio button                     internationalOrganisationsApplicable     false
     And The user should not see a field and summary error      ${organisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                        jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility response question and answer

Comp admin sets organisational eligibility to Yes and check for lead organisations fields
     [Documentation]    IFS-7195 IFS-7246
     Given the user clicks the button/link                      jQuery = button:contains("Edit")
     When the user selects the radio button                     internationalOrganisationsApplicable     true
     And The user should not see a field and summary error      ${organisationalEligibilityValidationErrorMessage}
     And the user clicks the button/link                        jQuery = button:contains("Save and continue")
     Then the user checks for lead organisations fields

Lead organisations validations
     [Documentation]    IFS-7246
     When the user clicks the button/link                     jQuery = button:contains("Save and continue")
     Then The user should see a field and summary error       ${leadOrganisationsValidationErrorMessage}

Comp admin sets international organisations can not lead the competition
     [Documentation]   IFS-7246
     When the user selects the radio button                                                                     leadInternationalOrganisationsApplicable  false
     And The user should not see a field and summary error                                                      ${leadOrganisationsValidationErrorMessage}
     And the user clicks the button/link                                                                        jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility and lead organisation response question and answers      No

Comp admin sets international organisations can lead the competition
     [Documentation]   IFS-7246
     Given the user clicks the button/link                                                                      jQuery = button:contains("Edit")
     And the user clicks the button/link                                                                        jQuery = button:contains("Save and continue")
     When the user selects the radio button                                                                     leadInternationalOrganisationsApplicable  true
     And the user clicks the button/link                                                                        jQuery = button:contains("Save and continue")
     Then comp admin can view organisation eligibility and lead organisation response question and answers      Yes

Comp admin creates international organisation eligibility competition
     [Documentation]  IFS-7195
     Given the user clicks the button/link         link = Return to setup overview
     When the user clicks the button/link          jQuery = a:contains("Complete")
     And the user clicks the button/link           jQuery = button:contains('Done')
     And the user navigates to the page            ${CA_UpcomingComp}
     Then the user should see the element          jQuery = h2:contains("Ready to open") ~ ul a:contains("${organisationEligibilityCompetitionName}")

Comp admin sets lead organisations can lead international competitions and sets competition to live
     [Documentation]  IFS-7195
     Given Get competition id and set open date to yesterday        ${organisationEligibilityCompetitionName}
     When the user navigates to the page                            ${CA_Live}
     Then the user should see the element                           jQuery = h2:contains('Open') ~ ul a:contains('${organisationEligibilityCompetitionName}')

Comp admin sets lead organisations can not lead international competitions and sets competition to live
     [Documentation]  IFS-7246
     Given the user navigates to the page                                               ${CA_UpcomingComp}
     When comp admin sets lead organisation can not lead the international competition
     And Get competition id and set open date to yesterday                              ${organisationEligibilityCompetitionName}
     Then the user navigates to the page                                                ${CA_Live}
     And the user should see the element                                                jQuery = h2:contains('Open') ~ ul a:contains('${organisationEligibilityCompetitionName}')

*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser            &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database

the user checks for organisational eligibility fields
    the user should see the element           jQuery = h1:contains("${organisationalEligibilityTitle}")
    the user should see the element           id = internationalOrganisationsApplicable
    the user should see the element           jQuery = span:contains("${organisationalEligibilityInfo}")
    the user should see the element           css = [for="comp-internationalOrganisationsApplicable-yes"]
    the user should see the element           css = [for="comp-internationalOrganisationsApplicable-no"]
    the user should see the element           jQuery = button:contains("Save and continue")
    the user should see the element           jQuery = span:contains("${projectEligibilityLink}")
    the user should see the element           link = Competition setup
    the user should see the element           link = Return to setup overview

the user checks for lead organisations fields
    the user should see the element           jQuery = h1:contains("${leadOrganisationsTitle}")
    the user should see the element           id = leadInternationalOrganisationsApplicable
    the user should see the element           css = [for="comp-leadInternationalOrganisationsApplicable-yes"]
    the user should see the element           css = [for="comp-leadInternationalOrganisationsApplicable-no"]
    the user should see the element           jQuery = button:contains("Save and continue")
    the user should see the element           jQuery = span:contains("${organisationalEligibilityTitle}")
    the user should see the element           link = Back to organisational eligibility
    the user should see the element           link = Return to setup overview


the user completes all categories except organisational eligibility category
    [Arguments]    ${orgType}  ${extraKeyword}  ${compType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}             ${researchParticipation}    ${researchCategory}  ${collaborative}  # 1 means 30%
    the user fills in the CS Milestones                     ${completionStage}     ${month}                    ${nextyear}
    the user marks the Application as done                  ${projectGrowth}       ${compType}
    the user fills in the CS Assessors
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview

comp admin can view organisation eligibility and lead organisation response question and answers
    [Arguments]     ${option}
    the user should see the element       jQuery = dt:contains("${organisationalEligibilitySubTitle}") ~ dd:contains("Yes")
    the user should see the element       jQuery = dt:contains("${leadOrganisationsSubTitle}") ~ dd:contains("${option}")
    the user should see the element       jQuery = button:contains("Edit")

comp admin can view organisation eligibility response question and answer
    the user should see the element         jQuery = dd:contains("No")
    the user should see the element         jQuery = button:contains("Edit")
    the user should not see the element     jQuery = h1:contains("${leadOrganisationsTitle}")

comp admin sets lead organisation can not lead the international competition
     the user clicks the button/link                                                   jQuery = .govuk-button:contains("Create competition")
     the user fills in the CS Initial details                                          ${leadOrganisationInternationalCompetition}  ${month}  ${nextyear}  ${compType_Programme}  2  GRANT
     the user selects the organisational eligibility                                   true    false
     the user completes all categories except organisational eligibility category      ${business_type_id}  KTP  ${compType_Programme}  project-setup-completion-stage  yes  1  true  single
     the user clicks the button/link                                                   jQuery = a:contains("Complete")
     the user clicks the button/link                                                   jQuery = button:contains('Done')

