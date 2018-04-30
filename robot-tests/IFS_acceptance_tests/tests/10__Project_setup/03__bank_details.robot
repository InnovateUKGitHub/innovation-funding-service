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
Suite Setup       finance contacts are submitted by all users
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          PS_Common.robot

# Alternative Bank account pair:12345677 - 000004 #
# Another valid B account pair: 51406795 - 404745 #

# Note that the Bank details scenario where the Partner is not eligible for funding
# is tested in the File 01__project_details.robot

*** Variables ***
&{lead_applicant_credentials_bd}  email=${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}  password=${short_password}
&{collaborator1_credentials_bd}   email=${PS_BD_APPLICATION_PARTNER_EMAIL}  password=${short_password}
&{collaborator2_credentials_bd}   email=${PS_BD_APPLICATION_ACADEMIC_EMAIL}  password=${short_password}

*** Test Cases ***
Links to other sections in Project setup dependent on project details for partners
    [Documentation]    INFUND-4428
    [Tags]    HappyPath
    When the user navigates to the page           ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    Then the user should see the element          link = Monitoring Officer
    And the user should see the element       link = Finance checks
    And the user should not see the element       link= Spend profile
    And the user should not see the element       link = Grant offer letter

Project Finance should not be able to access bank details page
    [Documentation]    INFUND-7090, INFUND-7109
    [Tags]    HappyPath
    [Setup]    log in as a different user   &{internal_finance_credentials}
    Given the user navigates to the page and gets a custom error message   ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details    ${403_error_message}
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should not see the element   css=#table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.action
    And the user should not see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.waiting
    And the user should not see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.ok

Bank details page
    [Documentation]    INFUND-3010, INFUND-6018, INFUND-7173
    [Tags]    HappyPath
    Given log in as a different user        ${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}  ${short_password}
    When the user clicks the button/link    link=${PS_BD_APPLICATION_TITLE}
    Then the user should see the element    css=ul li.require-action:nth-child(3)
    When the user clicks the button/link    link=View the status of partners
    Then the user navigates to the page     ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element     css=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)
    And the user clicks the button/link     link=Project setup status
    And the user should see the text in the page   We need bank details for those partners eligible for funding
    And the user clicks the button/link     link=Bank details
    Then the user should see the element    jQuery=.button:contains("Submit bank account details")
    And the user should see the text in the page    Bank account

Bank details server side validations
    [Documentation]    INFUND-3010
    [Tags]
    When the user clicks the button/link  jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link   id=submit-bank-details
    Then the user should see an error     Please enter a valid account number.
    And the user should see an error      Please enter a valid sort code.
    And the user should see an error      You need to select an address before you can continue.

The user enters bank details, wants to manually enter their address, leaves the address empty and submits
    [Documentation]  IFS-2731
    [Tags]
    Given the user enters text to a text field    name = accountNumber    24681012
    And the user enters text to a text field      name = sortCode         36912
    When the user selects the radio button        addressType             ADD_NEW
    And the user clicks the button/link           css = button[name = "manual-address"]        #Enter address manually
    Then the user clicks the button/link          jQuery = button:contains("Submit bank account details")
    And the user clicks the button/link           id=submit-bank-details    #Submit
    And the user should see a summary error       The address cannot be blank.
    And the user should see a summary error       The postcode cannot be blank.
    And the user should see a summary error       The town cannot be blank.

