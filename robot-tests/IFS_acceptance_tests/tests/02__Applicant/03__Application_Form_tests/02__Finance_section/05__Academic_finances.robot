*** Settings ***
Documentation     INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections    #TODO Pending INFUND-5218
...
...
...               INFUND-918: As an academic partner i want to be able to mark my finances as complete, so that the lead partner can have confidence in my finances
...
...
...               INFUND-2399: As a Academic partner I want to be able to add my finances including decimals for accurate recording of my finances
Suite Setup       Log in create a new invite application invite academic collaborators and accept the invite
Suite Teardown    the user closes the browser
Force Tags        Email    Applicant    Pending
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot
Resource          ../../../../resources/keywords/SUITE_SET_UP_ACTIONS.robot    #TODO it seems not possible to mark the finances as complete as Academic. Have created ticket INFUND-4747
Resource          ../../../../resources/keywords/EMAIL_KEYWORDS.robot

*** Variables ***

*** Test Cases ***
Academic finances should be editable when lead marks them as complete
    [Documentation]    INFUND-2314
    [Tags]
    [Setup]    Lead applicant marks the finances as complete
    Given guest user log-in    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123
    When The user navigates to the academic application finances
    Then the user should not see the element    css=#incurred-staff[readonly]
    And the user logs out if they are logged in
    [Teardown]    Lead applicant marks the finances as incomplete

Academic finance validations
    [Documentation]    INFUND-2399
    [Tags]
    [Setup]    Guest user log-in    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123
    When The user navigates to the academic application finances
    And the applicant enters invalid inputs
    And Mark academic finances as complete
    Then the user should see an error    This field should be 0 or higher
    Then the user should see an error    This field cannot be left blank
    And the user should see the element    css=.error-summary-list
    And the field should not contain the currency symbol

Academic finance calculations
    [Documentation]    INFUND-917, INFUND-2399
    [Tags]
    Given The user navigates to the academic application finances
    When the academic partner fills the finances
    Then the calculations should be correct and the totals rounded to the second decimal

Large pdf upload not allowed
    [Documentation]    INFUND-2720
    [Tags]    Upload
    When the academic partner uploads a file    ${too_large_pdf}
    Then the user should get an error page    ${too_large_pdf_validation_error}
    And the user should see the text in the page    Attempt to upload a large file

Non pdf uploads not allowed
    [Documentation]    INFUND-2720
    [Tags]    Upload
    [Setup]    The user navigates to the academic application finances
    When the academic partner uploads a file    ${text_file}
    Then the user should get an error page    ${wrong_filetype_validation_error}

Lead applicant can't upload a JeS file
    [Documentation]    INFUND-2720
    [Tags]
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When The user navigates to the academic application finances
    Then the user should not see the element    name=jes-upload

Academics upload
    [Documentation]    INFUND-917
    [Tags]    HappyPath
    [Setup]    Guest user log-in    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123
    When The user navigates to the academic application finances
    When the academic partner uploads a file    ${valid_pdf}
    Then the user should not see the text in the page    No file currently uploaded
    And the user should see the element    link=testing.pdf
    And the user waits for the file to be scanned by the anti virus software

Academic partner can view the file on the finances
    [Documentation]    INFUND-917
    [Tags]    HappyPath
    When The user navigates to the academic application finances
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page

Academic partner can view the file on the finances overview
    [Documentation]    INFUND-917
    [Tags]
    Given The user navigates to the finance overview of the academic
    When the user clicks the button/link    link=testing.pdf
    Then the user should not see an error in the page

Lead applicant can't view the file on the finances page
    [Documentation]    INFUND-917
    [Tags]
    [Setup]    Guest user log-in    &{lead_applicant_credentials}
    When The user navigates to the academic application finances
    Then the user should not see the text in the page    ${valid_pdf}

Lead applicant can view the file on the finances overview page
    [Documentation]    INFUND-917
    [Tags]
    Given The user navigates to the finance overview of the academic
    And the user should see the text in the page    ${valid_pdf}
    When the user clicks the button/link    link=${valid_pdf}
    Then the user should not see an error in the page

Academic finances JeS link showing
    [Documentation]    INFUND-2402
    [Tags]
    [Setup]    Guest user log-in    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123
    When The user navigates to the academic application finances
    Then the user can see the link for more JeS details

