*** Settings ***
Documentation     IFS-9305  KTP fEC/Non-fEC: display correct finance table if fEC option changes
...
...               IFS-9248  KTP fEC/Non-fEC: view non-fEC costs in project setup
...
...               IFS-9249  KTP fEC/Non-fEC: view and edit non-fEC costs in project setup
...
...               IFS-9250  feature/IFS-9250-ktp-fec-non-fec-changes-to-finances
...
...               IFS-9306  KTP fEC/non-fEC: 'academic and secretarial support' GOL value
...
...               IFS-9633  Funding sought calculated incorrectly
...
Suite Setup       Custom Suite Setup
Suite Teardown    Custom suite teardown
Resource          ../../resources/defaultResources.robot
Resource          ../../resources/common/Applicant_Commons.robot
Resource          ../../resources/common/Competition_Commons.robot
Resource          ../../resources/common/PS_Common.robot

*** Variables ***
${ktpApplication}                 FEC application duplicate
${ktpApplicationId}               ${application_ids["${ktpApplication}"]}
${ktpCompetiton}                  FEC KTP competition duplicate
${ktpCompetitonId}                ${competition_ids["${ktpCompetiton}"]}
&{ktpLead}                        email=joseph.vijay@master.64    password=${short_password}
${ktpLeadOrgName}                 Master 64
${costValue}                      100
${indirectCostTotal}              28
${totalProjectCosts}              1,135
${academicCostValue}              1000
${academicCostValueFormatted}     1,000
${indirectCostUpdated}            152
${totalProjectCostsUpdated}       2,159
${fundingSought}                  754

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
    Then the user completes project costs table for non-fec model     1  ${costValue}  Supervisor  1  test  3  test  5

Partner applicant completes the application
    [Documentation]  IFS-9305
    Given Log in as a different user                                                   &{collaborator1_credentials}
    When the user navigates to the page                                                ${server}/application/${ktpApplicationId}
    And the user clicks the button/link                                                link = Your project finances
    Then the partner applicant marks Your project finances information as complete     other-funding-no   ${SMALL_ORGANISATION_SIZE}  12  2020
    And the user accept the competition terms and conditions                           Return to application overview

Lead applicant submits the application
    [Documentation]  IFS-9305
    Given log in as a different user                              &{ktpLead}
    When the user navigates to the page                           ${server}/application/${ktpApplicationId}
    Then the user accept the competition terms and conditions     Return to application overview
    And the applicant submits the application

Lead applicant can view their non-FEC project finances in the Eligibility section
    [Documentation]  IFS-9248 IFS-9250
    [Setup]  internal user moves competition to project setup
    Given log in as a different user                             &{ktpLead}
    When the user navigates to finance checks
    And the user clicks the button/link                          link = your project finances
    Then the user should view their non-fec project finances
    And The user should not see the element                      link = View changes to finances

Lead applicant can view their non-FEC project finance overview
    [Documentation]  IFS-9248 IFS-9250
    Given the user clicks the button/link                              link = Back to finance checks
    And the user should not see the element                            link = view any changes to finances
    When the user clicks the button/link                               link = view the project finance overview
    Then the user should view the non-fec project finance overview

Partner can view the non-FEC project finance overview
    [Documentation]  IFS-9248
    Given log in as a different user                                   &{collaborator1_credentials}
    When the user navigates to finance checks
    And the user clicks the button/link                                link = view the project finance overview
    Then the user should view the non-fec project finance overview

IFS admin can view the correct fields in project finance overview table for non-fEC application
    [Documentation]  IFS-9249
    Given log in as a different user                             &{ifs_admin_user_credentials}
    And requesting IDs of this project
    And requesting organisation IDs
    When the user navigates to the page                          ${server}/project-setup-management/project/${project_id}/finance-check/organisation/${lead_org_id}/eligibility
    Then the user should view their non-fec project finances

