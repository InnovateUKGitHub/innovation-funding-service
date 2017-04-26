*** Settings ***
Documentation     INFUND-6794: As an applicant I will be invited to add funding details within the 'Your funding' page of the application
...               INFUND-6895: As a lead applicant I will be advised that changing my 'Research category' after completing 'Funding level' will reset the 'Funding level'
Suite Setup       Custom Suite Setup
Suite Teardown    mark application details incomplete the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Test Cases ***
Applicant has options to enter funding level and details of any other funding
    [Documentation]    INFUND-6794
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    And the user selects the radio button    other_funding-otherPublicFunding-    Yes
    Then the user should see the element    id=cost-financegrantclaim
    And the user should see the element    css=[name*=other_funding-fundingSource]
    And the user should see the element    css=[name*=other_funding-securedDate]
    And the user should see the element    css=[name*=other_funding-fundingAmount]
    And the user should see the radio button in the page    other_funding-otherPublicFunding-

Applicant can see maximum funding size available to them
    [Documentation]    INFUND-6794
    [Tags]    HappyPath
    When the user should see the text in the page    Enter your funding level (maximum 25%)

Funding level validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user enters text to a text field    id=cost-financegrantclaim    26
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the element    jQuery=span.error-message:contains("This field should be 25% or lower.")
    When the user enters text to a text field    id=cost-financegrantclaim    25
    Then the user should not see the element    jQuery=span.error-message:contains("This field should be 25% or lower.")
    And the user should not see the element    jQuery=.error-message

Other funding validations
    [Documentation]    INFUND-6794
    [Tags]
    When the user enters text to a text field    css=[name*=other_funding-securedDate]    20
    And the user enters text to a text field    css=[name*=other_funding-fundingAmount]    txt
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")
    Then the user should see the text in the page    Invalid secured date
    And the user should see the text in the page    This field cannot be left blank
    When the user enters text to a text field    css=[name*=other_funding-securedDate]    12-${nextyear}
    Then the user should not see the text in the page    Please enter a valid date
    When the user enters text to a text field    css=[name*=other_funding-fundingAmount]    20000
    Then the user should not see the text in the page    This field cannot be left blank
    And the user should not see an error in the page
    And the user selects the checkbox    termsAgreed
    And the user clicks the button/link    jQuery=button:contains("Mark as complete")

If funding is complete. application details has a warning message
    [Documentation]    INFUND-6895
    ...
    ...    INFUND-6823
    [Tags]    HappyPath
    Given the user navigates to the page    ${DASHBOARD_URL}
    And the user clicks the button/link    link=Robot test application
    When the user clicks the button/link    link=Application details
    And the user clicks the button/link    jQuery=button:contains(Edit)
    And the user clicks the button/link    jQuery=button:contains("Change your research category")
    Then the user should see the text in the page    Changing the research category will reset the funding level for all business participants

Changing application details sets funding level to incomplete
    [Documentation]    INFUND-6895
    [Tags]    HappyPath
    When the user changes the research category
    And the user clicks the button/link    name=mark_as_complete
    And applicant navigates to the finances of the robot application
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
    Given the user enters text to a text field  id=cost-financegrantclaim    43
    When the user enters text to a text field   css=[name*=other_funding-fundingSource]  Lottery funding
    Then the user enters text to a text field       css=[name*=other_funding-securedDate]  12-${nextyear}
    And the user enters text to a text field        css=[name*=other_funding-fundingAmount]  20000

Adding more funding rows
    [Documentation]    INFUND-6895, INFUND-8044
    [Tags]
    # TODO INFUND-8706
    When remove previous rows  jQuery=tr:first-of-type button:contains("Remove")
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
    # TODO INFUND-8706
    Given the user clicks the button/link  link=Your funding
    Then the user should see the element   jQuery=dt:contains("Funding level") + dd:contains("43")
    And the user clicks the button/link    jQuery=th:contains("uncle") ~ td:contains("£ 15,000")
    And the user clicks the button/link    jQuery=th:contains("grandma") ~ td:contains("£ 200,000")
    And the user should see the element    jQuery=button:contains("Edit")

*** Keywords ***
Custom Suite Setup
    log in and create a new application if there is not one already with complete application details and completed org size section
    ${nextyear} =  get next year
    Set suite variable  ${nextyear}

the user adds more rows in other funding
    the user enters text to a text field  css=[name*=other_funding-fundingSource]  Lottery funding
    the user enters text to a text field  css=[name*=other_funding-securedDate]  12-${nextyear}
    the user enters text to a text field  css=[name*=other_funding-fundingAmount]  20000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link       jQuery=button:contains("Add another source of funding")
    Wait Until Element Is Not Visible     jQuery=tr:last-of-type button:contains("Remove")
    the user enters text to a text field  css=tr:last-of-type input[name*=fundingSource]  wealthy uncle
    the user enters text to a text field  css=tr:last-of-type input[name*=securedDate]  02-${nextyear}
    the user enters text to a text field  css=tr:last-of-type input[name*=fundingAmount]  15000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    the user clicks the button/link       jQuery=button:contains("Add another source of funding")
    Wait Until Element Is Not Visible     jQuery=tr:last-of-type button:contains("Remove")
    the user enters text to a text field  css=tr:last-of-type input[name*=fundingSource]  wealthy grandma
    the user enters text to a text field  css=tr:last-of-type input[name*=securedDate]  11-${nextyear}
    the user enters text to a text field  css=tr:last-of-type input[name*=fundingAmount]  200000
    the user moves focus to the element   jQuery=button:contains("Mark as complete")
    wait for autosave
    Textfield Value Should Be             jQuery=label:contains("Total other funding") + input    £ 235,000

the user changes the research category
    [Documentation]    INFUND-8260
    # Often those labels need double click. Thus i made a separate keyword to looks more tidy
    the user clicks the button/link    jQuery=label[for="researchCategoryChoice-34"]
    the user clicks the button/link    jQuery=label[for="researchCategoryChoice-34"]
    the user clicks the button/link    jQuery=button:contains(Save)
