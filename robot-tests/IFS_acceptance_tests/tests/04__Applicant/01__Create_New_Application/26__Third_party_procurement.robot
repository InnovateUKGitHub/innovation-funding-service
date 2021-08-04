*** Settings ***
Documentation    IFS-10080  Third party procurement: Comp setup configuration
...
Suite Setup       Custom suite setup
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
${comp_name}    Third party procurement competition

*** Test Cases ***
Comp admin selects third party funder in funding information
    [Documentation]   IFS-10080
    Given comp admin creates procurement competition
    When the user fills in funding information for the third party procurement comp
    Then the user navigates to the page                                                ${CA_UpcomingComp}
    And the user should see the element                                                jQuery = h3 a:contains("Third party procurement competition")

User Applies to Third Party Competition
    [Documentation]  IFS-10083
    Given logged in user applies to competition                 ${comp_name}  3
    And the user fills in third-party Application details       ${appl_name}  ${tomorrowday}  ${month}  ${nextyear}
    And the applicant completes Application Team
    And the applicant marks EDI question as complete
    And the lead applicant fills all the questions and marks as complete(procurement)
    And the lead completes the questions with multiple answer choice and multiple appendices

    Then the third party applicant can view the strategic innovation terms and conditions



*** Keywords ***
Custom suite setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    Set predefined date variables
    Connect to database  @{database}

comp admin creates procurement competition
    the user navigates to the page                            ${CA_UpcomingComp}
    the user clicks the button/link                           jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details                  ${comp_name}  ${month}  ${nextyear}  Programme  NOT_AID  PROCUREMENT
    the user selects procurement Terms and Conditions
    the user fills in the CS Project eligibility              ${rto_type_id}    2   false   single-or-collaborative
    the user fills in the CS funding eligibility              false   Programme   NOT_AID
    the user selects the organisational eligibility to no     false
    the user fills in the CS Milestones                       PROJECT_SETUP   ${month}   ${nextyear}  No
    the user marks the procurement application as done        no   Programme
    the user clicks the button/link                           link = Public content
    the user fills in the Public content and publishes        procurement
    the user clicks the button/link                           link = Return to setup overview

the third party applicant can view the strategic innovation terms and conditions
    the user clicks the button/link                           link = Strategic Innovation Fund Governance Document
    the user should see the element                           jQuery = h1:contains("Strategic Innovation Fund Governance Document")
    the user should see the element                           jQuery =
