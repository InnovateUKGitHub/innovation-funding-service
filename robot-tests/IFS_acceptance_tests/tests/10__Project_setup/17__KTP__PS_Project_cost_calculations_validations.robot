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
${ktpProjectID}                    ${project_ids["${KTPapplication}"]}
${KTPapplicationId}                ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                   KTP in panel
${KTPcompetitonId}                 ${competition_ids["${KTPcompetiton}"]}
${ktpLeadOrgName}                  A base of knowledge
${ktpLeadOrgID}                    ${organisation_ids["${ktpLeadOrgName}"]}
${ktpPartnerOrgName}               Ludlow
${ktpPartnerOrgId}                 ${organisation_ids["${ktpPartnerOrgName}"]}
&{FinanceUser}                     email=lee.bowman@innovateuk.test    password=${short_password}
&{ifsAdmin}                        email=arden.pimenta@innovateuk.test     password=${short_password}
&{ktpLead}                         email=bob@knowledge.base    password=${short_password}
&{ktpPartner}                      email=jessica.doe@ludlow.co.uk    password=${short_password}

*** Test Cases ***
Internal user can edit the duration of the project
    [Documentation]  IFS-8328
    Given The user navigates to the page          ${server}/project-setup-management/competition/${KTPcompetitonId}/project/${ktpProjectID}/duration
    When the user enters text to a text field     id = durationInMonths    30
    And The user clicks the button/link           jQuery = button:contains("Save and return to project finances")
    Then The user should see the element          jQuery = dt:contains("Duration") ~ dd:contains("30 months")

Internal user can edit KTP finances in project setup
    [Documentation]  IFS-8328
    Given the user navigates to the page                     ${server}/project-setup-management/project/${ktpProjectID}/finance-check/organisation/${ktpLeadOrgID}/eligibility
    When The user clicks the button/link                     jQuery = button:contains("Open all")
    Then The user edits the KTP costs section
    And the user confirms the values in the finance table

Internal user approves the Eligibility of the lead applicant and partner
    [Documentation]  IFS-8328
    When the user approves project costs
    Then the user should see the element     jQuery = p:contains("The partner's finance eligibility has been approved by ")
    And the user navigates to the page       ${server}/project-setup-management/project/${ktpProjectID}/finance-check/organisation/${ktpPartnerOrgId}/eligibility
    And the user approves project costs

Internal user approves the Viability of the lead applicant and partner
    [Documentation]  IFS-8328
    When The user navigates to the page     ${server}/project-setup-management/project/${ktpProjectID}/finance-check/organisation/${ktpLeadOrgID}/viability
    Then the user approves viability
    And The user navigates to the page      ${server}/project-setup-management/project/${ktpProjectID}/finance-check/organisation/${ktpPartnerOrgId}/viability
    And the user approves viability

Internal user checks the values in Finance checks page
    [Documentation]  IFS-8328
    When The user navigates to the page                                      ${server}/project-setup-management/project/${ktpProjectID}/finance-check
    Then the user checks the project finances in the finance checks page

Internal user checks the Finance overview page
    [Documentation]  IFS-8328
    When The user clicks the button/link                                                               link = View finances
    Then the user verifies the values in the finance summary table after editing the project costs
    And the user checks the values in the projects costs summary table

The lead checks the Finance overview page
    [Documentation]  IFS-8328
    Given Log in as a different user                                                                  bob@knowledge.base    Passw0rd1357
    And The user navigates to the page                                                                ${server}/project-setup/project/${ktpProjectID}/finance-checks
    When The user clicks the button/link                                                              link = your project finance overview
    Then the user verifies the values in the finance summary table after editing the project costs
    And the user checks the values in the projects costs summary table
    And The user clicks the button/link                                                               link = Back to finance checks

The lead checks the Eligibility check page
    [Documentation]  IFS-8328
    When The user clicks the button/link                      link = review your project finances
    Then the user confirms the values in the finance table

The partner checks the Finance overview page
    [Documentation]  IFS-8328
    Given Log in as a different user                                                                  jessica.doe@ludlow.co.uk    Passw0rd1357
    And The user navigates to the page                                                                ${server}/project-setup/project/${ktpProjectID}/finance-checks
    When The user clicks the button/link                                                              link = project finance overview
    Then the user verifies the values in the finance summary table after editing the project costs
    And the user checks the values in the projects costs summary table

