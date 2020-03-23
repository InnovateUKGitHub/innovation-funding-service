*** Settings ***
Documentation     INFUND-8942 - Filter and sorting on 'Ineligible applications' dashboard
...
...               INFUND-7374 - As a member of the competitions team I can inform an applicant that their application is ineligible so that they know their application is not being sent for assessment
...
...               INFUND-7373 - As a member of the competitions team I can view a list of ineligible applications so that I know which applications have been marked as ineligible and which applicants have been informed
...
...               INFUND-9130 - Applicant dashboard: Application moved from 'Application in progress' section of dashboard to 'Previous applications' section
...
...               INFUND-7370 - As a member of the competitions team I can mark a submitted application as ineligible so that the application is not sent to be assessed
...
...               INFUND-8941 - As a member of the competitions team I can reinstate an application that as been marked as Ineligible
...
...               IFS-986 - Innovation Leads: Enable 'Mark as ineligible' for applications
...
...               IFS-1458 View unsuccessful applications after Inform state: initial navigation
...
...               IFS-1459 View unsuccessful applications after Inform state: list
...
...               IFS-1491 Inform Applicant - Ineligible page - couple of issues
...
...               IFS-3132 Email content templates for notifications
...
...               IFS-2994 New Stakeholder role and permissions
...
...               IFS-6021 External applicant dashboard - reflect internal Previous Tab behaviour
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot
Resource          ../02__Competition_Setup/CompAdmin_Commons.robot

*** Variables ***
${ineligibleApplication}  Living with Virtual Reality
${ineligibleApplicationNumber}    ${application_ids['${ineligibleApplication}']}
${ineligibleApplicationOverview}  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/application/${ineligibleApplicationNumber}
${ineligibleApplications}  ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/ineligible
${ineligibleMessage}  On checking your application we found that it did not meet these requirements.
# ${IN_ASSESSMENT_COMPETITION} is the Sustainable living models for the future
${submittedApplication}           Living with Digital Rights Management
${submittedApplicationNumber}     ${application_ids['${submittedApplication}']}

*** Test Cases ***
A non submitted application cannot be marked as ineligible
    [Documentation]    INFUND-7370
    [Tags]
    Given the user navigates to the page           ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/all
    Then the user should see the element           jQuery = td:contains("Rainfall") ~ td:contains("Started")
    When the user clicks the button/link           link = ${application_ids["Rainfall"]}
    Then the user should not see the element       jQuery = .govuk-details__summary:contains("Mark application as ineligible")
    [Teardown]    the user clicks the button/link  jQuery = .govuk-back-link:contains("Back")

Ineligigle button is shown on submitted applications
    [Documentation]    INFUND-7370
    [Tags]
    Given the user should see the element  jQuery = td:contains("${ineligibleApplication}") ~ td:contains("Submitted")
    When the user clicks the button/link   link = ${ineligibleApplicationNumber}
    Then the user should see the element   jQuery = .govuk-details__summary:contains("Mark application as ineligible")

Clicking the ineligible button
    [Documentation]  INFUND-7370 IFS-986
    [Tags]  InnovationLead
    [Setup]  log in as a different user     &{innovation_lead_one}
    Given the user navigates to the page    ${ineligibleApplicationOverview}
    Then the user checks for server side validation

Cancel marking the application as ineligible
    [Documentation]  INFUND-7370 IFS-986
    [Tags]  InnovationLead
    When the user clicks the button/link      jQuery = .button-clear:contains("Cancel")
    Then the user should not see the element  css = [aria-hidden = "false"] [id = "ineligibleReason"]

Client side validation - mark an application as ineligible
    [Documentation]  IFS-159
    [Tags]  InnovationLead
    Given the user clicks the button/link                   jQuery = .govuk-details__summary:contains("Mark application as ineligible")
    And the user enters multiple strings into a text field  id = ineligibleReason  a${SPACE}  402
    Then the user should see a field error                  Maximum word count exceeded. Please reduce your word count to 400.
    When the user enters text to a text field               id = ineligibleReason  This is the reason of ineligibility.
    [Teardown]  the user clicks the button/link             jQuery = .govuk-details__summary:contains("Mark application as ineligible")

