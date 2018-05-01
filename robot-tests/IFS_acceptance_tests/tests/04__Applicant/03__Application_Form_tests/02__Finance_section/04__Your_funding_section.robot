*** Settings ***
Documentation     INFUND-6794: As an applicant I will be invited to add funding details within the 'Your funding' page of the application
...               INFUND-6895: As a lead applicant I will be advised that changing my 'Research category' after completing 'Funding level' will reset the 'Funding level'
...               IFS-2659: UJ - External - Finances - Able to submit without Other funding
Suite Setup       Custom Suite Setup
Suite Teardown    the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot


*** Variables ***
${applicationName}  Hydrology the dynamics of Earth's surface water


*** Test Cases ***
Other funding validation message
    [Documentation]  IFS-2659
    [Tags]  HappyPath
    Given the user clicks the button/link               link=Your funding
    And the user selects the checkbox                   termsAgreed
    When The user clicks the button/link                jQuery=button:contains("Mark as complete")
    Then The user should see a field and summary error  Please tell us if you have received any other funding for this project.

Applicant has options to enter funding level and details of any other funding
    [Documentation]    INFUND-6794
    [Tags]    HappyPath
    Given the user selects the radio button    other_funding-otherPublicFunding-    Yes
    Then the user should see the element    css=[name^="finance-grantclaimpercentage"]
    And the user should see the element    css=[name*=other_funding-fundingSource]
    And the user should see the element    css=[name*=other_funding-securedDate]
    And the user should see the element    css=[name*=other_funding-fundingAmount]
    And the user should see the element    css=[name^="other_funding-otherPublicFunding-"] ~ label

Applicant can see maximum funding size available to them
    [Documentation]    INFUND-6794
    [Tags]    HappyPath
    The user should see the text in the page    Enter your funding level (maximum 50%)

Funding level validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user provides invalid value as percentage then he should see the error  This field should be 50% or lower.  60
    When the user provides invalid value as percentage then he should see the error  This field should be 0% or higher.  -14
    When the user provides invalid value as percentage then he should see the error  This field can only accept whole numbers.  15.35
    #TODO add server side validation for the percentage field when double number is provided IFS-3066
    When the user enters text to a text field  css=[name^="finance-grantclaimpercentage"]  24
    Then the user should not see an error in the page

Other funding validations
    [Documentation]    INFUND-6794
    [Tags]
    Given the user enters text to a text field           css=[name*=other_funding-securedDate]    20
    And the user enters text to a text field            css=[name*=other_funding-fundingAmount]    txt
    And the user clicks the button/link                 jQuery=button:contains("Mark as complete")
    And The user should see a field and summary error  Invalid secured date
    And The user should see a field and summary error   Funding source cannot be blank.
    #TODO update the below error after IFS-3454 is done.
    And The user should see a field and summary error   This field should be 1 or higher.
    When the user enters text to a text field           css=[name*=other_funding-securedDate]    12-${nextyear}
    And the user enters text to a text field            css=[name*=other_funding-fundingSource]  Lottery funding
    And the user enters text to a text field           css=[name*=other_funding-fundingAmount]    20000
    #TODO IFS-3457
    #Then the user cannot see a validation error in the page
    And the user selects the checkbox                   termsAgreed
    And the user clicks the button/link                 jQuery=button:contains("Mark as complete")

If funding is complete. application details has a warning message
    [Documentation]    INFUND-6895
    ...
    ...    INFUND-6823
    [Tags]    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=${applicationName}
    When the user clicks the button/link    link=Application details
    And the user clicks the button/link    jQuery=button:contains(Edit)
    And the user clicks the button/link    jQuery=button:contains("Change your research category")
    Then the user should see the text in the page    Changing the research category will reset the funding level for all business participants

Changing application details sets funding level to incomplete
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user changes the research category
    And the user clicks the button/link    name=mark_as_complete
    And the user navigates to Your-finances page  ${applicationName}
    Then the user should see the element    css=.task-list li:nth-of-type(3) .action-required

Funding level has been reset
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    Then the user should see the element    jQuery=button:contains("Mark as complete")
    And the user should not see the text in the element    css=[name*=other_funding-fundingSource]    Lottery funding
    And the user should not see the text in the element    css=[name*=other_funding-securedDate]    12-${nextyear}
    And the user should not see the text in the element    css=[name*=other_funding-fundingAmount]    20000

Funding level can be re-entered, and this saves correctly
    [Documentation]  INFUND-6895
    [Tags]  HappyPath
    Given the user enters text to a text field  css=[name^="finance-grantclaimpercentage"]    43
    When the user enters text to a text field   css=[name*=other_funding-fundingSource]  Lottery funding
    Then the user enters text to a text field       css=[name*=other_funding-securedDate]  12-${nextyear}
    And the user enters text to a text field        css=[name*=other_funding-fundingAmount]  20000

