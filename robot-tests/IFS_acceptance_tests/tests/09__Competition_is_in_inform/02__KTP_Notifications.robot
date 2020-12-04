*** Settings ***
Documentation     IFS-8549 KTP - Notification: unsuccessful and successful
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
&{ktp_KTA}                          email=hermen.mermen@ktn-uk.test    password=${short_password}
&{ifs_Admin}                        email=arden.pimenta@innovateuk.test     password=${short_password}
&{ktp_Lead}                         email=bob@knowledge.base    password=${short_password}
&{ktp_Partner}                      email=jessica.doe@ludlow.co.uk    password=${short_password}

*** Test Cases ***
Internal user marks the KTP application as unsuccessful
    [Documentation]  IFS-8549
    When the user navigates to the page                                        ${server}/management/competition/${KTP_competitonId}
    Then the user makes the application unsuccessful and sends notification

Internal user checks the status of the application
    [Documentation]  IFS-8549
    Given The user navigates to the page                          ${server}/management/dashboard/previous
    When The user clicks the button/link                          link = ${KTP_competiton}
    And The user clicks the button/link                           jQuery = button:contains("Projects")
    Then the user should see the element                          jQuery = tbody div:contains("${KTP_application}") ~ div:contains("Unsuccessful")
    And the user cannot make any changes in the project setup

The lead checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              bob@knowledge.base    Passw0rd1357
    Then The user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user cannot make any changes in Bank Details
    And the user is able to view the application overview page

The partner checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              jessica.doe@ludlow.co.uk    Passw0rd1357
    Then The user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user is able to view the application overview page

The KTA checks the status of the application
    [Documentation]  IFS-8549
    Given Log in as a different user                              hermen.mermen@ktn-uk.test    Passw0rd1357
    When The user clicks the button/link                          jQuery = h2:contains("Project setup")
    Then The user should see the element                          jQuery = h2:contains("Previous") ~ ul li:contains("${KTP_application}"):contains("Unsuccessful")
    And the project user is unable to make any changes
    And the user is able to view the application overview page

Project users checks their email
    The user reads his email     hermen.mermen@ktn-uk.test     KTP notifications: Notification regarding your application 301: KTP notifications application     Thank you for submitting your application for this funding competition
    The user reads his email     jessica.doe@ludlow.co.uk      KTP notifications: Notification regarding your application 301: KTP notifications application     Thank you for submitting your application for this funding competition
    The user reads his email     bob@knowledge.base            KTP notifications: Notification regarding your application 301: KTP notifications application     Thank you for submitting your application for this funding competition

*** Keywords ***
Custom suite setup
    the user logs-in in new browser   &{ifs_admin_user_credentials}

Requesting KTP Organisation IDs
    ${ktpLeadOrgID} =  get organisation id by name     ${ktpLeadOrgName}
    Set suite variable      ${ktpLeadOrgID}
    ${ktpPartnerOrgId} =  get organisation id by name    ${ktpPartnerOrgName}
    Set suite variable      ${ktpPartnerOrgId}

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
#     the user should not see the element     css = a[href="?financeType=ASSOCIATE_SALARY_COSTS"]
#     the user should not see the element     css = a[href="?financeType=ASSOCIATE_DEVELOPMENT_COSTS"]
#     the user should not see the element     css = a[href="?financeType=KTP_TRAVEL"]
#     the user should not see the element     css = a[href="?financeType=CONSUMABLES"]
#     the user should not see the element     css = a[href="?financeType=KNOWLEDGE_BASE"]
#     the user should not see the element     css = a[href="?financeType=ESTATE_COSTS"]
#     the user should not see the element     css = a[href="?financeType=ASSOCIATE_SUPPORT"]
#     the user should not see the element     css = a[href="?financeType=OTHER_COSTS"]

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
