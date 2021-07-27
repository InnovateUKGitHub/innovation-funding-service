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
    Then the user clicks the button/link                                               jQuery = a:contains("Complete")
    And the user clicks the button/link                                                jQuery = button:contains('Done')

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


