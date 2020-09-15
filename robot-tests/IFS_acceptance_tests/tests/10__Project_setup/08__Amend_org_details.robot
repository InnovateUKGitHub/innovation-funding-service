*** Settings ***
Documentation     IFS-6695: Edit funding levels in project setup
Suite Setup       Custom suite setup
Suite Teardown    Close browser and delete emails
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/common/Applicant_Commons.robot

*** Variables ***
${projectName}    PSC application 19
${projectId}      ${project_ids["${projectName}"]}
${compId}         ${competition_ids["Project Setup Comp 19"]}
${applId}         ${application_ids["${projectName}"]}

*** Test Cases ***
User can see current and applied for values with no changes
    [Documentation]  IFS-7005
    Given the user navigates to the page                  ${server}/project-setup-management/project/${projectId}/finance-check
    Then the user should see the correct funding values   £116,596  £116,596

User can view funding level change page
    [Documentation]  IFS-6695
    Given the user navigates to the page   ${server}/project-setup-management/project/${projectId}/finance-check-overview
    And The user clicks the button/link    link = Change funding level percentages
    When the user should see the element   jQuery = h1:contains("Change funding level")
    Then the user should see the element   jQuery = td:contains("Ward") ~ td:contains("£57,803") ~ td:contains("49.58%") ~ td ~ td:contains("£57,803") ~ td:contains("49.58%")
    And the user should see the element    jQuery = th:contains("Total grant value") ~ td:contains("£402,797") ~ td:contains("£116,596") ~ td:contains("£116,596")

Project finance cannot add an invalid percentage
    [Documentation]  IFS-6695
    Given the user enters text to a text field            id = partners[${orgId}].fundingLevel  300
    And the user should see a field error                 Funding level must be 45% or lower.
    When the user clicks the button/link                  jQuery = button:contains("Save and return to project finances")
    Then the user should see a field and summary error    Funding level must be 45% or lower.

User can change a percentage to be not requesting funding
    [Documentation]  IFS-7872
    Given the user enters text to a text field     id = partners[${orgId}].fundingLevel  0
    Then the user should not see a field error     The level of funding must be above 0%.

Values are updated dynamically as new percentages are added
    [Documentation]  IFS-6695  IFS-7128
    Given the user enters text to a text field           id = partners[${orgId}].fundingLevel  32.23
    Then the user should see the correct revised values  £62,283  51.44%  £121,076
    And the user should see the element                  jQuery = .message-alert:contains("The revised amount is higher")

New funding percentage is canceled if you select cancel
    [Documentation]  IFS-6695
    Given the user clicks the button/link                link = Cancel and return to finance overview
    And the user navigates to the page                   ${server}/project-setup-management/project/${projectId}/finance-check
    And the user should see the correct funding values   £116,596  £116,596
    When the user navigates to the page                  ${server}/project-setup-management/project/${projectId}/funding-level
    And the user should see the element                  jQuery = td:contains("Ward") ~ td:contains("£57,803") ~ td:contains("49.58%") ~ td ~ td:contains("£57,803") ~ td:contains("49.58%")
    Then the user should not see the element             jQuery = td:contains("Ward") ~ td:contains("£57,803") ~ td:contains("49.58%") ~ td ~ td:contains("£37,713") ~ td:contains("39.08%")

New funding percentage is applied on finance overview
    [Documentation]  IFS-6695  IFS-7128
    Given the user enters text to a text field      id = partners[${orgId}].fundingLevel  20
    And the user should not see the element         jQuery = .message-alert:contains("The revised amount is higher")
    When the user clicks the button/link            jQuery = button:contains("Save and return to project finances")
    Then the user should see the element            jQuery = th:contains("Ward Ltd") ~ td:contains("£200,903") ~ td:contains("20.00%") ~ td:contains("37,713") ~ td:contains("2,468") ~ td:contains("160,723")
    And the user should see the element             jQuery = h3:contains("Overview") + div table td:contains("23.96%")

New funding percentage is applied on finance checks
    [Documentation]  IFS-6695
    Given the user clicks the button/link                link = Finance checks
    Then the user should see the element                 jQuery = dt:contains("Total percentage grant") ~ dd:contains("23.96%")
    And the user should see the correct funding values   £116,596  £96,506

Approving any eligibility removes the link
    [Documentation]  IFS-6712
    [Setup]  the user clicks the button/link     css = a.eligibility-2
    Given the user approves project costs
    When the user clicks the button/link      link = Finance checks
    And the user clicks the button/link       link = View finances
    Then the user should not see the element  link = Change funding level percentages

New funding percentage does not show in the application
    [Documentation]  IFS-6695
    Given the user navigates to the page    ${server}/management/competition/${compId}/application/${applId}
    When the user clicks the button/link    jQuery = button:contains("Finances summary")
    Then the user should see the element    jQuery = th:contains("Ward") ~ td:contains("30.00%")

*** Keywords ***
Custom suite setup
    the user logs-in in new browser       &{internal_finance_credentials}
    Connect to Database    @{database}
    ${orgId} =  get organisation id by name  Ward Ltd
    Set Suite variable  ${orgId}

the user should see the correct funding values
    [Arguments]  ${applied}  ${current}
    the user should see the element   jQuery = dt:contains("Funding applied for") + dd:contains("${applied}")
    the user should see the element   jQuery = dt:contains("Current amount") + dd:contains("${current}")

the user should see the correct revised values
    [Arguments]  ${revisedfunding}  ${revisedpercentage}  ${revisedtotal}
    the user should see the element             jQuery = td:contains("Ward") ~ td:contains("£57,803") ~ td:contains("49.58%") ~ td ~ td:contains("${revisedfunding}") ~ td:contains("${revisedpercentage}")
    the user should see the element             jQuery = th:contains("Total grant value") ~ td:contains("£402,797") ~ td:contains("£116,596") ~ td:contains("${revisedtotal}")
    then the user should see the element        jQuery = dt:contains("Funding applied for") + dd:contains("£116,596") ~ dt:contains("Current amount") + dd:contains("${revisedtotal}")