Marking an application as ineligible moves it to the ineligible view
    [Documentation]  INFUND-7370 IFS-986
    [Tags]  InnovationLead
    Given the user marks an application as ineligible
    Then the user should be redirected to the correct page  ${ineligibleApplications}
    And the user should see the element         jQuery = td:contains("${ineligibleApplication}")
    And the user should not see the element     jQuery = td:contains("${ineligibleApplication}") ~ td > a:contains("Inform applicant")

Sort ineligible applications by lead
    [Documentation]  INFUND-8942  IFS-986
    [Tags]  InnovationLead
    When the application list is sorted by  Lead
    Then the applications should be sorted by column  3

Filter ineligible applications
    [Documentation]    INFUND-8942
    [Tags]
    [Setup]  log in as a different user        &{Comp_admin1_credentials}
    Given the user navigates to the page       ${ineligibleApplications}
    Then the user filters ineligible applications

Support user should see the inelibible application with reason
    [Documentation]  IFS-6152
    Given log in as a different user            &{support_user_credentials}
    When the user enters text to a text field   id = searchQuery   ${ineligibleApplicationNumber}
    And the user clicks the button/link         id = searchsubmit
    And the user clicks the button/link         link = ${ineligibleApplicationNumber}
    Then the user navigates to the page         ${ineligibleApplicationOverview}
    And the user should see the element         jQuery = h2:contains("Removed by") ~ p:contains("Ian Cooper, ${today}")
    And the user should see the element         jQuery = h2:contains("Reason for removal") ~ p:contains("This is the reason of why this application is ineligible")

The Administrator should see the ineligible applications in unsuccessful list but he cannot reinstate it
    [Documentation]  IFS-1458 IFS-1459 IFS-50
    [Tags]  Administrator
    [Setup]  log in as a different user        &{ifs_admin_user_credentials}
    Given the user navigates to the page       ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/previous
    And the user expands the section           Applications
    Then the user should see the element       jQuery = td:contains("${ineligibleApplication}")
    And the user should not see the element    jQuery = td:contains("${ineligibleApplication}") ~ td a:contains("Mark as successful")

Inform a user their application is ineligible
    [Documentation]  INFUND-7374  IFS-1491  IFS-3132
    [Tags]  Applicant
    [Setup]  log in as a different user       &{internal_finance_credentials}
    Given the user navigates to the page      ${ineligibleApplications}
    Then the user inform applicant their application is ineligible

Applicant is informed that his application is not eligible
    [Documentation]  INFUND-7374  IFS-3132  IFS-6021
    [Tags]  Applicant
    Given the applicant can see his application in the right section   Previous
    Then the user reads his email  ${Ineligible_user["email"]}         Notification regarding your application  ${ineligibleMessage}

Innovation Lead is not able to reinstate an application
    [Documentation]  INFUND-8941 IFS-986
    [Tags]  InnovationLead
    Given log in as a different user          &{innovation_lead_one}
    When the user navigates to the page       ${ineligibleApplicationOverview}
    Then the user should not see the element  jQuery = a[role = "button"]:contains("Reinstate application")

Reinstate an application
    [Documentation]  INFUND-8941 IFS-986 IFS-1458 IFS-1459
    [Tags]
    Given Log in as a different user          &{Comp_admin1_credentials}
    When the user navigates to the page       ${ineligibleApplicationOverview}
    And the user clicks the button/link       jQuery = a:contains("Reinstate application")
    When the user clicks the button/link      jQuery = button:contains("Reinstate application")
    And the user navigates to the page        ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/submitted
    Then the user should see the element      jQuery = td:contains("${ineligibleApplication}")
    And the reinstated application in no longer shown in the ineligible list
    And the applicant can see his application in the right section  Applications in progress

