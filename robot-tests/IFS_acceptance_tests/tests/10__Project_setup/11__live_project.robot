*** Settings ***
Documentation     INFUND-6376 As a partner I want to be shown information in IFS when I have successfully completed Project Setup so I am clear on what steps to take now the project is live
Resource          PS_Common.robot
Suite Setup       Project fiance approves the grant offer letter
Suite Teardown    Close browser and delete emails

*** Test Cases ***
Project dashboard shows message that the project is live
    [Documentation]    INFUND-6376
    [Tags]  HappyPath
    Given the lead partner logs in and navigate to applications dashboard
    When the user navigates to the page              ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")
    When log in as a different user                  ${PS_LP_Application_Lead_PM_Email}  ${short_password}
    And the user navigates to the page               ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")

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
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")
    When the lead partner logs in and navigate to applications dashboard
    And the user navigates to the page               ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")

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
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")
    When the lead partner logs in and navigate to applications dashboard
    And the user navigates to the page               ${server}/project-setup/project/${PS_LP_Application_Project_Id}
    Then the user should see the element             jQuery = .success-alert:contains("The project is live, you can review progress at ")

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
    Given the user clicks the button/link    link = Dashboard
    And the user should not see the element  id = dashboard-link-LIVE_PROJECTS_USER
    When the project is sent to acc
    And log in as a different user           ${PS_LP_Application_Academic_Email}  ${short_password}
    Then the user should see the element     id = dashboard-link-LIVE_PROJECTS_USER
    And the user should see the element      jQuery = h2:contains("Projects")

MO sould see project tab on dashboard once GOL is approved
    [Documentation]
    Given Log in as a different user  &{monitoring_officer_one_credentials}
    Then the user should see the element     id = dashboard-link-LIVE_PROJECTS_USER
    And the user should see the element      jQuery = h2:contains("Projects")

*** Keywords ***
grant offer letter is sent to users
    the user logs-in in new browser    &{internal_finance_credentials}
    the user navigates to the page     ${server}/project-setup-management/project/${PS_LP_Application_Project_Id}/grant-offer-letter/send
    the user uploads the file          grantOfferLetter  ${valid_pdf}
    the user selects the checkbox      confirmation
    the user clicks the button/link    id = send-gol
    the user clicks the button/link    jQuery = .modal-accept-send-gol .govuk-button:contains("Publish to project team")

users upload signed grant offer letter and submit
    the lead partner logs in and navigate to applications dashboard
    the user clicks the button/link    link = ${PS_LP_Application_Title}
    the user clicks the button/link    link = Grant offer letter
    choose file                        signedGrantOfferLetter    ${upload_folder}/${valid_pdf}
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
    the user clicks the button/link        link = ${valid_pdf}
    Select Window                          NEW
    the user closes the last opened tab
    the user clicks the button/link        link = Return to documents
    the user clicks the button/link        link = Exploitation plan
    the user clicks the button/link        link = ${valid_pdf}
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