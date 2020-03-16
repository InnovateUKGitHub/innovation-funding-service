*** Settings ***
Documentation     INFUND-3010 As a partner I want to be able to supply bank details for my business so that Innovate UK can verify its suitability for funding purposes
...
...               INFUND-3282 As a partner I want to be able to supply an existing or new address for my bank account to support the bank details verification process
...
...               INFUND-2621 As a contributor I want to be able to review the current Project Setup status of all partners in my project so I can get an indication of the overall status of the consortium
...
...               INFUND-4903 As a Project Finance team member I want to view a list of the status of all partners' bank details checks so that I can navigate from the internal dashboard
...
...               INFUND-6018 Partner should see a flag in Bank Details, when he needs to take an action
...
...               INFUND-6887 Duplicate validation error message in bank details section of PS
...
...               INFUND-7109 Bank Details Status - Internal user
...
...               INFUND-6482 Extra validation message showing on fields
...
...               INFUND-8276 Content: Bank Details: should not say "each"
...
...               INFUND-8688 Experian response - Error message if wrong bank details are submitted
...
...               IFS-1881 Project Setup internal project dashboard navigation
...
...               IFS-2015 Project Setup task management: Bank details
...
...               IFS-2398 - 2164 Add count of outstanding bank details checks to the task management link
...
...               IFS-2731  PS - External - Submitting Bank details with manually added Oper Address leads to ISE
Suite Setup       the user logs-in in new browser    &{internal_finance_credentials}
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

# Alternative Bank account pair:12345677 - 000004 #
# Another valid B account pair: 51406795 - 404745 #

# Note that the Bank details scenario where the Partner is not eligible for funding
# is tested in the File 01__Project_Details.robot

*** Test Cases ***
Project Finance should not be able to access bank details page
    [Documentation]    INFUND-7090, INFUND-7109
    [Tags]  HappyPath
    Given the user navigates to the page and gets a custom error message   ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/review-all-bank-details    ${403_error_message}
    When the user navigates to the page                                    ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user is not able to access bank details

Applicant user is unable to submit blank and invaild bank details
    [Documentation]   INFUND-3010, INFUND-6018, INFUND-7173, IFS-2731, INFUND-6887, INFUND-6482, INFUND-3282
    [Tags]  HappyPath
    [Setup]  log in as a different user   &{lead_applicant_credentials_bd}
    Given an applicant navigates to the Bank details page
    Then verify Bank details page validation

Applicant user is able to submit bank details
    [Documentation]    INFUND-3010, INFUND-2621, INFUND-7109, INFUND-8688, INFUND-3282
    [Tags]   HappyPath
    Given the user should see the element     jQuery = h1:contains("Bank details")
    Then the applicant user is able to submit bank details

Academic user is able to submit bank details
    [Documentation]    INFUND-3010, INFUND-2621, INFUND 6018, INFUND-8688
    [Tags]   HappyPath
    [Setup]  log in as a different user      &{collaborator2_credentials_bd}
    Given the academic user navigates to the bank details page
    When the academic user is able to enter bank details
    Then the academic user verifies bank details are submitted

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5543
    [Tags]
    [Setup]  log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page   ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the internal user is able to see updated statuses

User sees error response for invalid bank details for non-lead partner
    [Documentation]   INFUND-8688
    [Tags]  HappyPath
    Given log in as a different user               &{collaborator1_credentials_bd}
    When the non-lead partner navigates to the bank details
    Then Verify bank details blank submission page validation for non-lead partner

Non lead partner is able to submit bank details
    [Documentation]    INFUND-3010, INFUND-6018
    [Tags]  HappyPath
    Given the non lead partner submits bank details
    Then the non lead partner confirms bank details have been completed

Bank details verified by Experian require no action by the Project Finance
    [Documentation]  IFS-2495
    [Tags]  HappyPath
    [Setup]  log in as a different user       &{internal_finance_credentials}
    Given the bank details have been verified by the Experian  ${Vitruvius_Id}
    Then the project finance verifies that no further action is required

