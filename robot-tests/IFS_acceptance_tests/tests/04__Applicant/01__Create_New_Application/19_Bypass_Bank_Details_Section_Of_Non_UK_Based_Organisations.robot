*** Settings ***
Documentation     IFS-7163  Non-UK based partner organisation will bypass bank details section in Project Setup
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
&{partner_applicant_credentials_non_UK_based}                              email=nicole.brown@gmail.com           password=Passw0rd
${partner_organisation_name_non_UK_based}                                  Red Planet
&{partner_applicant_credentials_zero_funding}                              email=json.smith@gmail.com             password=Passw0rd
${partner_organisation_name_zero_funding}                                  INNOVATE LTD
&{partner_applicant_credentials_UK_based}                              	   email=belle.smith@gmail.com            password=Passw0rd
${partner_organisation_name_UK_based}                                      SmithZone
&{lead_applicant_credentials}                              	               email=troy.ward@gmail.com              password=Passw0rd
${lead_applicant_organisation_name}                                        Ward Ltd
${application_name}                                                        PSC application 3
${competition_name}                                                        Project Setup Comp 3

*** Test Cases ***
External dashboard - hide the bank details if partner is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international                          ${partner_organisation_name_non_UK_based}
    And the user logs-in in new browser                                    &{partner_applicant_credentials_non_UK_based}
    When the user navigates to the page                                    ${server}/project-setup/project/${project_id}
    Then the user should not see the element                               jQuery = h2:contains("Bank details")

External dashboard - Show the bank details if partner is uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to uk based                               ${partner_organisation_name_UK_based}
    And Log in as a different user                                         &{partner_applicant_credentials_UK_based}
    When the user navigates to the page                                    ${server}/project-setup/project/${project_id}
    Then the user should see the element                                   jQuery = h2:contains("Bank details")


External dashboard - hide the bank details if partner organisation is requesting zero fund
    [Documentation]    IFS - 7163
    [Tags]
    Given lead applicant invites new partner and accepts invitation
    When partner organisation sets funding level to zero
    Then the user should not see the element                               jQuery = h2:contains("Bank details")

External dashboard - hide the bank details if lead organisation is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international                          ${lead_applicant_organisation_name}
    And Log in as a different user                                         &{lead_applicant_credentials}
    When the user navigates to the page                                    ${server}/project-setup/project/${project_id}
    Then the user should not see the element                               jQuery = h2:contains("Bank details")

External dashboard - show the bank details if lead organisation is uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to uk based                               ${lead_applicant_organisation_name}
    When reload page
    Then the user should see the element                                   jQuery = h2:contains("Bank details")

External dashboard - lead applicant - view status of partners - will show the bank details as not required for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    When the user clicks the button/link                                   link = View the status of partners
    Then the user should see the element                                   jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should not see the element                                jQuery = th:contains("Ward Ltd (Lead)") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should not see the element                                jQuery = th:contains("SmithZone") ~ td:nth-child(6) span:contains("Not required for this partner")

External dashboard - partner organisation - view status of partners - will show the bank details as not required for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given Log in as a different user                                       &{partner_applicant_credentials_non_UK_based}
    And The user navigates to the page                                     ${server}/project-setup/project/${project_id}
    When the user clicks the button/link                                   link = View the status of partners
    Then the user should see the element                                   jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should not see the element                                jQuery = th:contains("Ward Ltd") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should not see the element                                jQuery = th:contains("SmithZone") ~ td:nth-child(6) span:contains("Not required for this partner")

Project setup dashboard - Bank details - No action required should display for non uk based and zero funding organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given Lead applicant submits bank details
    And zero funding parter submits the project and team details
    And Non UK based partner submits the project and team details
    And UK based partner submits the project and team details
    When Log in as a different user                                        &{ifs_admin_user_credentials}
    And the user navigates to the page                                     ${server}/project-setup-management/competition/${competitionID}/status/all
    And the user clicks the button/link                                    jQuery = td:nth-child(6) a:contains("Review")
    Then the user should see the element                                   css = li:nth-child(1) strong
    And the user should see the element                                    css = li:nth-child(4) strong
    And the user should see the element                                    css = li.read-only:nth-child(2) div.task-status > span:nth-child(1)
    And the user should see the element                                    css = li.read-only:nth-child(3) div.task-status > span:nth-child(1)

