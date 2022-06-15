*** Settings ***
Documentation     IFS-12065 Pre-Registration (Applicant Journey) Apply to an expression of interest application
...
...               IFS-12077 Pre-Registration (Applicant Journey) Application overview - content changes
...

Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot

*** Variables ***
${hecpPreregCompName}       Hecp Pre Registration Competition
${hecpPreregAppName}        preRegApplication

*** Test Cases ***
Comp Admin creates a prereg competition
    [Documentation]  IFS-12065
    Given The user logs-in in new browser                    &{Comp_admin1_credentials}
    Then the competition admin creates prereg competition    ${BUSINESS_TYPE_ID}  ${hecpPreregCompName}  Pre Registration  ${compType_HESTA}  NOT_AID  HECP  PROJECT_SETUP  no  50  false  single-or-collaborative

Applicants should view prereg related content on competition
    [Arguments]  IFS-12065
    Given Comp admin set the competion as prereg comp and hide the question, section and subsection
    When the user navigates to the page         ${frontDoor}
    And the user enters text to a text field    id = keywords   Pre Registration
    And the user clicks the button/link         id = update-competition-results-button
    Then the user should see the element        jQuery = li:contains("Horizon Europe Guarantee Competition For Pre Registration") div:contains("Refer to competition date for competition submission deadlines.")

Applicant can not view hidden question, section and subsection
    [Documentation]  IFS-12077
    When existing user creates a new application    ${hecpPreregCompName}
    Then the user should not see the element        link = Participating Organisation project region
    And the user should not see the element         link = Award terms and conditions
    And the user should not see the element         jQuery = h2:contains("Terms and conditions")
    And the user should not see subsection          Your project location

Applicants views expression of interest labels in application overview page for pre reg applications
    [Arguments]  IFS-12077
    Given the user clicks the button/link                         link = Back to expression of interest overview
    When the user completes the prereg application details        ${hecpPreregAppName}  ${tomorrowday}  ${month}  ${nextyear}   23
    And Requesting application ID of prereg application
    Then the user should see EOI labels for prereg application
    And the user should see the element                           jQuery = dt:contains("Application number:")+dd:contains("${preregApplicationID}")

Lead applicant completes the application sections
    [Arguments]  IFS-12077
    When the applicant completes Application Team                        COMPLETE  steve.smith@empire.com
    And the user completes the application research category             Feasibility studies
    And the user complete the work programme
    And The user is able to complete horizon grant agreement section
    And the lead applicant fills all the questions and marks as complete(prereg)
    And the user completes prereg project finances                      ${hecpPreregAppName}   yes
    Then the user should see the element                                jQuery = .progress:contains("100%")



*** Keywords ***
Requesting IDs of this hecp pre reg competition
    [Arguments]  ${competitionName}
    ${hecpPreregCompId} =  get comp id from comp title  ${hecpPreregCompName}
    Set suite variable  ${hecpPreregCompId}

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database

Requesting application ID of prereg application
    ${preregApplicationID} =  get application id by name  ${hecpPreregAppName}
    Set suite variable    ${preregApplicationID}

#the user starts a new pre reg application
##    Log in as a different user                                  &{lead_applicant_credentials}
##    the user select the competition and starts application      ${hecpPreregCompName}
##    the user selects the radio button                           createNewApplication  true      #Yes, I want to create a new application.
##    the user clicks the button/link                             jQuery = .govuk-button:contains("Continue")
##    the user clicks the button/link                             css = .govuk-button[type="submit"]    #Save and continue
#

the user should see EOI labels for prereg application
    the user should see the element      jQuery = h1:contains("Expression of interest overview")
    the user should see the element      jQuery = h2:contains("Expression of interest progress")
    the user should see the element      jQuery = h2:contains("Expression of interest questions")

the user completes prereg project finances
    [Arguments]  ${Application}   ${Project_growth_table}
    the user clicks the button/link                     link = Your project finances
    The user is able to complete hecp project costs
    Run Keyword if  '${Project_growth_table}' == 'no'   the user fills in the organisation information  ${Application}  ${SMALL_ORGANISATION_SIZE}
    Run Keyword if  '${Project_growth_table}' == 'yes'  the user fills the organisation details with Project growth table  ${Application}  ${SMALL_ORGANISATION_SIZE}
    the user completes prereg funding section           ${Application}
    the user clicks the button/link                     link = Back to expression of interest overview

