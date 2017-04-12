*** Settings ***
Documentation     INFUND-2601 As a competition administrator I want a view of all applications at the 'Funders Panel' stage
...
...               INFUND-7376 Updates to 'Funding decision' page display
...
...               INFUND-7377 Create a 'Manage funding applications' page to view and manage funding decision notifications
...
...               INFUND-8065 Filter on 'Funding decision' dashboard
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        CompAdmin  Applicant
Resource          ../../resources/defaultResources.robot

*** Variables ***
${funders_panel_competition_url}    ${server}/management/competition/${FUNDERS_PANEL_COMPETITION_NUMBER}
${onHoldSubject}  Your application has been put on hold
${onHoldMessage}  We have put your project on hold because our Assessment department is very busy at the moment.
${unsuccSubject}  Your Application was unsuccessful
${unsuccMessage}  We are sorry to annouce that your application has failed the assessment procedure.
${successSubject}  Your Application was successful
${successMessage}  We are happy to inform you that your application is eligible for funding.

*** Test Cases ***
Funding decision buttons should be disabled
    [Documentation]    INFUND-2601
    [Tags]
    When the user navigates to the page    ${funders_panel_competition_url}/funding
    Then the user should see the options to make funding decisions disabled

An application is selected and the buttons become enabled
    [Documentation]    INFUND-7377
    [Tags]
    When the user selects the checkbox    app-row-2
    Then the user should see the options to make funding decisions enabled
    When the user unselects the checkbox    app-row-2
    Then the user should see the options to make funding decisions disabled

Internal user puts the application on hold
    [Documentation]  INFUND-7376
    [Tags]  HappyPath
    Given the internal user marks the application as  On hold

Proj Finance user can send Fund Decision notification
    [Documentation]  INFUND-7376 INFUND-7377 INFUND-8813
    [Tags]  HappyPath
    [Setup]  log in as a different user      &{internal_finance_credentials}
    Given the user navigates to the page     ${funders_panel_competition_url}
    When the user clicks the button/link     jQuery=button:contains("Manage funding notifications")
    Then the user should see the element     jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    And the user should see the element      jQuery=button.disabled:contains("Write and send email")
    When the user selects the checkbox       app-row-63
    Then the user clicks the button/link     jQuery=button:contains("Write and send email")
    When the user clicks the button/link     jQuery=summary:contains("Review list of recipients")[aria-expanded="false"]
    Then the user should see the element     jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    And the user should not see the element  jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_2_TITLE}")
    When the user clicks the button/link     jQuery=button:contains("Send email to all applicants")
    Then the server side validation should be triggered
    When the user cancels the process needs to re-select the reciepients
    Then the user enters text to a text field  css=#subject  ${onHoldSubject}
    And the user enters text to a text field   css=.editor  ${onHoldMessage}
    And the user clicks the button/link        jQuery=button:contains("Send email to all applicants")
    Then the user should see the element       jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("Sent") ~ td:contains("${today}")

Internal user can filter notified applications
    [Documentation]  INFUND-7376 INFUND-8065
    [Tags]
    Given the user navigates to the page       ${funders_panel_competition_url}/manage-funding-applications
    When the user enters text to a text field  css=#stringFilter  ${FUNDERS_PANEL_APPLICATION_1_NUMBER}
    And the user clicks the button/link        jQuery=button:contains("Filter")
    Then the user should see the element       jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    When the user clicks the button/link       jQuery=a:contains("Clear all filters")
    And the user selects the option from the drop-down menu  No  id=sendFilter
    And the user clicks the button/link        jQuery=button:contains("Filter")
    Then the user should not see the element   jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")
    When the user selects the option from the drop-down menu  All  id=sendFilter
    And the user selects the option from the drop-down menu  ON_HOLD  id=fundingFilter
    And the user clicks the button/link        jQuery=button:contains("Filter")
    Then the user should see the element       jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("On hold")

External user reads his email
    [Documentation]  INFUND-7376
    [Tags]  HappyPath
    Given the external user reads his email and can see the correct status  Awaiting  ${onHoldSubject}  ${onholdmessage}
    Then the user should not see the element  jQuery=div:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") + div:contains("Unsuccessful")
    And the user should not see the element   jQuery=div:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") + div:contains("Successful")

