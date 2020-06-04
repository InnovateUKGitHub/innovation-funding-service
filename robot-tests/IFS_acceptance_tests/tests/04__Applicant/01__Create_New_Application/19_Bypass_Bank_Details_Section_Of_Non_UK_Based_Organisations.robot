*** Settings ***
Documentation     IFS-7163  Non-UK based partner organisation will bypass bank details section in Project Setup
...
...               IFS-7241  Correspondence address for non-UK based organisations


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
${correspondenceAddressTitle}           Correspondence address
${subTitleForCorrespondenceAddress}     This is the postal address for your organisation.
${countryValidationMessage}             You must select the country where your organisation is based.
${townOrCityValidationMessage}          You must enter your organisation's town or city.
${streetValidationMessage}              You must enter your organisation's street address.

*** Test Cases ***
External dashboard - hide the bank details if partner is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international                          ${partner_organisation_name_non_UK_based}
    And the user logs-in in new browser                                    &{partner_applicant_credentials_non_UK_based}
    When the user navigates to the page                                    ${server}/project-setup/project/${project_id}
    Then the user should not see the element                               jQuery = h2:contains("Bank details")

External dashboard - hide the bank details if lead organisation is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international                          ${lead_applicant_organisation_name}
    And Log in as a different user                                         &{lead_applicant_credentials}
    When the user navigates to the page                                    ${server}/project-setup/project/${project_id}
    Then the user should not see the element                               jQuery = h2:contains("Bank details")

Correspondence address field validations
    [Documentation]     IFS - 7241
    [Tags]
    Given the user navigates to the page                                ${server}/project-setup/project/${project_id}/details
    when the user clicks the button/link                                link = Correspondence address
    And the user check for correspondence address titles and info text
    And the user clicks the button/link                                 id = save-project-address-button
    Then the user should see field and summary validation messages

United kingdom should displaying in country list of correspondence address for non-uk based organisations
    [Documentation]     IFS - 7241
    [Tags]
    When The user enters text to an autocomplete field       id = country      United Kingdom
    Then the user sees the text in the text field            id = country      United Kingdom

non-uk based lead applicant can complete the correspondence address
    [Documentation]     IFS - 7241
    [Tags]
    When the user fills correspondence address data
    And the user clicks the button/link                                 id = save-project-address-button
    Then the user should see read only view of completed correspondence address details

non-uk based partner applicant can see the read only view of the corresponding address
    [Documentation]     IFS - 7241
    Given Log in as a different user        ${partner_organisation_name_non_UK_based}
    When the user navigates to the page     ${server}/project-setup/project/${project_id}/details
    Then the user should see read only view of completed correspondence address details


External dashboard - lead applicant - view status of partners - will show the bank details as not required for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given lead applicant invites new partner and accepts invitation
    And partner organisation sets funding level to zero
    When the user clicks the button/link                                   link = View the status of partners
    Then the user should see the element                                   jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Ward Ltd") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should not see the element                                jQuery = th:contains("SmithZone") ~ td:nth-child(6) span:contains("Not required for this partner")

External dashboard - partner organisation - view status of partners - will show the bank details as not required for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given Log in as a different user                                       &{partner_applicant_credentials_non_UK_based}
    And The user navigates to the page                                     ${server}/project-setup/project/${project_id}
    When the user clicks the button/link                                   link = View the status of partners
    Then the user should see the element                                   jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    And the user should see the element                                    jQuery = th:contains("Ward Ltd") ~ td:nth-child(6) span:contains("Not required for this partner")
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
    Then the user should see the element                                   css = li.read-only:nth-child(1) div.task-status > span:nth-child(1)
    And the user should see the element                                    css = li:nth-child(4) strong
    And the user should see the element                                    css = li.read-only:nth-child(2) div.task-status > span:nth-child(1)
    And the user should see the element                                    css = li.read-only:nth-child(3) div.task-status > span:nth-child(1)

Project setup dashboard - will not prevent the consortium's bank details from approval
    [Documentation]    IFS - 7163
    [Tags]
    When Comp admin approves bank details of partner organisation
    Then the user should see the element                                   jQuery = li:nth-child(4) span:nth-child(1)

Project setup dashboard - ifs admin can see the correspondence address entered by non uk based lead applicant
    [Documentation]     IFS - 7241
    When the user navigates to the page            ${server}/project-setup-management/competition/${competitionID}/project/${project_id}/details
    Then the user should see the element           jQuery = td:contains("Calle 11, San Sebastian, Argentina, X5187XAB")

