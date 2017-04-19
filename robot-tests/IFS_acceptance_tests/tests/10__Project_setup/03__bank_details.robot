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

Suite Setup       finance contacts are submitted by all users
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

# Alternative Bank account pair:12345677 - 000004 #
# Another valid B account pair: 51406795 - 404745 #

# Note that the Bank details scenario where the Partner is not eligible for funding
# and thus doesn't need to fill in his Bank details, will be tested in the File 01__project_details.robot
# whith use of project id = 3 TODO

*** Variables ***

*** Test Cases ***
Links to other sections in Project setup dependent on project details for partners
    [Documentation]    INFUND-4428
    [Tags]
    [Setup]   guest user log-in                   ${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}  ${short_password}
    When the user navigates to the page           ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element           jQuery=ul li.complete:nth-child(1)
    And the user should see the text in the page  Successful application
    Then the user should see the element          link = Monitoring Officer
    And the user should see the element       link = Finance checks
    And the user should not see the element       link= Spend profile
    And the user should not see the element       link = Grant offer letter
    [Teardown]  close any open browsers

Project Finance should not be able to access bank details page
    [Documentation]    INFUND-7090, INFUND-7109
    [Tags]    HappyPath
    [Setup]    Guest user log-in   &{internal_finance_credentials}
    Given the user navigates to the page and gets a custom error message   ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details    You do not have the necessary permissions for your request
    When the user navigates to the page     ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should not see the element   jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status.action
    And the user should not see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status.waiting
    And the user should not see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status.ok

Bank details page
    [Documentation]    INFUND-3010, INFUND-6018, INFUND-7173
    [Tags]    HappyPath
    Given log in as a different user        ${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}  ${short_password}
    When the user clicks the button/link    link=${PS_BD_APPLICATION_HEADER}
    Then the user should see the element    jQuery=ul li.require-action:nth-child(4)
    When the user clicks the button/link    link=status of my partners
    Then the user navigates to the page     ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page    Project team status
    And the user should see the element     jQuery=#table-project-status tr:nth-of-type(1) td.status.action:nth-of-type(3)
    And the user clicks the button/link     link=Project setup status
    And the user should see the text in the page   We need bank details for those partners eligible for funding
    And the user clicks the button/link     link=Bank details
    Then the user should see the element    jQuery=.button:contains("Submit bank account details")
    And the user should see the text in the page    Bank account

Bank details server side validations
    [Documentation]    INFUND-3010
    [Tags]
    When the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
    Then the user should see an error    Please enter an account number.
    And the user should see an error    Please enter a sort code.
    And the user should see an error    You need to select a billing address before you can continue.

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
    Then the user should not see the text in the page    Please enter an account number.
    And the user should see the text in the page    Please enter a valid account number.
    When the user enters text to a text field    name=accountNumber    12345679
    And the user moves focus away from the element    name=accountNumber
    Then the user should not see the text in the page    Please enter an account number.
    And the user should not see the text in the page    Please enter a valid account number.
    And the user should not see the text in the page    Please correct this field
    When the user enters text to a text field    name=sortCode    12345
    And the user moves focus away from the element    name=sortCode
    Then the user should see an error    Please enter a valid sort code.
    When the user enters text to a text field    name=sortCode    abcde
    And the user moves focus away from the element    name=sortCode
    Then the user should see an error    Please enter a valid sort code.
    When the user enters text to a text field    name=sortCode    123456
    And the user moves focus away from the element    name=sortCode
    Then the user should not see the text in the page    Please enter a sort code.
    And the user should not see the text in the page    Please enter a valid sort code.
    When the user selects the radio button    addressType    REGISTERED
    Then the user should not see the text in the page    You need to select a billing address before you can continue.

Bank account postcode lookup
    [Documentation]    INFUND-3282
    [Tags]    HappyPath
    When the user selects the radio button    addressType    ADD_NEW
    And the user enters text to a text field    name=addressForm.postcodeInput    ${EMPTY}
    And the user clicks the button/link    jQuery=.button:contains("Find UK address")
    Then the user should see the element    css=.error
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
    Then the user should see the text in the page    Bank details cannot be validated. 
    And the user should see the text in the page     please check your account number 
    And the user should see the text in the page     please check your sort code 