IFS admin can edit the project finances in project setup
    [Documentation]  IFS-9249  IFS-9633
    Given the user edits the Academic and secretarial support costs in project setup
    Then the user should see the element                                                 jQuery = th:contains("Total indirect costs") ~ td:contains("${indirectCostUpdated}")
    And the user should see the element                                                  jQuery = div:contains("Total project costs") input[value="£${totalProjectCostsUpdated}"]
    And the user should see the element                                                  jQuery = table:contains("Total costs") td:contains("£${totalProjectCostsUpdated}")
    #  funding sought (£) = (total of all costs except Indirect costs) x funding level (%)) + indirect costs (at 100%)
    And the user should see the element                                                  jQuery = table:contains("Funding sought (£)") tr:contains("${fundingSought}")

IFS admin can view the correct updated values in the Changes to finances page from eligibility screen
    [Documentation]  IFS-9249  IFS-9633
    Given the user clicks the button/link                               link = View changes to finances
    Then the user should view updated values in changes to finances

IFS admin can view the correct updated values in the Finance checks
    [Documentation]  IFS-9249  IFS-9633
    Given the user clicks the button/link     link = Eligibility
    When the user clicks the button/link      link = Back to finance checks
    Then the user should see the element      jQuery = dt:contains("Total project cost:") ~ dd:contains("£${totalProjectCostsUpdated}")
    Then the user should see the element      jQuery = dt:contains("Current amount:") ~ dd:contains("£${fundingSought}")

IFS admin can view the correct updated values in the Finance overview
    [Documentation]  IFS-9249  IFS-9633
    Given the user clicks the button/link     link = View finances
    Then the user should see the element      jQuery = th:contains("${ktpLeadOrgName}") ~ td:contains("${totalProjectCostsUpdated}")
    And the user should see the element       jQuery = td:contains("Academic and secretarial support") ~ td:contains("${academicCostValueFormatted}")
    And the user should see the element       jQuery = td:contains("Indirect costs") ~ td:contains("${indirectCostUpdated}")
    And the user should see the element       jQuery = th:contains("Total") ~ td:contains("£${totalProjectCostsUpdated}")
    And the user should see the element       jQuery = table:contains("Funding sought") th:contains("${ktpLeadOrgName}") ~ td:contains("${fundingSought}")

Lead applicant should be able to view any changes to finances screen before approved
    [Documentation]  IFS-9250
    Given log in as a different user                      &{ktpLead}
    When the user navigates to finance checks
    Then the user views the changes to finance screen

Lead applicant can view their non-FEC project finances in the Eligibility section when approved
    [Documentation]  IFS-9248  IFS-9633
    [Setup]  internal user approves finances
    Given log in as a different user                                           &{ktpLead}
    When the user navigates to finance checks
    And the user clicks the button/link                                        link = review your project finances
    Then the user should view their non-fec project finances after editing
    And the user should see the element                                        jQuery = p:contains("The partner's finance eligibility has been approved by ")

IFS admin can see the approved non-FEC cost categories in the GOL
    [Documentation]  IFS-9306
    [Setup]  internal user releases the feedback
    Given the user views the grant offer letter page
    When Select Window                                                 NEW
    Then the user should see the non-FEC cost categories in the GOL
    [Teardown]  the user closes the last opened tab

Competition admin can see the approved non-FEC cost categories in the GOL
    [Documentation]  IFS-9306
    Given log in as a different user                                   &{Comp_admin1_credentials}
    And The user clicks the button/link                                jQuery = a:contains("Project setup")
    When the user views the grant offer letter page
    And Select Window                                                  NEW
    Then the user should see the non-FEC cost categories in the GOL
    [Teardown]  the user closes the last opened tab

Project finance user can see the approved non-FEC cost categories in the GOL
    [Documentation]  IFS-9306
    Given log in as a different user                                   &{internal_finance_credentials}
    And The user clicks the button/link                                jQuery = a:contains("Project setup")
    When the user views the grant offer letter page
    And Select Window                                                  NEW
    Then the user should see the non-FEC cost categories in the GOL
    [Teardown]  the user closes the last opened tab