The user is able to complete hecp project costs
    the user clicks the button/link           link = Your project costs
    the user should see the element           jQuery = h1:contains("Your project costs")
    the user enters text to a text field      id = labour  50000
    the user enters text to a text field      id = subcontracting  50000
    the user enters text to a text field      id = travel  10000
    the user enters text to a text field      id = material  30000
    the user enters text to a text field      id = capital  20000
    the user enters text to a text field      id = other  40000
    the user enters text to a text field      id = overhead  0
    the user clicks the button/link           jQuery = button:contains("Mark")
    the user should see the element           jQuery = li:contains("Your project costs") > .task-status-complete

the user completes prereg funding section
    [Arguments]  ${Application}
    the user clicks the button/link             link = Your funding
    the user fills in the funding information   ${Application}   no

the progress indicator should show 100%
    element should contain      css = .progress-totals--max  100%

the user completes the prereg application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}  ${projectDuration}
    the user clicks the button/link             link = Application details
    the user should see the element             jQuery = h1:contains("Application details")
    the user enters text to a text field        id = name  ${appTitle}
    the user enters text to a text field        id = startDate  ${tomorrowday}
    the user enters text to a text field        css = #application_details-startdate_month  ${month}
    the user enters text to a text field        css = #application_details-startdate_year  ${nextyear}
    the user should see the element             jQuery = label:contains("Project duration in months")
    the user enters text to a text field        css = [id="durationInMonths"]  ${projectDuration}
    the user clicks the button/link             id = application-question-complete
    the user clicks the button/link             link = Back to application overview
    the user should see the element             jQuery = li:contains("Application details") > .task-status-complete

the competition admin creates prereg competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${fundingRule}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user navigates to the page                              ${CA_UpcomingComp}
    the user clicks the button/link                             jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                    ${competition}  ${month}  ${nextyear}  ${compType_HESTA}  STATE_AID  HECP
    the user selects the Terms and Conditions                   ${compType}  ${fundingRule}
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility                ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user fills in the CS funding eligibility                false   ${compType_HESTA}  ${fundingRule}
    the user selects the organisational eligibility to no       false
    the user completes milestones with out assessment
    the user marks the prereg application question as done
    the user fills in the CS Documents in other projects
    the user clicks the button/link                             link = Public content
    the user fills in the Public content and publishes          ${extraKeyword}
    the user clicks the button/link                             link = Return to setup overview
    the user clicks the button/link                             jQuery = a:contains("Complete")
    the user clicks the button/link                             jQuery = button:contains('Done')
    the user navigates to the page                              ${CA_UpcomingComp}
    the user should see the element                             jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

the user marks the prereg application question as done
    the user clicks the button/link                                 link = Application
    the user marks each question as complete                        Application details
    the assessed questions are marked complete(HECP type)
    the user clicks the button/link                                 jQuery = .govuk-heading-s a:contains("Finances")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 jQuery = button:contains("Done")
    the user clicks the button/link                                 link = Back to competition details
    the user should see the element                                 jQuery = div:contains("Application") ~ .task-status-complete

the user completes milestones with out assessment
    the user clicks the button/link                     link = Milestones
    the user clicks the button twice                    jQuery = label:contains("Project setup")
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user completes application submission page      Yes
    the user inputs application assessment decision     No
    the user clicks the button/link                     jQuery = button:contains("Done")
    the user clicks the button/link                     link = Back to competition details
    the user should see the element                     jQuery = div:contains("Milestones") ~ .task-status-complete

Get competitions id and set it as suite variable
    [Arguments]  ${competitionTitle}
    ${preregCompetitionId} =  get comp id from comp title  ${competitionTitle}
    Set suite variable  ${preregCompetitionId}

the user should not see subsection
    [Arguments]   ${subSectionName}
    the user clicks the button/link         link = Your project finances
    the user should not see the element     link = ${subSectionName}

Comp admin set the competion as prereg comp and hide the question, section and subsection
    Get competitions id and set it as suite variable     ${hecpPreregCompName}
    set competition as pre reg                           ${preregCompetitionId}
    set question as hidden in pre reg application        ${preregCompetitionId}
    set subsection as hidden in pre reg application      ${preregCompetitionId}
    set section as hidden in pre reg application         ${preregCompetitionId}
    update milestone to yesterday                        ${preregCompetitionId}  OPEN_DATE