Unsuccessful Funding Decision
    [Documentation]  INFUND-7376 INFUND-7377
    [Tags]
    Given the internal user marks the application as  Unsuccessful
    And the internal user sends an email notification  Unsuccessful  ${unsuccSubject}  ${unsuccMessage}
    Then the external user reads his email and can see the correct status  Unsuccessful  ${unsuccSubject}  ${unsuccMessage}

Successful Funding Decision
    [Documentation]  INFUND-7376 INFUND-7377
    [Tags]
    Given the internal user marks the application as  Successful
    And the internal user sends an email notification  Successful  ${successSubject}  ${successMessage}
    Then the external user reads his email and can see the correct status  Successful  ${successSubject}  ${successMessage}

*** Keywords ***
Custom Suite Setup
    delete the emails from both test mailboxes
    guest user log-in  &{Comp_admin1_credentials}
    ${today}  get today
    set suite variable  ${today}

the user should see the options to make funding decisions disabled
    the user should see the element  jQuery=button:contains("Successful")[disabled]
    the user should see the element  jQuery=button:contains("Unsuccessful")[disabled]
    the user should see the element  jQuery=button:contains("On hold")[disabled]

the user should see the options to make funding decisions enabled
    the user should not see the element  jQuery=button:contains("Successful")[disabled]
    the user should not see the element  jQuery=button:contains("Unsuccessful")[disabled]
    the user should not see the element  jQuery=button:contains("On hold")[disabled]

the user sets the funding decision of application
    [Arguments]    ${checkbox}    ${decision_button}
    the user selects the checkbox    ${checkbox}
    the user clicks the button/link    jQuery=button:contains("${decision_button}")

the server side validation should be triggered
    the user should see the element  jQuery=.error:contains("Please enter the email subject.")
    the user should see the element  jQuery=.error-summary-list:contains("Please enter the email subject.")
    the user should see the element  jQuery=.error:contains("Please enter the email message.")
    the user should see the element  jQuery=.error-summary-list:contains("Please enter the email message.")

the user cancels the process needs to re-select the reciepients
    the user clicks the button/link  jQuery=a:contains("Cancel")
    the user should see the element  jQuery=button.disabled:contains("Write and send email")
    the user selects the checkbox    app-row-63
    the user clicks the button/link  jQuery=button:contains("Write and send email")

the internal user marks the application as
    [Arguments]  ${decision}
    log in as a different user       &{Comp_admin1_credentials}
    the user navigates to the page   ${funders_panel_competition_url}
    the user clicks the button/link  jQuery=a:contains("Input and review funding decision")
    the user selects the checkbox    app-row-1
    the user clicks the button/link  jQuery=button:contains("${decision}")
    the user should see the element  jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("${decision}")
    the user clicks the button/link  jQuery=a:contains("Competition")

the internal user sends an email notification
    [Arguments]  ${decision}  ${subject}  ${message}
    the user navigates to the page   ${funders_panel_competition_url}
    the user clicks the button/link  jQuery=button:contains("Manage funding notifications")
    the user should see the element  jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("${decision}")
    the user selects the checkbox    app-row-63
    the user clicks the button/link  jQuery=button:contains("Write and send email")
    the user enters text to a text field  css=#subject  ${subject}
    the user enters text to a text field  css=.editor  ${message}
    the user clicks the button/link       jQuery=button:contains("Send email to all applicants")
    the user should see the element       jQuery=td:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") ~ td:contains("Sent") ~ td:contains("${today}")

the external user reads his email and can see the correct status
    [Arguments]  ${decision}  ${subject}  ${message}
    log in as a different user       ${test_mailbox_one}+fundsuccess@gmail.com  ${short_password}
    the user reads his email         ${test_mailbox_one}+fundsuccess@gmail.com  ${subject}  ${message}
    the user navigates to the page   ${server}/applicant/dashboard
    the user should see the element  jQuery=div:contains("${FUNDERS_PANEL_APPLICATION_1_TITLE}") + div:contains("${decision}")