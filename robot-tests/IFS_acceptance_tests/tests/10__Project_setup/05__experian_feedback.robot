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
Suite Setup       the user logs-in in new browser    &{internal_finance_credentials}
Suite Teardown    the user closes the browser
Force Tags        Project Setup
Resource          ../../resources/common/PS_Common.robot
Resource          ../../resources/defaultResources.robot

*** Variables ***
${reviewBankDetailsURL}   ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details

*** Test Cases ***
Project Finance can see Bank details requiring action
    [Documentation]    INFUND-3763, INFUND-4903
    [Tags]  HappyPath
    Given project finance navigates to bank details requiring action
    Then the user should be redirected to the correct page  ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details

Project Finance can see the company details with scores, statuses and has the options to edit,approve the bank details
    [Documentation]  INFUND-3763
    [Tags]  HappyPath
    Given project finance navigates to review bank details page
    When the user is able to see company details with scores
    Then the user is able to see company details with statuses
    And the user is able to see edit and approve the bank details options

Project Finance can change companies house details
    [Documentation]    INFUND-4054
    [Tags]  HappyPath
    Given the user navigates to the page                     ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    When the user navigates to change bank account details page
    Then the user is able to change companies house details

Project fiance is able to verify client/server side validation and cancel bank details changes
    [Documentation]    INFUND-4054,  INFUND-5899
    Given the user verifies client side validation
    Then the user verifies server side validation
    And the user is able to cancel bank details changes
    [Teardown]    the user goes back to the previous page

Project Finance is able update and approve bank account details
    [Documentation]    INFUND-4054, INFUND-6714, INFUND-7161
    Given the user updates bank account details
    When the user navigates to the page            ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    Then the user is able to approve bank details

Project Finance cannot approve the bank details again
    [Documentation]    INFUND-9061
    [Tags]  HappyPath
    Given the user is unable to change bank details once they have been approved
    Then the user is unable to approve the bank details again

Lead partner can see that bank details has been approved
    [Documentation]    INFUND-7109
    Given log in as a different user          &{PS_EF_Application_Partner_Email_credentials}
    When the user clicks the button/link      link = ${PS_EF_Application_Title}
    Then the lead partner is able to see approved bank details

Project partners and other internal users cannot access bank details page
    [Documentation]    INFUND-3763
    Given Specific user should not be able to access the page 403 error   ${reviewBankDetailsURL}    &{Comp_admin1_credentials}
    Then Specific user should not be able to access the page 403 error    ${reviewBankDetailsURL}    &{PS_EF_Application_Partner_Email_credentials}

*** Keywords ***
The lead partner is able to see approved bank details
    the user should see the element           css = ul li.complete:nth-child(4)
    the user clicks the button/link           link = View the status of partners
    the user should see the element            jQuery = h1:contains("Project team status")
    the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

The user is unable to approve the bank details again
    the user navigates to the page            ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Kazio_Id}/review-bank-details
    the user should see the element             jQuery = h2:contains("${Kazio_Name} - Account details")
    the user clicks the button/link            jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link             jQuery = .govuk-button:contains("Approve account")
    the user goes back to the previous page
    the user should not see the element        jQuery = .govuk-button:contains("Approve account")
    the user should see the element             jQuery = .success-alert:contains("The bank details provided have been approved.")

The user is unable to change bank details once they have been approved
    the user goes back to the previous page
    the user goes back to the previous page
    the user enters text to a text field       css = [id = "addressLine1"]    Montrose House 3
    the user clicks the button/link             id = modal-change-bank-details
    the user clicks the button/link             id = submit-change-bank-details
    the user should see a summary error        Bank details have already been approved and cannot be changed

The user is able to approve bank details
    the user should see the element       jQuery = h2:contains("${Gabtype_Name} - Account details")
    the user clicks the button/link       jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link       jQuery = .button-clear:contains("Cancel")
    the user should see the element       jQuery = .govuk-button:contains("Approve bank account details")    #Checking here that the option is still available
    the user clicks the button/link       jQuery = .govuk-button:contains("Approve bank account details")
    the user clicks the button/link       jQuery = .govuk-button:contains("Approve account")
    the user should not see the element   jQuery = .govuk-button:contains("Approve bank account details")
    the user should see the element       jQuery = .success-alert:contains("The bank details provided have been approved.")

