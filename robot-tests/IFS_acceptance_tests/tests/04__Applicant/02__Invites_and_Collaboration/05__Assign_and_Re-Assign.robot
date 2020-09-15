*** Settings ***
Documentation     This suite depends on the Previous one!
...               INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
...
...               INFUND-265: As both lead applicant and collaborator I want to see the changes other participants have made since my last visit, so I can see progress made on the application form
...               INFUND-877: As a collaborator I want to be able to mark application questions that have been assigned to me as complete, so that my lead applicant is aware of my progress
...
...               INFUND-2219 As a collaborator I do not want to be able to submit an application so that only the lead applicant has authority to do so
...
...               INFUND-2417 As a collaborator I want to be able to review the grant Terms and Conditions so that the lead applicant can agree to them on my behalf
...
...               INFUND-3016 As a collaborator I want to mark my finances as complete so the lead can progress with submitting the application.
...
...               INFUND-3288: Assigning questions more than once leads to an internal server error
...
...               INFUND-4806 As an applicant (lead) I want to be able to remove a registered collaborator so that I can manage members no longer required to be part of the consortium
...
...               INFUND-6823 As an Applicant I want to be invited to select the primary Research area for my project
...
...               IFS-3938 As an applicant the requirement prerequesites for Your funding are clear
Suite Setup       Custom suite setup
Suite Teardown    Custom suite teardown
Test Teardown
Force Tags        Applicant  Pending
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Applicant_Commons.robot

# This suite uses application: Assign test

*** Test Cases ***
Lead applicant can assign a question
    [Documentation]  INFUND-275, INFUND-280, IFS-265
    ...  This test depends on the previous test suite to run first
    [Tags]  HappyPath
    [Setup]  the user logs-in in new browser   ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    Given the applicant changes the name of the application
    And the user clicks the button/link        link = Public description
    When the applicant assigns the question to the collaborator  Dennis Bergkamp
    And the user should see the element        jQuery = p:contains("This question is assigned to"):contains("Dennis Bergkamp")

Lead applicant can assign question multiple times
    [Documentation]    INFUND-3288
    ...    This test depends on the previous test suite to run first
    When the user assigns the question to the collaborator      Stuart Anderson
    And the user should see the element                         jQuery = p:contains("This question is assigned to"):contains("you")
    And the applicant assigns the question to the collaborator  Dennis Bergkamp
    Then the user should see the element                        css = .textarea-wrapped .readonly
    And the user should see the element                         jQuery = p:contains("This question is assigned to"):contains("Dennis Bergkamp")

The question is enabled for the assignee
    [Documentation]  INFUND-275
    ...  This test depends on the previous test suite to run first
    [Tags]  HappyPath
    [Setup]  log in as a different user   ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = Assign test  #Application Title
    Then the user should see the browser notification  Stuart ANDERSON has assigned a question to you
    And the user should see the element   jQuery = li:contains("Public description") .task-status-incomplete
    When the user clicks the button/link  jQuery = .govuk-button:contains("Review")
    And the user expands the section      Public description
    Then the user should see the element  jQuery = button:contains("Assign to lead for review")
    And the user clicks the button/link   jQuery = button:contains("Return and edit")
    And the user should see the element   css = .textarea-wrapped .editor

Collaborator should see the terms and conditions from the overview page
    [Documentation]  INFUND-2417
    ...  This test depends on the previous test suite to run first
    Given the user clicks the button/link          link = Back to application overview
    When The user clicks the button/link           link = Award terms and conditions
    Then the user should see the element           jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    # checking new Innovate UK terms and conditions
    ${status}   ${value} =  Run Keyword And Ignore Error Without Screenshots  the user should see the element  jQuery = li:contains("Background")
    Run Keyword If  '${status}' == 'PASS'    Run Keywords    the user should see the element   jQuery = li:contains("Entire agreement") .ifs-list--number
    ...                                               AND    the user should see the element   jQuery = .disabled:contains("Agree and continue")

