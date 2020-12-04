*** Settings ***
Documentation     IFS-8638: Create new competition type
...
...               IFS-8751: Increase project duration in months
...
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/keywords/MYSQL_AND_DATE_KEYWORDS.robot

*** Variables ***
${heukarCompTypeSelector}             dt:contains("Competition type") ~ dd:contains("${compType_HEUKAR}")
${heukarApplicationName}              Heukar application

*** Test Cases ***
Comp admin can select the competition type option Heukar in Initial details on competition setup
    [Documentation]  IFS-8638
    Given the user logs-in in new browser             &{Comp_admin1_credentials}
    When the user navigates to the page               ${CA_UpcomingComp}
    And the user clicks the button/link               jQuery = .govuk-button:contains("Create competition")
    Then the user fills in the CS Initial details     ${heukarCompetitionName}  ${month}  ${nextyear}  ${compType_HEUKAR}  2  GRANT

Comp admin can view Heukar competition type in Initial details read only view
    [Documentation]  IFS-8638
    Given the user clicks the button/link    link = Initial details
    Then the user can view Heukar competition type in Initial details read only view

Comp admin creates Heukar competition
    [Documentation]  IFS-8751
    Given the user clicks the button/link                             link = Back to competition details
    Then the competition admin creates Heukar competition             ${BUSINESS_TYPE_ID}  ${heukarCompetitionName}  ${compType_HEUKAR}  ${compType_HEUKAR}  2  GRANT  RELEASE_FEEDBACK  no  1  false  single-or-collaborative
    [Teardown]  Get competition id and set open date to yesterday     ${heukarCompetitionName}

Lead applicant sees validation error messages in Application details when they enter numerical values > 84 in the Project duration in months field
    [Documentation]  IFS-8751
    Given log in as a different user                                 &{lead_applicant_credentials}
    And the user select the competition and starts application      ${heukarCompetitionName}
    And user selects where is organisation based                     isNotInternational
    And the user clicks the button/link                              link = Application details
    When the user fills in the Heukar Application details            ${heukarApplicationName}  ${tomorrowday}  ${month}  ${nextyear}  85
    Then the user should see project duration validation errors

Lead applicant can enter numerical values in Application details from 1 to 84 in the Project duration in months field
    [Documentation]  IFS-8751
    Given the user re-enters project duration in months without errors
    Then the user successfully marks Application details as complete

*** Keywords ***
the user can view Heukar competition type in Initial details read only view
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Edit")
    the user should see the element     jQuery = ${heukarCompTypeSelector}
    the user clicks the button/link     jQuery = button:contains("Done")

the competition admin creates HEUKAR competition
    [Arguments]  ${orgType}  ${competition}  ${extraKeyword}  ${compType}  ${stateAid}  ${fundingType}  ${completionStage}  ${projectGrowth}  ${researchParticipation}  ${researchCategory}  ${collaborative}
    the user selects the Terms and Conditions
# REMOVE/ADD NEGATIVE CASE FUNDING INFORMATION IN NEXT SPRINT
    the user fills in the CS Funding Information
    the user fills in the CS Project eligibility            ${orgType}  ${researchParticipation}  ${researchCategory}  ${collaborative}  # 1 means 30%
    the user selects the organisational eligibility         true    true
    the user fills in the CS Milestones                     ${completionStage}   ${month}   ${nextyear}
    the user marks the application as done                  ${projectGrowth}  ${compType}  ${competition}
# REMOVE/ADD NEGATIVE CASE ASSESSORS IN NEXT SPRINT
#    the user fills in the CS Assessors                      ${fundingType}
# REMOVE/ADD NEGATIVE CASE DOCUMENTS IN NEXT SPRINT
#    the user fills in the CS Documents in other projects
    the user clicks the button/link                         link = Public content
    the user fills in the Public content and publishes      ${extraKeyword}
    the user clicks the button/link                         link = Return to setup overview
    the user clicks the button/link                         jQuery = a:contains("Complete")
    the user clicks the button/link                         jQuery = button:contains('Done')
    the user navigates to the page                          ${CA_UpcomingComp}
    the user should see the element                         jQuery = h2:contains("Ready to open") ~ ul a:contains("${competition}")

user selects where is organisation based
    [Arguments]  ${org_type}
    the user selects the radio button     international  ${org_type}
    the user clicks the button/link       id = international-organisation-cta
    the user clicks the button/link       id = save-organisation-button

the user fills in the Heukar Application details
    [Arguments]  ${appTitle}  ${tomorrowday}  ${month}  ${nextyear}  ${projectDuration}
    the user should see the element             jQuery = h1:contains("Application details")
    the user enters text to a text field        id = name  ${appTitle}
    the user enters text to a text field        id = startDate  ${tomorrowday}
    the user enters text to a text field        css = #application_details-startdate_month  ${month}
    the user enters text to a text field        css = #application_details-startdate_year  ${nextyear}
    the user should see the element             jQuery = label:contains("Project duration in months")
    the user enters text to a text field        css = [id="durationInMonths"]  ${projectDuration}
    the user clicks the button twice            css = label[for="resubmission-no"]
    the user clicks the button/link             id = application-question-complete

the user should see project duration validation errors
    And the user should see a summary error     Enter the total number of months between 1 and 84.
    And the user should see a field error       Enter the total number of months between 1 and 84.

the user re-enters project duration in months without errors
    the user enters text to a text field        css = [id="durationInMonths"]  84
    the user should not see a summary error     Enter the total number of months between 1 and 84.
    the user should not see a field error       Enter the total number of months between 1 and 84.

the user successfully marks Application details as complete
    the user clicks the button/link             id = application-question-complete
    the user clicks the button/link             link = Back to application overview
    the user should see the element             jQuery = li:contains("Application details") > .task-status-complete

Custom Suite Setup
    Set predefined date variables
    The guest user opens the browser
    Connect to database  @{database}

Custom Suite Teardown
    the user closes the browser
    Disconnect from database
