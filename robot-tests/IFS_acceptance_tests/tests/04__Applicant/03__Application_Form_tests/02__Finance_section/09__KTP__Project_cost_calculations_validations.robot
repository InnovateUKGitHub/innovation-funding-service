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
...               IFS-9339 KTP fEC/Non-fEC: application changes for read-only finance tables
...
...               IFS-9340 KTP fEC/Non-fEC: application changes for print view
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../../../resources/defaultResources.robot
Resource          ../../../../resources/common/Applicant_Commons.robot
Resource          ../../../../resources/common/Competition_Commons.robot
Resource          ../../../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}  	                      KTP application duplicate
${KTPapplicationId}                       ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                          KTP new competition duplicate
${KTPcompetitonId}                        ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}                                email=bob@knowledge.base    password=${short_password}
${ktp_KTA_name}                           Hermen Mermen
${ktp_KTA_email}                          hermen.mermen@ktn-uk.test
&{KTA_assessor_credentials}               email=hermen.mermen@ktn-uk.test   password=${short_password}
&{supporter_credentials}                  email=hubert.cumberdale@salad-fingers.com   password=${short_password}
${supporter_name}                         Hubert Cumberdale
${KTA_invitation_email_subject}           Invitation to be Knowledge Transfer Adviser
${invited_email_pattern}                  You have been invited to be the knowledge transfer adviser for the Innovation Funding Service application:
${estateValue}                            11000
${associateSalaryTable}                   associate-salary-costs-table
${associateDevelopmentTable}              associate-development-costs-table
${limitFieldValidationMessage}            You must provide justifications for exceeding allowable cost limits.
${academic_secretarial_support_table}     academic-secretarial-costs-table
${academicSecretarialCost}                academic-secretarial-costs
@{turnover}                               100000  98000   96000
@{preTaxProfit}                           98000   96000   94000
@{netCurrentAssets}                       100000  100000  100000
@{liabilities}                            20000   15000   10000
@{shareHolderFunds}                       20000   15000   10000
@{loans}                                  35000   40000   45000
@{employees}                              2000    1500    1200

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
    When the user clicks the button/link        link = Your project costs
    Then the user should not see the element    jQuery = button:contains("Knowledge base supervisor")
    And the user should not see the element     jQuery = button:contains("Additional associate support")
    And the user should not see the element     jQuery = button:contains("Associates estates costs")

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
     Then the user should see the right values      100   Associate employment    223
     And the user should not see the element        jQuery = table[id="${associateDevelopmentTable}"] td:contains("Associate 1") ~ td:contains(${empty_field_warning_message})

KB applicant can provide an academic and secretarial support cost in a non-fEC project cost table
    [Documentation]  IFS-9243
    Given the user collapses and expands the academic and secretarial support section
    When the user enters text to a text field                                            id = academicAndSecretarialSupportForm  100
    Then the user should see the element                                                 jQuery = span:contains("100") ~ button:contains("Academic and secretarial support")
    And the user should see the element                                                  jQuery = h4:contains("Total academic and secretarial support costs") span:contains("100")

Calculate indirect cost
    [Documentation]  IFS-9244
    Given the user should see the element     jQuery = p:contains("The calculation is 46% of the sum of 2 grant amounts:'Associate employment costs' and the 'Academic and secretarial support costs'.")
    Then the user should see the element      jQuery = h4:contains("Total indirect costs"):contains("46")

Subcontracting costs should not display in project costs
    [Documentation]  IFS-8157
    Then subcontracting fields should not display

Travel and subsistence cost calculations
    [Documentation]  IFS-7790  IFS-8156
    When the user enters T&S costs                                           Supervisor  1  Knowledge Base biweekly travel  30  185
    And the user clicks the button/link                                      name = add_row
    And the user enters T&S costs                                            Associate  2  3 trips to Glasgow  3  200
    Then the user should see the right T&S cost summary and total values

Consumables calculations
    [Documentation]  IFS-7790
    Given the user fills in consumables
    Then the user should see the right values    2,000    Consumables    8496

Other costs calculations
    [Documentation]  IFS-7790
    Given the user fills in ktp other costs     Other costs   1000
    Then the user should see the right values   1,000    Other costs    9496

Total cost calculation
    [Documentation]  IFS-9245
    Then the user should see the element     jQuery = label:contains("'A base of knowledge' Total project costs")
    And the user should see the element      jQuery = div:contains("Total project costs") input[data-calculation-rawvalue="9496"]

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

