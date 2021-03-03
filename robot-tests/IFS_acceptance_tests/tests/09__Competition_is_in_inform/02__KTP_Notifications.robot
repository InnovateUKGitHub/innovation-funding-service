*** Settings ***
Documentation     IFS-8549 KTP - Notification: unsuccessful and successful
...
...               IFS-8974 New project team members get ISE when trying to access application team page
...
Suite Setup       Custom Suite Setup
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${KTP_application}  	            KTP notifications application
${ktp_ProjectID}                    ${project_ids["${KTP_application}"]}
${KTP_applicationId}                ${application_ids["${KTP_application}"]}
${KTP_competiton}                   KTP notifications
${KTP_competitonId}                 ${competition_ids["${KTP_competiton}"]}
${ktp_LeadOrgName}                  A base of knowledge
${ktp_LeadOrgID}                    ${organisation_ids["${ktp_LeadOrgName}"]}
${ktp_PartnerOrgName}               Ludlow
${ktp_PartnerOrgId}                 ${organisation_ids["${ktp_PartnerOrgName}"]}
${ktp_KTA_email}                    hermen.mermen@ktn-uk.test
&{ktp_KTA_Credentials}              email=${ktp_KTA_email}    password=${short_password}
${ktp_Lead_email}                   bob@knowledge.base
&{ktp_Lead_Credentials}             email=${ktp_Lead_email}    password=${short_password}
${ktp_Partner_email}                jessica.doe@ludlow.co.uk
&{ktp_Partner_Credentials}          email=${ktp_Partner_email}    password=${short_password}
${MemberName}                       Member
${MemberEmail}                      member_email@gmail.com
&{MemberCredentials}                email=${MemberEmail}    password=${short_password}

*** Test Cases ***
Lead invites a new team member to the project
    [Documentation]  IFS-8974
    Given the user navigates to the page        ${server}/applicant/dashboard
    And the user should see the element         jQuery = .task:contains("${KTP_application}") ~ .status:contains("Project in setup")
    When The user clicks the button/link        link = ${KTP_application}
    Then the lead invites a team member to the project

Internal user marks the KTP application as unsuccessful
    [Documentation]  IFS-8549
    Given Log in as a different user                                          &{ifs_admin_user_credentials}
    And the user navigates to the page                                        ${server}/management/competition/${KTP_competitonId}
    When the user makes the application unsuccessful and sends notification
    Then Project users checks their email

Internal user checks the status of the application
    [Documentation]  IFS-8549
    Given the user navigates to the page                          ${server}/management/dashboard/previous
    When the user clicks the button/link                          link = ${KTP_competiton}
    And the user clicks the button/link                           jQuery = button:contains("Projects")
    Then the user should see the element                          jQuery = tbody div:contains("${KTP_application}") ~ div:contains("Unsuccessful")
    And the user cannot make any changes in the project setup

The lead checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              &{ktp_Lead_Credentials}
    Then the user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user cannot make any changes in Bank Details
    And the user is able to view the application overview page

The partner checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              &{ktp_Partner_Credentials}
    Then the user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user is able to view the application overview page

The KTA checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              &{ktp_KTA_Credentials}
    When the user clicks the button/link                          jQuery = h2:contains("Project setup")
    Then the user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user is able to view the application overview page

The new member is able to access the application overview
    [Documentation]  IFS-8974
    Given Log in as a different user                                &{MemberCredentials}
    And the user should see the element                             jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    When The user clicks the button/link                            link = ${KTP_application}
    Then the user is able to view the application overview page

Internal user sends funding notifications and releases feedback
    [Documentation]  IFS-8974
    Given Log in as a different user         &{ifs_admin_user_credentials}
    When the user navigates to the page      ${server}/management/competition/${KTP_competitonId}
    Then The user clicks the button/link     jQuery = button:contains("Release feedback")

The new member is able to access the application overview after feedback is released
    [Documentation]  IFS-8974
    Given Log in as a different user                                &{MemberCredentials}
    When The user clicks the button/link                            link = ${KTP_application}
    Then the user is able to view the application overview page

*** Keywords ***
Custom suite setup
    the user logs-in in new browser   &{ktp_Lead_Credentials}

the user makes the application unsuccessful and sends notification
     the user clicks the button/link    link = Input and review funding decision
     the user selects the checkbox      app-row-1
     the user clicks the button/link    jQuery = button:contains("Unsuccessful")
     the user should see the element    jQuery = td:contains("${KTP_application}") ~ td:contains("Unsuccessful")
     the user clicks the button/link    link = Competition
     the user clicks the button/link    link = Manage funding notifications
     the user selects the checkbox      app-row-301
     the user clicks the button/link    jQuery = button:contains("Write and send email")
     the user clicks the button/link    jQuery = button:contains("Send email to all applicants")
     the user clicks the button/link    jQuery = .send-to-all-applicants-modal button:contains("Send email to all applicants")
     the user should see the element    jQuery = td:contains("${KTP_application}") ~ td:contains("Unsuccessful") ~ td:contains("Sent")

