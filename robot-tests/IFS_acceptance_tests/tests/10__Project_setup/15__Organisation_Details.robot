*** Settings ***
Documentation     IFS-6697 Project setup internal: View further organisation details
...
...               IFS-6923 Project setup internal: Edit organisation size details
Suite Setup       The user logs-in in new browser  &{ifs_admin_user_credentials}
Suite Teardown    The user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot
Resource          ../04__Applicant/Applicant_Commons.robot
*** Variables ***
${GrowthTableCompetitionLink}            ${server}/project-setup-management/competition/${GrowthTableCompId}/project/${PS_GTC_Application_Project_Id}/details
${GrowthTableCompetitionOrgSelectLink}   ${server}/project-setup-management/competition/${GrowthTableCompId}/project/${PS_GTC_Application_Project_Id}/organisation/select
${GrowthTableApplicationLink}            ${server}/management/competition/${GrowthTableCompId}/application/${PS_GTC_Application_No}
${NoGrowthTableCompetitionLink}          ${server}/project-setup-management/competition/${NoGrowthTableCompId}/project/${PS_NGTC_Application_Project_Id}/organisation/select
${NoGrowthTableApplicationLink}          ${server}/management/competition/${NoGrowthTableCompId}/application/${PS_NGTC_Application_No}
${WardLtdRadioBttnValue}                 117
${RedPlanetRadioBttnValue}               119
${SmithZoneRadioBttnValue}               118

*** Test Cases ***
IFS Admin able to view further Organisation details by selecting an organisation
    [Documentation]  IFS-6697
    Given the user navigates to the page                                       ${GrowthTableCompetitionOrgSelectLink}
    When the user selects an organisation                                      ${SmithZoneRadioBttnValue}
    Then the user should see further organisation details                      Business  SmithZone  89082442
    And the user should see Organisation size details with a growth table      Micro or small  1  2020  100000  200000  300000  400000  60

User is able to cancel edit organisation size
    [Documentation]  IFS-6923
    [Setup]  the user clicks the button/link    jQuery = a:contains("Edit organisation")
    Given the user enters text to a text field  id = financialYearEndYearValue   1993
    when the user clicks the button/link        jQUery = a:contains("Cancel and return to")
    Then the user should see the element        jQuery = input[id$="financialYearEndYearValue"][value = "2020"]

Edit organisation size page with growth table validations
     [Documentation]  IFS-6923
     Given the user clicks the button/link                                          jQuery = a:contains("Edit organisation")
     When the user clears organisation size details with a growth table
     Then the user sees organisation size with growth table validation errors
     [Teardown]  the user clicks the button/link                                    jQUery = a:contains("Cancel and return to")

IFS Admin is able to edit organisation size with a growth table
    [Documentation]  IFS-6923
    Given the user clicks the button/link                                                      jQuery = a:contains("Edit organisation")
    When the user updates organisation size details with a growth table                        LARGE  12  2019  600000  500000  400000  300000  200
    Then the user should see Organisation size details with a growth table                     Large  12  2019  600000  500000  400000  300000  200
    And organisation size details are still the same in application with a growth table        ${GrowthTableApplicationLink}  SmithZone  Micro or small  1  2020  100000  200000  300000  400000  60

Finance checks can't be approved if the updated funding level exceeds the permitted level for the organisation's size
    [Documentation]  IFS-6923
    [Setup]  the user navigates to the page             ${server}/project-setup-management/project/${PS_GTC_Application_Project_Id}/finance-check
    Given the user clicks the button/link               jQuery = a:contains("change funding level percentage")
    When the user should see a field and summary error  Funding level must be 25% or lower.
    Then the user isnt able to approve any finance checks

IFS Admin able to view further Organisation details without a growth table
    [Documentation]  IFS-6697
    Given the user navigates to the page                    ${NoGrowthTableCompetitionLink}
    Then the user should see further organisation details   Business  Ward Ltd  55522234
    And the user should see organisation size details       Micro or small  £700,000  50