Internal user can view the project cost tabel in the print view
    [Documentation]   IFS-9340
    When the user clicks the button/link                                                link = Print application
    Then the user should see the correct values in project cost table in print view
    [Teardown]   the user closes the last opened tab

Customer support user can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    Given log in as a different user                                    &{support_user_credentials}
    When the user navigates to the page                                 ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
    Then the user should see read only view for non-fec declaration

Customer support user can view read-only view of the project cost table in finance overview section
   [Documentation]   IFS-9339
   When the user navigates to the page                                 ${server}/management/competition/${KTPcompetitonId}/application/${KTPapplicationId}
   Then the user should see the correct data in the finance tables

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

Business user can view read-only view of the project cost table in finance overview section
   [Documentation]   IFS-9339
   When the user navigates to the page                                 ${server}/application/${KTPapplicationId}
   And the user clicks the button/link                                 link = Finances overview
   Then the user should see the correct data in the finance tables

KTA user assigned to application can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    [Setup]  knowledge based applicant invites KTA user to the application
    Given the user clicks the button/link                               jQuery = a:contains("Applications")
    When the user clicks the button/link                                link = ${KTPapplication}
    Then the user should see read only view for non-fec declaration

KTA user can view read-only view of the project cost table in finance summary section
    [Documentation]   IFS-9339
    Given the user navigates to the page                                ${server}/application/${KTPapplicationId}/summary
    Then the user should see the correct data in the finance tables

KTA user can view the project cost tabel in the print view
   [Documentation]   IFS-9340
    Given the user navigates to the page                                                ${server}/application/${KTPapplicationId}/summary
    When the user clicks the button/link                                                link = Print application
    Then the user should see the correct values in project cost table in print view
    [Teardown]   the user closes the last opened tab

KTA assessor assigned to application can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    [Setup]  knowledge based applicant completes and submits application
    Given update milestone to yesterday                                     ${KTPcompetitonId}  SUBMISSION_DATE
    When the user invites a registered KTA user to assess competition
    And the allocated assessor accepts invite to assess the competition
    And the user navigates to the page                                      ${server}/application/${KTPapplicationId}/summary
    Then the user should see read only view for non-fec declaration

Supporter can view the read-only view for 'No' selected fEC declaration
    [Documentation]  IFS-9246
    [Setup]  ifs admin invites a supporter to the ktp application
    Given log in as a different user                                    &{supporter_credentials}
    When the user navigates to the page                                 ${server}/application/${KTPapplicationId}/summary
    Then the user should see read only view for non-fec declaration

Supporter can view read-only view of the project cost table in finance summary section
    [Documentation]   IFS-9339
    When the user navigates to the page                                 ${server}/application/${KTPapplicationId}/summary
    Then the user should see the correct data in the finance tables

Supporter can view the project cost tabel in the print view
    [Documentation]   IFS-9340
    Given the user navigates to the page                                                ${server}/application/${KTPapplicationId}/summary
    When the user clicks the button/link                                                link = Print application
    Then the user should see the correct values in project cost table in print view
    [Teardown]   the user closes the last opened tab

KB can view the project cost tabel in the print view
   [Documentation]   IFS-9340
    Given log in as a different user                                                    &{KTPLead}
    And the user navigates to the page                                                  ${server}/application/${KTPapplicationId}
    When the user clicks the button/link                                                link = Print application
    Then the user should see the correct values in project cost table in print view
    [Teardown]   the user closes the last opened tab

*** Keywords ***
the user enters T&S costs
    [Arguments]  ${typeOfCost}  ${rowNumber}  ${travelCostDescription}  ${numberOfTrips}  ${costOfEachTrip}
    the user selects the option from the drop-down menu     ${typeOfCost}  jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) select[name^="ktp"][name$="type"]
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) textarea[name^="ktp"][name$="description"]  ${travelCostDescription}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="times"]  ${numberOfTrips}
    the user enters text to a text field                    jQuery = div:contains(Travel and subsistence) tr:nth-of-type(${rowNumber}) input[name^="ktp"][name$="eachCost"]  ${costOfEachTrip}

Custom suite setup
    Set predefined date variables
    Connect to database                 @{database}
    the user logs-in in new browser     &{KTPLead}
    the user clicks the button/link     link = ${KTPapplication}
    the user clicks the button/link     link = Your project finances
    the user clicks the button/link     link = Your fEC model

Custom suite teardown
    Close browser and delete emails
    Disconnect from database

non-applicant user navigates to your FEC model page
    the user clicks the button/link     jQuery = div:contains("A base of knowledge") ~ a:contains("View finances")
    the user clicks the button/link     link = Your fEC model

