*** Settings ***
Documentation     IFS-7790 KTP: Your finances - Edit
...
...               IFS-7959 KTP Your Project Finances - Links for Detailed Finances
...
...               IFS-8156 KTP Project costs - T&S
...
...               IFS-8154 KTP Project Costs - consumables
...
...               IFS-8157 KTP Project costs - Subcontracting costs
...
...               IFS-8158 KTP project costs justification
...
...               IFS-9242 KTP fEC/Non-fEC: Non-fEC project costs tables
...
...               IFS-9239 KTP fEC/Non-fEC: Your fEC model
...
...               IFS-9243 KTP fEC/Non-fEC: academic and secretarial support cost category
...
...               IFS-9244 KTP fEC/Non-fEC: indirect costs cost category
...
...               IFS-9246 KTP fEC/Non-fEC: application changes for read-only viewers
...
Suite Setup       Custom Suite Setup
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot
Resource          ../../../../resources/common/Competition_Commons.robot
Resource          ../../../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}  	                      KTP application
${KTPapplicationId}                       ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                          KTP new competition
${KTPcompetitonId}                        ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}                                email=bob@knowledge.base    password=${short_password}
${ktp_KTA_email}                          hermen.mermen@ktn-uk.test
${KTA_invitation_email_subject}           Invitation to be Knowledge Transfer Adviser
${invited_email_pattern}                  You have been invited to be the knowledge transfer adviser for the Innovation Funding Service application:
${estateValue}                            11000
${associateSalaryTable}                   associate-salary-costs-table
${associateDevelopmentTable}              associate-development-costs-table
${limitFieldValidationMessage}            You must provide justifications for exceeding allowable cost limits.
${academic_secretarial_support_table}     academic-secretarial-costs-table
${academicSecretarialCost}                academic-secretarial-costs

*** Test Cases ***
New lead applicant can make a 'No' selection for the organisation's fEC model and save the selection
    [Documentation]  IFS-9239
    Given the user selects the radio button          fecModelEnabled  fecModelEnabled-no
    And the user clicks the button/link              link = Back to your project finances
    And the user sees the selection is not saved
    When the user selects the radio button           fecModelEnabled  fecModelEnabled-no
    And the user clicks the button/link              jQuery = button:contains("Save and return to project finances")
    Then the user sees the selection is saved

New lead applicant can mark Your fEC model section as complete if 'No' is selected
    [Documentation]  IFS-9239
    When the user clicks the button/link     jQuery = button:contains("Mark as complete")
    Then the user should see the element     jQuery = li:contains("Your fEC model") span:contains("Complete")
    [Teardown]   the user completes your funding section

Knowledge based applicant cannot view or edit fEC specific project costs based on non-fEC selection
    [Documentation]  IFS-9242
    Given the user clicks the button/link        link = Your project costs
    When the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    And the user should not see the element      jQuery = button:contains("Additional associate support")
    Then the user should not see the element     jQuery = button:contains("Associates estates costs")

Associate employment and development client side validation
    [Documentation]  IFS-7790
    Given expand the sections
    When the user fills in associate salary        ${EMPTY}  ${EMPTY}
    And the user enters text to a text field       jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${EMPTY}
    Then the user should see the element           jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})
    And the user should see the element            jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

Mark as complete with no associates is not allowed
    [Documentation]  IFS-7790
    Given the user clicks the button/link     css = label[for="stateAidAgreed"]
    When the user clicks the button/link      jQuery = button:contains("Mark as complete")
    Then the user should see the element      jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})
    And the user should see the element       jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

Entering duration in months autofills associate development
    [Documentation]  IFS-7790
    Given the user clicks the button/link         jQuery = button:contains("Open all")
    When the user fills in associate salary       1  100
    Then the user should see the element          jQuery = table[id="${associateDevelopmentTable}"] td:contains("1")
    And the user should not see the element       jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message}) ~ td:contains(${empty_field_warning_message})

Calculation for associate employment and development
    [Documentation]  IFS-7790
     Given the user enters text to a text field     jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  100
     When the user should see the element           jQuery = span:contains("100") ~ button:contains("Associate development")
     Then the user should see the right values      100   Associate employment    246
     And the user should not see the element        jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

KB applicant can provide an academic and secretarial support cost in a non-fEC project cost table
    [Documentation]  IFS-9243
    Given the user collapses and expands the academic and secretarial support section
    When the user enters text to a text field     id = academicAndSecretarialSupportForm  100
    Then the user should see the element          jQuery = span:contains("100") ~ button:contains("Academic and secretarial support")
    And the user should see the element           jQuery = h4:contains("Total academic and secretarial support costs") span:contains("100")

