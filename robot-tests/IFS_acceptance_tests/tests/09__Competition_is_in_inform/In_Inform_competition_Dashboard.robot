*** Settings ***
Documentation     INFUND-7365 Inflight competition dashboards: Inform dashboard
...
...               INFUND-7561 Inflight competition dashboards- View milestones
...
...               INFUND-8050 Release feedback and send notification email
...
...               INFUND-7861 Feedback overview dashboard of application after feedback is released
...
...               INFUND-8168 Question list and finance summary on Feedback overview dashboard
...
...               INFUND-8169 Assessors scores viewed on Feedback Overview by applicant
...
...               INFUND-8172 Assessor's Overall feedback displayed on Feedback Overview
...
...               INFUND-7950 Updates to 'Inform' - Competition dashboard
...
...               INFUND-8005 Feedback per question for applicant
...
...               INFUND-8876 No back navigation on applicant feedback view
...
...               INFUND-8066 Filter on 'Manage funding notifications' dashboard
...
...               IFS-1458 View unsuccessful applications after Inform state: initial navigation
...
...               IFS-1459 View unsuccessful applications after Inform state: list
...
...               IFS-1517 Internal user: competitions listing in Previous tab
...
...               IFS-2437 Viewing application details when feedback has been released
...
...               IFS-2256 Missing print button and sections of the application cannot be viewed when in 'feedback' status.
...
...               IFS-2640 Innovation Leads can access ‘Previous’ tab
Suite Setup       The user logs-in in new browser  &{Comp_admin1_credentials}
Suite Teardown    Close browser and delete emails
Force Tags        CompAdmin
Resource          ../../resources/defaultResources.robot

*** Variables ***
${proj_electric_drive}  ${application_ids['Electric Drive']}
${proj_app_with_ineligible}  ${application_ids['Application with ineligible']}

*** Test Cases ***
Competition Dashboard
    [Documentation]    INFUND-7365
    [Tags]    HappyPath
    When The user clicks the button/link            link=${INFORM_COMPETITION_NAME}
    Then The user should see the element            jQuery=span:contains("${INFORM_COMPETITION_NAME}")
    And The user should see the element             jQuery=h1:contains("Inform")
    And The user should see the element             jQuery=dd:contains("Programme")
    And The user should see the element             jQuery=dd:contains("Materials and manufacturing")
    And The user should see the element             jQuery=dd:contains("Digital manufacturing")
    And The user should see the element             jQuery=a:contains("Invite assessors to assess the competition")
    And the user should not see the element         link=View and update competition setup

Milestones for the In inform competition
    [Documentation]    INFUND-7561 INFUND-7950
    [Tags]
    Then the user should see the element    jQuery=.govuk-button:contains("Manage funding notifications")
    And the user should see the element     jQuery=button:contains("Release feedback")
    And the user should see the element     css=li:nth-child(13).done    #Verify that 12. Notifications
    And the user should see the element     css=li:nth-child(14).not-done    #Verify that 13. Release feedback is not done

Filtering on the Manage funding applications page
    [Documentation]    INFUND-8066
    [Tags]
    Given The user clicks the button/link                      jQuery=.govuk-button:contains("Manage funding notifications")
    And the user enters text to a text field                   id=stringFilter    ${application_ids['Climate control solution']}
    And the user selects the option from the drop-down menu    Yes    id=sendFilter
    And the user selects the option from the drop-down menu    Successful    id=fundingFilter
    When the user clicks the button/link                       jQuery=button:contains("Filter")
    Then the user should see the element                       jQuery=td:nth-child(2):contains("${application_ids['Climate control solution']}")
    And the user should not see the element                    jQuery=td:nth-child(2):contains("${application_ids['Electric Drive']}")
    And the user clicks the button/link                        jQuery=a:contains("Clear all filters")
    And the user should see the element                        jQuery=td:nth-child(2):contains("${application_ids['Electric Drive']}")
    [Teardown]    The user clicks the button/link              link=Competition

Checking release feedback button state is correct
    [Documentation]    INFUND-7950
    [Tags]
    Given the user clicks the button/link                                   link=Input and review funding decision
    And the user selects the checkbox                                       app-row-3
    And the user clicks the button/link                                     jQuery=button:contains("On hold")
    When the user clicks the button/link                                    jQuery=.govuk-back-link:contains("Competition")
    Then the user should see that the element is disabled                   jQuery=button:contains("Release feedback")
    [Teardown]    User sends the notification to enable release feedback

