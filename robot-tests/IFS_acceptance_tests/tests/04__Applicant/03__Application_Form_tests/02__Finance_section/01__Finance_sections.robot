*** Settings ***
Documentation     INFUND-45: As an applicant and I am on the application form on an open application, I expect the form to help me fill in financial details, so I can have a clear overview and less chance of making mistakes.
...
...               INFUND-1815: Small text changes to registration journey following user testing
...
...
...               INFUND-2965: Investigation into why financials return to zero when back spacing
...
...               INFUND-2051: Remove the '0' in finance fields
...
...               INFUND-2961: ‘Working days per year’ in Labour Costs do not default to 232.
...
...               INFUND-7522:  Create 'Your finances' view excluding 'Your organisation' page where 'Organisation type' is 'Research' and sub category is 'Academic'
...
Suite Setup       Custom Suite Setup
Suite Teardown    TestTeardown User closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Variables ***
${applicationName}  Planetary science Pluto's telltale heart

*** Test Cases ***
Finance sub-sections
    [Documentation]    INFUND-192
    [Tags]    HappyPath
    Then the user should see all the Your-Finances Sections

Not requesting funding guidance
    [Documentation]    INFUND-7093
    [Tags]
    Given the user should not see the funding guidance
    When the user clicks the button/link                jQuery=summary span:contains("Not requesting funding")
    Then the user should see the funding guidance
    When the user clicks the button/link                jQuery=summary span:contains("Not requesting funding")
    Then the user should not see the funding guidance

Not requesting funding button
    [Documentation]    INFUND-7093
    [Tags]
    When the user clicks the button/link                jQuery=summary span:contains("Not requesting funding")
    And the user clicks the button/link                 jQuery=button:contains("Not requesting funding")
    Then the user should see the funding guidance
    And the user should see the element                 jQuery=button:contains("Requesting funding")
    And the user should see the element                 jQuery=li:nth-of-type(2) span:contains("No action required")
    And the user should see the element                 jQuery=li:nth-of-type(3) span:contains("No action required")

Requesting funding button
    [Documentation]    INFUND-7093
    [Tags]
    When the user clicks the button/link                jQuery=button:contains("Requesting funding")
    Then the user should see the element                jQuery=li:nth-of-type(2) > .action-required
    And the user should not see the element             jQuery=li:nth-of-type(3) span:contains("No action required")
    And the user should not see the element             jQuery=li:nth-of-type(3) > .task-status-complete
    And the user should not see the funding guidance

Organisation name visible in the Finance section
    [Documentation]    INFUND-1815
    [Tags]
    When the user clicks the button/link             link=Your project costs
    Then the user should see the text in the page    Provide the project costs for 'Empire Ltd'
    And the user should see the text in the page     'Empire Ltd' Total project costs

Guidance in the your project costs
    [Documentation]    INFUND-192
    [Tags]
    [Setup]  Applicant navigates to the finances of the Robot application
    Given the user clicks the button/link   link=Your project costs
    When the user clicks the button/link    jQuery=#form-input-1085 button:contains("Labour")
    And the user clicks the button/link     css=#collapsible-0 summary
    Then the user should see the element    css=#details-content-0 p
    And the user should see the element     jQuery=.labour-costs-table tr:nth-of-type(1) td:nth-of-type(1) input[value=""]

Working days per year should be 232
    [Documentation]    INFUND-2961
    Then the working days per year should be 232 by default

User pressing back button should get the correct version of the page
    [Documentation]    INFUND-2695
    [Tags]
    [Setup]  Applicant navigates to the finances of the Robot application
    And the user clicks the button/link  link=Your project costs
    Given The user adds three material rows
    When the user navigates to another page
    And the user should be redirected to the correct page without the usual headers    ${project_guidance}
    And the user goes back to the previous page
    Then the user should see the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    [Teardown]    the user removes the materials rows

Non-academic partner finance section
    [Documentation]    INFUND-7522
    [Tags]    HappyPath
    [Setup]  Log in as a different user     &{collaborator1_credentials}
    Given the user navigates to Your-finances page  ${applicationName}
    Then The user should see the element      JQuery=span.summary:contains("Not requesting funding")
    and the user should see the element     link=Your project costs
    and the user should see the element     link=Your organisation
    and the user should not see the element     link=Your funding

Academic partner finance section
    [Documentation]    INFUND-7522
    [Tags]    HappyPath
    [Setup]  Log in as a different user       &{collaborator2_credentials}
    Given the user navigates to Your-finances page  ${applicationName}
    Then The user should not see the element      link=Not requesting funding
    and the user should see the element       link=Your project costs
    and the user should not see the element     link=Your organisation
    and the user should see the element        link=Your funding
    and the user should not see the element     link=application details