Calculate indirect cost
    [Documentation]  IFS-9244
    Given the user should see the element     jQuery = p:contains("The calculation is 46% of the sum of 2 grant amounts:'Associate employment costs' and the 'Academic and secretarial support costs'.")
    Then the user should see the element      jQuery = h4:contains("Total indirect costs"):contains("92")

Subcontracting costs should not display in project costs
    [Documentation]  IFS-8157
    Then subcontracting fields should not display

Travel and subsistence cost calculations
    [Documentation]  IFS-7790  IFS-8156
    When the user enters T&S costs                                           Supervisor  1  Knowledge Base biweekly travel  30  185
    And the user clicks the button/link                                      name = add_row
    And the user enters T&S costs                                            Associate  2  3 trips to Glasgow  3  200
    Then the user should see the right T&S cost summary and total values

Other costs calculations
    [Documentation]  IFS-7790
    Given the user fills in ktp other costs     Other costs   1000
    Then the user should see the right values   1,000    Other costs    7542

Consumables calculations
    [Documentation]  IFS-7790
    Given the user fills in consumables
    Then the user should see the right values    2,000    Consumables    9542

Additional company cost estimation validations
    [Documentation]  IFS-7790  IFS-8154
    Given the user clicks the button/link            id = accordion-finances-heading-additional-company-costs
    When the user fills additional company costs     ${EMPTY}  ${EMPTY}
    Then the user should see the validation messages for addition company costs

Additional company cost estimation calculations
    [Documentation]  IFS-7790  IFS-8154
    Given the user fills additional company costs       description  100
    Then the user should see the element                jQuery = h4:contains("Total additional company cost estimates"):contains("£600")

Limit justification validation
    [Documentation]  IFS-8158
    Given the user clicks the button/link                 exceed-limit-yes
    Then the user clicks the button/link                  jQuery = button:contains("Mark as complete")
    And the user should see a field and summary error     ${limitFieldValidationMessage}
    And Input Text                                        css = .textarea-wrapped .editor  This is some random text

Mark as complete and check read only view
    [Documentation]  IFS-7790  IFS-8154
    Given the user clicks the button/link    jQuery = button:contains("Mark as complete")
    When the user clicks the button/link     link = Your project costs
    Then the user should see the read only view of KTP

Finance overview values
    [Documentation]  IFS-7790 IFS-7959
    Given the user navigates to the page     ${server}/application/${KTPapplicationId}
    And The user clicks the button/link      link = Finances overview
    Then the user should see the correct data in the finance tables

Internal user views values
    [Documentation]  IFS-7790 IFS-7959
    Given log in as a different user       &{Comp_admin1_credentials}
    When the user navigates to the page    ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
    And The user clicks the button/link    jQuery = button:contains("Finances summary")
    Then the user should see the correct data in the finance tables

Customer support user can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    Given log in as a different user                                    &{support_user_credentials}
    When the user navigates to the page                                 ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
    Then the user should see read only view for non-fec declaration

IFS admin can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    Given log in as a different user                                    &{ifs_admin_user_credentials}
    When the user navigates to the page                                 ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
    Then the user should see read only view for non-fec declaration

Business user can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    Given log in as a different user                                    &{collaborator1_credentials}
    When the user clicks the button/link                                link = ${KTPapplication}
    And the user clicks the button/link                                 link = Finances overview
    Then the user should see read only view for non-fec declaration

KTA user assigned to application can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    [Setup]  knowledge based applicant invites KTA user to the application
    Given the user clicks the button/link                               jQuery = a:contains("Applications")
    When the user clicks the button/link                                link = ${KTPapplication}
    And the user clicks the button/link                                 jQuery = button:contains("Finances summary")
    Then the user should see read only view for non-fec declaration

*** Keywords ***
the user enters T&S costs
    [Arguments]  ${typeOfCost}  ${rowNumber}  ${travelCostDescription}  ${numberOfTrips}  ${costOfEachTrip}
    the user selects the option from the drop-down menu     ${typeOfCost}  jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) select[name^="ktp"][name$="type"]
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) textarea[name^="ktp"][name$="description"]  ${travelCostDescription}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="times"]  ${numberOfTrips}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="eachCost"]  ${costOfEachTrip}

Custom suite setup
    the user logs-in in new browser     &{KTPLead}
    the user clicks the button/link     link = ${KTPapplication}
    the user clicks the button/link     link = Your project finances
    the user clicks the button/link     link = Your fEC model

