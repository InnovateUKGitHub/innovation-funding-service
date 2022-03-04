*** Settings ***
Documentation    IFS-11442 OFGEM: Create a "ThirdParty" generic template
...
...
Suite Setup       Custom suite setup
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${thirdPartyOfgemCompetitionName}    Third party ofgem competition
${thirdPartyOfgemApplicationName}    Third party ofgem application

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

*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database              @{database}

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