Mark all as complete
    [Documentation]    INFUND-918
    [Tags]
    Given log in as user    ${test_mailbox_one}+academictest@gmail.com    Passw0rd123
    And The user navigates to the academic application finances
    And the user should see the element    link=testing.pdf
    When the user enters text to a text field    id=tsb-ref    123123
    Then textfield value should be    id=tsb-ref    123123
    When the user clicks the button/link    jQuery=.button:contains("Mark all as complete")
    Then the user redirects to the page    Please provide Innovate UK with information about your project.    Application overview
    and the user navigates to the finance overview of the academic
    And the user should see the element    css=.finance-summary tr:nth-of-type(2) img[src*="/images/field/tick-icon"]

User should not be able to edit or upload the form
    [Documentation]    INFUND-2437
    [Tags]
    When The user navigates to the academic application finances
    Then the user should not see the element    jQuery=button:contains("Remove")
    And the user should see the element    css=#incurred-staff[readonly]

File delete should not be allowed when marked as complete
    [Documentation]    INFUND-2437
    [Tags]
    When The user navigates to the academic application finances
    Then the user should not see the text in the page    Remove

Academic finance overview
    [Documentation]    INFUND-917
    ...
    ...    INFUND-2399
    [Tags]
    Given the user navigates to the finance overview of the academic
    Then the finance table should be correct
    When the user clicks the button/link    link=testing.pdf
    Then the user should see the text in the page    Adobe Acrobat PDF Files
    [Teardown]    The user marks the academic application finances as incomplete

*** Keywords ***
the academic partner fills the finances
    [Documentation]    INFUND-2399
    The user enters text to a text field    id=incurred-staff    999.999
    The user enters text to a text field    id=travel    999.999
    The user enters text to a text field    id=other    999.999
    The user enters text to a text field    id=investigators    999.999
    The user enters text to a text field    id=estates    999.999
    The user enters text to a text field    id=other-direct    999.999
    The user enters text to a text field    id=indirect    999.999
    The user enters text to a text field    id=exceptions-staff    999.999
    The user enters text to a text field    id=exceptions-other-direct    999.999
    The user enters text to a text field    id=tsb-ref    123123
    Mouse Out    css=input
    Sleep    300ms

the calculations should be correct and the totals rounded to the second decimal
    Textfield Value Should Be    id=subtotal-directly-allocated    £ 3,000
    Textfield Value Should Be    id=subtotal-exceptions    £ 2,000
    Textfield Value Should Be    id=total    £ 9,000

the academic partner uploads a file
    [Arguments]    ${file_name}
    Choose File    name=jes-upload    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms

the finance table should be correct
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £9,000
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(2)    £3,000
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(3)    £1,000
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(4)    £1,000
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(6)    £0
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(7)    £1,000
    Wait Until Element Contains    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(8)    £3,000

Lead applicant marks the finances as complete
    Given guest user log-in    steve.smith@empire.com    Passw0rd
    The user navigates to the academic application finances
    the user selects the radio button    financePosition-organisationSize    SMALL
    The user enters text to a text field    id=cost-financegrantclaim    20
    The user clicks the button/link    jQuery=#otherFundingShowHideToggle label:contains(No) input
    When the user marks the finances as complete
    Then the user redirects to the page    Please provide Innovate UK with information about your project.    Application overview
    the user closes the browser

Lead applicant marks the finances as incomplete
    Given guest user log-in    steve.smith@empire.com    Passw0rd
    When The user navigates to the academic application finances
    And the user clicks the button/link    jQuery=button:contains("Edit")
    And Close Browser
    #And Switch to the first browser

the user can see the link for more JeS details
    the user should see the element    link=Je-S website
    the user should see the element    xpath=//a[contains(@href,'https://je-s.rcuk.ac.uk')]

the applicant enters invalid inputs
    The user enters text to a text field    id=incurred-staff    100£
    The user enters text to a text field    id=travel    -89
    The user enters text to a text field    id=other    999.999
    The user enters text to a text field    id=investigators    999.999
    The user enters text to a text field    id=estates    999.999
    The user enters text to a text field    id=other-direct    999.999
    The user enters text to a text field    id=indirect    999.999
    The user enters text to a text field    id=exceptions-staff    999.999
    The user enters text to a text field    id=exceptions-other-direct    999.999
    The user enters text to a text field    id=tsb-ref    ${EMPTY}

the field should not contain the currency symbol
    Textfield Value Should Be    id=incurred-staff    100

Mark academic finances as complete
    Focus    jQuery=.button:contains("Mark all as complete")
    the user clicks the button/link    jQuery=.button:contains("Mark all as complete")

the user waits for the file to be scanned by the anti virus software
    Sleep    5s
    # this sleep statement is necessary as we wait for the antivirus scanner to work. Please do not remove during refactoring!
