*** Settings ***
Documentation     INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections
...
...               INFUND-918: As an academic partner i want to be able to mark my finances as complete, so that the lead partner can have confidence in my finances
...
...               INFUND-2399: As a Academic partner I want to be able to add my finances including decimals for accurate recording of my finances
...
...               INFUND-8347: Update 'Your project costs' for academics
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
    When the user navigates to the academic application finances
    And the user clicks the button/link       link=Your project costs
    Then the user should not see the element  css=#incurred-staff[readonly]
    [Teardown]    Lead applicant marks the finances as incomplete

Academic finance validations
    [Documentation]    INFUND-2399
    [Tags]
    [Setup]    Log in as a different user    ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to the academic application finances
    And the user clicks the button/link  link=Your project costs
    And the applicant enters invalid inputs
    And Mark academic finances as complete
    Then the user should see an error    This field should be 0 or higher.
    Then the user should see an error    This field cannot be left blank.
    And the user should see the element  css=.error-summary-list
    And the field should not contain the currency symbol

Academic finance calculations
    [Documentation]    INFUND-917, INFUND-2399
    [Tags]
    Given the user navigates to the academic application finances
    When the user clicks the button/link  link=Your project costs
    And the academic partner fills the finances
    Then the calculations should be correct and the totals rounded to the second decimal

Large pdf upload not allowed
    [Documentation]    INFUND-2720
    [Tags]    Upload
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
    Given the user navigates to the academic application finances
    When the user clicks the button/link      link=Your project costs
    Then the user should not see the element  css=.upload-section label

Academics upload
    [Documentation]    INFUND-917
    [Tags]    HappyPath
    [Setup]    log in as a different user              ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to the academic application finances
    And the user clicks the button/link                link=Your project costs
    When the academic partner uploads a file           ${valid_pdf}
    Then the user should not see the text in the page  No file currently uploaded
    And the user should see the element                link=testing.pdf (opens in a new window)
    And the user waits for the file to be scanned by the anti virus software

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
    When the user navigates to the academic application finances
    And the user clicks the button/link                link=Your project costs
    Then the user should not see the text in the page  ${valid_pdf}

Lead applicant can view the file on the finances overview page
    [Documentation]    INFUND-917
    [Tags]    Pending
    # TODO Pending due to INFUND-9372
    When the user navigates to the finance overview of the academic
    And the user should see the text in the page  ${valid_pdf}
    When the user opens the link in new window    ${valid_pdf}
    Then the user should not see an error in the page
    [Teardown]    the user goes back to the previous page

Academic finances JeS link showing
    [Documentation]    INFUND-2402, INFUND-8347
    [Tags]
    [Setup]    log in as a different user     ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    When the user navigates to the academic application finances
    Then the user should not see the element  link=Your funding
    When the user clicks the button/link      link=Your project costs
    Then the user can see JeS details

Mark all as complete
    [Documentation]    INFUND-918
    [Tags]
    Given log in as a different user               ${test_mailbox_one}+academictest@gmail.com    ${correct_password}
    And the user navigates to the academic application finances
    And the user clicks the button/link            link=Your project costs
    And the user should see the element            link=testing.pdf (opens in a new window)
    When the user enters text to a text field      id=tsb-ref    123123
    Then textfield value should be                 id=tsb-ref    123123
    When the user clicks the button/link           jQuery=button:contains("Mark as complete")
    Then the user should see the text in the page  Your finances
    And the user navigates to the finance overview of the academic
    And the user should see the element            css=.finance-summary tr:nth-of-type(2) img[src*="/images/field/tick-icon"]

User should not be able to edit or upload the form
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to the academic application finances
    And the user clicks the button/link       link=Your project costs
    Then the user should not see the element  jQuery=button:contains("Remove")
    And the user should see the element       css=#incurred-staff[readonly]

File delete should not be allowed when marked as complete
    [Documentation]    INFUND-2437
    [Tags]
    When the user navigates to the academic application finances
    Then the user should not see the text in the page  Remove