Release feedback
    [Documentation]    INFUND-8050
    [Tags]    Email    HappyPath
    When The user clicks the button/link                 jQuery=button:contains("Release feedback")
    Then The user should not see the element             jQuery=h1:contains("Inform")
    When The user clicks the button/link                 jQuery=a:contains("Live")
    Then The user should not see the element             link=${INFORM_COMPETITION_NAME}
    And the user reads his email                         ${test_mailbox_two}+releasefeedback@gmail.com    ${INFORM_COMPETITION_NAME}: Feedback for application ${application_ids['High Performance Gasoline Stratified']} is now available.    The feedback provided by the independent assessors has been reviewed by Innovate UK

Unsuccessful applicant sees unsuccessful alert
    [Documentation]    INFUND-7861
    [Tags]    Email
    [Setup]    log in as a different user    &{unsuccessful_released_credentials}
    Given the user should see the element    jQuery=.status:contains("Unsuccessful")
    When the user clicks the button/link     jQuery=a:contains("Electric Drive")
    And the user should see the element      jQuery=.warning-alert:contains("Your application has not been successful in this competition")

Internal user can see ineligible and unsuccessful applications in the Previous tab
    [Documentation]  IFS-1458  IFS-1459  IFS-1517  IFS-2640
    [Tags]  HappyPath
    When the user checks the ineligible and unsuccessful applications in the Previous tab    ${Comp_admin1_credentials["email"]}  ${short_password}
    Then the user checks the ineligible and unsuccessful applications in the Previous tab    ${innovation_lead_one["email"]}  ${short_password}
    #TODO IFS-2744 Is there a better way to pass emails as variables? If not, we can continue to use what's in this test case.

Successful applicant see successful alert
    [Documentation]    INFUND-7861
    [Tags]    Email    HappyPath
    [Setup]    log in as a different user    &{successful_released_credentials}
    Given the user should see the element    jQuery=.status:contains("Successful")
    When the user clicks the button/link     jQuery=.previous-applications a:contains("High Performance Gasoline Stratified")
    Then the user should see the element     jQuery=.success-alert:contains("Congratulations, your application has been successful")

View feedback from each assessor
    [Documentation]    INFUND-8172
    [Tags]    Email    HappyPath
    Then the user should see the element    jQuery=h3:contains("Assessor 1") ~ .wysiwyg-styles p:contains("I have no problem recommending this application")
    And the user should see the element     jQuery=h3:contains("Assessor 2") ~ .wysiwyg-styles p:contains("Very good, but could have been better in areas")
    And the user should see the element     jQuery=h3:contains("Assessor 3") ~ .wysiwyg-styles p:contains("I enjoyed reading this application, well done")

Question scores and application details are correct
    [Documentation]    INFUND-8169 INFUND-7861
    [Tags]    Email    HappyPath
    Then the application question scores are correct
    And the application details are correct

User can see the Application details along with feedback
    [Documentation]    INF-2473  IFS-2256
    [Tags]
    Given the user should see the element                           jQuery=h2:contains("Application details")
    Then the user should see the element                            jQuery=h3:contains("Project title") ~ p:contains("High Performance Gasoline Stratified")
    And the user checks the Project summary functionality
    Given the user checks the Public description functionality
    When the user checks the Scope functionality
    Then the user should see the element                            jQuery=h2:contains("Application details")

User can see feedback to individual questions
    [Documentation]    INFUND-8005
    [Tags]
    Given the user clicks the button/link            jQuery=a:contains("6. Innovation")
    Then the user should see the element             jQuery=h3:contains("Your answer") ~ div[data-md-to-html] p:contains("This is the applicant response for what is innovative about your project?.")
    And the user should see the element              jQuery=h4:contains("Assessor 1") ~ div[data-md-to-html] p:contains("This is the innovation feedback")
    [Teardown]    the user clicks the button/link    jQuery=.govuk-back-link:contains("Feedback overview")