Bank details submission
    [Documentation]    INFUND-3010, INFUND-2621, INFUND-7109, INFUND-8688
    [Tags]    Experian    HappyPath
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    Given the user enters text to a text field        name=accountNumber    00000123
    When the user enters text to a text field         name=sortCode    000004
    Then the user clicks the button/link              jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link               jquery=button:contains("Submit")
    Then the user should see the text in the page     Bank details cannot be validated. 
    And the user should see the text in the page      please check your account number 
    And the user should see the text in the page      please check your sort code 
    When the user enters text to a text field         name=accountNumber    12345677
    And the user enters text to a text field          name=sortCode    000004
    When the user clicks the button/link              jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link               jquery=button:contains("Cancel")
    And the user should not see the text in the page  The bank account details below are being reviewed
    When the user clicks the button/link              jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link               jquery=button:contains("Submit")
    And the user should see the text in the page      The bank account details below are being reviewed
    Then the user navigates to the page               ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element               jQuery=ul li.waiting:nth-child(4)
    When the user clicks the button/link              link=status of my partners
    Then the user navigates to the page               ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page      Project team status
    And the user should see the element               jQuery=#table-project-status tr:nth-of-type(1) td.status.waiting:nth-of-type(3)
    When log in as a different user                   &{internal_finance_credentials}
    And the user navigates to the page               ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should see the element              jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.action

Bank details for Academic
    [Documentation]    INFUND-3010, INFUND-2621, INFUND 6018, INFUND-8688
    [Tags]    Experian    HappyPath
    # Please note that the bank details for these Experian tests are dummy data specifically chosen to elicit certain responses from the stub.
    Given log in as a different user               ${PS_BD_APPLICATION_ACADEMIC_EMAIL}  ${short_password}
    When the user clicks the button/link           link=${PS_BD_APPLICATION_HEADER}
    Then the user should see the element           jQuery=ul li.require-action:nth-child(4)
    When the user clicks the button/link           link=status of my partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            jQuery=#table-project-status tr:nth-of-type(3) td.status.action:nth-of-type(3)
    And the user clicks the button/link            link=Project setup status
    And the user clicks the button/link            link=Bank details
    When the user enters text to a text field       name=accountNumber    00000123
    Then the user enters text to a text field       name=sortCode    000004
    And the user clicks the button/link             jQuery=div:nth-child(2) label.selection-button-radio[for="address-use-org"]
    Then the user clicks the button/link            jQuery=div:nth-child(2) label.selection-button-radio[for="address-use-org"]
    When the user clicks the button/link            jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link             jquery=button:contains("Submit")
    Then the user should see the text in the page   Bank details cannot be validated. 
    And the user should see the text in the page    please check your account number 
    And the user should see the text in the page    please check your sort code 
    When the user enters text to a text field      name=accountNumber  51406795
    And the user enters text to a text field       name=sortCode  404745
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
    And the user clicks the button/link            jquery=button:contains("Submit")
    And the user should see the text in the page   The bank account details below are being reviewed
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element            jQuery=ul li.complete:nth-child(2)
    When the user clicks the button/link           link=status of my partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            jQuery=#table-project-status tr:nth-of-type(3) td.status.waiting:nth-of-type(3)

Status updates correctly for internal user's table
    [Documentation]    INFUND-4049, INFUND-5543
    [Tags]      HappyPath
    [Setup]    log in as a different user  &{Comp_admin1_credentials}
    When the user navigates to the page    ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should see the element   jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(1).status.ok       # Project details
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(2).status.action   # MO
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(3).status.action   # Bank details
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(4).status.action   # Finance checks
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(5).status          # Spend Profile
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(6).status.waiting  # Other Docs
    And the user should see the element    jQuery=#table-project-status tr:nth-of-type(2) td:nth-of-type(7).status          # GOL