IFS Admin able to edit further Organisation details without a growth table
    [Documentation]  IFS-6923
    Given the user clicks the button/link                                    jQuery = a:contains("Edit organisation")
    When the user updates organisation size details without a growth table   LARGE  9000000  300
    Then the user should see organisation size details                       Large  £9,000,000  300
    And organisation size details are still the same in application          ${NoGrowthTableApplicationLink}  Ward  Micro or small  £700,000  50

Finance user is able to view further Organisation details by selecting an organisation
    [Documentation]  IFS-6697
    [Setup]  log in as a different user                                        &{internal_finance_credentials}
    Given the user navigates to the page                                       ${GrowthTableCompetitionOrgSelectLink}
    When the user selects an organisation                                      ${SmithZoneRadioBttnValue}
    Then the user should see further organisation details                      Business  SmithZone  89082442
    And the user should see Organisation size details with a growth table      Large  12  2019  600000  500000  400000  300000  200

Finance user is able to edit organisation size with a growth table
    Given the user clicks the button/link                                                      jQuery = a:contains("Edit organisation")
    When the user updates organisation size details with a growth table                        SMALL  7  2018  400000  300000  200000  100000  20
    Then the user should see Organisation size details with a growth table                     Micro or small  7  2018  400000  300000  200000  100000  20
    And organisation size details are still the same in application with a growth table        ${GrowthTableApplicationLink}  SmithZone  Micro or small  1  2020  100000  200000  300000  400000  60

Finance user is able to view further Organisation details without a growth table
    [Documentation]  IFS-6697
    Given the user navigates to the page                    ${NoGrowthTableCompetitionLink}
    Then the user should see further organisation details   Business  Ward Ltd  55522234
    And the user should see organisation size details       Large  £9,000,000  300

Finance user is able to edit further Organisation details without a growth table
    [Documentation]  IFS-6923
    Given the user clicks the button/link                                    jQuery = a:contains("Edit organisation")
    When the user updates organisation size details without a growth table   SMALL  800000  200
    Then the user should see organisation size details                       Micro or small  £800,000  200
    And organisation size details are still the same in application          ${NoGrowthTableApplicationLink}  Ward  Micro or small  £700,000  50

Support user is able to view further Organisation details by selecting an organisation and isnt able to edit
    [Documentation]  IFS-6697  IFS-6923
    [Setup]  log in as a different user                          &{support_user_credentials}
    Given the user navigates to the page                         ${NoGrowthTableCompetitionLink}
    When the user should see further organisation details        Business  Ward Ltd  55522234
    And the user should see organisation size details            Micro or small  £800,000  200
    Then the user should not see the element                     jQuery = a:contains("Edit organisation")

Comp Admin user is able to view further Organisation details by selecting an organisation and isnt able to edit
    [Documentation]  IFS-6697  IFS-6923
    [Setup]  log in as a different user                          &{Comp_admin1_credentials}
    Given the user navigates to the page                         ${NoGrowthTableCompetitionLink}
    When the user should see further organisation details        Business  Ward Ltd  55522234
    And the user should see organisation size details            Micro or small  £800,000  200
    Then the user should not see the element                     jQuery = a:contains("Edit organisation")

Innovation Lead user is able to view further Organisation details by selecting an organisation and isnt able to edit
    [Documentation]  IFS-6697  IFS-6923
    [Setup]  log in as a different user                          &{innovation_lead_one}
    Given the user navigates to the page                         ${NoGrowthTableCompetitionLink}
    When the user should see further organisation details        Business  Ward Ltd  55522234
    And the user should see organisation size details            Micro or small  £800,000  200
    Then the user should not see the element                     jQuery = a:contains("Edit organisation")

Stakeholder user is able to view further Organisation details by selecting an organisation and isnt able to edit
    [Documentation]  IFS-6697  IFS-6923
    [Setup]  log in as a different user                                              &{stakeholder_user}
    Given the user navigates to the page                                             ${server}/project-setup-management/competition/${PS_Competition_Id}/project/${Grade_Crossing_Project_Id}/organisation/${Vitruvius_Id}/details/with-growth-table
    When the user should see further organisation details                            Business  Vitruvius Stonework Limited  60674010
    And the user should see Organisation size details with a growth table            Micro or small  1  2020  100000  200000  300000  400000  60
    Then the user should not see the element                                         jQuery = a:contains("Edit organisation")