Project Finance can see the progress of partners bank details
    [Documentation]  INFUND-4903, INFUND-5966, INFUND-5507
    [Tags]
    Given the user navigates to the page            ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then project finance is able to view progress of partners bank details

IFS Admin can see Bank Details
    [Documentation]    INFUND-4903, INFUND-4903, IFS-603, IFS-1881
    [Tags]  HappyPath
    [Setup]  log in as a different user                       &{ifs_admin_user_credentials}
    Given the user navigates to the page                      ${COMP_MANAGEMENT_PROJECT_SETUP}
    When the admin user navigates to All projects
    Then the admin user is able to see bank details

Other internal users do not have access to bank details export
    [Documentation]  INFUND-5852
    [Tags]
    [Setup]  log in as a different user                                 &{Comp_admin1_credentials}
    Given the user navigates to the page                                ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    Then the user should not see the element                            link = Export all bank details
    And the user navigates to the page and gets a custom error message  ${server}/project-setup-management/competition/${PS_Competition_Id}/status/bank-details/export  ${403_error_message}

Project Finance user can export bank details
    [Documentation]  INFUND-5852
    [Tags]
    Given the project finance user downloads the bank details
    Then the user opens the excel and checks the content
    [Teardown]  remove the file from the operating system  bank_details.csv

Project Finance approves Bank Details through the Bank Details list
    [Documentation]    IFS-2015 IFS-2398/2164
    [Tags]  HappyPath
    Given log in as a different user        &{internal_finance_credentials}
    When the user navigates to the page     ${server}/management/dashboard/project-setup
    Then project finance is able to approve the bank details    ${A_B_Cad_Services_Name}
    And the project finance user confirms the approved bank details

*** Keywords ***
The admin user is able to see bank details
    the user should see the element                      css = #table-project-status tr:nth-of-type(4) td.status.action:nth-of-type(5)
    the user clicks the button/link                      css = #table-project-status tr:nth-of-type(4) td.status.action:nth-of-type(5) a
    the user should be redirected to the correct page    ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/review-all-bank-details
    the user should see the element                      jQuery = p:contains("each partner has submitted their bank details")
    the user should see the element                      jQuery = li:contains("${Vitruvius_Name}") .task-status-complete
    the user should see the element                      jQuery = li:contains("${A_B_Cad_Services_Name}") .action-required
    the user should see the element                      jQuery = li:contains("${Armstrong_Butler_Name}") .action-required
    the user clicks the button/link                      link = ${A_B_Cad_Services_Name}
    the user should see the element                      jQuery = .govuk-button:contains("Approve bank account details")

The admin user navigates to All projects
    the user clicks the button/link      jQuery = button:contains("Next")
    the user clicks the button/link      link = ${PS_Competition_Name}
    the user should see the element      link = All projects

Project finance is able to view progress of partners bank details
    the user clicks the button/link                      css = #table-project-status tr:nth-child(4) td:nth-child(6) a
    the user should be redirected to the correct page    ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/review-all-bank-details
    the user should see the element                      jQuery = p:contains("This overview shows whether each partner has submitted their bank details")
    the user should see the element                      jQuery = li:contains("${Vitruvius_Name}") .task-status-complete
    the user should see the element                      jQuery = li:contains("${A_B_Cad_Services_Name}") .action-required
    the user should see the element                      jQuery = li:contains("${Armstrong_Butler_Name}") .action-required
    the user clicks the button/link                      link = ${A_B_Cad_Services_Name}
    the user should see the element                      jQuery = h2:contains("${A_B_Cad_Services_Name} - Account details")
    the user should see the element                      jQuery = p:contains("${Grade_Crossing_Partner_Finance}")
    the user should see the element                      jQuery = p:contains("${Grade_Crossing_Partner_Email}")
    the user goes back to the previous page
    the user clicks the button/link                      link = ${Armstrong_Butler_Name}
    the user should see the element                      jQuery = h2:contains("${Armstrong_Butler_Name} - Account details")
    the user should see the element                      jQuery = p:contains("${Grade_Crossing_Academic_Finance}")
    the user should see the element                      jQuery = p:contains("${Grade_Crossing_Academic_Email}")

