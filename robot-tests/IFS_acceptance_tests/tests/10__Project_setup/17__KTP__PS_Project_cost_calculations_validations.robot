*** Settings ***
Documentation     IFS-8328 KTP Project Setup - finance checks (internal and external)
...
Suite Setup       Custom Suite Setup
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}  	               KTP in panel application
${KTPapplicationId}                ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                   KTP in panel
${KTPcompetitonId}                 ${competition_ids["${KTPcompetiton}"]}
${ktpLeadOrgName}                  A base of knowledge
${ktpPartnerOrgName}               Ludlow
&{FinanceUser}                     email=lee.bowman@innovateuk.test    password=Passw0rd1357
&{CompAdmin}                       email=arden.pimenta@innovateuk.test    password=Passw0rd1357
&{ktpLead}                         email=bob@knowledge.base    password=Passw0rd1357
&{ktpPartner}                      email=jessica.doe@ludlow.co.uk    password=Passw0rd1357
${estateValue}                     11000
${associateSalaryTable}            associate-salary-costs-table
${associateDevelopmentTable}       associate-development-costs-table
${limitFieldValidationMessage}     You must provide justifications for exceeding allowable cost limits.

*** Test Cases ***
Internal user can edit the duration of the project
    [Documentation]  IFS-8328
    [Setup]  Requesting project ID of this Project
    Given Requesting KTP Organisation IDs
    And the user navigates to the page            ${server}/project-setup-management/competition/${KTPcompetitonId}/project/${ProjectID}/duration
    When the user enters text to a text field     id = durationInMonths    30
    And The user clicks the button/link           jQuery = button:contains("Save and return to project finances")
    Then The user should see the element          jQuery = dt:contains("Duration") ~ dd:contains("30 months")

Internal user can edit KTP finances in project setup
    [Documentation]  IFS-8328
    [Setup]  Requesting project ID of this Project
    Given Requesting KTP Organisation IDs
    And the user navigates to the page        ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${ktpLeadOrgID}/eligibility
    When The user clicks the button/link      jQuery = button:contains("Open all")
    Then The user edits the KTP costs section
    And The user should see the element

Internal user approves the Eligibility and Viability of the lead applicant and partner
    [Documentation]  IFS-8328
    When the the user approves project costs
    Then the user sees the text in the element     jQuery = p:contains("The partner's finance eligibility has been approved by ")
    And the user navigates to the page             ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${ktpPartnerOrgId}/eligibility
    And the user approves project costs

Internal user approves the Viability of the lead applicant and partner
    [Documentation]  IFS-8328
    When The user navigates to the page     ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${ktpLeadOrgID}/viability
    Then the user approves viability
    When The user navigates to the page     ${server}/project-setup-management/project/${ProjectID}/finance-check/organisation/${ktpPartnerOrgId}/viability
    Then the user approves viability




*** Keywords ***
Requesting project ID of this Project
    ${ProjectID} =  get project id by name     ${KTPapplication}
    Set suite variable     ${ProjectID}

Requesting KTP Organisation IDs
    ${ktpLeadOrgID} =  get organisation id by name     ${ktpLeadOrgName}
    Set suite variable      ${ktpLeadOrgID}
    ${ktpPartnerOrgId} =  get organisation id by name    ${ktpPartnerOrgName}
    Set suite variable      ${ktpPartnerOrgId}

The user edits the KTP costs section
    the user clicks the button/link          css = a[href="?financeType=ASSOCIATE_SALARY_COSTS"]
    the user enters text to a text field     jQuery = td:contains("Associate 1") ~ td input[id$="duration"]    20
    the user enters text to a text field     jQuery = td:contains("Associate 1") ~ td input[id$="cost"]    30
    the user enters text to a text field     jQuery = td:contains("Associate 2") ~ td input[id$="duration"]    15
    the user enters text to a text field     jQuery = td:contains("Associate 2") ~ td input[id$="cost"]    35
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=ASSOCIATE_DEVELOPMENT_COSTS"]
    the user enters text to a text field     jQuery = td:contains("Associate 1") ~ td input[id$="cost"]    40
    the user enters text to a text field     jQuery = td:contains("Associate 2") ~ td input[id$="cost"]    45
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=KTP_TRAVEL"]
    the user enters text to a text field     id = ktpTravelCostRows[8217].eachCost    50
    the user enters text to a text field     id = ktpTravelCostRows[8218].eachCost    55
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=CONSUMABLES"]
    the user enters text to a text field     id = consumableCostRows[8219].cost    60
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=KNOWLEDGE_BASE"]
    the user enters text to a text field     id = knowledgeBaseCostRows[8220].cost    70
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=ESTATE_COSTS"]
    the user enters text to a text field     id = estateCostRows[8221].cost    80
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=ASSOCIATE_SUPPORT"]
    the user enters text to a text field     id = associateSupportCostRows[8222].cost    90
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=OTHER_COSTS"]
    the user enters text to a text field     id = otherRows[8164].estimate    100
    the user clicks the button/link          css = button[name="save-eligibility"]