*** Keywords ***
Custom Suite Setup
    Connect to Database                    @{database}
    The user logs-in in new browser        &{ktpLead}
    the user clicks the button/link        link = ${ktpApplication}

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
    moving competition to Closed                         ${ktpCompetitonId}
    log in as a different user                           &{internal_finance_credentials}
    the user closed ktp assesment                        ${ktpCompetitonId}
    the user navigates to the page                       ${server}/project-setup-management/competition/${ktpCompetitonId}/status/all
    the user refreshes until element appears on page     jQuery = tr div:contains("${ktpApplication}")

the user navigates to finance checks
    the user clicks the button/link     jQuery = li:contains("Project in setup") a:contains("${ktpApplication}")
    the user clicks the button/link     link = Finance checks

the user should view their non-fec project finances
    ${leadApplicantCheck}    ${VALUE}=    Run Keyword And Ignore Error Without Screenshots    Element Should Be Visible    jQuery=h2:contains("Detailed finances")
    run keyword if    '${leadApplicantCheck}' == 'PASS'    the user should see the element         jQuery = legend:contains("Will you be using the full economic costing (fEC) funding model?") p:contains("No")
    run keyword if    '${leadApplicantCheck}' == 'FAIL'    the user should see the element         jQuery = h3:contains("Is the knowledge base partner using the fEC funding model?") ~ p:contains("No")
    the user should see the element         jQuery = span:contains("${costValue}") ~ button:contains("Academic and secretarial support")
    the user should see the element         jQuery = th:contains("Total academic and secretarial support costs") ~ td:contains("${costValue}")
    the user should see the element         jQuery = span:contains("${indirectCostTotal}") ~ button:contains("Indirect costs")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("${indirectCostTotal}")
    the user should see the element         jQuery = div:contains("Total project costs") input[value="£${totalProjectCosts}"]
    the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = button:contains("Associates estates costs")
    the user should not see the element     jQuery = button:contains("Additional associate support")

the user should view their non-fec project finances after editing
    the user should see the element         jQuery = table:contains("Funding sought (£)") td:contains("${fundingSought}")
    the user should see the element         jQuery = legend:contains("Will you be using the full economic costing (fEC) funding model?") p:contains("No")
    the user should see the element         jQuery = span:contains("${academicCostValueFormatted}") ~ button:contains("Academic and secretarial support")
    the user should see the element         jQuery = th:contains("Total academic and secretarial support costs") ~ td:contains("${academicCostValueFormatted}")
    the user should see the element         jQuery = span:contains("${indirectCostUpdated}") ~ button:contains("Indirect costs")
    the user should see the element         jQuery = th:contains("Total indirect costs") ~ td:contains("${indirectCostUpdated}")
    the user should see the element         jQuery = div:contains("Total project costs") input[value="£${totalProjectCostsUpdated}"]
    the user should not see the element     jQuery = button:contains("Knowledge base supervisor")
    the user should not see the element     jQuery = button:contains("Associates estates costs")
    the user should not see the element     jQuery = button:contains("Additional associate support")

the user should view the non-fec project finance overview
    the user should see the element         jQuery = h3:contains("Project cost summary")
    the user should see the element         jQuery = tr:contains("Academic and secretarial support") td:contains("${costValue}")
    the user should see the element         jQuery = tr:contains("Indirect costs") td:contains("${indirectCostTotal}")
    the user should see the element         jQuery = tr:contains("Total") td:contains("${totalProjectCosts}")
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
    log in as a different user          &{ifs_admin_user_credentials}
    the user navigates to the page      ${server}/project-setup-management/project/${project_id}/finance-check
    confirm eligibility                 0
    confirm viability                   1
    the user clicks the button/link     jQuery = button:contains("Approve finance checks")

the user edits the Academic and secretarial support costs in project setup
    the user clicks the button/link          jQuery = a:contains("Edit project costs")
    the user enters text to a text field     id = academicAndSecretarialSupportForm   ${academicCostValue}
    the user clicks the button/link          id = save-eligibility

requesting IDs of this project
    ${project_id} =  get project id by name    ${ktpApplication}
    Set suite variable    ${project_id}

