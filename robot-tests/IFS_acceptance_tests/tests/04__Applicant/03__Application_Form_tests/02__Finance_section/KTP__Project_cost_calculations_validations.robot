*** Settings ***
Documentation     IFS-7790 KTP: Your finances - Edit
Suite Setup       Custom Suite Setup
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot
Resource          ../../../../resources/common/Competition_Commons.robot
Resource          ../../../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}  	      KTP application
${KTPapplicationId}       ${application_ids["${KTPapplication}"]}
${KTPcompetiton}          KTP new competition
${KTPcompetitonId}        ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}                email=bob@knowledge.base    password=Passw0rd
${estateValue}            11000


*** Test Cases ***
Associate employment and development client side validation
    [Documentation]  IFS-7790
    Given expand the sections
    When the user fills in associate salary        ${EMPTY}  ${EMPTY}
    And the user enters text to a text field       jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  ${EMPTY}
    Then the user should see the element           jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})
    And the user should see the element            jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

Mark as complete with no associates is not allowed
    [Documentation]  IFS-7790
    Given the user clicks the button/link     css = label[for="stateAidAgreed"]
    When the user clicks the button/link      jQuery = button:contains("Mark as complete")
    Then the user should see the element      jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})
    And the user should see the element       jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

Entering duration in months autofills associate development
    [Documentation]  IFS-7790
    Given the user fills in associate salary      12  123
    Then the user should see the element          jQuery = table[id="associate-development-costs-table"] td:contains("12")
    And the user should not see the element       jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})

Calculation for associate employment and development
    [Documentation]  IFS-7790
     Given the user enters text to a text field      jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  123
     When the user should see the element            jQuery = span:contains("123") ~ button:contains("Associate development")
     Then the user should see the right values       123   Associate employment    246
     And the user should not see the element         jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

Knowledge base supervisor can only add two rows
    [Documentation]  IFS-7790
    Given the user clicks the button/link        jQuery = button:contains("Knowledge base supervisor")
    When the user clicks the button/link         css = button[value="KNOWLEDGE_BASE"]
    Then the user should see the element         css = button[value="KNOWLEDGE_BASE"].govuk-visually-hidden

Knowledge base supervisor validations
    [Documentation]  IFS-7790
    Given the user clicks the button/link        jQuery = table[id="knowledge-base-table"] button:contains("Remove"):last
    When the user enters text to a text field    css = table[id="knowledge-base-table"] input[id$="description"]  ${EMPTY}
    And the user enters text to a text field     css = table[id="knowledge-base-table"] input[id$="cost"]         ${EMPTY}
    Then the user should see the element         jQuery = table[id="knowledge-base-table"] td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})

Knowledge base supervisor calculations
    [Documentation]  IFS-7790
    Given the user enters text to a text field    css = table[id="knowledge-base-table"] input[id$="description"]  supervisor
    When the user enters text to a text field     css = table[id="knowledge-base-table"] input[id$="cost"]         123
    Then the user should see the right values     123    Knowledge base supervisor   369
    And the user should not see the element       jQuery = table[id="knowledge-base-table"] td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})

Estate validations
    [Documentation]  IFS-7790
    Given the user clicks the button/link                 jQuery = button:contains("Estates")
    When the user enters text to a text field             css = input[id^="estate"][id$="description"]  estate
    And The user enters text to a text field              css = input[id^="estate"][id$="cost"]  ${estateValue}
    When the user clicks the button/link                  jQuery = button:contains("Mark as complete")
    Then The user should see a field and summary error    ${estate_Error_Message}

Estate calculations
    [Documentation]  IFS-7790
    Given the user enters text to a text field    css = input[id^="estate"][id$="cost"]  1000
    Then the user should see the right values     1,000   Estates     1369

Additional associate support validations
   [Documentation]  IFS-7790
   Given the user clicks the button/link        jQuery = button:contains("Additional associate support")
   When the user enters text to a text field    css = input[id^="associateSupport"][id$="description"]  ${EMPTY}
   And The user enters text to a text field     css = input[id^="associateSupport"][id$="cost"]  ${EMPTY}
   Then the user should see the element         jQuery = span:contains(${empty_field_warning_message}) ~input[id^="associateSupport"][id$="cost"]
   And the user should see the element          jQuery = span:contains(${empty_field_warning_message}) ~input[id^="associateSupport"][id$="description"]

Additional associate support calculation
   [Documentation]  IFS-7790
   Given the user enters text to a text field     css = input[id^="associateSupport"][id$="description"]  associate support
   When The user enters text to a text field      css = input[id^="associateSupport"][id$="cost"]  1000
   Then the user should see the right values      1,000   Additional associate support     2369
   And the user should not see the element        jQuery = span:contains(${empty_field_warning_message}) ~input[id^="associateSupport"][id$="cost"]
   And the user should not see the element        jQuery = span:contains(${empty_field_warning_message}) ~input[id^="associateSupport"][id$="description"]

Subcontracting costs calculations
    [Documentation]  IFS-7790
    Given the user clicks the button/link                jQuery = button:contains("Subcontracting costs")
    When the user enters text to a text field            css = input[id^="subcontracting"][id$="cost"]        1000
    Then the user fills in the subcontracting values

