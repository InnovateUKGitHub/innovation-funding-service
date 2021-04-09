*** Settings ***
Documentation     IFS-9305  KTP fEC/Non-fEC: display correct finance table if fEC option changes
...
...               IFS-9248  KTP fEC/Non-fEC: view non-fEC costs in project setup
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${KTPapplication}                         FEC application duplicate
${KTPapplicationId}                       ${application_ids["${KTPapplication}"]}
${KTPcompetiton}                          FEC KTP competition duplicate
${KTPcompetitonId}                        ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}                                email=joseph.vijay@master.64    password=${short_password}
${associateSalaryTable}                   associate-salary-costs-table
${associateDevelopmentTable}              associate-development-costs-table
${limitFieldValidationMessage}            You must provide justifications for exceeding allowable cost limits.
${academic_secretarial_support_table}     academic-secretarial-costs-table
${academicSecretarialCost}                academic-secretarial-costs
${cost_value}                             100

*** Test Cases ***
Lead applicant can view the project finances section is complete
    [Documentation]  IFS-9305
    Given the user clicks the button/link                     link = Your project finances
    When the user completes your project finances section
    Then the user should see the element                      jQuery = li:contains("Your project finances") span:contains("Complete")

Lead applicant can view the correct project costs fields are displayed for the fec model
    [Documentation]  IFS-9305
    Given the user clicks the button/link     link = Your project finances
    When the user clicks the button/link      link = Your project costs
    Then the user should see the element      jQuery = h3:contains("Knowledge base supervisor")
    And the user should see the element       jQuery = h3:contains("Associates estates costs")
    And the user should see the element       jQuery = h3:contains("Additional associate support")
    And the user clicks the button/link       link = Your project finances

Lead applicant edits the fec model to NO
    [Documentation]  IFS-9305
    Given the user edits the KTP fec model     fecModelEnabled-no
    Then the user should see the element       jQuery = li:contains("Your fEC model") span:contains("Complete")
    And the user should see the element        jQuery = li:contains("Your project costs") span:contains("Incomplete")

Lead applicant should view the correct project costs are displayed for non-fec selection
    [Documentation]  IFS-9305
    Given the user clicks the button/link                                     link = Your project costs
    Then the user views the project finance details for non-fec selection

Lead applicant completes the project finances section for non-fec model
    [Documentation]  IFS-9305
    Given the user clicks the button/link                             jQuery = button:contains("Open all")
    Then the user completes project costs table for non-fec model     1  ${cost_value}  Supervisor  1  test  3  test  5

Partner applicant completes the application
    [Documentation]  IFS-9305
    Given Log in as a different user                                                   &{collaborator1_credentials}
    When the user navigates to the page                                                ${server}/application/${KTPapplicationId}
    And the user clicks the button/link                                                link = Your project finances
    Then the partner applicant marks Your project finances information as complete     other-funding-no   ${SMALL_ORGANISATION_SIZE}  12  2020
    And the user accept the competition terms and conditions                           Return to application overview

Lead applicant submits the application
    [Documentation]  IFS-9305
    Given log in as a different user                              &{KTPLead}
    When the user navigates to the page                           ${server}/application/${KTPapplicationId}
    Then the user accept the competition terms and conditions     Return to application overview
    And the applicant submits the application

#-------------- Seb's
Lead applicant can view their non-FEC project finances in the Eligibility section
    [Documentation]  IFS-9248
    [Setup]  internal user moves competition to project setup
    Given log in as a different user                             &{KTPLead}
    When the user navigates to finance checks
    And the user clicks the button/link                          link = your project finances
    Then the user should view their non-fec project finances

#-------------- Seb's

*** Keywords ***
Custom Suite Setup
    Connect to Database                    @{database}
    The user logs-in in new browser        &{KTPLead}
    the user clicks the button/link        link = ${KTPapplication}

the user completes your project finances section
    the user clicks the button/link        link = Your fEC model
    the user clicks the button/link        jQuery = button:contains("Mark as complete")
    the user clicks the button/link        link = Your funding
    the user selects the radio button      otherFunding  other-funding-no
    the user clicks the button/link        jQuery = button:contains("Mark as complete")
    the user clicks the button/link        link = Your project costs
    the user clicks the button/link        exceed-limit-no
    the user clicks the button/link        css = label[for="stateAidAgreed"]
    the user clicks the button/link        jQuery = button:contains("Mark as complete")
    the user clicks the button/link        link = Your project location
    the user clicks the button/link        jQuery = button:contains("Mark as complete")
    the user clicks the button/link        link = Back to application overview

the user views the project finance details for non-fec selection
    the user should see the element         jQuery = button:contains("Academic and secretarial support")
    the user should see the element         jQuery = button:contains("Indirect costs")
    the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = button:contains("Additional associate support")
    the user should not see the element     jQuery = button:contains("Associates estates costs")

internal user moves competition to project setup
    moving competition to Closed                          ${KTPcompetitonId}
    log in as a different user                            &{internal_finance_credentials}
    the user closed ktp assesment                         ${KTPcompetitonId}
    the user navigates to the page                        ${server}/project-setup-management/competition/${KTPcompetitonId}/status/all
    the user refreshes until element appears on page      jQuery = tr div:contains("${KTPapplication}")

the user navigates to finance checks
    the user clicks the button/link     jQuery = li:contains("Project in setup") a:contains("${KTPapplication}")
    the user clicks the button/link     link = Finance checks

the user should view their non-fec project finances
    the user should see the element         jQuery = h2:contains("Detailed finances")
    the user should see the element         jQuery = legend:contains("Will you be using the full economic costing (fEC) funding model?") p:contains("No")
    the user should see the element         jQuery = button:contains("Open all")
    the user should see the element         jQuery = span:contains("${cost_value}") ~ button:contains("Academic and secretarial support")
    the user should see the element         jQuery = th:contains("Total academic and secretarial support costs") ~ td:contains("${cost_value}")
    the user should see the element         jQuery = span:contains("${cost_value}") ~ button:contains("Indirect costs")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("${cost_value}")
    the user should see the element         jQuery = div:contains("Total project costs") input[value=£999]
    the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = button:contains("Associates estates costs")
    the user should not see the element     jQuery = button:contains("Additional associate support")

Custom suite teardown
    Close browser and delete emails
    Disconnect from database
