*** Settings ***
Documentation     INFUND-1110: As an applicant/partner applicant I want to add my required Funding Level so that innovate uk know my grant request
...
...               INFUND-6394: As an Applicant I will be invited to input my Organisation size within a new ‘Your organisation’ page navigated to from ‘Your project finances’
...
...               INFUND-6894: As an Applicant I will be advised that changing my 'Organisation size' after completing 'Funding level' will reset the 'Funding level'
...
...               IFS-3938 As an applicant the requirement prerequesites for Your funding are clear
...
...               IFS-8991 Applicant journey - update content - 'Research category' & 'Your organisation'
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Force Tags        Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot

*** Test Cases ***
Before org size is selected, your funding link is not available
    [Documentation]    INFUND-6394  IFS-3938
    [Tags]  HappyPath
    Given applicant navigates to the finances of the robot application
    When the user clicks the button/link        link = Your funding
    Then the user should see the element        jQuery = li:contains("mark the"):contains("your organisation")

Small org can be selected
    [Documentation]    INFUND-1110, INFUND-6394
    [Tags]  HappyPath
    Given applicant navigates to the finances of the robot application
    And the user clicks the button/link         link = Your organisation
    And the user marks their organisation as    ${SMALL_ORGANISATION_SIZE}

Funding section is now available
    [Documentation]    INFUND-6394
    [Tags]  HappyPath
    When the user clicks the button/link             link = Your funding
    Then The user should see the element             jQuery = legend:contains("Are you requesting funding?")

Small org can't have more than 70% funding level
    [Documentation]    INFUND-1110
    [Tags]
    Given the user selects the radio button    requestingFunding   true
    When the user enters text to a text field  css = [name^="grantClaimPercentage"]  80
    Then the user should see a field error     Funding level must be 70% or lower.

Funding section can be completed with under 70%
    [Documentation]    INFUND-1110
    [Tags]  HappyPath
    When the user completes the funding section with funding level    45
    Then the user should not see the element    jQuery = .govuk-error-message

User sees warning that the funding section will be reset
    [Documentation]    INFUND-6894
    [Tags]  HappyPath
    When the user clicks the button/link             link = Your organisation
    Then the user should see the element             jQuery = .message-alert p:contains("The organisation size is used to calculate your funding level.")
    And the user should see the element              jQuery = .message-alert p:contains("Changing this selection will reset your funding level.")

Medium org can be selected
    [Documentation]    INFUND-1110, INFUND-6394
    [Tags]  HappyPath
    When the user clicks the button/link        jQuery = button:contains("Edit")
    And the user marks their organisation as    ${MEDIUM_ORGANISATION_SIZE}

Funding section shows as incomplete
    [Documentation]    INFUND-6394
    [Tags]
    When the user should see the element    css = .task-list li:nth-of-type(4) .task-status-incomplete

Funding section has been reset
    [Documentation]    INFUND-6894
    [Tags]  HappyPath
    When the user clicks the button/link    link = Your funding
    Then the funding section has been reset including funding level    45

Medium org can't have more than 60% level
    [Documentation]    INFUND-1110
    [Tags]
    Given the user selects the radio button      requestingFunding   true
    When the user enters text to a text field    css = [name^="grantClaimPercentage"]  70
    Then the user should see a field error       Funding level must be 60% or lower.

Funding section can be completed with under 60%
    [Documentation]    INFUND-1110
    [Tags]  HappyPath
    When the user completes the funding section with funding level    35
    Then the user should not see the element    css = .govuk-error-message

User still sees warning that the funding section will be reset
    [Documentation]    INFUND-6894
    [Tags]  HappyPath
    When the user clicks the button/link             link = Your organisation
    Then the user should see the element             jQuery = .message-alert p:contains("The organisation size is used to calculate your funding level.")
    And the user should see the element              jQuery = .message-alert p:contains("Changing this selection will reset your funding level.")

Large organisation can be selected
    [Documentation]    INFUND-1110, INFUND_6394, IFS-8991
    [Tags]  HappyPath
    Given the user clicks the button/link       jQuery = button:contains("Edit")
    Then the user marks their organisation as   ${LARGE_ORGANISATION_SIZE}

Funding section shows as incomplete again
    [Documentation]    INFUND-6394
    [Tags]
    When the user should see the element    css = .task-list li:nth-of-type(4) .task-status-incomplete

Funding section has been reset again
    [Documentation]    INFUND-6894
    [Tags]  HappyPath
    When the user clicks the button/link    link = Your funding
    Then the funding section has been reset including funding level    35

Large org can't have more than 50% level
    [Documentation]    INFUND-1110
    [Tags]
    Given the user selects the radio button    requestingFunding   true
    When the user enters text to a text field  css = [name^="grantClaimPercentage"]  60
    Then the user should see a field error     Funding level must be 50% or lower.

Funding section can be completed with under 50%
    [Documentation]    INFUND-1110
    [Tags]  HappyPath
    When the user completes the funding section with funding level    25
    Then the user should not see the element    jQuery = .govuk-error-message
    And the user marks the 'your funding' section as incomplete again

Eligibility criteria link navigates to competition eligibility page
    [Documentation]     IFS-8991
    Given the user clicks the button/link         jQuery = button:contains('Save and return to project finances')
    And the user clicks the button/link           link = Your organisation
    And the user clicks the button/link           id = mark_as_incomplete
    When the user clicks the button/link          jQuery = a:contains('eligibility criteria')
    Then the user should see the element          id = eligibility
    [Teardown]  the user goes back to the previous page

*** Keywords ***
Custom Suite Setup
    Connect to database  @{database}
    Set predefined date variables
    log in and create new application if there is not one already with complete application details  Robot test application  ${tomorrowday}  ${month}  ${nextyear}

The user marks their organisation as
    [Arguments]    ${org_size}
    the user selects the radio button           organisationSize  ${org_size}
    the user enters text to a text field        css = #turnover    150
    the user enters text to a text field        css = #headCount    0
    the user clicks the button/link             jQuery = button:contains("Mark as complete")
    the user should not see the element         css = .govuk-error-message
    the user should see the element             jQuery = p:contains("Please complete your project finances.")

the user completes the funding section with funding level
    [Arguments]    ${funding_level}
    the user selects the radio button       requestingFunding   true
    the user enters text to a text field    css = [name^="grantClaimPercentage"]    ${funding_level}
    the user selects the radio button       otherFunding  true
    the user enters text to a text field    css = [name*=source]           Lottery funding
    the user enters text to a text field    css = [name*=date]             12-2008
    the user enters text to a text field    css = [name*=fundingAmount]    20000
    the user clicks the button/link         jQuery = button:contains("Mark as complete")

the funding section has been reset including funding level
    [Arguments]    ${funding_level}
    the user selects the radio button                  requestingFunding   true
    the user should not see the text in the element    css = [name^="grantClaimPercentage"]    ${funding_level}
    the user should not see the text in the element    css = [name*=source]    Lottery funding
    the user should not see the text in the element    css = [name*=date]    12-2008
    the user should not see the text in the element    css = [name*=fundingAmount]    20000

the user marks the 'your funding' section as incomplete again
    the user clicks the button/link    link = Your funding
    the user clicks the button/link    jQuery = button:contains("Edit")

Custom suite teardown
    Mark application details as incomplete and the user closes the browser  Robot test application
    Disconnect from database
