*** Settings ***
Documentation     INFUND-6376 As a partner I want to be shown information in IFS when I have successfully completed Project Setup so I am clear on what steps to take now the project is live
...
...               IFS-8707 - Allow Users with LIVE PROJECTS USER role to Create Application
...

Resource          ../../resources/common/PS_Common.robot
Suite Setup       Project fiance approves the grant offer letter
Suite Teardown    Close browser and delete emails

*** Variables ***
${secondKTPOrgName}                   The University of Reading
${applicantKTACredentials}               john.fenton@ktn-uk.test
${email}                                 steve.smith@empire.com


*** Test Cases ***
Project dashboard shows message that the project is live
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    Given the lead partner logs in and navigate to applications dashboard
    When the user navigates to the page              ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is now live and you can review its progress.")

Status indicators should not show
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user should not see the element    link = status of my partners
    And the user should not see the element     css.complete
    And the user should not see the element     css = .action
    And the user should not see the element     css = .waiting

Project details section is read-only
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Project details
    Then the user should not see the element    link = Target start date
    And the user should not see the element     link = Correspondence address
    And the user should not see the element     link = Project Manager
    And the user should not see the element     link = ${Crystalrover_Name}
    [Teardown]    the user goes back to the previous page

Project Team section is read-only
    [Documentation]    IFS-7735
    When the user clicks the button/link        link = Project team
    Then the user should not see the element    link = Your finance contact
    And the user should not see the element     name = add-team-member
    And the user should not see the element     jQuery = button:contains("Remove")
    [Teardown]    the user goes back to the previous page

