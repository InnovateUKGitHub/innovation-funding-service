*** Settings ***
Documentation     INFUND-2601 As a competition administrator I want a view of all applications at the 'Funders Panel' stage
...
...               INFUND-7376 Updates to 'Funding decision' page display
...
...               INFUND-7377 Create a 'Manage funding applications' page to view and manage funding decision notifications
...
...               INFUND-8065 Filter on 'Funding decision' dashboard
...
...               INFUND-8624 Successful applications moved to 'Project setup' upon receiving funding decision notification email
...
...               INFUND-8854 Set competition state to Inform when ALL applications either set to Successful or Unsuccessful and final decision email sent
...
...               IFS-1620 Internal Project Setup dashboard: project visibility needed after successful notification not feedback released
...
...               IFS-2903 Identify root cause of failure in changing the Funding decision
...
...               IFS-3560 Email subject for application funded and funding to include competition name and application ID
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        CompAdmin  Applicant
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot
Resource          ../10__Project_setup/PS_Common.robot

*** Variables ***
${funders_panel_competition_url}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}
${application1Subject}    ${FUNDERS_PANEL_COMPETITION_NAME}: Notification regarding your application ${FUNDERS_PANEL_APPLICATION_1_NUMBER}: ${FUNDERS_PANEL_APPLICATION_1_TITLE}
${application2Subject}    Notification regarding your application ${FUNDERS_PANEL_APPLICATION_2_NUMBER}: ${FUNDERS_PANEL_APPLICATION_2_TITLE}
${onHoldMessage}          We have put your project on hold because our Assessment department is very busy at the moment.
${unsuccMessage}          We are sorry to annouce that your application has failed the assessment procedure.
${successMessage}         We are happy to inform you that your application is eligible for funding.

*** Test Cases ***
Funding decision buttons should be disabled
    [Documentation]    INFUND-2601
    [Tags]
    Given the user navigates to the page    ${funders_panel_competition_url}/funding
    Then the user should see the options to make funding decisions disabled

An application is selected and the buttons become enabled
    [Documentation]    INFUND-7377
    [Tags]
    When the user selects the checkbox      app-row-2
    Then the user should see the options to make funding decisions enabled
    When the user unselects the checkbox    app-row-2
    Then the user should see the options to make funding decisions disabled

Internal user puts the application on hold
    [Documentation]  INFUND-7376
    [Tags]
    Given the internal user marks the application as  On hold  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1

Proj Finance user can send Fund Decision notification
    [Documentation]  INFUND-7376 INFUND-7377 INFUND-8813, IFS-3560
    [Tags]
    [Setup]  log in as a different user      &{internal_finance_credentials}
    Given the user should see write and send email button is disabled
    When the user send funding decision email for all applicant
    Then the user should see a field and summary error  Please enter the email message.
    And the user cancels the process and needs to re-select the recipients
    When the internal sends the descision notification email to all applicants  ${onHoldMessage}
    Then the user should see the element     jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("Sent") ~ td:contains("${today}")

Internal user can filter notified applications
    [Documentation]  INFUND-7376 INFUND-8065
    [Tags]
    Given the user navigates to the page       ${funders_panel_competition_url}/manage-funding-applications
    Then the user filter applications by application number
    And the user filter applications by sent email status
    And the user filter applications by sent email status

External lead applicant and collaborators reads their funding decision emails
    [Documentation]  INFUND-7376  IFS-360
    [Tags]
    Given external lead applicant reads his email and checks status on his dashboard
    And external collaborators read their email

Unsuccessful Funding Decision
    [Documentation]  INFUND-7376 INFUND-7377
    [Tags]
    Given the internal user marks the application as  Unsuccessful  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1
    When the internal user sends an email notification  Unsuccessful  ${application1Subject}  ${unsuccMessage}  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    Then the external user reads his email and can see the correct status  Unsuccessful  ${application1Subject}  ${unsuccMessage}  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  ${test_mailbox_one}+fundsuccess@gmail.com

Successful Funding Decision
    [Documentation]  INFUND-7376 INFUND-7377  IFS-2903
    [Tags]
    Given the internal user marks the application as                         Successful  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1
    And the user checks that the statuses are changing correctly
    When the internal user sends an email notification                       Successful  ${application1Subject}  ${successMessage}  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    Then the external user reads his email and can see the correct status    Project in setup  ${application1Subject}  ${successMessage}  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  ${test_mailbox_one}+fundsuccess@gmail.com

