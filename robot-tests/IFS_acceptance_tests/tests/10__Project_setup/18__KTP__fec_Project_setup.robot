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
${KTPapplication}          FEC application duplicate
${KTPapplicationId}        ${application_ids["${KTPapplication}"]}
${KTPcompetiton}           FEC KTP competition duplicate
${KTPcompetitonId}         ${competition_ids["${KTPcompetiton}"]}
&{KTPLead}                 email=joseph.vijay@master.64    password=${short_password}
${cost_value}              100
${indirect_cost_total}     28

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

Lead applicant can view their non-FEC project finances in the Eligibility section
    [Documentation]  IFS-9248
    [Setup]  internal user moves competition to project setup
    Given log in as a different user                             &{KTPLead}
    When the user navigates to finance checks
    And the user clicks the button/link                          link = your project finances
    Then the user should view their non-fec project finances

Lead applicant can view their non-FEC project finance overview
    [Documentation]  IFS-9248
    Given the user clicks the button/link                              link = Back to finance checks
    When the user clicks the button/link                               link = view the project finance overview
    Then the user should view the non-fec project finance overview

Partner can view the non-FEC project finance overview
    [Documentation]  IFS-9248
    Given log in as a different user                                   &{collaborator1_credentials}
    When the user navigates to finance checks
    And The user clicks the button/link                                link = view the project finance overview
    Then the user should view the non-fec project finance overview

Lead applicant can view their non-FEC project finances in the Eligibility section when approved
    [Documentation]  IFS-9248
    [Setup]  internal user approves finances
    Given log in as a different user                             &{KTPLead}
    When the user navigates to finance checks
    And The user clicks the button/link                          link = review your project finances
    Then the user should view their non-fec project finances
    And the user should see the element                          jQuery = p:contains("The partner's finance eligibility has been approved by ")

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
    moving competition to Closed                         ${KTPcompetitonId}
    log in as a different user                           &{internal_finance_credentials}
    the user closed ktp assesment                        ${KTPcompetitonId}
    the user navigates to the page                       ${server}/project-setup-management/competition/${KTPcompetitonId}/status/all
    the user refreshes until element appears on page     jQuery = tr div:contains("${KTPapplication}")

the user navigates to finance checks
    the user clicks the button/link     jQuery = li:contains("Project in setup") a:contains("${KTPapplication}")
    the user clicks the button/link     link = Finance checks

the user should view their non-fec project finances
    the user should see the element         jQuery = h2:contains("Detailed finances")
    the user should see the element         jQuery = legend:contains("Will you be using the full economic costing (fEC) funding model?") p:contains("No")
    the user should see the element         jQuery = span:contains("${cost_value}") ~ button:contains("Academic and secretarial support")
    the user should see the element         jQuery = th:contains("Total academic and secretarial support costs") ~ td:contains("${cost_value}")
    the user should see the element         jQuery = span:contains("${indirect_cost_total}") ~ button:contains("Indirect costs")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("${indirect_cost_total}")
    the user should see the element         jQuery = div:contains("Total project costs") input[value="£1,135"]
    the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = button:contains("Associates estates costs")
    the user should not see the element     jQuery = button:contains("Additional associate support")

the user should view the non-fec project finance overview
    the user should see the element         jQuery = h3:contains("Project cost summary")
    the user should see the element         jQuery = tr:contains("Academic and secretarial support") td:contains("${cost_value}")
    the user should see the element         jQuery = tr:contains("Indirect costs") td:contains("${indirect_cost_total}")
    the user should see the element         jQuery = tr:contains("Total") td:contains("1,135")
    the user should not see the element     jQuery = tr:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = tr:contains("Associates estates costs")
    the user should not see the element     jQuery = tr:contains("Additional associate support")

the user closed ktp assesment
    [Arguments]  ${compID}
    the user navigates to the page      ${server}/management/competition/${compID}
    ${status}  ${value} =  Run Keyword And Ignore Error Without Screenshots  page should contain element  css = button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'PASS'  the user clicks the button/link  css = button[type="submit"][formaction$="close-assessment"]
    Run Keyword If  '${status}' == 'FAIL'  Run keywords    the user clicks the button/link    css = button[type="submit"][formaction$="notify-assessors"]
    ...    AND  the user clicks the button/link    css = button[type="submit"][formaction$="close-assessment"]
    run keyword and ignore error without screenshots     the user clicks the button/link    css = button[type="submit"][formaction$="close-assessment"]

internal user approves finances
    log in as a different user                              &{internal_finance_credentials}
    requesting IDs of this project
    requesting organisation IDs
    the user navigates to the page                          ${server}/project-setup-management/project/${project_id}/finance-check/organisation/${lead_org_id}/eligibility
    the user selects the checkbox                           project-eligible
    the user selects the option from the drop-down menu     Green  id = rag-rating
    the user clicks the button/link                         jQuery = .govuk-button:contains("Approve eligible costs")
    the user clicks the button/link                         name = confirm-eligibility

requesting IDs of this project
    ${project_id} =  get project id by name    ${KTPapplication}
    Set suite variable    ${project_id}

requesting organisation IDs
    ${lead_org_id} =    get organisation id by name     Master 64
    Set suite variable      ${lead_org_id}

Custom suite teardown
    Close browser and delete emails
    Disconnect from database
