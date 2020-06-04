*** Settings ***
Documentation     IFS-7163  Non-UK based partner organisation will bypass bank details section in Project Setup
...
...               IFS-7240  Project location details for non-UK based organisations

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/PS_Common.robot

*** Variables ***
&{partnerApplicantCredentialsNonUKBased}         email=nicole.brown@gmail.com           password=Passw0rd
${partnerOrganisationNameNonUKBased}             Red Planet
&{partnerApplicantCredentialsZeroFunding}        email=json.smith@gmail.com             password=Passw0rd
&{partnerApplicantCredentialsUKBased}            email=belle.smith@gmail.com            password=Passw0rd
${partnerOrganisationNameUKBased}                SmithZone
&{leadApplicantCredentials}                      email=troy.ward@gmail.com              password=Passw0rd
${leadApplicantOrganisationName}                 Ward Ltd
${applicationName}                               PSC application 3
${competitionName}                               Project Setup Comp 3
${applicationInProgress}                         Performance Application 4
${projectLocationInfo}                           Please enter the town or city where most of the project work will take place
${projectLocationValidationErrorMessage}         This field cannot be left blank.

*** Test Cases ***
External dashboard - hide the bank details if partner is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international       ${partnerOrganisationNameNonUKBased}
    And the user logs-in in new browser                 &{partnerApplicantCredentialsNonUKBased}
    When the user navigates to the page                 ${server}/project-setup/project/${project_id}
    Then the user should not see the element            jQuery = h2:contains("Bank details")

External dashboard - non-uk based partner applicant can complete the project location details
    [Documentation]    IFS - 7240
    [Tags]
    Given the user navigates to the page               ${server}/project-setup/project/${project_id}/organisation/${organisationRedId}/partner-project-location
    When the user should see project location details in project details section
    And the user enters text to a text field           id = town     delhi
    And the user clicks the button/link                jQuery = button:contains("Save project location")
    Then the user should see the element               jQuery = td:contains("Delhi")

Application form - non-uk based partner applicant can complete the project location details in project finances
    [Documentation]     IFS-7240
    [Tags]
    Given the user navigates to the page               ${server}/application/${applicationInProgressId}/form/your-project-location/organisation/${organisationRedId}/section/384
    When the user should see project location details in project finances
    And applicant enters project location details
    Then the user should see the element               jQuery = dd:contains("Äteritsiputeritsipuolilautatsijänkä")

External dashboard - hide the bank details if lead organisation is non-uk based
    [Documentation]    IFS - 7163
    [Tags]
    Given User sets organisation to international      ${leadApplicantOrganisationName}
    And Log in as a different user                     &{leadApplicantCredentials}
    When the user navigates to the page                ${server}/project-setup/project/${project_id}
    Then the user should not see the element           jQuery = h2:contains("Bank details")

Non-uk based project location validations
    [Documentation]    IFS - 7240
    Given the user navigates to the page                   ${server}/project-setup/project/${project_id}/organisation/${organisationWardId}/partner-project-location
    When the user should see project location details in project details section
    And the user clicks the button/link                    jQuery = button:contains("Save project location")
    Then the user should see a field and summary error     ${projectLocationValidationErrorMessage}

External dashboard - non-uk based lead applicant can complete the project location details
    [Documentation]    IFS - 7240
    [Tags]
    When the user enters text to a text field           id = town     mamungkukumpurangkuntjunya Hill
    And the user clicks the button/link                 jQuery = button:contains("Save project location")
    Then the user should see the element                jQuery = td:contains("Mamungkukumpurangkuntjunya Hill")

External dashboard - lead applicant - view status of partners - will show the bank details as not required for lead applicant organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given lead applicant invites new partner and accepts invitation
    And partner organisation sets funding level to zero
    When the user clicks the button/link               link = View the status of partners
    Then will show the bank details as not required for lead applicant organisations

External dashboard - partner organisation - view status of partners - will show the bank details as not required for non uk based and zero funding partner organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given Log in as a different user                   &{partnerApplicantCredentialsNonUKBased}
    And The user navigates to the page                 ${server}/project-setup/project/${project_id}
    When the user clicks the button/link               link = View the status of partners
    Then will show the bank details as not required for non uk based and zero funding partner organisations

Project setup dashboard - Bank details - No action required should display for non uk based and zero funding organisations
    [Documentation]    IFS - 7163
    [Tags]
    Given lead and partner applicants completes the project and bank details
    When Log in as a different user                    &{ifs_admin_user_credentials}
    And the user navigates to the page                 ${server}/project-setup-management/competition/${competitionID}/status/all
    And the user clicks the button/link                jQuery = td:nth-child(6) a:contains("Review")
    Then No action required should display for non uk based and zero funding organisations

Project setup dashboard - will not prevent the consortium's bank details from approval
    [Documentation]    IFS - 7163
    [Tags]
    When Comp admin approves bank details of partner organisation
    Then the user should see the element              jQuery = li:nth-child(4) span:nth-child(1)

Non-uk based organisations project location details updated in ifs admin project setup view
    [Documentation]     IFS-7240
    Given the user navigates to the page     ${server}/project-setup-management/competition/${competitionID}/status/all
    When the user clicks the button/link     jQuery = td:nth-child(2) a:contains("Complete")
    Then the user should see the element     jQuery = td:contains("Ward Ltd") ~ td:contains("Mamungkukumpurangkuntjunya Hill")
    And the user should see the element      jQuery = td:contains("Red Planet") ~ td:contains("Delhi")



