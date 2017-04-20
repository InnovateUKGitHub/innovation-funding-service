*** Settings ***
Documentation     INFUND-3763 As a project finance team member I want to receive feedback from Experian regarding a partners' bank account details
...
...               INFUND-4054 As a Project Finance team member I want to be able to review and amend unverified partner bank details to ensure they are suitable for approval
...
...               INFUND-4903: As a Project Finance team member I want to view a list of the status of all partners' bank details checks so that I can navigate from the internal dashboard
...
...               INFUND-6714 Proj finance cannot change account details
...
...               INFUND-7161 If browser back button is used bank account details can be changed again by IUK inspite of being approved once
...
...               INFUND-7109 Bank Details Status - Internal user
...
...               INFUND-5899 As an internal user I want to be able to use the breadcrumb navigation consistently throughout Project Setup so I can return to the previous page as appropriate
...
...               INFUND-9061 Internal server error on bank details being approved multiple times by proj finance
Suite Setup       all preliminary steps are completed
Suite Teardown    the user closes the browser
Force Tags        Experian    Project Setup
Resource          ../../resources/defaultResources.robot
Resource          PS_Variables.robot

*** Variables ***

*** Test Cases ***
Project Finance can see Bank details requiring action
    [Documentation]    INFUND-3763, INFUND-4903
    [Tags]    HappyPath
    [Setup]  guest user log-in            &{internal_finance_credentials}
    Given the user navigates to the page  ${server}/management/dashboard/project-setup
    When the user clicks the button/link  link=${PS_EF_Competition_Name}
    Then the user should see the element  jQuery=#table-project-status tr:nth-child(2) td:nth-child(2).status.ok
    And the user should see the element   jQuery=#table-project-status tr:nth-child(2) td:nth-child(3).status.action
    And the user should see the element   jQuery=#table-project-status tr:nth-child(2) td:nth-child(4).status.action
    Then the user clicks the button/link  jQuery=#table-project-status tr:nth-child(2) td:nth-child(4).status.action a
    And the user navigates to the page    ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/review-all-bank-details

Project Finance can see the company name with score
    [Documentation]  INFUND-3763
    [Tags]
    Given the user navigates to the page          ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/review-all-bank-details
    And the user clicks the button/link           link=${Ntag_Name}
    Then the user navigates to the page           ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Ntag_Id}/review-bank-details
    And the user should see the text in the page  ${Ntag_Name}
    And the user should see the element           jQuery=tr:nth-child(1) td:nth-child(3):contains("3 / 9")

Project Finance can see the company number with status
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the text in the page    Company Number
    And the user should see the text in the page     ${Ntag_No}
    And the user should see the element              jQuery=tr:nth-child(2) td:nth-child(3):contains("No Match")

Project Finance can see the account number with status
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the text in the page    Bank account number / Sort code
    And the user should see the text in the page    51406795 / 404745
    And the user should see the element             jQuery=tr:nth-child(3) td:nth-child(3):contains("No Match")

Project Finance can see the address with score
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the text in the page    Address
    And the user should see the text in the page     ${Ntag_Street}, London, E17 5LR
    And the user should see the element              jQuery=tr:nth-child(4) td:nth-child(3):contains("7 / 9")

Project Finance has the options to edit the details and to approve the bank details
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element    link=Change bank account details
    And the user should see the element    jQuery=.button:contains("Approve bank account details")

Project Finance can change address and companies house details
    [Documentation]    INFUND-4054
    [Tags]    HappyPath
    Given the user navigates to the page  ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Ntag_Id}/review-bank-details
    Then the user clicks the button/link  link=Change bank account details
    And the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Ntag_Id}/review-bank-details/change
    And the text box should be editable          id=company-name
    When the user enters text to a text field    id=street  ${Ntag_Street}
    And the user enters text to a text field     id=company-name  ${Ntag_Name}
    And the user enters text to a text field     id=companies-house-number  ${Ntag_No}