Bank details section is read-only
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Bank details
    Then the user should not see the element    name = accountNumber
    And the user should not see the element     name = sortCode
    And the user should not see the element     jQuery = .govuk-button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Spend profile
    And the user clicks the button/link         link = ${Zummacity_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Jabbertype_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Crystalrover_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Documents section is read-only
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link       link = Documents
    Then the use can see the mandatory documents
    [Teardown]    the user clicks the button/link    link = Set up your project

Grant offer letter section is read-only
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Grant offer letter
    Then the user should not see the element    jQuery = .govuk-button:contains("Send signed offer letter")
    And the user should not see the element     jQuery = button:contains("Remove")
    And the user should not see the element     name = signedGrantOfferLetter

Project dashboard shows message that the project is live for industrial partner
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    Given log in as a different user                 ${PS_LP_Application_Partner_Email}  ${short_password}
    When the user navigates to the page              ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is now live and you can review its progress.")
    When the lead partner logs in and navigate to applications dashboard
    And the user navigates to the page               ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is now live and you can review its progress.")

Status indicators should not show for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user should not see the element    link = status of my partners
    And the user should not see the element     css.complete
    And the user should not see the element     css = .action
    And the user should not see the element     css = .waiting

Project details section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link        link = Project details
    Then the user should not see the element    link = Target start date
    And the user should not see the element     link = Correspondence address
    And the user should not see the element     link = Project Manager
    And the user should not see the element     link = ${Crystalrover_Name}
    [Teardown]    the user goes back to the previous page

Bank details section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Bank details
    Then the user should not see the element    name = accountNumber
    And the user should not see the element     name = sortCode
    And the user should not see the element     jQuery = .govuk-button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link        link = Spend profile
    And the user clicks the button/link         link = ${Zummacity_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Jabbertype_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Crystalrover_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Documents section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    When the user clicks the button/link       link = Documents
    Then the use can see the mandatory documents
    [Teardown]    the user clicks the button/link    link = Set up your project

Grant offer letter section is read-only for industrial partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link        link = Grant offer letter
    Then the user should not see the element    jQuery = .govuk-button:contains("Send signed offer letter")
    And the user should not see the element     jQuery = button:contains("Remove")
    And the user should not see the element     name = signedGrantOfferLetter

Project dashboard shows message that the project is live for academic partner
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    Given log in as a different user                 ${PS_LP_Application_Academic_Email}  ${short_password}
    When the user navigates to the page              ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is now live and you can review its progress.")
    When the lead partner logs in and navigate to applications dashboard
    And the user navigates to the page               ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is now live and you can review its progress.")

Status indicators should not show for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user should not see the element    link = status of my partners
    And the user should not see the element     css.complete
    And the user should not see the element     css = .action
    And the user should not see the element     css = .waiting

Project details section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link        link = Project details
    Then the user should not see the element    link = Target start date
    And the user should not see the element     link = Correspondence address
    And the user should not see the element     link = Project Manager
    And the user should not see the element     link = ${Crystalrover_Name}
    [Teardown]    the user goes back to the previous page

Bank details section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link        link = Bank details
    Then the user should not see the element    name = accountNumber
    And the user should not see the element     name = sortCode
    And the user should not see the element     jQuery = .govuk-button:contains("Submit bank account details")
    [Teardown]    the user goes back to the previous page

Spend profile section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link        link = Spend profile
    And the user clicks the button/link         link = ${Zummacity_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Jabbertype_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("Edit spend profile")
    And the user should not see the element     jQuery = .govuk-button:contains("Allow edits")
    And the user goes back to the previous page
    When the user clicks the button/link        link = ${Crystalrover_Name}
    Then the user should not see the element    jQuery = .govuk-button:contains("edit spend profile")
    And the user goes back to the previous page
    [Teardown]    the user goes back to the previous page

Documents section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link       link = Documents
    Then the use can see the mandatory documents
    [Teardown]  the user clicks the button/link    link = Set up your project

Grant offer letter section is read-only for academic partner
    [Documentation]    INFUND-6376
    [Tags]
    When the user clicks the button/link                link = Grant offer letter
    Then the user should not see the element            jQuery = .govuk-button:contains("Send signed offer letter")
    And the user should not see the element             jQuery = button:contains("Remove")
    And the user should not see the element             name = signedGrantOfferLetter

PM should see project tab on dashboard once GOL is approved
    [Documentation]  IFS-4959
    Given the user clicks the button/link                    link = Dashboard
    And the user clicks the application tile if displayed
    When the project is sent to acc
    And log in as a different user                           ${PS_LP_Application_Academic_Email}  ${short_password}
    Then the user should see the element                     id = dashboard-link-LIVE_PROJECTS_USER
    And the user should see the element                      jQuery = h2:contains("Projects")

MO sould see project tab on dashboard once GOL is approved
    [Documentation]
    Given Log in as a different user  &{monitoring_officer_one_credentials}
    Then the user should see the element     id = dashboard-link-LIVE_PROJECTS_USER
    And the user should see the element      jQuery = h2:contains("Projects")

Live Project User is able to create a new application
    [Documentation]  IFS-8707
     Given the internal user approve the GOL      50
     When Log in as a different user            &{leadApplicantCredentials}
     And the user select the competition and starts application     KTP new competition
     And the user selects a knowledge based organisation    Reading   The University of Reading
     Then The user should see the element         jQuery = h1:contains("Application overview")

Live Project User is able to join an application within the same organisation
    [Documentation]  IFS-8707
     Given the user clicks the button/link    link = Application team
     When The user clicks the button/link      jQuery = button:contains("Add person to ${secondKTPOrgName}")
     And the user invites a person to the same organisation     Troy Ward  troy.ward@gmail.com
     And Logout as user
     And the user reads his email and clicks the link     troy.ward@gmail.com  Invitation to contribute in KTP new competition  You are invited by Steve Smith to participate in an application for funding through the Innovation Funding Service.  2
     And the user clicks the button/link               jQuery = a:contains("Continue")
     And the user logs in                      troy.ward@gmail.com  ${short_password}
     And The user clicks the button/link    jQuery = a:contains("Confirm and accept invitation")
     And The user clicks the button/link    jQuery = a:contains("Application team")
     Then The user should see the element         jQuery = td:contains("Troy Ward")


Live project user is able to join an application as a different organisation
    [Documentation]  IFS-8707
     Given Log in as a different user            ${applicantKTACredentials}  ${short_password}
     When the user select the competition and starts application     KTP new competition
     And the user selects a knowledge based organisation    Reading     The University of Reading
     And the user fills in the inviting steps    edward.morris@gmail.com
     And Logout as user
     And the user reads his email and clicks the link   edward.morris@gmail.com  Invitation to collaborate in KTP new competition  You are invited by John Fenton to participate in an application for funding through the Innovation Funding Service.  2
     And the user clicks the button/link               jQuery = a:contains("Continue")
     And the user logs in                              edward.morris@gmail.com  ${short_password}
     And the user clicks the button/link               jQuery = button:contains("Save and continue")
     And the user clicks the button/link               link = Application team
     Then The user should see the element              jQuery = td:contains("Edward Morris")


*** Keywords ***
the user logs in
    [Arguments]   ${email}   ${short_password}
    the guest user inserts user email and password   ${email}  ${short_password}
    the guest user clicks the log-in button

the user selects a knowledge based organisation
    [Arguments]   ${knowledgeBase}  ${completeKBOrganisartionName}
    input text                          id = knowledgeBase        ${knowledgeBase}
    the user clicks the button/link     jQuery = ul li:contains("${completeKBOrganisartionName}")
    the user clicks the button/link      JQuery = button:contains("Confirm")
    the user clicks the button/link      JQuery = button:contains("Save and continue")


project setup is completed and project is now live
    log in as a different user         &{leadApplicantCredentials}
    the user navigates to the page     ${server}/project-setup-management/competition/44/compeition/85/status/all
    the user clicks the button/link    xPath = /html/body/div[4]/main/div/section/div/table/tbody/tr[2]/td[6]/a
    the user selects the radio button

grant offer letter is sent to users
    the user logs-in in new browser    &{internal_finance_credentials}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_LP_Application_Project_Id}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${valid_pdf}
    the user selects the checkbox      confirmation
    the user clicks the button/link    id = send-gol
    the user clicks the button/link    jQuery = .modal-accept-send-gol .govuk-button:contains("Send grant offer letter")

users upload signed grant offer letter and submit
    the lead partner logs in and navigate to applications dashboard
    the user clicks the button/link    link = ${PS_LP_Application_Title}
    the user clicks the button/link    link = Grant offer letter
    the user uploads the file          id = signedGrantOfferLetter    ${valid_pdf}
    the user clicks the button/link    jQuery = a:contains("Send to Innovate UK")
    the user clicks the button/link    id = submit-gol-for-review

Project fiance approves the grant offer letter
    grant offer letter is sent to users
    users upload signed grant offer letter and submit
    log in as a different user         &{internal_finance_credentials}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_LP_Application_Project_Id}/grant-offer-letter/send
    the user selects the radio button  approvalType  acceptGOL
    the user clicks the button/link    id = submit-button
    the user clicks the button/link    jQuery = .modal-accept-signed-gol .govuk-button:contains("Accept signed grant offer letter")

the use can see the mandatory documents
    the user clicks the button/link        link = Collaboration agreement
    the user clicks the button/link        link = ${valid_pdf} (opens in a new window)
    Select Window                          NEW
    the user closes the last opened tab
    the user clicks the button/link        link = Return to documents
    the user clicks the button/link        link = Exploitation plan
    the user clicks the button/link        link = ${valid_pdf} (opens in a new window)
    Select Window                          NEW
    the user closes the last opened tab
    the user clicks the button/link        link = Return to documents

the lead partner logs in and navigate to applications dashboard
    log in as a different user      ${PS_LP_Application_Lead_PM_Email}  ${short_password}
    ${status}   ${value} =   Run Keyword And Ignore Error Without Screenshots  the user should see the element  id = dashboard-link-APPLICANT
    Run Keyword If  '${status}' == 'PASS'  Run keyword   the user clicks the button/link    id = dashboard-link-APPLICANT

the project is sent to acc
    Connect to database  @{database}
    execute sql string   UPDATE `${database_name}`.`grant_process` SET `pending`='1' WHERE `application_id`='${PS_LP_Application_No}';
    #The sleep is necessary as the grant table is read as part of a cron job which runs every 1 min
    sleep  60s
    Disconnect from database