Bank details for non-lead partner
    [Documentation]    INFUND-3010, INFUND-6018, INFUND-8688
    [Tags]    HappyPath
    #TODO pending due to INFUND-6090  Update with new Bank account pair
    Given log in as a different user               ${PS_BD_APPLICATION_PARTNER_EMAIL}  ${short_password}
    When the user clicks the button/link           link=${PS_BD_APPLICATION_HEADER}
    Then the user should see the element           jQuery=ul li.require-action:nth-child(4)
    And the user clicks the button/link            link=status of my partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            jQuery=#table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(3)
    And the user clicks the button/link            link=Project setup status
    Then the user should see the element           link=Bank details
    When the user clicks the button/link           link=Bank details
    Then the user should see the text in the page  Bank account
    When the user enters text to a text field      name=accountNumber    00000123
    Then the user enters text to a text field      name=sortCode    000004
    And the user clicks the button/link            jQuery=div:nth-child(2) label.selection-button-radio[for="address-use-org"]
    Then the user clicks the button/link            jQuery=div:nth-child(2) label.selection-button-radio[for="address-use-org"]
    When the user clicks the button/link           jQuery=.button:contains("Submit bank account details")
    And the user clicks the button/link            jquery=button:contains("Submit")
    Then the user should see the text in the page  Bank details cannot be validated. 
    And the user should see the text in the page   please check your account number 
    And the user should see the text in the page   please check your sort code 
    When the user enters text to a text field      name=accountNumber  51406795
    Then the user enters text to a text field      name=sortCode  404745
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
    And the user clicks the button/link            jQuery=.button:contains("Submit")
    And the user should see the element            jQuery=p:contains("The bank account details below are being reviewed")
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}
    And the user should see the element            jQuery=ul li.complete:nth-child(2)
    When the user clicks the button/link           link=status of my partners
    Then the user navigates to the page            ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/team-status
    And the user should see the text in the page   Project team status
    And the user should see the element            jQuery=#table-project-status tr:nth-of-type(2) td.status.waiting:nth-of-type(3)

Project Finance can see the progress of partners bank details
    [Documentation]  INFUND-4903, INFUND-5966, INFUND-5507
    [Tags]    HappyPath
    [Setup]  log in as a different user             &{internal_finance_credentials}
    Given the user navigates to the page            ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    And the user clicks the button/link             jQuery=#table-project-status tr:nth-child(2) td:nth-child(4) a
    Then the user navigates to the page             ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details
    And the user should see the text in the page    This overview shows whether each partner has submitted their bank details
    Then the user should see the element            jQuery=li:nth-child(1):contains("Review required")
    And the user should see the element             jQuery=li:nth-child(2):contains("Review required")
    And the user should see the element             jQuery=li:nth-child(3):contains("Review required")
    When the user clicks the button/link            link=${Eadel_Name}
    Then the user should see the text in the page   ${Eadel_Name} - Account details
    And the user should see the text in the page    ${PS_BD_APPLICATION_LEAD_FINANCE}
    And the user should see the element             jQuery=a:contains("${PS_BD_APPLICATION_PM_EMAIL}")
    And the user should see the text in the page    ${PS_BD_APPLICATION_LEAD_TELEPHONE}
    And the user goes back to the previous page
    When the user clicks the button/link            link=${Bluezoom_Name}
    Then the user should see the text in the page   ${Bluezoom_Name} - Account details
    And the user should see the text in the page    Ryan Welch
    And the user should see the text in the page    ${PS_BD_APPLICATION_PARTNER_EMAIL}
    And the user goes back to the previous page
    When the user clicks the button/link            link=${Npath_Name}
    Then the user should see the text in the page   ${Npath_Name} - Account details
    And the user should see the text in the page    ${PS_BD_APPLICATION_ACADEMIC_FINANCE}
    And the user should see the text in the page    ${PS_BD_APPLICATION_ACADEMIC_EMAIL}
    Then the user clicks the button/link            link=Bank details
    [Teardown]  the user clicks the button/link     link=Projects in setup