requesting organisation IDs
    ${lead_org_id} =    get organisation id by name     ${ktpLeadOrgName}
    Set suite variable      ${lead_org_id}

the user should view updated values in changes to finances
    the user should see the element     jQuery = table:contains("Updated") th:contains("Total costs") ~ td:contains("${totalProjectCostsUpdated}")
    the user should see the element     jQuery = table:contains("Updated") th:contains("Total project costs") ~ td:contains("£${totalProjectCostsUpdated}")
    the user should see the element     jQuery = table:contains("Updated") th:contains("Academic and secretarial support") ~ td:contains("${academicCostValueFormatted}")
    the user should see the element     jQuery = table:contains("Updated") th:contains("Indirect costs") ~ td:contains("${indirectCostUpdated}")
    the user should see the element     jQuery = table:contains("Updated") th:contains("Funding sought") ~ td:contains("${fundingSought}")

the user views the changes to finance screen
    the user clicks the button/link     link = view any changes to finances
    the user should see the element     jQuery = h1:contains("Changes to finances")
    the user clicks the button/link     link = Back to finance checks
    the user clicks the button/link     link = your project finances
    the user clicks the button/link     link = View changes to finances
    the user should see the element     jQuery = h1:contains("Changes to finances")

internal user releases the feedback
    the user assignes project to MO
    the lead applicant submits bank details
    project finance approves bank details
    ifs admin user releases the feedback

the user assignes project to MO
    log in as a different user                 &{ifs_admin_user_credentials}
    the user navigates to the page             ${server}/project-setup-management/competition/${ktpCompetitonId}/status/all
    the user clicks the button/link            jQuery = table:contains("MO") td:contains("Assign") a
    search for MO                              Hermen  Hermen Mermen
    the internal user assign project to MO     ${ktpApplicationId}  ${ktpApplication}

the lead applicant submits bank details
    log in as a different user                             &{ktpLead}
    the user clicks the button/link                        link = ${ktpApplication}
    the user clicks the button/link                        link = Bank details
    the user enters text to a text field                   name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                        id = postcode-lookup
    the user selects the index from the drop-down menu     1  id=addressForm.selectedPostcodeIndex
    applicant user enters bank details

project finance approves bank details
    log in as a different user                              &{internal_finance_credentials}
    the user navigates to the page                          ${server}/management/dashboard/project-setup
    project finance is able to approve the bank details     ${ktpLeadOrgName}

ifs admin user releases the feedback
    log in as a different user                                                &{ifs_admin_user_credentials}
    the user navigates to the page                                            ${server}/management/competition/${ktpCompetitonId}
    the user clicks the button/link                                           link = Input and review funding decision
    the user selects the checkbox                                             app-row-1
    the user clicks the button/link                                           jQuery = button:contains("Successful")
    the user clicks the button/link                                           jQuery = .govuk-back-link:contains("Competition")
    the user clicks the button/link                                           jQuery = a:contains("Manage funding notifications")
    the user selects the checkbox                                             app-row-${ktpApplicationId}
    the user clicks the button/link                                           jQuery = button:contains("Write and send email")
    the internal sends the descision notification email to all applicants     EmailTextBody
    the user clicks the button/link                                           jQuery = .govuk-back-link:contains("Competition")
    the user clicks the button/link                                           jQuery = button:contains("Release feedback")

the user views the grant offer letter page
    the user clicks the button/link     link = ${ktpCompetiton}
    the user clicks the button/link     jQuery = table:contains("GOL") td:contains("Review") a
    the user clicks the button/link     link = View the grant offer letter page (opens in a new window)

the user should see the non-FEC cost categories in the GOL
    the user should see the element     xpath = //td[text()="b. Academic and secretarial support"]/..//td[text()="${academicCostValue}"]
    the user should see the element     xpath = //td[text()="b. Academic and Secretarial Support"]/..//td[text()="${academicCostValue}"]

Custom suite teardown
    Close browser and delete emails
    Disconnect from database