The user updates bank account details
    the user enters text to a text field       css = [id = "addressLine1"]    Montrose House 2
    the user clicks the button/link            id = modal-change-bank-details
    the user clicks the button/link            id = submit-change-bank-details
    the user should see the element            jQuery = h2:contains("${Gabtype_Name} - Account details")
    the user clicks the button/link            link = Change bank account details
    the user sees the text in the text field   css = [id = "addressLine1"]    Montrose House 2
    the user clicks the button/link            id = modal-change-bank-details
    the user clicks the button/link            id = submit-change-bank-details

The user is able to cancel bank details changes
    the user clicks the button/link                     link = Cancel bank account changes
    the user should be redirected to the correct page   ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    the user clicks the button/link                     link = Change bank account details
    The user should see the enabled element             id = organisationName
    Set Focus To Element                                css = [id = "addressLine1"]
    the user sees the text in the text field            css = [id = "addressLine1"]  290 Parkside Circle
    the user clicks the button/link                     id = modal-change-bank-details
    the user clicks the button/link                     jQuery = .button-clear:contains("Cancel")
    The user should see the enabled element             id = organisationName
    the user clicks the button/link                     link = Review bank details
    the user should see the element                     jQuery = p:contains("These details are now undergoing an internal review. ")

The user verifies server side validation
    the user enters text to a text field            id = accountNumber  123
    the user enters text to a text field            id = sortCode  123
    the user clicks the button/link                 id = modal-change-bank-details
    the user clicks the button/link                 id = submit-change-bank-details
    the user should see a field and summary error   Please enter a valid account number
    the user should see a field and summary error   Please enter a valid sort code

The user verifies client side validation
    the user enters text to a text field    id = accountNumber    1234567
    the user enters text to a text field    id = sortCode    12345
    Set Focus To Element                    link = Cancel bank account changes
    the user should see a field error       Please enter a valid account number
    the user should see a field error       Please enter a valid sort code
    the user enters text to a text field    id = accountNumber    123456789
    the user enters text to a text field    id = sortCode    1234567
    Set Focus To Element                    link = Cancel bank account changes
    the user sees the text in the element   id = accountNumber    ${empty}    # Account numbers more than 8 numbers not allowed, so the input is not accepted
    the user sees the text in the element   id = sortCode    ${empty}    # Sort codes more than 6 numbers not allowed, so the input is not accepted
    the user should not see an error in the page

The user is able to change companies house details
    The user should see the enabled element   id = organisationName
    the user enters text to a text field      id = organisationName  ${Gabtype_Name}
    the user enters text to a text field      id = registrationNumber  14935204

The user navigates to change bank account details page
    the user clicks the button/link                     link = Change bank account details
    the user should be redirected to the correct page   ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details/change

The user is able to see edit and approve the bank details options
    the user should see the element   link = Change bank account details
    the user should see the element   jQuery = .govuk-button:contains("Approve bank account details")

The user is able to see company details with statuses
    the user should see the element   jQuery = td:contains("Company Number") ~ td:contains("14935204")
    the user should see the element   jQuery = tr:nth-child(2) td:nth-child(3):contains("No Match")
    the user should see the element   jQuery = td:contains("Bank account number / Sort code") ~ td:contains("${Account_One} / ${Sortcode_One}")
    the user should see the element   jQuery = tr:nth-child(3) td:nth-child(3):contains("No Match")

The user is able to see company details with scores
    the user should see the element   jQuery = td:contains("${Gabtype_Name}") ~ td:contains("10 / 9")
    the user should see the element   jQuery = td:contains("Address") ~ td:contains("290 Parkside Circle, London, E17 5LR")
    the user should see the element   jQuery = tr:nth-child(4) td:nth-child(3):contains("10 / 9")

Project finance navigates to bank details requiring action
    the user navigates to the page    ${server}/project-setup-management/competition/${PS_Competition_Id}/status/all
    the user clicks the button/link   link = 2
    the user clicks the button/link   css = #table-project-status > tbody > tr:nth-child(2) > td:nth-child(6) a  # Complete Bank details

Project finance navigates to review bank details page
    the user navigates to the page                      ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details
    the user clicks the button/link                     link = ${Gabtype_Name}
    the user should be redirected to the correct page   ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