The finance details are shown
    [Documentation]    INFUND-8168
    [Tags]    Email
    When the user clicks the button/link       css=.collapsible button
    Then the user should see the element       css=.collapsible div[aria-hidden="false"]
    And the user should not see the element    css=.collapsible div[aria-hidden="true"]

Selecting the dashboard link takes user back to the dashboard
    [Documentation]    INFUND-8876
    [Tags]
    Given the user clicks the button/link    jQuery=.govuk-back-link:contains("Dashboard")
    Then the user should see the element     jQuery=h1:contains("Dashboard")

*** Keywords ***
the application question scores are correct
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Business opportunity") + div .govuk-grid-column-one-third:contains("Average score 6 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Potential market") + div .govuk-grid-column-one-third:contains("Average score 5 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Project exploitation") + div .govuk-grid-column-one-third:contains("Average score 6 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Economic benefit") + div .govuk-grid-column-one-third:contains("Average score 4 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Technical approach") + div .govuk-grid-column-one-third:contains("Average score 4 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Innovation") + div .govuk-grid-column-one-third:contains("Average score 5 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Risks") + div .govuk-grid-column-one-third:contains("Average score 7 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Project team") + div .govuk-grid-column-one-third:contains("Average score 7 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Funding") + div .govuk-grid-column-one-third:contains("Average score 3 / 10")
    the user should see the element    jQuery=.govuk-grid-column-two-thirds:contains("Adding value") + div .govuk-grid-column-one-third:contains("Average score 7 / 10")
    the user should see the element    jQuery=p:contains("Average overall: 53%")

the application details are correct
    the user should see the element    jQuery=p:contains("High Performance Gasoline Stratified")
    the user should see the element    jQuery=p:contains("Electric Sounds Ltd")
    the user should see the element    jQuery=p:contains("Project start date: ")
    the user should see the element    jQuery=p:contains("Duration")
    the user should see the element    jQuery=h3:contains("Total project cost")

User sends the notification to enable release feedback
    the user clicks the button/link                                          link=Input and review funding decision
    the user selects the checkbox                                            app-row-3
    the user clicks the button/link                                          jQuery=button:contains("Unsuccessful")
    the user clicks the button/link                                          jQuery=.govuk-back-link:contains("Competition")
    the user clicks the button/link                                          jQuery=a:contains("Manage funding notifications")
    the user selects the checkbox                                            app-row-${application_ids['Electric Drive']}
    the user clicks the button/link                                          jQuery=button:contains("Write and send email")
    the internal sends the descision notification email to all applicants    EmailTextBody
    the user clicks the button/link                                          jQuery=.govuk-back-link:contains("Competition")

The user checks the Project summary functionality
    the user clicks the button/link    jQuery=a:contains("Project summary")
    the user should see the element    jQuery=h1:contains("Project summary")
    the user should see the element    jQuery=p:contains("This is the applicant response for project summary.")
    the user clicks the button/link    jQuery=a:contains("Feedback overview")

The user checks the Public description functionality
    the user clicks the button/link    jQuery=a:contains("Public description")
    the user should see the element    jQuery=h1:contains("Public description")
    the user should see the element    jQuery=p:contains("This is the applicant response for public description.")
    the user clicks the button/link    jQuery=a:contains("Feedback overview")

The user checks the Scope functionality
    the user clicks the button/link    jQuery=a:contains("Scope")
    the user should see the element    jQuery=h1:contains("Scope")
    the user should see the element    jQuery=p:contains("This is the applicant response for how does your project align with the scope of this competition?")
    the user should see the element    jQuery=h4:contains("Assessor 1")
    the user should see the element    jQuery=p:contains("This is the scope feedback")
    the user clicks the button/link    jQuery=a:contains("Feedback overview")

The user checks the ineligible and unsuccessful applications in the Previous tab
    [Arguments]  ${email}  ${password}
    log in as a different user         ${email}  ${password}
    the user clicks the button/link    jQuery = a:contains("Previous")
    the user clicks the button/link    link = ${NOT_EDITABLE_COMPETITION_NAME}
    the user should see the element    jQuery = td:contains("${proj_electric_drive}") ~ td:contains("Unsuccessful")
    the user should see the element    jQuery = td:contains("${INFORM_COMPETITION_NAME_1}") ~ td:contains("Successful")