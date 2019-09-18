*** Settings ***
Documentation     INFUND-6794: As an applicant I will be invited to add funding details within the 'Your funding' page of the application
...
...               INFUND-6895: As a lead applicant I will be advised that changing my 'Research category' after completing 'Funding level' will reset the 'Funding level'
...
...               IFS-2659: UJ - External - Finances - Able to submit without Other funding
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot

*** Test Cases ***
Other funding validation message
    [Documentation]  IFS-2659
    [Tags]
    Given the user clicks the button/link               link = Your funding
    And the user selects the checkbox                   termsAgreed
    When The user clicks the button/link                jQuery = button:contains("Mark as complete")
    Then The user should see a field and summary error  Select if there has been any other public sector funding.

Applicant has options to enter funding level and details of any other funding
    [Documentation]    INFUND-6794
    [Tags]
    Given the user selects the radio button    otherFunding  true
    And the user should see the element        css = [name*=source]
    And the user should see the element        css = [name*=date]
    And the user should see the element        css = [name*=fundingAmount]

Funding level validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user provides invalid value as percentage then he should see the error  Funding level must be 50% or lower.  60
    When the user provides invalid value as percentage then he should see the error  Funding level must be above 0%.  -14
    When the user provides invalid value as percentage then he should see the error  ${only_accept_whole_numbers_message}  15.35
    #TODO add server side validation for the percentage field when double number is provided IFS-3066
    And the user selects the radio button         requestingFunding   true
    When the user enters text to a text field     css = [name^="grantClaimPercentage"]  24
    Then the user cannot see a validation error in the page

Other funding validations
    [Documentation]    INFUND-6794
    [Tags]
    Given the user enters text to a text field          css = [name*=date]    20
    And the user enters text to a text field            css = [name*=fundingAmount]    txt
    And the user clicks the button/link                 jQuery = button:contains("Mark as complete")
    And The user should see a field and summary error   Enter date secured.
    And The user should see a field and summary error   Enter funding amount.
    And The user should see a field and summary error   Enter a funding source.
    When the user enters text to a text field           css = [name*=date]    12-${nextyear}
    And the user enters text to a text field            css = [name*=source]  Lottery funding
    And the user enters text to a text field            css = [name*=fundingAmount]    20000
    Then the user cannot see a validation error in the page
    And the user selects the checkbox                   termsAgreed
    And the user clicks the button/link                 jQuery = button:contains("Mark as complete")

If funding is complete. application details has a warning message
    [Documentation]    INFUND-6895
    ...
    ...    INFUND-6823
    [Tags]  HappyPath
    Given the user navigates to the page   ${APPLICANT_DASHBOARD_URL}
    And the user clicks the button/link    link = ${openCompetitionRTOApplication1Name}
    When the user clicks the button/link   link = Research category
    And the user clicks the button/link    jQuery = button:contains(Edit)
    Then the user should see the element   jQuery = .message-alert p:contains("Changing the research category will reset the funding level for all business participants.")

Changing application details sets funding level to incomplete
    [Documentation]    INFUND-6895
    [Tags]  HappyPath
    Given the user clicks the button twice    css = label[for="researchCategory2"]
    And the user clicks the button/link       id = application-question-complete
    And the user navigates to Your-finances page  ${openCompetitionRTOApplication1Name}
    Then the user should see the element      css = .task-list li:nth-of-type(4) .task-status-incomplete

Funding level has been reset
    [Documentation]    INFUND-6895
    [Tags]
    When the user clicks the button/link    link = Your funding
    Then the user should see the element    jQuery = button:contains("Mark as complete")
    And the user should not see the text in the element    css = [name*=source]    Lottery funding
    And the user should not see the text in the element    css = [name*=date]    12-${nextyear}
    And the user should not see the text in the element    css = [name*=fundingAmount]    20000

Funding level can be re-entered, and this saves correctly
    [Documentation]  INFUND-6895
    [Tags]
    Given the user selects the radio button         requestingFunding   true
    And the user enters text to a text field        css = [name^="grantClaimPercentage"]    25
    When the user enters text to a text field       css = [name*=source]  Lottery funding
    Then the user enters text to a text field       css = [name*=date]  12-${nextyear}
    And the user enters text to a text field        css = [name*=fundingAmount]  20000