Collaborator should see the review button instead of the review and submit
    [Documentation]  INFUND-2451
    ...  This test depends on the previous test suite to run first
    [Tags]  HappyPath
    Given the user navigates to the page          ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link           link = Assign test
    Then the user should not see the element      jQuery = .govuk-button:contains("Review and submit")
    And the user clicks the button/link           jQuery = .govuk-button:contains("Review")
    And the user should see the element           jQuery = .message-alert:contains("All sections must be marked as complete before the application can be submitted. Only the lead applicant is able to submit the application.")
    And the user should not see the element       jQuery = .govuk-button:contains("Submit application")

Last update message is correctly updating
    [Documentation]  INFUND-280
    ...  This test depends on the previous test suite to run first
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = Assign test
    And the user clicks the button/link   link = Public description
    When the collaborator edits the 'public description' question
    Then the user should see the element  jQuery = .form-footer .form-footer__info:contains("today"):contains("by you")

Collaborators cannot assign a question
    [Documentation]  INFUND-839
    ...  This test depends on the previous test suite to run first
    [Tags]  HappyPath
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = Assign test
    And the user clicks the button/link   link = Public description
    Then The user should see the element  jQuery = .button-clear:contains("Assign to lead for review")

Collaborators can mark as ready for review
    [Documentation]  INFUND-877
    ...  This test depends on the previous test suite to run first
    When the user clicks the button/link            jQuery = button:contains("Assign to lead for review")
    Then the user should see the notification       You have successfully assigned the question
    And the user should see the element             jQuery = p:contains("This question is assigned to"):contains("Stuart Anderson")

Collaborator cannot edit after marking ready for review
    [Documentation]  INFUND-275
    ...    This test depends on the previous test suite to run first
    Then the user should see the element  css = .textarea-wrapped .readonly

Collaborators should not be able to edit application details
    [Documentation]  INFUND-2298
    ...  This test depends on the previous test suite to run first
    [Tags]  HappyPath
    Given the user navigates to the page      ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link       link = Assign test
    And the user clicks the button/link       link = Application details
    Then the user should not see the element  css = [id="name"]
    And the user should not see the element   id = startDate
    And the user should not see the element   jQuery = button:contains("Mark as complete")

The question should be reassigned to the lead applicant
    [Documentation]  INFUND-275
    ...  This test depends on the previous test suite to run first
    [Setup]  log in as a different user      ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    Given the user navigates to the page     ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link      link = Assign test
    Then the user should see the browser notification  Dennis Bergkamp has assigned a question to you
    And the user should see the element      jQuery = li:contains("Public description"):contains("Assigned to"):contains("you")
    And the user clicks the button/link      link = Public description
    And the user should see the element      css = .textarea-wrapped .editor
    And the user should see the element      jQuery = .form-footer .form-footer__info:contains("today"):contains("by Dennis Bergkamp")
    And the user should not see the element  css = .textarea-wrapped .readonly

Appendices are assigned along with the question
    [Documentation]  INFUND-409
    ...  This test depends on the previous test suite to run first
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = Assign test
    And the user clicks the button/link   link = 6. Innovation
    And the user should see the element   jQuery = label:contains("Upload")
    When the applicant assigns the question to the collaborator  Dennis Bergkamp
    Then log in as a different user       ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to the page  ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link   link = Assign test
    And the user clicks the button/link   link = 6. Innovation
    And the user should see the element   jQuery = label:contains("Upload")
    And the user clicks the button/link   jQuery = button:contains("Assign to lead for review")
    And the user should not see the element    jQuery = label:contains("Upload")

RTO Collaborator is not guided that the research area is not selected
    [Documentation]  IFS-4099
    Given the user navigates to Your-finances page  Assign test
    When the user clicks the button/link            link = Your funding
    Then The user should not see the element        jQuery = .govuk-list li:contains("the lead applicant must mark the research category page as complete")
    And the user selects the radio button           requestingFunding   true
    And the user should see the element             css = [name^="grantClaimPercentage"]

Lead selects Research category
    [Documentation]  INFUND-6823  IFS-3938
    [Setup]  log in as a different user        ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    # this test is tagged as Email since it relies on an earlier invitation being accepted via email
    Given the user navigates to Your-finances page  Assign test
    And the user clicks the button/link        link = Your funding
    Then the user should see the element       jQuery = li:contains("mark the") a:contains("research category")
    When the user navigates to the page        ${APPLICANT_DASHBOARD_URL}
    Then the user clicks the button/link       link = Assign test
    When the user selects Research category    Feasibility studies

