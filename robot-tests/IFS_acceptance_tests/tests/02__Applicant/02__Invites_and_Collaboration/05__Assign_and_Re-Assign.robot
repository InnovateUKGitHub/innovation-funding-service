*** Settings ***
Documentation     INFUND-262: As a (lead) applicant, I want to see which fields in the form are being edited, so I can track progress
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
Suite Teardown    TestTeardown User closes the browser
Test Teardown
Force Tags        Applicant
Resource          ../../../resources/defaultResources.robot
Resource          ../Applicant_Commons.robot

*** Variables ***

*** Test Cases ***
Lead applicant can assign a question
    [Documentation]    INFUND-275, INFUND-280
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath
    [Setup]    Guest user log-in    ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    #This test depends on the previous test suite to run first
    Given the applicant changes the name of the application
    And the user clicks the button/link    link= Public description
    When the applicant assigns the question to the collaborator    css=#form-input-12 .editor    test1233    Dennis Bergkamp
    Then the user should see the notification    Question assigned successfully
    And the user should see the element    css=#form-input-12 .readonly
    And the question should contain the correct status/name    css=#form-input-12 .assignee span+span    Dennis Bergkamp

Lead applicant can assign question multiple times
    [Documentation]    INFUND-3288
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email
    When the user assigns the question to the collaborator    Stuart ANDERSON
    And the question should contain the correct status/name    css=#form-input-12 .assignee span+span    you
    And the applicant assigns the question to the collaborator    css=#form-input-12 .editor    test1233    Dennis Bergkamp
    Then the user should see the element    css=#form-input-12 .readonly
    And the question should contain the correct status/name    css=#form-input-12 .assignee span+span    Dennis Bergkamp

The question is enabled for the assignee
    [Documentation]    INFUND-275
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    HappyPath    Email
    [Setup]  log in as a different user    ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test  #Application Title
    Then the user should see the browser notification    Stuart ANDERSON has assigned a question to you
    And the question should contain the correct status/name    jQuery=#section-1 .section:nth-child(3) .assign-container    You
    And the user clicks the button/link    link= Public description
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly

Collaborator should see the terms and conditions from the overview page
    [Documentation]    INFUND-2417
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email
    Given the user clicks the button/link    link=Application Overview
    When The user clicks the button/link    link= view conditions of grant offer
    Then the user should see the text in the page    Terms and Conditions of an Innovate UK Grant Award
    And the user should see the text in the page    Entire Agreement

Collaborator should see the review button instead of the review and submit
    [Documentation]    INFUND-2451
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath  Pending
    # TODO Pending due to INFUND-7608
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Assign test
    Then the user should not see the element    jQuery=.button:contains("Review and submit")
    And the user clicks the button/link    jQuery=.button:contains("Review")
    And the user should see the text in the page    All sections must be marked as complete before the application can be submitted. Only the lead applicant is able to submit the application
    And the user should not see the element    jQuery=.button:contains("Submit application")
    [Teardown]

Collaborator should be able to edit the assigned question
    [Documentation]    INFUND-2302
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath  Pending
    # TODO Pending due to INFUND-7608
    When the user clicks the button/link    jQuery=button:contains("Public description")
    And the user should see the element    jQuery=button:contains("Assign to lead for review")

Last update message is correctly updating
    [Documentation]    INFUND-280
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link= Public description
    When the collaborator edits the 'public description' question
    Then the question should contain the correct status/name    css=#form-input-12 .textarea-footer    Last updated: Today by you

Collaborators cannot assign a question
    [Documentation]    INFUND-839
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link= Public description
    Then The user should see the text in the page  Assign to lead for review

Collaborators can mark as ready for review
    [Documentation]    INFUND-877
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    HappyPath    Email
    When the user clicks the button/link    jQuery=button:contains("Assign to lead for review")
    Then the user should see the notification    Question assigned successfully
    And the user should see the text in the page    You have reassigned this question to

Collaborator cannot edit after marking ready for review
    [Documentation]    INFUND-275
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath
    Then the user should see the element    css=#form-input-12 .readonly
    [Teardown]

Collaborators should not be able to edit application details
    [Documentation]    INFUND-2298
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link=Application details
    Then the user should see the element    css=#application_details-title[readonly]
    And the user should see the element    css=#application_details-startdate_day[readonly]
    And the user should not see the element    jQuery=button:contains("Mark as complete")

