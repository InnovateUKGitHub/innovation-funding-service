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
Force Tags        Experian    Project Setup
Resource          PS_Common.robot

*** Test Cases ***
Project Finance can see Bank details requiring action
    [Documentation]    INFUND-3763, INFUND-4903
    [Tags]  HappyPath
    Given the user navigates to the page  ${server}/management/dashboard/project-setup
    When the user clicks the button/link  link = ${PS_Competition_Name}
    And the user clicks the button/link   css = #table-project-status > tbody > tr:nth-child(7) > td:nth-child(5) a  # Complete Bank details
    Then the user should be redirected to the correct page  ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details

Project Finance can see the company name with score
    [Documentation]  INFUND-3763
    [Tags]  HappyPath
    Given the user navigates to the page          ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details
    And the user clicks the button/link           link = ${Gabtype_Name}
    Then the user should be redirected to the correct page     ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    And the user should see the element           jQuery = td:contains("${Gabtype_Name}") ~ td:contains("10 / 9")

Project Finance can see the company number with status
    [Documentation]    INFUND-3763
    [Tags]  HappyPath
    Then the user should see the element             jQuery = td:contains("Company Number") ~ td:contains("14935204")
    And the user should see the element              jQuery = tr:nth-child(2) td:nth-child(3):contains("No Match")

Project Finance can see the account number with status
    [Documentation]    INFUND-3763
    [Tags]  HappyPath
    Then the user should see the element      jQuery = td:contains("Bank account number / Sort code") ~ td:contains("${Account_One} / ${Sortcode_One}")
    And the user should see the element       jQuery = tr:nth-child(3) td:nth-child(3):contains("No Match")

Project Finance can see the address with score
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element         jQuery = td:contains("Address") ~ td:contains("290 Parkside Circle, London, E17 5LR")
    And the user should see the element          jQuery = tr:nth-child(4) td:nth-child(3):contains("10 / 9")

Project Finance has the options to edit the details and to approve the bank details
    [Documentation]    INFUND-3763
    [Tags]
    Then the user should see the element    link = Change bank account details
    And the user should see the element     jQuery = .govuk-button:contains("Approve bank account details")

Project Finance can change address and companies house details
    [Documentation]    INFUND-4054
    [Tags]  HappyPath
    Given the user navigates to the page                     ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    Then the user clicks the button/link                     link = Change bank account details
    And the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details/change
    And the text box should be editable                      id = organisationName
    And the user enters text to a text field                 id = organisationName  ${Gabtype_Name}
    And the user enters text to a text field                 id = registrationNumber  14935204

Bank account number and sort code validations client side
    [Documentation]    INFUND-4054
    [Tags]
    When the user enters text to a text field        id = accountNumber    1234567
    And the user enters text to a text field         id = sortCode    12345
    And Set Focus To Element                         link = Cancel bank account changes
    Then the user should see a field error           Please enter a valid account number
    And the user should see a field error            Please enter a valid sort code
    When the user enters text to a text field        id = accountNumber    123456789
    And the user enters text to a text field         id = sortCode    1234567
    And Set Focus To Element                         link = Cancel bank account changes
    Then the user sees the text in the element       id = accountNumber    ${empty}    # Account numbers more than 8 numbers not allowed, so the input is not accepted
    And the user sees the text in the element        id = sortCode    ${empty}    # Sort codes more than 6 numbers not allowed, so the input is not accepted
    And the user should not see an error in the page

BanProject Finance can see the progress of partners bank detailsk account number and sort code validations server side
    [Documentation]    INFUND-4054
    [Tags]
     When the user enters text to a text field      id = accountNumber  123
     And the user enters text to a text field       id = sortCode  123
     And the user clicks the button/link            id = modal-change-bank-details
     And the user clicks the button/link            id = submit-change-bank-details
     Then the user should see a field and summary error  Please enter a valid account number
     And the user should see a field and summary error   Please enter a valid sort code

Project Finance cancels bank details changes
    [Documentation]    INFUND-4054,  INFUND-5899
    [Tags]
    When the user clicks the button/link                      link = Cancel bank account changes
    Then the user should be redirected to the correct page    ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    When the user clicks the button/link                      link = Change bank account details
    Then the text box should be editable                      id = organisationName
    And Set Focus To Element                                  css = [id = "addressForm.manualAddress.addressLine1"]
    Then the user sees the text in the text field             css = [id = "addressForm.manualAddress.addressLine1"]  290 Parkside Circle
    When the user clicks the button/link                      id = modal-change-bank-details
    And the user clicks the button/link                       jQuery = .button-clear:contains("Cancel")
    Then the text box should be editable                      id = organisationName
    When the user clicks the button/link                      link = Review bank details
    Then the user should see the element                      jQuery = p:contains("These details are now undergoing an internal review. ")
    [Teardown]    the user goes back to the previous page