The project finance verifies that no further action is required
    the user navigates to the page       ${server}/project-setup-management/project/${Grade_Crossing_Project_Id}/organisation/${Vitruvius_Id}/review-bank-details
    the user should see the element      jQuery = .success-alert:contains("The bank details provided have been approved.")
    the user should not see the element  css = button[data-js-modal = "modal-partner-approve-bank-details"]
    the user navigates to the page       ${server}/project-setup-management/competitions/status/pending-bank-details-approvals
    the user should not see the element  jQuery = td:contains("${Grade_Crossing_Applicaiton_No}") ~ td:contains("${Vitruvius_Name}")

The non lead partner confirms bank details have been completed
    the user should see the element    jQuery = p:contains("The bank account details below are being")
    the user navigates to the page     ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element    css = ul li.complete:nth-child(1)
    the user clicks the button/link    link = View the status of partners
    the user navigates to the page     ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element    jQuery = h1:contains("Project team status")
    the user should see the element    css = #table-project-status tr:nth-of-type(2) td.status.waiting:nth-of-type(5)

The non lead partner submits bank details
    the user enters text to a text field         name = accountNumber  ${Account_One}
    the user enters text to a text field         name = sortCode  ${Sortcode_One}
    the user clicks the button/link              jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link              jquery = button:contains("Cancel")
    the user should not see an error in the page
    the user should not see the element          jQuery = p:contains("The bank account details below are being")
    the user clicks the button/link              jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link              id = submit-bank-details-model-button

Verify bank details blank submission page validation for non-lead partner
    partner fills in his bank details         ${Grade_Crossing_Partner_Email}   ${Grade_Crossing_Project_Id}  00000123  000004
    # Stub is configured to return error response for these values
    wait until keyword succeeds without screenshots  30 s  500 ms  the user should see the element  jQuery = .govuk-error-summary__list:contains("Please check your bank account number and/or sort code.")
    # Added this wait so to give extra execution time

The non-lead partner navigates to the bank details
    The user clicks the button/link           jQuery = .projects-in-setup a:contains("${Grade_Crossing_Application_Title}")
    The user clicks the button/link           link = Bank details

The internal user is able to see updated statuses
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(1).status.ok       # Project details
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(2).status.ok       # Project team
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.waiting  # Docs
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(4)                 # MO
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(5).status.action   # Bank details
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(6).status.action   # Finance checks Spend Profile
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(7)                 #Spend profile
    the user should see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(8).status          # GOL

The academic user verifies bank details are submitted
    the user should see the element           jQuery = p:contains("The bank account details below are being")
    the user navigates to the page            ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element           jQuery = ul li.complete:nth-child(1)
    the user clicks the button/link           link = View the status of partners
    the user navigates to the page            ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element           jQuery = h1:contains("Project team status")
    the user should see the element           css = #table-project-status tr:nth-of-type(3) td.status.waiting:nth-of-type(5)

The academic user is able to enter bank details
    partner fills in his bank details                ${Grade_Crossing_Academic_Email}  ${Grade_Crossing_Project_Id}  00000123  000004
    wait until keyword succeeds without screenshots  30 s  500 ms  the user should see the element  jQuery = .govuk-error-summary__list:contains("Please check your bank account number and/or sort code.")
    the user enters text to a text field             name = accountNumber   ${Account_One}
    the user enters text to a text field             name = sortCode  ${Sortcode_One}
    the user clicks the button/link                  jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                  jquery = button:contains("Cancel")
    the user should not see the element              jQuery = p:contains("The bank account details below are being reviewed")
    the user clicks the button/link                  jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                  id = submit-bank-details-model-button

The academic user navigates to the bank details page
    the user clicks the button/link                    jQuery = .projects-in-setup a:contains("${Grade_Crossing_Application_Title}")
    the user should see the element                    jQuery = li.require-action:contains("Bank details")
    the user clicks the button/link                    link = View the status of partners
    the user should be redirected to the correct page  ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element                    jQuery = h1:contains("Project team status")
    the user should see the element                    css = #table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(5)
    the user clicks the button/link                    link = Set up your project
    the user clicks the button/link                    link = Bank details