The question should be reassigned to the lead applicant
    [Documentation]    INFUND-275
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email    HappyPath
    [Setup]  log in as a different user     ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    Then the user should see the browser notification    Dennis Bergkamp has assigned a question to you
    And the question should contain the correct status/name    jQuery=#section-1 .section:nth-child(3) .assign-container    You
    And the user clicks the button/link    link= Public description
    And the user should see the element    css=#form-input-12 .editor
    And the user should not see the element    css=#form-input-12 .readonly

Appendices are assigned along with the question
    [Documentation]    INFUND-409
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link=6. Innovation
    And the user should see the text in the page    Upload
    When the applicant assigns the question to the collaborator    css=#form-input-6 .editor    test1233    Dennis Bergkamp
    Then log in as a different user          ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link=6. Innovation
    And the user should see the text in the page    Upload
    And the user clicks the button/link    jQuery=button:contains("Assign to lead for review")
    And the user should not see the text in the page    Upload

Lead marks finances as complete
    [Documentation]    INFUND-3016
    ...
    ...    This test depends on the previous test suite to run first
    [Tags]    Email
    [Setup]  log in as a different user    ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    # this test is tagged as Email since it relies on an earlier invitation being accepted via email
    Given the user navigates to the page  ${DASHBOARD_URL}
    And the user clicks the button/link  link=Assign test
    And the applicant completes the application details
    Given the user navigates to his finances page
    Then the user should see the element   link=Your project costs
    And the user should see the element    link=Your organisation
    And the user should see the element    jQuery=h3:contains("Your funding")
    When the user clicks the button/link   link=Your project costs
    Then the user fills in the project costs
    When the user navigates to his finances page
    Then the user fills in the organisation information
    And the user fills in the funding information
    When the user navigates to his finances page
    Then the user should see all sections complete

Collaborator from another organisation should be able to mark Finances as complete
    [Documentation]  INFUND-3016
    ...              This test depends on the previous test suite to run first
    [Tags]
    [Setup]  log in as a different user     ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    Given the user navigates to his finances page
    Then the user should see all sections incomplete
    And the collaborator is able to edit the finances

The question is disabled for other collaborators
    [Documentation]    INFUND-275
    ...
    ...    This test case is still using the old application
    [Tags]
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    Given Steve smith assigns a question to the collaborator
    Given log in as a different user       &{collaborator2_credentials}
    When the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    Then The user should see the element    css=#form-input-12 .readonly

The question is disabled on the summary page for other collaborators
    [Documentation]    INFUND-2302
    ...
    ...    This test case is still using the old application
    [Tags]
    Given the user navigates to the page    ${SUMMARY_URL}
    When the user clicks the button/link    jQuery=button:contains("Public description")
    Then the user should see the element    css=#form-input-12 .readonly
    And the user should not see the element    jQuery=button:contains("Assign to lead for review")

Lead applicant should be able to remove the registered partner
    [Documentation]    INFUND-4806
    [Tags]
    [Setup]    log in as a different user    ${test_mailbox_one}+invite2@gmail.com  ${correct_password}
    Given the user clicks the button/link    link= Assign test
    And the user clicks the button/link    link=view team members and add collaborators
    When the user clicks the button/link    jQuery=div:nth-child(6) a:contains("Remove")
    And the user clicks the button/link    jQuery=button:contains("Remove")
    Then the user should not see the element    link=Dennis Bergkamp
    #The following steps check if the collaborator should not see the application in the dashboard page
    And log in as a different user  ${test_mailbox_one}+invitedregistered@gmail.com  ${correct_password}
    And the user should not see the element    link= Assign test

*** Keywords ***
the user navigates to his finances page
    the user navigates to the page  ${DASHBOARD_URL}
    the user clicks the button/link  link=Assign test
    the user clicks the button/link  link=Your finances

the collaborator edits the 'public description' question
    Clear Element Text    css=#form-input-12 .editor
    Focus    css=#form-input-12 .editor
    The user enters text to a text field    css=#form-input-12 .editor    collaborator's text
    Focus    css=.app-submit-btn
    wait for autosave
    the user reloads the page

the question should contain the correct status/name
    [Arguments]    ${ELEMENT}    ${STATUS}
    Element Should Contain    ${ELEMENT}    ${STATUS}

the collaborator is able to edit the finances
    the user clicks the button/link   link=Your project costs
    the user fills in the project costs
    the user navigates to his finances page
    the user fills in the organisation information
    the user fills in the funding information

the applicant changes the name of the application
    Given the user clicks the button/link    link= ${OPEN_COMPETITION_NAME}
    And the user clicks the button/link    link= Application details
    And the user enters text to a text field    id=application_details-title    Assign test
    And The user clicks the button/link    jQuery=button:contains("Save and return")

