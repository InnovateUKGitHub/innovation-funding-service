*** Settings ***
Documentation    Suite description
Suite Setup       Custom Suite Setup
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot
Resource          ../../../../resources/common/Competition_Commons.robot
Resource          ../../../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}  	 KTP application
${KTPapplicationId}  ${application_ids["${KTPapplication}"]}
${KTPcompetiton}     KTP new competition
${KTPcompetitonId}   ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}           email=bob@knowledge.base    password=Passw0rd

*** Test Cases ***
Associate employment and development client side validation
        And the user clicks the button/link       jQuery = button:contains("Associate employment")
        And the user clicks the button/link       jQuery = button:contains("Associate development")
    Given the user enters text to a text field     jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="duration"]  ${EMPTY}
    When the user enters text to a text field      jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  ${EMPTY}
    And the user enters text to a text field       jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  ${EMPTY}
    Then the user should see the element           jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank") ~ td:contains("This field cannot be left blank")
    And the user should see the element            jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank")

Mark as complete with no associates is not allowed
    Given the user clicks the button/link     css = label[for="stateAidAgreed"]
    When the user clicks the button/link      jQuery = button:contains("Mark as complete")
    Then the user should see the element      jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank") ~ td:contains("This field cannot be left blank")
    And the user should see the element       jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank")

Entering duration in months autofills associate development
    Given the user enters text to a text field    jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="duration"]  12
    And the user enters text to a text field      jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  123
    Then the user should see the element          jQuery = table[id="associate-development-costs-table"] td:contains("12")
    And the user should not see the element       jQuery = table[id="associate-salary-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank") ~ td:contains("This field cannot be left blank")

Calculation for associate employment and development
     Given the user enters text to a text field  jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td input[id$="cost"]  123
     Then the user should see the element        jQuery = span:contains("123") ~ button:contains("Associate development")
     And the user should see the element         jQuery = span:contains("123") ~ button:contains("Associate employment")
     And the user should see the element         jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="246"]
     And the user should not see the element     jQuery = table[id="associate-development-costs-table"] td:contains("Associate 1") ~ td:contains("This field cannot be left blank")

Knowledge base supervisor can only add two rows
    Given the user clicks the button/link        jQuery = button:contains("Knowledge base supervisor")
    Given the user clicks the button/link        css = button[value="KNOWLEDGE_BASE"]
    Then the user should see the element         css = button[value="KNOWLEDGE_BASE"].govuk-visually-hidden

Knowledge base supervisor validations
    Given the user clicks the button/link        jQuery = table[id="knowledge-base-table"] button:contains("Remove"):last
    When the user enters text to a text field    css = table[id="knowledge-base-table"] input[id$="description"]  ${EMPTY}
    And the user enters text to a text field     css = table[id="knowledge-base-table"] input[id$="cost"]         ${EMPTY}
    Then the user should see the element         jQuery = table[id="knowledge-base-table"] td:contains("This field cannot be left blank") ~ td:contains("This field cannot be left blank")

Knowledge base supervisor calculations
    Given the user enters text to a text field    css = table[id="knowledge-base-table"] input[id$="description"]  supervisor
    When the user enters text to a text field     css = table[id="knowledge-base-table"] input[id$="cost"]         123
    Then the user should see the element          jQuery = div:contains("123") button:contains("Knowledge base supervisor")
    And the user should see the element           jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="369"]
    And the user should not see the element       jQuery = table[id="knowledge-base-table"] td:contains("This field cannot be left blank") ~ td:contains("This field cannot be left blank")

Estate validations
    Given the user clicks the button/link        jQuery = button:contains("Estates")
    Given the user enters text to a text field   css = input[id^="estate"][id$="description"]  estate
    When The user enters text to a text field    css = input[id^="estate"][id$="cost"]  11000
    When the user clicks the button/link         jQuery = button:contains("Mark as complete")
    Then The user should see a field and summary error    You should enter less than £10,000 for estate costs

Estate calculations
    Given the user enters text to a text field    css = input[id^="estate"][id$="cost"]  1000
    Then the user should see the element          jQuery = div:contains("1,000") button:contains("Estates")
    And the user should see the element           jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="1369"]

Additional associate support validations
   Given the user clicks the button/link        jQuery = button:contains("Additional associate support")
   Given the user enters text to a text field   css = input[id^="associateSupport"][id$="description"]  ${EMPTY}
   When The user enters text to a text field    css = input[id^="associateSupport"][id$="cost"]  ${EMPTY}
   Then the user should see the element         jQuery = span:contains("This field cannot be left blank") ~input[id^="associateSupport"][id$="cost"]
   And the user should see the element          jQuery = span:contains("This field cannot be left blank") ~input[id^="associateSupport"][id$="description"]

