*** Settings ***
Documentation     INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections
...
...               INFUND-918: As an academic partner i want to be able to mark my finances as complete, so that the lead partner can have confidence in my finances
...
...               INFUND-2399: As a Academic partner I want to be able to add my finances including decimals for accurate recording of my finances
...
...               INFUND-8347: Update 'Your project costs' for academics
...
...               IFS-2879: As a Research applicant I MUST accept the grant terms and conditions
Suite Setup       Custom Suite Setup
Suite Teardown    Close browser and delete emails
Force Tags        Email    Applicant
Resource          ../../../../resources/defaultResources.robot
Resource          ../../Applicant_Commons.robot


*** Variables ***

*** Test Cases ***
Academic finances should be editable when lead marks them as complete
    [Documentation]    INFUND-2314
    [Tags]    HappyPath
    [Setup]    Lead applicant marks the finances as complete
    Given Log in as a different user          ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to Your-finances page  Academic robot test application
    And the user clicks the button/link       link=Your project costs
    Then the user should not see the element  css=#incurred-staff[readonly]
    [Teardown]    Lead applicant marks the finances as incomplete

Academic finance validations
    [Documentation]    INFUND-2399  IFS-2879
    [Tags]
    [Setup]    Log in as a different user    ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to Your-finances page  Academic robot test application
    And the user clicks the button/link  link=Your project costs
    And the applicant enters invalid inputs
    And the element should be disabled       id=mark-all-as-complete
    And Mark academic finances as complete
    And the user should see the element  css=.error-summary-list

Academic finance calculations
    [Documentation]    INFUND-917, INFUND-2399
    [Tags]
    When the academic fills in the project costs
    And the user clicks the button/link  link=Your project costs
    Then the subtotals should be correctly updated

Large pdf upload not allowed
    [Documentation]    INFUND-2720
    [Tags]    Upload
    Given the user clicks the button/link         jQuery=.buttonlink:contains("Edit your project costs")
    Then the user clicks the button/link          css=button[name="remove_finance_document"]
    When the academic partner uploads a file      ${too_large_pdf}
    Then the user should get an error page        ${too_large_pdf_validation_error}
    And the user should see the text in the page  Attempt to upload a large file
    [Teardown]    the user goes back to the previous page

Non pdf uploads not allowed
    [Documentation]    INFUND-2720
    [Tags]    Upload
    When the academic partner uploads a file  ${text_file}
    Then the user should see an error         ${wrong_filetype_validation_error}

Lead applicant can't upload a JeS file
    [Documentation]    INFUND-2720
    [Tags]
    [Setup]    log in as a different user     &{lead_applicant_credentials}
    Given the user navigates to Your-finances page  Academic robot test application
    When the user clicks the button/link      link=Your project costs
    Then the user should not see the element  css=.upload-section label

Academics upload
    [Documentation]    INFUND-917
    [Tags]    HappyPath
    [Setup]    log in as a different user              ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to Your-finances page  Academic robot test application
    And the user clicks the button/link                link=Your project costs
    When the academic partner uploads a file           ${valid_pdf}
    Then the user should not see the text in the page  No file currently uploaded
    And the user should see the element                link=testing.pdf (opens in a new window)

Academic partner can view the file on the finances
    [Documentation]    INFUND-917
    [Tags]    HappyPath
    When the user opens the link in new window  ${valid_pdf}
    Then the user should not see an error in the page

Academic partner can view the file on the finances overview
    [Documentation]    INFUND-917
    [Tags]
    When the user navigates to the finance overview of the academic
    Then the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

Lead applicant can't view the file on the finances page
    [Documentation]    INFUND-917
    [Tags]
    [Setup]    log in as a different user              &{lead_applicant_credentials}
    When the user navigates to Your-finances page  Academic robot test application
    And the user clicks the button/link                link=Your project costs
    Then the user should not see the text in the page  ${valid_pdf}

Academic finances JeS link showing
    [Documentation]    INFUND-2402, INFUND-8347
    [Tags]
    [Setup]    log in as a different user     ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to Your-finances page  Academic robot test application
    Then the user should not see the element  link=Your funding
    And the user should see correct grant percentage
    When the user clicks the button/link      link=Your project costs
    Then the user can see JeS details