Project setup dashboard - will not prevent the consortium's bank details from approval
    [Documentation]    IFS - 7163
    [Tags]
    Given the user clicks the button/link                                  link = ${lead_applicant_organisation_name}
    When the user clicks the button/link                                   jQuery = .govuk-button:contains("Approve bank account details")
    And the user clicks the button/link                                    jQuery = .govuk-button:contains("Approve account")
    And the user clicks the button/link                                    link = Bank details
    And the user clicks the button/link                                    link = ${partner_organisation_name_UK_based}
    And the user clicks the button/link                                    jQuery = .govuk-button:contains("Approve bank account details")
    And the user clicks the button/link                                    jQuery = .govuk-button:contains("Approve account")
    And the user clicks the button/link                                    link = Bank details
    Then the user should see the element                                   jQuery = li:nth-child(1) span:nth-child(1)
    And the user should see the element                                    jQuery = li:nth-child(4) span:nth-child(1)

*** Keywords ***
Custom Suite Setup
    Connect to Database   @{database}
    ${ProjectID} =        get project id by name                           ${application_name}
    Set suite variable    ${ProjectID}
    ${competitionID} =    get comp id from comp title                      ${competition_name}
    Set suite variable    ${competitionID}
    ${applicationId} =    get application id by name                       ${application_name}
    Set suite variable    ${applicationId}

Custom suite teardown
    the user closes the browser
    Disconnect from database

Lead applicant submits bank details
    Log in as a different user                                             &{lead_applicant_credentials}
    project lead submits project address                                   ${project_id}
    the user navigates to the page                                         ${server}/project-setup/project/${project_id}/team/project-manager
    the user selects the radio button                                      projectManager  projectManager1
    the user clicks the button/link                                        id = save-project-manager-button
    the user navigates to the page                                         ${server}/project-setup/project/${project_id}/team
    The user selects their finance contact                                 financeContact1
    the user clicks the button/link                                        link = Set up your project
    the user enters bank details

zero funding parter submits the project and team details
    Log in as a different user                                             &{partner_applicant_credentials_zero_funding}
    the user navigates to the page                                         ${server}/project-setup/project/${project_id}/details
    the user clicks the button/link                                        link = Edit
    the user enters text to a text field                                   id = postcode      P05T C0D3
    the user clicks the button/link                                        jQuery = button:contains("Save project location")
    the user clicks the button/link                                        id = return-to-set-up-your-project-button
    the user clicks the button/link                                        link = Project team
    the user clicks the button/link                                        link = Your finance contact
    the user selects the radio button                                      financeContact  financeContact1
    the user clicks the button/link                                        id = save-finance-contact-button

Non UK based partner submits the project and team details
    Log in as a different user                                             &{partner_applicant_credentials_non_UK_based}
    the user navigates to the page                                         ${server}/project-setup/project/${project_id}
    the user clicks the button/link                                        link = Project team
    the user clicks the button/link                                        link = Your finance contact
    the user selects the radio button                                      financeContact  financeContact1
    the user clicks the button/link                                        id = save-finance-contact-button

UK based partner submits the project and team details
    Log in as a different user                                             &{partner_applicant_credentials_UK_based}
    the user navigates to the page                                         ${server}/project-setup/project/${project_id}/team
    the user clicks the button/link                                        link = Your finance contact
    the user selects the radio button                                      financeContact  financeContact1
    the user clicks the button/link                                        id = save-finance-contact-button
    the user clicks the button/link                                        link = Return to setup your project
    the user enters bank details

lead applicant invites new partner and accepts invitation
    Log in as a different user                                             &{ifs_admin_user_credentials}
    the user navigates to the page                                         ${server}/project-setup-management/competition/${competitionID}/project/${project_id}/team/partner
    the user adds a new partner organisation                               innovate    jsonsmith    json.smith@gmail.com
    a new organisation is able to accept project invite                    json  smith   json.smith@gmail.com   innovate  INNOVATE LTD    ${applicationId}    ${application_name}

partner organisation sets funding level to zero
    The new partner can complete Your organisation
    the user clicks the button/link                                        link = Your funding
    the user selects the radio button                                      requestingFunding   false
    the user selects the radio button                                      otherFunding  false
    the user clicks the button/link                                        jQuery = button:contains("Mark as complete")
    the user accept the competition terms and conditions                   Return to join project
    the user clicks the button/link                                        id = submit-join-project-button