Academic partner can upload file for field J-es PDF
    [Documentation]    INFUND-7522
    [Tags]    HappyPath
    Given the user navigates to Your-finances page  ${applicationName}
    and the user clicks the button/link         link=Your funding
    # Note the Jes form is already uploaded
    Then the user should see the element     css=a.uploaded-file
    When The user clicks the button/link       jQuery=button:contains("Remove")
    then the user should see the element       jQuery=label[class="button-secondary extra-margin"]
    and the user uploads the file   name=jes-upload   ${valid_pdf}
    and the user should see the text in the page    ${valid_pdf}

Academic partner's Your funding section is read only once marked as complete
    [Documentation]     INFUND-7405
    [Tags]    HappyPath
    When the user selects the checkbox      jQuery=label[for="agree-terms-page"]
    and the user clicks the button/link     jQuery=button:contains("Mark as complete")
    then the user should not see the element   css=input

File upload mandatory for Academic partner to mark section as complete
    [Documentation]    INFUND-8469
    [Tags]    HappyPath    Pending
    #TODO pending due to INFUND-8469
    # This will also check the auto-save as we hvaen't marked finances as complete yet
    Given the user navigates to Your-finances page  ${applicationName}
    and the user clicks the button/link      link=Your funding
    and the user clicks the button/link      jQuery=button:contains("Edit your funding")
    and the user clicks the button/link       jQuery=button:contains("Remove")
    When the user selects the checkbox      jQuery=label[for="agree-terms-page"]
    and the user clicks the button/link     jQuery=button:contains("Mark as complete")
    then the user should see a field error     css=a.uploaded-file

Applicant chooses Calculate overheads option
    [Documentation]     INFUND-6788, INFUND-8191, INFUND-7405
    [Tags]      Pending
    [Setup]  log in as a different user    &{lead_applicant_credentials}
    #TODO Pending due to INFUND-8706
    # This test also checks read only view of the overheads once section is marked as complete
    When the user navigates to Your-finances page     ${Competition_E2E}
    then the user fills in the project costs       ${Competition_E2E}
    When the user clicks the button/link    link=Your project costs
    and the user clicks the button/link    jQuery=button:contains("Overhead costs")
    then the user should not see the element      css=input

*** Keywords ***
Custom Suite Setup
    log in and create new application if there is not one already
    Applicant navigates to the finances of the Robot application

the user adds three material rows
    the user clicks the button/link    jQuery=button:contains("Materials")
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(1) td:nth-of-type(2) input    01
    the user moves focus to the element    jQuery=button:contains(Add another materials cost)
    the user clicks the button/link    jQuery=button:contains(Add another materials cost)
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    01
    the user moves focus to the element    jQuery=button:contains(Add another materials cost)
    the user clicks the button/link    jQuery=button:contains(Add another materials cost)
    the user should see the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    the user enters text to a text field    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    01
    the user moves the mouse away from the element    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input
    the user moves focus to the element    link=Please refer to our guide to project costs for further information.

the user removes the materials rows
    [Documentation]    INFUND-2965
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible Without Screenshots    css=#material-costs-table tbody tr:nth-of-type(4) td:nth-of-type(2) input    10s
    the user moves focus to the element    jQuery=#material-costs-table button:contains("Remove")
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible Without Screenshots    css=#material-costs-table tbody tr:nth-of-type(3) td:nth-of-type(2) input    10s
    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Run Keyword And Ignore Error Without Screenshots    the user clicks the button/link    jQuery=#material-costs-table button:contains("Remove")
    Wait Until Element Is Not Visible Without Screenshots    css=#material-costs-table tbody tr:nth-of-type(2) td:nth-of-type(2) input    10s
    the user clicks the button/link    jQuery=button:contains("Materials")

the working days per year should be 232 by default
    the user should see the element    css=[name^="labour-labourDaysYearly"]
    ${Days_value} =    Get Value    css=[name^="labour-labourDaysYearly"]
    Should Be Equal As Strings    ${Days_value}    232

the user navigates to another page
    the user clicks the button/link    link=Please refer to our guide to project costs for further information.
    Run Keyword And Ignore Error Without Screenshots    Confirm Action

the user should see the funding guidance
    [Documentation]    INFUND-7093
    the user should see the element           jQuery=#details-content-0 p

the user should not see the funding guidance
    [Documentation]    INFUND-7093
    the user should not see the element           jQuery=#details-content-0 p