Project Finance can see Bank Details
    [Documentation]    INFUND-4903, INFUND-4903
    [Tags]  HappyPath
    Given the user navigates to the page          ${COMP_MANAGEMENT_PROJECT_SETUP}
    And the user clicks the button/link           link=${PS_BD_Competition_Name}
    Then the user should see the element          jQuery=h2:contains("Projects in setup")
    And the user should see the element           jQuery=#table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(3)
    When the user clicks the button/link          jQuery=#table-project-status tr:nth-of-type(2) td.status.action:nth-of-type(3) a
    Then the user navigates to the page           ${server}/project-setup-management/project/${PS_BD_APPLICATION_PROJECT}/review-all-bank-details
    And the user should see the text in the page  each partner has submitted their bank details
    Then the user should see the element          jQuery=li:nth-child(1):contains("Review required")
    And the user should see the element           jQuery=li:nth-child(2):contains("Review required")
    And the user should see the element           jQuery=li:nth-child(1) a:contains("${Eadel_Name}")
    And the user should see the element           jQuery=li:nth-child(3):contains("Review required")
    When the user clicks the button/link          link=${Eadel_Name}
    Then the user should see the element          jQuery=.button:contains("Approve bank account details")

Other internal users do not have access to bank details export
    [Documentation]  INFUND-5852
    [Tags]
    [Setup]  log in as a different user       &{Comp_admin1_credentials}
    When the user navigates to the page       ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status
    Then the user should not see the element  link=Export all bank details
    And the user navigates to the page and gets a custom error message  ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status/bank-details/export  You do not have the necessary permissions for your request

Project Finance user can export bank details
    [Documentation]  INFUND-5852
    [Tags]  Download
    When the project finance user downloads the bank details
    Then the user opens the excel and checks the content
    [Teardown]  remove the file from the operating system  bank_details.csv

*** Keywords ***
the user moves focus away from the element
    [Arguments]    ${element}
    mouse out    ${element}
    focus    jQuery=.button:contains("Submit bank account details")

the user submits the bank account details
    [Arguments]    ${account_number}    ${sort_code}
    the user enters text to a text field    name=accountNumber    ${account_number}
    the user enters text to a text field    name=sortCode    ${sort_code}
    the user clicks the button/link    jQuery=.button:contains("Submit bank account details")
    the user clicks the button/link    jQuery=.button:contains("Submit")

finance contacts are submitted by all users
    user submits his finance contacts  ${PS_BD_APPLICATION_ACADEMIC_EMAIL}  ${Npath_Id}
    user submits his finance contacts  ${PS_BD_APPLICATION_PARTNER_EMAIL}  ${Bluezoom_Id}
    user submits his finance contacts  ${PS_BD_APPLICATION_LEAD_PARTNER_EMAIL}  ${Eadel_Id}
    logout as user
    close any open browsers

user submits his finance contacts
    [Arguments]  ${user}  ${id}
    guest user log-in  ${user}  ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_BD_APPLICATION_PROJECT}/details/finance-contact?organisation=${id}
    the user selects the radio button  financeContact  financeContact1
    the user clicks the button/link    jQuery=.button:contains("Save")

the project finance user downloads the bank details
    the user downloads the file    ${internal_finance_credentials["email"]}    ${server}/project-setup-management/competition/${PS_BD_Competition_Id}/status/bank-details/export    ${DOWNLOAD_FOLDER}/bank_details.csv

the user opens the excel and checks the content
    ${contents}=                read csv file  ${DOWNLOAD_FOLDER}/bank_details.csv
    ${eadel_details}=           get from list  ${contents}  7
    ${eadel}=                   get from list  ${eadel_details}  0
    should be equal             ${eadel}  ${Eadel_Name}
    ${Npath_details}=           get from list  ${contents}  8
    ${Npath}=                   get from list  ${Npath_details}  0
    should be equal             ${Npath}  ${Npath_Name}
    ${application_number}=      get from list  ${eadel_details}  1
    should be equal             ${application_number}  ${PS_BD_APPLICATION_NUMBER}
    ${postcode}=                get from list  ${eadel_details}  8
    should be equal             ${postcode}  CH64 3RU
    ${bank_account_name}=       get from list  ${eadel_details}  9
    should be equal             ${bank_account_name}  ${Eadel_Name}
    ${bank_account_number}=     get from list  ${eadel_details}  10
    should be equal             ${bank_account_number}  12345677
    ${bank_account_sort_code}=  get from list  ${eadel_details}  11
    should be equal             ${bank_account_sort_code}  000004