If any of the finance sections are completed the user is no longer able to edit project size
     [Documentation]  IFS-6923
     [Setup]  log in as a different user                       &{internal_finance_credentials}
     Given the user navigates to the page                      ${server}/project-setup-management/project/${PS_GTC_Application_Project_Id}/finance-check/organisation/${organisationSmithId}/viability
     When the user completes viability selection
     And the user clicks the button/link                       jQuery = button:contains("Confirm viability")
     And the user clicks the button/link                       jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")
     Then the user is no longer able to edit organisation size

*** Keywords ***
The user selects an organisation
    [Arguments]  ${value}
    the user selects the radio button  organisationId  ${value}
    the user clicks the button/link    jQuery = button:contains("View partner details")

The user should see further organisation details
    [Arguments]  ${OrgType}  ${RegName}  ${RegNumber}
    the user should see the element   jQuery = h2:contains("Organisation details")
    the user should see the element   jQuery = h3:contains("Organisation type") + p:contains("${OrgType}")
    the user should see the element   jQuery = h3:contains("Registered name") + p:contains("${RegName}")
    the user should see the element   jQuery = h3:contains("Registration number") + p:contains("${RegNumber}")

The user should see Organisation size details
    [Arguments]  ${OrgSize}  ${Turnover}  ${Employees}
    the user should see the element    jQuery = dt:contains("Size") + dd:contains("${OrgSize}")
    the user should see the element    jQuery = dt:contains("Turnover") + dd:contains("${Turnover}")
    the user should see the element    jQuery = dt:contains(Full time employees) + dd:contains(${Employees})

The user should see Organisation size details with a growth table
    [Arguments]  ${OrgSize}  ${Month}  ${Year}  ${AnnualTurnover}  ${AnnualProfits}  ${AnnualExport}  ${ReasearchDevelopmentSpend}  ${Employees}
    the user should see the element    jQuery = h2:contains("Organisation size")
    the user should see the element    jQuery = dt:contains(Size) + dd:contains("${OrgSize}")
    the user should see the element    jQuery = input[id$="financialYearEndMonthValue"][value = "${Month}"]
    the user should see the element    jQuery = input[id$="financialYearEndYearValue"][value = "${Year}"]
    the user should see the element    jQuery = input[id$="annualTurnoverAtLastFinancialYear"][value = "${AnnualTurnover}"]
    the user should see the element    jQuery = input[id$="annualProfitsAtLastFinancialYear"][value = "${AnnualProfits}"]
    the user should see the element    jQuery = input[id$="annualExportAtLastFinancialYear"][value = "${AnnualExport}"]
    the user should see the element    jQuery = input[id$="researchAndDevelopmentSpendAtLastFinancialYear"][value = "${ReasearchDevelopmentSpend}"]
    the user should see the element    jQuery = dt:contains(Full time employees) + dd:contains(${Employees})

The user updates organisation size details with a growth table
    [Arguments]  ${Size}  ${Month}  ${Year}  ${AnnualTurnover}  ${AnnualProfits}  ${AnnualExport}  ${ReasearchDevelopmentSpend}  ${Employees}
    the user selects the radio button     organisationSize  ${Size}
    the user enters text to a text field  id = financialYearEndMonthValue  ${Month}
    the user enters text to a text field  id = financialYearEndYearValue   ${Year}
    the user enters text to a text field  id = annualTurnoverAtLastFinancialYear  ${AnnualTurnover}
    the user enters text to a text field  id = annualProfitsAtLastFinancialYear  ${AnnualProfits}
    the user enters text to a text field  id = annualExportAtLastFinancialYear  ${AnnualExport}
    the user enters text to a text field  id = researchAndDevelopmentSpendAtLastFinancialYear  ${ReasearchDevelopmentSpend}
    the user enters text to a text field  id = headCountAtLastFinancialYear   ${Employees}
    the user saves and returns to organisation details page