*** Keywords ***
Custom Suite Setup
    Connect to Database   @{database}
    ${ProjectID} =        get project id by name                           ${applicationName}
    Set suite variable    ${ProjectID}
    ${competitionID} =    get comp id from comp title                      ${competitionName}
    Set suite variable    ${competitionID}
    ${applicationId} =    get application id by name                       ${applicationName}
    Set suite variable    ${applicationId}
    ${applicationInProgressId} =    get application id by name          ${applicationInProgress}
    Set suite variable     ${applicationInProgressId}

Custom suite teardown
    the user closes the browser
    Disconnect from database

Lead applicant submits bank details
    Log in as a different user                   &{leadApplicantCredentials}
    project lead submits project address         ${project_id}
    the user navigates to the page               ${server}/project-setup/project/${project_id}/team/project-manager
    the user selects the radio button            projectManager  projectManager1
    the user clicks the button/link              id = save-project-manager-button
    the user navigates to the page               ${server}/project-setup/project/${project_id}/team
    The user selects their finance contact       financeContact1
    the user clicks the button/link              link = Set up your project

zero funding parter submits the project and team details
    Log in as a different user                   &{partnerApplicantCredentialsZeroFunding}
    the user navigates to the page               ${server}/project-setup/project/${project_id}/details
    the user clicks the button/link              link = Edit
    the user enters text to a text field         id = postcode      P05T C0D3
    the user clicks the button/link              jQuery = button:contains("Save project location")
    the user clicks the button/link              id = return-to-set-up-your-project-button
    the user clicks the button/link              link = Project team
    the user clicks the button/link              link = Your finance contact
    the user selects the radio button            financeContact  financeContact1
    the user clicks the button/link              id = save-finance-contact-button

Non UK based partner submits the project and team details
    Log in as a different user                   &{partnerApplicantCredentialsNonUKBased}
    the user navigates to the page               ${server}/project-setup/project/${project_id}
    the user clicks the button/link              link = Project team
    the user clicks the button/link              link = Your finance contact
    the user selects the radio button            financeContact  financeContact1
    the user clicks the button/link              id = save-finance-contact-button

UK based partner submits the project and team details
    Log in as a different user                   &{partnerApplicantCredentialsUKBased}
    the user navigates to the page               ${server}/project-setup/project/${project_id}/team
    the user clicks the button/link              link = Your finance contact
    the user selects the radio button            financeContact  financeContact1
    the user clicks the button/link              id = save-finance-contact-button
    the user clicks the button/link              link = Return to setup your project
    the user enters bank details

lead applicant invites new partner and accepts invitation
    Log in as a different user                             &{ifs_admin_user_credentials}
    the user navigates to the page                         ${server}/project-setup-management/competition/${competitionID}/project/${project_id}/team/partner
    the user adds a new partner organisation               innovate    jsonsmith    json.smith@gmail.com
    a new organisation is able to accept project invite    json  smith   json.smith@gmail.com   innovate  INNOVATE LTD    ${applicationId}    ${application_name}

partner organisation sets funding level to zero
    The new partner can complete Your organisation
    the user clicks the button/link                         link = Your funding
    the user selects the radio button                       requestingFunding   false
    the user selects the radio button                       otherFunding  false
    the user clicks the button/link                         jQuery = button:contains("Mark as complete")
    the user accept the competition terms and conditions    Return to join project
    the user clicks the button/link                         id = submit-join-project-button

Comp admin approves bank details of partner organisation
    the user clicks the button/link     link = ${partnerOrganisationNameUKBased}
    the user clicks the button/link     jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link     jQuery = .govuk-button:contains("Approve account")
    the user clicks the button/link     link = Bank details

the user should see project location details in project details section
    the user should see the element     css = [for ="town"]
    the user should see the element     jQuery = span:contains("${projectLocationInfo}")
    the user should see the element     id = town
    the user should see the element     jQuery = button:contains("Save project location")

the user should see project location details in project finances
    the user should see the element     css = [for ="town"]
    the user should see the element     jQuery = span:contains("${projectLocationInfo}")
    the user should see the element     id = town
    the user should see the element     jQuery = button:contains("Save and return to project finances")

No action required should display for non uk based and zero funding organisations
    the user should see the element     css = li.read-only:nth-child(1) div.task-status > span:nth-child(1)
    the user should see the element     css = li:nth-child(4) strong
    the user should see the element     css = li.read-only:nth-child(2) div.task-status > span:nth-child(1)
    the user should see the element     css = li.read-only:nth-child(3) div.task-status > span:nth-child(1)

lead and partner applicants completes the project and bank details
    Lead applicant submits bank details
    zero funding parter submits the project and team details
    Non UK based partner submits the project and team details
    UK based partner submits the project and team details

will show the bank details as not required for non uk based and zero funding partner organisations
    the user should see the element       jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element       jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element       jQuery = th:contains("Ward Ltd") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should not see the element   jQuery = th:contains("SmithZone") ~ td:nth-child(6) span:contains("Not required for this partner")

will show the bank details as not required for lead applicant organisations
    the user should see the element       jQuery = th:contains("INNOVATE LTD") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element       jQuery = th:contains("Red Planet") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should see the element       jQuery = th:contains("Ward Ltd") ~ td:nth-child(6) span:contains("Not required for this partner")
    the user should not see the element   jQuery = th:contains("SmithZone") ~ td:nth-child(6) span:contains("Not required for this partner")

applicant enters project location details
    the user enters text to a text field     id = town     Äteritsiputeritsipuolilautatsijänkä
    the user clicks the button/link          id = mark_as_complete
    the user clicks the button/link          link = Your project location