Steve smith assigns a question to the collaborator
    the user navigates to the page    ${PUBLIC_DESCRIPTION_URL}
    When the applicant assigns the question to the collaborator    css=#form-input-12 .editor    test1233    Jessica Doe

the user fills in the project costs
    the user fills in Labour
    the user fills in Overhead costs
    the user fills in Material
    the user fills in Capital usage
    the user fills in Subcontracting costs
    the user fills in Travel and subsistence
    the user fills in Other Costs
    the user selects the checkbox    agree-state-aid-page
    the user clicks the button/link  jQuery=button:contains("Mark as complete")

the user fills in Labour
    the user clicks the button/link            jQuery=#form-input-20 button:contains("Labour")
    the user should see the element            css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input
    the user clears the text from the element  css=[name^="labour-labourDaysYearly"]
    the user enters text to a text field       css=[name^="labour-labourDaysYearly"]    230
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(2) input    120000
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(4) input    100
    the user moves focus to the element        jQuery=button:contains('Add another role')
    the user clicks the button/link            jQuery=button:contains('Add another role')
    the user should see the element            css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(2) input    120000
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(4) input    100
    the user enters text to a text field       css=.labour-costs-table tr:nth-of-type(3) td:nth-of-type(1) input    test
    the user clicks the button/link            jQuery=#form-input-20 button:contains("Labour")

the user fills in Overhead costs
    the user clicks the button/link    jQuery=#form-input-20 button:contains("Overhead costs")
    the user clicks the button/link    css=label[data-target="overhead-default-percentage"]
    the user clicks the button/link    jQuery=#form-input-20 button:contains("Overhead costs")

the user fills in Material
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Materials")
    the user should see the element       css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    the user enters text to a text field  css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Materials")

the user fills in Capital usage
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Capital usage")
    the user enters text to a text field  jQuery=textarea.form-control[name^=capital_usage-description]  some description
    Click Element                         jQuery=label:contains("New")
    the user enters text to a text field  css=.form-finances-capital-usage-depreciation  10
    the user enters text to a text field  css=.form-finances-capital-usage-npv  5000
    the user enters text to a text field  css=.form-finances-capital-usage-residual-value  25
    the user enters text to a text field  css=.form-finances-capital-usage-utilisation   100
    focus                                 jQuery=#section-total-12[readonly]
    the user should see the element       jQuery=#section-total-12[readonly]
    textfield should contain              css=#capital_usage .form-row:nth-of-type(1) [readonly]  £ 4,975
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Capital usage")

the user fills in Subcontracting costs
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Subcontracting costs")
    the user enters text to a text field  css=.form-finances-subcontracting-company  SomeName
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-country]  Netherlands
    the user enters text to a text field  jQuery=textarea.form-control[name^=subcontracting-role]  Quality Assurance
    the user enters text to a text field  jQuery=input.form-control[name^=subcontracting-subcontractingCost]  1000
    focus                                 css=#section-total-13[readonly]
    textfield should contain              css=#section-total-13[readonly]  £ 1,000
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Subcontracting costs")

the user fills in Travel and subsistence
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Travel and subsistence")
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(1) input    test
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    10
    the user enters text to a text field  css=#travel-costs-table tbody tr:nth-of-type(1) td:nth-of-type(3) input    100
    focus                                 css=#section-total-14[readonly]
    textfield should contain              css=#section-total-14[readonly]  £ 1,000
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Travel and subsistence")

the user fills in Other Costs
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Other Costs")
    the user enters text to a text field  jQuery=textarea.form-control[name^=other_costs-description]  some other costs
    the user enters text to a text field  jQuery=input.form-control[name^=other_costs-otherCost]  50
    focus                                 css=#section-total-15
    textfield should contain              css=#section-total-15  £ 50
    the user clicks the button/link       jQuery=#form-input-20 button:contains("Other Costs")

the user fills in the organisation information
    the user clicks the button/link    link=Your organisation
    the user selects the radio button  financePosition-organisationSize  financePosition-organisationSize-SMALL
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the user fills in the funding information
    the user navigates to his finances page
    the user clicks the button/link       link=Your funding
    the user enters text to a text field  css=#cost-financegrantclaim  60
    click element                         jQuery=label:contains("No")
    the user selects the checkbox         agree-terms-page
    the user clicks the button/link       jQuery=button:contains("Mark as complete")

the user should see all sections complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(1) img.section-status.complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(2) img.section-status.complete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(3) img.section-status.complete

the user should see all sections incomplete
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(1) img.section-status.assigned
    the user should see the element  jQuery=li.grid-row.section:nth-of-type(2) img.section-status.assigned
    the user should see the element  jQuery=h3:contains("Your funding")