the user should see read only view for non-fec declaration
    non-applicant user navigates to your FEC model page
    the user should not see the element                     jQuery = button:contains("Edit your fEC Model")
    the user should see the element                         jQuery = p:contains(No)

knowledge based applicant invites KTA user to the application
    log in as a different user                       &{KTPLead}
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
    the user should not see the element     jQuery = th:contains("Total knowledge base supervisor costs") ~ td:contains("£123")
    the user should not see the element     jQuery = th:contains("Total associates estates costs") ~ td:contains("£1,000")
    the user should not see the element     jQuery = th:contains("Total additional associate support costs") ~ td:contains("£1,000")
    the user should see the element         jQuery = th:contains("Total other costs") ~ td:contains("£1,000")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("£46")
    the user should see the element         jQuery = th:contains("Total additional company cost estimates") ~ td:contains("£600")

the user should see the correct data in the finance tables
    the user should see the element         jQuery = td:contains("Associate Employment") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Academic and secretarial support") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Associate development") ~ td:contains("100")
    the user should see the element         jQuery = td:contains("Travel and subsistence") ~ td:contains("6,150")
    the user should see the element         jQuery = td:contains("Consumables") ~ td:contains("2,000")
    the user should not see the element     jQuery = td:contains("Knowledge base supervisor") ~ td:contains("£123")
    the user should not see the element     jQuery = td:contains("Estate") ~ td:contains("£1,000")
    the user should not see the element     jQuery = td:contains("Additional associate support") ~ td:contains("£1,000")
    the user should see the element         jQuery = td:contains("Other costs") ~ td:contains("1,000")
    the user should see the element         jQuery = td:contains("Indirect costs") ~ td:contains("46")
    the user should see the element         jQuery = th:contains("Total") ~ td:contains("£9,496")

the user should see the correct values in project cost table in print view
    the user should see the element         xpath = //td[text()='100']/..//td[text()='Associate Employment']
    the user should see the element         xpath = //td[text()='100']/..//td[text()='Academic and secretarial support']
    the user should see the element         xpath = //td[text()='100']/..//td[text()='Associate development']
    the user should see the element         xpath = //td[text()='6,150']/..//td[text()='Travel and subsistence']
    the user should see the element         xpath = //td[text()='2,000']/..//td[text()='Consumables']
    the user should not see the element     xpath = //td[text()='123']/..//td[text()='Knowledge base supervisor']
    the user should not see the element     xpath = //td[text()='1,000']/..//td[text()='Estate']
    the user should not see the element     xpath = //td[text()='1,000']/..//td[text()='Additional associate support']
    the user should see the element         xpath = //td[text()='1,000']/..//td[text()='Other costs']
    the user should see the element         xpath = //td[text()='46']/..//td[text()='Indirect costs']
    the user should see the element         xpath = //td[text()='£9,496']/..//th[text()='Total']

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
    the user clicks the button/link        link = Your funding
    the user selects the radio button      requestingFunding  request-funding-yes
    the user enters text to a text field   css = input[id^="grantClaimPercentage"]    50
    the user selects the radio button      otherFunding  other-funding-no
    the user clicks the button/link        jQuery = button:contains("Mark as complete")

the user collapses and expands the academic and secretarial support section
    the user clicks the button/link         jQuery = button:contains("Academic and secretarial support")
    the user should not see the element     id = academicAndSecretarialSupportForm
    the user clicks the button/link         jQuery = button:contains("Academic and secretarial support")
    the user should see the element         jQuery = p:contains("You may enter up to £875")

knowledge based applicant completes and submits application
    log in as a different user                               &{KTPLead}
    the user clicks the button/link                          link = ${KTPapplication}
    the user clicks the button/link                          link = Your project finances
    the user marks the KTP project location as complete
    the user accept the competition terms and conditions     Return to application overview
    log in as a different user                               &{collaborator1_credentials}
    the user clicks the button/link                          link = ${KTPapplication}
    the user clicks the button/link                          link = Your project finances
    the user marks the KTP project location as complete
    the user fills in the KTP organisation information
    the user completes other funding section
    the user clicks the button/link                          link = Back to application overview
    the user accept the competition terms and conditions     Return to application overview
    log in as a different user                               &{KTPLead}
    the user clicks the button/link                          link = ${KTPapplication}
    the user clicks the button/link                          link = Review and submit
    the user clicks the button/link                          jQuery = button:contains("Submit application")