Bank details client side validations
    [Documentation]    INFUND-3010, INFUND-6887, INFUND-6482
    [Tags]    HappyPath
    When the user enters text to a text field    name=accountNumber    1234567
    And the user moves focus away from the element    name=accountNumber
    Then the user should not see the text in the page    Please enter an account number.
    And the user should not see the text in the page    Please correct this field
    And the user should see an error    Please enter a valid account number
    When the user enters text to a text field    name=accountNumber    abcdefgh
    And the user moves focus away from the element    name=accountNumber
    Then the user should see the text in the page    Please enter an account number.
    When the user enters text to a text field    name=accountNumber    12345679
    And the user moves focus away from the element    name=accountNumber
    Then the user should not see the text in the page    Please enter an account number.
    And the user should not see the text in the page    Please enter a valid account number.
    And the user should not see the text in the page    Please correct this field
    When the user enters text to a text field    name=sortCode    12345
    And the user moves focus away from the element    name=sortCode
    Then the user should see an error    Please enter a valid sort code.
    When the user enters text to a text field    name=sortCode    abcdef
    And the user moves focus away from the element    name=sortCode
    Then the user should see the text in the page    Please enter a sort code.
    When the user enters text to a text field    name=sortCode    123456
    And the user moves focus away from the element    name=sortCode
    Then the user should not see the text in the page    Please enter a sort code.
    And the user should not see the text in the page    Please enter a valid sort code.
    When the user selects the radio button    addressType    REGISTERED
    Then the user should not see the text in the page    You need to select an address before you can continue.

Bank account postcode lookup
    [Documentation]    INFUND-3282
    [Tags]    HappyPath
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    name=addressForm.postcodeInput    ${EMPTY}
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    css=.form-group-error
    When the user enters text to a text field    name=addressForm.postcodeInput    BS14NT/
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    name=addressForm.selectedPostcodeIndex
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    id=addressForm.postcodeInput    BS14NT
    And the user clicks the button/link    id=postcode-lookup
    Then the user should see the element    css=#select-address-block
    And the user clicks the button/link    css=#select-address-block > button
    And the address fields should be filled

Bank details experian validations
    [Documentation]    INFUND-3010, INFUND-8688
    [Tags]    Experian
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    When the user submits the bank account details    12345673    000003
    Then the user should see the text in the page    Bank account details are incorrect, please check and try again
    When the user submits the bank account details    00000123    000004 
    Then the user should see the element  jQuery=.error-summary-list:contains("Bank details cannot be validated.")

Bank details submission
    [Documentation]    INFUND-3010, INFUND-2621, INFUND-7109, INFUND-8688
    [Tags]    Experian    HappyPath
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    Given the user submits the bank account details   00000123    000004 
    Then the user should see the element              jQuery=.error-summary-list:contains("Bank details cannot be validated.")
    When the user enters text to a text field         name=accountNumber  ${account_two}
    And the user enters text to a text field          name=sortCode  ${sortCode_two}
    When the user clicks the button/link              jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link               jquery=button:contains("Cancel")
    And the user should not see the text in the page  The bank account details below are being reviewed
    When the user clicks the button/link              jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link               id=submit-bank-details
    And the user should see the text in the page      The bank account details below are being reviewed
    Then the user navigates to the page               ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element               jQuery=ul li.waiting:nth-child(3)
    When the user clicks the button/link              link=View the status of partners
    Then the user navigates to the page               ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page      Project team status
    And the user should see the element               css=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)
    When log in as a different user                   &{internal_finance_credentials}
    And the user navigates to the page               ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should see the element              css=#table-project-status tr:nth-of-type(4) td:nth-of-type(2).status.action