the user cannot make any changes in the project setup
     the user clicks the button/link                                jQuery = tbody td:nth-child(2)
     the user cannot make any changes in Project details section
     the user clicks the button/link                                link = Back to ktp notifications
     the user clicks the button/link                                jQuery = tbody td:nth-child(3)
     the user cannot make any changes in Project team section
     the user clicks the button/link                                link = Back to ktp notifications
     the user clicks the button/link                                jQuery = td:nth-child(4)
     the user should not see the element                            link = Change monitoring officer
     the user clicks the button/link                                link = Back to ktp notifications
     the user clicks the button/link                                jQuery = td:nth-child(6)
     the user cannot make any changes in Finance checks section

the project user is unable to make any changes
     the user clicks the button/link                                link = ${KTP_application}
     the user clicks the button/link                                link = Project details
     the user cannot make any changes in Project details section
     the user clicks the button/link                                link = Return to set up your project
     the user clicks the button/link                                link = Project team
     the user cannot make any changes in Project team section
     the user clicks the button/link                                link = Return to set up your project

the user cannot make any changes in Project details section
     the user should not see the element     link = Correspondence address
     the user should not see the element     link = Edit

the user cannot make any changes in Project team section
     the user should not see the element     link = Project manager
     the user should not see the element     link = Your finance contact
     the user should not see the element     jQuery = button:contains("Add team member")

the user cannot make any changes in Finance checks section
     the user navigates to the page          ${server}/project-setup-management/competition/${KTP_competitonId}/project/${ktp_ProjectID}/duration
     the user should not see the element     id = durationInMonths
     the user navigates to the page          ${server}/project-setup-management/project/${ktp_ProjectID}/funding-level
     the user should not see the element     jQuery = td:contains("${ktp_LeadOrgName}") ~ td:nth-child(6):contains("30.00%") input[type='hidden']
     the user navigates to the page          ${server}/project-setup-management/project/${ktp_ProjectID}/finance-check
     the user clicks the button/link         jQuery = th:contains("${ktp_LeadOrgName}") ~ td a:contains("Review")
     the user should not see the element     css = a[href="?financeType=ASSOCIATE_SALARY_COSTS"]
     the user should not see the element     css = a[href="?financeType=ASSOCIATE_DEVELOPMENT_COSTS"]
     the user should not see the element     css = a[href="?financeType=KTP_TRAVEL"]
     the user should not see the element     css = a[href="?financeType=CONSUMABLES"]
     the user should not see the element     css = a[href="?financeType=KNOWLEDGE_BASE"]
     the user should not see the element     css = a[href="?financeType=ESTATE_COSTS"]
     the user should not see the element     css = a[href="?financeType=ASSOCIATE_SUPPORT"]
     the user should not see the element     css = a[href="?financeType=OTHER_COSTS"]

the user cannot make any changes in Bank Details
     the user clicks the button/link         link = Bank details
     the user should not see the element     id = accountNumber
     the user should not see the element     id = sortCode
     the user should not see the element     jQuery = button:contains("Submit bank account details")
     the user clicks the button/link         link = Set up your project

the user is able to view the application overview page
    the user clicks the button/link         link = view application overview
    the user should see the element         jQuery = h1:contains("Application overview")
    the user should see the element         jQuery = dt:contains("Application name:") ~ dd:contains("${KTP_application}")

Project users checks their email
    The user reads his email     ${ktp_KTA_email}               KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition
    The user reads his email     ${ktp_Partner_email}           KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition
    The user reads his email     ${ktp_Lead_email}              KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition
    The user reads his email     bobs.mate@knowledge.base       KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition
    The user reads his email     kevin.summers@ludlow.co.uk     KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition
    The user reads his email     ${MemberEmail}                 KTP notifications: Notification regarding your application ${KTP_applicationId}: ${KTP_application}     Thank you for submitting your application for this funding competition

the lead invites a team member to the project
    the user clicks the button/link          link = Project team
    the user clicks the button/link          jQuery = button:contains("Add team member")
    the user enters text to a text field     css = input[name=name]   ${MemberName}
    the user enters text to a text field     css = input[name=email]  ${MemberEmail}
    the user clicks the button/link          jQuery = button:contains("Invite to")
    Logout as user
    the new member accepts the invitation

the new member accepts the invitation
    the user reads his email and clicks the link   ${MemberEmail}  KTP notifications: ${KTP_application}: Invitation for project ${KTP_applicationId}.  You have been invited to join the project ${KTP_application} by ${ktp_LeadOrgName}.  1
    the member creates a new account               Memfname   Memlname   ${MemberEmail}

the member creates a new account
    [Arguments]   ${MemberFName}   ${MemberLName}   ${MemberEmail}
    the user should see the element     jQuery = h1:contains("Join a project")
    the user clicks the button/link     jQuery = a:contains("Create account")
    the user fills in account details   ${MemberFName}   ${MemberLName}
    the user clicks the button/link     jQuery = button:contains("Create account")
    the user verifies their account     ${MemberEmail}
    the user clicks the button/link     link = Sign in
    the user can view the project       ${MemberEmail}

The user can view the project
     [Arguments]  ${MemberEmail}
     Logging in and Error Checking     ${MemberEmail}   ${short_password}
     the user should see the element   link = ${KTP_application}



