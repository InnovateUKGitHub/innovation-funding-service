*** Settings ***
Documentation     IFS-8994  Two new sets of terms & conditions required
...

Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/Assessor_Commons.robot

*** Variables ***
${atiSubsidyControl}            Aerospace Technology Institute (ATI) - Subsidy control (opens in a new window)
${innovateUKSubsidyControl}     Innovate UK - Subsidy control (opens in a new window)

*** Test Cases ***
Creating a new comp to confirm ATI subsidy control T&C's
    [Documentation]  IFS-8994
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  41
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        link = ${atiSubsidyControl}

ATI subsidy control t&c's are correct
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = ${atiSubsidyControl}
    And select window                        title = Terms and conditions of an ATI Programme grant - Innovation Funding Service
    Then the user should see the element     jQuery = h1:contains("Terms and conditions of an ATI Programme grant")
    And the user should see the element      jQuery = li:contains("State Aid/Subsidy Control obligations")
    [Teardown]   the user closes the last opened tab

ATI subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Creating a new comp to confirm Innovateuk subsidy control T&C's
    [Documentation]  IFS-8994
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  42
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        link = ${innovateUKSubsidyControl}

Innovateuk subsidy control t&c's are correct
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = ${innovateUKSubsidyControl}
    And select window                        title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Then the user should see the element     jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element      jQuery = li:contains("Subsidy Control/ State aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

*** Keywords ***
Custom suite setup
    Set predefined date variables
    Connect To Database   @{database}
    The user logs-in in new browser    &{ifs_admin_user_credentials}

Custom Suite teardown
    Close browser and delete emails
    Disconnect from database

the user fills in initial details
    [Arguments]   ${compName}
    the user navigates to the page               ${CA_UpcomingComp}
    the user clicks the button/link              jQuery = .govuk-button:contains("Create competition")
    the user fills in the CS Initial details     ${compName}  ${month}  ${nextyear}  ${compType_Programme}  SUBSIDY_CONTROL  KTP