The applicant user is able to submit bank details
    applicant user enters bank details
    verify applicant submission is waiting review

Verify applicant submission is waiting review
    the user should see the element                     jQuery = p:contains("The bank account details below are being")
    the user navigates to the page                      ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element                     jQuery = ul li.waiting:nth-child(4)
    the user clicks the button/link                     link = View the status of partners
    the user navigates to the page                      ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element                     jQuery = h1:contains("Project team status")
    the user should see the element                     css = #table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(4)
    log in as a different user                          &{internal_finance_credentials}
    the user navigates to the page                      ${server}/project-setup-management/competition/${PS_Competition_Id}/status
    the user should see the element                     css = #table-project-status tr:nth-of-type(5) td:nth-of-type(2).status.waiting

Verify Bank details page validation
    verify bank details blank submission page validation
    verify bank details invalid submission page validation
    verify bank details postcode lookup validation
    verify bank details experian validations

Verify bank details postcode lookup validation
    the user enters text to a text field                 name = addressForm.postcodeInput    ${EMPTY}
    the user clicks the button/link                      jQuery = .govuk-button:contains("Find UK address")
    the user should see the element                      css = .govuk-form-group--error
    the user enters text to a text field                 name = addressForm.postcodeInput    BS14NT
    the user clicks the button/link                      id = postcode-lookup
    the user selects the index from the drop-down menu   1  id=addressForm.selectedPostcodeIndex

Verify bank details invalid submission page validation
    the user enters text to a text field            name = accountNumber    1234567
    the user moves focus away from the element      name = accountNumber
    the user should see a field error               Please enter a valid account number
    the user should see a field error               This field should contain at least 8 characters.
    the user enters text to a text field            name = accountNumber    12345679
    the user moves focus away from the element      name = accountNumber
    the user should not see the element             jQuery = .govuk-error-message:contains("This field should contain at least 8 characters.")
    the user should not see the element             jQuery = .govuk-error-message:contains("Please enter a valid account number.")
    the user enters text to a text field            name = sortCode    12345
    the user moves focus away from the element      name = sortCode
    the user should see a field error               Please enter a valid sort code.
    the user should see a field error               This field should contain at least 6 characters.
    the user enters text to a text field            name = sortCode    123456
    the user moves focus away from the element      name = sortCode
    the user should not see the element             jQuery = .govuk-error-message:contains("Please enter a sort code.")
    the user should not see the element             jQuery = .govuk-error-message:contains("Please enter a valid sort code.")
    the user should see the element                 jQuery = span:contains("The first line of the address cannot be blank.")

Verify bank details experian validations
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    the user submits the bank account details       12345673    000003
    the user should see the element                 jQuery = .govuk-error-summary__list:contains("Please check your bank account number and/or sort code.")
    the user submits the bank account details       00000123    000004 
    the user should see the element                 jQuery = .govuk-error-summary__list:contains("Please check your bank account number and/or sort code.")

Verify bank details blank submission page validation
    the user clicks the button/link                   jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link                   id = submit-bank-details-model-button
    the user should see a field and summary error     Please enter a valid account number.
    the user should see a field and summary error     Please enter a valid sort code.
    the user should see a field and summary error     Search using a valid postcode or enter the address manually.
    the user enters text to a text field              name = accountNumber    24681012
    the user enters text to a text field              name = sortCode         36912
    the user clicks the button/link                   jQuery = button:contains("Enter address manually")
    the user clicks the button/link                   jQuery = button:contains("Submit bank account details")
    the user clicks the button/link                   id = submit-bank-details-model-button
    the user should see a summary error               The first line of the address cannot be blank.
    the user should see a summary error               The postcode cannot be blank.
    the user should see a summary error               The town cannot be blank.