*** Keywords ***
Custom suite setup
    the user logs-in in new browser   &{ifs_admin_user_credentials}

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
    the user enters text to a text field     jQuery = div:contains(Travel and subsistence) tr:nth-of-type(1) input[name^="ktp"][name$="eachCost"]    50
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=CONSUMABLES"]
    the user enters text to a text field     css = input[id^="consumableCost"][id$="cost"]    60
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=KNOWLEDGE_BASE"]
    the user enters text to a text field     css = table[id="knowledge-base-table"] input[id$="cost"]    70
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=ESTATE_COSTS"]
    the user enters text to a text field     css = input[id^="estate"][id$="cost"]    80
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=ASSOCIATE_SUPPORT"]
    the user enters text to a text field     css = input[id^="associateSupport"][id$="cost"]    90
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user clicks the button/link          css = a[href="?financeType=OTHER_COSTS"]
    the user enters text to a text field     css = input[id^="otherRows"][id$="estimate"]    100
    the user clicks the button/link          css = button[name="save-eligibility"]
    the user should see the element          css = [id = 'total-cost'][value='£720']

the user approves viability
    the user selects the checkbox                          costs-reviewed
    the user selects the checkbox                          project-viable
    Set Focus To Element                                   link = Contact us
    the user selects the option from the drop-down menu    Green  id = rag-rating
    the user clicks the button/link                        css = #confirm-button
    the user clicks the button/link                        jQuery = .modal-confirm-viability .govuk-button:contains("Confirm viability")

the user confirms the values in the finance table
    the user should see the element     jQuery = th:contains("Project duration")
#    the user should see the element     jQuery = td:nth-child(1):contains("30 months")
    the user should see the element     jQuery = th:contains("Total costs")
    the user should see the element     jQuery = td:nth-child(2):contains("£720")
    the user should see the element     jQuery = th:contains("Funding level (%)")
    the user should see the element     jQuery = td:nth-child(3):contains("30.00%")
    the user should see the element     jQuery = th:contains("Funding sought (£)")
    the user should see the element     jQuery = td:nth-child(4):contains("216")
    the user should see the element     jQuery = th:contains("Other funding (£)")
    the user should see the element     jQuery = td:nth-child(5):contains("0")
    the user should see the element     jQuery = th:contains("Company contribution (%)")
    the user should see the element     jQuery = td:nth-child(6):contains("0.00%")
    the user should see the element     jQuery = th:contains("Company contribution (£)")
    the user should see the element     jQuery = td:nth-child(7):contains("0")

the user verifies the values in the finance summary table after editing the project costs
    the user should see the element     jQuery = th:contains("A base of knowledge") + td:contains("£720") + td:contains("30.00%") + td:contains("216") + td:contains("0") + td:contains("0.00%") + td:contains("0")
    the user should see the element     jQuery = th:contains("Ludlow") + td:contains("0") + td:contains("0.00%") + td:contains("0") + td:contains("0") + td:contains("70.00%") + td:contains("504")
    the user should see the element     jQuery = th:contains("Total") + td:contains("£720") + td:contains("30.00%") + td:contains("216") + td:contains("0") + td:contains("70.00%") + td:contains("504")

the user checks the values in the projects costs summary table
    the user should see the element     jQuery = td:contains("Associate Employment") + td:contains("65")
    the user should see the element     jQuery = td:contains("Associate development") + td:contains("85")
    the user should see the element     jQuery = td:contains("Travel and subsistence") + td:contains("50")
    the user should see the element     jQuery = td:contains("Consumables") + td:contains("180")
    the user should see the element     jQuery = td:contains("Knowledge base supervisor") + td:contains("70")
    the user should see the element     jQuery = td:contains("Estate") + td:contains("80")
    the user should see the element     jQuery = td:contains("Additional associate support") + td:contains("90")
    the user should see the element     jQuery = td:contains("Other costs") + td:contains("100")
    the user should see the element     jQuery = th:contains("Total") + td:contains("£720")

the user checks the project finances in the finance checks page
    the user should see the element     jQuery = dt:contains("Total project cost:") + dd:contains("£720")
    the user should see the element     jQuery = dt:contains("Funding applied for:") + dd:contains("£359")
    the user should see the element     jQuery = dt:contains("Current amount:") + dd:contains("£216")
    the user should see the element     jQuery = dt:contains("Other public sector funding:") + dd:contains("£0")
    the user should see the element     jQuery = dt:contains("Total percentage grant:") + dd:contains("30.00%")
    the user should see the element     jQuery = dt:contains("Duration:") + dd:contains("30")