Additional associate support calculation
   Given the user enters text to a text field   css = input[id^="associateSupport"][id$="description"]  associate support
   When The user enters text to a text field    css = input[id^="associateSupport"][id$="cost"]  1000
   Then the user should see the element         jQuery = div:contains("1,000") button:contains("Additional associate support")
   And the user should see the element           jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="2369"]
   Then the user should not see the element         jQuery = span:contains("This field cannot be left blank") ~input[id^="associateSupport"][id$="cost"]
   And the user should not see the element          jQuery = span:contains("This field cannot be left blank") ~input[id^="associateSupport"][id$="description"]

Subcontracting costs calculations
    [Documentation]    INFUND-844
    Given the user clicks the button/link       jQuery = button:contains("Subcontracting costs")
    When the user enters text to a text field   css = input[id^="subcontracting"][id$="cost"]        1000
    And the user enters text to a text field    css = input[id^="subcontracting"][id$="name"]        Subcontracting
    And the user enters text to a text field    css = input[id^="subcontracting"][id$="country"]     UK
    And the user enters text to a text field    css = textarea[id^="subcontracting"][id$="role"]        Awesome
    Then the user should see the element        jQuery = div:contains("1,000") button:contains("Additional associate support")
    And the user should see the element         jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="3369"]

Travel and subsistence calculations
    Given the user clicks the button/link       jQuery = button:contains("Travel and subsistence")
    When the user enters text to a text field   css = input[id^="travelRows"][id$="item"]    Travel
    And the user enters text to a text field    css = input[id^="travelRows"][id$="times"]       2
    And the user enters text to a text field    css = input[id^="travelRows"][id$="eachCost"]    1000
    Then the user should see the element        jQuery = div:contains("2,000") button:contains("Travel and subsistence")
    And the user should see the element         jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="5369"]

Other costs calculations
    Given the user clicks the button/link       jQuery = button:contains("Other costs")
    When the user enters text to a text field   css = textarea[id^="otherRows"][id$="description"]    Other costs
    And the user enters text to a text field    css = input[id^="otherRows"][id$="estimate"]       1000
    Then the user should see the element        jQuery = div:contains("1,000") button:contains("Other costs")
    And the user should see the element         jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="6369"]

Consumables calculations
    Given the user clicks the button/link       jQuery = button:contains("Consumables")
    Given the user enters text to a text field    css = input[id^="consumableCost"][id$="item"]  consumable
    When the user enters text to a text field     css = input[id^="consumableCost"][id$="quantity"]       2
        When the user enters text to a text field     css = input[id^="consumableCost"][id$="cost"]       1000
    Then the user should see the element          jQuery = div:contains("2,000") button:contains("Consumables")
    And the user should see the element           jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="8369"]

Additional company cost estimation validations
   Given The user clicks the button/link           jQuery = button:contains("Additional company cost estimation")
   Given the user fills additional company costs   ${EMPTY}  ${EMPTY}
   Then the user should see the validation messages for addition company costs

Additional company cost estimation calculations
   Given the user fills additional company costs    description  100
   Then the user should see the element                jQuery = h4:contains("Total additional company cost estimations"):contains("£500")

Mark as complete and check read only view
   Given the user clicks the button/link    jQuery = button:contains("Mark as complete")
   When the user clicks the button/link     link = Your project costs
   Then the user should see the read only view of KTP

Finance overview values
   Given the user navigates to the page     ${server}/application/${KTPapplicationId}
   And The user clicks the button/link      link = Finances overview
   Then the user should see the correct data in the finance tables

Internal user views values
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
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ textarea[id$="associateSalary.description"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ textarea[id$="managementSupervision.description"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ textarea[id$="otherStaff.description"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ textarea[id$="capitalEquipment.description"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ textarea[id$="otherCosts.description"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ input[id$="associateSalary.cost"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ input[id$="managementSupervision.cost"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ input[id$="otherStaff.cost"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ input[id$="capitalEquipment.cost"]
    the user should see the element       jQuery = span:contains("This field cannot be left blank") ~ input[id$="otherCosts.cost"]

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
    the user should see the element       jQuery = h4:contains("Total additional company cost estimations"):contains("£500")

the user should see the correct data in the finance tables
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate salary costs") ~ th:contains("Associate development costs") ~ th:contains("Travel and subsistence")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Consumables") ~ th:contains("Knowledge base advisor") ~ th:contains("Estate")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate support costs") ~th:contains("Subcontracting") ~ th:contains("Other costs")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate salary costs") ~ th:contains("Associate development costs") ~ th:contains("Travel and subsistence")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Consumables") ~ th:contains("Knowledge base advisor") ~ th:contains("Estate")
    the user should see the element       jQuery = th:contains("Total") ~ th:contains("Associate support costs") ~th:contains("Subcontracting") ~ th:contains("Other costs")
    the user should see the element       jQuery = td:contains("£8,369") ~ td:contains("123") ~ td:contains("123") ~ td:contains(2,000) ~ td:contains(2,000) ~ td:contains("123") ~ td:contains("1,000") ~ td:contains(1,000) ~ td:contains(1,000) ~ td:contains("1,000")