Once Successful and Sent you cannot change your mind
    [Documentation]  INFUND-8651
    [Tags]
    Given log in as a different user                        &{internal_finance_credentials}
    When the user navigates to the page                     ${funders_panel_competition_url}/funding
    Then the user should not see the element                css = input[type = "checkbox"][value = "${FUNDERS_PANEL_APPLICATION_1_NUMBER}"]
    When the user navigates to the page                     ${funders_panel_competition_url}/manage-funding-applications
    Then the user should not see the element                css = input[type = "checkbox"][value = "${FUNDERS_PANEL_APPLICATION_1_NUMBER}"]
    And the user should see that the element is disabled    jQuery = button:contains("Write and send email")

Successful applications are turned into Project
    [Documentation]  INFUND-8624
    [Tags]
    Given log in as a different user      ${test_mailbox_one}+fundsuccess@gmail.com  ${short_password}
    Then the user should see the element  jQuery = .projects-in-setup li:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}")

Internal user can see the comp in Project Setup once applicant is notified
    [Documentation]  IFS-1620
    [Tags]
    Given log in as a different user                       &{Comp_admin1_credentials}
    When the user clicks the button/link                   jQuery = a:contains("Project setup")
    And the user should see the element                    jQuery = h2:not(".govuk-tabs__title"):contains("Project setup")
    Then the user clicks the button/link                   link = ${FUNDERS_PANEL_COMPETITION_NAME}
    And the user should be redirected to the correct page  ${Notified_Application_Competition_Status}

Once all final decisions have been made and emails are sent Comp moves to Inform status
    [Documentation]  INFUND-8854
    [Tags]
    Given the internal user marks the application as     Unsuccessful  ${FUNDERS_PANEL_APPLICATION_2_TITLE}  2
    And the internal user sends an email notification    Unsuccessful  ${application2Subject}  ${unsuccMessage}  ${FUNDERS_PANEL_APPLICATION_2_TITLE}  ${FUNDERS_PANEL_APPLICATION_2_NUMBER}
    Then the external user reads his email and can see the correct status  Unsuccessful  ${application2Subject}  ${unsuccMessage}  ${FUNDERS_PANEL_APPLICATION_2_TITLE}   worth.email.test.two+fundfailure@gmail.com
    Given log in as a different user                     &{Comp_admin1_credentials}
    When the user navigates to the page                  ${CA_Live}
    Then the user should see the element                 jQuery = section:contains("Inform") > ul:contains("${FUNDERS_PANEL_COMPETITION_NAME}")

*** Keywords ***
Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${today}  get today
    set suite variable  ${today}

the user should see the options to make funding decisions disabled
    the user should see the element  jQuery = button:contains("Successful")[disabled]
    the user should see the element  jQuery = button:contains("Unsuccessful")[disabled]
    the user should see the element  jQuery = button:contains("On hold")[disabled]

the user should see the options to make funding decisions enabled
    the user should not see the element  jQuery = button:contains("Successful")[disabled]
    the user should not see the element  jQuery = button:contains("Unsuccessful")[disabled]
    the user should not see the element  jQuery = button:contains("On hold")[disabled]

the user sets the funding decision of application
    [Arguments]    ${checkbox}    ${decision_button}
    the user selects the checkbox    ${checkbox}
    the user clicks the button/link    jQuery = button:contains("${decision_button}")

the user cancels the process and needs to re-select the recipients
    the user clicks the button/link  jQuery = a:contains("Cancel")
    the user should see the element  jQuery = button[disabled]:contains("Write and send email")
    the user selects the checkbox    app-row-${application_ids["${FUNDERS_PANEL_APPLICATION_1_TITLE}"]}
    the user clicks the button/link  jQuery = button:contains("Write and send email")

the internal user marks the application as
    [Arguments]  ${decision}  ${application}  ${tr}
    log in as a different user       &{Comp_admin1_credentials}
    the user navigates to the page   ${funders_panel_competition_url}
    the user clicks the button/link  jQuery = a:contains("Input and review funding decision")
    the user selects the checkbox    app-row-${tr}
    the user clicks the button/link  jQuery = button:contains("${decision}")
    the user should see the element  jQuery = td:contains("${application}") ~ td:contains("${decision}")
    the user clicks the button/link  jQuery = a:contains("Competition")