Submission of bank details for academic user
    [Documentation]    INFUND-3010, INFUND-2621, INFUND 6018, INFUND-8688
    [Tags]    Experian    HappyPath
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    Given log in as a different user               ${PS_BD_APPLICATION_ACADEMIC_EMAIL}  ${short_password}
    When the user clicks the button/link           jQuery=.projects-in-setup a:contains("${PS_BD_APPLICATION_TITLE}")
    Then the user should see the element           jQuery=li.require-action:contains("Bank details")
    When the user clicks the button/link           link=View the status of partners
    Then the user should be redirected to the correct page  ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the element            jQuery=h1:contains("Project team status")
    And the user should see the element            css=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(3)
    And the user clicks the button/link            link=Project setup status
    And the user clicks the button/link            link=Bank details
    When partner fills in his bank details         ${PS_BD_APPLICATION_ACADEMIC_EMAIL}  ${PS_BD_APPLICATION_PROJECT}  00000123  000004
    Then wait until keyword succeeds without screenshots  30 s  500 ms  the user should see the element  jQuery=.error-summary-list li:contains("Bank details cannot be validated.")
    # Added this wait so to give extra execution time
    When the user enters text to a text field      name=accountNumber   ${account_one}
    And the user enters text to a text field       name=sortCode  ${sortCode_one}
    When the user selects the radio button         addressType  ADD_NEW
    And the user enters text to a text field       id=addressForm.postcodeInput  BS14NT
    And the user clicks the button/link            id=postcode-lookup
    Then the user should see the element           css=#select-address-block
    And the user clicks the button/link            css=#select-address-block > button
    And the address fields should be filled
    When the user clicks the button/link           jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link            jquery=button:contains("Cancel")
    And the user should not see the text in the page  The bank account details below are being reviewed
    When the user clicks the button/link           jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link            id=submit-bank-details
    And the user should see the text in the page   The bank account details below are being reviewed
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element            jQuery=ul li.complete:nth-child(1)
    When the user clicks the button/link           link=View the status of partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            css=#table-project-status tr:nth-of-type(3) td.status.waiting:nth-of-type(3)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5543
    [Tags]      HappyPath
    [Setup]    log in as a different user  &{Comp_admin1_credentials}
    When the user navigates to the page    ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should see the element   css=#table-project-status tr:nth-of-type(4) td:nth-of-type(1).status.ok       # Project details
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(2).status.action   # MO
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(3).status.action   # Bank details
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(4).status.action   # Finance checks
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(5).status          # Spend Profile
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(6).status.waiting  # Other Docs
    And the user should see the element    css=#table-project-status tr:nth-of-type(4) td:nth-of-type(7).status          # GOL

User sees error response for invalid bank details for non-lead partner
    [Documentation]   INFUND-8688
    [Tags]    HappyPath
    Given log in as a different user               ${PS_BD_APPLICATION_PARTNER_EMAIL}  ${short_password}
    When the user clicks the button/link           jQuery=.projects-in-setup a:contains("${PS_BD_APPLICATION_TITLE}")
    Then the user clicks the button/link           link=Bank details
    When partner fills in his bank details         ${PS_BD_APPLICATION_PARTNER_EMAIL}  ${PS_BD_APPLICATION_PROJECT}  00000123  000004
    # Stub is configured to return error response for these values
    Then wait until keyword succeeds without screenshots  30 s  500 ms  the user should see the element  jQuery=.error-summary-list li:contains("Bank details cannot be validated.")
    # Added this wait so to give extra execution time

Non lead partner submits bank details
    [Documentation]    INFUND-3010, INFUND-6018
    [Tags]    HappyPath
    When the user enters text to a text field      name=accountNumber  ${account_one}
    Then the user enters text to a text field      name=sortCode  ${sortCode_one}
    When the user selects the radio button         addressType  ADD_NEW
    Then the user enters text to a text field      id=addressForm.postcodeInput  BS14NT
    And the user clicks the button/link            id=postcode-lookup
    And the user clicks the button/link            jQuery=.button:contains("Use selected address")
    And the address fields should be filled
    When the user clicks the button/link           jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link            jquery=button:contains("Cancel")
    Then the user should not see an error in the page
    And the user should not see the text in the page  The bank account details below are being reviewed
    When the user clicks the button/link           jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link            id=submit-bank-details
    And the user should see the element            jQuery=p:contains("The bank account details below are being reviewed")
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element            css=ul li.complete:nth-child(1)
    When the user clicks the button/link           link=View the status of partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            css=#table-project-status tr:nth-of-type(2) td.status.waiting:nth-of-type(3)