The user saves and returns to organisation details page
    the user clicks the button/link     jQuery = button:contains("Save and return to")
    the user clicks the button/link     jQuery = button:contains("Update organisation size")

Organisation size details are still the same in application with a growth table
    [Arguments]  ${link}  ${OrgName}  ${OrgSize}  ${Month}  ${Year}  ${AnnualTurnover}  ${AnnualProfits}  ${AnnualExport}  ${ReasearchDevelopmentSpend}  ${Employees}
    the user navigates to your organisation page on the application    ${link}  ${OrgName}
    The user should see Organisation size details with a growth table  ${OrgSize}  ${Month}  ${Year}  ${AnnualTurnover}  ${AnnualProfits}  ${AnnualExport}  ${ReasearchDevelopmentSpend}  ${Employees}

Organisation size details are still the same in application
    [Arguments]  ${link}  ${OrgName}  ${OrgSize}  ${Turnover}  ${Employees}
    the user navigates to your organisation page on the application     ${link}  ${OrgName}
    the user should see organisation size details   ${OrgSize}  ${Turnover}  ${Employees}

the user navigates to your organisation page on the application
    [Arguments]  ${link}  ${OrgName}
    the user navigates to the page    ${link}
    the user expands the section      Finances summary
    the user clicks the button/link   jQuery = div:contains("${OrgName}") ++ a:contains("View finances")
    the user clicks the button/link   jQuery = a:contains("Your organisation")

The user clears organisation size details with a growth table
    Clear Element Text  id = financialYearEndMonthValue
    Clear Element Text  id = financialYearEndYearValue
    Clear Element Text  id = annualTurnoverAtLastFinancialYear
    Clear Element Text  id = annualProfitsAtLastFinancialYear
    Clear Element Text  id = annualExportAtLastFinancialYear
    Clear Element Text  id = researchAndDevelopmentSpendAtLastFinancialYear
    Clear Element Text  id = headCountAtLastFinancialYear
    the user saves and returns to organisation details page

The user sees organisation size with growth table validation errors
    the user should see a field and summary error  Please enter a valid date.
    the user should see a field and summary error  This field cannot be left blank.
    the user should see a field and summary error  This field cannot be left blank.
    the user should see a field and summary error  This field cannot be left blank.
    the user should see a field and summary error  This field cannot be left blank.
    the user should see a field and summary error  This field cannot be left blank.

The user isnt able to approve any finance checks
    the user navigates to the page                        ${server}/project-setup-management/project/${PS_GTC_Application_Project_Id}/finance-check/organisation/${organisationSmithId}/viability
    the user completes viability selection
    the user should see the element                       jQuery = .govuk-button:contains("Confirm viability")[disabled]
    the user navigates to the page                        ${server}/project-setup-management/project/${PS_GTC_Application_Project_Id}/finance-check/organisation/${organisationSmithId}/eligibility
    the user completes elegibility selection
    the user should see the element                       jQuery = .govuk-button:contains("Approve eligible costs")[disabled]

The user completes viability selection
    the user selects the checkbox                         costs-reviewed
    the user selects the checkbox                         project-viable
    the user selects the option from the drop-down menu   Green  id = rag-rating

The user completes elegibility selection
    the user selects the checkbox    project-eligible
    the user selects the option from the drop-down menu   Green  id = rag-rating

The user updates organisation size details without a growth table
    [Arguments]  ${Size}  ${Turnover}  ${Employees}
    the user selects the radio button     organisationSize  ${Size}
    the user enters text to a text field  id = turnover   ${Turnover}
    the user enters text to a text field  id = headCount   ${Employees}
    the user saves and returns to organisation details page

The user is no longer able to edit organisation size
    the user navigates to the page               ${GrowthTableCompetitionOrgSelectLink}
    the user selects an organisation             ${SmithZoneRadioBttnValue}
    the user should not see the element          jQuery = a:contains("Edit organisation")

