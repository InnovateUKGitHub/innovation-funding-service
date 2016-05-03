*** Settings ***
Documentation     INFUND-917: As an academic partner i want to input my finances according to the JES field headings, so that i enter my figures into the correct sections
...
...
...               INFUND-918: As an academic partner i want to be able to mark my finances as complete, so that the lead partner can have confidence in my finances
...
...
...               INFUND-2399: As a Academic partner I want to be able to add my finances including decimals for accurate recording of my finances
Suite Setup       Guest user log-in    &{collaborator2_credentials}
Suite Teardown    User closes the browser
Force Tags        Finances    HappyPath
Resource          ../../../../resources/GLOBAL_LIBRARIES.robot
Resource          ../../../../resources/variables/GLOBAL_VARIABLES.robot
Resource          ../../../../resources/variables/User_credentials.robot
Resource          ../../../../resources/keywords/Login_actions.robot
Resource          ../../../../resources/keywords/User_actions.robot

*** Variables ***
${valid_pdf}      testing.pdf
${text_file}      testing.txt

*** Test Cases ***
Academic finances should be editable when lead marks finances as complete
    [Documentation]    INFUND-2314
    [Tags]    Failing
    [Setup]    Lead applicant marks the finances as complete
    #we need to adjust the application in order to mark this as complete
    When the user navigates to the page    ${YOUR_FINANCES_URL}
    Then the user should not see the element    css=#incurred-staff[readonly]
    [Teardown]    Lead applicant marks the finances as incomplete

Academic validations
    [Documentation]    INFUND-2399
    When the user navigates to the page    ${YOUR_FINANCES_URL}
    And the applicant enters invalid inputs
    Mark academic finances as complete
    Then the user should see an error    This field should be 0 or higher
    And the user should see the element    css=.error-summary-list
    And the field should not contain the currency symbol

Academic finance calculations
    [Documentation]    INFUND-917
    ...
    ...    INFUND-2399
    When the user navigates to the page    ${YOUR_FINANCES_URL}
    When the academic partner fills the finances
    Then the calculations should be correct and the totals rounded to the second decimal

Academic invalid upload
    When the academic partner uploads a file    ${text_file}
    Then the user should see the element    css=.error-summary
    And the user should see the text in the page    No file currently uploaded

Academics upload
    [Documentation]    INFUND-917
    [Tags]
    When the academic partner uploads a file    ${valid_pdf}
    Then the user should not see the text in the page    No file currently uploaded
    And the user should see the element    link=testing.pdf

Academic finances JeS link showing
    [Documentation]    INFUND-2402
    [Tags]    Academic
    When the user can see the link for more JeS details

Mark all as complete
    [Documentation]    INFUND-918
    Given the user reloads the page
    When the user clicks the button/link    jQuery=.button:contains("Mark all as complete")
    Then the user should be redirected to the correct page    ${APPLICATION_OVERVIEW_URL}
    And the user navigates to the page    ${FINANCES_OVERVIEW_URL}
    And the user should see the element    css=.finance-summary tr:nth-of-type(3) img[src="/images/field/tick-icon.png

File upload/delete should not be allowed when marked as complete
    [Documentation]    INFUND-2437
    [Tags]    Pending
    # Pending due to 2202
    Then the user cannot see the option to upload a file on the page    ${YOUR_FINANCES_URL}
    And the user cannot remove the uploaded file

Academic finance overview
    [Documentation]    INFUND-917
    ...
    ...    INFUND-2399
    [Tags]
    Given the user navigates to the page    ${FINANCES_OVERVIEW_URL}
    Then the finance table should be correct
    When the user clicks the button/link    link=testing.pdf
    Then the user should see the text in the page    Adobe Acrobat PDF Files

*** Keywords ***
the academic partner fills the finances
    [Documentation]    INFUND-2399
    Input Text    id=incurred-staff    999.999
    Input Text    id=travel    999.999
    Input Text    id=other    999.999
    Input Text    id=investigators    999.999
    Input Text    id=estates    999.999
    Input Text    id=other-direct    999.999
    Input Text    id=indirect    999.999
    Input Text    id=exceptions-staff    999.999
    Input Text    id=exceptions-other-direct    999.999
    Input Text    id=tsb-ref    123123

the calculations should be correct and the totals rounded to the second decimal
    Textfield Value Should Be    id=subtotal-directly-allocated    £ 3,000
    Textfield Value Should Be    id=subtotal-exceptions    £ 2,000
    Textfield Value Should Be    id=total    £ 9,000

the academic partner uploads a file
    [Arguments]    ${file_name}
    Choose File    name=jes-upload    ${UPLOAD_FOLDER}/${file_name}
    Sleep    500ms

the finance table should be correct
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(1)    £9,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(2)    £3,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(3)    £1,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(4)    £1,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(6)    £0
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(7)    £1,000
    Element Should Contain    css=.project-cost-breakdown tr:nth-of-type(3) td:nth-of-type(8)    £3,000

Lead applicant marks the finances as complete
    Given guest user log-in    steve.smith@empire.com    Passw0rd
    And the user navigates to the page    ${YOUR_FINANCES_URL}
    When the user clicks the button/link    jQuery=.button:contains("Mark all as complete")
    And close browser
    And Switch to the first browser

Lead applicant marks the finances as incomplete
    Given guest user log-in    steve.smith@empire.com    Passw0rd
    And the user navigates to the page    ${YOUR_FINANCES_URL}
    And the user clicks the button/link    jQuery=button:contains("Edit")
    And Close Browser
    And Switch to the first browser

the user reloads the page
    Reload Page

the user can see the link for more JeS details
    Element Should Be Visible    link=Je-S website
    Page Should Contain Element    xpath=//a[contains(@href,'https://je-s.rcuk.ac.uk')]

the applicant enters invalid inputs
    Input Text    id=incurred-staff    100£
    Input Text    id=travel    -89
    Input Text    id=other    999.999
    Input Text    id=investigators    999.999
    Input Text    id=estates    999.999
    Input Text    id=other-direct    999.999
    Input Text    id=indirect    999.999
    Input Text    id=exceptions-staff    999.999
    Input Text    id=exceptions-other-direct    999.999
    Input Text    id=tsb-ref    123123

the field should not contain the currency symbol
    Textfield Value Should Be    id=incurred-staff    100

Mark academic finances as complete
    Focus     jQuery=.button:contains("Mark all as complete")
    And the user clicks the button/link    jQuery=.button:contains("Mark all as complete")