Project setup dashboard - Monitoring office can see the correspondence address entered by non uk based lead applicant
    [Documentation]     IFS - 7241
    Given ifs admin assigns MO to the competition in project setup
    When the user navigates to the page       ${server}/project-setup-management/project/${project_id}/monitoring-officer
    Then the user should see the element      jQuery = p:contains("Argentina")

GOL template to be updated with country for correspondents address
    [Documentation]     IFS - 7241
    Given the user complete all sections of the project setup and generates GOL
    When the user clicks the button/link        link = View the grant offer letter page (opens in a new window)
    Then the user should see the element        jQuery = p:contains("Argentina")

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

Comp admin approves bank details of partner organisation
    the user clicks the button/link                                        link = ${partner_organisation_name_UK_based}
    the user clicks the button/link                                        jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link                                        jQuery = .govuk-button:contains("Approve account")
    the user clicks the button/link                                        link = Bank details

the user check for correspondence address titles and info text
    the user should see the element     jQuery = span:contains("${correspondenceAddressTitle}")
    the user should see the element     jQuery = p:contains("${subTitleForCorrespondenceAddress}")
    the user should see the element     link = Project details
    the user should see the element     jQuery = span:contains("Start typing your country's name and select it from the list")

the user fills correspondence address data
    the user enters text to a text field            id = addressLine1       Calle 11
    the user enters text to a text field            id = addressLine2       No 1111
    the user enters text to a text field            id = town               San Sebastian
    The user enters text to an autocomplete field   id = country            ARGENTINA
    the user enters text to a text field            id = zipCode            X5187XAB

the user should see read only view of completed correspondence address details
    the user should see the element     jQuery = td:contains("Calle 11, San Sebastian, Argentina, X5187XAB")
    the user should see the element     id = project-address-status

the user should see field and summary validation messages
    the user should see a field and summary error   ${countryValidationMessage}
    the user should see a field and summary error   ${townOrCityValidationMessage}
    the user should see a field and summary error   ${streetValidationMessage}

ifs admin assigns MO to the competition in project setup
    the user navigates to the page           ${server}/management/dashboard/project-setup
    the user clicks the button/link          link = Assign monitoring officers
    Search for MO                            Orvill  Orville Gibbs
    The internal user assign project to MO   ${applicationId}   ${application_name}

project finance approves Eligibility for Innovate uk organisation
    the user navigates to the page           ${server}/project-setup-management/project/${project_id}/finance-check/organisation/${organisationInnovateId}/eligibility
    the user approves project costs

external partner organisations submit the spend profile
    [Arguments]     ${username}  ${password}  ${project_id}  ${organisationID}
    log in as a different user          ${username}  ${password}
    the user navigates to the page      ${server}/project-setup/project/${project_id}/partner-organisation/${organisationID}/spend-profile
    the user clicks the button/link     link = Submit to lead partner

lead organisations submit the spend profile
    [Arguments]     ${project_id}  ${organisationID}
    log in as a different user          &{lead_applicant_credentials}
    the user navigates to the page      ${server}/project-setup/project/${project_id}/partner-organisation/${organisationID}/spend-profile/review
    the user clicks the button/link     name = mark-as-complete
    the user clicks the button/link     link = Review and submit project spend profile
    the user clicks the button/link     link = Submit project spend profile
    the user clicks the button/link     id = submit-send-all-spend-profiles

the user complete all sections of the project setup and generates GOL
    project manager submits both documents                      &{lead_applicant_credentials}     ${project_id}
    project finance approves both documents                     ${project_id}
    project finance approves Viability for                      ${organisationWardId}        ${project_id}
    project finance approves Viability for                      ${organisationInnovateId}    ${project_id}
    project finance approves Viability for                      ${organisationSmithId}       ${project_id}
    project finance approves Eligibility                        ${organisationWardId}        ${organisationSmithId}    ${organisationRedId}   ${project_id}
    project finance approves Eligibility for Innovate uk organisation
    the user clicks the button/link                             css = .generate-spend-profile-main-button
    the user clicks the button/link                             css = #generate-spend-profile-modal-button
    external partner organisations submit the spend profile     &{partner_applicant_credentials_non_UK_based}  ${project_id}   ${organisationRedId}
    external partner organisations submit the spend profile     &{partner_applicant_credentials_UK_based}       ${project_id}   ${organisationSmithId}
    external partner organisations submit the spend profile     &{partner_applicant_credentials_zero_funding}   ${project_id}   ${organisationInnovateId}
    lead organisations submit the spend profile                 ${project_id}   ${organisationWardId}
    proj finance approves the spend profiles
    the user navigates to the page                              ${server}/project-setup-management/project/${project_id}/grant-offer-letter/send