Mark all as complete
    [Documentation]    INFUND-918  IFS-2879
    [Tags]
    Given log in as a different user               ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    And the user navigates to Your-finances page  Academic robot test application
    And the user clicks the button/link            link=Your project costs
    And the user should see the element            link=testing.pdf (opens in a new window)
    When the user enters text to a text field      css=input[name$="tsb_reference"]  123123
    Then textfield value should be                 css=input[name$="tsb_reference"]  123123
    And the user selects the checkbox              termsAgreed
    When the user clicks the button/link           jQuery=button:contains("Mark as complete")
    Then the user should see the text in the page  Your finances
    And the user navigates to the finance overview of the academic
    And the user should see the element            css=.finance-summary tr:nth-of-type(2) img[src*="/images/ifs-images/icons/icon-tick"]

User should not be able to edit or upload the form
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to Your-finances page  Academic robot test application
    And the user should see correct grant percentage
    And the user clicks the button/link       link=Your project costs
    Then the user should not see the element  jQuery=button:contains("Remove")
    And the user should see the element       css=[name$="incurred_staff"][readonly]

File delete should not be allowed when marked as complete
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to Your-finances page  Academic robot test application
    Then the user should not see the text in the page  Remove

Academic finance overview
    [Documentation]  INFUND-917 INFUND-2399
    [Tags]
    Given the user navigates to the finance overview of the academic
    Then the finance table should be correct
    Then the user should not see an error in the page
    [Teardown]  The user marks the academic application finances as incomplete


*** Keywords ***
Custom Suite Setup
    the guest user opens the browser
    Login new application invite academic  ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation

the subtotals should be correctly updated
    Textfield Value Should Be  id=subtotal-directly-allocated  £3,047
    Textfield Value Should Be  id=subtotal-exceptions  £8,013

the academic partner uploads a file
    [Arguments]    ${file_name}
    Choose File    css=.upload-section input    ${UPLOAD_FOLDER}/${file_name}

the finance table should be correct
    capture large screenshot
    Wait Until Element Contains Without Screenshots  css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)  £32,698
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(2)  4,407
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(3)  8,909
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(4)  4,244
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(6)  0
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(7)  4,243
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(8)  10,895

Lead applicant marks the finances as complete
    Log in as a different user                       &{lead_applicant_credentials}
    the user clicks the button/link                  link=Academic robot test application
    the applicant completes the application details  Application details
    the user navigates to Your-finances page  Academic robot test application
    the user marks the finances as complete          Academic robot test application  labour costs  n/a  no

Lead applicant marks the finances as incomplete
    log in as a different user       &{lead_applicant_credentials}
    the user navigates to Your-finances page  Academic robot test application
    the user clicks the button/link  link=Your funding
    the user clicks the button/link  jQuery=button:contains("Edit")

the user can see JeS details
    the user should see the element  link=Je-S website
    the user should see the element  css=a[href*="https://je-s.rcuk.ac.uk"]
    the user should see the element  css=a[href*="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system"]

the applicant enters invalid inputs
    The user enters text to a text field  css=[name$="incurred_staff"]  100£
    The user enters text to a text field  css=[name$="incurred_travel_subsistence"]  -89
    The user enters text to a text field  css=[name$="incurred_other_costs"]  999.999
    The user enters text to a text field  css=[name$="allocated_investigators"]  hello!
    The user enters text to a text field  css=[name$="allocated_estates_costs"]  £$%^&*
    The user enters text to a text field  css=[name$="allocated_other_costs"]  TestIt!
    The user enters text to a text field  css=[name$="indirect_costs"]  42,42
    The user enters text to a text field  css=[name$="exceptions_staff"]  999.999
    The user enters text to a text field  css=[name$="exceptions_other_costs"]  ${EMPTY}
    The user enters text to a text field  css=[name$="tsb_reference"]  ${EMPTY}
    the user should see a field error  This field can only accept whole numbers.
    the user should see a field error  This field cannot be left blank.

Mark academic finances as complete
    the user selects the checkbox    termsAgreed
    the user clicks the button/link  id=mark-all-as-complete

the user should see correct grant percentage
    the user should see the text in the element   css=.form-group tr:nth-of-type(1) th:nth-of-type(2)  % Grant
    the user should see the text in the element   css=.form-group tr:nth-of-type(1) td:nth-of-type(2)  100%

The user marks the academic application finances as incomplete
    the user navigates to Your-finances page  Academic robot test application
    the user clicks the button/link    link=Your project costs
    Focus    jQuery=button:contains("Edit")
    the user clicks the button/link    jQuery=button:contains("Edit")
    wait for autosave