Adding more funding rows
    [Documentation]    INFUND-6895, INFUND-8044
    [Tags]
    When remove previous rows  css=tr:first-of-type .js-remove-row:not([value=""])
    Then the user adds more rows in other funding

Mark other funding as complete
    [Documentation]  INFUND-6895
    [Tags]  HappyPath
    Given the user selects the checkbox   termsAgreed
    When the user clicks the button/link  jQuery=button:contains("Mark as complete")
    Then the user should not see an error in the page
    And the user should see the element   css=.task-list li:nth-of-type(3) .task-status-complete

Read only view of the other funding
    [Documentation]    INFUND-6895, INFUND-8044
    [Tags]
    Given the user clicks the button/link  link=Your funding
    Then the user should see the element   jQuery=dt:contains("Funding level") + dd:contains("43")
    And the user clicks the button/link    jQuery=th:contains("uncle") ~ td:contains("£15,000")
    And the user clicks the button/link    jQuery=th:contains("grandma") ~ td:contains("£200,000")
    And the user should see the element    jQuery=button:contains("Edit")

*** Keywords ***
Custom Suite Setup
    Set predefined date variables
    the user logs-in in new browser       &{lead_applicant_credentials}
    ${applicationId} =  get application id by name  ${applicationName}
    the user navigates to the page  ${server}/application/${applicationId}
    the user clicks the button/link  link=Application details
    the user fills in the Application details  ${applicationName}  Feasibility studies  ${tomorrowday}  ${month}  ${nextyear}
    Complete the org size section  ${applicationName}

the user provides invalid value as percentage then he should see the error
    [Arguments]  ${error}  ${value}
    the user enters text to a text field  css=[name^="finance-grantclaimpercentage"]  ${value}
    the user moves focus to the element   css=button.button[type="submit"]
    the user should see a field error     ${error}

Complete the org size section
    [Arguments]  ${applicationName}
    the user navigates to the page    ${DASHBOARD_URL}
    the user clicks the button/link    link=${applicationName}
    the user clicks the button/link    link=Your finances
    the user clicks the button/link    link=Your organisation
    ${orgSizeReadonly}=  Run Keyword And Return Status    Element Should Be Visible   jQuery=button:contains("Edit")
    Run Keyword If    ${orgSizeReadonly}    the user clicks the button/link    jQuery=button:contains("Edit")
    the user selects the radio button    financePosition-organisationSize  ${LARGE_ORGANISATION_SIZE}
    the user enters text to a text field    jQuery=label:contains("Turnover") + input    150
    the user enters text to a text field    jQuery=label:contains("employees") + input    0
    the user moves focus to the element    jQuery=button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    jQuery=button:contains("Mark as complete")
    run keyword and ignore error without screenshots    the user clicks the button/link    link=Your finances

the user adds more rows in other funding
    the user enters text to a text field  css=[name*=other_funding-fundingSource]  Lottery funding
    the user enters text to a text field  css=[name*=other_funding-securedDate]  12-${nextyear}
    the user enters text to a text field  css=[name*=other_funding-fundingAmount]  20000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link       jQuery=button:contains("Add another source of funding")
    The user should see the element         css=tr:nth-of-type(2) input[name*=fundingSource]
    the user enters text to a text field  css=tr:nth-of-type(2) input[name*=fundingSource]  wealthy uncle
    the user enters text to a text field  css=tr:nth-of-type(2) input[name*=securedDate]  02-${nextyear}
    the user enters text to a text field  css=tr:nth-of-type(2) input[name*=fundingAmount]  15000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link       jQuery=button:contains("Add another source of funding")
    The user should see the element         css=tr:nth-of-type(3) input[name*=fundingSource]
    the user enters text to a text field  css=tr:nth-of-type(3) input[name*=fundingSource]  wealthy grandma
    the user enters text to a text field  css=tr:nth-of-type(3) input[name*=securedDate]  11-${nextyear}
    the user enters text to a text field  css=tr:nth-of-type(3) input[name*=fundingAmount]  200000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    Textfield Value Should Be             jQuery=label:contains("Total other funding") + input    £235,000

the user changes the research category
    [Documentation]    INFUND-8260
    # Often those labels need double click. Thus i made a separate keyword to looks more tidy
    the user clicks the button/link    css=label[for="researchCategoryChoice-34"]
    the user clicks the button/link    css=label[for="researchCategoryChoice-34"]
    the user clicks the button/link    jQuery=button:contains(Save)