Adding more funding rows
    [Documentation]    INFUND-6895, INFUND-8044
    [Tags]
    When remove previous rows  css = tr:first-of-type .js-remove-row:not([value=""])
    Then the user adds more rows in other funding

Mark other funding as complete
    [Documentation]  INFUND-6895
    [Tags]
    Given the user selects the checkbox   termsAgreed
    When the user clicks the button/link  jQuery = button:contains("Mark as complete")
    Then the user should not see an error in the page
    And the user should see the element   css = .task-list li:nth-of-type(3) .task-status-complete

Read only view of the other funding
    [Documentation]    INFUND-6895, INFUND-8044
    [Tags]
    Given the user clicks the button/link  link = Your funding
    Then the user should see the element   jQuery = dt:contains("Funding level") + dd:contains("25")
    And the user clicks the button/link    jQuery = th:contains("uncle") ~ td:contains("£15,000")
    And the user clicks the button/link    jQuery = th:contains("grandma") ~ td:contains("£200,000")
    And the user should see the element    jQuery = button:contains("Edit")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    the user logs-in in new browser                   &{lead_applicant_credentials}
    the user navigates to the page                    ${server}/application/${openCompetitionRTOApplication1Id}
    the user clicks the button/link                   link = Application details
    the user fills in the Application details         ${openCompetitionRTOApplication1Name}   ${tomorrowday}  ${month}  ${nextyear}
    the user selects research category                Feasibility studies
    Complete the org size section                     ${openCompetitionRTOApplication1Name}

the user provides invalid value as percentage then he should see the error
    [Arguments]  ${error}  ${value}
    the user selects the radio button     requestingFunding   true
    the user enters text to a text field  css = [name^="grantClaimPercentage"]  ${value}
    Set Focus To Element                  css = button.govuk-button[type="submit"]
    the user should see a field error     ${error}

Complete the org size section
    [Arguments]  ${applicationName}
    the user navigates to the page                      ${APPLICANT_DASHBOARD_URL}
    the user clicks the button/link                     link = ${applicationName}
    the user clicks the button/link                     link = Your project finances
    the user clicks the button/link                     link = Your organisation
    ${orgSizeReadonly} =   Run Keyword And Return Status    Element Should Be Visible   jQuery = button:contains("Edit")
    Run Keyword If    ${orgSizeReadonly}    the user clicks the button/link    jQuery = button:contains("Edit")
    the user selects the radio button                   organisationSize  ${LARGE_ORGANISATION_SIZE}
    the user enters text to a text field                css = #turnover    150
    the user enters text to a text field                css = #headCount    0
    the user selects the checkbox                       stateAidAgreed
    Set Focus To Element                                jQuery = button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    jQuery = button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    link = Your project finances

the user adds more rows in other funding
    the user clicks the button/link         jQuery = button:contains("Add another source of funding")
    the user enters text to a text field    css = [name*=source]  Lottery funding
    the user enters text to a text field    css = [name*=date]  12-${nextyear}
    the user enters text to a text field    css = [name*=fundingAmount]  20000
    Set Focus To Element                    jQuery = button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link         jQuery = button:contains("Add another source of funding")
    the user should see the element         css = tr:nth-of-type(2) input[name*=source]
    the user enters text to a text field    css = tr:nth-of-type(2) input[name*=source]  wealthy uncle
    the user enters text to a text field    css = tr:nth-of-type(2) input[name*=date]  02-${nextyear}
    the user enters text to a text field    css = tr:nth-of-type(2) input[name*=fundingAmount]  15000
    Set Focus To Element                    jQuery = button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link         jQuery = button:contains("Add another source of funding")
    the user should see the element         css = tr:nth-of-type(3) input[name*=source]
    the user enters text to a text field    css = tr:nth-of-type(3) input[name*=source]  wealthy grandma
    the user enters text to a text field    css = tr:nth-of-type(3) input[name*=date]  11-${nextyear}
    the user enters text to a text field    css = tr:nth-of-type(3) input[name*=fundingAmount]  200000
    Set Focus To Element                    jQuery = button:contains("Mark as complete")
    wait for autosave
    Textfield Value Should Be               jQuery = label:contains("Total other funding") + input    £235,000

the user changes the research category
    [Documentation]    INFUND-8260
    the user clicks the button twice   css = label[for="researchCategory2"]
    the user clicks the button/link    jQuery = button:contains(Save)

Custom suite teardown
    The user closes the browser