An applicant navigates to the Bank details page
    the user clicks the button/link     link = ${Grade_Crossing_Application_Title}
    the user should see the element     css = ul li.require-action:nth-child(5)
    the user clicks the button/link     link = View the status of partners
    the user navigates to the page      ${server}/project-setup/project/${Grade_Crossing_Project_Id}/team-status
    the user should see the element     jQuery = h1:contains("Project team status")
    the user should see the element     css = #table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(5)
    the user clicks the button/link     link = Set up your project
    the user should see the element     jQuery = h2:contains("Bank details") ~ p:contains("We need bank details for those partners eligible for funding.")
    the user clicks the button/link     link = Bank details
    the user should see the element     jQuery = .govuk-button:contains("Submit bank account details")
    the user should see the element     jQuery = h1:contains("Bank details")

The user is not able to access bank details
    the user should not see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(5).status.action
    the user should not see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(5).status.waiting
    the user should not see the element   css = #table-project-status tr:nth-of-type(4) td:nth-of-type(5).status.ok

The user moves focus away from the element
    [Arguments]  ${element}
    mouse out    ${element}
    Set Focus To Element        css = .govuk-button[data-js-modal = "modal-bank"]

The user submits the bank account details
    [Arguments]    ${account_number}    ${sort_code}
    the user enters text to a text field  name = accountNumber  ${account_number}
    the user enters text to a text field  name = sortCode  ${sort_code}
    the user clicks the button/link       jQuery = .govuk-button:contains("Submit bank account details")
    the user clicks the button/link       id = submit-bank-details-model-button

The project finance user downloads the bank details
    the user downloads the file  ${internal_finance_credentials["email"]}  ${server}/project-setup-management/competition/${PS_Competition_Id}/status/bank-details/export  ${DOWNLOAD_FOLDER}/bank_details.csv

The user opens the excel and checks the content
    ${contents} =                     read csv file  ${DOWNLOAD_FOLDER}/bank_details.csv
    ${vitruvius_details} =            get from list  ${contents}  6
    ${vitruvius} =                    get from list  ${vitruvius_details}  0
    should be equal                   ${vitruvius}  ${Vitruvius_Name}
    ${Armstrong_Butler_details} =     get from list  ${contents}  8
    ${Armstrong_Butler} =             get from list  ${Armstrong_Butler_details}  0
    should be equal                   ${Armstrong_Butler}  ${Armstrong_Butler_Name}
    ${application_number} =           get from list  ${vitruvius_details}  1
    should be equal                   ${application_number}  ${Grade_Crossing_Applicaiton_No}
    ${postcode} =                     get from list  ${vitruvius_details}  8
    should be equal                   ${postcode}  CH64 3RU
    ${bank_account_name} =            get from list  ${vitruvius_details}  9
    should be equal                   ${bank_account_name}  ${Vitruvius_Name}
    ${bank_account_number} =          get from list  ${vitruvius_details}  10
    should be equal                   ${bank_account_number}  ${Account_Two}
    ${bank_account_sort_code} =       get from list  ${vitruvius_details}  11
    should be equal                   ${bank_account_sort_code}  ${Sortcode_two}

The project finance user confirms the approved Bank Details
    the user navigates to the page         ${server}/project-setup-management/competitions/status/pending-bank-details-approvals
    the user should not see the element    jQuery = a:contains("Dreambit")
    the user navigates to the page         ${server}/project-setup-management/competition/${PS_Competition_Id}/status/all
    the user should see the element        jQuery = tr:contains("Complete") td:nth-child(6) a:contains("Complete")
    the external user is able see approved bank details

the external user is able see approved bank details
    log in as a different user            &{lead_applicant_credentials_bd}
    the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element       jQuery = li:contains("Bank details") .status-complete
    log in as a different user            &{collaborator1_credentials_bd}
    the user navigates to the page        ${server}/project-setup/project/${Grade_Crossing_Project_Id}
    the user should see the element       jQuery = li:contains("Bank details") .status-complete

The bank details have been verified by the Experian
    [Arguments]  ${organisationId}
    Connect to Database  @{database}
    execute sql string  UPDATE `${database_name}`.`bank_details` SET `company_name_score` = 7, `registration_number_matched` = 1, `address_score` = 8, `manual_approval` = 1 WHERE `organisation_id` = '${organisationId}';
    Disconnect from database