Bank details verified by Experian require no action by the Project Finance
    [Documentation]  IFS-2495
    [Tags]  HappyPath  MySQL
    [Setup]  log in as a different user      &{internal_finance_credentials}
    Given the bank details have been verified by the Experian  ${Vitruvius_Id}
    When the user navigates to the page      ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/organisation/${Vitruvius_Id}/review-bank-details
    Then the user should see the element     jQuery=.success-alert:contains("The bank details provided have been approved.")
    And the user should not see the element  css=button[data-js-modal="modal-partner-approve-bank-details"]
    When the user navigates to the page      ${server}/project-setup-management/competitions/status/pending-bank-details-approvals
    Then the user should not see the element  jQuery=td:contains("${PS_BD_APPLICATION_NUMBER}") ~ td:contains("${Vitruvius_Name}")

Project Finance can see the progress of partners bank details
    [Documentation]  INFUND-4903, INFUND-5966, INFUND-5507
    [Tags]    HappyPath
    Given the user navigates to the page            ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    And the user clicks the button/link             css=#table-project-status tr:nth-child(4) td:nth-child(4) a
    Then the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details
    And the user should see the text in the page    This overview shows whether each partner has submitted their bank details
    Then the user should see the element            jQuery=li:contains("${Vitruvius_Name}") .task-status-complete
    And the user should see the element             jQuery=li:contains("${A_B_Cad_Services_Name}") .action-required
    And the user should see the element             jQuery=li:contains("${Armstrong_Butler_Name}") .action-required
    When the user clicks the button/link            link=${A_B_Cad_Services_Name}
    Then the user should see the text in the page   ${A_B_Cad_Services_Name} - Account details
    And the user should see the text in the page    Ryan Welch
    And the user should see the text in the page    ${PS_BD_APPLICATION_PARTNER_EMAIL}
    And the user goes back to the previous page
    When the user clicks the button/link            link=${Armstrong_Butler_Name}
    Then the user should see the text in the page   ${Armstrong_Butler_Name} - Account details
    And the user should see the text in the page    ${PS_BD_APPLICATION_ACADEMIC_FINANCE}
    And the user should see the text in the page    ${PS_BD_APPLICATION_ACADEMIC_EMAIL}

IFS Admin can see Bank Details
    [Documentation]    INFUND-4903, INFUND-4903, IFS-603, IFS-1881
    [Tags]  HappyPath
    [Setup]  log in as a different user            &{ifs_admin_user_credentials}
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link=${PS_BD_Competition_Name}
    Then the user should see the element          link=All projects
    And the user should see the element           css=#table-project-status tr:nth-of-type(4) td.status.action:nth-of-type(3)
    When the user clicks the button/link          css=#table-project-status tr:nth-of-type(4) td.status.action:nth-of-type(3) a
    Then the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details
    And the user should see the text in the page  each partner has submitted their bank details
    Then the user should see the element          jQuery=li:contains("${Vitruvius_Name}") .task-status-complete
    And the user should see the element           jQuery=li:contains("${A_B_Cad_Services_Name}") .action-required
    And the user should see the element           jQuery=li:contains("${Armstrong_Butler_Name}") .action-required
    When the user clicks the button/link          link=${A_B_Cad_Services_Name}
    Then the user should see the element          jQuery=.button:contains("Approve bank account details")

Other internal users do not have access to bank details export
    [Documentation]  INFUND-5852
    [Tags]
    [Setup]  log in as a different user       &{Comp_admin1_credentials}
    When the user navigates to the page       ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should not see the element  link=Export all bank details
    And the user navigates to the page and gets a custom error message  ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status/bank-details/export  ${403_error_message}

Project Finance user can export bank details
    [Documentation]  INFUND-5852
    [Tags]  Download
    When the project finance user downloads the bank details
    Then the user opens the excel and checks the content
    [Teardown]  remove the file from the operating system  bank_details.csv