the internal user sends an email notification
    [Arguments]  ${decision}  ${subject}  ${message}  ${application}  ${id}
    the user navigates to the page          ${funders_panel_competition_url}
    the user clicks the button/link         jQuery = a:contains("Manage funding notifications")
    the user should see the element         jQuery = td:contains("${application}") ~ td:contains("${decision}")
    the user selects the checkbox           app-row-${id}
    the user clicks the button/link         jQuery = button:contains("Write and send email")
    the user enters text to a text field    css = .editor  ${message}
    the user clicks the button/link         jQuery = button:contains("Send email")[data-js-modal = "send-to-all-applicants-modal"]
    the user clicks the button/link         jQuery = .send-to-all-applicants-modal button:contains("Send email")
    the user should see the element         jQuery = td:contains("${application}") ~ td:contains("Sent") ~ td:contains("${today}")

the external user reads his email and can see the correct status
    [Arguments]  ${decision}  ${subject}  ${message}  ${application}  ${mail}
    log in as a different user       ${mail}  ${short_password}
    the user reads his email         ${mail}  ${subject}  ${message}
    the user navigates to the page   ${server}/applicant/dashboard
    the user should see the element  jQuery = div:contains("${application}") + div:contains("${decision}")

verify the user has received the on hold email
    [Arguments]  ${email_user}
    Given the external user reads his email and can see the correct status  Awaiting  ${application1Subject}  ${onholdmessage}  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  ${email_user}
    Then the user should not see the element  jQuery = div:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") + div:contains("Unsuccessful")
    And the user should not see the element   jQuery = div:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") + div:contains("Successful")

the internal user changes the funding decision status
    [Documentation]  IFS-2903
    [Arguments]  ${decision}  ${application}  ${tr}
    the user navigates to the page          ${funders_panel_competition_url}/funding
    the user selects the checkbox           app-row-${tr}
    the user clicks the button/link         jQuery = button:contains("${decision}")
    the user should see the element         jQuery = td:contains("${application}") ~ td:contains("${decision}")

the user checks that the statuses are changing correctly
    [Documentation]  IFS-2903
    the internal user changes the funding decision status    On hold  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1
    the internal user changes the funding decision status    Unsuccessful  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1
    the internal user changes the funding decision status    Successful  ${FUNDERS_PANEL_APPLICATION_1_TITLE}  1

the user should see write and send email button is disabled
    the user navigates to the page      ${funders_panel_competition_url}
    the user clicks the button/link     jQuery = a:contains("Manage funding notifications")
    the user should see the element     jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    the user should see the element     jQuery = button[disabled]:contains("Write and send email")

the user send funding decision email for all applicant
    the user selects the checkbox            app-row-${application_ids["${FUNDERS_PANEL_APPLICATION_1_TITLE}"]}
    the user clicks the button/link     jQuery = button:contains("Write and send email")
    the user should see the element      css = #subject[value^="<competition name>: Notification regarding your application <application number>: <application title>"]
    the user clicks the button/link     jQuery = summary:contains("Review list of recipients")[aria-expanded="false"]
    the user should see the element     jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    the user should not see the element  jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_2_TITLE}")
    the user clicks the button/link     css = button[data-js-modal="send-to-all-applicants-modal"]
    the user clicks the button/link     jQuery = .send-to-all-applicants-modal button:contains("Send email to all applicants")

the user filter applications by application number
    the user enters text to a text field   css = #stringFilter  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    the user clicks the button/link        jQuery = button:contains("Filter")
    the user should see the element        jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    the user clicks the button/link        jQuery = a:contains("Clear all filters")

the user filter applications by sent email status
    the user selects the option from the drop-down menu  No  id = sendFilter
    the user clicks the button/link                      jQuery = button:contains("Filter")
    the user should not see the element                  jQuery = td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")

external lead applicant reads his email and checks status on his dashboard
    verify the user has received the on hold email  ${test_mailbox_one}+fundsuccess@gmail.com
    log in as a different user                      ${test_mailbox_one}+fundsuccess@gmail.com   ${short_password}
    the user should see the element                 jQuery = .task:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ .status:contains("Application submitted")


external collaborators read their email
    verify the user has received the on hold email    ${lead_applicant}
    verify the user has received the on hold email    ${collaborator1_credentials["email"]}
    verify the user has received the on hold email    ${collaborator2_credentials["email"]}
    verify the user has received the on hold email    ${lead_applicant_alternative_user_credentials["email"]}
    verify the user has received the on hold email    ${collaborator1_alternative_user_credentials["email"]}
    verify the user has received the on hold email    ${collaborator2_alternative_user_credentials["email"]}