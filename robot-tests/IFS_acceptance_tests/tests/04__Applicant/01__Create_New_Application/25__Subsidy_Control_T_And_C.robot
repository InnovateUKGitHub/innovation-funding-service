*** Settings ***
Documentation     IFS-8994  Two new sets of terms & conditions required
...
...               IFS-9137  Update Subsidy control T&Cs for Innovate UK & ATI
...
...               IFS-9214 Add dual T&Cs to Subsidy Control Competitions
...
...               IFS-9233 Applicant can view and accept the correct T&Cs based on their determined Funding Rules
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../resources/defaultResources.robot
Resource          ../../../resources/common/Competition_Commons.robot
Resource          ../../../resources/common/Assessor_Commons.robot
Resource          ../../../resources/common/Applicant_Commons.robot

*** Variables ***
${atiSubsidyControl}             Aerospace Technology Institute (ATI) - Subsidy control (opens in a new window)
${atiStateAid}                   Aerospace Technology Institute (ATI) (opens in a new window)
${innovateUKSubsidyControl}      Innovate UK - Subsidy control (opens in a new window)
${innovateUKStateAid}            Innovate UK (opens in a new window)
${subsidyControlFundingComp}     Subsidy control t and c competition
${subsidyControlFundingApp}      Subsidy control application

*** Test Cases ***
Creating a new comp to confirm ATI subsidy control T&C's
    [Documentation]  IFS-8994  IFS-9137  IFS-9124
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  45
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user selects the radio button       termsAndConditionsId  35
    And the user clicks the button/link         jQuery = button:contains("Done")
    Then the user should see the element        jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Aerospace Technology Institute (ATI) - Subsidy control")
    And the user should see the element         jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Aerospace Technology Institute (ATI)")

ATI subsidy control t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    Given the user clicks the button/link                link = Return to setup overview
    And the user clicks the button/link                  link = Terms and conditions
    And the user clicks the button/link                  jQuery = button:contains("Edit")
    When the user clicks the button/link                 link = ${atiSubsidyControl}
    And select window                                    title = Terms and conditions of an ATI Programme grant - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an ATI Programme grant")
    And the user should see the element                  jQuery = li:contains("State Aid/Subsidy Control obligations")
    [Teardown]   the user closes the last opened tab

ATI State aid t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    When the user clicks the button/link        jQuery = button:contains("Done")
    And the user clicks the button/link         link = ${atiStateAid}
    And select window                           title = Terms and conditions of an ATI Programme grant - Innovation Funding Service
    Then the user should see the element        jQuery = h1:contains("Terms and conditions of an ATI Programme grant")
    And the user should see the element         jQuery = li:contains("State Aid obligations")
    And the user closes the last opened tab
    And the user clicks the button/link         jQuery = button:contains("Done")

ATI subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Creating a new comp to confirm Innovateuk subsidy control T&C's
    [Documentation]  IFS-8994  IFS-9137
    Given the user fills in initial details     ATI Subsidy Control Comp
    When the user clicks the button/link        link = Terms and conditions
    And the user selects the radio button       termsAndConditionsId  44
    And the user clicks the button/link         jQuery = button:contains("Done")
    And the user selects the radio button       termsAndConditionsId  34
    Then the user clicks the button/link        jQuery = button:contains("Done")
    And the user should see the element         jQuery = dt:contains("Subsidy control terms and conditions") ~ dd:contains("Innovate UK - Subsidy control")
    And the user should see the element         jQuery = dt:contains("State aid terms and conditions") ~ dd:contains("Innovate UK")

Innovateuk subsidy control t&c's are correct
    [Documentation]  IFS-8994  IFS-9124
    When the user clicks the button/link                 jQuery = button:contains("Edit")
    And the user clicks the button/link                  link = ${innovateUKSubsidyControl}
    And select window                                    title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element                  jQuery = li:contains("Subsidy Control/ State aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk State aid t&c's are correct
    [Documentation]  IFS-8994
    When the user clicks the button/link                 jQuery = button:contains("Done")
    And the user clicks the button/link                  link = ${innovateUKStateAid}
    And select window                                    title = Terms and conditions of an Innovate UK grant award - Innovation Funding Service
    Then the user should see the element                 jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element                  jQuery = li:contains("State Aid obligations")
    [Teardown]   the user closes the last opened tab

Innovateuk subsidy control T&C's section should be completed
    [Documentation]  IFS-8994
    When the user clicks the button/link     jQuery = button:contains("Done")
    And the user clicks the button/link      link = Back to competition details
    Then the user should see the element     jQuery = li:contains("Terms and conditions") .task-status-complete

Applicant can accept subsidy control terms and conditions based on NI declaration
    [Documentation]  IFS-9223
    Given log in as a different user                               janet.howard@example.com     ${short_password}
    And the user select the competition and starts application     ${subsidyControlFundingComp}
    And the user clicks the button/link                            jQuery = button:contains("Save and continue")
    When the user clicks the button/link                           link = Award terms and conditions
    Then the user should see the element                           jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user should see the element                            jQuery = ul li:contains("shall continue after the project term for a period of 6 years.")
    And the user accepts terms and conditions

Applicant can accept state aid terms and conditions based on NI declaration
    [Documentation]  IFS-9223
    Given the user creates an application             ${subsidyControlFundingComp}   ${subsidyControlFundingApp}
    And requesting subsidy control application id
    When update NI declaration of the application
    And the user clicks the button/link               link = Award terms and conditions
    Then the user should see the element              jQuery = h1:contains("Terms and conditions of an Innovate UK grant award")
    And the user accepts terms and conditions

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

the user creates an application
    [Arguments]  ${subsidyControlFundingComp}   ${subsidyControlFundingApp}
    the user select the competition and starts application      ${subsidyControlFundingComp}
    the user selects the radio button                           createNewApplication  true
    the user clicks the button/link                             jQuery = .govuk-button:contains("Continue")
    the user clicks the button/link                             css = .govuk-button[type="submit"]
    the user clicks the button/link                             link = Application details
    the user fills in the Application details                   ${subsidyControlFundingApp}  ${tomorrowday}  ${month}  ${nextyear}

the user accepts terms and conditions
    the user selects the checkbox      agreed
    the user clicks the button/link    jQuery = button:contains("Agree and continue")
    the user should see the element    jQuery = .form-footer:contains("Terms and conditions accepted")

requesting subsidy control application id
    ${subsidyControlAppId} =  get application id by name   ${subsidyControlFundingApp}
    Set suite variable    ${subsidyControlAppId}

update NI declaration of the application
    execute sql string   UPDATE `${database_name}`.`application_finance` SET `northern_ireland_declaration`=1 WHERE `application_id`='${subsidyControlAppId}';
    reload page