Stakeholders cannot mark applications as ineligible
    [Documentation]  IFS-2994
    [Tags]
    Given Log in as a different user            &{stakeholder_user}
    When the user navigates to the page         ${SERVER}/management/competition/${IN_ASSESSMENT_COMPETITION}/application/${submittedApplicationNumber}?origin = SUBMITTED_APPLICATIONS
    Then the user should see the element        jQuery = dt:contains("Competition name") ~ dd:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    And the user should not see the element     jQuery = .govuk-details__summary:contains("Mark application as ineligible")

Stakeholders cannot reinstate an application
    [Documentation]  IFS-2994
    [Tags]
    When the user navigates to the page        ${SERVER}/management/competition/${IN_ASSESSMENT_COMPETITION}/application/${ineligibleApplicationNumber}?origin = INELIGIBLE_APPLICATIONS
    Then the user should see the element       jQuery = dt:contains("Competition name") ~ dd:contains("${IN_ASSESSMENT_COMPETITION_NAME}")
    And the user should not see the element    css = a[data-js-modal = "modal-reinstate"]

*** Keywords ***
the applicant can see his application in the right section
    [Arguments]    ${section}
    Log in as a different user         &{Ineligible_user}
    the user should see the element    jQuery = h2:contains(${section}) ~ ul a:contains("Living with Virtual Reality")

the user navigates to ineligible applications
    the user clicks the button/link    link = ${IN_ASSESSMENT_COMPETITION_NAME}
    the user clicks the button/link    link = Applications: All, submitted, ineligible
    the user clicks the button/link    link = Ineligible applications

the reinstated application in no longer shown in the ineligible list
    the user navigates to the page       ${server}/management/competition/${IN_ASSESSMENT_COMPETITION}/applications/ineligible
    the user should not see the element  jQuery = td:contains("${ineligibleApplication}")

the user is required to enter a subject/message
    [Arguments]  ${fieldValidation}  ${field}  ${fieldContent}
    the user clicks the button/link         jQuery = button:contains("Send")
    the user should see a field error       ${fieldValidation}
    the user enters text to a text field    id = ${field}  ${fieldContent}

the user checks for server side validation
    the user clicks the button/link                jQuery = .govuk-details__summary:contains("Mark application as ineligible")
    #There are 2 buttons with the same name so we need to be careful
    the user should see the element                css = [id = "ineligibleReason"]
    browser validations have been disabled
    the user clicks the button/link                css = button[name = "markAsIneligible"]
    the user should see a field and summary error  ${empty_field_warning_message}

the user marks an application as ineligible
    the user clicks the button/link        jQuery = .govuk-details__summary:contains("Mark application as ineligible")
    the user enters text to a text field   id = ineligibleReason  This is the reason of why this application is ineligible
    the user clicks the button/link        jQuery = button:contains("Mark application as ineligible")

the user filters ineligible applications
    the user selects the option from the drop-down menu  No  id = filterInform
    the user clicks the button/link        jQuery = .govuk-button:contains("Filter")
    the user should see the element        jQuery = td:contains("${ineligibleApplication}") ~ td .govuk-button:contains("Inform applicant")
    the user should not see the element    jQuery = td:contains("Informed ineligible application") ~ td span:contains("Informed")
    the user clicks the button/link        jQuery = a:contains("Clear all filters")
    the user should see the element        jQuery = td:contains("Informed ineligible application") ~ td span:contains("Informed")

the user inform applicant their application is ineligible
    the user clicks the button/link           jQuery = td:contains("${ineligibleApplication}") ~ td > a:contains("Inform applicant")
    And the user clicks the button/link       jQuery = a:contains("Cancel")
    When the user clicks the button/link      jQuery = td:contains("${ineligibleApplication}") ~ td > a:contains("Inform applicant")
    And the user should see the element       jQuery = p:contains("${ineligibleMessage}")
    And the user clicks the button/link       jQuery = button:contains("Send")
    Then the user should see the element      jQuery = td:contains("${ineligibleApplication}") ~ td span:contains("Informed")

Custom Suite Setup
    The user logs-in in new browser  &{Comp_admin1_credentials}
    ${today} =  get today
    set suite variable  ${today}