Project Finance approves Bank Details through the Bank Details list
    [Documentation]    IFS-2015 IFS-2398/2164
    [Tags]
    Given log in as a different user    &{internal_finance_credentials}
    And the user navigates to the page    ${server}/management/dashboard/project-setup
    And the user clicks the button/link  jQuery=a:contains("Review bank details")
    When the user clicks the button/link    jQuery=a:contains("Dreambit")
    And the user clicks the button/link    jQuery=button:contains("Approve bank account details")
    And the user clicks the button/link    jQuery=button:contains("Approve account")
    Then the user should see the element    jQuery=h2:contains("The bank details provided have been approved.")
    And the project finance user confirms the approved bank details

*** Keywords ***
the user moves focus away from the element
    [Arguments]  ${element}
    mouse out    ${element}
    focus        css=.button[data-js-modal="modal-bank"]

the user submits the bank account details
    [Arguments]    ${account_number}    ${sort_code}
    the user enters text to a text field  name=accountNumber  ${account_number}
    the user enters text to a text field  name=sortCode  ${sort_code}
    the user clicks the button/link       jQuery=.button:contains("Submit bank account details")
    the user clicks the button/link       id=submit-bank-details

finance contacts are submitted by all users
    the user logs-in in new browser            &{lead_applicant_credentials_bd}
    the partner submits their finance contact  ${Vitruvius_Id}  ${PS_BD_APPLICATION_PROJECT}  &{lead_applicant_credentials_bd}
    the partner submits their finance contact  ${A_B_Cad_Services_Id}  ${PS_BD_APPLICATION_PROJECT}  &{collaborator1_credentials_bd}
    the partner submits their finance contact  ${Armstrong_Butler_Id}  ${PS_BD_APPLICATION_PROJECT}  &{collaborator2_credentials_bd}

the project finance user downloads the bank details
    the user downloads the file  ${internal_finance_credentials["email"]}  ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status/bank-details/export  ${DOWNLOAD_FOLDER}/bank_details.csv

the user opens the excel and checks the content
    ${contents}=                    read csv file  ${DOWNLOAD_FOLDER}/bank_details.csv
    ${vitruvius_details}=           get from list  ${contents}  3
    ${vitruvius}=                   get from list  ${vitruvius_details}  0
    should be equal                 ${vitruvius}  ${Vitruvius_Name}
    ${Armstrong_Butler_details}=    get from list  ${contents}  5
    ${Armstrong_Butler}=            get from list  ${Armstrong_Butler_details}  0
    should be equal                 ${Armstrong_Butler}  ${Armstrong_Butler_Name}
    ${application_number}=          get from list  ${vitruvius_details}  1
    should be equal                 ${application_number}  ${PS_BD_APPLICATION_NUMBER}
    ${postcode}=                    get from list  ${vitruvius_details}  8
    should be equal                 ${postcode}  CH64 3RU
    ${bank_account_name}=           get from list  ${vitruvius_details}  9
    should be equal                 ${bank_account_name}  ${Vitruvius_Name}
    ${bank_account_number}=         get from list  ${vitruvius_details}  10
    should be equal                 ${bank_account_number}  ${account_two}
    ${bank_account_sort_code}=      get from list  ${vitruvius_details}  11
    should be equal                 ${bank_account_sort_code}  ${sortCode_two}

The project finance user confirms the approved Bank Details
    the user navigates to the page    ${server}/project-setup-management/competitions/status/pending-bank-details-approvals
    the user should not see the element    jQuery=a:contains("Dreambit")
    the user navigates to the page    ${server}/project-setup-management/competition/${PS_SP_Competition_Id}/status/all
    the user should see the element    jQuery=tr:contains("Complete") td:nth-child(4) a:contains("Complete")

the bank details have been verified by the Experian
    [Arguments]  ${organisationId}
    Connect to Database  @{database}
    execute sql string  UPDATE `${database_name}`.`bank_details` SET `company_name_score`=7, `registration_number_matched`=1, `address_score`=8, `manual_approval`=1 WHERE `organisation_id`='${organisationId}';