Bank account number and sort code validations client side
    [Documentation]    INFUND-4054
    [Tags]
    When the user enters text to a text field    id=bank-acc-number    1234567
    And the user enters text to a text field    id=bank-sort-code    12345
    And the user moves focus to the element    link=Cancel bank account changes
    Then the user should see the text in the page    Please enter a valid account number
    And the user should see the text in the page    Please enter a valid sort code
    When the user enters text to a text field    id=bank-acc-number    123456789
    And the user enters text to a text field    id=bank-sort-code    1234567
    And the user moves focus to the element    link=Cancel bank account changes
    Then the user sees the text in the element    id=bank-acc-number    ${empty}    # Account numbers more than 8 numbers not allowed, so the input is not accepted
    And the user sees the text in the element    id=bank-sort-code    ${empty}    # Sort codes more than 6 numbers not allowed, so the input is not accepted
    And the user should not see an error in the page

Bank account number and sort code validations server side
    [Documentation]    INFUND-4054
    [Tags]
     When the user enters text to a text field    id=bank-acc-number    abcdefgh
     And the user enters text to a text field    id=bank-sort-code    abcdef
     And the user clicks the button/link           jQuery=.column-half.alignright .button:contains("Update bank account details")
     And the user clicks the button/link           jQuery=.modal-partner-change-bank-details .button:contains("Update bank account details")   #Due to popup
     Then the user should see the text in the page    Please enter a valid account number
     And the user should see the text in the page    Please enter a valid sort code


Project Finance cancels bank details changes
    [Documentation]    INFUND-4054,  INFUND-5899
    [Tags]    HappyPath
    When the user clicks the button/link          link=Cancel bank account changes
    Then the user should be redirected to the correct page  ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Ntag_Id}/review-bank-details
    When the user clicks the button/link          link=Change bank account details
    Then the text box should be editable          id=company-name
    And the user moves focus to the element       id=street
    Then the user sees the text in the text field    id=street  ${Ntag_Street}
    When the user clicks the button/link    jQuery=.column-half.alignright .button:contains("Update bank account details")
    And the user clicks the button/link     jQuery=.buttonlink:contains("Cancel")
    Then the text box should be editable    id=company-name
    When the user clicks the button/link    link=Review bank details
    Then the user should see the text in the page    These details are now undergoing an internal review.
    [Teardown]    the user goes back to the previous page

Project Finance updates bank account details
    [Documentation]    INFUND-4054
    [Tags]    HappyPath
    When the user enters text to a text field      id=street    Montrose House 2
    And the user clicks the button/link            jQuery=.column-half.alignright .button:contains("Update bank account details")
    And the user clicks the button/link            jQuery=.modal-partner-change-bank-details .button:contains("Update bank account details")   #Due to popup
    Then the user should see the text in the page  ${Ntag_Name} - Account details
    When the user clicks the button/link           link=Change bank account details
    Then the user sees the text in the text field  id=street    Montrose House 2
    When the user clicks the button/link           jQuery=.column-half.alignright button:contains("Update bank account details")
    Then the user clicks the button/link           jQuery=.modal-partner-change-bank-details .button:contains("Update bank account details")   #Due to popup

Project Finance approves the bank details
    [Documentation]    INFUND-4054, INFUND-6714, INFUND-7161
    [Tags]    HappyPath
    Given the user navigates to the page          ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Ntag_Id}/review-bank-details
    And the user should see the text in the page  ${Ntag_Name} - Account details
    When the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    And the user clicks the button/link     jQuery=.buttonlink:contains("Cancel")
    Then the user should see the element    jQuery=.button:contains("Approve bank account details")    #Checking here that the option is still available
    When the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    And the user clicks the button/link    jQuery=.button:contains("Approve account")
    Then the user should not see the element    jQuery=.button:contains("Approve bank account details")
    And the user should see the text in the page    The bank details provided have been approved.
    And the user should not see the text in the page  We are unable to save your bank account details
    When the user goes back to the previous page
    And the user goes back to the previous page
    When the user enters text to a text field      id=street    Montrose House 3
    And the user clicks the button/link            jQuery=.column-half.alignright .button:contains("Update bank account details")
    And the user clicks the button/link            jQuery=.modal-partner-change-bank-details .button:contains("Update bank account details")   #Due to popup
    Then the user should see the text in the page  Bank details have already been approved and cannot be changed