the user approves viability
    the user selects the checkbox        costs-reviewed
    the user selects the checkbox        project-viable
    Set Focus To Element                 link = Contact us
    the user selects the option from the drop-down menu  Green  id = rag-rating
    the user clicks the button/link      css = #confirm-button
    the user clicks the button/link      jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")

the user confirms the values in the finance table
    the user should see the element     jQuery = th:contains("Project duration")
    the user should see the element     jQuery = td:nth-child(1):contains("30 months")
    the user should see the element     jQuery = th:contains("Total costs")
    the user should see the element     jQuery = td:nth-child(2):contains("XXXXX")
    the user should see the element     jQuery = th:contains("Funding level (%)")
    the user should see the element     jQuery = td:nth-child(3):contains("30.00%")





the user enters T&S costs
    [Arguments]  ${typeOfCost}  ${rowNumber}  ${travelCostDescription}  ${numberOfTrips}  ${costOfEachTrip}
    the user selects the option from the drop-down menu     ${typeOfCost}  jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) select[name^="ktp"][name$="type"]
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) textarea[name^="ktp"][name$="description"]  ${travelCostDescription}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="times"]  ${numberOfTrips}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="eachCost"]  ${costOfEachTrip}

Custom suite setup
    the user logs-in in new browser   &{KTPLead}
    the user clicks the button/link   link = ${KTPapplication}
    the user clicks the button/link   link = Your project finances
    the user clicks the button/link   link = Your project costs

the user should see the validation messages for addition company costs
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="associateSalary.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="managementSupervision.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="otherStaff.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="capitalEquipment.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="otherCosts.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ textarea[id$="consumables.description"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="associateSalary.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="managementSupervision.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="otherStaff.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="capitalEquipment.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="consumables.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="otherCosts.cost"]

the user should see the read only view of KTP
    the user should see the element       jQuery = th:contains("Total associate employment costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total associate development costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total travel and subsistence costs") ~ td:contains("£6,150")
    the user should see the element       jQuery = th:contains("Total consumables costs") ~ td:contains("£2,000")
    the user should see the element       jQuery = th:contains("Total knowledge base supervisor costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total associates estates costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total additional associate support costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total other costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total additional company cost estimates") ~ td:contains("£600")

the user should see the correct data in the finance tables
    the user should see the element       jQuery = td:contains("Associate Employment") ~ td:contains("123")
    the user should see the element       jQuery = td:contains("Associate development") ~ td:contains("123")
    the user should see the element       jQuery = td:contains("Travel and subsistence") ~ td:contains("6,150")
    the user should see the element       jQuery = td:contains("Consumables") ~ td:contains("2,000")
    the user should see the element       jQuery = td:contains("Knowledge base supervisor") ~ td:contains("123")
    the user should see the element       jQuery = td:contains("Estate") ~ td:contains("1,000")
    the user should see the element       jQuery = td:contains("Additional associate support") ~ td:contains("1,000")
    the user should see the element       jQuery = td:contains("Other costs") ~ td:contains("1,000")
    the user should see the element       jQuery = th:contains("Total") ~ td:contains("£11,519")

the user fills in consumables
    the user enters text to a text field     css = input[id^="consumableCost"][id$="item"]  consumable
    the user enters text to a text field     css = input[id^="consumableCost"][id$="quantity"]       2
    the user enters text to a text field     css = input[id^="consumableCost"][id$="cost"]       1000

the user should see the right T&S cost summary and total values
    the user should see the element     jQuery = td:contains("Total knowledge base supervisor costs") ~ td:contains("£5,550")
    the user should see the element     jQuery = td:contains("Total associate travel costs") ~ td:contains("£600")
    the user should see the element     jQuery = h4:contains("Total travel and subsistence costs")
    the user should see the element     jQuery = span:contains("£6,150")

the user should see the right values
     [Arguments]   ${sectionTotal}    ${section}    ${total}
    the user should see the element     jQuery = div:contains("${sectionTotal}") button:contains("${section}")
    the user should see the element     jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="${total}"]

the user fills in ktp other costs
    [Arguments]   ${description}   ${estimate}
    the user enters text to a text field     css = textarea[id^="otherRows"][id$="description"]    ${description}
    the user enters text to a text field     css = input[id^="otherRows"][id$="estimate"]       ${estimate}

the user fills in associate salary
    [Arguments]   ${duration}  ${cost}
    the user enters text to a text field    jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="duration"]  ${duration}
    the user enters text to a text field      jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${cost}

expand the sections
    the user clicks the button/link       jQuery = button:contains("Associate employment")
    the user clicks the button/link       jQuery = button:contains("Associate development")

subcontracting fields should not display
    the user should not see the element     css = input[id^="subcontracting"][id$="cost"]
    the user should not see the element     css = input[id^="subcontracting"][id$="name"]
    the user should not see the element     css = input[id^="subcontracting"][id$="country"]
    the user should not see the element     css = textarea[id^="subcontracting"][id$="role"]