Lead marks finances as complete
    [Documentation]  INFUND-3016
    ...  This test depends on the previous test suite to run first
    Given the user navigates to the page                 ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link                  jQuery = .progress-list a:contains("Assign test")
    Then the applicant completes the application details  Assign test  ${tomorrowday}  ${month}  ${nextyear}
    When the user navigates to Your-finances page         Assign test
    Then the user should see the element                 link = Your project costs
    And the user should see the element                  link = Your organisation
    And the user should see the element                  jQuery = h3:contains("Your funding")
    When the user fills in the project costs             labour costs  n/a
    And the user enters the project location
    And the user navigates to Your-finances page         Assign test
    Then the user fills in the organisation information  Assign test   ${SMALL_ORGANISATION_SIZE}
    And the user fills in the funding information        Assign test   no
    When the user navigates to Your-finances page        Assign test
    Then the user should see all finance subsections complete

Collaborator from another organisation should be able to mark Finances as complete
    [Documentation]  INFUND-3016
    ...  This test depends on the previous test suite to run first
    [Setup]  log in as a different user               ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to Your-finances page    Assign test
    Then the user should see all finance subsections incomplete
    And the collaborator is able to edit the finances

The question is disabled for other collaborators
    [Documentation]  INFUND-275
    ...    This test case is still using the old application
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    Given Steve smith assigns a question to the collaborator
    Given log in as a different user       &{collaborator2_credentials}
    And the user navigates to the page     ${APPLICATION_OVERVIEW_URL}
    When the user clicks the button/link   jQuery = a:contains("Public description")
    Then The user should see the element   css = .textarea-wrapped .readonly

The question is disabled on the summary page for other collaborators
    [Documentation]  INFUND-2302
    ...    This test case is still using the old application
    Given the user navigates to the page     ${SUMMARY_URL}
    When the user expands the section        Public description
    Then the user should see the element     jQuery = label:contains("Public description") ~ p.wysiwyg-styles
    And the user should not see the element  jQuery = button:contains("Assign to lead for review")

Lead applicant should be able to remove the partner organisation
    [Documentation]  INFUND-8590
    [Tags]  HappyPath
    [Setup]  log in as a different user    ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    Given the user clicks the button/link  link = Assign test
    And the user clicks the button/link    link = Application team
    And the user clicks the button/link                jQuery = td:contains("Dennis") ~ td a:contains("Remove organisation")
    Then The user clicks the button/link               jQuery = tr:contains("Dennis") .warning-modal button:contains("Remove organisation")
    Then the user should see the element   jQuery = h1:contains("Application team")
    And the user should not see the element  jQuery = td:contains("Dennis Bergkamp")
    #The following steps check if the collaborator should not see the application in the dashboard page
    And log in as a different user  ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    And the user should not see the element  link = Assign test

*** Keywords ***
the collaborator edits the 'public description' question
    Clear Element Text  css = .textarea-wrapped .editor
    The user enters text to a text field  css = .textarea-wrapped .editor  collaborator's text
    Set Focus To Element    link = Sign out
    wait for autosave
    the user reloads the page

the collaborator is able to edit the finances
    the user fills in the project costs             labour costs  n/a
    the user navigates to Your-finances page        Assign test
    the user fills in the organisation information  Assign test   ${SMALL_ORGANISATION_SIZE}
    the user fills in the funding information       Assign test   no

the applicant changes the name of the application
    Given the user clicks the button/link     link = ${UNTITLED_APPLICATION_DASHBOARD_LINK}
    And the user clicks the button/link       link = Application details
    And the user enters text to a text field  css = [id="name"]  Assign test
    And The user clicks the button/link       jQuery = button:contains("Save and return")

Steve smith assigns a question to the collaborator
    the user navigates to the page    ${APPLICATION_OVERVIEW_URL}
    the user clicks the button/link   jQuery = a:contains("Public description")
    When the applicant assigns the question to the collaborator  Jessica Doe

Custom suite setup
      Set predefined date variables
      Connect to database  @{database}

Custom suite teardown
    The user closes the browser
    Disconnect from database