Project Finance cannot approve the bank details again
    [Documentation]    INFUND-9061
    [Tags]
    Given the user navigates to the page          ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/organisation/${Jetpulse_Id}/review-bank-details
    And the user should see the text in the page  ${Jetpulse_Name} - Account details
    When the user clicks the button/link    jQuery=.button:contains("Approve bank account details")
    And the user clicks the button/link    jQuery=.button:contains("Approve account")
    And the user goes back to the previous page
    Then the user should not see the element   jQuery=.button:contains("Approve account")
    And the user should see the text in the page    The bank details provided have been approved.

Lead partner can see that bank details has been approved
    [Documentation]    INFUND-7109
    [Tags]    HappyPath
    [Setup]    log in as a different user          ${PS_EF_APPLICATION_PM_EMAIL}  ${short_password}
    When the user clicks the button/link           link=${PS_EF_APPLICATION_HEADER}
    Then the user should see the element           jQuery=ul li.complete:nth-child(4)
    When the user clicks the button/link           link=status of my partners
    And the user should see the text in the page   Project team status
    And the user should see the element            jQuery=#table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(3)

Other internal users cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    the user navigates to the page and gets a custom error message  ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/review-all-bank-details  You do not have the necessary permissions for your request

Project partners cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    log in as a different user  ${PS_EF_APPLICATION_PM_EMAIL}  ${short_password}
    the user navigates to the page and gets a custom error message  ${server}/project-setup-management/project/${PS_EF_APPLICATION_PROJECT}/review-all-bank-details  You do not have the necessary permissions for your request


*** Keywords ***
the text box should be editable
    [Arguments]    ${text_field}
    Wait Until Element Is Visible Without Screenshots    ${text_field}
    Element Should Be Enabled    ${text_field}

all preliminary steps are completed
    finance contacts are submitted by all users
    project lead submits project details
    eligible partners submit their bank details
    logout as user
    close any open browsers

finance contacts are submitted by all users
    user submits his finance contacts  ${PS_EF_APPLICATION_ACADEMIC_EMAIL}  ${Wikivu_Id}
    user submits his finance contacts  ${PS_EF_APPLICATION_PARTNER_EMAIL}  ${Jetpulse_Id}
    user submits his finance contacts  ${PS_EF_APPLICATION_LEAD_PARTNER_EMAIL}  ${Ntag_Id}

user submits his finance contacts
    [Arguments]  ${user}  ${id}
    guest user log-in  ${user}  ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_EF_APPLICATION_PROJECT}/details/finance-contact?organisation=${id}
    the user selects the radio button  financeContact  financeContact1
    the user clicks the button/link    jQuery=.button:contains("Save")

project lead submits project details
    log in as a different user         ${PS_EF_APPLICATION_LEAD_PARTNER_EMAIL}    ${short_password}
    the user navigates to the page     ${server}/project-setup/project/${PS_EF_APPLICATION_PROJECT}/details/project-address
    the user selects the radio button  addressType  address-use-org
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user navigates to the page     ${server}/project-setup/project/${PS_EF_APPLICATION_PROJECT}/details/project-manager
    the user selects the radio button  projectManager  projectManager2
    the user clicks the button/link    jQuery=.button:contains("Save")
    the user navigates to the page     ${server}/project-setup/project/${PS_EF_APPLICATION_PROJECT}/details
    the user clicks the button/link    jQuery=.button:contains("Mark as complete")
    the user clicks the button/link    jQuery=.button:contains("Submit")

eligible partners submit their bank details
    the user fills in his bank details  ${PS_EF_APPLICATION_ACADEMIC_EMAIL}
    the user fills in his bank details  ${PS_EF_APPLICATION_PARTNER_EMAIL}
    the user fills in his bank details  ${PS_EF_APPLICATION_LEAD_PARTNER_EMAIL}

the user fills in his bank details
    [Arguments]  ${user}
    log in as a different user            ${user}  ${short_password}
    the user navigates to the page        ${server}/project-setup/project/${PS_EF_APPLICATION_PROJECT}/bank-details
    the user enters text to a text field  name=accountNumber  51406795
    the user enters text to a text field  name=sortCode  404745
    the user selects the radio button     addressType  address-use-org
    the user clicks the button/link       jQuery=.button:contains("Submit bank account details")
    the user clicks the button/link       jQuery=.button:contains("Submit")