Travel and subsistence calculations
    [Documentation]  IFS-7790
    Given the user clicks the button/link       jQuery = button:contains("Travel and subsistence")
    When the user enters text to a text field   css = input[id^="travelRows"][id$="item"]    Travel
    And the user enters text to a text field    css = input[id^="travelRows"][id$="times"]       2
    And the user enters text to a text field    css = input[id^="travelRows"][id$="eachCost"]    1000
    Then the user should see the right values   2,000    Travel and subsistence    5369

Other costs calculations
    [Documentation]  IFS-7790
    Given the user fills in ktp other costs     Other costs   1000
    Then the user should see the right values   1,000    Other costs    6369

Consumables calculations
    [Documentation]  IFS-7790
    Given the user fills in consumables
    Then the user should see the right values    2,000    Consumables    8369

Additional company cost estimation validations
    [Documentation]  IFS-7790
    Given the user clicks the button/link            jQuery = button:contains("Additional company cost estimation")
    When the user fills additional company costs     ${EMPTY}  ${EMPTY}
    Then the user should see the validation messages for addition company costs

Additional company cost estimation calculations
    [Documentation]  IFS-7790
    Given the user fills additional company costs       description  100
    Then the user should see the element                jQuery = h4:contains("Total additional company cost estimations"):contains("£500")

Mark as complete and check read only view
    [Documentation]  IFS-7790
    Given the user clicks the button/link    jQuery = button:contains("Mark as complete")
    When the user clicks the button/link     link = Your project costs
    Then the user should see the read only view of KTP

Finance overview values
    [Documentation]  IFS-7790
    Given the user navigates to the page     ${server}/application/${KTPapplicationId}
    And The user clicks the button/link      link = Finances overview
    Then the user should see the correct data in the finance tables

Internal user views values
    [Documentation]  IFS-7790
    Given log in as a different user       &{Comp_admin1_credentials}
    When the user navigates to the page    ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
    And The user clicks the button/link    jQuery = button:contains("Finances summary")
    Then the user should see the correct data in the finance tables

*** Keywords ***
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
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="associateSalary.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="managementSupervision.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="otherStaff.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="capitalEquipment.cost"]
    the user should see the element       jQuery = span:contains(${empty_field_warning_message}) ~ input[id$="otherCosts.cost"]

the user should see the read only view of KTP
    the user should see the element       jQuery = th:contains("Total associate employment costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total associate development costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total travel and subsistence") ~ td:contains("£2,000")
    the user should see the element       jQuery = th:contains("Total consumables costs") ~ td:contains("£2,000")
    the user should see the element       jQuery = th:contains("Total knowledge base supervisor costs") ~ td:contains("£123")
    the user should see the element       jQuery = th:contains("Total estates costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total additional associate support costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total subcontracting costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total other costs") ~ td:contains("£1,000")
    the user should see the element       jQuery = th:contains("Total additional company cost estimations") ~ td:contains("£500")

the user should see the correct data in the finance tables
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate salary costs") ~ th:contains("Associate development costs") ~ th:contains("Travel and subsistence")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Consumables") ~ th:contains("Knowledge base advisor") ~ th:contains("Estate")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate support costs") ~th:contains("Subcontracting") ~ th:contains("Other costs")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate salary costs") ~ th:contains("Associate development costs") ~ th:contains("Travel and subsistence")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Consumables") ~ th:contains("Knowledge base advisor") ~ th:contains("Estate")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate support costs") ~th:contains("Subcontracting") ~ th:contains("Other costs")
    the user should see the element       jQuery = td:contains("£8,369") ~ td:contains("123") ~ td:contains("123") ~ td:contains(2,000) ~ td:contains(2,000) ~ td:contains("123") ~ td:contains("1,000") ~ td:contains(1,000) ~ td:contains(1,000) ~ td:contains("1,000")

the user fills in consumables
    the user clicks the button/link          jQuery = button:contains("Consumables")
    the user enters text to a text field     css = input[id^="consumableCost"][id$="item"]  consumable
    the user enters text to a text field     css = input[id^="consumableCost"][id$="quantity"]       2
    the user enters text to a text field     css = input[id^="consumableCost"][id$="cost"]       1000

the user should see the right values
    [Arguments]   ${sectionTotal}    ${section}    ${total}
    the user should see the element          jQuery = div:contains("${sectionTotal}") button:contains("${section}")
    the user should see the element           jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="${total}"]

the user fills in ktp other costs
    [Arguments]   ${description}   ${estimate}
    the user clicks the button/link          jQuery = button:contains("Other costs")
    the user enters text to a text field     css = textarea[id^="otherRows"][id$="description"]    ${description}
    the user enters text to a text field     css = input[id^="otherRows"][id$="estimate"]       ${estimate}

the user fills in associate salary
    [Arguments]   ${duration}  ${cost}
    the user enters text to a text field    jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="duration"]  ${duration}
    the user enters text to a text field      jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  ${cost}

expand the sections
    the user clicks the button/link       jQuery = button:contains("Associate employment")
    the user clicks the button/link       jQuery = button:contains("Associate development")

the user fills in the subcontracting values
    the user enters text to a text field      css = input[id^="subcontracting"][id$="name"]        Subcontracting
    the user enters text to a text field      css = input[id^="subcontracting"][id$="country"]     UK
    the user enters text to a text field      css = textarea[id^="subcontracting"][id$="role"]        Awesome
    the user should see the right values      1,000   Subcontracting     3369