the user completes other funding section
    the user clicks the button/link       link = Other funding
    the user selects the radio button     otherFunding  false
    the user clicks the button/link       jQuery = button:contains("Mark as complete")

the user marks the KTP project location as complete
    the user enters the project location
    the user should see the element          jQuery = li:contains("Your project location") span:contains("Complete")
    the user clicks the button/link          link = Back to application overview

the user fills in the KTP organisation information
    the user clicks the button/link                                                link = Your project finances
    the user clicks the button/link                                                link = Your organisation
    ${STATUS}    ${VALUE} =   Run Keyword And Ignore Error Without Screenshots     page should contain element  jQuery = button:contains("Edit")
    Run Keyword If    '${status}' == 'PASS'                                        the user clicks the button/link  jQuery = button:contains("Edit")
    the user selects the radio button                                              organisationSize  ${SMALL_ORGANISATION_SIZE}
    the user enters text to a text field                                           name = financialYearEndMonthValue  04
    the user enters text to a text field                                           name = financialYearEndYearValue   2020
    the user fills financial overview section
    the user clicks the button/link                                                jQuery = button:contains("Mark as complete")
    the user should see the element                                                jQuery = li:contains("Your organisation") span:contains("Complete")

the user fills financial overview section
    ${i} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{turnover}
             \    the user enters text to a text field     id = years[${i}].turnover  ${ELEMENT}
             \    ${i} =   Evaluate   ${i} + 1

    ${j} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{preTaxProfit}
             \    the user enters text to a text field     id = years[${j}].preTaxProfit  ${ELEMENT}
             \    ${j} =   Evaluate   ${j} + 1

    ${k} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{netCurrentAssets}
             \    the user enters text to a text field     id = years[${k}].currentAssets  ${ELEMENT}
             \    ${k} =   Evaluate   ${k} + 1

    ${l} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{liabilities}
             \    the user enters text to a text field     id = years[${l}].liabilities  ${ELEMENT}
             \    ${l} =   Evaluate   ${l} + 1

    ${m} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{shareHolderFunds}
             \    the user enters text to a text field     id = years[${m}].shareholderValue  ${ELEMENT}
             \    ${m} =   Evaluate   ${m} + 1

    ${n} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{loans}
             \    the user enters text to a text field     id = years[${n}].loans  ${ELEMENT}
             \    ${n} =   Evaluate   ${n} + 1

    ${a} =  Set Variable   0
        :FOR   ${ELEMENT}   IN    @{employees}
             \    the user enters text to a text field     id = years[${a}].employees  ${ELEMENT}
             \    ${a} =   Evaluate   ${a} + 1

    the user enters text to a text field     id = groupEmployees  200

the user invites a registered KTA user to assess competition
    log in as a different user               &{Comp_admin1_credentials}
    the user clicks the button/link          link = ${KTPcompetiton}
    the user clicks the button/link          link = Invite assessors to assess the competition
    the user enters text to a text field     id = assessorNameFilter   ${ktp_KTA_name}
    the user clicks the button/link          jQuery = .govuk-button:contains("Filter")
    the user clicks the button/link          jQuery = tr:contains("${ktp_KTA_name}") label[for^="assessor-row"]
    the user clicks the button/link          jQuery = .govuk-button:contains("Add selected to invite list")
    the user clicks the button/link          link = Invite
    the user clicks the button/link          link = Review and send invites
    the user enters text to a text field     id = message    This is custom text
    the user clicks the button/link          jQuery = .govuk-button:contains("Send invitation")

the allocated assessor accepts invite to assess the competition
    log in as a different user                            &{KTA_assessor_credentials}
    the user clicks the button/link                       jQuery = a:contains("Assessments")
    the user clicks the button/link                       link = ${KTPcompetiton}
    the user selects the radio button                     acceptInvitation  true
    the user clicks the button/link                       jQuery = button:contains("Confirm")
    the user should be redirected to the correct page     ${server}/assessment/assessor/dashboard

ifs admin invites a supporter to the ktp application
    log in as a different user               &{ifs_admin_user_credentials}
    the user clicks the button/link          link = ${KTPcompetiton}
    the user clicks the button/link          link = Manage supporters
    the user clicks the button/link          link = Assign supporters to applications
    the user clicks the button/link          jQuery = td:contains("${KTPapplication}") ~ td a:contains("Assign")
    the user enters text to a text field     id = filter    ${supporter_name}
    the user clicks the button/link          jQuery = button:contains("Filter")
    the user selects the checkbox            select-all-check
    the user clicks the button/link          jQuery = button:contains("Add selected to application")