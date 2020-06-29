*** Settings ***
Documentation     IFS-7313  New completion stage for Procurement - Comp setup journey
...
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        CompAdmin
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${sbriType1CompetitionName}     SBRI Type 1 Competition


*** Test Cases ***
Comp admin saves the completition stage with competition close option
    [Documentation]  IFS-7313
    Given the user completes initial details of the competition        ${sbriType1CompetitionName}  PROCUREMENT
    When the user navigates to completition stage
    And the user saves the completion stage with competition close     COMPETITION_CLOSE
    Then the user should see competition close in read only page       Competition close

Comp admin edits the completition stage with competition close option
    [Documentation]  IFS-7313
    Given the user clicks the button/link                               jQuery = button:contains("Edit")
    When the user saves the completion stage with competition close     COMPETITION_CLOSE
    Then the user should see competition close in read only page        Competition close

Comp admin complete the SBRI type 1 milestones
    [Documentation]  IFS-7313
    Given the user clicks the button/link                                  jQuery = span:contains("Milestones")
    When the user fills in the competition close Milestones
    Then the user should see the correct inputs in the Milestones form
    And the user should see milestones section marked as complete

Comp admin complete the SBRI type 1 competition setup
    [Documentation]  IFS-7313
    Given the user completes all sections of competition setup     ${business_type_id}  SBRI Type 1  ${compType_Programme}  yes  1  true  collaborative
    When the user clicks the button/link                           jQuery = a:contains("Complete")
    And the user clicks the button/link                            jQuery = button:contains('Done')
    And the user navigates to the page                             ${CA_UpcomingComp}
    Then the user should see the element                           jQuery = h2:contains("Ready to open") ~ ul a:contains("${sbriType1CompetitionName}")

Comp admin sets SBRI Type 1 competition to live
     [Documentation]  IFS-7313
     Given Get competition id and set open date to yesterday     ${sbriType1CompetitionName}
     When the user navigates to the page                         ${CA_Live}
     Then the user should see the element                        jQuery = h2:contains('Open') ~ ul a:contains('${sbriType1CompetitionName}')


*** Keywords ***
Custom Suite Setup
    Connect to Database  @{database}
    The user logs-in in new browser            &{Comp_admin1_credentials}
    Set predefined date variables

Custom suite teardown
    the user closes the browser
    Disconnect from database

the user completes initial details of the competition
    [Arguments]    ${competitionName}   ${fundingType}
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     ${competitionName}  ${month}  ${nextyear}  ${compType_Programme}  2   ${fundingType}

the user navigates to completition stage
    the user clicks the button/link     link = Initial details
    the user clicks the button/link     jQuery = span:contains("Completion stage")

the user saves the completion stage with competition close
    [Arguments]     ${completionStage}
    the user selects the radio button     selectedCompletionStage   ${completionStage}
    the user clicks the button/link       jQuery = button:contains("Done")

the user should see competition close in read only page
    [Arguments]     ${completionStageValue}
    the user clicks the button/link     jQuery = span:contains("Completion stage")
    the user should see the element     jQuery = strong:contains("${completionStageValue}")

the user fills in the competition close Milestones
    ${i} =  Set Variable   1
     :FOR   ${ELEMENT}   IN    @{sbriType1Milestones}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.day input  ${i}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.month input  ${month}
      \    the user enters text to a text field  jQuery = th:contains("${ELEMENT}") ~ td.year input  ${nextyear}
      \    ${i} =   Evaluate   ${i} + 1
    the user clicks the button/link              jQuery = button:contains("Done")

the user should see milestones section marked as complete
    the user clicks the button/link     link = Competition details
    the user should see the element     jQuery = div:contains("Milestones") ~ .task-status-complete

the user completes all sections of competition setup
    [Arguments]    ${orgType}  ${extraKeyword}  ${compType}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects procurement Terms and Conditions
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility              ${orgType}             ${researchParticipation}    ${researchCategory}  ${collaborative}  # 1 means 30%
    the user selects the organisational eligibility to no     false
    the user marks the Application as done                    ${projectGrowth}       ${compType}
    the user fills in the CS Assessors
    the user clicks the button/link                           link = Public content
    the user fills in the Public content and publishes        ${extraKeyword}
    the user clicks the button/link                           link = Return to setup overview