Project Finance updates bank account details
    [Documentation]    INFUND-4054
    [Tags]
    When the user enters text to a text field      css = [id = "addressForm.manualAddress.addressLine1"]    Montrose House 2
    And the user clicks the button/link            id = modal-change-bank-details
    And the user clicks the button/link            id = submit-change-bank-details
    Then the user should see the element           jQuery = h2:contains("${Gabtype_Name} - Account details")
    When the user clicks the button/link           link = Change bank account details
    Then the user sees the text in the text field  css = [id = "addressForm.manualAddress.addressLine1"]    Montrose House 2
    When the user clicks the button/link           id = modal-change-bank-details
    Then the user clicks the button/link           id = submit-change-bank-details

Project Finance approves the bank details
    [Documentation]    INFUND-4054, INFUND-6714, INFUND-7161
    [Tags]  HappyPath
    Given the user navigates to the page            ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Gabtype_Id}/review-bank-details
    And the user should see the element             jQuery = h2:contains("${Gabtype_Name} - Account details")
    When the user clicks the button/link            jQuery = .govuk-button:contains("Approve bank account details")
    And the user clicks the button/link             jQuery = .button-clear:contains("Cancel")
    Then the user should see the element            jQuery = .govuk-button:contains("Approve bank account details")    #Checking here that the option is still available
    When the user clicks the button/link            jQuery = .govuk-button:contains("Approve bank account details")
    And the user clicks the button/link             jQuery = .govuk-button:contains("Approve account")
    Then the user should not see the element        jQuery = .govuk-button:contains("Approve bank account details")
    And the user should see the element             jQuery = .success-alert:contains("The bank details provided have been approved.")
    When the user goes back to the previous page
    And the user goes back to the previous page
    When the user enters text to a text field       css = [id = "addressForm.manualAddress.addressLine1"]    Montrose House 3
    And the user clicks the button/link             id = modal-change-bank-details
    And the user clicks the button/link             id = submit-change-bank-details
    Then the user should see a summary error        Bank details have already been approved and cannot be changed

Project Finance cannot approve the bank details again
    [Documentation]    INFUND-9061
    [Tags]  HappyPath
    Given the user navigates to the page            ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/organisation/${Kazio_Id}/review-bank-details
    And the user should see the element             jQuery = h2:contains("${Kazio_Name} - Account details")
    When the user clicks the button/link            jQuery = .govuk-button:contains("Approve bank account details")
    And the user clicks the button/link             jQuery = .govuk-button:contains("Approve account")
    And the user goes back to the previous page
    Then the user should not see the element        jQuery = .govuk-button:contains("Approve account")
    And the user should see the element             jQuery = .success-alert:contains("The bank details provided have been approved.")

Lead partner can see that bank details has been approved
    [Documentation]    INFUND-7109
    [Tags]
    [Setup]    log in as a different user          ${PS_EF_Application_Lead_Partner_Email}  ${short_password}
    When the user clicks the button/link           link = ${PS_EF_Application_Title}
    Then the user should see the element           css = ul li.complete:nth-child(4)
    When the user clicks the button/link           link = View the status of partners
    And the user should see the element            jQuery = h1:contains("Project team status")
    And the user should see the element            css = #table-project-status tr:nth-of-type(1) td.status.ok:nth-of-type(4)

Other internal users cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    log in as a different user    &{Comp_admin1_credentials}
    Given the user navigates to the page and gets a custom error message  ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details  ${403_error_message}

Project partners cannot access this page
    [Documentation]    INFUND-3763
    [Tags]
    [Setup]    log in as a different user  ${PS_EF_Application_Partner_Email}  ${short_password}
    Given the user navigates to the page and gets a custom error message  ${server}/project-setup-management/project/${PS_EF_Application_Project_No}/review-all-bank-details  ${403_error_message}

*** Keywords ***
the text box should be editable
    [Arguments]    ${text_field}
    Wait Until Element Is Visible Without Screenshots  ${text_field}
    Element Should Be Enabled  ${text_field}