non-applicant user navigates to your FEC model page
    the user clicks the button/link     jQuery = div:contains(A base of knowledge) ~ a:contains("View finances")
    the user clicks the button/link     link = Your fEC model

the user should see read only view for non-fec declaration
    non-applicant user navigates to your FEC model page
    the user should not see the element                     jQuery = button:contains("Edit your fEC Model")
    the user should see the element                         jQuery = p:contains(No)

knowledge based applicant invites KTA user to the application
    the user logs-in in new browser                  &{KTPLead}
    the user clicks the button/link                  link = ${KTPapplication}
    the user clicks the button/link                  link = Application team
    the user enters text to a text field             id = ktaEmail   ${ktp_KTA_email}
    the user clicks the button/link                  name = invite-kta
    logout as user
    the user reads his email and clicks the link     ${ktp_KTA_email}   ${KTA_invitation_email_subject}   ${invited_email_pattern}
    the user clicks the button/link                  jQuery = a:contains("Continue")
    logging in and error checking                    ${ktp_KTA_email}   ${short_password}

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
    the user should see the element         jQuery = th:contains("Total associate employment costs") ~ td:contains("£100")
    the user should see the element         jQuery = th:contains("Total academic and secretarial support costs") ~ td:contains("£100")
    the user should see the element         jQuery = th:contains("Total associate development costs") ~ td:contains("£100")
    the user should see the element         jQuery = th:contains("Total travel and subsistence costs") ~ td:contains("£6,150")
    the user should see the element         jQuery = th:contains("Total consumables costs") ~ td:contains("£2,000")
    the user should not see the element     jQuery = th:contains("Total knowledge base supervisor costs")
    the user should not see the element     jQuery = th:contains("Total associates estates costs")
    the user should not see the element     jQuery = th:contains("Total additional associate support costs")
    the user should see the element         jQuery = th:contains("Total other costs") ~ td:contains("£1,000")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("£92")
    the user should see the element         jQuery = th:contains("Total additional company cost estimates") ~ td:contains("£600")

the user should see the correct data in the finance tables
    the user should see the element         jQuery = td:contains("Associate Employment") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Academic and secretarial support") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Associate development") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Travel and subsistence") ~ td:contains("6,150")
    the user should see the element         jQuery = td:contains("Consumables") ~ td:contains("2,000")
    the user should not see the element     jQuery = td:contains("Knowledge base supervisor") ~ td:contains("123")
    the user should not see the element     jQuery = td:contains("Estate") ~ td:contains("1,000")
    the user should not see the element     jQuery = td:contains("Additional associate support") ~ td:contains("1,000")
    the user should see the element         jQuery = td:contains("Other costs") ~ td:contains("1,000")
    the user should see the element         jQuery = td:contains("Indirect costs") ~ td:contains("92")
    the user should see the element         jQuery = th:contains("Total") ~ td:contains("£9,542")

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
    the user enters text to a text field     jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="duration"]  ${duration}
    the user enters text to a text field     jQuery = table[id="${associateSalaryTable}"] td:contains("Associate 1") ~ td input[id$="cost"]  ${cost}

expand the sections
    the user clicks the button/link       jQuery = button:contains("Associate employment")
    the user clicks the button/link       jQuery = button:contains("Associate development")

subcontracting fields should not display
    the user should not see the element     css = input[id^="subcontracting"][id$="cost"]
    the user should not see the element     css = input[id^="subcontracting"][id$="name"]
    the user should not see the element     css = input[id^="subcontracting"][id$="country"]
    the user should not see the element     css = textarea[id^="subcontracting"][id$="role"]

the user sees the selection is not saved
    the user clicks the button/link                         link = Your fEC model
    the user sees that the radio button is not selected     fecModelEnabled  fecModelEnabled-no

the user sees the selection is saved
    the user clicks the button/link                     link = Your fEC model
    the user sees that the radio button is selected     fecModelEnabled  fecModelEnabled-no

the user completes your funding section
    the user clicks the button/link       link = Your funding
    the user selects the radio button     requestingFunding  request-funding-no
    the user selects the radio button     otherFunding  other-funding-no
    the user clicks the button/link       jQuery = button:contains("Mark as complete")

the user collapses and expands the academic and secretarial support section
    the user clicks the button/link         jQuery = button:contains("Academic and secretarial support")
    the user should not see the element     id = academicAndSecretarialSupportForm
    the user clicks the button/link         jQuery = button:contains("Academic and secretarial support")
    the user should see the element         jQuery = p:contains("You may enter up to £875")