Academic finance overview
    [Documentation]    INFUND-917
    ...
    ...    INFUND-2399
    [Tags]
    Given the user navigates to the finance overview of the academic
    Then the finance table should be correct
    Then the user should not see an error in the page
    [Teardown]    The user marks the academic application finances as incomplete

*** Keywords ***
Custom Suite Setup
    the guest user opens the browser
    Login new application invite academic  ${test_mailbox_one}+academictest@gmail.com  Invitation to collaborate in ${openCompetitionBusinessRTO_name}  You will be joining as part of the organisation

the academic partner fills the finances
    [Documentation]    INFUND-2399
    The user enters text to a text field  id=incurred-staff    999.999
    The user enters text to a text field  id=travel    999.999
    The user enters text to a text field  id=other    999.999
    The user enters text to a text field  id=investigators    999.999
    The user enters text to a text field  id=estates    999.999
    The user enters text to a text field  id=other-direct    999.999
    The user enters text to a text field  id=indirect    999.999
    The user enters text to a text field  id=exceptions-staff    999.999
    The user enters text to a text field  id=exceptions-other-direct    999.999
    The user enters text to a text field  id=tsb-ref    123123
    Mouse Out                             css=input
    wait for autosave

the calculations should be correct and the totals rounded to the second decimal
    Textfield Value Should Be  id=subtotal-directly-allocated    £3,000
    Textfield Value Should Be  id=subtotal-exceptions    £2,000
    Textfield Value Should Be  id=total    £9,000

the academic partner uploads a file
    [Arguments]    ${file_name}
    Choose File    css=.upload-section input    ${UPLOAD_FOLDER}/${file_name}


the finance table should be correct
    Wait Until Element Contains Without Screenshots  css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(1)    £9,000
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(2)    3,000
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(3)    1,000
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(4)    1,000
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(6)    0
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(7)    1,000
    Element Should Contain                           css=.project-cost-breakdown tr:nth-of-type(2) td:nth-of-type(8)    3,000

Lead applicant marks the finances as complete
    Log in as a different user                       &{lead_applicant_credentials}
    the user clicks the button/link                  link=Academic robot test application
    the applicant completes the application details  Application details
    the user navigates to the academic application finances
    the user marks the finances as complete          Academic robot test application  labour costs  n/a


Lead applicant marks the finances as incomplete
    log in as a different user       &{lead_applicant_credentials}
    the user navigates to the academic application finances
    the user clicks the button/link  link=Your funding
    the user clicks the button/link  jQuery=button:contains("Edit")


the user can see JeS details
    the user should see the element  link=Je-S website
    the user should see the element  css=a[href*="https://je-s.rcuk.ac.uk"]
    the user should see the element  css=a[href*="https://www.gov.uk/government/publications/innovate-uk-completing-your-application-project-costs-guidance/guidance-for-academics-applying-via-the-je-s-system"]

the applicant enters invalid inputs
    The user enters text to a text field  id=incurred-staff    100£
    The user enters text to a text field  id=travel    -89
    The user enters text to a text field  id=other    999.999
    The user enters text to a text field  id=investigators    999.999
    The user enters text to a text field  id=estates    999.999
    The user enters text to a text field  id=other-direct    999.999
    The user enters text to a text field  id=indirect    999.999
    The user enters text to a text field  id=exceptions-staff    999.999
    The user enters text to a text field  id=exceptions-other-direct    999.999
    The user enters text to a text field  id=tsb-ref    ${EMPTY}

the field should not contain the currency symbol
    Textfield Value Should Be  id=incurred-staff    100

Mark academic finances as complete
    the user moves focus to the element  id=mark-all-as-complete
    the user clicks the button/link      id=mark-all-as-complete

the user waits for the file to be scanned by the anti virus software
    Sleep    5s
    # this sleep statement is necessary as we wait for the antivirus scanner to work. Please do not remove during refactoring!
