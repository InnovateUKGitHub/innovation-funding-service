*** Settings ***
Documentation     INFUND-1110: As an applicant/partner applicant I want to add my required Funding Level so that innovate uk know my grant request
...
...               INFUND-6394: As an Applicant I will be invited to input my Organisation size within a new ‘Your organisation’ page navigated to from ‘Your finances’
...
...               INFUND-6894: As an Applicant I will be advised that changing my 'Organisation size' after completing 'Funding level' will reset the 'Funding level'
Suite Setup       log in and create new application if there is not one already with complete application details
Suite Teardown    mark application details incomplete the user closes the browser
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../FinanceSection_Commons.robot

*** Variables ***


*** Test Cases ***
Before org size is selected, your funding link is not available
    [Documentation]    INFUND-6394
    [Tags]
    When applicant navigates to the finances of the robot application
    Then the user should not see the element    link=Your funding

Small org can be selected
    [Documentation]    INFUND-1110, INFUND-6394
    [Tags]    HappyPath
    Given applicant navigates to the finances of the robot application
    And the user clicks the button/link    link=Your organisation
    And the user marks their organisation as    ${SMALL_ORGANISATION_SIZE}


Funding section is now available
    [Documentation]    INFUND-6394
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    Then the user should see the text in the page    Enter your funding level


Small org can't have more than 70% funding level
    [Documentation]    INFUND-1110
    [Tags]
    When the user enters text to a text field    id=cost-financegrantclaim    46
    Then the user should see the element  jQuery=span.error-message:contains("This field should be 45% or lower.")


Funding section can be completed with under 70%
    [Documentation]    INFUND-1110
    [Tags]    HappyPath
    When the user completes the funding section with funding level    45
    Then the user should not see the element    jQuery=.error-message


User sees warning that the funding section will be reset
    [Documentation]    INFUND-6894
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your organisation
    Then the user should see the text in the page    The organisation size is used to calculate your funding level in the application.
    And the user should see the text in the page    Changing this selection will reset your funding level.


Medium org can be selected
    [Documentation]    INFUND-1110, INFUND-6394
    [Tags]    HappyPath
    When the user clicks the button/link   jQuery=button:contains("Edit")
    And the user marks their organisation as    ${MEDIUM_ORGANISATION_SIZE}

Funding section shows as incomplete
    [Documentation]    INFUND-6394
    [Tags]
    When the user should see the element    css=.task-list li:nth-of-type(3) .action-required

Funding section has been reset
    [Documentation]    INFUND-6894
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    Then the funding section has been reset including funding level    45


Medium org can't have more than 60% level
    [Documentation]    INFUND-1110
    [Tags]
    When the user enters text to a text field    css=#cost-financegrantclaim    36
    Then the user should see the element  jQuery=span.error-message:contains("This field should be 35% or lower.")


Funding section can be completed with under 60%
    [Documentation]    INFUND-1110
    [Tags]    HappyPath
    When the user completes the funding section with funding level    35
    Then the user should not see the element    jQuery=.error-message


User still sees warning that the funding section will be reset
    [Documentation]    INFUND-6894
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your organisation
    Then the user should see the text in the page    The organisation size is used to calculate your funding level in the application.
    And the user should see the text in the page    Changing this selection will reset your funding level.


Large organisation can be selected
    [Documentation]    INFUND-1110, INFUND_6394
    [Tags]    HappyPath
    When the user clicks the button/link   jQuery=button:contains("Edit")
    And the user marks their organisation as    ${LARGE_ORGANISATION_SIZE}

Funding section shows as incomplete again
    [Documentation]    INFUND-6394
    [Tags]
    When the user should see the element    css=.task-list li:nth-of-type(3) .action-required


Funding section has been reset again
    [Documentation]    INFUND-6894
    [Tags]    HappyPath
    When the user clicks the button/link    link=Your funding
    Then the funding section has been reset including funding level    35



Large org can't have more than 50% level
    [Documentation]    INFUND-1110
    [Tags]
    When the user enters text to a text field    css=#cost-financegrantclaim    27
    Then the user should see the element  jQuery=span.error-message:contains("This field should be 25% or lower.")


Funding section can be completed with under 50%
    [Documentation]    INFUND-1110
    [Tags]    HappyPath
    When the user completes the funding section with funding level    25
    Then the user should not see the element    jQuery=.error-message
    And the user marks the 'your funding' section as incomplete again



*** Keywords ***

The user marks their organisation as
    [Arguments]    ${org_size}
    the user selects the radio button    financePosition-organisationSize  ${org_size}
    the user enters text to a text field    jQuery=label:contains("Turnover") + input    150
    the user enters text to a text field    jQuery=label:contains("employees") + input    0
    the user clicks the button/link    jQuery=button:contains("Mark as complete")
    the user should not see the element  jQuery=.error-message
    the user should see the text in the page    Please complete your project finances.


the user completes the funding section with funding level
    [Arguments]    ${funding_level}
    the user enters text to a text field    id=cost-financegrantclaim    ${funding_level}
    the user selects the radio button    other_funding-otherPublicFunding-    Yes
    the user enters text to a text field    css=[name*=other_funding-fundingSource]    Lottery funding
    the user enters text to a text field    css=[name*=other_funding-securedDate]    12-2008
    the user enters text to a text field    css=[name*=other_funding-fundingAmount]    20000
    the user selects the checkbox    termsAgreed
    the user clicks the button/link    jQuery=button:contains("Mark as complete")

the funding section has been reset including funding level
    [Arguments]    ${funding_level}
    Then the user should not see the text in the element    css=#cost-financegrantclaim    ${funding_level}
    And checkbox should not be selected  termsAgreed
    And the user should not see the text in the element    css=[name*=other_funding-fundingSource]    Lottery funding
    And the user should not see the text in the element    css=[name*=other_funding-securedDate]    12-2008
    And the user should not see the text in the element    css=[name*=other_funding-fundingAmount]    20000

the user marks the 'your funding' section as incomplete again
    the user clicks the button/link    link=Your funding
    the user clicks the button/link    jQuery=button:contains("Edit")
